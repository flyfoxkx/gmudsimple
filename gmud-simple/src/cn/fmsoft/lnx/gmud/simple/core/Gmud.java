/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * Gmud: base server, initialize static data, start input thread, provide archive.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple.core;

import cn.fmsoft.lnx.gmud.simple.core.GmudData.ClassID;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 对外接口封装
 * 
 * @author nxliao
 * 
 */
public class Gmud {

	final static boolean DEBUG = true;

	private final static Object SYNC = new Object();

	protected final static String END_TAG = "gmud.end";

	public final static String SAVE_PATH = "gmud_save";

	public final static int WQX_ORG_WIDTH = 160;
	public final static int WQX_ORG_HEIGHT = 80;
	static final int FRAME_TIME = 1000 / 60;

	/** 等待按键 */
	static final int DELAY_WAITKEY = 50;
	/** 1秒 */
	static final int DELAY_1S = 1000;
	static final int DELAY_TICK = 10;
	/** 自动按键，如长按 {LEFT} 等 */
	static final int DELAY_AUTO_KEY_MAX = 120;
	static final int DELAY_AUTO_KEY_RATE = 10;
	static final int DELAY_AUTO_KEY_MIN = 60;

	static Map sMap;
	static Player sPlayer;

	// public static boolean Running = false;

	public final static int RS_UNINITIALIZED = 0;
	public final static int RS_RUNNING = 1;
	public final static int RS_PAUSE = 2;
	public final static int RS_STOP = 3;

	private static int sRunStatus = RS_UNINITIALIZED;

	/** 是否已经开启了游戏主线程 */
	public static boolean PLAYING = false;

	static Context sContext;

	static Activity sActivity = null;

	static boolean sbConfig_MinScale = true;

	private static ICallback sCallback;

	/**
	 * 清除数据，用于自杀
	 * 
	 * @return
	 */
	static boolean CleanSave() {
		return sPlayer.Clean(sContext);
	}

	/**
	 * 存档
	 * 
	 * @return
	 */
	static boolean WriteSave() {
		return sPlayer.Save(sContext);
	}

	/**
	 * 读档
	 * 
	 * @return
	 */
	static boolean LoadSave() {
		return sPlayer.load(sContext);
	}

	static void tryWait() throws InterruptedException {
		SYNC.wait();
	}

	static void tryNotify() {
		try {
			SYNC.notifyAll();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sleep 方式延时
	 * 
	 * @param millis
	 */
	static void GmudDelay(int millis) {
		while (true) {
			final int status;
			synchronized (SYNC) {
				status = sRunStatus;
				if (status == RS_RUNNING) {
					break;
				}

				if (status == RS_STOP) {
					throw new RuntimeException(END_TAG);
				}

				try {
					tryWait();
				} catch (InterruptedException e) {
					throw new RuntimeException(END_TAG);
				}
			}
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(END_TAG);
		}
	}

	/**
	 * 等待按键，不清除旧的按键状态，只要出现掩码中的按键，就返回
	 * 
	 * @param keyFlag
	 *            按键标记，如 {@link Input#kKeyExit} | {@link Input#kKeyEnt}
	 * @return 返回按键扫描码，如 {@link Input#kKeyExit} | {@link Input#kKeyEnt}
	 */
	static int GmudWaitKey(int keyFlag) {
		while ((Input.inputstatus & keyFlag) == 0) {
			Gmud.GmudDelay(DELAY_WAITKEY);
			Input.ProcessMsg();
		}
		return Input.inputstatus;
	}

	/**
	 * 等待按键，不清除旧的按键状态，只要出现掩码中的按键或超时，就返回
	 * 
	 * @param keyFlag
	 *            按键标记，如 {@link Input#kKeyExit} | {@link Input#kKeyEnt}
	 * @param timeOut
	 *            超时，单位：毫秒，不一定精确
	 * @return 返回按键扫描码，如 {@link Input#kKeyExit} | {@link Input#kKeyEnt} ，超时返回0
	 * @see #GmudWaitKey(int)
	 */
	static int GmudWaitKey(int keyFlag, int timeOut) {
		final int delay;
		if (timeOut > DELAY_WAITKEY * 3) {
			delay = DELAY_WAITKEY;
		} else {
			delay = DELAY_TICK;
		}
		timeOut /= delay;
		while ((Input.inputstatus & keyFlag) == 0) {
			Gmud.GmudDelay(delay);
			Input.ProcessMsg();
			if (timeOut-- <= 0) {
				return 0;
			}
		}
		return Input.inputstatus;
	}

	/**
	 * 等待新的按键，会清除旧的按键
	 * 
	 * @param keyFlag
	 *            按键标记
	 * @return 按键扫描码
	 * @see #GmudWaitKey(int)
	 */
	static int GmudWaitNewKey(int keyFlag) {
		Input.ClearKeyStatus();
		return GmudWaitKey(keyFlag);
	}

	/**
	 * 等待任意键，会清除旧的按键
	 * 
	 * @see #GmudWaitNewKey(int)
	 */
	static int GmudWaitAnyKey() {
		Input.ClearKeyStatus();
		return GmudWaitKey(Input.kKeyAny);
	}

	// 初始化一些静态数据
	static void prepare(Context ctx) {
		sMap = Map.getInstance();
		sPlayer = Player.getInstance();
	}

	public static void exit() {
		Input.Stop();

		if (sActivity != null) {
			sActivity.finish();
		} else {
			System.exit(0);
		}
	}

	// public static void setMinScale(boolean bMin) {
	// sbConfig_MinScale = bMin;
	// }

	// 计算初始缩放比例
	// public static void checkScale() {
	// try {
	// final Display display = sActivity.getWindowManager()
	// .getDefaultDisplay();
	//
	// // 按竖屏的模式计算宽高比
	// int w = display.getWidth();
	// int h = display.getHeight();
	// if (sbConfig_MinScale && w > h) {
	// int tmp = w;
	// w = h;
	// h = tmp;
	// }
	//
	// final float scale;
	// float scale_w, scale_h;
	// scale_w = 1.0f * w / Gmud.WQX_ORG_WIDTH;
	// scale_h = 1.0f * h / Gmud.WQX_ORG_HEIGHT;
	// if (scale_w < scale_h) {
	// scale = scale_w;
	// } else {
	// scale = scale_h;
	// }
	// int s = (int) scale;
	// Video.resetScale(s);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public static void bind(Activity activity) {
	// sActivity = activity;
	//
	// checkScale();
	// }
	//
	// public static void unbind(Activity activity) {
	// sActivity = null;
	// }

	public Gmud(Context context) {
		sContext = context;

		sRunStatus = RS_UNINITIALIZED;

		Res.init(context);
		Video.VideoInit();
		new GmudMain(context).start();
	}

	public static interface ICallback {
		/** 弹出框输入名字，不可为空，完成后需调用 {@link Gmud#SetNewName(String)}， 0玩家名 1武器名 */
		public void EnterNewName(int type);

		public void UpdateTime(long minutes, int seconds);
	}

	public static void SetCallback(ICallback callback) {
		sCallback = callback;
	}

	public static interface IVideoCallback {
		/** 通知有更新，由外部保留副本刷新 */
		void VideoPostUpdate(Bitmap video);
	}

	public static void SetVideoCallback(IVideoCallback callback) {
		Video.SetCallback(callback);
	}

	public static void ResetVideoLayout(Rect rect) {
		Video.ResetLayout(rect);
	}

	public static boolean IsRunning() {
		synchronized (SYNC) {
			return sRunStatus == RS_RUNNING;
		}
	}

	public static void Start() {
		synchronized (SYNC) {
			if (sRunStatus == RS_UNINITIALIZED) {
				sRunStatus = RS_RUNNING;
				tryNotify();
			}
		}
	}

	/** 暂停游戏线程 */
	public static void Pause() {
		synchronized (SYNC) {
			if (sRunStatus == RS_RUNNING)
				sRunStatus = RS_PAUSE;
		}
	}

	/** 恢复游戏线程 */
	public static void Resume() {
		synchronized (SYNC) {
			if (sRunStatus == RS_PAUSE) {
				sRunStatus = RS_RUNNING;
				tryNotify();
			}
		}
	}

	/** 中止游戏线程 */
	public static boolean Exit() {
		final int status;
		synchronized (SYNC) {
			status = sRunStatus;
			sRunStatus = RS_STOP;
			tryNotify();
		}
		return (status != RS_UNINITIALIZED && status != RS_STOP);
	}

	// ////////////////////////////////////////////////////////////////////////
	private static String s_tmp_new_name = null;

	public static void SetNewName(String name) {
		s_tmp_new_name = name;
		Resume();
	}

	/** 等待输入名字， 0玩家名 1武器名 */
	protected static String WaitForNewName(final int type) {
		if (sCallback != null) {
			sCallback.EnterNewName(type);
			Pause();
			GmudDelay(1);
		}
		return s_tmp_new_name;
	}

	/** 更新显示游戏时间 */
	protected static void UpdatePlayTime(long minutes, int seconds) {
		if (sCallback != null) {
			sCallback.UpdateTime(minutes, seconds);
		}
	}

	public static void setImageSmooth(boolean smooth) {
		Video.setImageSmooth(smooth);
	}

	/** 取玩家的名字 */
	public static String GetPlayerName() {
		if (sPlayer != null)
			return sPlayer.player_name;
		return null;
	}

	/** 性别 0男 1女 */
	public static int GetPlayerSex() {
		if (sPlayer != null) {
			return sPlayer.sex;
		}
		return -1;
	}

	public static String GetPlayerClass() {
		return GmudData.class_name[sPlayer == null ? ClassID.None : sPlayer
				.GetClassID()];
	}
}
