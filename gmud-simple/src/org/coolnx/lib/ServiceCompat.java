package org.coolnx.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public abstract class ServiceCompat extends Service {
	/** {@link Service#setForeground} */
	private static final Class<?>[] clazz_setForeground = new Class[] { Boolean.TYPE };
	/** {@link Service#startForeground} */
	private static final Class<?>[] clazz_startForeground = new Class[] {
			Integer.TYPE, Notification.class };
	/** {@link Service#stopForeground} */
	private static final Class<?>[] clazz_stopForeground = new Class[] { Boolean.TYPE };

	private NotificationManager mNM;
	private Method mMethod_setForeground;
	private Method mMethod_startForeground;
	private Method mMethod_stopForeground;

	private int mBinderCount = 0;
	private int mStartId = -1;
	private Handler mHandler = new Handler() {
		public final void handleMessage(Message paramAnonymousMessage) {
			if (!ServiceCompat.this.checkBinderCount())
				ServiceCompat.this.stopSelfResult(ServiceCompat
						.getStartID(ServiceCompat.this));
		}
	};

	private void tryCallMethod(Method method, Object[] params) {
		try {
			method.invoke(this, params);
		} catch (InvocationTargetException localInvocationTargetException) {
			localInvocationTargetException.printStackTrace();
			return;
		} catch (IllegalAccessException localIllegalAccessException) {
			localIllegalAccessException.printStackTrace();
		}
	}

	private void clearAllMsg() {
		mHandler.removeCallbacksAndMessages(null);
	}

	protected final int getStartID() {
		return mStartId;
	}

	static protected final int getStartID(ServiceCompat compat) {
		return compat.mStartId;
	}

	public void TryStopForeground(int id) {
		if (mMethod_stopForeground != null) {
			tryCallMethod(this.mMethod_stopForeground,
					new Object[] { Boolean.TRUE });
		} else {
			mNM.cancel(id);
			tryCallMethod(this.mMethod_setForeground,
					new Object[] { Boolean.FALSE });
		}
	}

	public void TryStartForeground(int id, Notification notification) {
		if (mMethod_startForeground != null) {
			tryCallMethod(mMethod_startForeground, new Object[] { id,
					notification });
		} else {
			tryCallMethod(mMethod_setForeground, new Object[] { Boolean.TRUE });
			mNM.notify(id, notification);
		}
	}

	protected abstract void onStart(Intent paramIntent);

	protected final void checkBinderAutoStopDelayed() {
		clearAllMsg();
		// mHandler.sendMessageDelayed(mHandler.obtainMessage(), 60000L);
	}

	protected boolean checkBinderCount() {
		return this.mBinderCount > 0;
	}

	public IBinder onBind(Intent intent) {
		mBinderCount++;
		return null;
	}

	public void onCreate() {
		super.onCreate();

		mNM = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
		try {
			mMethod_startForeground = getClass().getMethod("startForeground",
					clazz_startForeground);
			mMethod_stopForeground = getClass().getMethod("stopForeground",
					clazz_stopForeground);
			checkBinderAutoStopDelayed();
			return;
		} catch (NoSuchMethodException e) {
		}

		mMethod_startForeground = null;
		mMethod_stopForeground = null;
		try {
			mMethod_setForeground = getClass().getMethod("setForeground",
					clazz_setForeground);
			checkBinderAutoStopDelayed();
			return;
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	public void onDestroy() {
		super.onDestroy();
		clearAllMsg();
	}

	public void onRebind(Intent paramIntent) {
		mBinderCount++;
		super.onRebind(paramIntent);
	}

	public void onStart(Intent intent, int startId) {
		mStartId = startId;
		onStart(intent);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		mStartId = startId;
		onStart(intent);
		return 2;
	}

	public boolean onUnbind(Intent paramIntent) {
		if (--mBinderCount <= 0)
			checkBinderAutoStopDelayed();
		return true;
	}
}