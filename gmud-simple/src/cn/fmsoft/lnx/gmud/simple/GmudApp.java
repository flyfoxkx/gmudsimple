package cn.fmsoft.lnx.gmud.simple;

import android.app.Application;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

public class GmudApp extends Application {

	private GuardServer mServer;
	private GmudActivity mActivity;

	@Override
	public void onCreate() {
		// VMRuntime.getRuntime().setMinimumHeapSize(4 * 1024 * 1024);
		super.onCreate();

		Gmud.SetCallback(new Gmud.ICallback() {

			@Override
			public void UpdateTime(long minutes, int seconds) {
				if (mActivity != null) {
					mActivity.UpdateTime(minutes, seconds);
				}
				if (mServer != null) {
					mServer.UpdateTime(minutes, seconds);
				}
			}

			@Override
			public void EnterNewName(int type) {
				if (mActivity != null)
					mActivity.EnterNewName(type);
			}
		});

		new Gmud(getBaseContext());
	}

	/**
	 * There's no guarantee that this function is ever called.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	protected void BindGmudActivity(GmudActivity activity) {
		mActivity = activity;
	}

	protected void UnbindGmudActivity(GmudActivity activity) {
		mActivity = null;
	}

	protected void BindGuardServer(GuardServer server) {
		mServer = server;
	}

	protected void UnbindGuardServer(GuardServer server) {
		mServer = null;
	}
}
