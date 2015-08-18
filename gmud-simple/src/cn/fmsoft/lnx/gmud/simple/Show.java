/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

/**
 * 展示. 定期刷新！
 * 
 * @author nxliao
 * 
 */
public class Show extends SurfaceView implements SurfaceHolder.Callback,
		Gmud.IVideoCallback {

	private final Object LOCK = new Object();

	private final static int DELAY_AUTO_UPDATE_KEYPAD = 50;

	private static int mUpdateStatus = -1;

	/** 更新游戏区 */
	private static final int UPDATE_VIDEO = 1 << 0;
	/** 更新按键 */
	private static final int UPDATE_KEYPAD = 1 << 1;
	/** 更新全部 */
	private static final int UPDATE_ALL = 1 << 2;
	/** 停止绘制 */
	private static final int UPDATE_CANCEL = 1 << 31;

	private Bitmap mBmShadow;
	private Canvas mCanvas;

	private Configure.Design mDesign;

	private class MyThread extends Thread {
		private int mDrawTickCount;

		public MyThread() {
			super("auto-draw");
		}

		@Override
		public void run() {
			mDrawTickCount = 0;
			while (Show.this.tryDraw(mDrawTickCount)) {
				try {
					sleep(DELAY_AUTO_UPDATE_KEYPAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mDrawTickCount++;
			}
		}
	}

	public Show(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}

	public boolean tryDraw(int drawTick) {
		synchronized (LOCK) {
			final int status = mUpdateStatus;
			if ((status & UPDATE_CANCEL) != 0)
				return false;

			final boolean update_video;
			final boolean update_keypad;

			final Rect dirty = new Rect();

			final Canvas canvas;
			final SurfaceHolder holder = getHolder();

			if ((status & UPDATE_ALL) != 0) {
				update_video = true;
				update_keypad = true;
				canvas = holder.lockCanvas();
			} else {
				if (mDesign != null) {
					mDesign.UnionInvalidateRect(dirty);
				}

				update_video = (status & UPDATE_VIDEO) != 0;
				update_keypad = (status & UPDATE_KEYPAD) != 0;
				if (update_video) {
					dirty.set(Configure.sRcVideo);
				}
				if (update_keypad) {
					Configure.UnionInvalidateRect(dirty);
				}
				if (dirty.isEmpty()) {
					return true;
				} else {
					canvas = holder.lockCanvas(dirty);
				}
			}

			if (canvas != null) {
				if (mDesign != null) {
					mDesign.Draw(canvas, mBmShadow, drawTick);
				} else {
					Configure.Draw(canvas, mBmShadow, drawTick);
				}
				mUpdateStatus &= ~(UPDATE_VIDEO | UPDATE_KEYPAD | UPDATE_ALL);
			}

			try {
				holder.unlockCanvasAndPost(canvas);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.EXACTLY) {
			// Parent has told us how big to be. So be it.
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (LOCK) {
			mUpdateStatus &= ~(UPDATE_CANCEL);
		}

		Gmud.SetVideoCallback(this);

		synchronized (LOCK) {
			mUpdateStatus = UPDATE_KEYPAD | UPDATE_VIDEO;
		}

		new MyThread().start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		synchronized (LOCK) {
			Configure.reset(width > height, width, height);
			mUpdateStatus |= UPDATE_KEYPAD | UPDATE_VIDEO;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (LOCK) {
			mUpdateStatus |= UPDATE_CANCEL;
		}
		Gmud.SetVideoCallback(null);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void VideoPostUpdate(Bitmap video) {
		synchronized (LOCK) {
			if ((mUpdateStatus & UPDATE_CANCEL) != 0)
				return;

			if (video == null || video.isRecycled())
				return;

			if (mCanvas == null) {
				mCanvas = new Canvas();
			}

			if (mBmShadow == null) {
				mBmShadow = Bitmap.createBitmap(video);
				mCanvas.setBitmap(mBmShadow);
			} else if (mBmShadow.getWidth() != video.getWidth()
					|| mBmShadow.getHeight() != video.getHeight()) {
				final Bitmap bm = Bitmap.createBitmap(video);
				mCanvas.setBitmap(bm);
				mBmShadow.recycle();
				mBmShadow = bm;
			}

			mCanvas.drawBitmap(video, 0, 0, null);
			mUpdateStatus |= UPDATE_VIDEO;
		}
	}

	public void KeyPostUpdate() {
		synchronized (LOCK) {
			mUpdateStatus |= UPDATE_KEYPAD;
		}
	}

	protected void StartDesign(Configure.Design design) {
		Gmud.Pause();
		synchronized (LOCK) {
			mDesign = design;
			mUpdateStatus |= UPDATE_ALL;
			Configure.startDesign(design);
		}
	}

	protected void ApplyDesign(boolean isApply) {
		synchronized (LOCK) {
			if (isApply) {
				mDesign.apply();
			} else {
				mDesign.cancel();
			}
			mDesign = null;
			mUpdateStatus |= UPDATE_ALL;
		}
		Gmud.Resume();
	}

	protected void ResetDesign() {
		synchronized (LOCK) {
			mDesign.reset();
			mUpdateStatus |= UPDATE_ALL;
		}
	}

	protected void hideSoftKey(boolean hideSoftKey) {
		Configure.hidekeypad(hideSoftKey);
		synchronized (LOCK) {
			mUpdateStatus |= UPDATE_ALL;
		}
	}

	protected void HitTest(MotionEvent event) {
		if (Configure.HitTest(event)) {
			KeyPostUpdate();
		}
	}
}
