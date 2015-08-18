package cn.fmsoft.lnx.gmud.simple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Rect;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

/**
 * 布局配置<br/>
 * <b>NOTE: 仅提供给 {@link Show} 和 {@link Configure} 使用</b>
 * 
 * @author nxliao(xtulnx@126.com)
 */
class ConfigInfo {
	/** 默认：背景色 */
	public final static int DEF_BACKGROUND_COLOR = 0xffb6c2c2;

	/** 横屏时左右留白，避免游戏区占满屏导致按钮不可见 */
	final static int MARGIN_H_LAND = 80;
	final static int MARGIN_V_LAND = 80;
	final static int MARGIN_H_PORT = 0;
	final static int MARGIN_V_PORT = 160;

	final static int DEF_BT_WIDTH = 96;
	final static int DEF_BT_HEIGHT = 32;
	final static int DEF_BT_COLUMN_PORT = 3;
	final static int DEF_BT_ROW_PORT = 4;
	final static int DEF_BT_COLUMN_LAND = 2;
	final static int DEF_BT_ROW_LAND = 5;
	final static int DEF_BT_TITLE_SIZE = 10;

	final static int KEY_INDEX_MAX = Configure._KEY_INDEX_MAX;

	/** 默认：竖屏：软键配置 */
	private final static int[][] DEF_SOFTKEY_PORT = new int[][] { { 0, 0 },
			{ 2, 0 }, { 1, 3 }, { 0, 3 }, { 1, 2 }, { 2, 3 }, { 0, 1 },
			{ 2, 1 }, { 1, 1 } };
	/** 默认：横屏：软键配置 */
	private final static int[][] DEF_SOFTKEY_LAND = new int[][] { { 0, 0 },
			{ 1, 0 }, { 1, 4 }, { 1, 2 }, { 1, 1 }, { 1, 3 }, { 0, 2 },
			{ 0, 3 }, { 0, 1 } };

	/** 各按键区 */
	protected final Rect mRcKeys[] = new Rect[KEY_INDEX_MAX];
	/** 该配置应用的宽高 */
	protected int mWidth, mHeight;
	/** 背景颜色 */
	protected int mBackgroundColor;

	/** 检查是否适配当前的宽高 */
	protected boolean match(int w, int h) {
		return mWidth == w && mHeight == h;
	}

	/** 保持比例按整数倍数拉伸铺满 */
	private static int _get_video_fill_scale(int w, int h) {
		final float scale;
		float scale_w, scale_h;
		scale_w = 1.0f * w / Gmud.WQX_ORG_WIDTH;
		scale_h = 1.0f * h / Gmud.WQX_ORG_HEIGHT;
		if (scale_w < scale_h) {
			scale = scale_w;
		} else {
			scale = scale_h;
		}
		int s = (int) scale;
		return s;
	}

	/**
	 * 构造默认配置（横屏）
	 * 
	 * @param w
	 * @param h
	 */
	protected static final ConfigInfo generateDefaultLand(int w, int h) {
		final ConfigInfo info = new ConfigInfo(w, h);

		// 计算最小整数倍
		final int scale = _get_video_fill_scale(w - MARGIN_H_LAND * 2, h
				- MARGIN_V_LAND);
		final int vw = Gmud.WQX_ORG_WIDTH * scale;
		final int vh = Gmud.WQX_ORG_HEIGHT * scale;

		final int bw = (int) (Configure.CUR_DENSITY * DEF_BT_WIDTH);
		final int bh = (int) (Configure.CUR_DENSITY * DEF_BT_HEIGHT);

		int bph = (w - vw - 8 * 2) / DEF_BT_COLUMN_LAND - bw;
		if (bph > 0) {
			bph = 0;
		}
		final int bpv = (h - bh * DEF_BT_ROW_LAND) / (DEF_BT_ROW_LAND + 1);

		final int top = 0 + bpv;
		final Rect rcKeys[] = info.mRcKeys;
		final int softkey[][] = DEF_SOFTKEY_LAND;
		for (int i = 0, c = Configure._KEY_MAX_; i < c; i++) {
			final int x = softkey[i][0] == 0 ? bph : (w - bw - bph);
			final int y = top + (bh + bpv) * softkey[i][1];
			rcKeys[i] = new Rect(x, y, x + bw, y + bh);
		}
		rcKeys[Configure._KEY_INDEX_VIDEO] = new Rect((w - vw) / 2,
				(h - vh) / 2, (vw + w) / 2, (h + vh) / 2);
		rcKeys[Configure._KEY_INDEX_BG] = new Rect(0, 0, bw, bh);
		rcKeys[Configure._KEY_INDEX_TITLE] = new Rect(0, 0, bw, bh);

		return info;
	}

	/**
	 * 构造默认配置（竖屏）
	 * 
	 * @param w
	 * @param h
	 */
	protected static final ConfigInfo generateDefaultPort(int w, int h) {
		final ConfigInfo info = new ConfigInfo(w, h);
		// 计算最小整数倍
		final int scale = _get_video_fill_scale(w - MARGIN_H_PORT * 2, h
				- MARGIN_V_PORT);
		final int vw = Gmud.WQX_ORG_WIDTH * scale;
		final int vh = Gmud.WQX_ORG_HEIGHT * scale;

		final int bw = (int) (Configure.CUR_DENSITY * DEF_BT_WIDTH);
		final int bh = (int) (Configure.CUR_DENSITY * DEF_BT_HEIGHT);

		final int bph = (w - bw * DEF_BT_COLUMN_PORT)
				/ (DEF_BT_COLUMN_PORT + 1);
		final int bpv = (h - vh - bh * DEF_BT_ROW_PORT) / (DEF_BT_ROW_PORT + 1);

		final int top = vh + bpv;
		final Rect rcKeys[] = info.mRcKeys;
		final int softkey[][] = DEF_SOFTKEY_PORT;
		for (int i = 0, c = Configure._KEY_MAX_; i < c; i++) {
			final int x = bph + (bw + bph) * softkey[i][0];
			final int y = top + (bh + bpv) * softkey[i][1];
			rcKeys[i] = new Rect(x, y, x + bw, y + bh);
		}
		rcKeys[Configure._KEY_INDEX_VIDEO] = new Rect((w - vw) / 2, 0,
				(vw + w) / 2, vh);
		rcKeys[Configure._KEY_INDEX_BG] = new Rect(0, 0, bw, bh);
		rcKeys[Configure._KEY_INDEX_TITLE] = new Rect(0, 0, bw, bh);

		return info;
	}

	private ConfigInfo(int w, int h) {
		mWidth = w;
		mHeight = h;
		mBackgroundColor = DEF_BACKGROUND_COLOR;
		for (int i = 0, c = KEY_INDEX_MAX; i < c; i++) {
			mRcKeys[i] = new Rect();
		}
	}

	protected ConfigInfo(ConfigInfo info) {
		Copy(info);
	}

	protected void Copy(ConfigInfo info) {
		mWidth = info.mWidth;
		mHeight = info.mHeight;
		mBackgroundColor = info.mBackgroundColor;
		for (int i = 0, c = KEY_INDEX_MAX; i < c; i++) {
			mRcKeys[i] = new Rect(info.mRcKeys[i]);
		}
	}

	protected static void flattenToJSON(ConfigInfo info, JSONObject joRoot)
			throws JSONException {
		joRoot.put("w", info.mWidth);
		joRoot.put("h", info.mHeight);
		joRoot.put("bg", info.mBackgroundColor);

		JSONArray joKeys = new JSONArray();
		final Rect[] rects = info.mRcKeys;
		for (int i = 0, c = KEY_INDEX_MAX; i < c; i++) {
			final Rect rc = rects[i];
			JSONArray jo = new JSONArray();
			jo.put(rc.left);
			jo.put(rc.top);
			jo.put(rc.right);
			jo.put(rc.bottom);
			joKeys.put(jo);
		}
		joRoot.put("keys", joKeys);
	}

	protected static ConfigInfo unflattenFromJSON(JSONObject joRoot,
			ConfigInfo defInfo) throws JSONException {
		ConfigInfo info = new ConfigInfo(defInfo);

		info.mWidth = joRoot.getInt("w");
		info.mHeight = joRoot.getInt("h");
		info.mBackgroundColor = joRoot.getInt("bg");

		JSONArray keys = joRoot.getJSONArray("keys");
		if (keys != null && keys.length() == KEY_INDEX_MAX) {
			final Rect[] rects = info.mRcKeys;
			for (int i = 0, c = KEY_INDEX_MAX; i < c; i++) {
				JSONArray jo = keys.getJSONArray(i);
				rects[i].set(jo.getInt(0), jo.getInt(1), jo.getInt(2),
						jo.getInt(3));
			}
		}
		return info;
	}
}
