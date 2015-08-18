package cn.fmsoft.lnx.gmud.simple;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import cn.fmsoft.lnx.gmud.simple.ColorPickerDialog.OnColorChangedListener;
import cn.fmsoft.lnx.gmud.simple.CustomSoftKeySizeDialog.OnCustomSoftKeyListener;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;
import cn.fmsoft.lnx.gmud.simple.core.Input;

/**
 * 管理各部件位置区域及显示，响应按键点击，<br/>
 * <b>NOTE: 仅提供给 {@link Show} 和 {@link ConfigInfo} 使用</b>
 * 
 * @author nxliao
 * 
 */
final class Configure {
	final static boolean DEBUG = false;
	final static String DBG_TAG = Configure.class.getName();

	private final static Object LOCK = new Object();

	public static final int KEY_Exit = 0;
	public static final int KEY_Ent = 1;
	public static final int KEY_Down = 2;
	public static final int KEY_Left = 3;
	public static final int KEY_Up = 4;
	public static final int KEY_Right = 5;
	public static final int KEY_PgUp = 6;
	public static final int KEY_PgDn = 7;
	public static final int KEY_Fly = 8;
	public static final int KEY_HELP = 9;
	public static final int _KEY_MAX_ = KEY_Fly + 1;
	/** 游戏区 */
	public static final int _KEY_INDEX_VIDEO = _KEY_MAX_ + 0;
	/** 背板 */
	public static final int _KEY_INDEX_BG = _KEY_MAX_ + 1;
	/** 标题文本 */
	public static final int _KEY_INDEX_TITLE = _KEY_MAX_ + 2;
	public static final int _KEY_INDEX_MAX = _KEY_INDEX_TITLE + 1;

	public static final int _KEY_MASK_CLEAR_ = -1 << _KEY_MAX_;
	public static final int _KEY_MASK_ = ~_KEY_MASK_CLEAR_;

	/** 临时对象 */
	private static final Rect TMP_RECT = new Rect();
	/** 剪切区，每次绘制前重新获取 */
	private static final Rect sTmpRcClip = new Rect();

	/** 视频输出区域 */
	protected static final Rect sRcVideo = new Rect();
	/** 各按键区，用于点击判断及绘制虚拟按键 */
	protected static final Rect sRcKeys[] = new Rect[_KEY_INDEX_MAX];
	/** 虚拟按键的背板显示区域，相对于 {@link #sRcKeys} */
	protected static final Rect sRcKeyBg = new Rect();
	/** 虚拟按键的外包框 */
	protected static final Rect sRcKeyBorder = new Rect();
	/** 虚拟按键的标题显示区域，相对于 {@link #sRcKeys}，同 {@link #sRcKeyBg} */
	protected static final Rect sRcKeyTitle = new Rect();
	/** 按下状态 */
	private static int sPressMask = 0;
	/** 按下或弹起的变化 */
	private static int sPressDirty = 0;

	/** 虚拟按键的背板图（共用） */
	private static Drawable IMG_BG;
	/** 虚拟按键的前景图 */
	private static final Drawable IMG_TITLE[] = new Drawable[_KEY_MAX_];
	/** 游戏区的盖子，模拟NC1020的LOGO */
	private static Drawable VIDEO_COVER;
	/** 游戏区的盖子的边框大小 */
	private static final Rect sRcVideoCoverPadding = new Rect();
	/** 虚拟按键的默认标题画笔 */
	private static Paint TITLE_PAINT;
	private static Paint VIDEO_PAINT;
	/** 虚拟按键的默认标题文本 */
	private static String TITLE[];
	public static float CUR_DENSITY = 1.0f;

	private static boolean sShowKeypad = true;

	private static ConfigInfo sDefConfigLand, sDefConfigPort;
	private static ConfigInfo sCurConfigInfo;
	private static int sWidth, sHeight;
	private static final Rect sBound = new Rect();
	/** 背景色 */
	private static int sBackground;
	private static boolean sIsLandscape;

	private static Context sContext;

	private static boolean checkIntialization() {
		synchronized (LOCK) {
			return (sContext != null);
		}
	}

	public static void recycle() {
		synchronized (LOCK) {
			if (!checkIntialization())
				return;

			sContext = null;
			IMG_BG = VIDEO_COVER = null;
			TITLE = null;
			VIDEO_PAINT = TITLE_PAINT = null;
			for (int i = 0, c = _KEY_INDEX_MAX; i < c; i++) {
				sRcKeys[i] = null;
			}
			for (int i = 0, c = _KEY_MAX_; i < c; i++) {
				IMG_TITLE[i] = null;
			}
		}
	}

	/** 初始化 */
	public static void init(Context ctx) {
		synchronized (LOCK) {
			if (checkIntialization())
				return;
			
			sContext = ctx;

			Resources res = ctx.getResources();
			final DisplayMetrics dm = res.getDisplayMetrics();
			CUR_DENSITY = dm.density;

			IMG_BG = new DEF_DRAWABLE_BG(res);
			VIDEO_COVER = res.getDrawable(R.drawable.nc1020);
			TITLE = res.getStringArray(R.array.soft_key);
			if (VIDEO_COVER != null)
				VIDEO_COVER.getPadding(sRcVideoCoverPadding);

			VIDEO_PAINT = new Paint();
			VIDEO_PAINT.setAntiAlias(true);
			VIDEO_PAINT.setFilterBitmap(true);

			TITLE_PAINT = new Paint();
			TITLE_PAINT.setAntiAlias(true);
			TITLE_PAINT.setFilterBitmap(true);
			TITLE_PAINT.setFakeBoldText(true);
			TITLE_PAINT.setTextAlign(Paint.Align.LEFT);
			TITLE_PAINT.setShadowLayer(3, 1, 2, Color.BLACK);

			for (int i = 0, c = _KEY_MAX_; i < c; i++) {
				sRcKeys[i] = new Rect();
				IMG_TITLE[i] = new DEF_DRAWABLE_TITLE(TITLE[i]);
			}
			sRcKeys[_KEY_INDEX_VIDEO] = sRcVideo;
			sRcKeys[_KEY_INDEX_BG] = sRcKeyBg;
			sRcKeys[_KEY_INDEX_TITLE] = sRcKeyTitle;
		}
	}

	/** 绘制单个软键 */
	private static void _draw_key(Canvas canvas, int id, Rect rc) {
		final int flag = (1 << id);
		final int ox = rc.left, oy = rc.top;
		final int level = ((sPressMask & flag) == 0 ? 0 : 1);// no press

		Drawable d = IMG_BG;
		Rect r = sRcKeyBg;
		d.setBounds(r.left + ox, r.top + oy, r.right + ox, r.bottom + oy);
		d.setLevel(level);
		d.draw(canvas);

		d = IMG_TITLE[id];
		r = sRcKeyTitle;
		d.setBounds(r.left + ox, r.top + oy, r.right + ox, r.bottom + oy);
		d.setLevel(level);
		d.draw(canvas);

		if ((sPressDirty & flag) != 0)
			sPressDirty &= ~(flag);
	}

	private static String getConfigName(boolean isLandscape) {
		return isLandscape ? "land" : "port";
	}

	/**
	 * 加载自定义配置
	 * 
	 * @param isLandscape
	 *            是否横屏
	 * @param defInfo
	 *            默认配置
	 * @return
	 */
	private static ConfigInfo tryLoadConfig(boolean isLandscape,
			ConfigInfo defInfo) {
		SharedPreferences sp = sContext.getSharedPreferences("design",
				Context.MODE_PRIVATE);
		String design = sp.getString(getConfigName(isLandscape), null);
		if (design != null) {
			try {
				JSONObject joRoot = new JSONObject(design);
				return ConfigInfo.unflattenFromJSON(joRoot, defInfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ConfigInfo(defInfo);
	}

	/**
	 * 储存自定义配置
	 * 
	 * @param isLandscape
	 * @param info
	 * @return
	 */
	private static boolean trySaveConfig(boolean isLandscape, ConfigInfo info) {
		SharedPreferences sp = sContext.getSharedPreferences("design",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		JSONObject joRoot = new JSONObject();
		try {
			ConfigInfo.flattenToJSON(info, joRoot);
			editor.putString(getConfigName(isLandscape), joRoot.toString());
			return editor.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 应用配置
	 * 
	 * @param info
	 */
	private static void applyConfig(ConfigInfo info) {
		sCurConfigInfo = new ConfigInfo(info);
		for (int i = 0, c = _KEY_INDEX_MAX; i < c; i++) {
			sRcKeys[i].set(info.mRcKeys[i]);
		}
		sRcKeyBorder.set(0, 0, sRcKeys[0].width(), sRcKeys[0].height());
		sRcKeyBorder.union(sRcKeyBg);
		sRcKeyBorder.union(sRcKeyTitle);
		sBackground = info.mBackgroundColor;

		// 重置缩放比例
		Gmud.ResetVideoLayout(sRcVideo);
	}

	/** 将当前使用的配置，转存到 ConfigInfo 结构中 */
	private static void dumpConfig(ConfigInfo info) {
		for (int i = 0, c = _KEY_INDEX_MAX; i < c; i++) {
			info.mRcKeys[i].set(sRcKeys[i]);
		}
		info.mBackgroundColor = sBackground;
	}

	/**
	 * 构造默认配置
	 * 
	 * @param isLandscape
	 * @param w
	 * @param h
	 * @return
	 */
	private static ConfigInfo generateDefault(boolean isLandscape, int w, int h) {
		ConfigInfo defInfo = isLandscape ? sDefConfigLand : sDefConfigPort;
		if (defInfo == null || !defInfo.match(w, h)) {
			if (isLandscape) {
				defInfo = ConfigInfo.generateDefaultLand(w, h);
				sDefConfigLand = defInfo;
			} else {
				defInfo = ConfigInfo.generateDefaultPort(w, h);
				sDefConfigPort = defInfo;
			}
		}
		return defInfo;
	}

	/**
	 * 根据总区域大小重置各区域
	 * 
	 * @param isLandscape
	 *            是否横屏
	 * @param w
	 *            宽
	 * @param h
	 *            高
	 */
	public static void reset(boolean isLandscape, int w, int h) {
		synchronized (LOCK) {
			if (!checkIntialization())
				return;

			ConfigInfo defInfo = generateDefault(isLandscape, w, h);

			// try load ...
			final ConfigInfo info = tryLoadConfig(isLandscape, defInfo);

			applyConfig(info == null ? defInfo : info);
			sIsLandscape = isLandscape;
			sWidth = w;
			sHeight = h;
			sBound.set(0, 0, w, h);
		}
	}

	/** 检查有无必要重绘 */
	protected static boolean updateInvalidate() {
		return false;
	}

	private static Rect _unionKeyBound(int id) {
		final Rect bound = TMP_RECT;
		bound.setEmpty();
		return _unionKeyBound(bound, id);
	}

	private static Rect _unionKeyBound(Rect bound, int id) {
		Rect r = sRcKeys[id];
		final int ox = r.left, oy = r.top;
		r = sRcKeyBorder;
		bound.union(r.left + ox, r.top + oy, r.right + ox, r.bottom + oy);
		return bound;
	}

	// private static Rect _gainKeyBound(Rect base) {
	// Rect bound = TMP_RECT;
	// bound.setEmpty();
	// return _gainKeyBound(bound, base);
	// }

	private static Rect _gainKeyBound(Rect bound, Rect base) {
		final int ox = base.left, oy = base.top;
		final Rect r = sRcKeyBorder;
		bound.union(r.left + ox, r.top + oy, r.right + ox, r.bottom + oy);
		return bound;
	}

	/** 合并（普通）“脏”区域 */
	protected static void UnionInvalidateRect(Rect bound) {
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			if ((sPressDirty & (1 << i)) != 0) {
				_unionKeyBound(bound, i);
			}
		}
	}

	public static void Draw(Canvas canvas, Bitmap video, int tick) {
		synchronized (LOCK) {
			if (!checkIntialization())
				return;
			drawBackground(canvas);
			if (sShowKeypad)
				drawKeypad(canvas);
			drawVideo(canvas, video);
		}
	}

	protected static void hidekeypad(boolean hide) {
		sShowKeypad = !hide;
	}

	/** 绘制软键 */
	private static void drawKeypad(Canvas canvas) {
		/* 剪切区 */
		final Rect clip = sTmpRcClip;
		canvas.getClipBounds(clip);
		final Rect rcKeys[] = sRcKeys;
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			final Rect rc = _unionKeyBound(i);
			if (Rect.intersects(clip, rc))
				_draw_key(canvas, i, rcKeys[i]);
		}
	}

	private static Rect _addVideoCoverPadding(Rect rc) {
		Rect bound = new Rect(rc);
		bound.left -= sRcVideoCoverPadding.left;
		bound.top -= sRcVideoCoverPadding.top;
		bound.right += sRcVideoCoverPadding.right;
		bound.bottom += sRcVideoCoverPadding.bottom;
		return bound;
	}

	/** 绘制游戏区 */
	private static void drawVideo(Canvas canvas, Bitmap video) {
		if (VIDEO_COVER != null) {
			VIDEO_COVER.setBounds(_addVideoCoverPadding(sRcVideo));
			VIDEO_COVER.draw(canvas);
		}
		if (video != null && !video.isRecycled())
			canvas.drawBitmap(video, null, sRcVideo, VIDEO_PAINT);
	}

	private static void drawBackground(Canvas canvas) {
		canvas.drawColor(sBackground);
	}

	/** 响应单击按钮，如无变化返回false，否则返回true */
	protected static boolean HitTest(MotionEvent event) {
		final int action = event.getAction();
		final int count = event.getPointerCount();

		if (DEBUG)
			Log.d(DBG_TAG, "action=" + action);

		final int flag;
		switch ((action & MotionEvent.ACTION_MASK)) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			flag = 0;
		}
			break;

		default: {
			int tmp = 0;
			for (int i = 0, c = count; i < c; i++) {
				final int x = (int) event.getX(i);
				final int y = (int) event.getY(i);
				tmp |= HitTestFlag(x, y);
			}
			flag = tmp;
		}
			break;
		}

		if (flag == sPressMask)
			return false;
		sPressDirty = (sPressMask ^ flag);
		sPressMask = (sPressMask & _KEY_MASK_CLEAR_) | flag;
		Input.GmudSetKey(sPressMask);
		return true;
	}

	/** 返回位置所在的所有元件掩码 */
	protected static int HitTestFlag(int x, int y) {
		int flag = 0;
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			if (sRcKeys[i].contains(x, y)) {
				flag |= 1 << i;
			}
		}
		return flag;
	}

	/** 返回位置所在的最顶层元素ID */
	protected static int HitTestId(int x, int y) {
		for (int i = _KEY_MAX_; i >= 0; i--) {
			if (sRcKeys[i].contains(x, y))
				return i;
		}
		return -1;
	}

	protected static void onKeyDown(int flag) {
		sPressMask |= flag;
		Input.GmudSetKey(sPressMask);
	}

	protected static void onKeyUp(int flag) {
		if (flag == 0)
			sPressMask = 0;
		else
			sPressMask &= ~(flag);
		Input.GmudSetKey(sPressMask);
	}

	protected static void onKeySet(int flag) {
		sPressMask = flag;
		Input.GmudSetKey(sPressMask);
	}

	protected static void startDesign(Design design) {
		final ConfigInfo info = sCurConfigInfo;
		design.SetConfigInfo(info);
	}

	protected static void applyDesign(Design design) {
		final ConfigInfo info = sCurConfigInfo;
		dumpConfig(info);
		if (DEBUG) {
			Log.d(DBG_TAG, " apply design, save configure");
		}
		trySaveConfig(sIsLandscape, info);
		applyConfig(info);
	}

	protected static void cancelDesign(Design design) {
		final ConfigInfo info = design.GetConfigInfo();
		applyConfig(info);
	}

	protected static void resetDesign() {
		final ConfigInfo info = generateDefault(sIsLandscape, sWidth, sHeight);
		applyConfig(info);
	}

	/**
	 * 自定义界面，直接修改了 Configure 的变量
	 * 
	 * @author nxliao
	 * @version 0.1.20130219.1454
	 */
	public static class Design extends GestureDetector.SimpleOnGestureListener
			implements OnColorChangedListener, OnCustomSoftKeyListener {

		public static final int DESIGN_START = 0;
		public static final int DESIGN_APPLY = 1;
		public static final int DESIGN_CANCEL = 2;
		public static final int DESIGN_RESET = 3;

		private float mCurX;
		private float mCurY;
		private Show mShow;

		/** 当前元素ID */
		private int mHitId = -1;

		private boolean mRedraw = false;
		private boolean mDirty = false;

		/** 目标位置（即 ACTION_UP 后元素应放下的位置） */
		private Rect mRcTarget, mRcTmp = new Rect();

		ConfigInfo mConfigInfo;

		/** 吸附状态 */
		private int mAdsorb = 0, mAdsorbBak = 0;
		private float mAX, mAY;
		private static final int DEF_ADSORB_H = 24;
		private static final int DEF_ADSORB_V = 24;
		private final int FLAG_ADSORB_LEFT = 1 << 0;
		private final int FLAG_ADSORB_TOP = 1 << 1;
		private final int FLAG_ADSORB_RIGHT = 1 << 2;
		private final int FLAG_ADSORB_BOTTOM = 1 << 3;
		private Dialog mDialog;

		public Design(Show show) {
			mShow = show;
		}

		public void SetConfigInfo(ConfigInfo info) {
			mConfigInfo = info;
		}

		public ConfigInfo GetConfigInfo() {
			return mConfigInfo;
		}

		protected boolean updateInvalidate() {
			return false;
		}

		/**
		 * 检查水平方向上的吸附，会修改 {@link #mCurX}、 {@link #mAX}、 {@link #mAdsorb} 、
		 * {@link #mAdsorbBak}
		 * 
		 * @param x
		 * @param flag
		 * @return 是否做了吸附处理
		 */
		private boolean checkAdsorbH(int x, int flag) {
			final int t = (int) (x + mCurX + mAX);
			boolean adsorb = (t > -DEF_ADSORB_H && t < DEF_ADSORB_H);
			if ((mAdsorb & flag) != 0) {
				if (adsorb) {
					mAX += mCurX;
					mCurX = 0;
				} else {
					mCurX += mAX;
					mAX = 0;
					mAdsorb &= ~(flag);
				}
			} else {
				if (adsorb && ((mAdsorbBak & flag) == 0)) {
					mAdsorbBak |= flag;
					mAdsorb |= flag;
					mAX += mCurX + x;
					mCurX = -x;
				} else {
					// nothing
					return false;
				}
			}
			return true;
		}

		/**
		 * 检查垂直方向上的吸附，会修改 {@link #mCurY}、 {@link #mAY}、 {@link #mAdsorb} 、
		 * {@link #mAdsorbBak}
		 * 
		 * @param y
		 * @param flag
		 * @return 是否做了吸附处理
		 */
		private boolean checkAdsorbV(int y, int flag) {
			final int t = (int) (y + mCurY + mAY);
			boolean adsorb = (t > -DEF_ADSORB_V && t < DEF_ADSORB_V);
			if ((mAdsorb & flag) != 0) {
				if (adsorb) {
					mAY += mCurY;
					mCurY = 0;
				} else {
					mCurY += mAY;
					mAY = 0;
					mAdsorb &= ~(flag);
				}
			} else {
				if (adsorb && (mAdsorbBak & flag) == 0) {
					mAdsorbBak |= flag;
					mAdsorb |= flag;
					mAY += mCurY + y;
					mCurY = -y;
				} else {
					// nothing
					return false;
				}
			}
			return true;
		}

		protected void UnionInvalidateRect(Rect bound) {
			if (mRedraw) {
				bound.union(sBound);
				mRedraw = false;
			} else if (mHitId >= 0 && mDirty) {
				final Rect rc;
				if (mHitId < _KEY_MAX_) {
					rc = mRcTmp;
					rc.setEmpty();
					_gainKeyBound(rc, mRcTarget);
					bound.union(rc);
				} else {
					rc = mRcTarget;
					bound.union(_addVideoCoverPadding(rc));
				}

				// 检查边界吸附
				if (!checkAdsorbH(rc.left - sBound.left, FLAG_ADSORB_LEFT)) {
					checkAdsorbH(rc.right - sBound.right, FLAG_ADSORB_RIGHT);
				}
				if (!checkAdsorbV(rc.top - sBound.top, FLAG_ADSORB_TOP)) {
					checkAdsorbV(rc.bottom - sBound.bottom, FLAG_ADSORB_BOTTOM);
				}

				rc.offset((int) mCurX, (int) mCurY);
				if (rc != mRcTarget) {
					mRcTarget.offset((int) mCurX, (int) mCurY);
				}

				if (mHitId < _KEY_MAX_)
					bound.union(rc);
				else
					bound.union(_addVideoCoverPadding(rc));

				mCurX = 0;
				mCurY = 0;
				mDirty = false;
			}
		}

		protected void Draw(Canvas canvas, Bitmap video, int tick) {
			drawBackground(canvas);
			drawKeypad(canvas);
			drawVideo(canvas, video);
		}

		/** 返回位置所在的最顶层元素ID */
		private int _hitTestId(int x, int y) {
			final Rect[] rc = sRcKeys;
			for (int i = _KEY_MAX_; i >= 0; i--) {
				if (rc[i].contains(x, y))
					return i;
			}
			return -1;
		}

		private static void resetRectSize(Rect rc, int w, int h) {
			final int x = rc.left + rc.right;
			final int y = rc.top + rc.bottom;
			rc.set((x - w) >> 1, (y - h) >> 1, (x + w) >> 1, (y + h) >> 1);
		}

		private void _tryScale(int id) {
			final Context context = mShow.getContext();
			if (id < 0) {
				// 空白区
				mDialog = new ColorPickerDialog(context, this, sBackground);
				mDialog.show();
			} else if (id < _KEY_MAX_) {
				// 按钮
				Rect rc = sRcKeys[id], r;
				final int ox = rc.left, oy = rc.top;

				Drawable bg = new DEF_DRAWABLE_BG(context.getResources());
				r = sRcKeyBg;
				bg.setBounds(r.left + ox, r.top + oy, r.right + ox, r.bottom
						+ oy);

				Drawable title = new DEF_DRAWABLE_TITLE(TITLE[id], true);
				r = sRcKeyTitle;
				title.setBounds(r.left + ox, r.top + oy, r.right + ox, r.bottom
						+ oy);
				mDialog = new CustomSoftKeySizeDialog(context, this, rc, bg,
						title);
				mDialog.show();
				// for (int i = 0, c = _KEY_MAX_; i < c; i++) {
				// resetRectSize(sRcKeys[i], nw, nh);
				// }
				// mRedraw = true;
			} else if (id == _KEY_MAX_) {
				// 游戏区按标准大小的整数倍增加
				final int BW = Gmud.WQX_ORG_WIDTH, BH = Gmud.WQX_ORG_HEIGHT;
				final int MW = sWidth, MH = sHeight;
				int w = sRcVideo.width(), h = sRcVideo.height();
				int nw, nh;
				if (w >= MW || h >= MH) {
					nw = BW;
					nh = BH;
				} else {
					if (w * BH > h * BW) {
						nw = BW + BW * h / BH;
						nh = BH + h;
					} else {
						nh = BH + BH * w / BW;
						nw = BW + w;
					}
					if (nw > MW || nh > MH) {
						if (nw * MH > nh * MW) {
							nh = nh * MW / nw;
							nw = MW;
						} else {
							nw = nw * MH / nh;
							nh = MH;
						}
					}
				}
				resetRectSize(sRcVideo, nw, nh);
				if (!Rect.intersects(sBound, sRcVideo)) {
					// 如果不可见，则需要调整回来
					sRcVideo.offsetTo(sBound.left, sBound.top);
				}
				mRedraw = true;
			}
		}

		//
		// ////////////////////////////////////////////////////////////////////////
		//
		// - 手势事件 -
		//
		//

		// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
		public boolean onSingleTapUp(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onSingleTapUp");
			return true;
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		public void onLongPress(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onLongPress");
		}

		// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (DEBUG)
				Log.d(DBG_TAG, "onScroll dx=" + distanceX + " dy=" + distanceY
						+ " ox=" + e2.getX() + " oy=" + e2.getY());
			if (mHitId >= 0) {
				mCurX -= distanceX;
				mCurY -= distanceY;
				mDirty = true;
			}
			return true;
		}

		// 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE,
		// 1个ACTION_UP触发
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (DEBUG)
				Log.d(DBG_TAG, "onFling");
			return false;
		}

		// 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
		// 注意和onDown()的区别，强调的是没有松开或者拖动的状态
		public void onShowPress(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onShowPress");
		}

		// 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发Java代码
		public boolean onDown(MotionEvent e) {
			mCurX = 0;
			mCurY = 0;
			mHitId = _hitTestId((int) e.getX(), (int) e.getY());
			if (DEBUG)
				Log.d(DBG_TAG, "onDown at=" + mHitId);
			if (mHitId >= 0) {
				mDirty = true;
				mAdsorb = 0;
				mAdsorbBak = 0;
				mRcTarget = sRcKeys[mHitId];
			} else {
				mDirty = false;
			}
			return true;
		}

		public boolean onDoubleTap(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onDoubleTap action=" + e.getAction());
			return true;
		}

		public boolean onDoubleTapEvent(MotionEvent e) {
			final int action = e.getAction();
			if (DEBUG)
				Log.d(DBG_TAG, "onDoubleTapEvent action=" + action);
			if (action == MotionEvent.ACTION_UP) {
				_tryScale(mHitId);
			}
			return true;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onSingleTapConfirmed");
			return true;
		}

		@Override
		public void colorChanged(int color) {
			sBackground = color;
			mRedraw = true;
		}

		private void clear() {
			mHitId = -1;
			if (mDialog != null) {
				mDialog.cancel();
				mDialog = null;
			}
		}

		public void apply() {
			applyDesign(this);
			clear();
		}

		public void cancel() {
			cancelDesign(this);
			clear();
		}

		public void reset() {
			resetDesign();
			clear();
		}

		@Override
		public void onCustomSoftKeyApply(Rect rcFocuse, Rect rcBg, Rect rcTitle) {
			final int nw = rcFocuse.width();
			final int nh = rcFocuse.height();
			final int ox = rcFocuse.left, oy = rcFocuse.top;
			final Rect rc = TMP_RECT;
			rc.set(rcBg);
			rc.offset(-ox, -oy);
			sRcKeyBg.set(rc);
			rc.set(rcTitle);
			rc.offset(-ox, -oy);
			sRcKeyTitle.set(rc);
			for (int i = 0; i < _KEY_MAX_; i++) {
				resetRectSize(sRcKeys[i], nw, nh);
			}

			sRcKeyBorder.set(0, 0, nw, nh);
			sRcKeyBorder.union(sRcKeyBg);
			sRcKeyBorder.union(sRcKeyTitle);

			mRedraw = true;
		}
	}

	/** 默认背板绘制 */
	private static final class DEF_DRAWABLE_BG extends Drawable {
		private static Drawable sNormal, sFocus;

		private int mAlpha;

		public DEF_DRAWABLE_BG(Resources res) {
			if (sNormal == null)
				sNormal = res.getDrawable(R.drawable.bg_normal);
			if (sFocus == null)
				sFocus = res.getDrawable(R.drawable.bg_focus);
			mAlpha = 255;
		}

		private Drawable getCurDrawable() {
			return getLevel() == 0 ? sNormal : sFocus;
		}

		@Override
		public void draw(Canvas canvas) {
			Drawable d = getCurDrawable();
			if (d != null) {
				if (mAlpha != 255)
					d.setAlpha(mAlpha);
				d.setBounds(getBounds());
				d.draw(canvas);
				if (mAlpha != 255)
					d.setAlpha(255);
			}
		}

		@Override
		public void setAlpha(int alpha) {
			mAlpha = alpha;
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		protected boolean onLevelChange(int level) {
			if (sFocus != null && sNormal != null)
				return true;
			return super.onLevelChange(level);
		}
	};

	/** 默认标题绘制 */
	private static final class DEF_DRAWABLE_TITLE extends Drawable {
		private String mTitle;
		// private boolean mCustom;
		private float mTextSize, mTextScale;
		private int mAlpha;
		private int mWidth, mHeight;
		private Paint mPaint;

		public DEF_DRAWABLE_TITLE(String title) {
			this(title, false);
		}

		public DEF_DRAWABLE_TITLE(String title, boolean custom) {
			mTitle = title;
			// mCustom = custom;

			mTextSize = mTextScale = 1;
			mWidth = mHeight = 0;
			mAlpha = 255;
			mPaint = TITLE_PAINT;
		}

		@Override
		public void draw(Canvas canvas) {
			final Paint p = mPaint;
			p.setColor(getLevel() == 0 ? Color.GREEN : Color.YELLOW);
			p.setTextSize(mTextSize);
			p.setTextScaleX(mTextScale);
			p.setAlpha(mAlpha);
			String title = mTitle;
			Rect bounds = new Rect();
			p.getTextBounds(title, 0, title.length(), bounds);
			Rect rc = getBounds();
			canvas.drawText(title, rc.centerX() - bounds.centerX(),
					rc.centerY() - bounds.centerY(), p);
		}

		@Override
		public void setAlpha(int alpha) {
			mAlpha = alpha;
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		protected boolean onLevelChange(int level) {
			return super.onLevelChange(level);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			final int w = bounds.width();
			final int h = bounds.height();
			if (w == mWidth && h == mHeight)
				return;

			mWidth = w;
			mHeight = h;

			final Paint p = mPaint;
			float scale = 1, size = (float) h * 5 / 6;
			p.setTextSize(size);
			p.setTextScaleX(scale);
			float textW = p.measureText(mTitle);
			if (textW > w) {
				scale = w / textW;
			}
			mTextSize = size;
			mTextScale = scale;
		}
	};
}