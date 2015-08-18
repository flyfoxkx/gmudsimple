package cn.fmsoft.lnx.gmud.simple.core;

import java.util.Arrays;

import cn.fmsoft.lnx.gmud.simple.core.GmudData.ClassID;
import cn.fmsoft.lnx.gmud.simple.core.NPC.NPCSKILLINFO;

public class task {
	
	/** (size:40) */
	static final int temp_tasks_data[] = new int[40];

	static final String yes_no = "\n([输入]确认 [跳出]放弃)\n";
	private static final String award = "你被奖励了：\n";
	private static final String award_exp = " 点经验 ";
	private static final String award_potential = " 点潜能 ";
	private static final String no_award = "努力吧，干活去吧！";

	// 27
	private static final int item_to_find[] = new int[] {
			1, 2, 3, 4, 5, 6, 7, 8, 9, 11,
			12, 13, 16, 17, 18, 19, 35, 40, 42, 43, 
			46, 48, 49, 72, 73, 74, 76
		};
	private static final String item_start = "今天妾身正准备请人去找o,能否帮个忙？";
	private static final String item_wait = "妾身还盼着您的o呢！";
	private static final String old_woman_task_name[] = new String[]{
			"扫地", "挑水", "劈柴"
		};
	private static final String old_woman_start = "老身年事已高，有好心人帮帮我『o』吗？";
	private static final String old_woman_wait = "老身吩咐你的事做完了么？";
	private static final String old_woman_unable = "唉！你也小有名气了，老身使唤不动你了！";
	static final String old_woman_award = "你被奖励了：20点实战经验 10点潜能 50金钱";
	private static final String kill_start = "老夫夜观天象，o阳寿已尽，你去解决他!";
	private static final String kill_wait = "老夫不是让你解决o吗？";
	private static final String exist_award = "看你红光满面，还是先去顾炎武处领赏吧！";
	private static final String talk_start = "请速去拜见o";
	private static final String talk_wait = "老夫不是说过请去拜见o吗！";
	private static final String task_finish = "你完成了任务，去顾炎武处领赏吧！";
	private static final String bad_man_start = "近有恶人『o』在m为非作歹，请速去为民除害！";
	private static final String bad_man_wait = "在下不是叫你去收服o吗？";
	static final String bad_man_nothing = "本镇治安良好";
	static final String bad_man_doing = "本镇正在缉拿人犯『o』";
	private static final String bad_man_family_name[] = new String[]
	{"赵", "钱", "孙", "李", "周", "吴", "郑", "王"};
	private static final String bad_man_name[] = new String []
	{"一", "二", "三", "四", "五", "六", "七", "八"};
	static final int bad_man_skill[] = new int[50];
	static int bad_man_mapid = -1;
	
	static void reset() {
		Arrays.fill(bad_man_skill, 0);
		bad_man_mapid = -1;
	}

	/** 打哈哈的对话 */
	private static void CommonDialog() {
		Gmud.sMap.DrawMap(-1);
		int type = util.RandomInt(5);
		final String s1 = UI.readDialogText(type);
		final String s2 = s1.replaceAll("\\$o", UI.npc_name);
		UI.DrawDialog(s2.replaceAll("\\$n", Gmud.sPlayer.player_name));
	}

	static void Talk(int npc_id) {
		// 村长任务正在进行
		if (temp_tasks_data[9] == 1 && temp_tasks_data[10] == 0
				&& npc_id == temp_tasks_data[8]) {
			Gmud.sMap.DrawMap(-1);
			UI.ShowDialog(5); // “我知道了，多谢来访”

			// 标记已达成目标
			temp_tasks_data[10] = 1;
			return;
		}

		// 恶人
		if (npc_id == 179) {
			Gmud.sMap.DrawMap(-1);
			UI.DrawDialog("小兔崽子儿，我看你是不想活了！！");
			return;
		}

		final int talk_type = NPCINFO.NPC_attribute[npc_id][0];
		if (talk_type == 0) {
			// 打哈哈的对话
			CommonDialog();
		} else if (talk_type > 0) {
			// (特别点的)普通 NPC 对话
			Gmud.sMap.DrawMap(-1);
			final String s1 = UI.readDialogText(talk_type);
			String s2 = s1.replaceAll("\\$o", UI.npc_name);
			UI.DrawDialog(s2.replaceAll("\\$n", Gmud.sPlayer.player_name));
		} else if (talk_type < 0) {
			// 任务型NPC
			Gmud.sMap.DrawMap(-1);
			SpecialDialog(npc_id);
		}
	}

//	extern bool RandomBool(int);

	/**
	 * 检查物品型任务NPC
	 * 
	 * @param item_id
	 *            任务物品ID
	 * @param item_count
	 *            物品个数
	 * @param just_eq
	 *            是否要求数量刚好，如果为TRUE则多了或少了都不行
	 * @param tip_id
	 *            任务对话文本ＩＤ
	 * @return 是否成功提交任务，如果成功了则直接删除任务物品
	 */
	private final static boolean _check_task_npc_item(int item_id,
			int item_count, boolean just_eq, int tip_id) {
		final int index = Gmud.sPlayer.ExistItem(item_id, item_count);
		if (index >= 0
				&& (!just_eq || Gmud.sPlayer.item_package[index][2] == item_count)) {
			String tip = UI.readDialogText(tip_id) + yes_no;
			int last_key = UI.DialogBx(tip, UI.TITLE_X, 0);
			last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
			if ((last_key & Input.kKeyEnt) != 0) {
				// 失去物品
				Gmud.sPlayer.LoseItem(index, item_count);
				return true;
			}
		}
		return false;
	}

	static void SpecialDialog(int talk_type) {
		
		switch (talk_type) {
		default:
			break;
		case 1: // 小顽童
		{
			int tip_id = util.RandomBool(80) ? 26 : 27;
			UI.ShowDialog(tip_id);

			// 刚好2个 "糖葫芦"，换老花镜
			if (_check_task_npc_item(6, 2, true, 28)) {
				Gmud.sPlayer.GainOneItem(40);
			}
			break;
		}

		case 27: // 小书童
		{
			int tip_id = util.RandomInt(3) + 36;
			UI.ShowDialog(tip_id);
			return;
		}

		case 29: // 老裁缝
		{
			UI.ShowDialog(40);
			// 老花镜换精制布衣
			if (_check_task_npc_item(40, 1, false, 41)) {
				Gmud.sPlayer.GainOneItem(43);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(42);
			}
			break;
		}

		case 19: // 荷西
		{
			UI.ShowDialog(33);
			if (_check_task_npc_item(74, 30, false, 34)) {
				Gmud.sPlayer.GainOneItem(33);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(35);
			}
			break;
		}

		case 4: // 厨师
		{
			UI.ShowDialog(30);

			// "猪肉"
			if (_check_task_npc_item(5, 1, false, 31)) {
				Gmud.sPlayer.GainOneItem(12);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(32);
			}
			break;
		}

		case 36: // 盐商
		{
			UI.ShowDialog(45);

			// "海外仙丹"
			if (_check_task_npc_item(10, 1, false, 46)) {
				Gmud.sPlayer.GainOneItem(62);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(47);
			}
			break;
		}
			
		case 46: // 平阿四
		{
			final int tip_id = util.RandomBool(80) ? 48 : 49;
			UI.ShowDialog(tip_id);

			// "焦黄纸页"
			if (_check_task_npc_item(69, 1, false, 50)) {
				Gmud.sPlayer.GainOneItem(68);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(51);
			}
		}
			break;

		case 89: // 食野太郎
		{
			final int tip_id = 59 + util.RandomInt(3);
			UI.ShowDialog(tip_id);

			// "包子"
			if (_check_task_npc_item(2, 10, false, 62)) {
				Gmud.sPlayer.GainOneItem(70);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(63);
			}
		}
			break;

		case 67: // 小红
		{
			final int tip_id = 54 + util.RandomInt(2);
			UI.ShowDialog(tip_id);

			// "白玉萧"
			if (_check_task_npc_item(29, 1, false, 56)) {
				Gmud.sPlayer.GainOneItem(28);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(57);
			}
		}
			break;

		case 63: // 司棋
		{
			final int tip_id = 64 + util.RandomInt(2);
			UI.ShowDialog(tip_id);

			if (_check_task_npc_item(72, 1, false, 66)) {
				Gmud.sPlayer.GainOneItem(71);
				Gmud.sMap.DrawMap(-1);
				UI.ShowDialog(67);
			}
		}
			break;

		case 0: // 阿庆嫂
		{
			UI.ShowDialog(24 + util.RandomInt(2));
		}
			break;

		case 110: // 白瑞德
		{
			// "王蛇胆"
			int index = Gmud.sPlayer.ExistItem(88, 1);
			if (index >= 0 && Gmud.sPlayer.isClass(ClassID.XueShan)) {
				// "雪山剑法" 的级别 >= 150 "基本剑术">60
				if (Gmud.sPlayer.GetSkillLevel(38) >= 150
						&& Gmud.sPlayer.GetSkillLevel(2) > 60) {
					UI.ShowDialog(21);
					Gmud.sPlayer.LoseItem(index, 1);
					Gmud.sPlayer.lasting_tasks[0] = 1;
					break;
				}
			}
			CommonDialog();
		}
			break;
		
		case 2: //捕快
			KillWanted();
			break;

		case 30: // 顾炎武
			GetAward();
			break;

		case 5: // 村长
			FindPeople();
			break;

		case 9: // 中年妇人
			FindItem();
			break;

		case 25: // 平一指
			KillPeople();
			break;

		case 24: // 老婆婆
			OldWoman();
			break;

		case 13: // 石料管事"
		{
			final int tip_id;
			if (Gmud.sPlayer.exp < 1000) {
				tip_id = 13;// less exp
			} else if (Gmud.sPlayer.exp > 20000) {
				tip_id = 14; // more exp
			} else if (temp_tasks_data[25] == 1) {
				tip_id = 12; // 存在石料
			} else if (temp_tasks_data[29] < 240 && temp_tasks_data[26] > 0) {
				tip_id = 16; // 不需要
			} else if (Gmud.sPlayer.GainOneItem(87)) {
				tip_id = 17;
				temp_tasks_data[25] = 1;
				temp_tasks_data[26] += 1;
				if (temp_tasks_data[27] < 40)
					temp_tasks_data[27] = 40;
				else
					// TODO: 石料奖励与悟性和福缘相关，降低难度。
					temp_tasks_data[27] += util.RandomInt(10 + (Gmud.sPlayer
							.GetSavvy() + Gmud.sPlayer.bliss) / 2) + 1;
				if (temp_tasks_data[28] < 20)
					temp_tasks_data[28] = 20;
				else
					temp_tasks_data[28] += util.RandomInt(5 + (Gmud.sPlayer
							.GetSavvy() + Gmud.sPlayer.bliss) / 2) + 1;
				temp_tasks_data[29] = 0;
			} else {
				tip_id = 15;
				temp_tasks_data[25] = 0;
			}
			UI.ShowDialog(tip_id);
		}
			break;

		case 14: // 工地管事
		{
			final int tip_id;
			if (temp_tasks_data[25] == 0) {
				tip_id = 18;
			} else {
				int index = Gmud.sPlayer.ExistItem(87, 1);
				if (index >= 0) {
					Gmud.sPlayer.LoseItem(index, 1);
					Gmud.sPlayer.exp += temp_tasks_data[27];
					Gmud.sPlayer.potential += temp_tasks_data[28];
					tip_id = 20;
				} else {
					tip_id = 19;
				}
				temp_tasks_data[25] = 0;
			}
			UI.ShowDialog(tip_id);
			if (tip_id == 20) {
				// 提示奖励的内容
				String str = award + temp_tasks_data[27] + award_exp
						+ temp_tasks_data[28] + award_potential;
				UI.DrawDialog(str);
			}
		}
			break;

		case 136: // 华岳
		{
			final int tip_id;
			// 50 0000
			if (Gmud.sPlayer.money >= 0x7a120) {
				Gmud.sPlayer.money -= 0x7a120;
				Gmud.sPlayer.bliss += 1;
				if (Gmud.sPlayer.bliss > 90)
					Gmud.sPlayer.bliss = 90;
				tip_id = 23;
			} else {
				tip_id = 22;
			}
			UI.ShowDialog(tip_id);
		}
			break;

		case 145: // 干匠
		{
			if (Gmud.sPlayer.lasting_tasks[2] != 0) {
				// "呵呵，我的铸剑谷随时对你开放！"
				UI.ShowDialog(195);
				Gmud.sMap.LoadMap(87);
				Gmud.sMap.SetPlayerLocation(0, 4);
				Gmud.sMap.DrawMap(0);
				return;
			}
			if (Gmud.sPlayer.exp < 0x186a0) {
				// "欢迎来到铸剑谷！"
				UI.ShowDialog(109);
				return;
			}
			// "作为铸剑师，不但要精通铸造技能，还要了解每种武器的性能。"
			UI.ShowDialog(196);
			// "你也想进我的铸剑谷吗？"
			UI.ShowDialog(88);

			// PK "墨邪"
			PKMoxie();
		}
			break;

		case 156:    //月下老人
		{
			if (Gmud.sPlayer.isClass(ClassID.MaoShan)) {
				UI.DrawDialog(Res.STR_MATCHMAKER_MAOSHAN);
				return;
			}
			if (6 > Gmud.sPlayer.GetAge()) {
				UI.DrawDialog(Res.STR_MATCHMAKER_TOO_YOUNG);
				return;
			}
			if (0 == Gmud.sPlayer.lasting_tasks[6]) {
				UI.DrawDialog(Res.STR_MATCHMAKER_NEED_HOUSE);
				return;
			}
			String str = "请选择下列选项: 结婚: 1.求婚 2.允婚 离婚: 3.分道 4.扬镳";
			UI.DrawDialog(str);
			return;
		}
			/*
			你已有家室还来结婚？重婚是犯罪的!
			你尚未婚配就来离婚，想拿老夫开涮吗？

			恭喜恭喜,你结婚了! 

			有人向你求婚『name』是一位age岁的gender性,看上去face,同意吗?y/n
			 
			请选择下列选项: 结婚: 1.求婚 2.允婚 离婚: 3.分道 4.扬镳    (请按数字键选择)  
			  
			你决定求婚，现在正怀着忐忑的心情等待对方的答复... 

			请检查线路! 

			落花有意流水无情,此事只好作罢, 
			真情不在朝朝暮暮,此事从长计议!  

			别开玩笑,你们性别相同耶!

			name同意了,此乃天作之合，祝你们白头到老
			 
			你决定与name断绝夫妻情分，现在正等待对方答复...

			唉，如此你我情断义绝，多自珍重吧。 
			如此了断倒也干脆，离婚了就别在来找我。

			一日夫妻百日恩，还是不逞一时意气了吧。
			床头吵架床尾和，日子还是要过的。

			你爱人根本就不是这个人,别人家里的事你凑什么热闹!  

			世事艰辛，这日子...还能过到一块儿去吗, 不过了? y/n  

			非法请求!  

			你拒绝了对方的请求!
			*/

		case 157: //老管家
			CommonDialog();
			//o.d("您来啦！~");  //家具 翻修 销毁
			return;
		case 6: //独行大侠
			CommonDialog();
			break;
		}	
	}

	private final static int[] PK_MOXIE_WEAPON_ID = new int[] { 11, 17, 21, 22 };
	private final static String[] PK_MOXIE_WEAPON_NAME = new String[] { "刀",
			"剑", "杖", "鞭" };
	private final static String PK_MOXIE_FAILED = "莫邪哼了一声：我们考的是SW，你这算什么";

	/** PK "墨邪" */
	private static void PKMoxie() {
		final Player player = Gmud.sPlayer;
		final Battle battle = new Battle(146, 231, 1);
		final int last_weapon = player.GetWeaponID();
		Battle.sBattle = battle;
		int index;
		for (int i = 0; i < 4; i++) {
			final int weapon_id = PK_MOXIE_WEAPON_ID[i];
			if (player.GainOneItem(weapon_id)) {
				// "干匠递给你一把大砍刀，说道：先让我的夫人墨邪试试你对刀的掌握吧"
				UI.ShowDialog(90 + i);

				// 给玩家自动装备上武器
				index = player.ExistItem(weapon_id, 1);
				player.EquipWeapon(index);

				NPC.ResetData(146);
				battle.BattleMain();
				Input.ClearKeyStatus();
				Gmud.sMap.DrawMap(-1);

				boolean failed = false;
				//if (player.hp < player.hp_max / 2 ) {
				// 玩家血量不及上限一半，失败
				if (NPC.NPC_attrib[146][11] >= NPCINFO.NPC_attribute[146][12] / 2) {
					UI.ShowDialog(95);
					failed = true;
				} else if (player.equips[15] != weapon_id) {
					// 玩家当前装备的武器必须是 "钢刀"
					String str = util.ReplaceStr(PK_MOXIE_FAILED, "SW",
							PK_MOXIE_WEAPON_NAME[i]);
					UI.DrawDialog(str);
					failed = true;
				}

				// 取回送给玩家的装备
				player.UnEquipWeapon();
				index = player.ExistItem(weapon_id, 1);
				if (index != -1)
					player.DeleteOneItem(index);
				if (failed)
					break;
			} else {
				// "你身上的东西太多了，什么也拿不起来来"
				UI.ShowDialog(89);
				break;
			}

			// 挑战成功
			if (i == 3) {
				UI.ShowDialog(94);
				player.lasting_tasks[2] = 1;
				Gmud.sMap.LoadMap(87);
				Gmud.sMap.SetPlayerLocation(0, 4);
				Gmud.sMap.DrawMap(0);
			} else {
				UI.ShowDialog(96);
			}
		}

		// delete glpBattle;
		// glpBattle = 0;
		Battle.sBattle = null;

		// 恢复玩家的武器
		index = player.ExistItem(last_weapon, 1);
		if (index != -1)
			player.EquipWeapon(index);
	}

	/** 检查是否有 交接任务但没有领赏 */
	private static int check_award() {
		for (int i = 0; i < 3; i++) {
			if (temp_tasks_data[11 + i * 5] == 1) {
				return i;
			}
		}
		return -1;
	}

	/** 向顾炎武领取奖励 */
	private static void GetAward() {
		final String tip;
		final int[] data = temp_tasks_data;
		final int id = check_award();
		if (id >= 0) {
			final int pos = id * 5;
			int j8 = data[pos + 12] + util.RandomInt(Gmud.sPlayer.bliss);
			if (id < 2)
				j8 = data[12] + data[17];
			if (util.RandomBool(75)) {
				Gmud.sPlayer.exp += j8;
				tip = award + j8 + award_exp;
			} else {
				Gmud.sPlayer.potential += j8
						+ util.RandomInt(Gmud.sPlayer.bliss / 2);
				tip = award + j8 + award_potential;
			}
			data[pos + 8] = 0;
			data[pos + 9] = 0;
			data[pos + 10] = 0;
			data[pos + 11] = 0;
		} else {
			tip = no_award;
		}
		UI.DrawDialog(tip);
	}

	/** 中年妇女任务 */
	static void FindItem() {
		if (check_award() >= 0) {
			UI.DrawDialog(exist_award);
			return;
		}

		int item_id;
		if (temp_tasks_data[14] != 0) {
			final String tip;
			item_id = temp_tasks_data[13];
			final int index = Gmud.sPlayer.ExistItem(item_id, 1);
			if (index >= 0) {
				Gmud.sPlayer.LoseItem(index, 1);
				temp_tasks_data[15] = 1;
				temp_tasks_data[16] = 1;
				tip = task_finish;
			} else {
				String item_name = Items.item_names[item_id];
				tip = item_wait.replaceAll("o", item_name);
			}
			UI.DrawDialog(tip);
			return;
		}

		if (Gmud.sPlayer.exp < 3000)
			item_id = item_to_find[util.RandomInt(27)];
		else
			item_id = util.RandomInt(75) + 1;

		temp_tasks_data[13] = item_id;
		temp_tasks_data[14] = 1;
		temp_tasks_data[15] = 0;
		temp_tasks_data[16] = 0;
		temp_tasks_data[17] = util.RandomInt(20 + Gmud.sPlayer.GetSavvy() * 3);
		if (temp_tasks_data[17] < 5)
			temp_tasks_data[17] = 5;

		String item_name = Items.item_names[item_id];
		String str = item_start.replaceAll("o", item_name);
		UI.DrawDialog(str);
	}

	/** 村长任务 */
	static void FindPeople() {
		// 如果经验大于 80000(0x13880) 并且未开始打坛，身上没有图，则送一张 79青龙坛地图
		if (Gmud.sPlayer.exp > 0x13880 && Gmud.sPlayer.lasting_tasks[1] == 0
				&& Gmud.sPlayer.ExistItem(79, 1) < 0) {
			Gmud.sPlayer.GainOneItem(79);
			UI.ShowDialog(158);
			return;
		}

		// 检查是否有没有领赏，则提示先领
		if (check_award() >= 0) {
			UI.DrawDialog(exist_award);
			return;
		}

		// 已经领任务了
		if (temp_tasks_data[9] != 0) {
			String tip;
			if (temp_tasks_data[10] != 0) {
				temp_tasks_data[11] = 1;
				tip = task_finish;
			} else {
				final String npc_name = NPC.NPC_names[temp_tasks_data[8]];
				tip = talk_wait.replaceAll("o", npc_name);
			}
			UI.DrawDialog(tip);
			return;
		}

		// 计算找人范围
		int exp = Gmud.sPlayer.exp;
		if (exp == 0)
			exp = 100;
		int max;
		if (exp > 1000)
			max = 146;
		else
			max = 37 / (1000 / exp);
		if (max == 0)
			max = 1;

		// 随机产生 要找的npc，跳过 5村长 9中年妇女 30顾炎武
		int npc_id;
		do {
			npc_id = util.RandomInt(max);
		} while (npc_id == 5 || npc_id == 9 || npc_id == 30);

		temp_tasks_data[8] = npc_id;
		temp_tasks_data[9] = 1;
		temp_tasks_data[10] = 0;
		temp_tasks_data[11] = 0;
		temp_tasks_data[12] = util.RandomInt(20 + Gmud.sPlayer.GetSavvy() * 3);
		if (temp_tasks_data[12] < 5)
			temp_tasks_data[12] = 5;

		String npc_name = NPC.NPC_names[temp_tasks_data[8]];
		String str = talk_start.replaceAll("o", npc_name);
		UI.DrawDialog(str);
	}

	/** 老婆婆任务 */
	static void OldWoman() {
		final String tip;
		if (Gmud.sPlayer.exp > 5000) {
			tip = old_woman_unable;
		} else {
			if (temp_tasks_data[24] == 1) {
				tip = old_woman_wait;
			} else {
				int task_id = util.RandomInt(3);
				temp_tasks_data[23] = task_id;
				temp_tasks_data[24] = 1;
				String task_name = old_woman_task_name[task_id];
				tip = old_woman_start.replaceAll("o", task_name);
			}
		}
		UI.DrawDialog(tip);
		return;
	}

	/** 完成老婆婆任务 */
	static void OldWomanFinish(int task_id, int tip, int hp_cost) {
		if (temp_tasks_data[23] == task_id && temp_tasks_data[24] != 0) {
			if (Gmud.sPlayer.hp < 10 + hp_cost) {
				UI.ShowDialog(136);
			} else {
				for (int i = 0; i < 4; i++) {
					String str = UI.readDialogText(tip + i);
					UI.DrawTalk(str);
					Video.VideoUpdate();
					Gmud.GmudDelay(900);
				}

				UI.ShowDialog(135);
				Gmud.sPlayer.exp += 20;
				Gmud.sPlayer.potential += 10;
				Gmud.sPlayer.money += 50;
				Gmud.sPlayer.hp -= hp_cost;
				UI.DrawDialog(task.old_woman_award);
			}
			temp_tasks_data[24] = 0;
		}
	}

	/** 平一指任务 */
	static void KillPeople() {
		if (check_award() >= 0) {
			UI.DrawDialog(exist_award);
			return;
		}

		if (temp_tasks_data[19] != 0) {
			final String tip;
			if (temp_tasks_data[20] != 0) {
				temp_tasks_data[21] = 1;
				tip = task_finish;
			} else {
				final int npc_id = temp_tasks_data[18];
				final String npc_name = NPC.NPC_names[npc_id];
				tip = kill_wait.replaceAll("o", npc_name);
			}
			UI.DrawDialog(tip);
			return;
		}

		// 根据玩家等级随机选择合适的目标
		int level = (Gmud.sPlayer.GetSkillAverageLevel() + 5) / 5;
		int max = GmudData.GetKillTaskTable(level);
		if (max == 0) {
			return;
		}
		final int npc_index = util.RandomInt(max);
		final int npc_id = GmudData.kill_task_temp_table[npc_index];
		temp_tasks_data[18] = npc_id;
		temp_tasks_data[19] = 1;
		temp_tasks_data[20] = 0;
		temp_tasks_data[21] = 0;
		temp_tasks_data[22] = util.RandomInt(Gmud.sPlayer.GetSavvy() * 4
				+ Gmud.sPlayer.SetFaceLevel() * 2);

		String npc_name = NPC.NPC_names[npc_id];
		String str = kill_start.replaceAll("o", npc_name);
		UI.DrawDialog(str);
		return;
	}

	/** 捕快任务 */
	static void KillWanted()
	{
		// 如果有恶人并且不在25分钟时间点上，就提示有恶人
//		if (temp_tasks_data[0] != 0 && temp_tasks_data[5] / 30 != 49)
		if (temp_tasks_data[0] != 0 && temp_tasks_data[5]<600)
		{
//			wstring str(bad_man_wait);
//			UI.DrawDialog(&ReplaceStr(&str, L"o", NPC.NPC_names[179]));
			String str = bad_man_wait.replaceAll("o", NPC.NPC_names[179]);
			UI.DrawDialog(str);
			return;
		}
		//　如果时间不到300s且有杀过恶人，就提示暂无恶人
		if (temp_tasks_data[5] < 300 && temp_tasks_data[1] > 0)
		{
//			wstring str(bad_man_nothing);
//			UI.DrawDialog(&str);
			UI.DrawDialog(bad_man_nothing);
			return;
		}
		// 累计恶人难度，以10为循环
		temp_tasks_data[34] += 1;
		temp_tasks_data[34] %= 10;
		// 如果有恶人且时间在25分钟，则将恶人难度归0————25分法？
		if(49 == temp_tasks_data[5] / 30 && 1 == temp_tasks_data[0])
			temp_tasks_data[34] = 0;
		
		// 随机恶人的地图位置
		int i1 = util.RandomInt(9);    //where
		temp_tasks_data[2] = i1;   
		while((task.bad_man_mapid = GmudData.bad_man_map[i1][util.RandomInt(16)]) == 0);   //set map image&event
		// 随机恶人的名字
		int l1 = util.RandomInt(8);  //random name 1
		int i2 = util.RandomInt(8);  //random name 2
		/*/
		wstring s1(bad_man_family_name[l1]); 
		s1 += bad_man_name[i2];
		wchar_t* tp = new wchar_t[4];
		wcscpy(tp, s1.c_str());      //set obj name
		NPC.NPC_names[179] = tp;
		//*/
		NPC.NPC_names[179] = bad_man_family_name[l1] + bad_man_name[i2];
		//wcscpy(NPC.NPC_names[179], s1.c_str()); 
		
		// 随机恶人原型，用于取技能
		int j2;
		int k2 = j2 = util.RandomInt(20);
		j2 = GmudData.bad_man_based_npc[j2];
		
		// 清除物品
		//equip clean
		for (int l2 = 0; l2 < 5; l2++)
			NPC.NPC_item[179][l2] = 0;
		
		// 给布衣和武器
		NPC.NPC_item[179][0] = 42;
		NPC.NPC_item[179][1] = GmudData.bad_man_weapon[k2];  //weapon
		
		// 复制属性
		for (int i3 = 0; i3 < 18; i3++)
			NPC.NPC_attrib[179][i3] = NPCINFO.NPC_attribute[j2][i3];

		Battle.sBattle = new Battle(0, 0, 1);
		Battle.sBattle.CopyPlayerData();
		NPC.NPC_attrib[179][5] = (Gmud.sPlayer.GetForce() - 5) + temp_tasks_data[34] * temp_tasks_data[1] + temp_tasks_data[1];
		NPC.NPC_attrib[179][6] = (Gmud.sPlayer.GetAgility() - 5) + (temp_tasks_data[34] * temp_tasks_data[1]) / 2 + temp_tasks_data[1] / 2;
		NPC.NPC_attrib[179][7] = (Gmud.sPlayer.GetSavvy() - 5) + temp_tasks_data[34] * temp_tasks_data[1] + temp_tasks_data[1];
		NPC.NPC_attrib[179][8] = (Gmud.sPlayer.GetAptitude() - 5) + temp_tasks_data[34] * temp_tasks_data[1] + temp_tasks_data[1];
		NPC.NPC_attrib[179][9] = Battle.sBattle.CalcHit(0);
		NPC.NPC_attrib[179][10] = Battle.sBattle.CalcAvoid(0);
		NPC.NPC_attrib[179][13] = Gmud.sPlayer.fp_level / 2 + (temp_tasks_data[1] * Gmud.sPlayer.fp_level) / 5;
		NPC.NPC_attrib[179][14] = Gmud.sPlayer.fp_level / 2 + (temp_tasks_data[1] * Gmud.sPlayer.fp_level) / 5;
		NPC.NPC_attrib[179][16] = util.RandomInt(temp_tasks_data[1] * 300 + temp_tasks_data[34] * temp_tasks_data[1] * 200) + 90;
		
		// 累计除恶次数
		temp_tasks_data[1] += 1;
		
		// 设置恶人的经验
		NPC.NPC_attrib[179][3] = 100 + temp_tasks_data[1] * (Gmud.sPlayer.exp / 10);
		// 如果玩家有内力，则生成加力
		if (Gmud.sPlayer.fp_level > 0)
			NPC.NPC_attrib[179][4] = temp_tasks_data[1] + temp_tasks_data[34] * temp_tasks_data[1];
		else
			NPC.NPC_attrib[179][4] = 0;
		NPC.NPC_attrib[179][1] = 0;
		NPC.NPC_attrib[179][0] = 77;
		int j3 = Gmud.sPlayer.hp_full / 2 + (temp_tasks_data[1] * Gmud.sPlayer.hp_full) / 4;
		NPC.NPC_attrib[179][11] = NPC.NPC_attrib[179][12] = NPC.NPC_attrib[179][15] = j3;
		// 取玩家最猛的一个技能的等级
		int k3 = Gmud.sPlayer.GetMaxSkillLevel();
		Battle.sBattle.CalcFighterLevel(0);
		// 如果玩家整体等级大于20
		if (Battle.sBattle.fighter_data[0][62] > 20)
			k3 -= 10;
//		delete glpBattle;
//		glpBattle = 0;
		Battle.sBattle = null;
		// 计算恶人的每个技能等级（都是一样等级）
		int i4 = k3 + (temp_tasks_data[34] * temp_tasks_data[1]) / 2 + (temp_tasks_data[1] - 1) * 3;
		if (temp_tasks_data[1] > 7)
			i4 -= 5;
		if (i4 > 250)
			i4 = 250;
		if (i4 < 0)
			i4 = 0;
		// 取恶人原型的技能表
		NPCSKILLINFO nsk= new NPCSKILLINFO();
		NPC.GetNPCSkill(nsk, j2);

		int j4 = nsk.data[0];
		bad_man_skill[0] = j4;
		for (int k4 = 0; k4 < j4; k4++)
		{
			bad_man_skill[1 + k4 * 2] = nsk.data[1 + k4 * 2];
			bad_man_skill[1 + k4 * 2 + 1] = i4;
		}
//		free(nsk.data);
		// 实战经验奖励
		temp_tasks_data[3] = util.RandomInt(Gmud.sPlayer.bliss*6+Gmud.sPlayer.GetSavvy() * 2 + Gmud.sPlayer.SetFaceLevel() * 4 + temp_tasks_data[1] * 40) + temp_tasks_data[1] * 100;
		// 潜能奖励(与福缘关联起来)
		temp_tasks_data[4] = util.RandomInt(Gmud.sPlayer.bliss*3+Gmud.sPlayer.GetSavvy() + Gmud.sPlayer.SetFaceLevel() * 2 + temp_tasks_data[1] * 20) + temp_tasks_data[1] * 50;
//		wstring str(bad_man_start);
//		UI.DrawDialog(&ReplaceStr(&ReplaceStr(&str, L"o", NPC.NPC_names[179]), L"m", GmudData::map_name[temp_tasks_data[2]]));
		String str = bad_man_start.replaceAll("o", NPC.NPC_names[179]);
		UI.DrawDialog(str.replaceAll("m", GmudData.map_name[temp_tasks_data[2]]));
		// 如果是25分法，就增加一些特别奖励
		if(49 == temp_tasks_data[5] / 30 && 1 == temp_tasks_data[0])
		{
			temp_tasks_data[3] |= 0x61E;
			temp_tasks_data[4] |= 0x30F;
		}
		temp_tasks_data[5] = 0;
		temp_tasks_data[0] = 1;
		Map.NPC_flag[179] = 0;
	}
}
