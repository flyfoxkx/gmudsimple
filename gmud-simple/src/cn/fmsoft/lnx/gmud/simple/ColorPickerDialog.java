package cn.fmsoft.lnx.gmud.simple;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * 颜色选择
 * 
 * @author nxliao
 * 
 */
class ColorPickerDialog extends Dialog {

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	private OnColorChangedListener mListener;
	private int mInitialColor;

	public ColorPickerDialog(Context context, OnColorChangedListener listener,
			int initialColor) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(int color) {
				mListener.colorChanged(color);
				dismiss();
			}
		};

		setContentView(new ColorPickerView(getContext(), l, mInitialColor));
		setTitle("Pick a Color");
		setCanceledOnTouchOutside(true);
	}

	/** 颜色选择板：色相(Hue) 饱和度(Saturation) 色调(value) */
	private static class ColorPickerView extends View {
		private Paint mPaintHue;

		private Paint mPaintCursor;

		private Paint mPaint;

		private Paint mCenterPaint;

		/* 圆盘： R->(R|B)->B->(G|B)->G->(R|G)->R */
		private static final int[] HUE_COLORS = new int[] { 0xFFFF0000,
				0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00,
				0xFFFF0000 };

		private OnColorChangedListener mListener;

		private static final int DEF_CENTER_X = 100;
		private static final int DEF_CENTER_Y = 100;
		private static final int DEF_CURSOR_RADIUS = 6;
		private static final int DEF_HUE_WIDTH = 24;
		private static final int DEF_HUE_RADIUS_OUT = DEF_CENTER_X;
		private static final int DEF_HUE_RADIUS_IN = DEF_CENTER_X
				- DEF_HUE_WIDTH + 1;
		private static final int DEF_HUE_RADIUS = (DEF_CENTER_X - DEF_HUE_WIDTH / 2);
		private static final int DEF_HUE_CURSOR_RADIUS = DEF_HUE_RADIUS;
		private static final int DEF_SAT_VAL_WIDTH = DEF_HUE_RADIUS_IN * 7 / 10 - 3;
		private static final int DEF_SAT_VAL_HEIGHT = DEF_SAT_VAL_WIDTH;
		private static final int DEF_BT_WIDTH = DEF_CENTER_X * 414 / 1000;

		private int CENTER_X;
		private int CENTER_Y;
		private int HUB_RADIUS;
		private int HUB_RADIUS_IN;
		private int HUB_RADIUS_OUT;
		private int STROKE_WIDTH;
		private int HUB_CURSOR_RADIUS;
		private int CURSOR_RADIUS;
		private int SAT_VAL_WIDTH;
		private int SAT_VAL_HEIGHT;
		private int BT_WIDTH;

		private float mCxHub, mCyHub, mCxSatVal, mCySatVal;

		private int mInitialColor;

		/**
		 * hsv[0] is Hue [0 .. 360) hsv[1] is Saturation [0...1] hsv[2] is Value
		 * [0...1]
		 */
		final float[] currentColorHsv = new float[3];

		private int getColor() {
			return Color.HSVToColor(currentColorHsv);
		}

		/** 0.0f-360.0f */
		private void setHue(float hue) {
			currentColorHsv[0] = hue;

			double angle = (hue + 90) * 2 * Math.PI / 360;
			mCxHub = (float) (HUB_CURSOR_RADIUS * Math.sin(angle));
			mCyHub = (float) (HUB_CURSOR_RADIUS * Math.cos(angle));
		}

		private void setSat(float sat) {
			currentColorHsv[1] = sat;
		}

		private void setVal(float val) {
			currentColorHsv[2] = val;
		}

		private void resetColor(int color) {
			mInitialColor = color;
			Color.colorToHSV(color, currentColorHsv);
			setHue(currentColorHsv[0]);

			mCxSatVal = currentColorHsv[1] * (SAT_VAL_WIDTH + SAT_VAL_WIDTH)
					- SAT_VAL_WIDTH;
			if (mCxSatVal > SAT_VAL_WIDTH)
				mCxSatVal = SAT_VAL_WIDTH;
			if (mCxSatVal < -SAT_VAL_WIDTH)
				mCxSatVal = -SAT_VAL_WIDTH;
			mCySatVal = SAT_VAL_HEIGHT - currentColorHsv[2]
					* (SAT_VAL_HEIGHT + SAT_VAL_HEIGHT);
			if (mCySatVal > SAT_VAL_HEIGHT)
				mCySatVal = SAT_VAL_HEIGHT;
			if (mCySatVal < -SAT_VAL_HEIGHT)
				mCySatVal = -SAT_VAL_HEIGHT;
		}

		ColorPickerView(Context c, OnColorChangedListener l, int color) {
			super(c);

			float density = c.getResources().getDisplayMetrics().density;
			CENTER_X = (int) (DEF_CENTER_X * density);
			CENTER_Y = (int) (DEF_CENTER_Y * density);
			HUB_RADIUS = (int) (DEF_HUE_RADIUS * density);
			HUB_RADIUS_IN = (int) (DEF_HUE_RADIUS_IN * density);
			HUB_RADIUS_OUT = (int) (DEF_HUE_RADIUS_OUT * density);
			STROKE_WIDTH = (int) (DEF_HUE_WIDTH * density);
			CURSOR_RADIUS = (int) (DEF_CURSOR_RADIUS * density);
			HUB_CURSOR_RADIUS = (int) (DEF_HUE_CURSOR_RADIUS * density);
			SAT_VAL_WIDTH = (int) (DEF_SAT_VAL_WIDTH * density);
			SAT_VAL_HEIGHT = (int) (DEF_SAT_VAL_HEIGHT * density);
			BT_WIDTH = (int) (DEF_BT_WIDTH * density);

			mRectFOld = new RectF(-CENTER_X, -CENTER_Y, -CENTER_X + BT_WIDTH,
					-CENTER_Y + BT_WIDTH * 2);
			mRectFNew = new RectF(CENTER_X - BT_WIDTH, -CENTER_Y, CENTER_X,
					-CENTER_Y + BT_WIDTH * 2);

			mListener = l;

			resetColor(color);

			Shader s = new SweepGradient(0, 0, HUE_COLORS, null);
			mPaintHue = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintHue.setShader(s);
			mPaintHue.setStyle(Paint.Style.STROKE);
			mPaintHue.setStrokeWidth(STROKE_WIDTH);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setStrokeWidth(5);

			mPaintCursor = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintCursor.setStyle(Paint.Style.STROKE);
			mPaintCursor.setStrokeWidth(density);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		private RectF mRectFNew, mRectFOld;

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.translate(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);

			Paint p;
			p = mPaint;
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setColor(mInitialColor);
			canvas.drawOval(mRectFOld, p);

			// 绘制当前颜色
			p = mPaint;
			p.setColor(getColor());
			canvas.drawOval(mRectFNew, p);
			canvas.drawCircle(0, 0, HUB_RADIUS_IN, p);

			/* 圆盘 */
			canvas.drawCircle(0, 0, HUB_RADIUS, mPaintHue);

			// 色盘
			p = mPaint;
			p.setStyle(Paint.Style.FILL);
			LinearGradient luar = new LinearGradient(0.f, -SAT_VAL_HEIGHT, 0.f,
					SAT_VAL_HEIGHT, 0xffffffff, 0xff000000, TileMode.CLAMP);
			float color[] = { currentColorHsv[0], 1f, 1f };
			int rgb = Color.HSVToColor(color);
			Shader dalam = new LinearGradient(-SAT_VAL_WIDTH, 0.f,
					SAT_VAL_WIDTH, 0.f, 0xffffffff, rgb, TileMode.CLAMP);
			ComposeShader shader = new ComposeShader(luar, dalam,
					PorterDuff.Mode.MULTIPLY);
			p.setShader(shader);
			canvas.drawRect(-SAT_VAL_WIDTH, -SAT_VAL_HEIGHT, SAT_VAL_WIDTH,
					SAT_VAL_HEIGHT, p);
			p.setShader(null);

			// 指示器
			p = mPaintCursor;
			p.setColor(mCyHub > 0 ? Color.WHITE : Color.BLACK);
			canvas.drawCircle(mCxHub, mCyHub, CURSOR_RADIUS, p);
			p.setColor(mCySatVal > 0 ? Color.WHITE : Color.BLACK);
			canvas.drawCircle(mCxSatVal, mCySatVal, CURSOR_RADIUS, p);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X * 2 + 16, CENTER_Y * 2 + 16);
		}

		private static final float PI = 3.1415926f;

		private final int ID_HUB = 0;
		private final int ID_SAT_VAL = 1;
		private final int ID_OK = 2;
		private final int ID_CANCEL = 3;
		private int mHitId = -1;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				if (x >= -SAT_VAL_WIDTH && x <= SAT_VAL_WIDTH
						&& y >= -SAT_VAL_HEIGHT && y <= SAT_VAL_HEIGHT) {
					mHitId = ID_SAT_VAL;
				} else {
					float r = x * x + y * y;
					if (r > HUB_RADIUS_IN * HUB_RADIUS_IN
							&& r < HUB_RADIUS_OUT * HUB_RADIUS_OUT) {
						mHitId = ID_HUB;
					} else {
						mHitId = -1;
					}
				}
				if (mHitId != -1) {
					invalidate();
				}
			}
				break;

			case MotionEvent.ACTION_MOVE: {
				if (mHitId == ID_SAT_VAL) {
					if (x < -SAT_VAL_WIDTH)
						x = -SAT_VAL_WIDTH;
					if (x > SAT_VAL_WIDTH)
						x = SAT_VAL_WIDTH;
					if (y < -SAT_VAL_HEIGHT)
						y = -SAT_VAL_HEIGHT;
					if (y > SAT_VAL_HEIGHT)
						y = SAT_VAL_HEIGHT;
					mCxSatVal = x;
					mCySatVal = y;
					setSat((x + SAT_VAL_WIDTH)
							/ (SAT_VAL_WIDTH + SAT_VAL_WIDTH));
					setVal(1.f - ((y + SAT_VAL_HEIGHT) / (SAT_VAL_HEIGHT + SAT_VAL_HEIGHT)));
					invalidate();
					break;
				} else if (mHitId == ID_HUB) {
					// -pi,pi
					float angle = (float) java.lang.Math.atan2(y, -x);
					setHue((angle + PI) * 360 / (2 * PI));
					invalidate();
					break;
				}
			}
				break;

			case MotionEvent.ACTION_UP:
				if (mHitId == -1) {
					if (mRectFNew.contains(x, y)) {
						mListener.colorChanged(getColor());
						invalidate();
					} else if (mRectFOld.contains(x, y)) {
						resetColor(mInitialColor);
						invalidate();
					}
				}
				break;
			}
			return true;
		}
	}

}
