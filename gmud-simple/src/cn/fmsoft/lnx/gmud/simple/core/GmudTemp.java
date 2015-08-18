package cn.fmsoft.lnx.gmud.simple.core;

public class GmudTemp {
	static private int a = 0;
	static Object timer_thread_handle;

	/**
	 * (size:32,2) [0]索引{@link Player#skills}or {@link Player#item_package},
	 * [1]0或1
	 */
	static int temp_array_32_2[][] = new int[32][2];
	static int temp_array_20_2[][] = new int[20][2];

	static void ClearAllData() {
		int i;
		for (i = 0; i < 32; i++) {
			temp_array_32_2[i][0] = 255;
			temp_array_32_2[i][1] = 0;
		}
		for (i = 0; i < 20; i++) {
			temp_array_20_2[i][0] = 255;
			temp_array_20_2[i][1] = 0;
		}
		/*
		 * for(i = 0; i<40; i++) { temp_task_data[i] = 0; }
		 */
	}

	static void Clear32Data() {
		for (int i = 0; i < 32; i++) {
			temp_array_32_2[i][0] = 255;
			temp_array_32_2[i][1] = 0;
		}
	}

	static void Clear20Data() {
		for (int i = 0; i < 20; i++) {
			temp_array_20_2[i][0] = 255;
			temp_array_20_2[i][1] = 0;
		}
	}

	/** 单位时间片 5秒调用一次 */
	static void TimerFunc() {
		task.temp_tasks_data[5] += 5; // 捕快time
		task.temp_tasks_data[29] += 5; // 石料time
		task.temp_tasks_data[30] += 5; // 存档时间
		task.temp_tasks_data[31] += 5; // 年龄周期
		if (task.temp_tasks_data[31] >= 60) {
			++Gmud.sPlayer.played_time; // ++play time
			task.temp_tasks_data[31] -= 60;
		}
		Gmud.UpdatePlayTime(Gmud.sPlayer.played_time,
				task.temp_tasks_data[31]);
		task.temp_tasks_data[32] += 5;
		for (int i = 0; i < 2; i++) {
			if (a < 0 || a > 147) // npc recover
			{
				Map.NPC_flag[156] = 0;
				NPC.ResetData(156);
				Map.NPC_flag[157] = 0;
				NPC.ResetData(157);
				a = 0;
			}
			if (Map.NPC_flag[a] == 1) {
				Map.NPC_flag[a] = 0;
				NPC.ResetData(a);
			}
			a++;
			if (a > 147)
				a = 0;
		}

		if (Battle.sBattle == null) {
			task.temp_tasks_data[33] += 5; // recover Hp fp mp time
			if (task.temp_tasks_data[33] > 15) {
				// 更新最大生命值（与年龄和内力上限有关）
				final int hp_full = Gmud.sPlayer.GetHPMax();
				Gmud.sPlayer.hp_full = hp_full;

				final int food = Gmud.sPlayer.food;
				final int water = Gmud.sPlayer.water;
				if (food > 0 && water > 0) {
					final int aptitude = Gmud.sPlayer.GetAptitude() / 10;

					int hp = Gmud.sPlayer.hp;
					int hp_max = Gmud.sPlayer.hp_max;
					if (hp < hp_max) {
						hp += aptitude;
						if (hp > hp_max)
							hp = hp_max;
						Gmud.sPlayer.hp = hp;
					}
					if (hp_max < hp_full) {
						hp_max += aptitude;
						if (hp_max > hp_full)
							hp_max = hp_full;
						Gmud.sPlayer.hp_max = hp_max;
					}

					int fp = Gmud.sPlayer.fp;
					final int fp_level = Gmud.sPlayer.fp_level;
					if (fp < fp_level) {
						fp += aptitude;
						if (fp > fp_level)
							fp = fp_level;
						Gmud.sPlayer.fp = fp;
					}

					int mp = Gmud.sPlayer.mp;
					final int mp_level = Gmud.sPlayer.mp_level;
					if (mp < mp_level) {
						mp += aptitude;
						if (mp > mp_level)
							mp = mp_level;
						Gmud.sPlayer.mp = mp;
					}
				}
				if (food > 0)
					Gmud.sPlayer.food = food - 1;
				if (water > 0)
					Gmud.sPlayer.water = water - 1;
				task.temp_tasks_data[33] = 0;
			}
		}
	}
}
