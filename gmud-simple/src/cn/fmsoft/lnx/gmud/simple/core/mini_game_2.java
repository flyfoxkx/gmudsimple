package cn.fmsoft.lnx.gmud.simple.core;

import android.graphics.Bitmap;

/**
 * 小游戏——跳舞毯
 * <ol>
 * <b>规则</b>
 * <li>固定单位时间片</li>
 * <li>单位时间内，随机产生一个方向</li>
 * <li>单位时间内，仅响应一次方向键</li>
 * <li>单位时间过后，重置方向状态</li>
 * </ol>
 * 
 * @author nxliao
 * 
 */
public class mini_game_2 {
	private static int score = 0;
	private static int dir = 0;
	private static int char_dir = -1;

	/* 各方向 */
	private static final int DIR_LEFT = 0;
	private static final int DIR_UP = 1;
	private static final int DIR_DOWN = 2;
	private static final int DIR_RIGHT = 3;
	private static final int DIR_CENTER = 4;

	/* 人物图资源 */
	private static final int CHARACTER_IMG_BASE = 74;

	/* 时间片控制 */
	private static final int DELAY_TICK = 80;
	private static final int TIME_ROUND = 4;

	// 人物方向图
	private static final int DIR_CHAR[] = new int[] { 3, 1, 0, 5, 1 };

	// 方向图
	private static Bitmap sDirImg[] = new Bitmap[5];

	// 角色图
	private static Bitmap sCharImg[] = new Bitmap[5];

	private static void clean() {
		for (int i = 0; i <= DIR_CENTER; i++) {
			if (sDirImg[i] != null) {
				sDirImg[i].recycle();
				sDirImg[i] = null;
			}
			if (sCharImg[i] != null) {
				sCharImg[i].recycle();
				sCharImg[i] = null;
			}
		}
	}

	private static void loadResource(Player pl) {
		sDirImg[DIR_LEFT] = Res.loadimage(252);
		sDirImg[DIR_UP] = Res.loadimage(253);
		sDirImg[DIR_DOWN] = Res.loadimage(251);
		sDirImg[DIR_RIGHT] = Res.loadimage(254);
		sDirImg[DIR_CENTER] = Res.loadimage(250);

		final int id = pl.image_id * 6 + CHARACTER_IMG_BASE;
		for (int i = 0; i < DIR_CENTER; i++) {
			sCharImg[i] = Res.loadimage(id + DIR_CHAR[i]);
		}
		sCharImg[DIR_CENTER] = sCharImg[DIR_UP];
	}

	private static void init() {
		score = 0;
		dir = char_dir = DIR_CENTER;
		loadResource(Gmud.sPlayer);
	}

	private static void draw() {
		final int sx = 77;
		final int sy = 24;
		final int dy = 6;
		final int cx = 89;
		final int cy = 69;
		Video.VideoClear();

		// 画框和线条
		Video.VideoDrawRectangle(1, 1, Gmud.WQX_ORG_WIDTH - 1,
				Gmud.WQX_ORG_HEIGHT - 1);
		Video.VideoDrawLine(sx, sy, Gmud.WQX_ORG_WIDTH - 1, sy);
		Video.VideoDrawLine(sx, 1, sx, Gmud.WQX_ORG_HEIGHT - 1);
		Video.VideoDrawLine(cx, cy, cx + 61, cy);
		Video.VideoDrawString(String.format("score %05d", score), UI.TITLE_X,
				UI.SYSTEM_MENU_Y);

		int x, y;

		// 绘制人物
		x = cx + (char_dir == DIR_RIGHT ? 38 : (char_dir == DIR_LEFT ? 8 : 23));
		y = cy - (char_dir == DIR_UP ? 32 : 16);
		Video.VideoDrawImage(sCharImg[char_dir], x, y);

		// 绘制自动的方向// up:25
		x = sx + 8 + 1 + dir * 16;
		y = dy;
		Video.VideoDrawImage(sDirImg[dir], x, y);

		// 方向图
		if (char_dir != DIR_CENTER) {
			x = 16 + (char_dir == DIR_RIGHT ? 31 : (char_dir == DIR_LEFT ? 1
					: 16));
			y = 24 + (char_dir == DIR_DOWN ? 31 : (char_dir == DIR_UP ? 1 : 16));
			Video.VideoDrawImage(sDirImg[char_dir], x, y);
			if (char_dir == DIR_RIGHT)
				x++;
			else if (char_dir == DIR_LEFT)
				x--;
			else if (char_dir == DIR_UP)
				y--;
			else if (char_dir == DIR_DOWN)
				y++;
			Video.VideoDrawImage(sDirImg[char_dir], x, y);
		}
		Video.VideoDrawImage(sDirImg[DIR_CENTER], 16, 24);
	}

	static int GameMain() {
		init();
		dir = util.RandomInt(4);

		final int KEY_FLAG = Input.kKeyLeft | Input.kKeyRight | Input.kKeyUp
				| Input.kKeyDown | Input.kKeyExit;

		int round = TIME_ROUND;
		int last_key = 0;
		boolean update = true;
		boolean awarded = false;
		while (Input.Running) {
			if (!awarded) {
				if ((last_key & Input.kKeyExit) != 0) {
					break;
				} else if ((last_key & Input.kKeyLeft) != 0) {
					char_dir = DIR_LEFT;
				} else if ((last_key & Input.kKeyUp) != 0) {
					char_dir = DIR_UP;
				} else if ((last_key & Input.kKeyDown) != 0) {
					char_dir = DIR_DOWN;
				} else if ((last_key & Input.kKeyRight) != 0) {
					char_dir = DIR_RIGHT;
				}
				if (char_dir != DIR_CENTER) {
					update = true;
					awarded = true;
					if (dir == char_dir) {
						score += 3;
					}
				}
			}
			if (update) {
				update = false;
				draw();
				Video.VideoUpdate();
			}

			Gmud.GmudDelay(DELAY_TICK);

			if (!awarded) {
				last_key = Input.inputstatus & KEY_FLAG;
			}

			if (round-- == 0) {
				round = TIME_ROUND;
				dir = util.RandomInt(DIR_CENTER);
				char_dir = DIR_CENTER;
				last_key = 0;
				Input.ClearKeyStatus();
				awarded = false;
				update = true;
			}
		}
		clean();
		return score;
	}
}
