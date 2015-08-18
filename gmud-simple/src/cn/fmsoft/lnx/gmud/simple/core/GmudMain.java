/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * GmudMain: the main thread.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple.core;

import cn.fmsoft.lnx.gmud.simple.core.GmudData.LastTask;
import android.content.Context;

public class GmudMain extends Thread {

	static final int STATE_INVALID = 0x00;
	static final int STATE_UNINITALIZE = 0x01;
	static final int STATE_WAIT_UI = 0x02;
	static final int STATE_WAIT_NEW_NAME = 0x08;
	static final int STATE_RUNNING = 0x10;

	static int sStaus = STATE_INVALID;

	private Context mContext;

	public GmudMain(Context context) {
		super("GmudMain-thread");
		mContext = context;
		sStaus = STATE_INVALID;
	}

	@Override
	public void run() {
		sStaus = STATE_UNINITALIZE;

		try {

			Gmud.prepare(mContext);

			// 等待UI方面准备好
			// waiting... for Video call resume!
			Gmud.GmudDelay(1);

			// 启动游戏主线程
			sStaus = STATE_RUNNING;

			Input.InitInput();
			Input.ProcessMsg();
			Video.VideoUpdate();

			// 开启自动回血线程
			new Thread("Timer") {
				@Override
				public void run() {
					while (Input.Running) {
						if (Gmud.IsRunning() && Gmud.PLAYING) {
							GmudTemp.TimerFunc();
							try {
								sleep(5000);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}.start();

			gamemain();

		} catch (Exception e) {
			e.printStackTrace();
		}

		sStaus = STATE_INVALID;
	}

	/**
	 * 重新开始游戏，创建一个新的人物
	 */
	static synchronized void NewGame() {

		if ((sStaus & STATE_RUNNING) == 0) {
			return;
		}

		final Player player = Player.getInstance();
		final NewGame ng = new NewGame();
		ng.ShowStory();
		final int id = ng.SelectChar();
		player.sex = (id > 1) ? 1 : 0;
		player.image_id = id;

		// ng.EnterName(hwnd);
		player.player_name = Gmud.WaitForNewName(0);

		ng.AllocPoint(player);
	}

	static synchronized void Restart() {
		final Map map = Map.getInstance();
		final Player player = Player.getInstance();
		player.reset();

		Input.ClearKeyStatus();

		GmudMain.NewGame();

		// initialize & load
		task.reset();
		NPC.InitData();
		Battle.sBattle = null;
		map.LoadMap(0);
		// map.m_stack_pointer = 0;
		map.LoadPlayer(player.image_id);

		// enter map
		Video.VideoClear();
		map.SetPlayerLocation(0, 4);
		map.DrawMap(0);
		Video.VideoUpdate();
	}

	void SetWeapon(Player player) {
		int i1 = 0;
		int j1 = 0;
		boolean flag = false;
		int attack = player.lasting_tasks[7];
		String s1 = GmudData.weapon_first_name[attack - 1]; // first word
		if (player.lasting_tasks[8] / 256 > 0) {
			// 自身属性: 天渊 → 合灵
			i1 = player.lasting_tasks[8] / 256 - 1;
			j1 = player.lasting_tasks[8] & 255;
			s1 += GmudData.weapon_last_name[i1 * 4 + j1];
			flag = true;
		} else if (player.lasting_tasks[8] >= 24) {
			// 未定属性：绝世 → 蟠桃
			int l1 = player.lasting_tasks[8];
			s1 += GmudData.weapon_last_name[l1];
		}
		int i2 = (player.lasting_tasks[7] * player.GetSavvy()) / 10;
		Items.item_names[77] = s1.toString(); // set Desc word
		Items.item_attribs[77][0] = 2; // set item type
		Items.item_attribs[77][1] = player.lasting_tasks[5]; // set weapon type
		Items.item_attribs[77][2] = i2; // set attack
		if (flag)
			if (i1 == 1)
				Items.item_attribs[77][3] = 20 - j1 * 5; // +命中
			else if (i1 == 0)
				Items.item_attribs[77][4] = 20 - j1 * 5; // +回避
		if (player.lasting_tasks[8] >= 24 && !flag) // +附属属性 该属性暂时无用
			Items.item_attribs[77][5] = player.lasting_tasks[8] - 10;
		else
			Items.item_attribs[77][5] = 20 - j1 * 5;
		if (player.ExistItem(77, 1) < 0) // exist?
			player.GainOneItem(77); // add one
	}

	void gamemain() {
		final Map map = Map.getInstance();
		final Player player = Player.getInstance();

		if (!Gmud.LoadSave()) {
			NewGame();
		}

		// new 自制武器
		if (1 == player.lasting_tasks[LastTask.NEW_WEAPON]) {
			// enter name
			player.weapon_name = Gmud.WaitForNewName(1);

			// 属性
			int k1 = 0;
			if (util.RandomInt(100) < 5 + player.bliss) // rand < q.A +5
				k1 = (util.RandomInt(6) + 1) * 256 + util.RandomInt(4);
			else if (util.RandomInt(100) < player.bliss) // rand < q.A
				k1 = 24 + util.RandomInt(13);

			player.lasting_tasks[4] = 0;
			player.lasting_tasks[7] = util.RandomInt(52) + 1; // attack
			player.lasting_tasks[8] = k1;
			player.lasting_tasks[9] = 1;
			// WriteSave(); // savedata
			Video.VideoClear();
			Video.VideoDrawString("您的武器铸造成功！", 20, 35);
			Video.VideoUpdate();
			Gmud.GmudWaitAnyKey();
		}
		if (player.lasting_tasks[9] == 1)
			SetWeapon(player); // 自制武器调整
		if (player.lasting_tasks[LastTask.PK_GANG] > 0
				&& player.lasting_tasks[LastTask.PK_GANG] < 8) {
			// 如果正在打坛，须防止玩家丢了地图
			final int item_id = 79 + player.lasting_tasks[LastTask.PK_GANG];
			if (player.ExistItem(item_id, 1) < 0)
				player.GainOneItem(item_id);
		}
		// initialize & load
		task.reset();
		NPC.InitData();
		Battle.sBattle = null;
		map.LoadMap(0);
		// map.m_stack_pointer = 0;
		map.LoadPlayer(player.image_id);

		// enter map
		Video.VideoClear();
		map.SetPlayerLocation(0, 4);
		map.DrawMap(0);
		Video.VideoUpdate();

		// GmudTemp.timer_thread_handle = CreateThread(0, 0, StartTimer, 0, 0,
		// 0);

		Input.ClearKeyStatus();

		int last_key = 0;

		Gmud.PLAYING = true;

		while (Input.Running) {
			if ((last_key & Input.kKeyEnt) != 0) {
				map.KeyEnter();
				map.DrawMap(-1);
			} else if ((last_key & Input.kKeyExit) != 0) {
				UI.MainMenu();
				map.DrawMap(-1);
			} else if ((last_key & Input.kKeyFly) != 0) {
				UI.Fly();
				map.DrawMap(-1);
			} else if ((last_key & Input.kKeyUp) != 0) {
				if (map.GetCurOrientation() == Map.CharOrientation.UP) {
					map.DirUp();
				} else {
					map.SetPlayerLocation(-1, 0);
					map.DrawMap(-1);
				}
			} else if ((last_key & Input.kKeyDown) != 0) {
				if (map.GetCurOrientation() == Map.CharOrientation.DOWN) {
					map.DirDown();
				} else {
					map.SetPlayerLocation(-1, 1);
					map.DrawMap(-1);
				}
			} else if ((last_key & Input.kKeyPgUp) != 0) {
				Input.ClearKeyStatus();
				do {
					map.DirLeft(4);
					Video.VideoUpdate();
					Gmud.GmudDelay(60);
					last_key = Input.getScanCode();
				} while ((last_key == Input.kKeyPgUp) || last_key == 0);
				continue;
			} else if ((last_key & Input.kKeyPgDn) != 0) {
				Input.ClearKeyStatus();
				while ((last_key & Input.kKeyPgDn) != 0 || last_key == 0) {
					map.DirRight(4);
					Video.VideoUpdate();
					Gmud.GmudDelay(60);
					last_key = Input.getScanCode();
				}
				continue;
			} else if ((last_key & Input.kKeyLeft) != 0) {
				Input.ClearKeyStatus();
				int delay = Gmud.DELAY_AUTO_KEY_MAX;
				do {
					map.DirLeft(4);
					Video.VideoUpdate();
					Gmud.GmudDelay(delay);
					if (delay > Gmud.DELAY_AUTO_KEY_MIN) {
						delay -= Gmud.DELAY_AUTO_KEY_RATE;
					}
					last_key = Input.getScanCode();
				} while (last_key == Input.kKeyLeft);
				continue;
			} else if ((last_key & Input.kKeyRight) != 0) {
				Input.ClearKeyStatus();
				int delay = Gmud.DELAY_AUTO_KEY_MAX;
				do {
					map.DirRight(4);
					Video.VideoUpdate();
					Gmud.GmudDelay(delay);
					if (delay > Gmud.DELAY_AUTO_KEY_MIN) {
						delay -= Gmud.DELAY_AUTO_KEY_RATE;
					}
					last_key = Input.getScanCode();
				} while (last_key == Input.kKeyRight);
				continue;
			}
			Video.VideoUpdate();
			last_key = Gmud.GmudWaitNewKey(Input.kKeyAny);
		}
		// CloseHandle(GmudTemp.timer_thread_handle);
		// if(glpBattle)
		// delete glpBattle;
		// delete glPlayer;
		// delete sMap;

		Gmud.PLAYING = false;
	}
}
