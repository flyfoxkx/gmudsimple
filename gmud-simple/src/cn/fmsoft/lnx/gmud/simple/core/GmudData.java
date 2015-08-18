package cn.fmsoft.lnx.gmud.simple.core;

class GmudData {
	
	static final class ClassID {
		final static int None = 0;
		final static int BaGuaMen = 1;
		final static int HuaJian = 2;
		final static int HongLianJiao = 3;
		final static int YiHeGu = 4;
		final static int WuDang = 5;
		final static int XueShan = 6;
		final static int ShouWang = 7;
		final static int MaoShan = 8;
		final static int _MAX_ = 9;
	}
	
	/** size: 40 */
	static final class LastTask {
		/** 雪花六出？ */
		final static int UNKNOW0 = 0;
		/** 打坛进度 0无 [1,8] */
		final static int PK_GANG = 1;
		/** 开放铸剑谷 */
		final static int OPEN_ChouJianGu = 2;
		/** 上次铸武器时玩家的经验？ */
		final static int LAST_WEAPON_EXP = 3;
		/** 自制武器中 */
		final static int NEW_WEAPON = 4;
		final static int UNKNOW5 = 5;
		/** 开放桃花园 */
		final static int OPEN_TaoHuaYuan = 6;
		final static int UNKNOW7 = 7;
		final static int UNKNOW8 = 8;
		final static int UNKNOW9 = 9;
		final static int UNKNOW10 = 10;
		final static int UNKNOW11 = 11;
		final static int UNKNOW12 = 12;
		final static int UNKNOW13 = 13;
		final static int UNKNOW14 = 14;
		final static int UNKNOW15 = 15;
		final static int UNKNOW16 = 16;
		final static int UNKNOW17 = 17;
		final static int UNKNOW18 = 18;
		final static int UNKNOW19 = 19;
		final static int UNKNOW20 = 20;
		final static int UNKNOW21 = 21;
		final static int UNKNOW22 = 22;
		final static int UNKNOW23 = 23;
		final static int UNKNOW24 = 24;
		final static int UNKNOW25 = 25;
		final static int UNKNOW26 = 26;
		final static int UNKNOW27 = 27;
		final static int UNKNOW28 = 28;
		final static int UNKNOW29 = 29;
		final static int UNKNOW30 = 30;
		final static int UNKNOW31 = 31;
		final static int UNKNOW32 = 32;
		final static int UNKNOW33 = 33;
		final static int UNKNOW34 = 34;
		final static int UNKNOW35 = 35;
		final static int UNKNOW36 = 36;
		final static int UNKNOW37 = 37;
		final static int UNKNOW38 = 38;
		final static int UNKNOW39 = 39;
	}
	
	static final String boss_map_name[] = new String[] {
			"青龙坛", "地罡坛", "朱雀坛", "山岚坛", "玄武坛", "紫煞坛", "天微坛", "白虎坛"
		};

	static final int boss_map_id[] = new int[] {
			23, 73, 59, 79, 31, 54, 64, 44
		};

	static final String level_name[] = new String[] {
			"不堪一击", "毫不足虑", "不足挂齿", "初学乍练", "勉勉强强", "初窥门径", "初出茅庐", "略知一二", "普普通通", "平平常常", 
			"平淡无奇", "粗懂皮毛", "半生不熟", "登堂入室", "略有小成", "已有小成", "鹤立鸡群", "驾轻就熟", "青出於蓝", "融会贯通", 
			"心领神会", "炉火纯青", "了然於胸", "略有大成", "已有大成", "豁然贯通", "非比寻常", "出类拔萃", "罕有敌手", "技冠群雄", 
			"神乎其技", "出神入化", "傲视群雄", "登峰造极", "无与伦比", "所向披靡", "一代宗师", "精深奥妙", "神功盖世", "举世无双", 
			"惊世骇俗", "撼天动地", "震古铄今", "超凡入圣", "威镇寰宇", "空前绝后", "天人合一", "深藏不露", "深不可测", "返璞归真", 
			"极轻很轻", "极轻很轻"
			};

	static final String attack_level_name[] = new String[] {
			"极轻", "很轻", "不轻", "不重", "很重", "极重"
			};
	static final String hit_point_name[] = new String[] {
			"头部", "颈部", "胸口", "后心", "左肩", "右肩", "左臂", "右臂", "左手", "右手", 
			"腰间", "小腹", "左腿", "右腿", "左脚", "右脚"
			};
	static final String class_name[] = new String[] {
			"普通百姓", "八卦门", "花间派", "红莲教", "尹贺谷", "太极门", "雪山剑派", "兽王派", "茅山派"
			};
	/** (size:11) */
	static final String map_name[] = new String[] {
			"平安镇", "商家堡", "玉女峰", "五指山", "冰火岛", "武当山", "大雪山", "黑森林", "灵心观", "铸剑谷", 
			"桃花源"
			};

	/** (size:11) 主地图（可以飞的地图）的ID，见 {@link #map_name} */
	static final int fly_dest_map[] = new int[] { // map id
	0, 23, 44, 59, 31, 64, 54, 79, 73, 87, 89 };

	// [19]
	static final int flyable_map[] = new int[]{
			0, 23, 44, 59, 31, 64, 54, 79, 73, 18, 
			19, 39, 40, 41, 42, 21, 22, 87, 89
			};

	// [20]
	static final int bad_man_based_npc[] = new int[]{  //恶人原型NPC
			38, 47, 73, 80, 87, 90, 94, 96, 97, 101, 
			110, 118, 120, 122, 127, 128, 135, 139, 141, 58
			};

	static final int bad_man_weapon[] = new int [] {  //恶人武器
			11, 11, 21, 21, 11, 0, 11, 11, 35, 35, 
			0, 35, 35, 35, 0, 0, 0, 0, 0, 22
			};

	// [9][16]
	static final int bad_man_map[][] = new int [][] {  //恶人地图id
			{
				1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 
				12, 13, 14, 15, 16, 17
			}, {
				24, 25, 26, 27, 29, 30, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				45, 46, 47, 48, 49, 50, 52, 53, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				60, 61, 62, 63, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				32, 33, 34, 35, 37, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				65, 66, 67, 68, 70, 71, 72, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				55, 56, 58, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				80, 81, 82, 84, 85, 86, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}, {
				74, 75, 76, 77, 78, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0
			}
			};
		
		// [14][2]
		static final String face_level_name[][] = new String[][] {
			{
				"面目狰狞", "惨不忍睹"
			}, {
				"牛嘴马眼", "貌赛无盐"
			}, {
				"不是人样", "相貌简陋"
			}, {
				"一塌糊涂", "眼大嘴小"
			}, {
				"还过得去", "还过得去"
			}, {
				"相貌平平", "相貌平平"
			}, {
				"身材均称", "尚有姿色"
			}, {
				"五官端正", "身材娇好"
			}, {
				"双眼有神", "美貌如花"
			}, {
				"相貌英俊", "婷婷玉立"
			}, {
				"骨骼清奇", "闭月羞花"
			}, {
				"气宇轩昂", "沉鱼落雁"
			}, {
				"仪表堂堂", "人间仙子"
			}, {
				"风流俊雅", "美央绝伦"
			}
			};
		
		// 武器描述:
		//
		// 锯齿开光破甲狼牙罗刹修罗菩提金光雾光地火烈风巨索青冥白虹青虹霓虹金蛇紫霞碧水观日飞影太风太渊松鹤凤翔龙翔华凤观日飞影太风太渊松鹤凤翔龙翔华凤黄龙玉龙天龙莫邪七星鱼肠龙泉三才五圣乱神霸王天后帝王昆仑练狱浩然屠龙依天日月风云天地乾坤无极轩辕膚豾
		//
		// 一品怪力绿波蔓陀鹤顶绿野灵泉天芝玉露蟠桃混沌狻猊熊罴麒麟天盾雷电紫电飓电飞火绝世
		//
		// 笑颦弱水戏候樱华天仙晦明合灵人灵地灵天灵暗光合玄人玄地玄天玄泻力合宇人宇地宇天宇沉水合渊人渊地渊天渊
		// 
		// [50+2]
		static final String weapon_first_name[] = new String[] {
				"锯齿",
				"开光", "破甲", "狼牙", "罗刹", "修罗", "菩提", "金光", "雾光", "地火", "烈风", 
				"巨索", "青冥", "白虹", "青虹", "霓虹", "金蛇", "紫霞", "碧水", "观日", "飞影", 
				"太风", "太渊", "松鹤", "凤翔", "龙翔", "华凤", "黄龙", "玉龙", "天龙", "莫邪", 
				"七星", "鱼肠", "龙泉", "三才", "五圣", "乱神", "霸王", "天后", "帝王", "昆仑", 
				"炼狱", "浩然", "屠龙", "倚天", "日月", "风云", "天地", "乾坤", "无极", "轩辕",
				"膚豾"
			};
		
		// [37]
		static final String weapon_last_name[]  = new String[] {
				"混沌", "麒麟", "天盾", "圣盾", "雷电", "紫电", "飓电", "极光", "天渊", "地渊", 
				"人渊", "合渊", "天宇", "地宇", "人宇", "合宇", "天玄", "地玄", "人玄", "合玄", 
				"天灵", "地灵", "人灵", "合灵", "飞火", "沉水", "绝世", "一品", "怪力", "绿波", 
				"蔓陀", "鹤定", "绿野", "灵泉", "天芝", "玉露", "蟠桃"
			};
		
	static final int kill_task_temp_table[] = new int[147];

	/**
	 * (size:147) 平一指任务目标的等级限制表，用于提取限定等级内的目标，防止超过玩家能力上限，内容参考
	 * {@link NPC#NPC_names} 范围 ["阿庆嫂","山大王")
	 */
	private static final int kill_task_table[] = new int[] {
		2, 0, 1, 5, 1, 255, 12, 4, 0, 255, 
		3, 0, 0, 255, 255, 2, 0, 12, 14, 1, 
		0, 4, 5, 4, 1, 255, 1, 0, 0, 1, 
		255, 0, 2, 2, 0, 255, 0, 4, 10, 7, 
		3, 2, 16, 17, 18, 12, 0, 27, 9, 3, 
		2, 1, 9, 16, 15, 15, 11, 18, 26, 9, 
		8, 9, 8, 9, 1, 0, 18, 2, 16, 13, 
		5, 14, 14, 19, 16, 4, 5, 2, 13, 12, 
		27, 17, 6, 15, 3, 4, 7, 17, 13, 4, 
		19, 12, 3, 16, 27, 14, 17, 20, 14, 8, 
		5, 24, 0, 1, 2, 6, 0, 6, 11, 9, 
		28, 0, 5, 11, 12, 3, 16, 4, 17, 13, 
		14, 16, 7, 3, 25, 25, 18, 7, 3, 30, 
		10, 11, 3, 3, 3, 13, 6, 6, 23, 25, 
		25, 30, 6, 6, 25, 25, 8
	};

	/**
	 * 为平一指任务获取在限定等级内的目标
	 * 
	 * @param level
	 *            等级限制
	 * @return 返回可用NPC数量
	 */
	static int GetKillTaskTable(int level) {
		final int[] temp = kill_task_temp_table;
		final int[] table = kill_task_table;

		// for (int i = 0; i < 147; i++)
		// temp[i] = 0;

		int top = 0;
		for (int i = 0; i < 147; i++)
			if (table[i] <= level)
				temp[top++] = i;
		return top;
	}
}
