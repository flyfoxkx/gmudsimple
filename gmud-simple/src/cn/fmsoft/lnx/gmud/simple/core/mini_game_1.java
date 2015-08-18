package cn.fmsoft.lnx.gmud.simple.core;

import android.graphics.Bitmap;

public class mini_game_1 {

	private static final int BALL_R = 3;

	// 进度条的位置
	private static final int PROCESS_LEFT = 9;
	private static final int PROCESS_TOP = 14;
	private static final int PROCESS_WIDTH = 42;
	private static final int PROCESS_HEIGHT = 8;
	private static final int PROCESS_C_LEFT = (PROCESS_WIDTH - 6) / 2;
	private static final int PROCESS_C_RIGHT = PROCESS_C_LEFT + 6 + 1;

	private static final int PROCESS_MIN = BALL_R + 1;
	private static final int PROCESS_MAX = PROCESS_WIDTH - BALL_R - 1;

	// 地平线
	private static final int GROUND_X = 19;
	private static final int GROUND_Y = 74;

	private static final int CHAR_X = GROUND_X + 28;
	private static final int CHAR_Y = GROUND_Y - 16;

	private static int sPx = 0, sTx = 0, sTy = 0;
	private static int score = 0;

	// 进度控制计算
	private static final int SPEED_RATE = 1024;
	private static final int sThrowStep = SPEED_RATE * 2;
	private static final int THROW_X_MAX = 66 * SPEED_RATE;
	private static final double THROW_Y_ANGLE_BASE = Math.PI * 3 / 4;
	private static final double THROW_Y_ANGLE = THROW_X_MAX
			/ THROW_Y_ANGLE_BASE;
	private static final int sThrowYMax = (int) (-40 / (Math
			.sin(THROW_Y_ANGLE_BASE)));

	private static int sSSpeed, sSStep, sSPos;
	private static boolean sDirLeft = true;
	private static int sThrowPos;

	private static Bitmap sBD;
	private static Bitmap sCharImg[] = new Bitmap[2];
	private static int sCharAction = 0;

	private static void init(Player pl) {
		final int cImgId = 74 + pl.image_id * 6;
		sCharImg[0] = Res.loadimage(cImgId + 4);
		sCharImg[1] = Res.loadimage(cImgId + 5);
		sBD = Res.loadimage(249);
	}

	private static void clean() {
		if (sCharImg[0] != null) {
			sCharImg[0].recycle();
			sCharImg[0] = null;
		}
		if (sCharImg[1] != null) {
			sCharImg[1].recycle();
			sCharImg[1] = null;
		}
		if (sBD != null) {
			sBD.recycle();
			sBD = null;
		}
	}

	private static void draw_ball(int x, int y) {
		Video.VideoDrawArc(x, y, BALL_R);
		Video.VideoFillArc(x, y, BALL_R - 2);
	}

	private static void draw_scroll_ball() {
		Video.VideoDrawRectangle(PROCESS_LEFT, PROCESS_TOP, PROCESS_WIDTH,
				PROCESS_HEIGHT);
		draw_ball(PROCESS_LEFT + 1 + sPx, PROCESS_TOP + 1 + BALL_R);
	}

	private static void draw_process() {
		Video.VideoClearRect(PROCESS_LEFT, PROCESS_TOP, PROCESS_WIDTH,
				PROCESS_HEIGHT);
		draw_scroll_ball();
	}

	private static void draw() {
		Video.VideoClear();

		// 绘制线框
		Video.VideoDrawRectangle(0, 0, Gmud.WQX_ORG_WIDTH - 1,
				Gmud.WQX_ORG_HEIGHT - 1);

		draw_scroll_ball();

		Video.VideoDrawLine(PROCESS_LEFT + PROCESS_C_LEFT, PROCESS_TOP
				+ PROCESS_HEIGHT, PROCESS_LEFT + PROCESS_C_LEFT, PROCESS_TOP
				+ PROCESS_HEIGHT + 2);
		Video.VideoDrawLine(PROCESS_LEFT + PROCESS_C_RIGHT, PROCESS_TOP
				+ PROCESS_HEIGHT, PROCESS_LEFT + PROCESS_C_RIGHT, PROCESS_TOP
				+ PROCESS_HEIGHT + 2);
		Video.VideoDrawLine(GROUND_X, GROUND_Y, Gmud.WQX_ORG_WIDTH - 4,
				GROUND_Y);
		Video.VideoDrawString(String.format("SCORE　%05d", score), UI.TITLE_X, 0);
		draw_ball(CHAR_X + 14 + BALL_R + sTx, GROUND_Y - BALL_R + sTy);
		Video.VideoDrawImage(sCharImg[sCharAction], CHAR_X, CHAR_Y);
		Video.VideoDrawImage(sBD, GROUND_X + 108, GROUND_Y - 55);
	}

	/** 更新小球的滚动位置 */
	private static void _resetScrollSpeed() {
		sSSpeed = 2 * SPEED_RATE + util.RandomInt(6 * SPEED_RATE);
		sSStep = 2 * SPEED_RATE - util.RandomInt(4 * SPEED_RATE);
	}

	private static void resetScrollPos() {
		sDirLeft = true;
		sPx = (PROCESS_MAX + PROCESS_MIN) / 2;
		sSPos = sPx * SPEED_RATE;
		_resetScrollSpeed();
	}

	private static void updateScrollPos() {
		if (sSSpeed > sSStep)
			sSSpeed -= sSStep;
		else
			sSSpeed = SPEED_RATE;
		if (sDirLeft)
			sSPos -= sSSpeed;
		else
			sSPos += sSSpeed;
		int pos = sSPos / SPEED_RATE;
		if (pos < PROCESS_MIN) {
			pos = PROCESS_MIN;
		} else if (pos > PROCESS_MAX) {
			pos = PROCESS_MAX;
		}
		if ((sDirLeft && pos <= PROCESS_MIN)
				|| (!sDirLeft && pos >= PROCESS_MAX)) {
			sDirLeft = !sDirLeft;
			_resetScrollSpeed();
		}
		if (pos != sPx) {
			sPx = pos;
			draw_process();
			Video.VideoUpdate();
		}
	}

	private static boolean checkScrollPos() {
		return (sPx > PROCESS_C_LEFT && sPx < PROCESS_C_RIGHT);
	}

	private static void resetThrowPos() {
		sTx = 0;
		sTy = 0;
		sThrowPos = 0;
	}

	private static boolean updateThrowPos() {
		boolean ret = true;
		sThrowPos += sThrowStep;
		// 66,-37
		if (sThrowPos < THROW_X_MAX) {
			sTx = sThrowPos / SPEED_RATE;
			sTy = (int) (Math.sin(sThrowPos / THROW_Y_ANGLE) * sThrowYMax);
		} else {
			sTx = 66;
			sTy = -37 + (sThrowPos - THROW_X_MAX) * 2 / SPEED_RATE;
			if (sTy >= 0) {
				ret = false;
				sTy = 0;
			}
		}
		draw();
		Video.VideoUpdate();
		return ret;
	}

	static int GameMain() {
		init(Gmud.sPlayer);
		resetScrollPos();
		resetThrowPos();

		int runMode = 0;
		score = 0;

		boolean update = true;
		int last_key = 0;
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		while (Input.Running) {
			if (runMode == 1) {
				updateScrollPos();
			} else if (runMode == 2) {
				if (!updateThrowPos()) {
					resetThrowPos();
					update = true;
					runMode = 0;
					sCharAction = 0;
				}
			}
			if (update) {
				update = false;
				draw();
				Video.VideoUpdate();
			}

			if ((last_key & Input.kKeyExit) != 0)
				break;
			if ((runMode == 0 || runMode == 1)
					&& (last_key & Input.kKeyEnt) != 0) {
				if (runMode == 0) {
					runMode = 1;
				} else if (runMode == 1) {
					if (checkScrollPos()) {
						score += 20;
						runMode = 2;
						sCharAction = 1;
						resetThrowPos();
					} else {
						runMode = 0;
					}
					resetScrollPos();
				} else if (runMode == 2) {
				}
				Input.ClearKeyStatus();
			}

			Gmud.GmudDelay(50);

			Input.ProcessMsg();
			last_key = Input.inputstatus;
		}
		clean();
		return score;
	}
}
