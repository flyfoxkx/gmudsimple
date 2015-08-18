package cn.fmsoft.lnx.gmud.simple.core;

public class Magic {
	private static final int MAGIC_COST[] = new int[] {
			200, 75, 75, 80, 180, 160, 100, 175, 160, 80, 
			165, 20, 30, 50, 80, 20, 30, 100, 80, 20, 
			30, 60, 90, 80, 150, 100, 190, 180, 100, 150, 
			150, 120, 160, 70, 110, 180, 200, 180, 160
		};

	/** size[39] */
	static final String MAGIC_NAME[] = new String[] {
	/* 0 */"流星飞掷", "雷动九天", "红莲出世", "冰心决", "雪花六出",
	/* 5 */"神倒鬼跌", "三花", "落英缤纷", "柳浪闻莺", "变熊术",
	/* 10 */"猛虎啸", "闪光弹", "雷火弹", "掌心雷", "连珠雷",
	/* 15 */"火焰弹", "烈焰球", "三味火", "火风暴", "寒冰弹",
	/* 20 */"雾棱镖", "冰凌剑", "暴风雪", "化掌为刀", "八卦刀影掌",
	/* 25 */"忍术烟幕", "忍法影分身", "旋风三连斩", "迎风一刀斩", "震字决",
	/* 30 */"挤字决", "乱环决", "阴阳决", "缠绵决", "连字决",
	/* 35 */"三环套月", "八阵刀影掌", "飞鹰召唤", "变鹰术" };

	private static final String NEED_WAIT = "刚用完外功，还是歇歇吧.";

	// [102]
	private static final int MAGIC_DESC[]= new int[]{
		0,114,162,266,370,460,571,625,719,808,
		909,1001,1093,1189,1297,1394,1491,1582,1683,1755,
		1859,1924,2009,2117,2203,2307,2372,2483,2534,2617,
		2701,2811,2912,2989,3072,3128,3199,3220,3315,3413,
		3500,3616,3714,3796,3895,3967,4063,4158,4265,4357,
		4459,4542,4610,4745,4808,4901,4994,5078,5137,5202,
		5255,5305,5355,5396,5434,5475,5570,5680,5768,5869,
		5916,5981,6067,6117,6173,6226,6273,6305,6370,6435,
		6557,6604,6654,6725,6775,6843,6911,6973,7011,7064,
		7096,7128,7217,7267,7356,7406,7448,7535,7599,7641,
		7665,7703
	};

//	extern bool RandomBool(int);
//	extern int util.RandomInt(int);

	static String GetMagicDesc(int id) {
		if (id < 0 || id > 100) {
			return "";
		}
		return Res.readtext(3, MAGIC_DESC[id], MAGIC_DESC[1 + id]);
	};
	
	/** 使用绝招，返回描述文本 */
	static String UseMagic(int magic_id) {
		String str = "";
		int id_active = Battle.sBattle.m_active_id;
		int id_rival = id_active == 0 ? 1 : 0;
		int fp = Battle.sBattle.fighter_data[id_active][4];
		int mp = Battle.sBattle.fighter_data[id_active][6];
		Battle.sBattle.b_int_array1d_static[13] = magic_id;
		if (magic_id >= 11 && magic_id <= 22) {
			if (mp < MAGIC_COST[magic_id]) {
				return "你的法力不足";
			}
		} else if (fp < MAGIC_COST[magic_id]) {
			return "你的内力不足";
		}
		switch (magic_id) {
		case 23: // 化掌为刀
			{
				int k2 = 0;
				int l2 = 0;
				if (TestSelectedSkillLevel(id_active, 0, 12, 1) > 0)
				{
					// 八卦游身掌 + 混元一气功
					int j3 = TestSelectedSkillLevel(id_active, 0, 12, 105);
					if (j3 < 0)
						return NeedMoreLevel(12);
					int k1 = TestSelectedSkillLevel(id_active, 3, 14, 105);
					if (k1 < 0)
						return NeedMoreLevel(14);
					j3 = Calc1(id_active, 0, 12);
					k2 = 5 + (j3 - 90) / 10;
					l2 = 10 + (j3 - 90) / 2;
				} else
				{
					// 八阵八卦掌 + 混元一气功
					int k3 = TestSelectedSkillLevel(id_active, 0, 13, 135);
					if ((k3) < 0)
						return NeedMoreLevel(13);
					int l1 = TestSelectedSkillLevel(id_active, 3, 14, 135);
					if ((l1) < 0)
						return NeedMoreLevel(14);
					k3 = Calc1(id_active, 0, 13);
					k2 = 12 + (k3 - 90) / 10;
					l2 = 10 + (k3 - 90);
			}
			boolean flag1 = Battle.sBattle.a(id_active, 0, k2, -1, -1, 1, 10)
					&& Battle.sBattle.a(id_active, 1, l2, -1, -1, -1, 10);
			if (!flag1)
					return NEED_WAIT;
				Battle.sBattle.b(1, 0, 1);
				break;
			}
		case 6: //三花
			{
				int l3;
				if ((l3 = TestSelectedSkillLevel(id_active, 3, 20, 90)) < 0)
					return NeedMoreLevel(20);
				boolean flag2 = false;
				l3 = Calc1(id_active, 3, 20);
				int i4 = 20 + (l3 - 90) / 5;
				int j4 = 10 + (l3 - 90) / 10;
				if (!(flag2 = (flag2 = Battle.sBattle.a(id_active, 2, i4, -1, -1, 5, 8)) && Battle.sBattle.a(id_active, 3, j4, -1, -1, -1, 8)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 4, 1);
				break;
			}
		case 2: //红莲出世
			{
				int k4 = TestSelectedSkillLevel(id_active, 3, 25, 120);
				if (k4 < 0)
					return NeedMoreLevel(25);
				int l5 = 0;
				k4 = Calc1(id_active, 3, 25);
				l5 = 14 + util.RandomInt((k4 - 120) / 4);
				boolean flag3;
				if (!(flag3 = Battle.sBattle.a(id_active, 0, l5, -1, -1, -1, 10)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 13, 1);
				break;
			}
		case 1: //雷动九天
			{
				int l4 = TestSelectedSkillLevel(id_active, 3, 25, 90);
				if (l4 < 0)
					return NeedMoreLevel(25);
				int i6 = 21 + util.RandomInt((l4 - 90) / 3);
				boolean flag4;
				if (!(flag4 = Battle.sBattle.a(id_active, 1, i6, -1, -1, -1, 10)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 12, 1);
				break;
			}
		case 25: //忍术烟幕
			{
				if (TestSelectedSkillLevel(id_active, 3, 26, 90) < 0)
					return NeedMoreLevel(26);
				int j7 = Battle.sBattle.fighter_data[id_active][9];
				int k7 = 50 + (Battle.sBattle.fighter_data[id_active][4] - Battle.sBattle.fighter_data[id_rival][4]) / 20;
				if (k7 < 0)
					k7 = 1;
				if (k7 > 99)
					k7 = 99;
				if (util.RandomBool(k7))
				{
					Battle.sBattle.a(id_active, 0, j7, -1, -1, -1, 10);
					boolean flag5 = false;
				} else
				{
					Battle.sBattle.d(1, 20, 1);
				}
				Battle.sBattle.b(1, 19, 1);
				break;
			}
		case 26: //忍法影分身
			{
				if (TestSelectedSkillLevel(id_active, 3, 26, 110) < 0)
					return NeedMoreLevel(26);
				int l7 = Battle.sBattle.fighter_data[id_active][37] / 3;
				if (l7 < 1)
					l7 = 1;
				if (l7 > 80)
					l7 = 80;
				Battle.sBattle.a(id_active, 5, l7, -1, 101, 18, 10);
				boolean flag6 = false;
				Battle.sBattle.b(1, 17, 1);
				break;
			}
		case 3: //冰心决
			{
				int i8 = TestSelectedSkillLevel(id_active, 3, 36, 90);
				if (i8 < 0)
					return NeedMoreLevel(36);
				i8 = Calc1(id_active, 3, 36);
				int l8 = 40 + (i8 - 90) / 3;
				boolean flag7;
				if (!(flag7 = Battle.sBattle.a(id_active, 6, l8, -1, -1, 44, 6)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 43, 1);
				break;
			}
		case 24: //八卦刀影掌
			{
				if (TestSelectedSkillLevel(id_active, 1, 11, 105) < 0)
					return NeedMoreLevel(11);
				if (TestSelectedSkillLevel(id_active, 3, 14, 105) < 0)
					return NeedMoreLevel(14);
				if (TestSelectedSkillLevel(id_active, 0, 12, 1) < 0)
					return NeedMoreLevel(12);
				int l9 = 0;
				Battle.sBattle.a(util.RandomInt(6) + 8, 0, 0, 0);
				l9 = (l9 = 0 + Battle.sBattle.PhyAttack(true)) + Battle.sBattle.PhyAttack(false);
				Battle.sBattle.b(1, 2, 1);
				Battle.sBattle.GetDamageDesc(1, l9, id_rival, 11);
				break;
			}
		case 36: //八阵刀影掌
			{
				if (TestSelectedSkillLevel(id_active, 1, 11, 135) < 0)
					return NeedMoreLevel(11);
				if (TestSelectedSkillLevel(id_active, 3, 14, 105) < 0)
					return NeedMoreLevel(14);
				if (TestSelectedSkillLevel(id_active, 0, 13, 105) < 0)
					return NeedMoreLevel(13);
				int i10 = 0;
				Battle.sBattle.a(util.RandomInt(9) + 14, 0, 0, 0);
				i10 = 0 + Battle.sBattle.PhyAttack(true);
				Battle.sBattle.a(util.RandomInt(6) + 8, 0, 0, 0);
				i10 = (i10 += Battle.sBattle.PhyAttack(true)) + Battle.sBattle.PhyAttack(false);
				Battle.sBattle.b(1, 2, 1);
				Battle.sBattle.GetDamageDesc(1, i10, id_rival, 11);
				break;
			}
		case 8: //柳浪闻莺
			{
				int i5;
				if ((i5 = TestSelectedSkillLevel(id_active, 3, 20, 150)) < 0)
					return NeedMoreLevel(20);
				int i13;
				if ((i13 = TestSelectedSkillLevel(id_active, 1, 18, 150)) < 0)
					return NeedMoreLevel(18);
				int j10 = 0;
				Battle.sBattle.a(-1, 0, 0, 0);
				j10 = (j10 = (j10 = 0 + Battle.sBattle.PhyAttack(true)) + Battle.sBattle.PhyAttack(true)) + Battle.sBattle.PhyAttack(false);
				Battle.sBattle.b(1, 4, 1);
				Battle.sBattle.GetDamageDesc(1, j10, id_rival, 18);
				break;
			}
		case 7: //落英缤纷
			{
				if (TestSelectedSkillLevel(id_active, 3, 20, 120) < 0)
					return NeedMoreLevel(20);
				if (TestSelectedSkillLevel(id_active, 1, 17, 120) < 0)
					return NeedMoreLevel(17);
				int k13 = Calc1(id_active, 1, 17) / 3;
				if (k13 < 0)
					k13 = 1;
				if (k13 > 80)
					k13 = 80;
				Battle.sBattle.b(1, 4, 1);
				if (Battle.sBattle.fighter_data[id_rival][29] == 0)
				{
					if (util.RandomBool(k13))
					{
						Battle.sBattle.d(1, 10, 1);
						Battle.sBattle.a(id_rival, 4, -1, -1, 99, -1, 4);
					} else
					{
						Battle.sBattle.d(1, 11, 1);
						Battle.sBattle.a(id_active, 4, -1, -1, 99, -1, 3);
					}
				} else
				if (util.RandomBool(k13))
				{
					Battle.sBattle.d(1, 8, 1);
					Battle.sBattle.stack_fighterdate_set(id_rival, 29, -123);
				} else
				{
					Battle.sBattle.d(1, 9, 1);
					Battle.sBattle.a(id_active, 4, -1, -1, 99, -1, 4);
				}
				break;
			}
		case 0: //流星飞掷
			{
				if (TestSelectedSkillLevel(id_active, 3, 25, 120) < 0)
					return NeedMoreLevel(25);
				if (TestSelectedSkillLevel(id_active, 1, 23, 120) < 0)
					return NeedMoreLevel(23);
				if (Battle.sBattle.fighter_data[id_active][8] < 33)
				{
					return "膂力不够,无法使用绝招";
				}
				int l13 = Calc1(id_active, 1, 23) / 6;
				if (l13 < 0)
					l13 = 1;
				if (l13 > 40)
					l13 = 40;
				Battle.sBattle.b(1, 14, 1);
				Battle.sBattle.stack_fighterdate_set(id_active, 29, -123);
				if (util.RandomBool(l13))
				{
					Battle.sBattle.d(1, 15, 1);
					Battle.sBattle.stack_fighterdate_set(id_rival, 1, 0);
					Battle.sBattle.stack_fighterdate_set(id_rival, 2, 0);
				} else
				{
					Battle.sBattle.d(1, 16, 1);
				}
				break;
			}
		case 27: //旋风三连斩
			{
				if (TestSelectedSkillLevel(id_active, 3, 26, 120) < 0)
					return NeedMoreLevel(26);
				if (TestSelectedSkillLevel(id_active, 1, 29, 90) < 0)
					return NeedMoreLevel(29);
				int k10 = 0;
				boolean flag12 = false;
				Battle.sBattle.a(87, 0, 0, -1);
				k10 = 0 + Battle.sBattle.PhyAttack(true);
				Battle.sBattle.a(88, 0, 0, -1);
				k10 += Battle.sBattle.PhyAttack(true);
				Battle.sBattle.a(89, 0, 0, -1);
				k10 += Battle.sBattle.PhyAttack(true);
				Battle.sBattle.b(1, 21, 1);
				Battle.sBattle.GetDamageDesc(1, k10, id_rival, 29);
				break;
			}
		case 28: //迎风一刀斩
			{
				int i7;
				if ((i7 = TestSelectedSkillLevel(id_active, 3, 26, 120)) < 0)
					return NeedMoreLevel(26);
				int j16;
				if ((j16 = TestSelectedSkillLevel(id_active, 1, 29, 120)) < 0)
					return NeedMoreLevel(29);
				Battle.sBattle.b(1, 22, 1);
				Battle.sBattle.a(-1, 70, 15, -1);
				int l10 = Battle.sBattle.PhyAttack(true);
				Battle.sBattle.GetDamageDesc(1, l10, id_rival, 29);
				break;
			}
		case 4: //雪花六出
			{
				if (TestSelectedSkillLevel(id_active, 3, 36, 90) < 0)
					return NeedMoreLevel(36);
				if (TestSelectedSkillLevel(id_active, 1, 38, 90) < 0)
					return NeedMoreLevel(38);
				int i11 = 0;
				int l16 = 0;
				int i17 = Calc1(id_active, 1, 38);
				l16 = 2 + (i17 - 90) / 30; // 连出的次数
				if (Gmud.sPlayer.lasting_tasks[0] == 1)
					l16++;
				Battle.sBattle.b(1, 45, 1);
				for (int j17 = 0; j17 < l16; j17++)
				{ // TODO：连续多次物理攻击 
					Battle.sBattle.a(-1, -1, -1, -1);
					i11 += Battle.sBattle.PhyAttack(true);
				}
				Battle.sBattle.GetDamageDesc(6, i11, id_rival, 38);
				break;
			}
		case 5: //神倒鬼跌
			{
				if (TestSelectedSkillLevel(id_active, 3, 36, 150) < 0)
					return NeedMoreLevel(36);
				if (TestSelectedSkillLevel(id_active, 0, 39, 90) < 0)
					return NeedMoreLevel(39);
				Battle.sBattle.b(1, 46, 1);
				int i14= Calc1(id_active, 0, 39) / 5;
				if (util.RandomBool(i14))
				{
					Battle.sBattle.d(1, 47, 1);
					Battle.sBattle.a(id_rival, 4, -1, -1, 99, -1, 4);
				} else
				{
					Battle.sBattle.d(1, 48, 1);
				}
				break;
			}
		case 9: //变熊术
			{
				if (TestSelectedSkillLevel(id_active, 3, 45, 150) < 0)
					return NeedMoreLevel(45);
				int l18 = Calc1(id_active, 3, 45) / 2;
				if (l18 < 0)
					l18 = 10;
				boolean flag8;
				if (!(flag8 = Battle.sBattle.a(id_active, 6, l18, -1, -1, 56, 8)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 55, 1);
				break;
			}
		case 10: //猛虎啸
			{
				int i18;
				if ((i18 = TestSelectedSkillLevel(id_active, 3, 45, 150)) < 0)
					return NeedMoreLevel(45);
				int j14 = (Battle.sBattle.fighter_data[id_active][4] - Battle.sBattle.fighter_data[id_rival][4]) / 20;
				if ((j14 += 50) < 0)
					j14 = 1;
				if (j14 > 90)
					j14 = 90;
				Battle.sBattle.b(1, 49, 1);
				if (util.RandomBool(j14))
				{
					Battle.sBattle.d(1, 50, 1);
					Battle.sBattle.a(id_rival, 4, -1, -1, 99, -1, 8);
				} else
				{
					Battle.sBattle.d(1, 51, 1);
				}
				break;
			}
		case 37: //"飞鹰召唤"
			{
				if (TestSelectedSkillLevel(id_active, 3, 45, 180) < 0)
					return NeedMoreLevel(45);
				int i19 = Calc1(id_active, 3, 45);
				if (!Battle.sBattle.a(id_rival, 7, util.RandomInt(i19), -1, 96, -1, 12))
					return NEED_WAIT;
				Battle.sBattle.b(1, 95, 1);
				break;
			}
		case 38: //"变鹰术"
			{
				if (TestSelectedSkillLevel(id_active, 3, 45, 150) < 0)
					return NeedMoreLevel(45);
				if (TestSelectedSkillLevel(id_active, 0, 44, 1) < 0)
				{
//					wstring str("你还未学过鹰爪功");
//					return str;
					return "你还未学过鹰爪功";
				}
				boolean flag10;
				if (!(flag10 = Battle.sBattle.a(id_active, 2, 20, -1, -1, 53, 12)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 52, 1);
				break;
			}
		case 29: //震字决
			{
				// 太极神功+内功 需要 90 级
				if ( TestSelectedSkillLevel(id_active, 3, 32, 90)< 0)
					return NeedMoreLevel(32);
				// 太极拳+拳脚 需要 90 级
				if (TestSelectedSkillLevel(id_active, 0, 31, 90) < 0)
					return NeedMoreLevel(31);
				
				// 命中率: 70+(使用者内力-敌人内力)/20
				int hit = 70 + (Battle.sBattle.fighter_data[id_active][4] - Battle.sBattle.fighter_data[id_rival][4]) / 20;
				if (hit < 0)
					hit = 1;
				if (hit > 90)
					hit = 90;
				
				// 伤害：10+Random(使用者内力/5)
				int hurt = Battle.sBattle.fighter_data[id_active][4] / 5;
				hurt = 10 + util.RandomInt(hurt);
				
				// 记录攻击描述
				// magic.txt:39:突然你双手左右连划，一个圆圈已将SB套住，太极拳的~随即使出！
				Battle.sBattle.b(1, 39, 1);
				
				if (util.RandomBool(hit))
				{
					// LNX: 镇字诀，血的上限也掉？
					int j22 = Battle.sBattle.fighter_data[id_rival][1] - hurt; // hp
					int k23 = Battle.sBattle.fighter_data[id_rival][2] - hurt; // hp-max
					if (j22 < 0)
						j22 = 0;
					if (k23 < 0)
						k23 = 0;
					Battle.sBattle.stack_fighterdate_set(id_rival, 1, j22);
					Battle.sBattle.stack_fighterdate_set(id_rival, 2, k23);
					Battle.sBattle.d(1, 40, 1);
				} else
				if (util.RandomBool(10))
				{
					// ... 各自退几步
					Battle.sBattle.d(1, 42, 1);
				} else
				{
					// ... 再也来不及出招
					Battle.sBattle.d(1, 41, 1);
					Battle.sBattle.a(id_active, 4, -1, -1, 99, -1, 4);
				}
				break;
			}
		case 30: //挤字决
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 105) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 0, 31, 105) < 0)
					return NeedMoreLevel(31);
				
				// [4] - fp
				int l14 =70 + (Battle.sBattle.fighter_data[id_active][4] - Battle.sBattle.fighter_data[id_rival][4]) / 20;
				if (l14 < 0)
					l14 = 1;
				if (l14 > 95)
					l14 = 95;
				int k11 = Battle.sBattle.fighter_data[id_active][4] / 10;
				k11 = 10 + util.RandomInt(k11);
				Battle.sBattle.b(1, 29, 1);
				if (util.RandomBool(l14))
				{ // 减掉 对方的 fp 值
					int k22 = Battle.sBattle.fighter_data[id_rival][4] - k11;
					if (k22 < 0)
						k22 = 0;
					Battle.sBattle.stack_fighterdate_set(id_rival, 4, k22);
					Battle.sBattle.d(1, 30, 1);
				} else
				if (util.RandomBool(10))
				{
					Battle.sBattle.d(1, 32, 1);
				} else
				{
					Battle.sBattle.d(1, 31, 1);
					int l22 = Battle.sBattle.fighter_data[id_active][4] - k11 / 2;
					if ((l22) < 0)
						l22 = 0;
					Battle.sBattle.stack_fighterdate_set(id_active, 4, l22);
				}
				break;
			}
		case 31: // 乱环决 
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 150) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 0, 31, 150) < 0)
					return NeedMoreLevel(31);
				int i15 = Calc1(id_active, 0, 31) / 3;
				if (i15 < 0)
					i15 = 1;
				if (i15 > 95)
					i15 = 95;
				Battle.sBattle.b(1, 33, 1);
				if (util.RandomBool(i15))
				{
					int i23 = util.RandomInt(12) + 1;
					Battle.sBattle.a(id_rival, 4, -1, -1, 99, -1, i23);
					Battle.sBattle.d(1, 34, 1);
				} else
				{
					Battle.sBattle.d(1, 35, 1);
				}
				break;
			}
		case 32: // 阴阳决 
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 180) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 0, 31, 180) < 0)
					return NeedMoreLevel(31);
				int j23 = util.RandomInt(2) + 121;
				Battle.sBattle.a(j23, 0, 0, -1);
				int l11 = Battle.sBattle.PhyAttack(true);
				if ((l11) < 20)
					Battle.sBattle.d(1, 38, 1);
				else
					Battle.sBattle.d(1, 37, 1);
				break;
			}

		case 33: // 缠绵决 
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 120) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 1, 30, 120) < 0)
					return NeedMoreLevel(30);
				int j15 = Calc1(id_active, 1, 30) / 3;
				if (j15 < 0)
					j15 = 1;
				if (j15 > 90)
					j15 = 90;
				Battle.sBattle.b(1, 23, 1);
				if (util.RandomBool(j15))
				{
					int k24 = util.RandomInt(7) + 1;
					Battle.sBattle.a(id_rival, 4, -1, -1, 99, -1, k24);
					Battle.sBattle.d(1, 24, 1);
				} else
				{
					Battle.sBattle.d(1, 25, 1);
				}
				break;
			}
		case 34: // 连字决
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 120) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 1, 30, 120) < 0)
					return NeedMoreLevel(30);
				boolean flag11 = false;
				if (!(flag11 = (flag11 = Battle.sBattle.a(id_active, 0, 10, -1, -1, 27, 10)) || Battle.sBattle.a(id_active, 3, 12 + util.RandomInt(3), -1, -1, -1, 10)))
					return NEED_WAIT;
				Battle.sBattle.b(1, 26, 1);
				break;
			}
		case 35: //三环套月
			{
				if (TestSelectedSkillLevel(id_active, 3, 32, 180) < 0)
					return NeedMoreLevel(32);
				if (TestSelectedSkillLevel(id_active, 1, 30, 180) < 0)
					return NeedMoreLevel(30);
				Battle.sBattle.a(101, 0, 0, -1);
				int i12 = Battle.sBattle.PhyAttack(true);
				Battle.sBattle.a(102, 0, 0, -1);
				i12 += Battle.sBattle.PhyAttack(true);
				Battle.sBattle.a(103, 0, 0, -1);
				i12 += Battle.sBattle.PhyAttack(true);
				Battle.sBattle.b(1, 28, 1);
				Battle.sBattle.GetDamageDesc(1, i12, id_rival, 30);
				break;
			}
		case 11: // '\013'
		case 12: // '\f'
		case 13: // '\r'
		case 14: // '\016'
		case 15: // '\017'
		case 16: // '\020'
		case 17: // '\021'
		case 18: // '\022'
		case 19: // '\023'
		case 20: // '\024'
		case 21: // '\025'
		case 22: // '\026'
		default:
			return UseMPMagic(id_active, id_rival, magic_id);  //use magic
		}
		if (magic_id >= 11 && magic_id <= 22)   //cost FP / MP
			Battle.sBattle.CostMP(id_active, MAGIC_COST[magic_id]);
		else
			Battle.sBattle.CostFP(id_active, MAGIC_COST[magic_id]);
		str = "";
		return str;
	}

	static String NeedMoreLevel(int skill_id)
	{
//		wstring str("你的武功修为不够");
		if (skill_id < 0 || skill_id > 57)
			return "你的武功修为不够";
		else
			return "你的" + Skill.skill_name[skill_id] + "修为不够";
	}
/** 收集可用技能列表到 Battle.a_int_array2d_static，使用[0,10)的栈，存绝招ID，见 {@link #MAGIC_NAME} */
	static void Effect(int id_active)
	{
		int j = 0;
		for (int l = 0; l < 10; l++)
			Battle.sBattle.a_int_array2d_static[id_active][l] = 255;

		int i1 = Battle.sBattle.fighter_data[id_active][36]; // 内功
		int j1 = Battle.sBattle.fighter_data[id_active][29];
		int k1 = Items.item_attribs[j1][1];
		if (i1 != 255)
			switch (i1)
			{
			default:
				break;

			case 14: //混元一气功
				{
					int l1 = Calc1(id_active, 0, 12); // 八卦游身掌 的等级
					int j2 = Calc1(id_active, 0, 13);// 八阵八卦掌
					int l2 = Calc1(id_active, 1, 11);// 八卦刀
					if (k1 == 0)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][0] = 23; //化掌为刀
					}
					if ((l1 > 0 || j2 > 9) && l2 > 0 && k1 == 1)
					{
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 24;
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 36;
					}
					break;
				}
			case 20: //三花聚顶
				{
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][0] = 6;
					int j3 = Calc1(id_active, 1, 17);
					int l3 = Calc1(id_active, 1, 18);
					if (j3 > 0 && k1 == 9)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][1] = 7;
					}
					if (l3 > 0 && k1 == 1)
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 8;
					break;
				}
			case 25: //普天同济
				{
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][0] = 1;
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][1] = 2;
					int i4;
					if ((i4 = Calc1(id_active, 1, 23)) > 0 && k1 == 7)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][2] = 0;
					}
					break;
				}
			case 26: //扶桑忍术
				{
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][0] = 25;
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][1] = 26;
					int j4;
					if ((j4 = Calc1(id_active, 1, 29)) > 0 && k1 == 1)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][2] = 27;
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][3] = 28;
					}
					break;
				}
			case 32: //太极神功
				{
					int k4 = Calc1(id_active, 0, 31);
					int l4 = Calc1(id_active, 1, 30);
					if (k4 > 0 && k1 == 0)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][0] = 29;
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][1] = 30;
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][2] = 31;
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][3] = 32;
					}
					if (l4 > 0 && k1 == 6)
					{
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 33;
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 34;
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 35;
					}
					break;
				}
			case 36: //雪山内功
				{
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][0] = 3;
					int i5 = Calc1(id_active, 1, 38);
					int j5 = Calc1(id_active, 0, 39);
					if (i5 > 0 && k1 == 6)
					{
						j++;
						Battle.sBattle.a_int_array2d_static[id_active][1] = 4;
					}
					if (j5 > 0 && k1 == 0)
						Battle.sBattle.a_int_array2d_static[id_active][j++] = 5;
					break;
				}
			case 45: // 龙象般若功
				j++;
				Battle.sBattle.a_int_array2d_static[id_active][0] = 9;
				j++;
				Battle.sBattle.a_int_array2d_static[id_active][1] = 37;
				j++;
				Battle.sBattle.a_int_array2d_static[id_active][2] = 38;
				int k5;
				if ((k5 = Calc1(id_active, 0, 43)) > 0 && k1 == 0)
				{
					j++;
					Battle.sBattle.a_int_array2d_static[id_active][3] = 10;
				}
				break;
			}
		int k3 = 0;
		if (Battle.sBattle.fighter_data[id_active][36] == 48) // 如果内功是 48谷衣心法
			k3 = Battle.sBattle.fighter_data[id_active][37];
		int i2 = Calc1(id_active, 6, 52); // 五雷咒 的等级
		int k2 = Calc1(id_active, 6, 50); // 万鸦咒
		int i3 = Calc1(id_active, 6, 51); // 玄冰咒
		if (i2 > 0)
		{
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 11;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 12;
			if (k3 > 0)
				Battle.sBattle.a_int_array2d_static[id_active][j++] = 13;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 14;
		}
		if (k2 > 0)
		{
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 15;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 16;
			if (k3 > 0)
				Battle.sBattle.a_int_array2d_static[id_active][j++] = 17;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 18;
		}
		if (i3 > 0)
		{
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 19;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 20;
			Battle.sBattle.a_int_array2d_static[id_active][j++] = 21;
			Battle.sBattle.a_int_array2d_static[id_active][j] = 22;
		}
	}

	static String UseMPMagic(int i, int j, int l)
	{
		switch (l)
		{
		case 11: //闪光弹
			{
				int i1;
				if ((i1 = TestSelectedSkillLevel(i, 6, 52, 37)) < 0)
					return NeedMoreLevel(52);
				int i7 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				int i4 = Calc3(i, j, i7);
				int i10 = Calc4(i, j, 10, i7);
				Battle.sBattle.b(1, 82, 1);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				if (util.RandomBool(i4))
				{
					Battle.sBattle.GetMagicHitDesc(85, 7, i10, j);
				} else
				{
					int k12;
					if ((k12 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 100) > 90)
						k12 = 90;
					if (util.RandomBool(k12))
					{
						Battle.sBattle.GetMagicHitDesc(85, 7, i10 / 3, i);
						Battle.sBattle.d(1, 83, 1);
					} else
					{
						Battle.sBattle.d(0, 247, 1);
					}
				}
				break;
			}
		case 12: // 雷火弹
			{
				int j1;
				if ((j1 = TestSelectedSkillLevel(i, 6, 52, 37)) < 0)
					return NeedMoreLevel(52);
				int j4 = 0;
				int j7 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				j4 = Calc3(i, j, j7);
				int j10 = Calc4(i, j, 8, j7);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 84, 1);
				if (util.RandomBool(j4))
				{
					Battle.sBattle.GetMagicHitDesc(85, 7, j10, j);
				} else
				{
					int l12;
					if ((l12 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 100) > 90)
						l12 = 90;
					if (util.RandomBool(l12))
					{
						Battle.sBattle.GetMagicHitDesc(85, 7, j10 / 4, i);
						Battle.sBattle.d(1, 92, 1);
					} else
					{
						Battle.sBattle.d(0, 247, 1);
					}
				}
				break;
			}
		case 13: // 掌心雷
			{
				int k1;
				if ((k1 = TestSelectedSkillLevel(i, 6, 52, 120)) < 0)
					return NeedMoreLevel(52);
				int i13;
				if ((i13 = TestSelectedSkillLevel(i, 3, 48, 135)) < 0)
					return NeedMoreLevel(48);
				int k4 = 0;
				int k7 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				k4 = Calc3(i, j, k7);
				int k10 = Calc4(i, j, 6, k7);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 93, 1);
				if (util.RandomBool(k4))
				{
					Battle.sBattle.GetMagicHitDesc(85, 7, k10, j);
					if (util.RandomBool(10))
						Battle.sBattle.a(j, 4, -1, -1, 99, -1, 6);
				} else
				{
					int k13;
					if ((k13 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 20) > 90)
						k13 = 90;
					if (util.RandomBool(k13))
					{
						Battle.sBattle.GetMagicHitDesc(85, 7, k10 / 5, i);
						Battle.sBattle.d(1, 94, 1);
					} else
					{
						Battle.sBattle.d(0, 247, 1);
					}
				}
				break;
			}
		case 14: // 连珠雷
			{
				int l1;
				if ((l1 = TestSelectedSkillLevel(i, 6, 52, 180)) < 0)
					return NeedMoreLevel(52);
				int l4 = 0;
				int l7 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				l4 = Calc3(i, j, l7);
				int l10;
				l10 = (l10 = (l10 = Calc4(i, j, 8, l7)) + Calc4(i, j, 8, l7)) + Calc4(i, j, 10, l7);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63] * 3);
				Battle.sBattle.b(1, 93, 1);
				if (util.RandomBool(l4))
				{
					Battle.sBattle.GetMagicHitDesc(85, 7, l10, j);
				} else
				{
					int l13;
					if ((l13 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 20) > 90)
						l13 = 90;
					if (util.RandomBool(l13))
					{
						Battle.sBattle.GetMagicHitDesc(85, 7, l10 / 4, i);
						Battle.sBattle.d(1, 94, 1);
					} else
					{
						Battle.sBattle.d(0, 247, 1);
					}
				}
				break;
			}
		case 15: // '\017'
			{
				int i2;
				if ((i2 = TestSelectedSkillLevel(i, 6, 50, 52)) < 0)
					return NeedMoreLevel(50);
				int i5 = 0;
				int i8 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				i5 = Calc3(i, j, i8);
				int i11 = Calc4(i, j, 11, i8);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 57, 1);
				if (util.RandomBool(i5))
				{
					Battle.sBattle.GetMagicHitDesc(60, 6, i11, j);
					if (util.RandomBool(10))
					{
						Battle.sBattle.c(1, 97, 1);
						Battle.sBattle.a(j, 7, i11 / 10, -1, 98, -1, 8);
					}
				} else
				{
					Battle.sBattle.d(1, 64, 1);
				}
				break;
			}
		case 16: // '\020'
			{
				int j2;
				if ((j2 = TestSelectedSkillLevel(i, 6, 50, 80)) < 0)
					return NeedMoreLevel(50);
				int j5 = 0;
				int j8 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				j5 = Calc3(i, j, j8);
				int j11 = Calc4(i, j, 7, j8);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 65, 1);
				if (util.RandomBool(j5))
					Battle.sBattle.GetMagicHitDesc(60, 6, j11, j);
				else
					Battle.sBattle.d(1, 64, 1);
				break;
			}

		case 17: // '\021'
			{
				int k2;
				if ((k2 = TestSelectedSkillLevel(i, 6, 50, 120)) < 0)
					return NeedMoreLevel(50);
				int j13;
				if ((j13 = TestSelectedSkillLevel(i, 3, 48, 170)) < 0)
					return NeedMoreLevel(48);
				int k5 = 0;
				int k8 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				k5 = Calc3(i, j, k8);
				Calc4(i, j, 7, k8);
				boolean flag = false;
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 66, 1);
				if (util.RandomBool(k5 / 2))
				{
					Battle.sBattle.d(1, 67, 1);
					for (int i14 = 0; i14 < 16; i14++)
						if (Battle.sBattle.fighter_data[j][14 + i14] != 0)
							Battle.sBattle.stack_fighterdate_set(j, i14, -123);

				} else
				{
					Battle.sBattle.d(1, 68, 1);
				}
				break;
			}
		case 18: // '\022'
			{
				int l2;
				if ((l2 = TestSelectedSkillLevel(i, 6, 50, 80)) < 0)
					return NeedMoreLevel(50);
				int l5 = 0;
				int l8 = (Calc1(i, 6, 52) + Calc1(i, 5, 49)) / 2;
				l5 = Calc3(i, j, l8);
				int k11;
				k11 = (k11 = (k11 = Calc4(i, j, 7, l8)) + Calc4(i, j, 7, l8)) + Calc4(i, j, 7, l8);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63] * 3);
				Battle.sBattle.b(1, 65, 1);
				if (util.RandomBool(l5))
					Battle.sBattle.GetMagicHitDesc(60, 6, k11, j);
				else
					Battle.sBattle.d(1, 64, 1);
				break;
			}
		case 19: // '\023'
			{
				int i3;
				if ((i3 = TestSelectedSkillLevel(i, 6, 51, 45)) < 0)
					return NeedMoreLevel(51);
				int i6 = 0;
				int i9 = (Calc1(i, 6, 51) + Calc1(i, 5, 49)) / 2;
				i6 = Calc3(i, j, i9);
				int l11 = Calc4(i, j, 12, i9);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 69, 1);
				if (util.RandomBool(i6))
				{
					Battle.sBattle.GetMagicHitDesc(72, 5, l11, j);
					if (util.RandomBool(20))
						Battle.sBattle.a(j, 4, -1, -1, 99, -1, 4);
				} else
				{
					int j14;
					if ((j14 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 20) > 90)
						j14 = 90;
					if (util.RandomBool(j14))
					{
						Battle.sBattle.GetMagicHitDesc(72, 5, l11 / 5, i);
						Battle.sBattle.d(1, 70, 1);
					} else
					{
						Battle.sBattle.d(1, 78, 1);
					}
				}
				break;
			}
		case 20: // '\024'
			{
				int j3;
				if ((j3 = TestSelectedSkillLevel(i, 6, 51, 90)) < 0)
					return NeedMoreLevel(51);
				int j6 = 0;
				int j9 = (Calc1(i, 6, 51) + Calc1(i, 5, 49)) / 2;
				j6 = Calc3(i, j, j9);
				int i12 = Calc4(i, j, 8, j9);
				Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63]);
				Battle.sBattle.b(1, 71, 1);
				if (util.RandomBool(j6))
				{
					Battle.sBattle.GetMagicHitDesc(72, 5, i12, j);
				} else
				{
					int k14;
					if ((k14 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 20) > 90)
						k14 = 90;
					if (util.RandomBool(k14))
					{
						Battle.sBattle.GetMagicHitDesc(72, 5, i12 / 5, i);
						Battle.sBattle.d(1, 77, 1);
					} else
					{
						Battle.sBattle.d(1, 78, 1);
					}
				}
				break;
			}
		case 21: // '\025'
			{
				int k3;
				if ((k3 = TestSelectedSkillLevel(i, 6, 51, 150)) < 0)
					return NeedMoreLevel(51);
				int k6 = 0;
				int k9 = (Calc1(i, 6, 51) + Calc1(i, 5, 49)) / 2;
				k6 = Calc3(i, j, k9);
				Battle.sBattle.b(1, 79, 1);
				if (util.RandomBool(k6 / 2))
				{
					Battle.sBattle.d(1, 80, 1);
					Battle.sBattle.a(j, 4, -1, -1, 99, -1, 6);
				} else
				{
					Battle.sBattle.d(1, 81, 1);
				}
				break;
			}
		case 22: // '\026'
			int l3;
			if ((l3 = TestSelectedSkillLevel(i, 6, 51, 180)) < 0)
				return NeedMoreLevel(51);
			int l6 = 0;
			int l9 = (Calc1(i, 6, 51) + Calc1(i, 5, 49)) / 2;
			l6 = Calc3(i, j, l9);
			int j12;
			j12 = (j12 = (j12 = Calc4(i, j, 8, l9)) + Calc4(i, j, 8, l9)) + Calc4(i, j, 8, l9);
			Battle.sBattle.CostMP(i, Battle.sBattle.fighter_data[i][63] * 3);
			Battle.sBattle.b(1, 79, 1);
			if (util.RandomBool(l6))
			{
				Battle.sBattle.GetMagicHitDesc(72, 5, j12, j);
				Battle.sBattle.d(1, 80, 1);
			} else
			{
				int l14;
				if ((l14 = (Battle.sBattle.fighter_data[j][4] - Battle.sBattle.fighter_data[i][4]) / 20) > 90)
					l14 = 90;
				if (util.RandomBool(l14))
				{
					Battle.sBattle.GetMagicHitDesc(72, 5, j12 / 5, i);
					Battle.sBattle.d(1, 77, 1);
				} else
				{
					Battle.sBattle.d(1, 78, 1);
				}
			}
			break;
		}
		if (l >= 11 && l <= 22)
			Battle.sBattle.CostMP(i, MAGIC_COST[l]);
		else
			Battle.sBattle.CostFP(i, MAGIC_COST[l]);
//		wstring str = "";
		String str = "";
		return str;
	}

	/**
	 * 计算 正在使用的技能的等级（门派技能/2+基本功/4），如果技能ID不符合，则返回0
	 * 
	 * @param id
	 *            角色ID
	 * @param skill_type
	 *            技能类别
	 * @param skill_id
	 *            需要匹配的门派技能ID
	 * @return
	 */
	static int Calc1(int id, int skill_type, int skill_id) {
		final int[] data = Battle.sBattle.fighter_data[id];

		int skill = data[30 + skill_type * 2];
		if (skill != skill_id) {
			// 技能ID不符
			return 0;
		}
		int level = data[30 + skill_type * 2 + 1] / 2;
		if (data[46 + skill_type * 2] != 255)
			level += data[46 + skill_type * 2 + 1] / 4;
		return level;
	}

	 /** 计算正在使用的技能等级（门派技能+基本功/2），如果技能ID不符合则返回 -1 */
	static int TestSelectedSkillLevel(int id_player, int type, int skill_id, int test_level) {
		final int[] data = Battle.sBattle.fighter_data[id_player];

		// 技能ID不符
		if (data[30 + type * 2] != skill_id)
			return -1;

		int level = data[30 + type * 2 + 1];


		if (data[46 + type * 2] != 255)
			level += data[46 + type * 2 + 1] / 2;

		if (level < test_level)
			return -1;
		return level;
	}

	static int Calc3(int id, int id_rival, int level) {
		final int[] data = Battle.sBattle.fighter_data[id];
		final int[] data_rival = Battle.sBattle.fighter_data[id_rival];
		
		// 敏捷差
		int agility_gap = data[9] - data_rival[9];
		// 经验差
		int exp_gap = Battle.sBattle.CalcExpLevel(data[64] / 100)
				- Battle.sBattle.CalcExpLevel(data_rival[64] / 100);
		// 等级差
		int level_gap = level * 2 - data_rival[62];
		
		int gap = 50 + agility_gap + exp_gap + level_gap;
		if (gap < 1)
			gap = 1;
		if (gap > 80)
			gap = 80;

		int j1 = Battle.sBattle.CalaAvtiveSpeed(id_rival, 5, 4);
		if (j1 > 0 && j1 < 20)
			gap = 100;
		return gap;
	}

	static int Calc4(int id, int j, int l, int i1) {
		final int[] data = Battle.sBattle.fighter_data[id];

		// mp_plus + mp/l
		int j1 = (data[63] + data[6] / l + (i1 * 10) / l)
				- Battle.sBattle.CalcDefenseB(j);
		if (j1 < 1)
			j1 = 1;
		j1 = j1 / 2 + util.RandomInt(j1 / 2);
		if (j1 < 10)
			j1 = 10 + util.RandomInt(10);
		return j1;
	}
}
