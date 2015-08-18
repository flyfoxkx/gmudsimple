package cn.fmsoft.lnx.gmud.simple.core;

import java.util.ArrayList;

import cn.fmsoft.lnx.gmud.simple.core.GmudData.ClassID;

public class UI {
	public static final int TITLE_X = 6;
	public static final int SYSTEM_MENU_Y = 2;
	public static final int CURSOR_W = 6;
	public static final int CURSOR_H = 9;
	/** 用于选择的方框大小 */
	public static final int CURSOR_BOX_W = 6;
	/** 用于选择的圆圈大小 */
	public static final int CURSOR_CIRCLE_R = 4;

	/**
	 * 是否自动[确认]，用于学习技能
	 */
	static private int m_auto_confirm;

	// [8]
	static final String boss_map_name[] = new String[] { "青龙坛", "地罡坛", "朱雀坛",
			"山岚坛", "玄武坛", "紫煞坛", "天微坛", "白虎坛" };

	static final int boss_map_id[] = new int[] { 23, 73, 59, 79, 31, 54, 64, 44 };

	/** 主菜单文本 "查看", "物品", "技能", "功能" */
	static final String main_menu_items[] = new String[] { "查看", "物品", "技能",
			"功能" };

	// [199]
	static final int dialog_point[] = new int[] { 0, 59, 112, 165, 220, 292,
			322, 355, 394, 421, 475, 551, 580, 631, 688, 742, 799, 829, 884,
			926, 977, 1028, 1139, 1202, 1253, 1292, 1337, 1382, 1463, 1505,
			1538, 1595, 1658, 1712, 1781, 1829, 1907, 1952, 2030, 2093, 2129,
			2201, 2256, 2331, 2379, 2442, 2520, 2595, 2676, 2754, 2847, 2901,
			2949, 3003, 3063, 3135, 3180, 3267, 3336, 3429, 3471, 3536, 3605,
			3698, 3731, 3815, 3896, 3941, 4004, 4022, 4067, 4094, 4148, 4178,
			4232, 4265, 4322, 4370, 4412, 4515, 4639, 4767, 4833, 4923, 5113,
			5227, 5272, 5356, 5455, 5488, 5542, 5636, 5714, 5801, 5892, 6003,
			6093, 6129, 6171, 6237, 6294, 6396, 6456, 6515, 6557, 6671, 6773,
			6827, 6854, 6950, 6974, 7061, 7148, 7199, 7325, 7388, 7502, 7559,
			7655, 7730, 7793, 7883, 7952, 8012, 8033, 8054, 8075, 8096, 8117,
			8138, 8159, 8180, 8201, 8222, 8243, 8264, 8300, 8354, 8459, 8534,
			8657, 8687, 8756, 8819, 8951, 9029, 9107, 9167, 9245, 9323, 9383,
			9479, 9545, 9605, 9668, 9716, 9779, 9845, 9908, 9980, 10034, 10103,
			10142, 10217, 10331, 10454, 10511, 10574, 10679, 10778, 10838,
			10931, 10976, 11042, 11117, 11171, 11240, 11297, 11369, 11438,
			11504, 11603, 11651, 11771, 11822, 11885, 11948, 12011, 12074,
			12149, 12263, 12377, 12476, 12524, 12620, 12701, 12748, 12832,
			12898 };

	static final String readDialogText(int id) {
		return Res.readtext(Res.TYPE_DIALOG, dialog_point[id],
				dialog_point[1 + id]);
	}

	static void DrawDead() {
		for (int i = 112; i < 123; i++) {
			UI.ShowDialog2(i);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		}
	}

	/**
	 * 显示战斗胜利的界面
	 * 
	 * @param money
	 *            获得的金钱
	 * @param items
	 *            获得的物品ID表
	 */
	static void BattleWin(int money, short[] items) {
		StringBuilder ib = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			final int item_id = items[i];
			if (item_id == 0)
				break;
			ib.append(" ").append(Items.item_names[item_id]);
		}

		String str = String.format(Res.STR_BATTLE_WIN, money, ib.toString());
		DialogBx(str, TITLE_X, SYSTEM_MENU_Y);
		Video.VideoUpdate();
		Gmud.GmudWaitNewKey(Input.kKeyExit);
	}

	/** 在屏幕下方绘制单行文本，如果有多行，则分多次输出 */
	static void DrawMapTip(String s) {
		final int lineH = Video.LARGE_FONT_SIZE; // 行高
		final int y = Gmud.WQX_ORG_HEIGHT - lineH - 1;
		final int x = TITLE_X;
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH - x - 2);
		for (int i = 0, c = as.size(); i < c; i++) {
			Video.VideoClearRect(0, y, Gmud.WQX_ORG_WIDTH, lineH + 1);
			Video.VideoDrawRectangle(1, y, Gmud.WQX_ORG_WIDTH - 1, lineH);
			Video.VideoDrawStringSingleLine(as.get(i), TITLE_X, y + 1);
			Video.VideoUpdate();
			Gmud.GmudWaitNewKey(Input.kKeyAny);
		}
	}

	/** 加载资源，并显示不超过5行的多行文本框 */
	static void ShowDialog2(final int id) {
		final String s = readDialogText(id);
		final int lineH = Video.SMALL_LINE_H;
		Video.VideoFillRectangle(0, 0, 160, 80, 0);
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH - 16);
		for (int i = 0, c = as.size(); i < c && i < 5; i++)
			Video.VideoDrawStringSingleLine(as.get(i), 12, 10 + i * lineH, 2);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
	}

	static void ShowDialog(int tip_id) {
		DrawDialog(readDialogText(tip_id));
	}

	/** 在顶端显示最多2行的文本，如果有更多行数，则分批输出，等待任意键返回 */
	static void DrawDialog(String s) {
		final int lineH = Video.SMALL_LINE_H;
		final int height = lineH * 2 + 1; // 输出区域的高度，2行
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH
				- TITLE_X - 1);
		for (int i = 0, c = as.size(); i < c;) {
			// 清空输出区域，绘制边框
			Video.VideoClearRect(0, 0, Gmud.WQX_ORG_WIDTH, height);
			Video.VideoDrawRectangle(0, 0, Gmud.WQX_ORG_WIDTH, height);
			Video.VideoDrawStringSingleLine(as.get(i), TITLE_X, 1);

			// 如果还有文本，则再输出一行
			if (++i < c) {
				Video.VideoDrawStringSingleLine(as.get(i), TITLE_X, lineH);
				i++;
			}
			Video.VideoUpdate();
			if (i < c) {
				// 如果还有文本，就闪光标
				DrawFlashCursor(Gmud.WQX_ORG_WIDTH - 8, lineH + 2, 7);
			} else {
				// 等待任意键返回
				Gmud.GmudWaitAnyKey();
			}
		}
	}

	/**
	 * 绘制一个向下闪烁的光标，直到有掩码中指定的按键按下，则返回
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param keyFlag
	 *            按键掩码
	 * @return 按键
	 */
	static int DrawFlashCursor(int x, int y, int w, int keyFlag) {
		int key = 0;
		final int h = w / 2 + 1;
		boolean blink = false;
		Input.ClearKeyStatus();
		while (key == 0) {
			if (!blink) {
				Video.VideoDrawArrow(x, y, w, h, 0 + 2 + 0);
			} else {
				Video.VideoDrawArrow(x, y, w, h, 0 + 2 + 4);
			}
			Video.VideoUpdate();
			blink = !blink;
			key = Gmud.GmudWaitKey(keyFlag, 300);
		}
		return key;
	}

	/**
	 * 绘制一个向下闪烁的光标，直到有任意键按下，则返回
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @return
	 * @see #DrawFlashCursor(int, int, int, int)
	 */
	static int DrawFlashCursor(int x, int y, int w) {
		return DrawFlashCursor(x, y, w, Input.kKeyAny);
	}

	/**
	 * 绘制一个实心向右的箭头，大小(7x11)
	 * 
	 * @param x
	 * @param y
	 */
	static void DrawCursor(int x, int y) {
		Video.VideoDrawArrow(x, y, CURSOR_W, CURSOR_H, 1 + 2);
	}

	/** 绘制主菜单 */
	static void DrawMainMenu(int menu_id) {
		final int lineH = Video.SMALL_FONT_SIZE;
		final int x = TITLE_X;
		final int y = 0;
		final int item_width = lineH + lineH * 2; // 菜单项的宽度（含光标区域）
		final int width = item_width * 4;
		int height = lineH + 2;
		Video.VideoClearRect(x, y, width, height);
		Video.VideoDrawRectangle(x, y, width, height);
		DrawCursor(x + menu_id * item_width + (lineH - CURSOR_W) / 2, y
				+ (lineH + 1 - CURSOR_H) / 2);
		for (int i = 0, xx = x + lineH; i < 4; i++) {
			Video.VideoDrawStringSingleLine(main_menu_items[i], xx, y);
			xx += item_width;
		}
	}

	static void MainMenu() {
		int menu_id = 0;
		boolean update = true;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else
					menu_id = 3;
				update = true;
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (menu_id < 3)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				if (menu_id == 0) {
					ViewPlayer();
				} else if (menu_id == 1) {
					PlayerItem();
				} else if (menu_id == 2) {
					PlayerSkill();
				} else if (menu_id == 3) {
					if (SystemMenu() != 0) {
						update = true;
						break;
					}
				}
				Gmud.sMap.DrawMap(-1);
				update = true;
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}

			if (update) {
				update = false;
				DrawMainMenu(menu_id);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyEnt | Input.kKeyExit);
		}
	}

	static void Fly() {
		// 检查当前地图是否可飞
		int can_fly = -1;
		final int cur_map_id = Gmud.sMap.GetCurMapID();
		for (int i = 0; i < 19; i++)
			if (GmudData.flyable_map[i] == cur_map_id)
				can_fly = i;
		if (can_fly < 0)
			return;

		// 等级不够
		if (Gmud.sPlayer.GetFlySkillLevel() < 30)
			return;

		// 扣 fp 先
		if (Gmud.sPlayer.fp < 200) {
			UI.DrawDialog(Res.STR_FLY_NEED_MORE_FP);
			return;
		}
		Gmud.sPlayer.fp -= 200;

		// 隐藏未激活的地图:铸剑谷　桃花园
		boolean open_choujiangu = Gmud.sPlayer.lasting_tasks[2] != 0;
		boolean open_taohuayuan = Gmud.sPlayer.lasting_tasks[6] != 0;
		String[] title = GmudData.map_name;
		final int max_count = GmudData.map_name.length;
		int count;
		if (!open_choujiangu || !open_taohuayuan) {
			title = new String[max_count];
			System.arraycopy(GmudData.map_name, 0, title, 0, max_count - 2);
			count = max_count - 2;
			if (open_choujiangu) {
				title[count++] = GmudData.map_name[max_count - 2];
			}
			if (open_taohuayuan) {
				title[count++] = GmudData.map_name[max_count - 1];
			}
		} else {
			count = max_count;
		}

		// 让玩家选择目标地图
		UIUtils.ShowMenu(title, count, 4, 4, 4, Video.SMALL_LINE_H * 4,
				MENUID_FLY);
	}

	static int FlyCallback(int index) {
		// 隐藏未激活的地图:铸剑谷　桃花园
		if (Gmud.sPlayer.lasting_tasks[2] == 0) {
			final int max_count = GmudData.fly_dest_map.length;
			if (index == max_count - 2) {
				index = max_count - 1;
			}
		}

		int map_id = GmudData.fly_dest_map[index]; // read map
		Gmud.sMap.LoadMap(map_id);
		// Gmud.sMap.m_stack_pointer = 0;
		Gmud.sMap.SetPlayerLocation(0, 4);
		Gmud.sMap.DrawMap(0);
		Input.ClearKeyStatus();
		return 1;
	}

	static int SystemMenu() {
		final int w = Video.SMALL_LINE_H * 3 + TITLE_X;
		int x = Gmud.WQX_ORG_WIDTH - w - TITLE_X;
		int ret = UIUtils.ShowMenu(sys_menu_words, sys_menu_words.length, 5, x,
				SYSTEM_MENU_Y, w, MENUID_SYSTEM);
		if (ret == 1)
			return 1;
		return 0;
	}

	static int SystemCallback(int menu_id) {
		if (menu_id == 0) {
			FPMenu();
			return 1;
		}
		if (menu_id == 1) {
			MPMenu();
			return 1;
		}
		if (menu_id == 2) {
			PractMenu();
			return 1;
		}
		if (menu_id == 3) {
			SaveMenu();
			return 2;
		}
		if (menu_id == 4) {
			ExitMenu();
			return 2;
		}
		return 0;
	}

	static void ExitMenu() {
		final int y = Video.SMALL_FONT_SIZE + TITLE_X + SYSTEM_MENU_Y;
		Input.ClearKeyStatus();
		int last_key = DialogBx(Res.STR_EXIT_QUERY, TITLE_X, y);
		if ((last_key & Input.kKeyEnt) != 0) {
			if (task.temp_tasks_data[30] > 360) {
				DialogBx(Res.STR_EXIT_QUERY_SAVE, Video.SMALL_FONT_SIZE, y
						+ Video.SMALL_FONT_SIZE);
				last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
				if ((last_key & Input.kKeyEnt) != 0) {
					Gmud.WriteSave();
				}
			}
			Gmud.exit();
		}
	}

	static void SaveMenu() {
		if (task.temp_tasks_data[30] >= 100) {
			// save file
			String str = "存档成功!";
			if (Gmud.WriteSave())
				DrawTip(str);
			else {
				str = "存档失败!";
				DrawTip(str);
			}
		} else {
			String str = "请稍后再存";
			DrawTip(str);
		}
		Video.VideoUpdate();
		Gmud.GmudWaitNewKey(Input.kKeyExit);
	}

	/**
	 * 在指定位置显示所有文本，等待任意键按下就返回
	 * 
	 * @param s
	 * @param x
	 * @param y
	 * @return 按键值
	 */
	static int DialogBx(String s, int x, int y) {
		final int lineH = Video.SMALL_LINE_H;
		final int width = Gmud.WQX_ORG_WIDTH - 8 - x;
		final ArrayList<String> as = Video.SplitString(s, width - 4);
		final int size = as.size();
		final int height = size * lineH + 4;
		Video.VideoClearRect(x, y, width, height);
		Video.VideoDrawRectangle(x, y, width, height);
		for (int i = 0, top = 0; i < size && top < height; i++, top += lineH) {
			Video.VideoDrawStringSingleLine(as.get(i), x + 1, y + 1 + top);
		}
		Video.VideoUpdate();
		return Gmud.GmudWaitAnyKey();
	}

	static void DrawTip(String s1) {
		final int lineH = Video.SMALL_LINE_H;
		final int width = s1.length() * lineH;
		final int height = lineH + 4;
		int x = (Gmud.WQX_ORG_WIDTH - width) / 2;
		if (x < 0)
			x = 0;
		int y = (Gmud.WQX_ORG_WIDTH / 2 - height) / 2 - 10;
		if (y < 0)
			y = 0;
		Video.VideoClearRect(x, y, width, height);
		Video.VideoDrawRectangle(x, y, width, height);
		Video.VideoDrawStringSingleLine(s1, x + 2, y + 2);
	}

	/**
	 * 输出多行文本在[0,H/2]位置，并自动刷新，等待660毫秒
	 * 
	 * @param s
	 */
	static void DrawStringFromY(String s) {
		final int y = Gmud.WQX_ORG_HEIGHT / 2 + 1;
		Video.VideoClearRect(0, y, Gmud.WQX_ORG_WIDTH, Video.SMALL_LINE_H * 3);
		Video.VideoDrawString(s, 0, y);
		Video.VideoUpdate();
		Gmud.GmudDelay(660);
	}

	/**
	 * 绘制数字加减对话框，用于 {加力} 等。
	 * 
	 * @param cur
	 * @param max
	 *            最大值，[0-999]
	 * @param x
	 * @param y
	 */
	static void DrawNumberBox(int cur, int max, int x, int y) {
		final int num_w = 32; // 数字区的宽度
		final int arrow_w = 5;
		final int arrow_h = 3;
		final int nw = Video.SMALL_FONT_SIZE / 2; // 每个数字的宽度
		final int h = Video.SMALL_LINE_H + 1; // 总高度，假定小号文本高度固定为 13
		final int w = num_w + 5 + arrow_w; // 总宽度
		if (cur > max)
			cur = max;
		if (cur < 0)
			cur = 0;

		// 绘制外边框
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);

		// 分隔线
		Video.VideoDrawLine(x + num_w + 1, y, x + num_w + 1, (y + h));
		Video.VideoDrawLine(x + num_w + 1, y + h / 2, (x + w), y + h / 2);

		// 绘制上下箭头
		Video.VideoDrawArrow(x + num_w + 3, (y + h / 2) - 2, arrow_w, -arrow_h,
				cur > 0 ? 2 : 0);
		Video.VideoDrawArrow(x + num_w + 3, (y + h / 2) + 2, arrow_w, arrow_h,
				cur < max ? 2 : 0);

		// 右对齐绘制数字
		x += num_w - nw - 1;
		while (cur >= 10) {
			Video.VideoDrawStringSingleLine(String.valueOf(cur % 10), x, y);
			x -= nw + 1;
			cur /= 10;
		}
		Video.VideoDrawStringSingleLine(String.valueOf(cur), x, y);
	}

	/**
	 * 绘制进度条
	 * 
	 * @param max
	 *            总值
	 * @param cur
	 *            当前进度值
	 * @param numerator
	 *            分子数值
	 * @param denominator
	 *            分母数值
	 */
	static void DrawProgressBox(int max, int cur, int numerator, int denominator) {
		max *= 1000;
		cur *= 1000;
		final int x = 32;
		final int y = 1;
		final int h = 8;
		final int bar_w = 65;

		// 先绘制空框
		Video.VideoClearRect(0, y, Gmud.WQX_ORG_WIDTH, h);
		Video.VideoDrawRectangle(x, y, bar_w, h);

		int pro_w; // 进度值
		if (cur >= max && max > 0) {
			pro_w = bar_w;
		} else if (max <= bar_w) {
			if (max <= 0)
				max = 1;
			pro_w = (bar_w / max) * cur;
		} else {
			int j4 = max / bar_w;
			if (max % bar_w > 0)
				j4++;
			if (j4 <= 0)
				j4 = 1;
			pro_w = cur / j4;
		}
		if (pro_w > bar_w)
			pro_w = bar_w;
		Video.VideoFillRectangle(x, y, pro_w, h);

		String num = String.format("%d/%d", numerator, denominator);
		Video.VideoDrawNumberData(num, x + bar_w + 7, 3);
	}

	/**
	 * 绘制 <b>GmudTemp.temp_array_20_2</b> 从start起num 个技能列表
	 * 
	 * @param x
	 *            x坐标起点
	 * @param y
	 *            y坐标起点
	 * @param size
	 *            技能数量
	 * @param start
	 *            技能起点
	 * @param selPos
	 *            当前已选中的序号[0,2]
	 * @param drawLevel
	 *            是否显示技能等级（如 ×180）, 自练技能时不显示 x
	 */
	static void DrawSkillList(int x, int y, int size, int start, int selPos,
			boolean drawLevel) {
		if (size <= 0)
			return;

		final int r = CURSOR_CIRCLE_R; // 圆圈
		final int lineH = Video.SMALL_FONT_SIZE;
		final int w, h;
		if (size >= 3)
			h = lineH * 3 + 2;
		else
			h = size * lineH + 2;
		if (drawLevel)
			w = lineH * 8 + 6;
		else
			w = lineH * 6;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);

		final int gap = (lineH - r - r) / 2 + 1;
		final int data[][] = GmudTemp.temp_array_20_2;
		for (int i = 0; i < size && i < 3; i++, y += lineH) {
			int skill_id = data[start + i][0];
			int skill_level = data[start + i][1];
			if (skill_id == 255)
				continue;
			Video.VideoDrawArc(x + gap + r, y + gap + r, r);
			if (i == selPos)
				Video.VideoFillArc(x + gap + r, y + gap + r, r - 2);
			String s1 = Skill.skill_name[skill_id];
			if (drawLevel) {
				s1 += "×";
				s1 += String.valueOf(skill_level);
			}
			Video.VideoDrawStringSingleLine(s1, x + lineH, y);
		}
	}

	// *********** UI-Player ************//
	static final String player_menu_words[] = new String[] { "状态", "描述", "属性",
			"婚姻" };

	static final String item_menu_words[] = new String[] { "食物", "药物", "武器",
			"装备", "其它", "接收" };

	static final String useitem_menu_words[] = new String[] { "使用", "丢弃", "发送" };

	static final String skill_menu_words[] = new String[] { "拳脚", "兵刃", "轻功",
			"内功", "招架", "知识", "法术" };

	static final String sys_menu_words[] = new String[] { "内力", "法力", "练功",
			"存档", "结束" };

	static final String fp_menu_words[] = new String[] { "打坐", "加力", "吸气", "疗伤" };

	static final String mp_menu_words[] = new String[] { "冥思", "法点" };

	static final String player_attrib_menu_words[] = new String[] { "膂力", "敏捷",
			"根骨", "悟性" };

	static void DrawViewPlayer(int menu_id) {
		int x = TITLE_X;
		int y = SYSTEM_MENU_Y;
		final int width = Gmud.WQX_ORG_WIDTH - x - SYSTEM_MENU_Y;
		final int height = Gmud.WQX_ORG_HEIGHT - y - SYSTEM_MENU_Y - 1;
		Video.VideoClearRect(x - 1, y, width, height);
		Video.VideoDrawRectangle(x - 1, y, width, height);
		x += Video.SMALL_LINE_H + 2;
		y += 1;
		Video.VideoDrawStringSingleLine(player_menu_words[menu_id], x, y);

		// 绘制方块
		x += Video.SMALL_LINE_H * 2 + TITLE_X;
		y += (Video.SMALL_LINE_H - CURSOR_BOX_W) / 2;
		for (int i = 0; i < 4; i++) {
			Video.VideoDrawRectangle(x, y, CURSOR_BOX_W, CURSOR_BOX_W);
			if (i == menu_id) {
				Video.VideoFillRectangle(x, y, CURSOR_BOX_W, CURSOR_BOX_W, 0);
			}
			x += CURSOR_BOX_W + 3;
		}

		switch (menu_id) {
		case 0:
			DrawPlayerStatus();
			break;
		case 1:
			DrawPlayerDesc();
			break;
		case 2:
			DrawPlayerAttrib();
			break;
		case 3:
			DrawPlayerMerry();
			break;
		}
	}

	static void ViewPlayer() {
		/* 按条件打开玩家隐藏属性 */
		if (Gmud.sMap.GetCurMapID() == 1
				&& Gmud.sMap.GetCurOrientation() == Map.CharOrientation.DOWN) {
			Battle.sBattle = new Battle(-1, 0, 1);
			Battle.sBattle.CopyData();
		}

		int menu_id = 0;
		boolean update = true;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else
					menu_id = 3;
				update = true;
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (menu_id < 3)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				break;
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}
			Input.ClearKeyStatus();
			if (update) {
				update = false;
				// !! 在后面的绘制中，可能会有按键响应，所以这里先 clear，然后直接进入下一按键判断
				DrawViewPlayer(menu_id);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyEnt | Input.kKeyExit);
		}

		if (Battle.sBattle != null) {
			Battle.sBattle = null;
		}
	}

	static void _draw_player_info(String[] desc, int size, int x) {
		final int y = Video.SMALL_FONT_SIZE + SYSTEM_MENU_Y;
		for (int j = 0, ty = y; j < size; j++) {
			if (j >= 5 && j % 5 == 0) {
				Video.VideoUpdate();
				final int last_key = DrawFlashCursor(147, 71, CURSOR_W,
						Input.kKeyDown | Input.kKeyLeft | Input.kKeyRight
								| Input.kKeyEnt | Input.kKeyExit);
				if ((last_key & Input.kKeyDown) == 0) {
					break;
				} else {
					ty = y;
				}
				final int width = Gmud.WQX_ORG_WIDTH - x - SYSTEM_MENU_Y - 2;
				final int height = Gmud.WQX_ORG_HEIGHT - y - SYSTEM_MENU_Y - 2;
				Video.VideoClearRect(x, y, width, height);
			}
			Video.VideoDrawStringSingleLine(desc[j], x, ty);
			ty += Video.SMALL_FONT_SIZE;
		}
	}

	static void DrawPlayerStatus() {
		final String as[] = new String[6];
		int i = 0;
		as[i++] = String.format("食物:%d/%d", Gmud.sPlayer.food,
				Gmud.sPlayer.GetFoodMax());
		as[i++] = String.format("饮水:%d/%d", Gmud.sPlayer.water,
				Gmud.sPlayer.GetWaterMax());

		final int hp_percent;
		if (Gmud.sPlayer.hp_full <= 0
				|| Gmud.sPlayer.hp_max >= Gmud.sPlayer.hp_full)
			hp_percent = 100;
		else
			hp_percent = Gmud.sPlayer.hp_max * 100 / Gmud.sPlayer.hp_full;
		as[i++] = String.format("生命:%d/%d(%d%%)", Gmud.sPlayer.hp,
				Gmud.sPlayer.hp_max, hp_percent);

		as[i++] = String.format("内力:%d/%d(+%d)", Gmud.sPlayer.fp,
				Gmud.sPlayer.fp_level, Gmud.sPlayer.fp_plus);
		if (Gmud.sPlayer.isClass(ClassID.MaoShan)) {
			// 只有茅山派才显示法力
			as[i++] = String.format("法力:%d/%d(+%d)", Gmud.sPlayer.mp,
					Gmud.sPlayer.mp_level, Gmud.sPlayer.mp_plus);
		}
		as[i++] = String.format("经验:%d 潜能:%d", Gmud.sPlayer.exp,
				Gmud.sPlayer.potential);
		_draw_player_info(as, i, TITLE_X);
	}

	static void DrawPlayerDesc() {
		String as[] = new String[5];
		as[0] = String.format("[%s]%s",
				GmudData.class_name[Gmud.sPlayer.GetClassID()],
				Gmud.sPlayer.player_name);

		int sex = Gmud.sPlayer.sex;
		if (sex != 1)
			sex = 0;

		as[1] = String.format("你是一位%d岁的%s", 14 + Gmud.sPlayer.GetAge(),
				sex != 0 ? "女性" : "男性");

		int facelevel = Gmud.sPlayer.GetFaceLevel();
		as[2] = (facelevel < 0) ? "你一脸稚气" : String.format("你长得%s,%s",
				GmudData.face_level_name[facelevel][sex],
				GmudData.face_level_name[facelevel + 1][sex]);

		as[3] = String.format("武艺看起来%s",
				GmudData.level_name[Gmud.sPlayer.GetPlayerLevel() / 5]);
		as[4] = String.format("出手似乎%s",
				GmudData.attack_level_name[Gmud.sPlayer.GetAttackLevel()]);

		_draw_player_info(as, 5, TITLE_X);
	}

	static void DrawPlayerAttrib() {
		String as[] = new String[5];
		as[0] = String.format("金钱:%d", Gmud.sPlayer.money);
		as[1] = String.format("膂力　[%d/%d]", Gmud.sPlayer.GetForce(),
				Gmud.sPlayer.pre_force);
		as[2] = String.format("敏捷　[%d/%d]", Gmud.sPlayer.GetAgility(),
				Gmud.sPlayer.pre_agility);
		as[3] = String.format("悟性　[%d/%d]", Gmud.sPlayer.GetSavvy(),
				Gmud.sPlayer.pre_savvy);
		as[4] = String.format("根骨　[%d/%d]", Gmud.sPlayer.GetAptitude(),
				Gmud.sPlayer.pre_aptitude);
		_draw_player_info(as, 5, TITLE_X);

		Battle battle = Battle.sBattle;
		if (battle != null) {
			final int x = Video.SMALL_FONT_SIZE + Gmud.WQX_ORG_HEIGHT;
			as[0] = String.format("攻击　[%d]", battle.CalcAttack(0));
			as[1] = String.format("防御　[%d]", battle.CalcDefenseB(0));
			as[2] = String.format("闪避　[%d]", battle.CalcAvoid(0));
			as[3] = String.format("命中　[%d]", battle.CalcHit(0));
			as[4] = String.format("福缘　[%d]", Gmud.sPlayer.bliss);
			_draw_player_info(as, 5, x);
		}
	}

	static void DrawPlayerMerry() {
		Video.VideoDrawStringSingleLine(Gmud.sPlayer.GetConsortName(), TITLE_X,
				Video.SMALL_FONT_SIZE + SYSTEM_MENU_Y);
	}

	private static int DrawDeleteItem() {
		int ret = 0;
		final int x = 84;
		final int y = 30;
		final int w = 63;
		final int h = Video.SMALL_LINE_H + 3;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		Video.VideoDrawStringSingleLine("删除吗?y/n", x, y);
		Video.VideoUpdate();
		final int last_key = Gmud.GmudWaitAnyKey();
		if ((last_key & Input.kKeyEnt) != 0) {
			ret = 1;
		}
		return ret;
	}

	/**
	 * 绘制列表，如 物品或技能，左为分类表，右为分类具体项（使用 {@link GmudTemp#temp_array_32_2} 的数据）
	 * 
	 * @param groups
	 *            分类表标题
	 * @param groupTop
	 *            分类表顶部位置
	 * @param group_sel
	 *            分类表高亮项
	 * @param item_top
	 *            子项顶部
	 * @param item_sel
	 *            子项高亮
	 * @param type
	 *            类别
	 * @param isSkill
	 *            0物品表 1技能表
	 */
	static void DrawList(String groups[], int groupTop, int group_sel,
			int item_top, int item_sel, int type, int isSkill) {
		final int length = (isSkill == 0) ? 6 : 7;
		final int lineH = Video.SMALL_FONT_SIZE;
		final int g_w = lineH * 2;
		final int x = Video.SMALL_LINE_H * 3 + TITLE_X;
		final int y = SYSTEM_MENU_Y;
		final int w = Gmud.WQX_ORG_WIDTH - x - TITLE_X;
		final int h = lineH * 5;
		final int gap_l = g_w + 2;
		Video.VideoClearRect(x, y, w, h + 2);
		Video.VideoDrawRectangle(x, y, w, h + 2);
		Video.VideoDrawLine(x + gap_l, y + 1, x + gap_l, y + h + 1);

		// 绘制分类表
		if (groupTop + group_sel <= length - 1) {
			int tx = x + 1;
			int ty = y;
			for (int i = 0, pos = groupTop; i < 5 && pos < length; i++, pos++) {
				if (i == group_sel) {
					Video.VideoFillRectangle(tx, ty + 1, g_w, lineH);
					Video.VideoDrawStringSingleLine(groups[pos], tx, ty, 2);
				} else {
					Video.VideoDrawStringSingleLine(groups[pos], tx, ty);
				}
				ty += lineH;
			}
		}

		// 绘制子项
		int tx = x + gap_l + 2;
		int ty = y;
		final int box_w = CURSOR_BOX_W;
		final int ox = (lineH - box_w) / 2;
		final int oy = (lineH - box_w);
		for (int i = 0; i < 5; i++) {
			int index = GmudTemp.temp_array_32_2[i + item_top][0];
			int isSel = GmudTemp.temp_array_32_2[i + item_top][1];
			if (index == 255)
				return;

			Video.VideoDrawRectangle(tx + ox, ty + oy, box_w, box_w);
			if (type == 1 && item_sel == i)
				Video.VideoFillRectangle(tx + ox, ty + oy, box_w, box_w);
			if (isSel == 1) {
				Video.VideoDrawStringSingleLine("√", tx + 3, ty);
				/*
				 * Video.VideoDrawLine(i3 + l3 + 4 + 4, j4 * k2 + 5 + 3 + j5 +
				 * 9, i3 + l3 + 8 + 4, j4 * k2 + 5 + 2 + 4 + 9);
				 * Video.VideoDrawLine(i3 + l3 + 4 + 4, j4 * k2 + 5 + 3 + j5 +
				 * 9, i3 + l3 + 8 + 13, j4 * k2 + 5 + 2 + 4);
				 */
			}

			final String title;
			if (isSkill == 0) {
				final int id = Gmud.sPlayer.item_package[index][0];
				final String name = Items.item_names[id];
				if (Items.item_repeat[id] == 1) {
					title = String.format("%s x%d", name,
							Gmud.sPlayer.item_package[index][2]);
				} else {
					title = name;
				}
			} else {
				int id = Gmud.sPlayer.skills[index][0];
				title = Skill.skill_name[id];
			}
			Video.VideoDrawStringSingleLine(title, tx + lineH, ty);
			ty += lineH;
		}
	}

	static void PlayerItem() {
		// 焦点是否在大类列表里面 0是 1不是
		int type = 0;
		int group_top = 0, group_sel = 0;
		int item_top = 0, item_sel = 0, item_count = 0;
		int CW = 12;
		ArrayList<String> item_desc = null;
		int item_desc_count = 0, item_desc_cur = 0;

		boolean update = false;
		boolean update_group = true;
		boolean update_item = false;
		boolean update_desc = false;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (type == 0) {
					if (group_sel > 0)
						group_sel--;
					else if (group_top > 0)
						group_top--;
					else {
						group_top = 1;
						group_sel = 5 - 1;
					}
					update_group = true;
				} else {
					if (item_sel > 0)
						item_sel--;
					else if (item_top > 0)
						item_top--;
					else {
						if (item_count < 5) {
							item_top = 0;
							item_sel = item_count - 1;
						} else {
							item_top = item_count - 5;
							item_sel = 5 - 1;
						}
					}
					update_item = true;
				}
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (type == 0) {
					if (group_sel < 4)
						group_sel++;
					else if (group_top < 1)
						group_top++;
					else {
						group_top = 0;
						group_sel = 0;
					}
					update_group = true;
				} else {
					if (item_top + item_sel + 1 >= item_count) {
						item_top = 0;
						item_sel = 0;
					} else if (item_sel < 4)
						item_sel++;
					else
						item_top++;
					update_item = true;
				}
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (type != 0 && item_desc_count > 1
						&& item_desc_cur + 1 < item_desc_count) {
					item_desc_cur++;
					update_desc = true;
				}
			} else if ((last_key & Input.kKeyExit) != 0) {
				if (type != 0) {
					type = 0;
					update_group = true;
					update_desc = true;
				} else {
					break;
				}
			} else if ((last_key & Input.kKeyEnt) != 0) {
				if (type == 0) {
					if (item_count > 0) {
						type = 1;
						update_item = true;
					}
				} else {
					ItemMenu(item_top, item_sel);
					type = 0;
					update_group = true;
					update_desc = true;
				}
			}

			if (update_group) {
				item_count = (Gmud.sPlayer.CopyItemData(-1, group_sel
						+ group_top)) & 0xff;
				item_top = item_sel = 0;
				item_desc_count = item_desc_cur = 0;
			}

			if (update_item) {
				if (type == 1) {
					update_desc = true;
					// 读取物品的描述，并分行
					int index = GmudTemp.temp_array_32_2[item_top + item_sel][0];
					String desc = Items
							.GetItemDesc(Gmud.sPlayer.item_package[index][0]);
					item_desc = Video
							.SplitString(desc, Gmud.WQX_ORG_WIDTH - CW);
					item_desc_count = item_desc.size();
					item_desc_cur = 0;
				}
			}
			if (update_group || update_item) {
				DrawList(item_menu_words, group_top, group_sel, item_top,
						item_sel, type, 0);
				update = true;
			}
			if (update_desc) {
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT - CW,
						Gmud.WQX_ORG_WIDTH, CW);
				if (item_desc_count > 0) {
					String desc = item_desc.get(item_desc_cur);
					if (item_desc_cur < item_desc_count - 1)
						desc += " >";
					Video.VideoDrawStringSingleLine(desc, 2,
							Gmud.WQX_ORG_HEIGHT - CW);
					update = true;
				}
			}
			if (update) {
				Video.VideoUpdate();
			}
			update = false;
			update_group = false;
			update_item = false;
			update_desc = false;
			last_key = Gmud.GmudWaitNewKey(Input.kKeyUp | Input.kKeyDown
					| Input.kKeyEnt | Input.kKeyExit | Input.kKeyLeft
					| Input.kKeyRight);
		}
	}

	/** 物品操作菜单 */
	static void ItemMenu(final int top, final int sel) {
		final int ret = UIUtils.ShowMenu(UIUtils.MENU_TYPE_BOX, 10,
				useitem_menu_words, useitem_menu_words.length, 3, 96, 16, 38,
				MENUID_ITEM) - 1;

		final int item_id = GmudTemp.temp_array_32_2[top + sel][0];
		if (ret == 0) {
			// use item
			String s = Gmud.sPlayer.UseItem(item_id);
			if (s.length() > 0) {
				final int lineH = Video.SMALL_LINE_H;
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT - lineH,
						Gmud.WQX_ORG_WIDTH, lineH);
				Video.VideoDrawStringSingleLine(s, 2, Gmud.WQX_ORG_WIDTH / 2
						- lineH);
				Video.VideoUpdate();
				Gmud.GmudWaitAnyKey();
			}
		} else if (ret == 1) {
			// delete item
			if (DrawDeleteItem() == 1) {
				Gmud.sPlayer.DeleteOneItem(item_id);
			}
		} else if (ret == 2) {
			// TODO send item
			DrawTip("不能发送!");
			Video.VideoUpdate();
			Gmud.GmudWaitAnyKey();
		}
	}

	static void PlayerSkill() {
		int type = 0;
		int groupTop = 0;
		int groupSel = 0;
		int top = 0;
		int sel = 0;
		int count = 0;
		boolean update_group = true;
		boolean update_item = false;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (type == 0) {
					if (groupSel > 0)
						groupSel--;
					else if (groupTop > 0) {
						groupTop--;
					} else {
						groupTop = 2;
						groupSel = 4;
					}
					update_group = true;
				} else {
					if (sel > 0) {
						sel--;
					} else if (top > 0) {
						top--;
					} else if (count < 5) {
						top = 0;
						sel = count - 1;
					} else {
						top = count - 5;
						sel = 4;
					}
					update_item = true;
				}
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (type == 0) {
					if (groupSel < 4)
						groupSel++;
					else if (groupTop < 2)
						groupTop++;
					else
						groupTop = groupSel = 0;
					update_group = true;
				} else {
					if (sel < 4 && top + sel < count - 1)
						sel++;
					else if (top + 5 < count)
						top++;
					else
						top = sel = 0;
					update_item = true;
				}
			} else if ((last_key & Input.kKeyExit) != 0) {
				if (type != 0) {
					type = 0;
					update_group = true;
					update_item = true;
				} else {
					break;
				}
			} else if ((last_key & Input.kKeyEnt) != 0) {
				if (type == 0 && count > 0) {
					type = 1;
					update_item = true;
				} else if (type != 0) {
					final int skillType = groupTop + groupSel;
					final int index = top + sel;
					int skill_id = Gmud.sPlayer.skills[GmudTemp.temp_array_32_2[index][0]][0];
					if (skill_id < 10) {
						// 基本功，不做任何处理
					} else {
						if (skillType != 5) {
							// 知识类技能不可选
							int isSel = GmudTemp.temp_array_32_2[index][1];
							if (isSel == 0)
								Gmud.sPlayer.SelectSkill(skill_id, skillType);
							else
								Gmud.sPlayer.UnselectSkill(skillType);
							if (Battle.sBattle != null)
								Battle.sBattle.CopyPlayerSelectSkills();
							update_group = true;
						}
						update_item = true;
						type = 0;
					}
				}
			}

			if (update_item) {
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT
						- Video.SMALL_LINE_H, Gmud.WQX_ORG_WIDTH,
						Video.SMALL_LINE_H);
			}
			if (update_group) {
				count = Gmud.sPlayer.CopySkillData(-1, groupTop + groupSel) & 0xff;
				top = 0;
				sel = 0;
				update_item = true;
			}
			if (update_item) {
				if (count > 0 && type != 0) {
					final int index = GmudTemp.temp_array_32_2[top + sel][0];
					final int point = Gmud.sPlayer.skills[index][2];
					int level = Gmud.sPlayer.skills[index][1];
					if (level > 255)
						level = 255;
					if (level < 0)
						level = 0;
					String s = String.format("%s %d /%d",
							GmudData.level_name[level / 5], level, point);
					Video.VideoDrawStringSingleLine(s, 35, Gmud.WQX_ORG_HEIGHT
							- Video.SMALL_LINE_H);
				}
			}
			if (update_group || update_item) {
				DrawList(skill_menu_words, groupTop, groupSel, top, sel, type,
						1);
				Video.VideoUpdate();
				update_group = false;
				update_item = false;
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyUp | Input.kKeyDown
					| Input.kKeyEnt | Input.kKeyExit);
		}
	}

	static void FPMenu() {
		Gmud.sMap.DrawMap(-1);
		UIUtils.ShowMenu(fp_menu_words, fp_menu_words.length, 4, 115, 11, 44,
				MENUID_FP);
	}

	static int FPCallback(int sel) {
		if (sel == 0) {
			if (RecoverFP() == 1)
				return 1;
		} else if (sel == 1) {
			if (FPPlusMenu() == 1)
				return 1;
		} else if (sel == 2) {
			String s = Gmud.sPlayer.Breathing();
			DrawStringFromY(s);
		} else if (sel == 3) {
			String s = Gmud.sPlayer.Recovery();
			if (s == Res.STR_RECOVER_SUCCESS) {
				DrawStringFromY(Res.STR_RECOVER_START);
			}
			DrawStringFromY(s);
		}
		Gmud.sMap.DrawMap(-1);
		return 0;
	}

	/**
	 * 打坐　或　冥想,
	 * 
	 * @param isFP
	 * @return 0:正常中止 1:没有选择 2:基本功不够 3:已达到上限
	 * @see Player#Think()
	 * @see Player#Meditation()
	 */
	static int _recover_fp_mp(boolean isFP) {
		int err = 0;
		// 控制在 [60,180]之间
		final int speed = 180 - 2 * (isFP ? Gmud.sPlayer.GetFPSpeed()
				: Gmud.sPlayer.GetMPSpeed());
		Input.ClearKeyStatus();
		do {
			final int max, cur;
			if (isFP) {
				max = Gmud.sPlayer.fp_level;
				cur = Gmud.sPlayer.fp;
			} else {
				max = Gmud.sPlayer.mp_level;
				cur = Gmud.sPlayer.mp;
			}
			DrawProgressBox(max * 2, cur, cur, max);
			Video.VideoUpdate();
			if (Gmud.GmudWaitKey(Input.kKeyExit, speed) != 0) {
				break;
			}
			if (isFP)
				err = Gmud.sPlayer.Meditation();
			else
				err = Gmud.sPlayer.Think();
		} while (err == 0);
		return err;
	}

	/** 打坐 */
	static int RecoverFP() {
		final int err = _recover_fp_mp(true);
		final String str;
		if (err == 1) {
			// 1:没有选择
			str = Res.STR_NO_INNER_KONGFU_STRING;
		} else if (err == 2) {
			// 2:基本功不够
			str = Res.STR_INNER_KONGFU_TOO_LOW;
		} else if (err == 3) {
			// 3:已达到上限
			str = Res.STR_INNER_KONGFU_TOO_LOW;
		} else {
			return 0;
		}
		DrawStringFromY(str);
		return 0;
	}

	/**
	 * 在指定位置 (x,y) 使用加点框
	 * 
	 * @param max
	 * @param last
	 * @param x
	 * @param y
	 * @return 最终的点数，如果是按下 EXIT 键，则返回原始的点数
	 */
	static int _fp_mp_plush_menu(final int max, final int last, int x, int y) {
		int cur = last > max ? max : last;
		boolean update = true;
		int last_key = 0;
		while (true) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (cur > 0) {
					cur--;
					update = true;
				}
			} else if ((last_key & Input.kKeyLeft) != 0) {
				if (cur < max) {
					cur += 10;
					if (cur > max)
						cur = max;
					update = true;
				}
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (cur > 0) {
					if (cur > 10)
						cur -= 10;
					else
						cur = 0;
					update = true;
				}
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (cur < max) {
					cur++;
					update = true;
				}
			} else if ((last_key & Input.kKeyEnt) != 0) {
				break;
			} else if ((last_key & Input.kKeyExit) != 0) {
				cur = last;
				break;
			}
			if (update) {
				DrawNumberBox(cur, max, x, y);
				Video.VideoUpdate();
				update = false;
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyUp | Input.kKeyDown | Input.kKeyEnt
					| Input.kKeyExit);
		}
		return cur;
	}

	/** 加力菜单 */
	static int FPPlusMenu() {
		final String str;
		if (255 == Gmud.sPlayer.select_skills[3]) {
			str = Res.STR_NO_INNER_KONGFU_STRING;
		} else {
			final int max = Gmud.sPlayer.GetPlusFPMax();
			final int last = Gmud.sPlayer.fp_plus;
			final int cur = _fp_mp_plush_menu(max, last, 53, 44);
			if (cur != last)
				Gmud.sPlayer.fp_plus = cur;
			if (cur == max) {
				str = String.format(Res.STR_FP_PLUS_LIMIT_STRING, max);
			} else
				str = null;
		}
		if (str != null) {
			DrawStringFromY(str);
		}
		return 0;
	}

	static int RecoverMP() {
		final int err = _recover_fp_mp(false);
		final String str;
		if (err == 1) {
			// 1:没有选择
			str = Res.STR_NO_FASHU_STRING;
		} else if (err == 2) {
			// 2:基本功不够
			str = Res.STR_FASHU_TOO_LOW;
		} else if (err == 3) {
			// 3:已达到上限
			str = Res.STR_FASHU_TOO_LOW;
		} else {
			return 0;
		}
		DrawStringFromY(str);
		return 0;
	}

	static int MPPlusMenu() {
		final String str;
		if (255 == Gmud.sPlayer.select_skills[6]) {
			str = Res.STR_NO_FASHU_STRING;
		} else {
			final int max = Gmud.sPlayer.GetPlusMPMax();
			final int last = Gmud.sPlayer.mp_plus;
			final int cur = _fp_mp_plush_menu(max, last, 53, 44);
			if (cur != last)
				Gmud.sPlayer.mp_plus = cur;
			if (cur == max) {
				str = String.format(Res.STR_MP_PLUS_LIMIT_STRING, max);
			} else {
				str = null;
			}
		}
		if (str != null) {
			DrawStringFromY(str);
		}
		return 0;
	}

	static void MPMenu() {
		Gmud.sMap.DrawMap(-1);
		UIUtils.ShowMenu(mp_menu_words, mp_menu_words.length, 2, 115, 11, 44,
				MENUID_MP);
	}

	static int MPCallback(int sel) {
		if (sel == 0) {
			if (RecoverMP() == 1)
				return 1;
		} else if (sel == 1)
			if (MPPlusMenu() == 1)
				return 1;
		Gmud.sMap.DrawMap(-1);
		return 0;
	}

	/**
	 * 自练技能菜单
	 */
	static void PractMenu() {
		// 计算可用于练习的技能数量
		final int size = Gmud.sPlayer.GetPracticeSkillNumber();
		if (size == 0)
			return;
		Gmud.sMap.DrawMap(-1);
		int last_key = 0;
		boolean update = true;
		int top = 0;
		int sel = 0;
		while (true) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else if (top > 0) {
					top--;
				} else if (size > 2) {
					top = size - 3;
					sel = 2;
				} else {
					top = 0;
					sel = size - 1;
				}
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (top + sel + 1 >= size) {
					top = 0;
					sel = 0;
				} else if (sel < 2) {
					sel++;
				} else {
					top++;
				}
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				final int pos = top + sel;
				final int skill_id = GmudTemp.temp_array_20_2[pos][0];
				final int index = Gmud.sPlayer.SetNewSkill(skill_id);
				if (index == -1)
					break;
				int err = 0;
				do {
					err = DrawPractice(index);
					Gmud.sMap.DrawMap(-1);
				} while (err == 0);
				update = true;
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}
			if (update) {
				update = false;
				DrawSkillList(78, 11, size, top, sel, false);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyDown | Input.kKeyUp
					| Input.kKeyEnt | Input.kKeyExit);
		}
		Input.ClearKeyStatus();
	}

	/**
	 * 练功，如果中途连续多次按下 ENTER 键，则自动调整 {@link #m_auto_confirm}
	 * 
	 * @param index
	 *            玩家技能表中的索引
	 * @return <br/>
	 *         0正常学习 1很难提高，需要向师傅请教 2需要提升内功 3打坐不够 4没有趁手兵器 5有伤 6升级 7基本功没有 <br/>
	 *         0正常 1经验不足 2潜能不足 3钱不足 4等级超过 5升级
	 * @see Player#PracticeSkill(int)
	 * @see Player#StudySkill(int, int)
	 * @see #m_auto_confirm
	 */
	static int _draw_study_practice(int index, boolean isPractice, int maxLevel) {
		int err = 0;
		// 控制在 [60,180]之间
		final int speed = 180 - 2 * (isPractice ? Gmud.sPlayer
				.GetPracticeSpeed(index) : Gmud.sPlayer.GetStudySpeed());
		final int[] skill_data = Gmud.sPlayer.skills[index];
		Input.ClearKeyStatus();
		do {
			DrawProgressBox(skill_data[4], skill_data[2], skill_data[2],
					skill_data[1]);
			Video.VideoUpdate();
			// 等待 ENTER 或 EXIT 按键，或超时
			final int last_key = Gmud.GmudWaitKey(Input.kKeyEnt
					| Input.kKeyExit, speed);
			if ((last_key & Input.kKeyExit) != 0) {
				break;
			}
			if (last_key != 0) {
				if (m_auto_confirm > 0 && (last_key & Input.kKeyEnt) != 0) {
					--m_auto_confirm;
				}
				Input.ClearKeyStatus();
			}
			if (isPractice)
				err = Gmud.sPlayer.PracticeSkill(index);
			else
				err = Gmud.sPlayer.StudySkill(index, maxLevel);
		} while (err == 0);
		return err;
	}

	/**
	 * 练功
	 * 
	 * @param index
	 *            玩家技能表中的索引
	 * @return 0升级 1按下Exit键 3不能继续（如经验不足、基本功等级不够、受伤等）
	 */
	static int DrawPractice(int index) {
		final int err = _draw_study_practice(index, true, 0);
		final String str;
		if (err == 1) {
			str = Res.STR_PRACTICE_NEED_MASTER;
		} else if (err == 2) {
			str = Res.STR_INNER_KONGFU_TOO_LOW;
		} else if (err == 3) {
			str = Res.STR_PRACTICE_NEED_FP_LEVEL;
		} else if (err == 4) {
			str = Res.STR_PRACTICE_NEED_WEAPON;
		} else if (err == 5) {
			str = Res.STR_PRACTICE_NEED_HEALTHFUL;
		} else if (err == 6) {
			str = Res.STR_STUDY_YOUR_SKILL_PROGRESS;
		} else if (err == 7) {
			str = Res.STR_PRACTICE_NO_BASE_SKILL;
		} else {
			return 1;
		}
		DrawStringFromY(str);
		if (err != 6)
			return 3;
		return 0;
	}

	// ********** UI-NPC **********//
	static int npc_id = 0;
	static int npc_image_id = 0;
	static String npc_name;
	static final String npc_menu_words[] = new String[] { "交谈", "查看", "战斗",
			"切磋", "交易", "拜师", "请教" };

	static void TalkWithNPC(int npc_id) {
		task.Talk(npc_id);
	}

	static void EnterBattle(int npc_id) {
		Battle.sBattle = new Battle(npc_id, UI.npc_image_id, 0);
		Battle.sBattle.BattleMain();
		Battle.sBattle = null;

		/*
		 * aa.f = 0; aa.i = o.e; aa.j = id; aa.k = 0; aa.h = 0; aa.d();
		 */
	}

	static void EnterTryBattle(int npc_id) {
		if (NPC.NPC_attrib[npc_id][11] < NPC.NPC_attrib[npc_id][15] / 2) {
			Gmud.sMap.DrawMap(-1);
			Gmud.GmudDelay(120);
			String str = "对方看起来并不想你和切磋";
			UI.DrawDialog(str);
			return;
		} else {
			Battle.sBattle = new Battle(npc_id, UI.npc_image_id, 1);
			Battle.sBattle.BattleMain();
			// delete glpBattle;
			// glpBattle = 0;
			Battle.sBattle = null;
			/*
			 * aa.f = 0; aa.i = o.e; aa.j = i1; aa.k = 1; aa.h = 0; aa.d();
			 */
			return;
		}
	}

	static void Trade(int id) {
		if (NPC.NPC_sell_list[id][0] == 0) { // 卖
			int size = Gmud.sPlayer.CopyItemList();
			TradeWithNPC(true, size);
			return;
		} else { // 买
			int size = NPC.CopyItemList(id);
			TradeWithNPC(false, size);
			return;
		}
	}

	/***
	 * 与 NPC 交易
	 * 
	 * @param sell
	 *            false买，true卖
	 * @param size
	 *            物品列表大小，见 {@link GmudTemp#temp_array_32_2}
	 */
	static void TradeWithNPC(boolean sell, int size) {
		if (size <= 0)
			return;
		Gmud.sMap.DrawMap(-1);
		DrawTalk(readDialogText(6 + (sell ? 1 : 0)));

		final int x = TITLE_X + TITLE_X;
		final int y = Video.SMALL_FONT_SIZE + SYSTEM_MENU_Y;
		int top = 0;
		int sel = 0;
		Input.ClearKeyStatus();
		boolean update = true;
		int last_key = 0;
		while (true) {
			Input.ProcessMsg();
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0) {
					sel--;
				} else if (top > 0) {
					top--;
				} else if (size > 3) {
					top = size - 3;
					sel = 2;
				} else {
					top = 0;
					sel = size - 1;
				}
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (top + sel + 1 >= size) {
					top = 0;
					sel = 0;
				} else if (sel < 2)
					sel++;
				else
					top++;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				final int pos = top + sel;
				final int id = GmudTemp.temp_array_32_2[pos][0];
				if (sell) {
					// TODO: 用 序号 代替 ID
					// Gmud.sPlayer.LoseItem(i3, 1);
					int index = Gmud.sPlayer.ExistItem(id, 1);
					if (index >= 0) {
						Gmud.sPlayer.LoseItem(index, 1);
						Gmud.sPlayer.money += (Items.item_attribs[id][6] * 7) / 10;
						break;
					}
				} else {
					int limit_money = Items.item_attribs[id][6];
					if (Gmud.sPlayer.money >= limit_money
							&& Gmud.sPlayer.GainOneItem(id)) {
						Gmud.sPlayer.money -= Items.item_attribs[id][6];
						break;
					}
				}
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}
			if (update) {
				update = false;
				DrawItemList(x, y, size, top, sel, sell);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyExit | Input.kKeyEnt
					| Input.kKeyDown | Input.kKeyUp);
		}
		return;
	}

	/**
	 * 绘制物品列表，最多３行
	 * 
	 * @param x
	 * @param y
	 * @param size
	 *            列表数量
	 * @param start
	 *            可见列表中最顶上一个的索引
	 * @param sel
	 * @param sell
	 */
	static void DrawItemList(final int x, final int y, int size, int start,
			int sel, boolean sell) {
		if (size <= 0)
			return;
		int lineH = Video.SMALL_FONT_SIZE;
		final int h, w;
		if (size >= 3)
			h = lineH * 3 + 2;
		else
			h = size * lineH + 2;
		if (sell)
			w = lineH * 7 + 4 + 4;
		else
			w = lineH * 5 + 4;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		final int ox = lineH / 2;
		int tx = x + lineH;
		int ty = y;
		for (int i = 0; i < size && i < 3; i++) {
			int item_id = GmudTemp.temp_array_32_2[start + i][0];
			int item_number = GmudTemp.temp_array_32_2[start + i][1];
			if (item_id == 255)
				continue;
			Video.VideoDrawArc(tx - ox, ty + ox + 1, CURSOR_CIRCLE_R);
			if (i == sel) {
				Video.VideoFillArc(tx - ox, ty + ox + 1, CURSOR_CIRCLE_R - 2);
				int cost = Items.item_attribs[item_id][6];
				if (sell)
					cost = (cost * 7) / 10;
				final String str = String.format("金钱:%d　价格:%d",
						Gmud.sPlayer.money, cost);
				final int tmp_y = y + lineH * 3 + 4;
				Video.VideoClearRect(x, tmp_y,
						Gmud.WQX_ORG_WIDTH - x - TITLE_X, lineH);
				Video.VideoDrawStringSingleLine(str, x, tmp_y);
			}

			String str = Items.item_names[item_id];
			if (sell) {
				str += String.format("x%d", item_number);
			}
			Video.VideoDrawStringSingleLine(str, tx, ty);
			ty += lineH;
		}
	}

	/**
	 * 计算技能等级
	 * 
	 * @param i
	 *            门派功夫ID
	 * @param j
	 *            基本功夫ID
	 * @return
	 */
	static int GetSkillTypeLevel(int i, int j) {
		return (Gmud.sPlayer.GetSkillLevel(i) + Gmud.sPlayer.GetSkillLevel(j) / 2);
	}

	// extern wstring ReplaceStr(wstring*, LPCWSTR, LPCWSTR);

	static void ApprenticeWords(int i) {
		// UI.DrawDialog(&ReplaceStr(&ReplaceStr(&gmud_readtext(5,
		// UI.dialog_point[i], UI.dialog_point[1 + i]), "$o",
		// UI.npc_name.c_str()), "m", Gmud.sPlayer.player_name));
		String str = readDialogText(i);
		String s1 = str.replaceAll("\\$o", UI.npc_name);
		UI.DrawDialog(s1.replaceAll("m", Gmud.sPlayer.player_name));
	}

	static void Apprentice(int id) {
		if (!Gmud.sPlayer.isClass(ClassID.None)
				&& !Gmud.sPlayer.isClass(NPCINFO.NPC_attribute[id][1])) {
			Gmud.sMap.DrawMap(-1);
			DrawDialog(readDialogText(9));
			return;
		}
		Gmud.sMap.DrawMap(-1);
		switch (id) {
		case 96: // 谷虚道长
			ApprenticeWords(182);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.WuDang);
			break;

		case 97: // 古松道长
			ApprenticeWords(182);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.WuDang);
			break;

		case 101: // 清虚道长
			if (Gmud.sPlayer.fp_level < 1500) {
				ApprenticeWords(183);
				return;
			}
			if (GetSkillTypeLevel(32, 0) < 180) {
				ApprenticeWords(184);
				return;
			}
			if (GetSkillTypeLevel(31, 1) < 150) {
				ApprenticeWords(185);
				return;
			}
			if (Gmud.sPlayer.GetSavvy() < 28) {
				ApprenticeWords(186);
				return;
			}
			ApprenticeWords(187);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.WuDang);
			break;

		case 122: // 雪山教头
			if (Gmud.sPlayer.GetAgility() < 22) {
				ApprenticeWords(188);
				return;
			}
			ApprenticeWords(189);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.XueShan);
			break;

		case 118: // 封万剑
			if (Gmud.sPlayer.GetAgility() < 23) {
				ApprenticeWords(188);
				return;
			}
			if (GetSkillTypeLevel(36, 0) < 60) {
				ApprenticeWords(193);
				return;
			}
			ApprenticeWords(192);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.XueShan);
			break;

		case 110: // 白瑞德
			if (Gmud.sPlayer.fp_level < 1200) {
				ApprenticeWords(190);
				return;
			}
			if (Gmud.sPlayer.GetAptitude() < 32) {
				ApprenticeWords(191);
				return;
			}
			ApprenticeWords(194);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.XueShan);
			break;

		case 38: // 商宝震
			ApprenticeWords(163);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.BaGuaMen);
			break;

		case 43: // 商剑鸣
			if (Gmud.sPlayer.fp_level < 500) {
				ApprenticeWords(159);
				return;
			}
			if (GetSkillTypeLevel(14, 0) < 75) {
				ApprenticeWords(160);
				return;
			}
			if (GetSkillTypeLevel(11, 3) < 75) {
				ApprenticeWords(161);
				return;
			}
			ApprenticeWords(162);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.BaGuaMen);
			break;

		case 47: // 王维扬
			if (Gmud.sPlayer.fp_level < 800) {
				ApprenticeWords(159);
				return;
			}
			if (GetSkillTypeLevel(14, 0) < 150) {
				ApprenticeWords(160);
				return;
			}
			if (GetSkillTypeLevel(11, 3) < 150) {
				ApprenticeWords(164);
				return;
			}
			ApprenticeWords(165);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.BaGuaMen);
			break;

		case 90: // 腾王丸
			ApprenticeWords(181);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.YiHeGu);
			break;

		case 87: // 花十郎
			if (GetSkillTypeLevel(27, 1) < 90) {
				ApprenticeWords(180);
				return;
			}
			ApprenticeWords(181);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.YiHeGu);
			break;

		case 94: // 和仲阳
			if (Gmud.sPlayer.GetForce() < 22) {
				ApprenticeWords(178);
				return;
			}
			if (GetSkillTypeLevel(26, 0) < 180) {
				ApprenticeWords(180);
				return;
			}
			ApprenticeWords(179);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.YiHeGu);
			break;

		case 73: // 方长老
			ApprenticeWords(174);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.HongLianJiao);
			break;

		case 80: // 余鸿儒
			if (GetSkillTypeLevel(25, 0) < 180) {
				ApprenticeWords(175);
				return;
			}
			if (Gmud.sPlayer.GetForce() < 30) {
				ApprenticeWords(176);
				return;
			}
			ApprenticeWords(174);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.HongLianJiao);
			break;

		case 56: // 平婆婆
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.HuaJian);
			break;

		case 57: // 桑轻虹
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (GetSkillTypeLevel(19, 1) < 90) {
				ApprenticeWords(172);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.HuaJian);
			break;

		case 66: // 唐晚词
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (GetSkillTypeLevel(19, 1) < 75) {
				ApprenticeWords(172);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.HuaJian);
			// fall through

		case 58: // 李青照
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (Gmud.sPlayer.GetSkillLevel(9) < 100) {
				ApprenticeWords(168);
				return;
			}
			if (Gmud.sPlayer.GetFaceLevel() < 9) {
				ApprenticeWords(167);
				return;
			}
			ApprenticeWords(170);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.HuaJian);
			break;

		case 127: // 华佗
			if (Gmud.sPlayer.pre_savvy > 18) {
				ApprenticeWords(151);
				return;
			}
			ApprenticeWords(150);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.ShouWang);
			break;

		case 126: // 北海鳄神
			if (GetSkillTypeLevel(40, 255) < 60) {
				ApprenticeWords(152);
				return;
			}
			ApprenticeWords(149);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.ShouWang);
			break;

		case 129: // 娜可露露
			if (GetSkillTypeLevel(40, 255) < 110) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(45, 0) < 165) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(41, 3) < 165) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(43, 1) < 165) {
				ApprenticeWords(153);
				return;
			}
			ApprenticeWords(148);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.ShouWang);
			break;

		case 135: // 葛洪
			if (Gmud.sPlayer.pre_savvy < 28) {
				ApprenticeWords(154);
				return;
			}
			ApprenticeWords(145);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.MaoShan);
			break;

		case 138: // 留孙真人
			if (Gmud.sPlayer.GetSavvy() < 28) {
				ApprenticeWords(154);
				return;
			}
			if (GetSkillTypeLevel(49, 9) < 120) {
				ApprenticeWords(155);
				return;
			}
			ApprenticeWords(147);
			ApprenticeWords(10);

			Gmud.sPlayer.SetClassID(ClassID.MaoShan);
			break;

		case 141: // 茅盈
			if (Gmud.sPlayer.GetSavvy() < 40) {
				ApprenticeWords(156);
				return;
			}
			if (Gmud.sPlayer.mp_level < 1200) {
				ApprenticeWords(157);
				return;
			}
			ApprenticeWords(146);
			ApprenticeWords(10);
			Gmud.sPlayer.SetClassID(ClassID.MaoShan);
			break;

		default:
			return;
		}
		Gmud.sPlayer.teacher_id = id;
	}

	/**
	 * 学技能
	 * 
	 * @param skill_index
	 * @param maxLevel
	 * @return 0正常 -1按下EXIT 其它问题（等级太高、经验不足、无潜能、没钱）
	 * @see Player#StudySkill(int, int)
	 */
	static int DrawConsult(int skill_index, int maxLevel) {
		final int err = _draw_study_practice(skill_index, false, maxLevel);
		final String str;
		if (err == 1) {
			str = Res.STR_STUDY_NEED_MORE_EXPERIENCE;
		} else if (err == 2) {
			str = Res.STR_STUDY_NEED_MORE_PONTENTIAL;
		} else if (err == 3) {
			str = Res.STR_STUDY_NEED_MORE_MONEY;
		} else if (err == 4) {
			str = Res.STR_STUDY_YOU_ARE_MASTER;
		} else if (err == 5) {
			str = Res.STR_STUDY_YOUR_SKILL_PROGRESS;
		} else {
			return -1;
		}
		DrawStringFromY(str);
		if (err != 5)
			return err;

		if (m_auto_confirm == 0)
			return 0;

		Gmud.sMap.DrawMap(-1);
		int last_key = DialogBx(Res.STR_STUDY_QUERY_CONTINUE, TITLE_X,
				Video.SMALL_FONT_SIZE * 3);
		last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
		if ((last_key & Input.kKeyEnt) != 0) {
			return 0;
		}
		return -1;
	}

	static void ConsultWithNPC(int size) {
		Gmud.sMap.DrawMap(-1);
		UI.DrawTalk(readDialogText(8));
		final int x = 5;
		final int y = Video.SMALL_LINE_H + 2;
		int top = 0;
		int sel = 0;

		final int max;
		if (size > 3)
			max = size - 3;
		else
			max = 0;

		boolean update = true;
		int last_key = 0;
		while (Input.Running) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else if (top > 0) {
					top--;
				} else {
					top = max;
					if (max == 0)
						sel = size - 1;
					else
						sel = 2;
				}
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (sel < 2 && sel < size - 1)
					sel++;
				else if (top < size - 3) {
					top++;
				} else {
					top = 0;
					sel = 0;
				}
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				int index = top + sel;
				int skill_id = GmudTemp.temp_array_20_2[index][0];
				int skill_level = GmudTemp.temp_array_20_2[index][1];
				int skill_index = Gmud.sPlayer.SetNewSkill(skill_id);
				if (skill_index >= 0) {
					m_auto_confirm = 3;
					int err;
					do {
						err = DrawConsult(skill_index, skill_level);
						Gmud.sMap.DrawMap(-1);
					} while (err == 0);
					if (err == 2) {
						// == 2 没有潜能，不能学
						break;
					}
					update = true;
				}
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}

			if (update) {
				update = false;
				DrawSkillList(x, y, size, top, sel, true);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyDown | Input.kKeyUp
					| Input.kKeyEnt | Input.kKeyExit);
		}
	}

	static void Consult(int id) {
		// 独行大侠，要求 exp > 20W
		if (6 == id && Gmud.sPlayer.exp < 0x30d40) {
			Gmud.sMap.DrawMap(-1);
			String str = Res.STR_LONE_HEROE_REQUEST_ENOUGH_EXPERIENCE;
			DrawDialog(str);
		} else {
			int size = NPC.CopyNCPSkillList(id);
			if (0 == size) {
			} else {
				ConsultWithNPC(size);
			}
		}
	}

	static void NPCMainMenu(int npc_id, int npc_type, int x) {
		final String[] titles;
		final int count;
		if (npc_type <= 0) {
			count = 4;
			titles = npc_menu_words;
		} else {
			count = 5;
			titles = new String[5];
			System.arraycopy(npc_menu_words, 0, titles, 0, 4);
			titles[4] = npc_menu_words[npc_type];
		}
		final int w = Video.SMALL_LINE_H * 2 + 12;
		if (x < TITLE_X)
			x = TITLE_X;
		if (x + w + TITLE_X > Gmud.WQX_ORG_WIDTH)
			x = Gmud.WQX_ORG_WIDTH - TITLE_X - w;
		Video.VideoClearRect(TITLE_X, 1, Video.SMALL_LINE_H * 4,
				Video.SMALL_LINE_H);
		Video.VideoDrawStringSingleLine(npc_name, TITLE_X, 1);
		final int sel = UIUtils.ShowMenu(UIUtils.MENU_TYPE_BOX, 12, titles,
				count, count, x, -1, w, MENUID_NPC) - 1;
		if (sel >= 0) {
			if (sel == 0) {
				TalkWithNPC(npc_id); // 交谈
			} else if (sel == 1) {
				ViewNPC(npc_id); // 查看
			} else if (sel == 2) {
				EnterBattle(npc_id); // 战斗
			} else if (sel == 3) {
				EnterTryBattle(npc_id); // 切磋
			} else if (sel == 4) {
				if (npc_type == 4)
					Trade(npc_id); // 交易
				else if (npc_type == 5)
					Apprentice(npc_id); // 拜师
				else if (npc_type == 6)
					Consult(npc_id); // 请教
			}
		}
	}

	static void DrawTalk(String tip) {
		final int h = Video.SMALL_LINE_H * 2;
		ArrayList<String> as = Video.SplitString(tip, Gmud.WQX_ORG_WIDTH
				- TITLE_X - 1);
		final int size = as.size();
		for (int i = 0; i < size;) {
			Video.VideoClearRect(0, 0, Gmud.WQX_ORG_WIDTH - 1, h);
			Video.VideoDrawRectangle(0, 0, Gmud.WQX_ORG_WIDTH - 1, h);
			Video.VideoDrawStringSingleLine(as.get(i), TITLE_X, 1);
			if (++i < size) {
				Video.VideoDrawStringSingleLine(as.get(i), TITLE_X,
						Video.SMALL_LINE_H);
				i++;
			}
		}
	}

	static void ViewNPC(int npcId) {
		int age = NPCINFO.NPC_attribute[npcId][2];
		if (age < 10)
			age = 10;
		age = (age / 10) * 10;

		Battle.sBattle = new Battle(npcId, 0, 0);
		Battle.sBattle.CopyNPCData(npcId);
		Battle.sBattle.CalcFighterLevel(1);
		int level = Battle.sBattle.fighter_data[1][62] / 5;
		int attack_level = Battle.sBattle.CalcAttackLevel(1);
		Battle.sBattle = null;

		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int item_id = NPC.NPC_item[npcId][i];
			if ((item_id < 77 || item_id > 86)
					&& (item_id < 88 || item_id > 91)
					&& (item_id < 68 || item_id > 71) && item_id != 10
					&& item_id > 0) {
				b.append(Items.item_names[item_id]).append(" ");
			}
		}
		final String items = b.toString();
		final String desc = NPC.GetNPCDesc(npcId);
		String str = String.format(Res.STR_NPC_DESC, npc_name, age,
				GmudData.level_name[level],
				GmudData.attack_level_name[attack_level], items, desc);
		final int x = TITLE_X;
		final int y = SYSTEM_MENU_Y + 2;
		final int w = Gmud.WQX_ORG_WIDTH - x - 2;
		final int h = Gmud.WQX_ORG_HEIGHT - y - 2;
		final ArrayList<String> as = Video.SplitString(str, w);
		final int size = as.size();
		final int max_line = 6;
		int pages = (size + max_line - 1) / max_line;
		for (int i = 0, head = 0; i < pages; head += max_line) {
			Video.VideoClearRect(x, y, w, h);
			Video.VideoDrawRectangle(x, y, w, h);
			for (int j = 0, t = head; j < max_line && t < size; j++, t++)
				Video.VideoDrawStringSingleLine(as.get(t), x, y + j
						* Video.SMALL_FONT_SIZE);
			Video.VideoUpdate();
			if (++i < pages) {
				int last_key = DrawFlashCursor(147, y + 2, CURSOR_W,
						Input.kKeyDown | Input.kKeyExit);
				if ((last_key & Input.kKeyExit) != 0) {
					break;
				}
			} else {
				Gmud.GmudWaitAnyKey();
				break;
			}
		}
	}

	final static int MENUID_FP = 0;
	final static int MENUID_MP = 1;
	final static int MENUID_PRACT = 2;
	final static int MENUID_SYSTEM = 3;
	final static int MENUID_FLY = 4;
	final static int MENUID_ITEM = 5;
	final static int MENUID_NPC = 6;

	private static int _def_callback_index(int sel) {
		return sel + 1;
	}

	public static int onMenuCallBack(int callbackID, int sel) {
		int ret = 0;
		switch (callbackID) {
		case MENUID_FP:
			return FPCallback(sel);
		case MENUID_MP:
			return MPCallback(sel);
		case MENUID_SYSTEM:
			return SystemCallback(sel);
		case MENUID_FLY:
			return FlyCallback(sel);
		case MENUID_ITEM:
			return _def_callback_index(sel);
		case MENUID_NPC:
			return _def_callback_index(sel);

		default:
			break;
		}
		return ret;
	}

}
