package cn.fmsoft.lnx.gmud.simple.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.fmsoft.lnx.gmud.simple.core.GmudData.ClassID;

public class Player {

	/* keep single instance */
	static private Player sInstance;

	static synchronized Player getInstance() {
		if (sInstance == null) {
			sInstance = new Player();
		}
		return sInstance;
	}

	/** 年龄：暂定１年为现实１天 */
	public static final int AGE_TIME = 24 * 60;

	/** 物品列表的最多数量 */
	static final int MAX_ITEM_SIZE = 32;
	/** 技能列表的最多数量 */
	static final int MAX_SKILL_SIZE = 32;

	int image_id; // 人物形象
	int sex; // 性别 0男 1女
	int hp, hp_max, hp_full; // 生命
	int fp, fp_level, fp_plus; // 内力
	int mp, mp_level, mp_plus; // 魔法
	int money, exp, potential; // 金钱,经验,潜能
	int water, food; // 水，食物
	int pre_force; // 先天力量
	int pre_agility; // 先天敏捷
	int pre_savvy; // 先天悟性
	int pre_aptitude; // 先天根骨
	int face_level; // 外貌
	private int class_id; // 门派
	int teacher_id; // 师傅
	int bliss; // 福缘
	int married; // 婚姻
	int unknow1, unknow2, unknow3;

	/**
	 * .[][0]:技能ID[0,53] .[][1]:技能等级 .[][2]:已学习的点数 .[][3]:未知
	 * .[][4]:当前级别技能提升技能的点数=(Level+1)*(Level+1)/(GetSavvy()*2*10 + Rnd(16))
	 */
	int skills[][] = new int[MAX_SKILL_SIZE][5];

	int lasting_tasks[] = new int[40];

	long played_time;

	/** (size:16) 当前的装备表 [15]武器 */
	int/* char */equips[] = new int[16];

	/**
	 * [0]:拳脚 [1]:兵刃 [2]:轻功 [3]:内功(255表示没有) [4]:招架 [5]:知识 [6]:法术
	 */
	int select_skills[] = new int[8];

	/** (size:32,3) [0]:ID [1]:是否已装备 [2]:数量 */
	int item_package[][] = new int[MAX_ITEM_SIZE][3];

	// byte player_name[] = new byte[16];
	// char weapon_name[] = new char[16]; // wchar_t
	// char consort_name[] = new char[16];

	String player_name;
	String weapon_name;
	String consort_name;

	String intArray2String(int num[], int size) {
		StringBuilder builder = new StringBuilder();
		// builder.append(size);
		for (int i = 0; i < size; i++) {
			builder.append(num[i]);
			builder.append(',');
		}
		return builder.toString();
	}

	String intArray2String(int num[][], int size_v, int size_h) {
		StringBuilder builder = new StringBuilder();
		// builder.append(size_v);
		// builder.append(size_h);
		for (int i = 0; i < size_v; i++) {
			for (int j = 0; j < size_h; j++) {
				builder.append(num[i][j]);
				builder.append(',');
			}
		}
		return builder.toString();
	}

	void String2IntArray(String str, int num[], int size) {
		int start = 0, cur;
		for (int i = 0; i < size; i++) {
			cur = str.indexOf(',', start + 1);
			num[i] = Integer.parseInt(str.substring(start, cur));
			start = cur + 1;
		}
	}

	void String2IntArray(String str, int num[][], int size_v, int size_h) {
		int start = 0, cur;
		for (int i = 0; i < size_v; i++) {
			for (int j = 0; j < size_h; j++) {
				cur = str.indexOf(',', start + 1);
				num[i][j] = Integer.parseInt(str.substring(start, cur));
				start = cur + 1;
			}
		}
	}

	boolean Clean(Context context) {

		SharedPreferences preferences;
		preferences = context.getSharedPreferences(Gmud.SAVE_PATH,
				Context.MODE_PRIVATE); // 其中fileName表示存放配置的配置文件名称

		Editor editor = preferences.edit();
		editor.putString("key", "");

		editor.commit();

		return true;
	}

	boolean Save(Context context) {
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(Gmud.SAVE_PATH,
				Context.MODE_PRIVATE); // 其中fileName表示存放配置的配置文件名称

		Editor editor = preferences.edit();

		editor.putString("player_name", player_name)
				.putString("weapon_name", weapon_name)
				.putString("consort_name", consort_name)
				.putInt("image_id", image_id)
				.putInt("sex", sex)
				.putInt("hp", hp)
				.putInt("hp_max", hp_max)
				.putInt("hp_full", hp_full)
				.putInt("fp", fp)
				.putInt("fp_level", fp_level)
				.putInt("fp_plus", fp_plus)
				.putInt("mp", mp)
				.putInt("mp_level", mp_level)
				.putInt("mp_plus", mp_plus)
				.putInt("money", money)
				.putInt("exp", exp)
				.putInt("potential", potential)
				.putInt("water", water)
				.putInt("food", food)
				.putInt("pre_force", pre_force)
				.putInt("pre_agility", pre_agility)
				.putInt("pre_savvy", pre_savvy)
				.putInt("pre_aptitude", pre_aptitude)
				.putInt("face_level", face_level)
				.putInt("class_id", class_id)
				.putInt("teacher_id", teacher_id)
				.putInt("bliss", bliss)
				.putInt("married", married)
				.putInt("unknow1", unknow1)
				.putInt("unknow2", unknow2)
				.putInt("unknow3", unknow3)
				.putString("skills", intArray2String(skills, MAX_SKILL_SIZE, 5))
				.putString("lasting_tasks", intArray2String(lasting_tasks, 40))
				.putString("equips", intArray2String(equips, 16))
				.putString("select_skills", intArray2String(select_skills, 8))
				.putString("item_package",
						intArray2String(item_package, MAX_ITEM_SIZE, 3))
				.putLong("played_time", played_time);

		editor.putString("key", "lnx");

		editor.commit();
		return true;
	}

	boolean load(Context context) {
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(Gmud.SAVE_PATH,
				Context.MODE_PRIVATE);

		String key = preferences.getString("key", "");
		if (key == "") {
			return false;
		}

		player_name = preferences.getString("player_name", player_name);
		weapon_name = preferences.getString("weapon_name", weapon_name);
		consort_name = preferences.getString("consort_name", consort_name);

		image_id = preferences.getInt("image_id", image_id);
		sex = preferences.getInt("sex", sex);
		hp = preferences.getInt("hp", hp);
		hp_max = preferences.getInt("hp_max", hp_max);
		hp_full = preferences.getInt("hp_full", hp_full);
		fp = preferences.getInt("fp", fp);
		fp_level = preferences.getInt("fp_level", fp_level);
		fp_plus = preferences.getInt("fp_plus", fp_plus);
		mp = preferences.getInt("mp", mp);
		mp_level = preferences.getInt("mp_level", mp_level);
		mp_plus = preferences.getInt("mp_plus", mp_plus);
		money = preferences.getInt("money", money);
		exp = preferences.getInt("exp", exp);
		potential = preferences.getInt("potential", potential);
		water = preferences.getInt("water", water);
		food = preferences.getInt("food", food);
		pre_force = preferences.getInt("pre_force", pre_force);
		pre_agility = preferences.getInt("pre_agility", pre_agility);
		pre_savvy = preferences.getInt("pre_savvy", pre_savvy);
		pre_aptitude = preferences.getInt("pre_aptitude", pre_aptitude);
		face_level = preferences.getInt("face_level", face_level);
		class_id = preferences.getInt("class_id", class_id);
		teacher_id = preferences.getInt("teacher_id", teacher_id);
		bliss = preferences.getInt("bliss", bliss);
		married = preferences.getInt("married", married);
		unknow1 = preferences.getInt("unknow1", unknow1);
		unknow2 = preferences.getInt("unknow2", unknow2);
		unknow3 = preferences.getInt("unknow3", unknow3);

		String2IntArray(preferences.getString("skills", ""), skills,
				MAX_SKILL_SIZE, 5);
		String2IntArray(preferences.getString("lasting_tasks", ""),
				lasting_tasks, 40);
		String2IntArray(preferences.getString("equips", ""), equips, 16);
		String2IntArray(preferences.getString("select_skills", ""),
				select_skills, 8);
		String2IntArray(preferences.getString("item_package", ""),
				item_package, MAX_ITEM_SIZE, 3);
		played_time = preferences.getLong("played_time", played_time);

		GmudTemp.ClearAllData();

		return true;
	}

	void reset() {
		water = food = hp_full = hp_max = hp = 100;
		pre_force = pre_agility = pre_savvy = pre_aptitude = 20;
		fp = fp_level = fp_plus = mp = mp_level = mp_plus = 0;
		money = potential = 100;
		exp = 0;

		image_id = sex = class_id = teacher_id = 0;
		bliss = married = unknow1 = unknow2 = unknow3 = 0;

		// 测试：随机初始化的面貌
		face_level = (int) (Math.random() * 12);

		item_package[0][0] = 42;
		item_package[0][1] = 0;
		item_package[0][2] = 1;
		int i;
		for (i = 0; i < 16; i++)
			equips[i] = 0;
		for (i = 1; i < MAX_ITEM_SIZE; i++)
			item_package[i][0] = item_package[i][1] = item_package[i][2] = 0;

		for (i = 0; i < 8; i++)
			select_skills[i] = 255;
		for (i = 0; i < MAX_SKILL_SIZE; i++) {
			skills[i][0] = 255;
			skills[i][1] = skills[i][2] = skills[i][3] = skills[i][4] = 0;
		}

		for (i = 0; i < 40; i++)
			lasting_tasks[i] = 0;
		GmudTemp.ClearAllData();
		played_time = 0;
		// for(i = 0; i<16; i++)
		// player_name[i] = 0;
		// for(i = 0; i<16; i++)
		// weapon_name[i] = 0;
		// for(i = 0; i<16; i++)
		// consort_name[i] = 0;

		// initialization
		player_name = "[No Name]";
		weapon_name = null;
		consort_name = null;
	}

	public Player() {
		reset();
	}

	// dead
	void PlayerDead() {
		potential /= 2;
		exp -= (exp / 10);
		money -= (money / 5);
		bliss -= util.RandomInt(60);
		if (bliss < 0) {
			bliss = 0;
		}

		// skill--
		for (int i = 0; i < MAX_SKILL_SIZE; i++) {
			if (skills[i][0] == 255 || skills[i][1] == 0)
				continue;
			--skills[i][1];
			if (skills[i][1] <= 0) {
				skills[i][1] = skills[i][2] = skills[i][3] = skills[i][4] = 0;
			}
		}
	}

	// 门派id
	int GetClassID() {
		return (class_id > ClassID._MAX_) ? 0 : class_id;
	}

	void SetClassID(int classId) {
		class_id = classId;
	}

	boolean isClass(int classId) {
		return class_id == classId;
	}

	/**
	 * 
	 * @return 食物最大
	 */
	int GetFoodMax() {
		return 75 + 15 * pre_force;
	}

	/**
	 * 饮水最大
	 * 
	 * @return
	 */
	int GetWaterMax() {
		return 60 + 15 * pre_force;
	}

	/**
	 * 取生命值(Health Point)上限，与内力、年龄有关
	 * 
	 * @return HP Max
	 */
	int GetHPMax() {
		return 100 + (fp_level / 4) + (GetAge() * 20);
	}

	/**
	 * 年龄增量（14起）
	 * 
	 * @return
	 */
	int GetAge() {
		return (int) (played_time / AGE_TIME);
	}

	/**
	 * 计算技能的等级，如果此技能无效或未学，返回0
	 * 
	 * @param id
	 *            技能ID, 见 {@link Skill#skill_name}
	 * @return 技能等级 0技能无效或未学或技能为0
	 */
	int GetSkillLevel(int id) {
		if (id > 53 || id < 0)
			return 0;
		for (int j = 0; j < MAX_SKILL_SIZE; j++) {
			final int skill_id = skills[j][0];
			if (skill_id >= 0 && skill_id <= 53 && skill_id == id)
				return skills[j][1];
		}
		return 0;
	}

	/**
	 * @return 招式总数
	 */
	int GetSkillNumber() {
		int c = 0;
		for (int i = 0; i < MAX_SKILL_SIZE; i++)
			if (skills[i][0] != 255)
				c++;
		return c;
	}

	/**
	 * @return 角色等级
	 */
	int GetPlayerLevel() {
		int weapon_id = GetWeaponID();
		int weapon_type = Items.item_attribs[weapon_id][1];
		int level = 0;
		if (weapon_id == 0) {
			level += (GetSkillLevel(select_skills[0]) * 2) / 3;
			level += GetSkillLevel(1) / 3;
			level += (GetSkillLevel(select_skills[3]) * 2) / 3;
			level += GetSkillLevel(0) / 3;
			level += (GetSkillLevel(select_skills[2]) * 2) / 3;
			level += GetSkillLevel(7) / 3;

			// 招架技能
			int l1 = select_skills[4];
			if (l1 != 255 && Skill.skill_type[l1] == 0)
				level += (GetSkillLevel(l1) * 2) / 3;

			// 基本招架
			level += GetSkillLevel(8) / 3;

			level = level / 4;
		} else {
			int i2 = select_skills[1];
			if (i2 != 255) {
				int j2 = Skill.skill_weapon_type[i2];
				if (weapon_type == j2) {
					// 有　武器　且技能与兵刃类型相符
					level += (GetSkillLevel(i2) * 2) / 3;
					int k2 = Skill.weapon_to_base_skill[weapon_type];
					level += GetSkillLevel(k2) / 3;
				}
			}

			level += (GetSkillLevel(select_skills[3]) * 2) / 3;
			level += GetSkillLevel(0) / 3;
			level += (GetSkillLevel(select_skills[2]) * 2) / 3;
			level += GetSkillLevel(7) / 3;
			int l2 = select_skills[4];
			if (l2 != 255 && Skill.skill_type[l2] == 1)
				level += (GetSkillLevel(l2) * 2) / 3;
			level += GetSkillLevel(8) / 3;

			level = level / 4;
		}
		if (level > 255)
			level = 255;
		if (level < 0)
			level = 0;
		return level;
	}

	// 平均技能等级
	int GetSkillAverageLevel() {
		int count = 0;
		int sum_level = 0;
		for (int i = 0; i < MAX_SKILL_SIZE; i++)
			if (skills[i][0] != 255) {
				count++;
				sum_level += skills[i][1];
			}

		if (count == 0)
			return 0;
		else
			return sum_level / count;
	}

	/**
	 * 
	 * @return 轻功等级
	 */
	int GetFlySkillLevel() {
		return (GetSkillLevel(select_skills[2]) + GetSkillLevel(7) / 2);
	}

	/**
	 * 设置相貌等级,含技能提升 {驻颜术} {扶桑忍术}
	 * 
	 * @return
	 */
	int SetFaceLevel() {
		int i = GetSkillLevel(10) / 15 + GetSkillLevel(26) / 15 + face_level;
		return (i > 13) ? 13 : i;
	}

	/**
	 * 
	 * @return 相貌等级 [0-13]
	 */
	int GetFaceLevel() {
		if (GetAge() < 2)
			return -1;

		return SetFaceLevel();
	}

	/**
	 * 
	 * @return 加力max
	 */
	int GetPlusFPMax() {
		int base_level = GetSkillLevel(0);
		if (0 == base_level) {
			return 0;
		} else {
			int skill_level = GetSkillLevel(select_skills[3]);
			return (base_level / 2 + skill_level) / 2;
		}
	}

	/**
	 * Mana Point
	 * 
	 * @return 法点max
	 */
	int GetPlusMPMax() {
		// "基本法术"
		int base_level = GetSkillLevel(4);
		if (0 == base_level) {
			return 0;
		} else {
			int skill_level = GetSkillLevel(select_skills[6]);
			return (base_level / 2 + skill_level) / 2;
		}
	}

	/**
	 * 
	 * @return 武器ID
	 */
	int GetWeaponID() {
		return equips[15];
	}

	/** 后天膂力 */
	int GetForce() {
		int base_level = GetSkillLevel(1);

		// 加上自制武器的属性
		int j1 = 0;
		if (lasting_tasks[9] != 0 && lasting_tasks[8] / 256 == 4
				&& equips[15] == 77)
			j1 = 20 - (lasting_tasks[8] & 3) * 5/* Items.item_attribs[77][4] */; // 自制武器加成，下同

		return base_level / 10 + pre_force + j1;
	}

	/** 敏捷 */
	int GetAgility() {
		int base_level = GetSkillLevel(7);

		// 加上自制武器的属性
		int j1 = 0;
		if (lasting_tasks[9] != 0 && lasting_tasks[8] / 256 == 3
				&& equips[15] == 77)
			j1 = 20 - (lasting_tasks[8] & 3) * 5/* Items.item_attribs[77][4] */;

		return base_level / 10 + pre_agility + j1;
	}

	/** 悟性 */
	int GetSavvy() {
		int base_level = GetSkillLevel(9);

		// 加上自制武器的属性
		int j1 = 0;
		if (lasting_tasks[9] != 0 && lasting_tasks[8] / 256 == 6
				&& equips[15] == 77)
			j1 = 20 - (lasting_tasks[8] & 3) * 5/* Items.item_attribs[77][4] */;

		return base_level / 10 + pre_savvy + j1;
	}

	/** 根骨 */
	int GetAptitude() {
		int base_level = GetSkillLevel(0);

		// 加上自制武器的属性
		int j1 = 0;
		if (lasting_tasks[9] != 0 && lasting_tasks[8] / 256 == 5
				&& equips[15] == 77)
			j1 = 20 - (lasting_tasks[8] & 3) * 5/* Items.item_attribs[77][4] */;

		return base_level / 10 + pre_aptitude + j1;
	}

	/** 婚姻 */
	String GetConsortName() {
		final String str;
		if (null == consort_name) {
			if (0 == sex)
				str = Res.STR_CONSORT_NONE_M;
			else
				str = Res.STR_CONSORT_NONE_F;
		} else {
			if (0 == sex) {
				str = String.format(Res.STR_CONSORT_IS_M, consort_name);
			} else {
				str = String.format(Res.STR_CONSORT_IS_F, consort_name);
			}
		}
		return str;
	}

	/**
	 * 重置 技能升级 需要的点数 = (当前等级+1)*(当前等级+1) / 悟性 * 2 * 10 + Rnd(16)
	 * 
	 * @param id
	 */
	void SetSkillUpgrate(int id) {
		int level = skills[id][1] + 1;
		int point = (((level + 1) * (level + 1)) / GetSavvy()) * 2 * 10;
		if (point < 10)
			point = 10;
		point += util.RandomInt(16);
		skills[id][4] = point;
		// skills[id][2] = 0;
	}

	// 出手等级
	int GetAttackLevel() {
		int attack = GetForce();
		int weaponId = GetWeaponID();
		if (weaponId != 0)
			attack += Items.item_attribs[weaponId][2];
		attack += fp_plus;
		if (attack > 5 * 20)
			return 5;
		return (attack) / 20;
	}

	/**
	 * 打坐速度
	 * 
	 * @return [1,60]
	 */
	int GetFPSpeed() {
		final int base_level = GetSkillLevel(0);
		final int skill_level = GetSkillLevel(select_skills[3]);
		final int aptitude = GetAptitude();
		// 基本内功/20 + 门派内功/10 + 后天根骨/5
		int speed = (base_level / 2 + skill_level) / 10 + aptitude / 5;
		if (speed <= 0)
			speed = 1;
		if (speed > 60)
			speed = 60;
		return speed;
	}

	/**
	 * 冥思速度
	 * 
	 * @return [1,60]
	 */
	int GetMPSpeed() {
		final int base_level = GetSkillLevel(4);
		final int skill_level = GetSkillLevel(select_skills[6]);
		final int savvy = GetSavvy();
		// 基本法术/20 + 选择法术/10 + 后天悟性/5
		int speed = (base_level / 2 + skill_level) / 10 + savvy / 5;
		if (speed <= 0)
			speed = 1;
		if (speed > 60)
			speed = 60;
		return speed;
	}

	/** 学习速度 [1,60] */
	int GetStudySpeed() {
		// 后天悟性 / 2
		int speed = GetSavvy() / 2;
		if (speed <= 0)
			speed = 1;
		if (speed > 60)
			speed = 60;
		return speed;
	}

	/** 练功速度 [1,60] */
	int GetPracticeSpeed(int index) {
		final int skill_id = skills[index][0];
		final int skill_level;
		if (skill_id == select_skills[2]) {
			// 轻功
			skill_level = GetSkillLevel(7);
		} else {
			final int weapon_type = Skill.skill_weapon_type[skill_id];
			if (weapon_type == 0) {
				skill_level = GetSkillLevel(1);
			} else {
				int base_skill = Skill.weapon_to_base_skill[weapon_type];
				skill_level = GetSkillLevel(base_skill);
			}
		}

		// 基本内功/10 + 门派内功/5 + 对应基本招式 / 5
		int speed = (GetSkillLevel(0) / 2 + GetSkillLevel(select_skills[3]))
				/ 5 + skill_level / 5;
		if (speed <= 0)
			speed = 1;
		if (speed > 60)
			speed = 60;
		return speed;
	}

	/**
	 * 可练功招式数, [0,2] [拳脚，兵刃，轻功]
	 * 
	 * @return 招式数量 GmuTemp.temp_array_20_2[0,2] 用于保存技能等级
	 */
	int GetPracticeSkillNumber() {
		int i1 = 0;
		for (int j1 = 0; j1 < 3; j1++) {
			int k1;
			int l1;
			if (select_skills[j1] != 255
					&& (l1 = GetSkillLevel(k1 = select_skills[j1])) != 0) {
				GmudTemp.temp_array_20_2[i1][0] = k1; // 技能ID
				GmudTemp.temp_array_20_2[i1][1] = l1; // 等级
				i1++;
			}
		}
		return i1;
	}

	/** 检查是否存在id物品有num个，返回 背包索引，如果不存在物品或数量不够，返回-1 */
	int ExistItem(int item_id, int num) {
		for (int index = 0; index < MAX_ITEM_SIZE; index++)
			if (item_package[index][0] == item_id
					&& item_package[index][2] >= num)
				return index;

		return -1;
	}

	/**
	 * 增加一个物品
	 * 
	 * @param item_id
	 *            物品ID
	 * @return
	 */
	boolean GainOneItem(int item_id) {
		if (item_id <= 0)
			return false;
		// item 可叠加
		if (Items.item_repeat[item_id] > 0) {
			for (int i = 0; i < MAX_ITEM_SIZE; i++) {
				if (item_package[i][0] == item_id && item_package[i][2] < 255) {
					item_package[i][2]++;
					return true;
				}
			}
		}
		// 找个空位添加物品
		for (int i = 0; i < MAX_ITEM_SIZE; i++) {
			if (item_package[i][0] == 0) {
				item_package[i][0] = item_id;
				item_package[i][1] = 0;
				item_package[i][2] = 1;
				return true;
			}
		}

		// 没法添加
		return false;
	}

	void LoseOneItem(int item_id) {
		for (int index = 0; index < MAX_ITEM_SIZE; index++)
			if (item_package[index][0] == item_id)
				DeleteOneItem(index);
	}

	/**
	 * 删除物品
	 * 
	 * @param package_index
	 *            当前包裹中的编号
	 * @param count
	 *            需要删除的数量
	 */
	void LoseItem(int package_index, int count) {
		int index = -1;

		// TODO: 其实应该不用下面的循环，待分析 @lnx
		if (0 <= package_index && package_index < MAX_ITEM_SIZE) {
			// 转成ID
			package_index = item_package[package_index][0];
		}

		// 根据物品ID找最后一个
		for (int i = 0; i < MAX_ITEM_SIZE; i++) {
			if (item_package[i][0] != package_index)
				continue;
			// 又找到了物品，并且没有被【已装备】
			if (index >= 0 && item_package[i][1] == 0) {
				index = i;
				continue;
			}
			if (index < 0) {
				// 记录找到的物品
				index = i;
			}
		}

		if (index >= 0) {
			item_package[index][2] -= count;
			if (item_package[index][2] <= 0) {
				// 物品删没有了，如果此物品【已装备】，需要解除装备
				if (item_package[index][1] == 1) {
					for (int i2 = 0; i2 < 16; i2++) {
						if (equips[i2] == package_index) {
							equips[i2] = 0;
						}
					}
				}
				item_package[index][0] = item_package[index][1] = item_package[index][2] = 0;
			}
		}
	}

	/** 删除一个背包中的物品， */
	void DeleteOneItem(int package_index) {
		if (package_index == -1)
			return;

		// 如果【已装备】则不删除
		if (item_package[package_index][1] == 1)
			return;

		int item_id = item_package[package_index][0];
		if (item_id < 0 || item_id > 91)
			return;

		int item_type = Items.item_attribs[item_id][0]; // 物品类型
		if (item_type == 2) {
			// 武器
			if (item_package[package_index][1] != 0)
				UnEquipWeapon();
		} else if (item_type == 3 && item_package[package_index][1] != 0) {
			int item_equip_type = Items.item_attribs[item_id][1]; // 装备类型，
			UnEquipArmor(item_equip_type);
		}
		item_package[package_index][0] = item_package[package_index][1] = item_package[package_index][2] = 0;
	}

	private boolean _LostItems(int package_index, int count) {
		if (item_package[package_index][2] >= count) {
			item_package[package_index][2] -= count;
			if (item_package[package_index][2] == 0) {
				int item_id = item_package[package_index][0];
				for (int i = 0; i < 16; i++)
					if (equips[i] == item_id)
						equips[i] = 0;

				item_package[package_index][0] = 0;
				item_package[package_index][1] = 0;
				DeleteOneItem(package_index);
			}
			return true;
		} else {
			return false;
		}
	}

	String UseItem(int package_index) {
		int item_id = item_package[package_index][0];
		if (item_id < 0 || item_id > 91)
			return "";
		int item_group = Items.item_attribs[item_id][0]; // 物品类别
		int item_type = Items.item_attribs[item_id][1]; // 小类型
		switch (item_group) // switch type
		{
		case 5:
		default:
			break;

		case 0: { // 吃食物
			// 战斗状态不能吃食物
			if (Battle.sBattle != null)
				break;
			if (food >= GetFoodMax())
				return "你再也吃不下任何东西了";
			food += Items.item_attribs[item_id][2];
			water += Items.item_attribs[item_id][3];
			item_package[package_index][2]--;
			if (item_package[package_index][2] <= 0) {
				item_package[package_index][0] = 0;
				item_package[package_index][1] = 0;
				item_package[package_index][2] = 0;
			}
		}
			break;

		case 1: // 用药
			if (Battle.sBattle != null && item_type == 0) {
				// 疗伤，增加血量最大值hp-max
				int player_id = Battle.sBattle.m_player_id;
				int hp_max_tmp = Battle.sBattle.fighter_data[player_id][2];
				int hp_full_tmp = Battle.sBattle.fighter_data[player_id][3];
				if (hp_max_tmp >= hp_full_tmp)
					break;
				hp_max_tmp += Items.item_attribs[item_id][2];
				if (hp_max_tmp > hp_full_tmp)
					hp_max_tmp = hp_full_tmp;
				item_package[package_index][2]--;
				if (item_package[package_index][2] <= 0) {
					item_package[package_index][0] = 0;
					item_package[package_index][1] = 0;
					item_package[package_index][2] = 0;
				}
				Battle.sBattle.fighter_data[player_id][2] = hp_max_tmp;
				Battle.sBattle.stack_fighterdate_set(player_id, 2, hp_max_tmp);
				break;
			}
			if (item_type == 0) {
				if (hp_max == hp_full)
					break;
				hp_max += Items.item_attribs[item_id][2];
				if (hp_max > hp_full)
					hp_max = hp_full;
				item_package[package_index][2]--;
				if (item_package[package_index][2] <= 0) {
					item_package[package_index][0] = 0;
					item_package[package_index][1] = 0;
					item_package[package_index][2] = 0;
				}
				break;
			}
			if (item_type != 1 || Battle.sBattle != null)
				break;
			if (lasting_tasks[13] < 10000) {
				// 增加内力上限
				fp_level += item_package[package_index][2];
				lasting_tasks[13] += 1;
			}
			item_package[package_index][2]--;
			if (item_package[package_index][2] <= 0) {
				item_package[package_index][2] = 0;
				item_package[package_index][0] = 0;
				item_package[package_index][1] = 0;
			}
			break;

		case 2: // weapon
			if ((Battle.sBattle != null)
					&& (item_package[package_index][0] == 77))
				break; // 战斗的时候不换自制武器（影响玩家属性计算）

			if (item_package[package_index][1] == 0)
				EquipWeapon(package_index);
			else
				UnEquipWeapon();
			if (Battle.sBattle != null)
				Battle.sBattle.CopyPlayerEquips();
			break;

		case 3: // equip
		{
			if (item_package[package_index][1] == 0)
				EquipArmor(item_type, package_index);
			else
				UnEquipArmor(item_type);
			if (Battle.sBattle != null)
				Battle.sBattle.CopyPlayerEquips();
			break;
		}
		case 4: // read book
		{
			if (Battle.sBattle != null)
				break;

			int k2 = GetSkillLevel(9); // 读书写字
			if (k2 == 0)
				return "你还是个文盲!";
			int l2 = k2 / 2;
			if (l2 == 0)
				l2 = 1;
			int skill_id = Items.item_attribs[item_id][2];
			int hp_expend = Items.item_attribs[item_id][3];
			int max_level = Items.item_attribs[item_id][4];
			int require_EXP = Items.item_attribs[item_id][5];
			int skill_pos;
			if (GetSkillLevel(skill_id) == 0) {
				skill_pos = AddNewSkill(skill_id);
				if (skill_pos == -1) {
					return "";
				}

				skills[skill_pos][1] = 1;
				SetNewSkill(skill_id);
				/*
				 * wchar_t str[9]; wcscpy(str, "你学会了"); wcscat(str,
				 * Skill.skill_name[i3]); return str;
				 */
				return "你埋头研读,似乎有点心得";
			}
			skill_pos = SetNewSkill(skill_id);
			if (skills[skill_pos][4] <= 0) {
				SetSkillUpgrate(skill_pos);
			}
			if (max_level < skills[skill_pos][1])
				return "书上所说的对你太浅了";
			int k4 = (((require_EXP * 1000) / max_level) * (skills[skill_pos][1] + 1)) / 1000;
			if (exp < k4)
				return "你的实战经验不足";
			if (hp < hp_expend)
				return "你现在太疲倦了,没法研读";
			skills[skill_pos][2] += l2;
			hp -= hp_expend;
			if (skills[skill_pos][2] > skills[skill_pos][4]) {
				skills[skill_pos][4] = 0;
				skills[skill_pos][1] += 1;
				skills[skill_pos][2] = 0;
				SetSkillUpgrate(skill_pos);
				return "你的功夫进步了";
			} else {
				return "你埋头研读,似乎有点心得";
			}
		}
		}
		return "";
	}

	/** 装备物品，equip_index装备表的索引， package_index背包索引 */
	void EquipArmor(int equip_index, int package_index) {
		int item_id = item_package[package_index][0];
		if (item_id < 0 || item_id > 91)
			return;
		// 物品属性必须是【3装备】
		if (Items.item_attribs[item_id][0] != 3)
			return;
		if (equips[equip_index] != 0)
			UnEquipArmor(equip_index);
		item_package[package_index][1] = 1;
		equips[equip_index] = item_id;
	}

	/** 卸除装备， equip_index当前装备表中的索引 */
	void UnEquipArmor(int equip_index) {
		int item_id = equips[equip_index];
		if (item_id == 0)
			return;

		// 将背包中物品去掉【已装备】的标志
		for (int i = 0; i < MAX_ITEM_SIZE; i++) {
			if (item_package[i][0] == item_id && item_package[i][1] != 0) {
				item_package[i][1] = 0;
				break;
			}
		}
		equips[equip_index] = 0;
	}

	/** 佩戴武器, item_index背包物品索引 */
	void EquipWeapon(int item_index) {
		int item_id = item_package[item_index][0];
		if (equips[15] != 0)
			UnEquipWeapon();
		if (item_id < 0 || item_id > 91)
			return;
		// 物品类别，必须是 2武器
		if (Items.item_attribs[item_id][0] != 2)
			return;
		// 将背包的物品置上【已装备】的标记
		item_package[item_index][1] = 1;
		// 添加的装备表
		equips[15] = item_id;
	}

	/** 卸除武器 */
	void UnEquipWeapon() {
		int item_id = equips[15];
		if (item_id == 0)
			return;

		for (int i = 0; i < MAX_ITEM_SIZE; i++) {
			if (item_package[i][0] == item_id && item_package[i][1] != 0) {
				// 刪除【已装备】的标记
				item_package[i][1] = 0;
				break;
			}
		}
		// 卸除装备
		equips[15] = 0;
	}

	int CopyItemList() {
		int top = 0;
		for (int i = 0; i < MAX_ITEM_SIZE; i++) {
			if (item_package[i][0] != 0 && item_package[i][1] == 0) {
				GmudTemp.temp_array_32_2[top][0] = item_package[i][0];
				GmudTemp.temp_array_32_2[top][1] = item_package[i][2];
				top++;
			}
		}
		return top;
	}

	/**
	 * 添加新技能，如果技能已存在，则返回序号，否则新建一个等级为1的技能。
	 * 
	 * @param id
	 * @return
	 */
	int AddNewSkill(int id) {
		if (GetSkillLevel(id) > 0)
			return SetNewSkill(id);
		for (int j1 = 0; j1 < MAX_SKILL_SIZE; j1++)
			if (skills[j1][0] == 255) {
				skills[j1][0] = id;
				skills[j1][1] = 1;
				skills[j1][2] = 0;
				skills[j1][3] = 0;
				SetSkillUpgrate(j1);
				return j1;
			}

		return -1;
	}

	/**
	 * 添加新技能，如果该技能已经存在，直接返回它的索引序号
	 * 
	 * @param skill_id
	 *            技能ID
	 * @return -1异常或无效技能 [0,32)技能序号
	 */
	int SetNewSkill(int skill_id) {
		if (skill_id < 0 || skill_id > 53)
			return -1;

		for (int i = 0; i < MAX_SKILL_SIZE; i++)
			if (skills[i][0] >= 0 && skills[i][0] <= 53
					&& skills[i][0] == skill_id)
				return i;

		for (int i = 0; i < MAX_SKILL_SIZE; i++)
			if (skills[i][0] > 53) {
				skills[i][0] = skill_id;
				skills[i][1] = 0;
				skills[i][2] = skills[i][3] = skills[i][4] = 0;
				return i;
			}

		return -1;
	}

	/**
	 * 勾选技能
	 * 
	 * @param skill_id
	 * @param type
	 */
	void SelectSkill(int skill_id, int type) {
		if (skill_id < 10)
			return;
		int cur_id = select_skills[type];
		if (cur_id != 255)
			UnselectSkill(type);
		select_skills[type] = skill_id;
	}

	/** 取消选择技能 */
	void UnselectSkill(int type) {
		select_skills[type] = 255;
	}

	/**
	 * 请教
	 * 
	 * @param skill_index
	 *            玩家技能表中的索引
	 * @param maxlevel
	 *            最大可学的技能
	 * @return 0正常 1经验不足 2潜能不足 3钱不足 4等级超过 5升级
	 */
	int StudySkill(int skill_index, int maxlevel) {
		int skill_id = skills[skill_index][0];
		int skill_level = skills[skill_index][1];
		if (skill_level > maxlevel)
			return 4;

		if (exp < skill_level * 2 * (skill_level * 2))
			return 1;

		if (skills[skill_index][4] <= 0)
			SetSkillUpgrate(skill_index);

		int point = skills[skill_index][2];
		if (point > skills[skill_index][4]) {
			skills[skill_index][2] = skills[skill_index][4];
			return 0;
		}

		if (point == skills[skill_index][4]) {
			skills[skill_index][2] = 0;
			skills[skill_index][4] = 0;
			skills[skill_index][1] += 1;
			return 5;
		}

		if (potential <= 0)
			return 2;

		if (skill_id == 9 && money <= 0)
			return 3;

		if ((point += 4) / 10 != skills[skill_index][2] / 10) {
			potential -= 1;
			if (skill_id == 9)
				money -= 1;
		}
		skills[skill_index][2] = point;
		return 0;
	}

	/**
	 * 练功
	 * 
	 * @param index
	 *            技能的序号
	 * @return 0 正常学习 1 很难提高，需要向师傅请教 2 需要提升内功 3 打坐不够 4 没有趁手兵器 5 有伤 6 升级 7基本功没有
	 */
	int PracticeSkill(int index) {

		// 要先疗伤
		if (hp_full != hp_max)
			return 5;

		// 计算基本功的等级
		final int base_level;

		final int skill_id = skills[index][0]; // 实际技能ID
		if (skill_id == select_skills[2]) {
			// 如果是已选择的轻功
			base_level = GetSkillLevel(7);
		} else {
			final int weapon_type = Skill.skill_weapon_type[skill_id];
			if (weapon_type == 0) {
				// 拳脚类
				base_level = GetSkillLevel(1);
			} else {
				// 检查武器
				final int weapon_id = GetWeaponID();
				if (weapon_id <= 0 || weapon_id > 92
						|| weapon_type != Items.item_attribs[weapon_id][1])
					return 4;

				// 计算武器类基本功的等级
				final int base_skill_weapon = Skill.weapon_to_base_skill[weapon_type];
				if (base_skill_weapon != 255) {
					base_level = GetSkillLevel(base_skill_weapon);
				} else {
					base_level = 0;
				}
			}
		}

		// 没有基本功
		if (base_level == 0)
			return 7;

		// 已超过基本功
		int skill_level = skills[index][1];
		if (skill_level > base_level)
			return 1;

		// 检查内功修为是否足够 (基本内功/2 + 门派内功)
		int k3 = GetSkillLevel(0) / 2 + GetSkillLevel(select_skills[3]);
		if (skill_level > k3)
			return 2;

		// 检查内力等级是否足够
		int l3 = k3 * 10;
		if (class_id == 8)
			l3 += exp / 1000;
		int i4 = ((skill_level + 1) * ((l3 * 1000) / k3)) / 1000;
		if (i4 > fp_level)
			return 3;

		if (skills[index][4] <= 0)
			SetSkillUpgrate(index);

		int k4 = skills[index][2];
		if (k4 > skills[index][4]) {
			skills[index][2] = skills[index][4];
			return 0;
		}
		if (k4 == skills[index][4]) {
			skills[index][2] = 0;
			skills[index][4] = 0;
			skills[index][1] += 1;
			return 6;
		} else {
			k4 += 1 + base_level / 5 + GetSavvy() / 10;
			skills[index][2] = k4;
			return 0;
		}
	}

	// 所学技能最高等级
	int GetMaxSkillLevel() {
		int max_level = 0;
		for (int i = 0; i < MAX_SKILL_SIZE; i++) {
			int level = skills[i][1];
			if (skills[i][0] != 255 && level > max_level)
				max_level = level;
		}
		return max_level;
	}

	/** 吸气 */
	String Breathing() {
		final int base_level = GetSkillLevel(0);
		final int skill_id = select_skills[3];
		if (skill_id >= 255 || base_level <= 0) {
			return Res.STR_NO_INNER_KONGFU_STRING;
		}
		if (hp >= hp_max) {
			hp = hp_max;
			return Res.STR_BREATH_HEALTHFUL;
		}
		if (fp_level < 50 || fp < 50) {
			return Res.STR_BREATH_NEED_MORE_FP;
		}
		int j1 = (hp_max - hp) * 2;
		if (j1 > fp)
			j1 = fp;
		fp -= j1;
		hp += j1 / 2;
		if (hp > hp_max)
			hp = hp_max;
		return Res.STR_BREATH_SUCCESS;
	}

	/** 疗伤 */
	String Recovery() {
		final int base_leve = GetSkillLevel(0);
		final int skill_id = select_skills[3];
		if (skill_id >= 255 || base_leve <= 0) {
			return Res.STR_NO_INNER_KONGFU_STRING;
		}
		if (hp_max >= hp_full) {
			hp_max = hp_full;
			return Res.STR_RECOVER_HEALTHFUL;
		}
		final int skill_level = GetSkillLevel(skill_id);
		final int level = base_leve / 2 + skill_level;
		if (level < 45) {
			return Res.STR_RECOVER_KONGFU_LEVEL_TOO_LOW;
		}
		if (hp_max <= 0 || hp_full / hp_max > 4) {
			return Res.STR_RECOVER_INJURED_TOO_HEAVY;
		}
		if (fp_level < 150 || fp < 150) {
			return Res.STR_RECOVER_NEED_MORE_FP;
		}
		fp -= 50;
		hp_max += (level * 2) / 3;
		if (hp_max > hp_full)
			hp_max = hp_full;
		return Res.STR_RECOVER_SUCCESS;
	}

	/**
	 * 打坐
	 * 
	 * @return 0:正常 1:没有选择 2:基本法术不够 3:已达到上限
	 */
	int Meditation() {
		// 基本内功
		int base_level = GetSkillLevel(0);
		if (base_level == 0)
			return 2;

		// 门派内功
		int skill_level = GetSkillLevel(select_skills[3]);
		if (skill_level == 0)
			return 1;

		if (fp >= fp_level * 2) {
			// max = 基本内功*5+门派内功*10+（年龄-14）*10+经验/1000
			int max = (base_level / 2 + skill_level) * 10 + exp / 1000;
			if (fp_level < max) {
				fp = 0;
				fp_level += 1;
				return 0;
			} else {
				fp = fp_level;
				return 3;
			}
		} else {
			// @lnx: 增加一个与技能相关的量，让打坐加快
			fp += 2 + (base_level + skill_level * 2) / 40;
			return 0;
		}
	}

	/**
	 * 冥思
	 * 
	 * @return 0:正常 1:没有选择 2:基本法术不够 3:已达到上限
	 */
	int Think() {
		// 基本法术
		int base_level = GetSkillLevel(4);
		if (base_level == 0) // basic == 0
			return 2;

		int skill_level = GetSkillLevel(select_skills[6]);
		if (skill_level == 0)
			return 1;

		if (mp >= mp_level * 2) {
			int max = (base_level / 2 + skill_level) * 10;
			if (class_id == 8)
				max += exp / 1000;
			if (mp_level < max) {
				mp = 0;
				mp_level += 1;
				return 0;
			} else {
				mp = mp_level;
				return 3;
			}
		} else {
			// @lnx: 增加一个与技能相关的量，让打坐加快
			mp += 3 + (base_level + skill_level * 2) / 40;
			return 0;
		}
	}

	/**
	 * 
	 * @param i1
	 * @param type
	 *            物品类型 {@link Items#item_attribs}
	 * @return (第一个物品的索引<<8)|(物品个数)
	 */
	int CopyItemData(int i1, final int type) {
		GmudTemp.Clear32Data();
		i1++;
		int k1 = 0;
		int pos = 0;
		for (int i2 = i1; i2 < MAX_ITEM_SIZE; i2++) {
			int item_id = item_package[i2][0];
			if (item_id == 0 || item_id > 91)
				continue;
			int item_type = Items.item_attribs[item_id][0];

			// other
			if (item_type == 5)
				item_type = 4;
			if (item_type != type)
				continue;

			if (pos == 0) {
				GmudTemp.temp_array_32_2[pos][0] = i2;
				GmudTemp.temp_array_32_2[pos][1] = item_package[i2][1];
				pos++;
				k1 = i2;
			} else {
				GmudTemp.temp_array_32_2[pos][0] = i2;
				GmudTemp.temp_array_32_2[pos][1] = item_package[i2][1];
				pos++;
			}
		}

		if (pos == 0)
			return 0;
		else
			return (k1 << 8) + pos;
	}

	/**
	 * 
	 * @param i1
	 * @param type
	 *            技能类型
	 * @return
	 */
	int CopySkillData(int i1, final int type) {
		GmudTemp.Clear32Data();
		++i1;
		int k1 = 0;
		int pos = 0;
		final int sel_skill_id = select_skills[type];
		for (int i2 = i1; i2 < MAX_SKILL_SIZE; i2++) {
			final int skill_id = skills[i2][0];
			if (skill_id < 0 || skill_id > 53)
				continue;

			final int skill_type = Skill.skill_type[skill_id];

			// 技能类别不符　且　非招架（４）　或　非拳脚（０）兵刃（１）　基本功（<=9）
			if (skill_type != type
					&& (type != 4 || skill_type >= 2 || skill_id <= 9))
				continue;

			if (pos == 0) {
				GmudTemp.temp_array_32_2[pos][0] = i2;
				GmudTemp.temp_array_32_2[pos][1] = (sel_skill_id != skill_id ? 0
						: 1);
				pos++;
				k1 = i2;
				continue;
			}
			if (pos < MAX_SKILL_SIZE) {
				GmudTemp.temp_array_32_2[pos][0] = i2;
				GmudTemp.temp_array_32_2[pos][1] = (sel_skill_id != skill_id ? 0
						: 1);
			}
			pos++;
		}

		if (pos == 0)
			return 0;
		else
			return (k1 << 8) + pos;
	}
}
