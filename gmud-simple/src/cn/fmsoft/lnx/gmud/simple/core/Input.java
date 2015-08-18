/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * Input: collect all input(key) messages. And, it is the main thread at background.
 * 
 * @author nxliao
 */

package cn.fmsoft.lnx.gmud.simple.core;

import android.view.KeyEvent;

/**
 * 这是一个提供静态方法的类.
 */
public class Input {
	public static final int kKeyExit = 1 << 0;
	public static final int kKeyEnt = 1 << 1;
	public static final int kKeyDown = 1 << 2;
	public static final int kKeyLeft = 1 << 3;
	public static final int kKeyUp = 1 << 4;
	public static final int kKeyRight = 1 << 5;
	public static final int kKeyPgUp = 1 << 6;
	public static final int kKeyPgDn = 1 << 7;
	public static final int kKeyFly = 1 << 8;
	public static final int kKeyAny = 0xffff;

	public static boolean Running = false;

	static int inputstatus = 0;
	static int lastkey = 0;
	static int SetKeyFlag = -1;

	/** 键盘扫描码 */
	private static final byte KEY_CODE_KB[] = new byte[9];
	// static byte setKeytp[] = new byte[9];

	/** 按键状态，可用于获取当前某个键是否按下 */
	private static int sScanCode = 0;

	/** 按键消息值，使用一次后自动清零 */
	private static int sKey = 0;

	/**
	 * 初始化输入
	 */
	protected static void InitInput() {
		Running = true;
		GmudDefaultKey();
		ClearKeyStatus();
	}

	static void ProcessMsg() {
		// MSG msg;
		// Running = GetMessage(&msg, NULL, 0, 0);
		// TranslateMessage(&msg);
		// DispatchMessage(&msg);
		// if(Running <= 0)
		// {
		// Running = 0;
		// exit(0);
		// }
		return;
	}

	static synchronized void Stop() {
		Running = false;
	}

	static synchronized void ClearKeyStatus() {
		inputstatus = 0;
	}

	/** 配置默认的键盘映射 */
	private static void GmudDefaultKey() {
		KEY_CODE_KB[0] = KeyEvent.KEYCODE_W;
		KEY_CODE_KB[1] = KeyEvent.KEYCODE_S;
		KEY_CODE_KB[2] = KeyEvent.KEYCODE_DPAD_LEFT;
		KEY_CODE_KB[3] = KeyEvent.KEYCODE_DPAD_RIGHT;
		KEY_CODE_KB[4] = KeyEvent.KEYCODE_A; // Delete 左连
		KEY_CODE_KB[5] = KeyEvent.KEYCODE_D; // PGDN 右连
		KEY_CODE_KB[6] = KeyEvent.KEYCODE_E; // enter 输入
		KEY_CODE_KB[7] = KeyEvent.KEYCODE_Q; // alt跳出
		KEY_CODE_KB[8] = KeyEvent.KEYCODE_F; // End 轻功
	}

	/**
	 * 是否有新的按键消息
	 * 
	 * @return
	 */
	protected static synchronized boolean hasKey() {
		return sKey != 0;
	}

	/**
	 * 获取新的按键消息，同时会清除此消息记录。
	 * 
	 * @return
	 */
	protected static synchronized int PopKey() {
		int key = sKey;
		sKey = 0;
		return key;
	}

	/**
	 * 获取按键状态，用来判断按键是否按下
	 * 
	 * @return
	 */
	protected static synchronized int getScanCode() {
		return sScanCode;
	}
	
	static synchronized int getKeyCode() {
		return inputstatus;
	}

	static public synchronized void GmudSetKey(int mask) {
		inputstatus = mask;
		sScanCode = mask;
	}

	static synchronized void GmudProcessKey(int key) {
		int id = 0;
		while (id < 9) {
			if (key == KEY_CODE_KB[id])
				break;
			id++;
		}
		boolean changed = (key != lastkey);
		lastkey = key;
		if (changed) {
			switch (id) {
			case 0:
				inputstatus = kKeyUp;
				return;
			case 1:
				inputstatus = kKeyDown;
				return;
			case 2:
				inputstatus = kKeyLeft;
				return;
			case 3:
				inputstatus = kKeyRight;
				return;
			case 4:
				inputstatus = kKeyPgUp | kKeyLeft;
				return;
			case 5:
				inputstatus = kKeyPgDn | kKeyRight;
				return;
			case 6:
				inputstatus = kKeyEnt;
				return;
			case 7:
				inputstatus = kKeyExit;
				return;
			case 8:
				inputstatus = kKeyFly;
				return;
			}
		}
	}

	public static boolean onKey(int keyCode, KeyEvent event) {

		if (!Running) {
			return false;
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// if (keyCode==KeyEvent.KEYCODE_Q) {
			// Gmud.sPlayer.potential += 1000;
			// } else if (keyCode==KeyEvent.KEYCODE_W) {
			// Gmud.sPlayer.exp+=2000;
			// }

			GmudProcessKey(keyCode);
			if ((inputstatus & (kKeyDown | kKeyUp | kKeyLeft | kKeyRight)) != 0) {
				// lastkey = 0;
				// inputstatus = 0;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			lastkey = 0;
		}
		return false;
	}

}
