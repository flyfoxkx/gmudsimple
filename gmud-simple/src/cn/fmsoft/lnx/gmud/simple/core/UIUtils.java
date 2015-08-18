package cn.fmsoft.lnx.gmud.simple.core;

/**
 * UI方面通用模块，如列表菜单
 * 
 * @author nxliao
 * 
 */
class UIUtils {

	/** 指示器：向右的小箭头 见 {@link UI#DrawCursor(int, int)} */
	static int MENU_TYPE_CURSOR = 0;
	/** 指示器：小方块 */
	static int MENU_TYPE_BOX = 1;
	/** 指示器：圆圈 */
	static int MENU_TYPE_CIRCLE = 2;

	private static final int menu_indicator_size[] = new int[] { UI.CURSOR_W,
			UI.CURSOR_H, UI.CURSOR_BOX_W, UI.CURSOR_BOX_W, UI.CURSOR_CIRCLE_R,
			UI.CURSOR_CIRCLE_R };

	/**
	 * 竖向菜单
	 * 
	 * @param title
	 *            标题
	 * @param max
	 *            总项数
	 * @param vCount
	 *            可见项数
	 * @param callbackID
	 *            当按 Back 键回调的ID
	 * @return 0 或 回调函数的返回值
	 */
	static int ShowMenu(int type, int pad_left, String title[], int count,
			int vCount, int x, int y, final int w, final int callbackID) {
		final int id_w = menu_indicator_size[type + type];
		final int id_h = menu_indicator_size[type + type + 1];
		final int lineH = Video.SMALL_LINE_H;
		final int oy = (lineH - id_h) / 2;
		final int ox = (pad_left + 1 - id_w) / 2;
		final int h = lineH * vCount + 2;
		int top = 0, sel = 0;
		boolean update = true;
		int ret = 0;
		if (y == -1) {
			y = (Gmud.WQX_ORG_HEIGHT - h) >> 1;
		}
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0) {
					sel--;
				} else if (top > 0) {
					top--;
				} else if (count < vCount) {
					top = 0;
					sel = count - 1;
				} else {
					top = count - vCount;
					sel = vCount - 1;
				}
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (top + sel >= count - 1) {
					top = sel = 0;
				} else if (sel < vCount - 1) {
					sel++;
				} else {
					top++;
				}
				update = true;
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				ret = UI.onMenuCallBack(callbackID, top + sel);
				if (ret != 0)
					break;
				update = true;
			}

			if (update) {
				Video.VideoClearRect(x, y, w, h);
				Video.VideoDrawRectangle(x, y, w, h);
				for (int i = 0, ty = y, pos = top; i < vCount && pos < count; i++, pos++, ty += lineH) {
					Video.VideoDrawStringSingleLine(title[pos], x + pad_left,
							ty);
					if (type == MENU_TYPE_CURSOR) {
						if (i == sel) {
							UI.DrawCursor(x + ox, ty + oy);
						}
					} else if (type == MENU_TYPE_BOX) {
						Video.VideoDrawRectangle(x + ox, ty + oy, id_w, id_h);
						if (sel == i) {
							Video.VideoFillRectangle(x + ox, ty + oy, id_w,
									id_h);
						}
					} else if (type == MENU_TYPE_CIRCLE) {
						Video.VideoDrawArc(x + ox, ty + oy, id_w);
						if (i == sel) {
							Video.VideoFillArc(x + ox, ty + oy, id_w - 2);
						}
					}
				}
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyUp | Input.kKeyDown
					| Input.kKeyEnt | Input.kKeyExit);
		}
		return ret;
	}

	static int ShowMenu(String title[], int count, int vCount, final int x,
			final int y, final int w, final int callbackID) {
		return ShowMenu(MENU_TYPE_CURSOR, Video.SMALL_FONT_SIZE, title, count,
				vCount, x, y, w, callbackID);
	}

}
