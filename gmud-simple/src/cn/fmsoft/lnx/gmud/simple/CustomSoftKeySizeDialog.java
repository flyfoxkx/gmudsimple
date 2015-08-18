package cn.fmsoft.lnx.gmud.simple;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 调整　按钮　尺寸的对话框
 * 
 * @author nxliao
 * 
 */
class CustomSoftKeySizeDialog extends Dialog {
	final static boolean DEBUG = true;
	final static String DBG_TAG = CustomSoftKeySizeDialog.class.getName();

	private static final int ITEM_HIT_TEST = -2;
	private static final int ITEM_NONE = -1;
	private static final int ITEM_FOCUSE = 0;
	private static final int ITEM_BG = 1;
	private static final int ITEM_TITLE = 2;

	/* 3个功能按钮 */
	private static final int ITEM_MAX = 3;

	private static final int[] STR_ID_BUTTON = new int[] {
			R.string.custom_key_hithot, R.string.custom_key_bg,
			R.string.custom_key_title, };
	private static final int[] STR_ID_TIP = new int[] {
			R.string.custom_key_summary_hithot, R.string.custom_key_summary_bg,
			R.string.custom_key_summary_title,
			R.string.custom_key_summary_apply, };

	public interface OnCustomSoftKeyListener {
		/**
		 * 应用虚拟按键尺寸修改
		 * 
		 * @param rcFocuse
		 *            热点区
		 * @param rcBg
		 *            背板区
		 * @param rcTitle
		 *            标题区
		 */
		public void onCustomSoftKeyApply(final Rect rcFocuse, final Rect rcBg,
				final Rect rcTitle);
	}

	private OnCustomSoftKeyListener mListener;
	private Drawable mDrawables[] = new Drawable[ITEM_MAX];

	/** 热点区示意——虚线框 */
	private static final class DEF_DRAWABLE_FOCUS extends Drawable {
		private Paint mPaint;

		public DEF_DRAWABLE_FOCUS() {
			super();
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(1.5f);
			mPaint.setColor(Color.MAGENTA);
			mPaint.setPathEffect(new DashPathEffect(new float[] { 8, 5, 6, 4, 3, 9 },
					1));
		}

		@Override
		public void draw(Canvas canvas) {
			if (getLevel() == 1)
				canvas.drawRect(getBounds(), mPaint);
		}

		@Override
		public void setAlpha(int alpha) {
			mPaint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		@Override
		public int getOpacity() {
			return 0;
		}
	}

	/**
	 * 设计按钮尺寸样式
	 * 
	 * @param context
	 * @param listener
	 *            监听器
	 * @param rcFocus
	 *            事件响应区大小
	 * @param bg
	 *            背板，其 bound 表示尺寸，通过 {@link Drawable#setLevel(int)} 来确定显示状态
	 * @param title
	 *            标题，其 bound 表示尺寸，通过 {@link Drawable#setLevel(int)} 来确定显示状态
	 */
	public CustomSoftKeySizeDialog(Context context,
			final OnCustomSoftKeyListener listener, final Rect rcFocus,
			final Drawable bg, final Drawable title) {
		super(context);
		mListener = listener;
		final Drawable focus = new DEF_DRAWABLE_FOCUS();
		focus.setBounds(rcFocus);
		mDrawables[ITEM_FOCUSE] = focus;
		mDrawables[ITEM_BG] = bg;
		mDrawables[ITEM_TITLE] = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OnCustomSoftKeyListener listener = new OnCustomSoftKeyListener() {
			@Override
			public void onCustomSoftKeyApply(Rect rcFocuse, Rect rcBg,
					Rect rcTitle) {
				if (mListener != null) {
					mListener.onCustomSoftKeyApply(rcFocuse, rcBg, rcTitle);
				}
				dismiss();
			}
		};
		setContentView(new AdjustButtonView(getContext(), listener, mDrawables));
		setTitle(R.string.custom_key_dialog);
		setCanceledOnTouchOutside(true);
		// View v = getWindow().findViewById(android.R.id.title);
		// if (v != null && v instanceof TextView) {
		// v.setBackgroundColor(Color.RED);
		// }
	}

	private static class AdjustButtonView extends View {
		/* 位置标记, 默认使用 LEFT|TOP，其它方向需要判断 */
		private static final int POS_X_LEFT = 0;
		private static final int POS_X_RIGHT = 1;
		private static final int POS_Y_TOP = 0;
		private static final int POS_Y_BOTTOM = 1 << 1;
		// private static final int POS_MASK_X = POS_X_LEFT | POS_X_RIGHT;
		// private static final int POS_MASK_Y = POS_Y_TOP | POS_Y_BOTTOM;

		/* 绘制4角的线条方向{[dx,dy],[dx,dy]} */
		private static final int[][] CORNER_DIR = new int[][] { { 1, 0, 0, 1 },
				{ -1, 0, 0, 1 }, { 1, 0, 0, -1 }, { -1, 0, 0, -1 } };
		private static final int[] CORNER_INDEX = new int[] {
				POS_X_LEFT | POS_Y_TOP, POS_X_RIGHT | POS_Y_TOP,
				POS_X_LEFT | POS_Y_BOTTOM, POS_X_RIGHT | POS_Y_BOTTOM, };

		private static final int DEF_CORNER_BORDER_SIZE = 4;

		/* 默认界面大小(2*DEF_CENTER_X, 2*DEF_CENTER_Y) */
		private static final int DEF_CENTER_X = 100;
		private static final int DEF_CENTER_Y = 100;

		private static final int DEF_BT_PADDING_TOP = 3;
		private static final int DEF_BT_PADDING_HORIZONTAL = 8;
		private static final int DEF_BT_HEIGHT = 16;
		private static final int DEF_BT_WIDTH = (DEF_CENTER_X * 2 - DEF_BT_PADDING_HORIZONTAL
				* (ITEM_MAX + 1))
				/ ITEM_MAX;

		/* 提示语位置 */
		private static final int DEF_TIP_TOP = DEF_CENTER_Y * 2 - DEF_BT_HEIGHT
				- DEF_BT_PADDING_TOP;

		/* 目标物件的尺寸限制 */
		private static final int DEF_SHOW_TOP = DEF_BT_PADDING_TOP
				+ DEF_BT_HEIGHT + 6;
		private static final int DEF_SIZE_W_MIN = 16;
		private static final int DEF_SIZE_W_MAX = DEF_CENTER_X * 2 - 16;
		private static final int DEF_SIZE_H_MIN = 16;
		private static final int DEF_SIZE_H_MAX = DEF_TIP_TOP - DEF_SHOW_TOP;
		private static final int DEF_SHOW_CX = DEF_CENTER_X;
		private static final int DEF_SHOW_CY = DEF_SHOW_TOP
				+ (DEF_SIZE_H_MAX / 2);

		private int CENTER_X;
		private int CENTER_Y;
		private int BT_PADDING_TOP;
		private int BT_PADDING_HORIZONTAL;
		private int BT_HEIGHT;
		private int BT_WIDTH;
		private int TIP_TOP;
		private int SHOW_TOP;
		private int SHOW_CX;
		private int SHOW_CY;
		private int SIZE_W_MIN;
		private int SIZE_W_MAX;
		private int SIZE_H_MIN;
		private int SIZE_H_MAX;
		private int CORNER_BORDER_SIZE;

		private OnCustomSoftKeyListener mListener;

		private Drawable mDrawables[];
		private Rect mRcBounds[] = new Rect[ITEM_MAX];

		private Rect mRcTmp = new Rect();
		private RectF mRcfTmp = new RectF();

		/**
		 * 目前的属性类别 {@link #ITEM_NONE}
		 * {@link CustomSoftKeySizeDialog#ITEM_FOCUSE} ...
		 */
		private int mCurSel = -1;

		private Paint mPaint;
		private final int mTouchSlop;
		private final int mDoubleTapTimeout;

		private long mLastClickTick;

		private boolean mZoomIn = false;
		private int mFlagPos = POS_X_LEFT | POS_Y_TOP;

		private static final int[] COLOR_TOOLBAR_BG = new int[] { Color.GRAY,
				Color.GREEN };
		private static final int[] COLOR_TOOLBAR_TITLE = new int[] {
				Color.BLACK, Color.RED };
		private static final int[] COLOR_TIP = new int[] { Color.DKGRAY,
				Color.WHITE };
		private static final int[] COLOR_CORNER_BORDER = new int[] {
				Color.CYAN, Color.YELLOW };
		private static final int COLOR_BACKGROUND = 0x80666666;

		private final String[] STR_BUTTON = new String[ITEM_MAX];
		private final String[] STR_TIP = new String[ITEM_MAX + 1];

		public AdjustButtonView(Context context,
				OnCustomSoftKeyListener listener, Drawable drawables[]) {
			super(context);

			mListener = listener;
			mDrawables = drawables;

			mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
			mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();

			final Rect rc = mRcTmp;
			rc.setEmpty();
			for (int i = 0; i < ITEM_MAX; i++) {
				final Rect bound = drawables[i].getBounds();
				mRcBounds[i] = new Rect(bound);
				rc.union(bound);
			}
			// TODO: 调整各区域的偏移，使整体的中心与View的中心吻合，限制无效区域
			for (int i = 0, cx = rc.centerX(), cy = rc.centerY(); i < ITEM_MAX; i++) {
				mRcBounds[i].offset(-cx, -cy);
				drawables[i].setBounds(mRcBounds[i]);
			}

			// 初始化各尺寸常量
			final Resources res = context.getResources();
			final float density = res.getDisplayMetrics().density;
			CENTER_X = (int) (DEF_CENTER_X * density);
			CENTER_Y = (int) (DEF_CENTER_Y * density);
			BT_PADDING_TOP = (int) (DEF_BT_PADDING_TOP * density);
			BT_PADDING_HORIZONTAL = (int) (DEF_BT_PADDING_HORIZONTAL * density);
			BT_HEIGHT = (int) (DEF_BT_HEIGHT * density);
			BT_WIDTH = (int) (DEF_BT_WIDTH * density);
			TIP_TOP = (int) (DEF_TIP_TOP * density);
			SHOW_TOP = (int) (DEF_SHOW_TOP * density);
			SHOW_CX = (int) (DEF_SHOW_CX * density);
			SHOW_CY = (int) (DEF_SHOW_CY * density);
			SIZE_W_MIN = (int) (DEF_SIZE_W_MIN * density);
			SIZE_W_MAX = (int) (DEF_SIZE_W_MAX * density);
			SIZE_H_MIN = (int) (DEF_SIZE_H_MIN * density);
			SIZE_H_MAX = (int) (DEF_SIZE_H_MAX * density);
			CORNER_BORDER_SIZE = (int) (DEF_CORNER_BORDER_SIZE * density);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

			for (int i = 0; i < ITEM_MAX; i++) {
				STR_BUTTON[i] = res.getString(STR_ID_BUTTON[i]);
				STR_TIP[i] = res.getString(STR_ID_TIP[i]);
			}
			STR_TIP[ITEM_MAX] = res.getString(STR_ID_TIP[ITEM_MAX]);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			final Paint p = mPaint;
			final Rect rc = mRcTmp;
			final RectF rectF = mRcfTmp;

			canvas.drawColor(COLOR_BACKGROUND);

			p.setTextSize(BT_HEIGHT * 3 / 4);
			p.setStrokeWidth(1);

			// draw Tool-bar
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			rectF.set(BT_PADDING_HORIZONTAL, BT_PADDING_TOP, BT_WIDTH
					+ BT_PADDING_HORIZONTAL, BT_PADDING_TOP + BT_HEIGHT);
			for (int i = 0, c = ITEM_MAX; i < c; i++) {
				p.setColor(COLOR_TOOLBAR_BG[mCurSel == i ? 1 : 0]);
				canvas.drawRoundRect(rectF, CORNER_BORDER_SIZE
						+ CORNER_BORDER_SIZE, CORNER_BORDER_SIZE, p);
				p.setColor(COLOR_TOOLBAR_TITLE[mCurSel == i ? 1 : 0]);
				String title = STR_BUTTON[i];
				p.getTextBounds(title, 0, title.length(), rc);
				canvas.drawText(title, rectF.centerX() - rc.centerX(),
						rectF.centerY() - rc.centerY(), p);
				rectF.offset(BT_WIDTH + BT_PADDING_HORIZONTAL, 0);
			}

			// draw tip
			p.setStyle(Paint.Style.FILL);
			p.setColor(COLOR_TIP[0]);
			p.setTextSize(BT_HEIGHT);
			p.setLinearText(true);
			String tip = STR_TIP[mCurSel < 0 ? ITEM_MAX : mCurSel];
			p.getTextBounds(tip, 0, tip.length(), rc);
			p.setShadowLayer(3, 2, 1, COLOR_TIP[1]);
			canvas.drawText(tip, CENTER_X - rc.centerX(),
					TIP_TOP + rc.height(), p);
			p.setShadowLayer(0, 0, 0, 0);

			// draw focus
			canvas.translate(SHOW_CX, SHOW_CY);
			mDrawables[ITEM_FOCUSE].draw(canvas);
			mDrawables[ITEM_BG].draw(canvas);
			mDrawables[ITEM_TITLE].draw(canvas);

			// p.setColor(Color.RED);
			// canvas.drawLine(-4, 0, 4, 0, p);
			// canvas.drawLine(0, -4, 0, 4, p);

			// draw bolder
			if (mCurSel == ITEM_NONE) {

			} else {
				mDrawables[mCurSel].copyBounds(rc);
				_drawCornerBorder(canvas, rc,
						(mZoomIn && mHitId >= ITEM_MAX) ? mFlagPos : -1);
			}
		}

		/** 绘制矩形的４角 */
		private void _drawCornerBorder(Canvas canvas, Rect rc, int activePos) {
			final Paint p = mPaint;
			final int corner_size = CORNER_BORDER_SIZE;
			for (int i = 0; i < 4; i++) {
				final int posFlag = CORNER_INDEX[i];
				final int x = ((posFlag & POS_X_RIGHT) != 0) ? rc.right
						: rc.left;
				final int y = ((posFlag & POS_Y_BOTTOM) != 0) ? rc.bottom
						: rc.top;
				final int[] dirs = CORNER_DIR[i];
				p.setColor(COLOR_CORNER_BORDER[posFlag == activePos ? 1 : 0]);
				canvas.drawLine(x, y, x + dirs[0] * corner_size, y + dirs[1]
						* corner_size, p);
				canvas.drawLine(x, y, x + dirs[2] * corner_size, y + dirs[3]
						* corner_size, p);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2);
		}

		/**
		 * {@link #ITEM_NONE}-空白区, {@link #ITEM_HIT_TEST}-单击按钮测试效果, &lt;
		 * {@link #ITEM_MAX}-平移组件, 其它:缩放与{@link #mCurSel}相关的组件
		 */
		private int mHitId = ITEM_NONE;
		private float mLastX, mLastY;
		private float mOverX, mOverY;
		private float mDownX, mDownY;
		private boolean mFirstClick;
		private boolean mChangeItem;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {
				if (mFirstClick) {
					if (x >= mDownX + mTouchSlop || mDownX >= x + mTouchSlop
							|| y >= mDownY + mTouchSlop
							|| mDownY >= y + mTouchSlop) {
						mFirstClick = false;
					} else {
						break;
					}
				}

				if (mChangeItem) {
				} else if (mZoomIn) {
					_tryScale(x, y);
				} else if (mHitId == ITEM_NONE) {
					if (mCurSel != ITEM_NONE) {
						// 位置于各“属性”区外，认为是调整大小
						mHitId = ITEM_MAX + mCurSel;
						mZoomIn = true;
					} else {
						// 不处理“空白区”的移动事件
					}
				} else if (mHitId == ITEM_HIT_TEST) {
					_tryHitTest(x, y);
				} else if (mHitId < ITEM_MAX) {
					_tryMove(x, y);
				}
				mLastX = x;
				mLastY = y;
			}
				break;

			case MotionEvent.ACTION_DOWN: {
				_onDown(x, y);
			}
				break;

			case MotionEvent.ACTION_UP:
				if (mChangeItem) {
					_resetDrawableStatus(ITEM_NONE, mCurSel);
				} else if (mHitId == ITEM_NONE) {
					// click space area
					if (mCurSel != ITEM_NONE) {
						mCurSel = ITEM_NONE;
						_resetDrawableStatus(ITEM_NONE, mCurSel);
					} else {
						_onClick();
					}
				} else if (mHitId == ITEM_HIT_TEST) {
					_resetDrawableStatus(ITEM_NONE, ITEM_NONE);
				} else {
					// 应用尺寸修改（平移或缩放）
					mDrawables[mCurSel].copyBounds(mRcBounds[mCurSel]);
				}
				mHitId = ITEM_NONE;
				mChangeItem = false;
				mZoomIn = false;
				invalidate();
				break;

			case MotionEvent.ACTION_CANCEL:
				if (mChangeItem) {
				} else if (mHitId == ITEM_NONE) {
					_resetDrawableStatus(mHitId, mCurSel);
				} else if (mHitId == ITEM_HIT_TEST) {
					_resetDrawableStatus(ITEM_NONE, ITEM_NONE);
				} else {
					// 还原尺寸修改（平移或缩放）
					mDrawables[mCurSel].setBounds(mRcBounds[mCurSel]);
				}
				mHitId = ITEM_NONE;
				mChangeItem = false;
				mZoomIn = false;
				invalidate();
				break;
			}
			return true;
		}

		/** 重置各 {@link #mDrawables} 的状态 */
		private void _resetDrawableStatus(int hitId, int selId) {
			for (int i = 0; i < ITEM_MAX; i++) {
				final Drawable d = mDrawables[i];

				// 点击测试，或，此组件是“热点区”并被选中，或，
				final int level;
				if (i == ITEM_FOCUSE) {
					level = (selId == ITEM_NONE && hitId == ITEM_NONE) ? 0 : 1;
				} else {
					level = hitId == ITEM_HIT_TEST ? 1 : 0;
				}
				d.setLevel(level);

				// 当前选择的组件或未选择任何组件
				final int alpha = (selId == i || selId == ITEM_NONE) ? 255 : 96;
				d.setAlpha(alpha);
			}
		}

		private void _onDown(float x, float y) {
			mOverY = mOverX = 0;
			mLastX = mDownX = x;
			mLastY = mDownY = y;
			mFirstClick = true;
			mZoomIn = false;
			mChangeItem = false;

			final int tx = (int) (x - SHOW_CX), ty = (int) (y - SHOW_CY);
			if (y < SHOW_TOP) {
				// on ToolBar
				int pos = (int) (x / (BT_PADDING_HORIZONTAL + BT_WIDTH));
				if (pos >= ITEM_MAX)
					pos = ITEM_MAX - 1;
				mHitId = pos;
				mChangeItem = true;
				if (mCurSel != pos) {
					mCurSel = pos;
				}
			} else if (mCurSel == ITEM_NONE) {
				if (mRcBounds[ITEM_FOCUSE].contains(tx, ty)) {
					// 单击演示
					mHitId = ITEM_HIT_TEST;
					_resetDrawableStatus(mHitId, mCurSel);
				} else {
					// 无处理，等待单击空白区或仅仅无效操作（MOVE）
					mHitId = ITEM_NONE;
				}
			} else {
				// 检查是否平移组件
				final Rect rc = mRcBounds[mCurSel];
				if (rc.contains(tx, ty)) {
					mHitId = mCurSel;
				} else {
					// 如果待会出现MOVE事件，会触发缩放
					mHitId = ITEM_NONE;
					mFlagPos = (tx > rc.centerX() ? POS_X_RIGHT : POS_X_LEFT)
							| (ty > rc.centerY() ? POS_Y_BOTTOM : POS_Y_TOP);
				}
			}
			invalidate();
		}

		private void _tryHitTest(float x, float y) {
			// nothing happen
		}

		private void _onClick() {
			final long tick = SystemClock.elapsedRealtime();
			if (mDoubleTapTimeout > tick - mLastClickTick) {
				mListener.onCustomSoftKeyApply(mRcBounds[ITEM_FOCUSE],
						mRcBounds[ITEM_BG], mRcBounds[ITEM_TITLE]);
			}
			mLastClickTick = tick;
		}

		// 平移
		private void _tryMove(float x, float y) {
			final Rect rc = mRcTmp;
			mDrawables[mCurSel].copyBounds(rc);

			final float dx = x - mLastX + mOverX;
			final float dy = y - mLastY + mOverY;
			int ox = (int) dx;
			int oy = (int) dy;
			if (ox + rc.left + SIZE_W_MAX / 2 < 0) {
				ox = -SIZE_W_MAX / 2 - rc.left;
			} else if (ox + rc.right > SIZE_W_MAX / 2) {
				ox = SIZE_W_MAX / 2 - rc.right;
			}
			if (oy + rc.top + SIZE_H_MAX / 2 < 0) {
				oy = -SIZE_H_MAX / 2 - rc.top;
			} else if (oy + rc.bottom > SIZE_H_MAX / 2) {
				oy = SIZE_H_MAX / 2 - rc.bottom;
			}
			mOverX = dx - ox;
			mOverY = dy - oy;
			if (ox != 0 || oy != 0) {
				rc.offset(ox, oy);
				mDrawables[mCurSel].setBounds(rc);
				invalidate();
			}
		}

		private void _tryScale(float x, float y) {
			final Rect rc = mRcTmp;
			mDrawables[mCurSel].copyBounds(rc);

			final float dx = x - mLastX + mOverX;
			final float dy = y - mLastY + mOverY;
			int ox = (int) dx;
			int oy = (int) dy;
			if ((mFlagPos & POS_X_RIGHT) == 0) {
				if (ox + rc.left + SIZE_W_MAX / 2 < 0) {
					ox = -SIZE_W_MAX / 2 - rc.left;
				} else if (ox + rc.left + SIZE_W_MIN > rc.right) {
					ox = rc.right - rc.left - SIZE_W_MIN;
				}
				rc.left += ox;
			} else {
				if (ox + rc.right > SIZE_W_MAX / 2) {
					ox = SIZE_W_MAX / 2 - rc.right;
				} else if (ox + rc.right < rc.left + SIZE_W_MIN) {
					ox = rc.left + SIZE_W_MIN - rc.right;
				}
				rc.right += ox;
			}

			if ((mFlagPos & POS_Y_BOTTOM) == 0) {
				if (oy + rc.top + SIZE_H_MAX / 2 < 0) {
					oy = -SIZE_H_MAX / 2 - rc.top;
				} else if (oy + rc.top + SIZE_H_MIN > rc.bottom) {
					oy = rc.bottom - rc.top - SIZE_H_MIN;
				}
				rc.top += oy;
			} else {
				if (oy + rc.bottom > SIZE_H_MAX / 2) {
					oy = SIZE_H_MAX / 2 - rc.bottom;
				} else if (oy + rc.bottom < rc.top + SIZE_H_MIN) {
					oy = rc.top + SIZE_H_MIN - rc.bottom;
				}
				rc.bottom += oy;
			}

			mOverX = dx - ox;
			mOverY = dy - oy;
			if (ox != 0 || oy != 0) {
				mDrawables[mCurSel].setBounds(rc);
				invalidate();
			}
		}
	}
}
