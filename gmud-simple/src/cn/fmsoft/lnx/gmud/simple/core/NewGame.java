package cn.fmsoft.lnx.gmud.simple.core;

import android.graphics.Bitmap;

public class NewGame {
	private static final String Title = "　　 *英雄坛说*";
	private static final String[] PROLOGUE = new String[] { "　　在这件事没发生之",
			"前，我一直在平静地生", "活着；如果这件事没有", "发生，那么我依然会这", "样平静甚至有点单调地",
			"继续生活下去。可是最", "终它还是发生了，就是", "现在，在我按下按钮的", "时候，我启动了远古大",
			"陆英雄坛的时空转换装", "置。当我从时空扭曲的", "强大力场中恢复知觉的", "时候，我已经来到了这",
			"片传说中神秘的土地。", "这里无法考证年代，我", "所来到的地方好像是中", "原偏西的位置，一个不",
			"大不小的小镇，镇上行", "人混杂，还算热闹。从", "他们的交谈中我得知这", "里叫“平安小镇”。而",
			"当我注视自己的时候才", "发现，我变成了一个十", "四岁的少年！这里有什", "么秘密？这是造就英雄",
			"的融炉或是邪恶的渊源", "？我不知道，我只知道", "，从今以后，我的生活", "将完全改变。。。　　",
			"By gmud小组", "", "" };

	private static final String Attrib_names[] = new String[] { "膂力", "敏捷",
			"根骨", "悟性" };

	void ShowStory() {
		final int max_line = PROLOGUE.length;
		int y = 5 * 16;
		int top_line = 0;
		int last_key = 0;
		while (Input.Running && last_key == 0) {
			Input.ProcessMsg();
			if (--y == 0) {
				y = 16;
				top_line++;
				if (top_line + 4 >= max_line)
					break;
			}
			Video.VideoClearRect(0, 16, 160, 80);
			Video.VideoDrawStringSingleLine(PROLOGUE[top_line], 1, y, 1);
			Video.VideoDrawStringSingleLine(PROLOGUE[top_line + 1], 1,
					y + 1 * 16, 1);
			Video.VideoDrawStringSingleLine(PROLOGUE[top_line + 2], 1,
					y + 2 * 16, 1);
			Video.VideoDrawStringSingleLine(PROLOGUE[top_line + 3], 1,
					y + 3 * 16, 1);
			Video.VideoDrawStringSingleLine(PROLOGUE[top_line + 4], 1,
					y + 4 * 16, 1);
			Video.VideoClearRect(0, 0, 160, 16);
			Video.VideoDrawStringSingleLine(Title, 0, 0, 1);
			Video.VideoUpdate();
			last_key = Gmud.GmudWaitKey(Input.kKeyAny, 100);
		}
		Video.VideoClear();
		Video.VideoUpdate();
	}

	int SelectChar() {
		final Bitmap charactor[] = new Bitmap[3];
		charactor[0] = Res.loadimage(75);
		charactor[1] = Res.loadimage(81);
		charactor[2] = Res.loadimage(87);
		int id = 0;
		boolean update = true;
		int last_key = 0;
		while (Input.Running) {
			Input.ProcessMsg();
			if ((last_key & Input.kKeyLeft) != 0) {
				if (id > 0)
					id--;
				update = true;
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (id < 2)
					id++;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				break;
			}
			if (update) {
				update = false;
				DrawChar(charactor, id);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyEnt | Input.kKeyExit);
		}
		charactor[0].recycle();
		charactor[2].recycle();
		charactor[1].recycle();
		return id;
	}

	private void DrawChar(Bitmap[] charactor, int id) {
		Video.VideoClear();
		Video.VideoDrawRectangle(1, 1, 158, 78);
		Video.VideoDrawRectangle(2, 2, 156, 76);
		Video.VideoDrawRectangle(4, 4, 152, 72);
		Video.VideoDrawStringSingleLine("请选择您的人物:", 16, 6, 1);
		Video.VideoDrawImage(charactor[0], 20, 28);
		Video.VideoDrawImage(charactor[1], 56, 28);
		Video.VideoDrawImage(charactor[2], 92, 28);

		final int x = 16 + id * 36;
		Video.VideoDrawRectangle(x, 24, 25, 24);
		Video.VideoDrawRectangle(x + 1, 25, 23, 22);
	}

	private void DrawAlloc(int[] points, int remaining, int id) {
		Video.VideoClear();
		Video.VideoDrawRectangle(1, 1, 159, 79);
		Video.VideoDrawRectangle(3, 3, 155, 75);
		Video.VideoDrawStringSingleLine("→", 10, 10 + id * 16);
		if (remaining > 0)
			Video.VideoDrawStringSingleLine(String.valueOf(remaining), 5, 4, 3);
		for (int i = 0; i < 4; i++) {
			String linestr = Attrib_names[i] + ":  < " + points[i] + " >";
			Video.VideoDrawStringSingleLine(linestr, 24, 10 + i * 16);
		}
	}

	void AllocPoint(Player pl) {
		final int points[] = new int[4];
		points[0] = points[1] = points[2] = points[3] = 20;

		boolean update = true;
		int Remaining = 0;
		int id = 0;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyEnt) != 0) {
				if (Remaining == 0) {
					pl.pre_force = points[0];
					pl.pre_agility = points[1];
					pl.pre_aptitude = points[2];
					pl.pre_savvy = points[3];
					break;
				}
			} else if ((last_key & Input.kKeyUp) != 0) {
				id = id <= 0 ? 3 : (id - 1);
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				id = id < 3 ? (id + 1) : 0;
				update = true;
			} else if ((last_key & Input.kKeyLeft) != 0) {
				if (points[id] > 10) {
					points[id]--;
					Remaining++;
					update = true;
				}
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (Remaining > 0 && points[id] < 30) {
					Remaining--;
					points[id]++;
					update = true;
				}
			}
			if (update) {
				update = false;
				DrawAlloc(points, Remaining, id);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyLeft | Input.kKeyUp
					| Input.kKeyRight | Input.kKeyDown | Input.kKeyEnt
					| Input.kKeyExit);
		}
	}
}
