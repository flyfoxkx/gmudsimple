package cn.fmsoft.lnx.gmud.simple.core;

import android.graphics.Bitmap;

public class uibattle {

	static int hit_id = -1;
	static int menu_id = 0;
	static Bitmap player_img;
	static Bitmap NPC_img;
	static Bitmap hp_img;
	static Bitmap fp_img;
	static Bitmap mp_img;
	static Bitmap hit_img;
	static final String menu_title[] = new String[] { "普通攻击", "绝招攻击", "使用内力",
			"使用物品", "调整招式", "逃跑" };

	static final String[] desc_words = new String[] { "N反手握刀纵声长啸，霎时间，天地为之变色，这一刀之势虽然平平，却威力惊人。" };

	/** 双方的武器，用于显示招式时输出 */
	static int weapon_id[] = new int[2];

	/**
	 * 绘制内力菜单，只有 {加力} 和 {吸气}
	 * 
	 * @param sel
	 *            当前选择
	 */
	static void DrawFPMenu(int sel) {
		final int x = 58;
		final int y = 48;
		final int w = Video.SMALL_LINE_H * 4;
		final int h = Video.SMALL_LINE_H * 2;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		UI.DrawCursor(x + (Video.SMALL_LINE_H - UI.CURSOR_W) / 2, y
				+ Video.SMALL_LINE_H * sel + (Video.SMALL_LINE_H - UI.CURSOR_H)
				/ 2);
		for (int i = 0; i < 2; i++) {
			Video.VideoDrawStringSingleLine(UI.fp_menu_words[i + 1], x
					+ Video.SMALL_FONT_SIZE, y + Video.SMALL_LINE_H * i);
		}
	}

	/**
	 * 加力菜单
	 */
	static void DrawPlusFp() {
		final int id_active = Battle.sBattle.m_active_id;
		final String str;
		// 无内功，直接返回
		if (Battle.sBattle.fighter_data[id_active][36] == 255) {
			str = Res.STR_NO_INNER_KONGFU_STRING;
		} else {
			// 加力上限
			final int max = Gmud.sPlayer.GetPlusFPMax();
			// 当前加力
			final int last = Battle.sBattle.fighter_data[id_active][0];
			final int cur = UI._fp_mp_plush_menu(max, last, 63, 43);
			if (cur != last) {
				Battle.sBattle.fighter_data[id_active][0] = cur;
				Battle.sBattle.stack_fighterdate_set(id_active, 0, cur);
			}
			if (cur == max) {
				str = String.format(Res.STR_FP_PLUS_LIMIT_STRING, max);
			} else {
				str = null;
			}
		}
		if (str != null) {
			UI.DrawStringFromY(str);
		}
	}

	static void UseFPMenu() {
		int sel = 0;
		int last_key = 0;
		boolean update = true;
		while (true) {
			Input.ProcessMsg();
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else
					sel = 1;
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (sel < 1)
					sel++;
				else
					sel = 0;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				if (sel == 0) {
					DrawPlusFp();
				} else if (sel == 1) {
					String str = Battle.sBattle.Breath();
					UI.DrawStringFromY(str);
				}
				update = true;
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}
			if (update) {
				update = false;
				DrawMain();
				DrawFPMenu(sel);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyDown | Input.kKeyUp
					| Input.kKeyEnt | Input.kKeyExit);
		}
	}

	/** 战斗菜单 */
	static void Main() {
		boolean update = true;
		int last_key = 0;
		while (true) {
			if ((last_key & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else
					menu_id = 5;
				update = true;
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (menu_id < 5)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((last_key & Input.kKeyExit) != 0) {
				menu_id = 0;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				if (menu_id == 0) { // "普通攻击",
					Battle.sBattle.PhyAttack(false);
					break;
				} else if (menu_id == 1) { // "绝招攻击"
					if (MagicMenu() > 0)
						break;
					update = true;
				} else if (menu_id == 2) { // "使用内力"
					UseFPMenu();
					update = true;
				} else if (menu_id == 3) { // "使用物品"
					UI.PlayerItem();
					update = true;
				} else if (menu_id == 4) { // "调整招式"
					UI.PlayerSkill();
					update = true;
				} else if (menu_id == 5) { // "逃跑"
					if (Battle.sBattle.CalcAct()) {
						Battle.sBattle.bEscape = true;
						// Battle.sBattle.b_int_static = -1;
					} else {
						Battle.sBattle.b(2, 36, 1);
					}
					break;
				}
			}
			if (update) {
				update = false;
				DrawMain();
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyEnt | Input.kKeyExit);
		}
	}

	/**
	 * 绘制血条
	 * 
	 * @param cur
	 *            当前血量
	 * @param max
	 *            最大血量
	 * @param full
	 *            满血
	 * @param x
	 *            坐标
	 * @param y
	 * @param show_percent
	 *            是否输出数字，如 99/100
	 */
	static void DrawHPRect(int cur, int max, int full, int x, int y,
			boolean show_percent) {
		if (full <= 0)
			return;
		final int length = 39;
		int len;
		len = cur * length / full;
		if (len > length)
			len = length;
		Video.VideoFillRectangle(x, y, len, 3);

		len = max * length / full;
		if (len > length)
			len = length;
		Video.VideoDrawLine(x, y + 4, x + len, y + 4);

		if (show_percent) {
			String str = String.format("%d/%d", cur, max);
			Video.VideoDrawNumberData(str, x + length + 1, y);
		}
	}

	static void DrawMain() {
		Video.VideoClear();

		final int id_player = Battle.sBattle.m_player_id;
		final int id_rival = (id_player == 0) ? 1 : 0;
		final int x = 3;
		final int x_npc = x + 100;
		int y = 0;

		// draw name
		Video.VideoDrawStringSingleLine(Battle.sBattle.m_player_name, x + 5, y);
		Video.VideoDrawStringSingleLine(Battle.sBattle.m_npc_name, x_npc, y);
		y += Video.SMALL_LINE_H + 2;

		// draw image
		Video.VideoDrawImage(hit_id == id_player ? hit_img : player_img, x + 6,
				y);
		Video.VideoDrawImage(hit_id == id_rival ? hit_img : NPC_img, x_npc + 3,
				y);
		hit_id = -1;
		y += 18;

		final int[] data_player = Battle.sBattle.fighter_data[id_player];
		final int[] data_rival = Battle.sBattle.fighter_data[id_rival];
		final int x_r = x + 12;

		// draw HP y:33
		Video.VideoDrawImage(hp_img, x, y);
		DrawHPRect(data_player[1], data_player[2], data_player[3], x_r, y + 2,
				true);
		DrawHPRect(data_rival[1], data_rival[2], data_rival[3], x_npc + 2,
				y + 2, false);
		y += 9;

		// draw FP
		Video.VideoDrawImage(fp_img, x, y);
		DrawHPRect(data_player[4], data_player[5], data_player[5], x_r, y + 2,
				true);
		DrawHPRect(data_rival[4], data_rival[5], data_rival[5], x_npc + 2,
				y + 2, false);
		y += 9;

		// draw MP
		if (data_player[42] != 255 && data_player[66] == 8) {
			Video.VideoDrawImage(mp_img, x, y);
			DrawHPRect(data_player[6], data_player[7], data_player[7], x_r,
					y + 2, true);
			y += 8;
		}

		// u.a(0);

		final int t_y = y + 1;
		final int t_x = x_r + 36;
		for (int i = 0; i < 6; i++) {
			Video.VideoDrawRectangle(t_x + i * 8, t_y, 6, 6);
			if (i == menu_id) {
				Video.VideoFillRectangle(t_x + i * 8, t_y, 6, 6);
				Video.VideoDrawStringSingleLine(menu_title[i], t_x - 4, t_y + 8);
			}
		}
	}

	/**
	 * 描述过招内容
	 * 
	 * @param attack_type
	 *            出招类型 0:物理攻击 1:技能攻击 2:攻击结果 3:??自定义描述文本于desc_words中 4:伤害描述
	 * @param attack_desc
	 *            描述文本ID
	 */
	static void PhyAttack(int attack_type, int attack_desc) {
		String str_desc;
		int id_active = Battle.sBattle.m_active_id;
		int id_player = Battle.sBattle.m_player_id;
		switch (attack_type) {
		case 0: // 物理攻击
			str_desc = Skill.GetAttackDesc(attack_desc);
			if (id_active == id_player) {
				str_desc = util.ReplaceStr(str_desc, "N", "你");
				str_desc = util.ReplaceStr(str_desc, "SB",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SW",
						Items.item_names[weapon_id[id_active]]);
			} else {
				str_desc = util.ReplaceStr(str_desc, "N",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
				str_desc = util.ReplaceStr(str_desc, "SW",
						Items.item_names[weapon_id[id_active]]);
			}

			// 攻击部位
			String s;
			int hit_point = Battle.sBattle.a_int_array1d_static[3];
			if (hit_point != -1)
				s = GmudData.hit_point_name[hit_point];
			else {
				s = "";
			}
			str_desc = util.ReplaceStr(str_desc, "SP", s);
			break;
		case 1: {
			str_desc = Magic.GetMagicDesc(attack_desc);
			int k2 = Battle.sBattle.a_int_array1d_static[13];
			if (id_active == id_player) {
				str_desc = util.ReplaceStr(str_desc, "SB",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SW",
						Items.item_names[weapon_id[id_active]]);
			} else {
				str_desc = util.ReplaceStr(str_desc, "你",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
				str_desc = util.ReplaceStr(str_desc, "SW",
						Items.item_names[weapon_id[id_active]]);
			}
			if (k2 != -1)
				str_desc = util.ReplaceStr(str_desc, "~", Magic.MAGIC_NAME[k2]);
			break;
		}
		case 2: // hit result
		{
			int j3 = attack_desc & 0xff;
			str_desc = Skill.GetHitDesc(j3);
			int k3 = attack_desc / 256;
			String s2 = "";
			if (k3 > 36)
				s2 = Skill.GetHitDesc(k3);
			if (id_active == id_player) {
				str_desc = util.ReplaceStr(str_desc, "SB",
						Battle.sBattle.m_npc_name);
				if (j3 != 36) {
					str_desc += "(" + Battle.sBattle.m_npc_name + s2 + ")";
				}
				break;
			}
			str_desc = util
					.ReplaceStr(str_desc, "你", Battle.sBattle.m_npc_name);
			str_desc = util.ReplaceStr(str_desc, "SB", "你");
			if (j3 != 36) {
				str_desc += "(你" + s2 + ")";
			}
			break;
		}
		case 3: // ？？？ 特殊描述用于替换资源文件的内容？
			if (attack_desc < desc_words.length)
				str_desc = desc_words[attack_desc];
			else
				str_desc = "";
			if (id_active == id_player) {
				str_desc = util.ReplaceStr(str_desc, "N", "你");
				str_desc = util.ReplaceStr(str_desc, "SB",
						Battle.sBattle.m_npc_name);
			} else {
				str_desc = util.ReplaceStr(str_desc, "N",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
			}
			break;

		case 4: // 伤害描述
			str_desc = Skill.GetHitDesc(attack_desc);
			if (id_active == id_player) {
				str_desc = util.ReplaceStr(str_desc, "SB",
						Battle.sBattle.m_npc_name);
			} else {
				str_desc = util.ReplaceStr(str_desc, "你",
						Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
			}
			break;

		default:
			str_desc = "";
			break;
		}
		DrawMain();
		UI.DrawStringFromY(str_desc);
	}

	/**
	 * 绘制　大招　列表，最多3行
	 * 
	 * @param id_active
	 * @param count
	 * @param top
	 * @param sel
	 */
	static void DrawMagicMenu(int id_active, int count, int top, int sel) {
		final int[] data = Battle.sBattle.a_int_array2d_static[id_active];
		final int lineH = Video.SMALL_LINE_H;
		final int w = lineH * 6 + 2;
		final int h;
		if (count > 3) {
			h = Video.SMALL_LINE_H * 3;
		} else {
			h = Video.SMALL_LINE_H * count;
		}
		int x = 36;
		int y = lineH * 3;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		UI.DrawCursor(x + (lineH - UI.CURSOR_W) / 2, y + sel * lineH
				+ (lineH - UI.CURSOR_H) / 2);
		x += lineH;
		for (int i = 0; i < 3 && i + top < count; i++) {
			final int magic_id = data[top + i];
			if (magic_id < 0 || magic_id > 38)
				break;
			Video.VideoDrawStringSingleLine(Magic.MAGIC_NAME[magic_id], x, y);
			y += lineH;
		}
	}

	/**
	 * 大招菜单
	 * 
	 * @return 0没有使用任何招式 1已成功使用招式
	 */
	static int MagicMenu() {
		final int id_active = Battle.sBattle.m_player_id;
		Magic.Effect(id_active);
		final int count = Battle.sBattle.CountMagicEffect(id_active);
		if (count < 1)
			return 0;
		int top = 0;
		int sel = 0;
		boolean update = true;
		int last_key = 0;
		while (true) {
			if ((last_key & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else if (top > 0)
					top--;
				else if (count > 3) {
					top = count - 3;
					sel = 2;
				} else {
					top = 0;
					sel = count - 1;
				}
				update = true;
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (sel + top + 1 >= count) {
					sel = 0;
					top = 0;
				} else if (sel < 2)
					sel++;
				else
					top++;
				update = true;
			} else if ((last_key & Input.kKeyEnt) != 0) {
				int magic_id = Battle.sBattle.a_int_array2d_static[id_active][top
						+ sel];
				String s = Magic.UseMagic(magic_id);
				if (s.length() == 0) {
					return 1;
				} else {
					UI.DrawStringFromY(s);
					break;
				}
			} else if ((last_key & Input.kKeyExit) != 0) {
				break;
			}

			if (update) {
				update = false;
				DrawMagicMenu(id_active, count, top, sel);
				Video.VideoUpdate();
			}
			last_key = Gmud.GmudWaitNewKey(Input.kKeyDown | Input.kKeyUp
					| Input.kKeyEnt | Input.kKeyExit);
		}
		return 0;
	}
}
