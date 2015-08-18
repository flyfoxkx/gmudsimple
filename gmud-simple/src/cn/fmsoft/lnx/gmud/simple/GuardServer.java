package cn.fmsoft.lnx.gmud.simple;

import org.coolnx.lib.ServiceCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;
import cn.fmsoft.lnx.gmud.simple.core.Player;

/**
 * 守护服务
 * 
 * @author nxliao
 * 
 */
public class GuardServer extends ServiceCompat {
	protected static final int NOTIFY_ID = 0x1987;

	private static final int _MSG_USER_ = 0x1000;
	private static final int MSG_UPDATE_PLAY_TIME = _MSG_USER_ + 1;
	
	private PendingIntent mAlarmPendingIntent;

	private final class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_PLAY_TIME:
				show_time();
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	private Handler mHandler = new MyHandler();

	private Notification mNotification;

	private long mMinutes;
	private int mSeconds;

	private PendingIntent mPendingIntent;

	public void UpdateTime(long minutes, int seconds) {
		mMinutes = minutes;
		mSeconds = seconds;
		mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);
		mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
	}

	protected void show_time() {
		Notification notification = getNotification();
		CharSequence title = Gmud.GetPlayerName();
		CharSequence summary = null;
		if (title == null)
			title = getString(R.string.app_name);
		// String sex = Gmud.GetPlayerSex() == 0 ? "男" : "女";
		else if (mMinutes > 0 || mSeconds > 0) {
			summary = String.format("（%s）  %.2f'%4d:%d", Gmud.GetPlayerClass(),
					14 + (float) mMinutes / Player.AGE_TIME, mMinutes
							% Player.AGE_TIME, mSeconds);
		}
		notification.setLatestEventInfo(getBaseContext(), title, summary,
				mPendingIntent);
		TryStartForeground(NOTIFY_ID, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
		return null;
	}

	@Override
	protected void onStart(Intent paramIntent) {
		if (!Gmud.PLAYING) {
			stopSelf();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mMinutes = 0;
		mSeconds = 0;

		Intent notificationIntent = new Intent(this, GmudActivity.class);
		notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
		PendingIntent contentItent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		mPendingIntent = contentItent;

		((GmudApp) getApplication()).BindGuardServer(this);

		TryStartForeground(NOTIFY_ID, getNotification());
		
		mAlarmPendingIntent = PendingIntent.getService(this, 0, new Intent(
				this, GuardServer.class), 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), 1000 * 20, mAlarmPendingIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		((GmudApp) getApplication()).UnbindGuardServer(this);
		TryStopForeground(NOTIFY_ID);
		// mHandler.postDelayed(new Runnable() {
		// public final void run() {
		// Process.killProcess(Process.myPid());
		// }
		// }, 500L);

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(mAlarmPendingIntent);
	}

	private Notification getNotification() {
		if (mNotification == null) {
			mNotification = _createNotification();
		}
		return mNotification;
	}

	private Notification _createNotification() {
		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.tmp_icon,
				getString(R.string.app_name), System.currentTimeMillis());
		// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// 表明在点击了通知栏中的"清除通知"后，此通知自动清除。
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.FLAG_INSISTENT;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000; // //闪光时间，毫秒

		CharSequence contentTitle = "后台挂机服务"; // 通知栏标题

		CharSequence contentText = "游戏正在运行……";// 通知栏内容

		notification.setLatestEventInfo(this, contentTitle, contentText,
				mPendingIntent);
		return notification;
	}
}
