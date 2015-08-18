/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * Video, is used for clear, draw and fill (line, rectangle, arc, image, text).
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple.core;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * 输出绘制控制
 * 
 * @author nxliao
 * 
 */
class Video {

	/** 默认背景色 */
	static final int COLOR_BG = 0xff90b057;
	static final int COLOR_FG = Color.BLACK;

	static final int LARGE_FONT_SIZE = 16;
	static final int SMALL_FONT_SIZE = 12;
	static final int MINI_FONT_SIZE = 8;

	/** 小字号下的行高 */
	static final int SMALL_LINE_H = SMALL_FONT_SIZE + 1;

	static boolean VideoExited = true;

	private static final Object LOCK = new Object();

	private static int largeFnt = LARGE_FONT_SIZE;
	private static int smallFnt = SMALL_FONT_SIZE;
	private static int miniFnt = MINI_FONT_SIZE;

	private static Bitmap lpmemimg;
	private static Canvas lpmem;

	private static Paint fgBrush, bgBrush;
	private static Paint fgPen;

	private static Bitmap pnum;

	private static Paint sPaint;

	private static Gmud.IVideoCallback sCallback;

	/** 输出区域，不一定与[160x80]成比例 */
	private static Rect sDirtyRect;
	private static int sWidth, sHeight;

	private static boolean sConfig_ImageSmooth = false;

	public static void SetCallback(Gmud.IVideoCallback callback) {
		sCallback = callback;
		VideoUpdate();
	}

	/** 重置输出区域、纵横缩放比例、字体、framebuff */
	public static void ResetLayout(Rect rect) {
		if (sDirtyRect.equals(rect)) {
			return;
		}

		Bitmap tmp_lpmemiｍg;
		synchronized (LOCK) {
			tmp_lpmemiｍg = lpmemimg;
			lpmemimg = null;

			sDirtyRect.set(rect);

			final int w = rect.width();
			final int h = rect.height();
			sWidth = w;
			sHeight = h;

			Bitmap bmBack = null;
			if (tmp_lpmemiｍg != null && !tmp_lpmemiｍg.isRecycled()) {
				bmBack = tmp_lpmemiｍg;
				tmp_lpmemiｍg = Bitmap.createScaledBitmap(bmBack, sWidth,
						sHeight, true);
			} else {
				tmp_lpmemiｍg = Bitmap.createBitmap(sWidth, sHeight,
						Bitmap.Config.ARGB_8888);
				tmp_lpmemiｍg.eraseColor(COLOR_BG);
			}
			lpmemimg = tmp_lpmemiｍg;
			lpmem.setBitmap(lpmemimg);

			Matrix m = new Matrix();
			m.setScale((float) w / Gmud.WQX_ORG_WIDTH, (float) h
					/ Gmud.WQX_ORG_HEIGHT);
			lpmem.setMatrix(m);
			if (bmBack != null) {
				bmBack.recycle();
				bmBack = null;
			}

			VideoUpdate();
		}
	}

	static void setImageSmooth(boolean smooth) {
		if (sConfig_ImageSmooth != smooth) {
			sConfig_ImageSmooth = smooth;
			update_config_image_smooth();
		}
	}

	private static void update_config_image_smooth() {
		if (sPaint != null) {
			boolean smooth = sConfig_ImageSmooth;
			sPaint.setAntiAlias(smooth);
			sPaint.setFilterBitmap(smooth);
		}
	}

	static boolean VideoInit() {
		sDirtyRect = new Rect();

		sPaint = new Paint();
		update_config_image_smooth();

		lpmem = new Canvas();

		pnum = Res.loadimage(248);

		fgBrush = new Paint();
		fgBrush.setAntiAlias(true);
		fgBrush.setColor(COLOR_FG);
		fgBrush.setStyle(Style.FILL);
		bgBrush = new Paint();
		bgBrush.setAntiAlias(true);
		bgBrush.setColor(COLOR_BG);
		bgBrush.setStyle(Style.FILL);
		fgPen = new Paint();
		fgPen.setAntiAlias(true);
		fgPen.setColor(COLOR_FG);
		fgPen.setStyle(Style.STROKE);
		fgPen.setStrokeWidth(1.0f);

		// 初始为一倍大小的游戏区
		ResetLayout(new Rect(0, 0, Gmud.WQX_ORG_WIDTH, Gmud.WQX_ORG_HEIGHT));

		VideoClear();
		VideoUpdate();

		return true;
	}

	static void VideoShutdown() {
		// DeleteObject(pnum);
		// DeleteObject(largeFnt);
		// DeleteObject(smallFnt);
		// DeleteObject(blackBrush);
		// DeleteObject(greenBrush);
		// DeleteObject(blackPen);
		//
		// DeleteObject(lpmemimg);
		// DeleteObject(lpmem);
		// DeleteObject(lpwnd);
		//
		// GdiplusShutdown(m_pGdiToken);
		// ReleaseDC(hw,m_hdc);
		// VideoExited = 1;
		synchronized (LOCK) {
			lpmemimg.recycle();
			lpmemimg = null;
			lpmem = null;
		}
	}

	static void exit(int code) {
		throw new RuntimeException("Video exit(" + code + ").");
	}

	private static FontMetrics sFontMetrics = new FontMetrics();

	private static void _setTextSize(Paint paint, boolean isLarge) {
		_setTextSize(paint, isLarge ? largeFnt : smallFnt);
	}

	private static void _setTextSize(Paint paint, int fontSize) {
		paint.setTextSize(fontSize);
		paint.getFontMetrics(sFontMetrics);
	}

	/**
	 * 绘制文本，绘制前需要调用 {@link #_setTextSize(Paint, boolean)} 设置字号
	 * 
	 * @param text
	 *            文本
	 * @param x
	 *            横坐标[0,160)
	 * @param y
	 *            纵坐标[0,80)
	 * @param paint
	 *            画笔
	 */
	private static void _drawText(String text, int x, int y, Paint paint) {
		lpmem.drawText(text, x, y - sFontMetrics.ascent, paint);
	}

	private static void _drawMultiText(String str, int x, int y,
			int restrictWidth, boolean isLarge, Paint paint) {
		_setTextSize(paint, isLarge);
		int line = 0;
		int linestart = 0;
		int lineH = isLarge ? largeFnt : smallFnt;
		int k = str.length();
		for (int i = 1; i < k; i++) {
			float w = paint.measureText(str, linestart, i);
			if (w + x >= restrictWidth) {
				_drawText(str.substring(linestart, i - 1), x, y, paint);
				y += lineH;
				linestart = i - 1;
				line++;
			}
		}
		if (linestart < k) {
			_drawText(str.substring(linestart, k), x, y, paint);
			return;
		}
	}

	private static void _drawRect(int x, int y, int w, int h, Paint paint) {
		lpmem.drawRect(x, y, (x + w), (y + h), paint);
	}

	private static void _drawPath(Path path, Paint paint) {
		lpmem.drawPath(path, paint);
	}

	private static void _drawCircle(int x, int y, int r, Paint paint) {
		lpmem.drawCircle(x, y, r, paint);
	}

	private static void _drawLine(int x1, int y1, int x2, int y2, Paint paint) {
		lpmem.drawLine(x1, y1, x2, y2, paint);
	}

	private static void _drawBitmap(Bitmap bitmap, int left, int top,
			Paint paint) {
		lpmem.drawBitmap(bitmap, left, top, paint);
	}

	private static void _drawBitmap(Bitmap bitmap, Rect src, Rect dst,
			Paint paint) {
		lpmem.drawBitmap(bitmap, src, dst, paint);
	}

	static void VideoDrawLine(int x1, int y1, int x2, int y2) {
		_drawLine(x1, y1, x2, y2, fgPen);
	}

	/**
	 * 绘制一个箭头
	 * 
	 * @param x
	 * @param y
	 * @param w
	 *            宽度
	 * @param h
	 *            高度，如果小于0，表示向上
	 * @param type
	 *            类型，Bit0:左右方向 Bit1:实心 Bit2:用背景色(只用于实心)
	 */
	static void VideoDrawArrow(int x, int y, int w, int h, int type) {
		final Path path = new Path();
		path.moveTo(x, y);
		if ((type & 1) == 0) {
			path.lineTo(x + w, y);
			path.lineTo(x + (float) w / 2, y + h);
		} else {
			path.lineTo(x, y + h);
			path.lineTo(x + w, y + (float) h / 2);
		}
		path.close();

		if ((type & 2) == 0) {
			// clear
			_drawPath(path, bgBrush);
			_drawPath(path, fgPen);
		} else if ((type & 4) == 0) {
			_drawPath(path, fgBrush);
			_drawPath(path, fgPen);
		} else {
			_drawPath(path, bgBrush);
		}
	}

	static void VideoClear() {
		synchronized (LOCK) {
			lpmemimg.eraseColor(COLOR_BG);
		}
	}

	static void VideoClearRect(int x, int y, int width, int height) {
		_drawRect(x, y, width, height, bgBrush);
	}

	static void VideoDrawRectangle(int x, int y, int width, int height) {
		_drawRect(x, y, width, height, fgPen);
	}

	static void VideoFillRectangle(int x, int y, int width, int height) {
		VideoFillRectangle(x, y, width, height, 0);
	}

	static void VideoFillRectangle(int x, int y, int width, int height, int type) {
		_drawRect(x, y, width, height, (type != 0) ? bgBrush : fgBrush);
	}

	static void VideoDrawArc(int x, int y, int r) {
		_drawCircle(x, y, r, fgPen);
	}

	static void VideoFillArc(int x, int y, int r) {
		_drawCircle(x, y, r, fgBrush);
	}

	static void VideoDrawImage(/* Image* */Bitmap pI, int x, int y) {
		// private static Matrix sMatrix = new Matrix();
		// sMatrix.setScale(sScale, sScale);
		// sMatrix.postTranslate(x , y );
		// lpmem.drawBitmap(pI, sMatrix, sPaint);
		_drawBitmap(pI, x, y, sPaint);
	}

	static void VideoDrawString(String str, int x, int y) {
		VideoDrawString(str, x, y, 0);
	}

	static void VideoDrawString(String str, int x, int y, int type) {
		// 大字体y坐标:每行+16 //小字体y坐标:每行+13
		if (type != 0) {
			_setTextSize(fgBrush, true);
			_drawText(str, x, y, fgBrush);
		} else {
			_drawMultiText(str, x, y, Gmud.WQX_ORG_WIDTH, false, fgBrush);
		}
	}

	// static void VideoDrawString(const wchar_t*, int, int, int type = 0);
	/** 绘制单行文本，小号字 */
	static void VideoDrawStringSingleLine(final String str, int x, int y) {
		VideoDrawStringSingleLine(str, x, y, 0);
	}

	/**
	 * 绘制单行文本
	 * 
	 * @param str
	 * @param x
	 * @param y
	 * @param type
	 *            1:大号前景色 2:小号背景色 3:极小前景色 0:小号前景色
	 */
	static void VideoDrawStringSingleLine(final String str, int x, int y,
			int type) {
		// PointF origin(x, y);
		// 大字体y坐标:每行+16
		// 小字体y坐标:每行+13
		switch (type) {
		case 1:
			// lpmem->DrawString(str, -1, largeFnt, PointF(x, y), blackBrush);
			_setTextSize(fgBrush, true);
			_drawText(str, x, y, fgBrush);
			break;
		case 2:
			_setTextSize(bgBrush, false);
			_drawText(str, x, y, bgBrush);
			break;
		case 3:
			_setTextSize(fgBrush, miniFnt);
			_drawText(str, x, y, fgBrush);
			break;
		default:
			_setTextSize(fgBrush, false);
			_drawText(str, x, y, fgBrush);
		}
	}

	static void VideoDrawNumberData(String data, int x, int y) {
		final int W = 4;
		final int H = 5;
		Rect rectSrc = new Rect(0, 0, W, H);
		Rect rectDst = new Rect(x, y, (x + W), (y + H));
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			int num = ('0' <= c && c <= '9') ? (c - '0') : 10;
			rectSrc.left = W * num;
			rectSrc.right = W * num + W;
			_drawBitmap(pnum, rectSrc, rectDst, sPaint);
			rectDst.offset(W, 0);
		}
	}

	public static void VideoUpdate() {
		if (sCallback != null) {
			sCallback.VideoPostUpdate(lpmemimg);
		}
	}

	static ArrayList<String> SplitString(String str, int width) {
		final ArrayList<String> sv = new ArrayList<String>();

		int linestart = 0;
		int len = str.length();

		final Paint p = fgBrush;
		_setTextSize(p, false);
		for (int i = 0; i < len; i++) {
			if (str.charAt(i) == '\n') {
				if (i != 0) {
					sv.add(str.substring(linestart, i));
					linestart = i + 1;
				}
				continue;
			}

			float w = p.measureText(str, linestart, i);
			if (w >= width) {
				sv.add(str.substring(linestart, i - 1));
				linestart = i - 1;
			}
		}
		// if (linestart < k) {
		sv.add(str.substring(linestart, len));
		// }
		return sv;
	}
}
