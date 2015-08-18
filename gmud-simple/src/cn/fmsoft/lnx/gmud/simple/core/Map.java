package cn.fmsoft.lnx.gmud.simple.core;

import android.graphics.Bitmap;
import android.util.Log;

public class Map {

	/* keep single instance */
	static private Map sInstance;

	static synchronized Map getInstance() {
		if (sInstance == null) {
			sInstance = new Map();
		}
		return sInstance;
	}

	/**
	 * 0Up 1Down 2Left 4Right
	 */
	protected final static class CharOrientation {
		final static int UP = 0;
		final static int DOWN = 1;
		final static int LEFT = 2;
		final static int RIGHT = 4;
	}

	/** 绘制地图的基线，其下是绘制角色图片，其上绘制地图 */
	private final static int ELEM_BASE_Y = 51;

	/** 卷轴的停留范围，范围之外则产生滚动 */
	private final static int SCROLL_SCOPE_LEFT = 32;

	/** 卷轴的停留范围，范围之外则产生滚动 */
	private final static int SCROLL_SCOPE_RIGHT = 48;

	/** 人物宽度 */
	private final static int CHARACTER_WIDTH = 16;

	/** 当前地图ID */
	private int m_map_id;

	/** 玩家角色的面向 {@link CharOrientation} */
	private int m_player_dir;

	/** 栈指针，见 {@link #s_player_last_location} */
	private int m_stack_pointer;

	/** (size:6) 玩家角色的图片 */
	private Bitmap[] m_player_image = new Bitmap[6];

	/** 图元表，成员解析：&0x8000000!=0 为空白区，其宽度为 &0xffff，否则为图片ID，范围[0,244) */
	protected final MapInfo.MAPINFO map_image_data = new MapInfo.MAPINFO();

	/**
	 * 地图事件
	 * <ol>
	 * <li>>=512特殊事件，新的事件为event=event-512</li>
	 * <li>>=256特殊事件，见 {@link #map_event(int)}</li>
	 * <li>255 无效值</li>
	 * <li>[0,255)NPC的ID=event&0xff</li>
	 * <li>(-255,0)地图ID为-event并且人物放置在最左边</li>
	 * <li>-255 返回上次的地图</li>
	 * <li>(-512,-255)地图ID为-(event+256)并且人物放置在最右边</li>
	 * <li>-65535回到地图0</li>
	 * </ol>
	 */
	protected final MapInfo.MAPINFO map_event_data = new MapInfo.MAPINFO(); // event data

	static byte NPC_flag[] = new byte[185];

	/** 用于确定弹出对话框的显示位置x */
	static private int s_event_x = 0;

	/** 触发事件的图元ID */
	static private int s_image_id = 0;

	/** 在屏幕中（绘制）玩家角色的横坐标[0, 144] */
	private int m_player_x;

	/** 走路状态，0并脚 1张腿 */
	private int m_player_walk_status;

	/** 可见范围内最左边像素在卷轴中的坐标 */
	private int m_cur_scroll;

	/** 地图最左边的元素在表中的索引，见 {@value #map_image_data} 和 {@value #map_event_data} */
	private int m_left_index;

	/** 可见范围内最左边图元在卷轴中的坐标, <= {@link #m_cur_scroll} */
	private int m_img_scroll;

	/** 卷轴的总宽度 */
	private int m_total_width;

	/** 室内 */
	private boolean m_in_room;

	/** 仅标识刚调用 {@link #reset()} */
	private boolean m_reset;

	/** 门的参数 */
	private final int DOOR_X;
	private final int DOOR_Y;
	private final int DOOR_W;
	private final int DOOR_H;

	/** (size:244) 所有图片元素（地图及人物角色）缓存 */
	private Bitmap m_map_image[] = new Bitmap[244];

	/** (size:64) 地图栈，记录进房子前的位置，四元组 [0]地图ID [1]（地图横卷轴）偏移量 [2]角色屏幕中的横坐标 [3]角色朝向 */
	private static int s_player_last_location[] = new int[64];

	/** (size:93) 各图片元素的触发点，如房子的门的位置，三元组 [0]图片ID[0,244) [1]偏移位置 [2]宽度 */
	private final static int IMAGE_TRIGGER[] = new int[] { 15, 16, 16, 16, 13,
			14, 17, 16, 16, 18, 24, 27, 19, 15, 22, 20, 13, 18, 21, 15, 24, 56,
			22, 20, 57, 10, 27, 58, 25, 17, 60, 16, 16, 61, 16, 16, 62, 14, 16,
			63, 16, 16, 64, 16, 16, 65, 16, 16, 66, 16, 13, 67, 12, 16, 68, 16,
			16, 69, 16, 16, 121, 16, 16, 122, 16, 16, 123, 16, 16, 124, 16, 16,
			125, 13, 16, 136, 11, 23, 137, 16, 16, 138, 16, 16, 139, 16, 18,
			156, 26, 16, 158, 24, 18 };

	public Map() {
		reset();

		DOOR_X = 71; // 出口线起始X
		DOOR_W = 21; // 出口线X间距
		DOOR_H = 9; // 出口线Y长
		DOOR_Y = 71; // 出口线Y
	}

	public int GetCurMapID() {
		return m_map_id;
	}

	/**
	 * 取当前角色的朝向
	 * 
	 * @return 见 {@link CharOrientation}
	 */
	public int GetCurOrientation() {
		return m_player_dir;
	}

	void reset() {
		m_map_id = 0;
		m_stack_pointer = 0;

		m_in_room = true;
		m_reset = true; // xx

		// memset(Map::NPC_flag, 0, 185 * sizeof(unsigned char));
		// memset(map_image, 0, 244 * sizeof(Image*));
		java.util.Arrays.fill(NPC_flag, (byte) 0);
		java.util.Arrays.fill(m_map_image, null);
	}

	public void recycle() {
		// if(map_image_data.data)
		// delete[] map_image_data.data;
		// if(map_event_data.data)
		// delete[] map_event_data.data;
		// for(int i = 0; i < 244; i++)
		// if(map_image[i])
		// DeleteObject(map_image[i]);
		// delete[] map_image;
		// for(i = 0; i < 6; i++)
		// {
		// if(player_image[i])
		// DeleteObject(player_image[i]);
		// }
		// delete[] player_image;
	}

	/** 清除缓存 */
	public void CleanCache() {
		// TODO:清除图片元素资源，这个比较占内存，待实现
		m_map_image[0] = null;
	}

	/**
	 * 加载地图信息，包括事件和图片；自动将当前位图及位置信息入栈；
	 * <p>
	 * 2012-11-16 去除“自动删除未使用图片的资源“的代码
	 * 
	 * @param id
	 *            如果是主地图( {@link GmudData#fly_dest_map} )，则自动清空栈
	 * @return
	 */
	public boolean LoadMap(int id) {

		// map ready
		if (m_reset) {
			m_reset = false;
		}

		// 如果是(可飞的)主地图，就不需要地图栈，清空栈
		boolean is_root_map = false;
		for (int i = 0; i < 11; i++) {
			if (GmudData.fly_dest_map[i] == id) {
				m_stack_pointer = 0;
				is_root_map = true;
			}
		}

		// 检查是否在房间里面
		boolean is_in_room = m_in_room;
		m_in_room = false;
		for (int k2 = 0; k2 < 64; k2++) {
			if (id == MapInfo.Map_in_room[k2]) {
				m_in_room = true;
				break;
			}
		}

		// 将当前位置入栈
		if (m_stack_pointer < 64 - 4) {
			s_player_last_location[m_stack_pointer] = m_map_id;
			s_player_last_location[m_stack_pointer + 1] = m_cur_scroll;
			s_player_last_location[m_stack_pointer + 2] = m_player_x;
			s_player_last_location[m_stack_pointer + 3] = m_player_dir;
			m_stack_pointer += 4;
		}

		m_map_id = id;

		// 加载地图上各元素的图片信息
		MapInfo.GetMapElem(map_image_data, id);

		// 加载地图各元素对应的事件信息
		MapInfo.GetMapEvent(map_event_data, id);

		// XXX: 屏蔽武馆里面那面墙的地图属性。
		if (id == 5 && map_event_data.data[11] == -321) {
			map_event_data.data[11] = 255;
		}

		if (Gmud.DEBUG) {
			Log.i("lnx", "map id=" + id + " image-size="
					+ map_image_data.data.length + " event-size="
					+ map_event_data.data.length);
		}

		// 如果恶人在此地图，则将最后一个位置设置成 恶人NPC，否则去除这个空位（每张图最后都有一个空位）
		if (task.bad_man_mapid == id) {
			map_image_data.data[map_image_data.size - 1] = 186;
			map_event_data.data[map_event_data.size - 1] = 179;
		} else {
			--map_event_data.size;
			--map_image_data.size;
		}

		if (is_root_map && !is_in_room) {
			// 检查地图元素（ID=map_image_data.data[i], 0<=ID<244,
			// 0<=i<map_image_data.size）是否有使用到，否则释放此图片的资源
		}

		// 计算地图总宽度，顺便加载未加载的图片元素资源
		int width = 0;
		for (int j3 = 0; j3 < map_image_data.size; j3++) {
			// XXX: 这里加载图片，可能会卡顿
			int imgID = map_image_data.data[j3];
			if ((imgID & 0x8000000) != 0) {
				// 直接加上宽度
				width += imgID & 0xffff;
				continue;
			}

			// 如果此图片未加载，则加载它
			if (m_map_image[imgID] == null)
				m_map_image[imgID] = Res.loadimage(imgID);
			width += m_map_image[imgID].getWidth();
		}

		m_total_width = width;

		return true;
	}

	/** 读取玩家角色图片资源到 {@link #m_player_image} */
	public void LoadPlayer(int id) {
		if (id > 2)
			id = 2;

		final int imgId = 74 + id * 6;
		for (int j1 = 0; j1 < 6; j1++)
			m_player_image[j1] = Res.loadimage(imgId + j1);
	}

	/** 标记NPC是否挂了，挂了时变成不可见, {@link #NPC_flag} */
	static void SetNPCDead(int npc_id, byte byte0) {
		NPC_flag[npc_id] = byte0;
	}

	/**
	 * 
	 * @param in_x
	 * @param dir
	 *            朝向, 0Up 1Down 2Left 4Right
	 * @see #m_player_dir
	 */
	public void SetPlayerLocation(int in_x, int dir) {
		if (in_x >= 0) // -1 keep
		{
			if (in_x < 0)
				in_x = 0;
			if (in_x > 144)
				in_x = 144;
			m_player_x = in_x;
		}
		m_player_dir = dir;
		m_player_walk_status = 0;
	}

	/** 绘制角色 */
	private void DrawPlayer() {
		Video.VideoClearRect(0, ELEM_BASE_Y, 160, 79);

		final Bitmap bm;
		final int dir = m_player_dir;
		if (dir == CharOrientation.UP) {
			bm = m_player_image[0];
		} else if (dir == CharOrientation.DOWN) {
			bm = m_player_image[1];
		} else if (dir == CharOrientation.LEFT) {
			bm = m_player_image[2 + m_player_walk_status];
		} else if (dir == CharOrientation.RIGHT) {
			bm = m_player_image[4 + m_player_walk_status];
		} else {
			return;
		}

		Video.VideoDrawImage(bm, m_player_x, 1 + ELEM_BASE_Y);

		// draw 室内方框
		if (m_in_room) {
			Video.VideoDrawRectangle(0, 0, Gmud.WQX_ORG_WIDTH,
					Gmud.WQX_ORG_HEIGHT);

			// 出口
			Video.VideoDrawLine(DOOR_X, DOOR_Y, DOOR_X, DOOR_Y + DOOR_H);
			Video.VideoDrawLine(DOOR_X + DOOR_W, DOOR_Y, DOOR_X + DOOR_W,
					DOOR_Y + DOOR_H);
		}
	}

	/** 返回栈中的上级地图 */
	private void ReturnUpLevelMap() {
		if (m_stack_pointer >= 4) {
			boolean flag = m_in_room;
			int i1 = m_stack_pointer - 4;
			int mapID = s_player_last_location[i1];
			int sx = s_player_last_location[i1 + 1];
			int vx = s_player_last_location[i1 + 2];
			int dir = s_player_last_location[i1 + 3];
			LoadMap(mapID); // load map
			m_stack_pointer = i1; // stack --, LoadMap 会影响 m_stack_pointer
			if (flag)
				dir = 1;
			DrawMap(sx); // draw map
			SetPlayerLocation(vx, dir);
			DrawPlayer();
		}
	}

	/**
	 * 检查能否通过地图
	 * 
	 * @param npc_id
	 *            事件ID，或角色ID
	 * @return true不可通过　false可通过
	 */
	static boolean MapPassable(int npc_id) {
		switch (npc_id) {
		case 138: // "留孙真人", "茅山令牌"
			return Gmud.sPlayer.ExistItem(91, 1) < 0;

		case 126: // "北海鳄神"
			return Gmud.sPlayer.ExistItem(90, 1) < 0;

		case 41: // "护院武师", "青龙坛地图"
			return Gmud.sPlayer.ExistItem(79, 1) < 0;

		case 137:
			return Gmud.sPlayer.ExistItem(80, 1) < 0;

		case 77:
			return Gmud.sPlayer.ExistItem(81, 1) < 0;

		case 128:
			return Gmud.sPlayer.ExistItem(82, 1) < 0;

		case 92:
			return Gmud.sPlayer.ExistItem(83, 1) < 0;

		case 106:
			return Gmud.sPlayer.ExistItem(84, 1) < 0;

		case 100:
			return Gmud.sPlayer.ExistItem(85, 1) < 0;

		case 64:
			return Gmud.sPlayer.ExistItem(86, 1) < 0;
		}
		// 各坛的小兵 [158]"地罡喽啰"
		return npc_id >= 158 && npc_id <= 178 && Map.NPC_flag[npc_id] == 0;
	}

	/** 向下走 */
	public void DirDown() {
		if (m_in_room) {
			final int ox = m_player_x - DOOR_X;
			if (ox > -8 && ox < (DOOR_W + CHARACTER_WIDTH / 2))
				ReturnUpLevelMap();
		}
	}

	/** 向上 */
	public void DirUp() {
		final int event_index = get_event_index();
		if (event_index < 0)
			return;

		final int event = map_event_data.data[event_index];

		// nothing
		if (event == 255)
			return;

		if (event < 0) {
			// go back parent map
			if (event == -255) {
				ReturnUpLevelMap();
				return;
			}

			// goto map[0]
			if (event == -65535) {
				LoadMap(0);
				SetPlayerLocation(0, CharOrientation.RIGHT);
				DrawMap(0);
				return;
			}

			// 地图跳转 (-255,0]人物在左边 (512,255)人物在右边
			boolean onright = false;
			final int mapID;
			if (event < -255) {
				// goto map -[j1+256]
				onright = true;
				mapID = -(event + 256);
			} else {
				mapID = -event;
			}
			LoadMap(mapID);
			if (onright) {
				// 将角色放在地图最右边，并面朝左方
				m_cur_scroll = m_total_width - Gmud.WQX_ORG_WIDTH;
				if (m_cur_scroll < 0)
					m_cur_scroll = 0;
				SetPlayerLocation(Gmud.WQX_ORG_WIDTH, CharOrientation.LEFT);
			} else {
				m_cur_scroll = 0;
				SetPlayerLocation(0, CharOrientation.RIGHT);
			}

			// 如果进了房间，就放在门口
			if (m_in_room) {
				final int x = (DOOR_X + (DOOR_W - CHARACTER_WIDTH) / 2);
				SetPlayerLocation(x, CharOrientation.UP);
			}
			DrawMap(-1);
		}
	}

	/**
	 * 向左移动人物
	 * 
	 * @param dx
	 *            移动距离
	 */
	public void DirLeft(int dx) {
		// 变化走路的状态，产生行走的效果
		m_player_walk_status = 1 - m_player_walk_status;

		m_player_dir = CharOrientation.LEFT;

		if (m_cur_scroll == 0 && m_player_x < Gmud.WQX_ORG_WIDTH) {
			// 地图卷轴已经到最左边，只移动人物显示位置
			m_player_x -= 5;
			if (m_player_x < 0)
				m_player_x = 0;
		} else if (m_player_x > SCROLL_SCOPE_LEFT) {
			// 人物离左边够宽则只移动人物显示位置
			m_player_x -= 4;
			if (m_player_x < SCROLL_SCOPE_LEFT)
				m_player_x = SCROLL_SCOPE_LEFT;
		} else {
			// 向右滚动卷轴
			int scrool_x = m_cur_scroll - dx; // 新的卷轴左端位置
			if (scrool_x < 0)
				scrool_x = 0;

			int msx = m_img_scroll;
			int index = m_left_index;
			while (index >= 0) {
				// 让最左边图元可以显示出来
				if (msx <= scrool_x) {
					Video.VideoClearRect(0, 0, Gmud.WQX_ORG_WIDTH, ELEM_BASE_Y);
					draw_map(index, msx, scrool_x);
					break;
				}

				// 向左继续查找下一图元
				if (--index >= 0) {
					final int imgId = map_image_data.data[index];
					final int w = calc_elem_width(imgId);
					msx -= w;
				}
			}
		}

		DrawPlayer();
	}

	/**
	 * 向右移动人物
	 * 
	 * @param dx
	 *            移动距离
	 */
	public void DirRight(int dx) {
		int tmp_int;

		m_player_dir = CharOrientation.RIGHT;
		m_player_walk_status = 1 - m_player_walk_status;

		final int event_index = get_event_index();
		final int event;
		if (event_index >= 0)
			event = map_event_data.data[event_index];
		else
			event = -1;
		if (event >= 512 && MapPassable(event - 512)) {
			// 不能通过隐藏地图
			DrawPlayer();
			return;
		}

		final int max_scroll = m_total_width - Gmud.WQX_ORG_WIDTH;

		// 尚未到达右边滚轴缓冲区，只移动人物
		tmp_int = Gmud.WQX_ORG_WIDTH - SCROLL_SCOPE_RIGHT;
		if (m_player_x < tmp_int) {
			m_player_x += 5;
			if (m_player_x > tmp_int)
				m_player_x = tmp_int;
			DrawPlayer();
			return;
		}

		tmp_int = Gmud.WQX_ORG_WIDTH - SCROLL_SCOPE_RIGHT / 3;
		if (m_cur_scroll >= max_scroll && m_player_x < tmp_int) {
			// 卷轴已经不能滚动，只能移动人物
			m_player_x += 4;
			if (m_player_x > tmp_int)
				m_player_x = tmp_int;
			DrawPlayer();
			return;
		}

		// 查找合适的卷轴位置

		int scroll_x = m_cur_scroll + dx;
		if (scroll_x > max_scroll)
			scroll_x = max_scroll;
		if (scroll_x < 0)
			scroll_x = 0;

		int index = m_left_index;
		int msx = m_img_scroll;
		final int[] data = map_image_data.data;
		final int img_size = map_image_data.size;
		while (index < img_size) {
			final int imgId = data[index];
			final int w = calc_elem_width(imgId);
			if (msx + w > scroll_x) {
				Video.VideoClearRect(0, 0, Gmud.WQX_ORG_WIDTH, ELEM_BASE_Y);
				draw_map(index, msx, scroll_x);
				break;
			}
			msx += w;
			index++;
		}
		DrawPlayer();
	}

	/** 响应地图事件 */
	public void KeyEnter() {
		if (m_player_dir != CharOrientation.UP)
			return;

		final int event_index = get_event_index();
		if (event_index < 0)
			return;
		int event = map_event_data.data[event_index];
		if (event == 255) // nothing
			return;

		if (event >= 0) {
			if (event >= 512)
				event -= 512;
			if (event >= 256) {
				map_event(event - 256);
				return;
			}
			final int npc_id = event;

			// 此 NPC 已阵亡
			if (NPC_flag[npc_id] == 1)
				return;

			UI.npc_image_id = s_image_id;
			final int type = read_NPC_menu_type(npc_id);
			UI.NPCMainMenu(npc_id, type, s_event_x + 2);
			Input.ClearKeyStatus();
			DrawMap(-1);
			Video.VideoUpdate();
		}
	}

	/** 特殊地图事件 */
	private void map_event(int event_id) {
		switch (event_id) {
		default:
			break;

		case 0: // draw map name
		{
			for (int i = 0; i < 11; i++) {
				if (GmudData.fly_dest_map[i] == m_map_id) {
					Gmud.GmudDelay(200);
					String str = GmudData.map_name[i];
					UI.DrawMapTip(str);
				}
			}
		}
			break;

		case 1: {
			Gmud.GmudDelay(150);
			final String str;
			if (task.temp_tasks_data[0] == 0) {
				// 本镇治安良好
				str = task.bad_man_nothing;
			} else {
				// 本镇正在缉拿人犯
				final String doing = task.bad_man_doing;
				str = doing.replaceAll("o", NPC.NPC_names[179]);
			}
			UI.DrawMapTip(str);
		}
			break;

		case 2: // 扫地
			task.OldWomanFinish(0, 123, 30);
			break;

		case 3: // 挑水
			task.OldWomanFinish(1, 127, 40);
			break;

		case 4: // 劈柴
			task.OldWomanFinish(2, 131, 50);
			break;

		case 5: // 井
		{
			final String str;
			Gmud.GmudDelay(200);
			int max = Gmud.sPlayer.GetWaterMax();
			if (Gmud.sPlayer.water < max) {
				Gmud.sPlayer.water += 20;
				if (Gmud.sPlayer.water > max)
					Gmud.sPlayer.water = max;
				str = "你在井边用杯子舀起井水喝";
			} else {
				str = "你已经再也喝不下一滴水了";
			}
			UI.DrawMapTip(str);
		}
			break;

		case 6: // 钓鱼
		{
			final int tip_id;

			// 无钓竿
			if (Gmud.sPlayer.equips[9] != 73) {
				tip_id = 137;
			} else if (Gmud.sPlayer.hp < 40) {
				tip_id = 138;
			} else {
				UI.ShowDialog(139);
				UI.ShowDialog(140);
				Gmud.sPlayer.hp -= 40;
				if (util.RandomBool(Gmud.sPlayer.GetAgility() * 2)) {
					UI.ShowDialog(142);
					// 检查鱼篓
					if (Gmud.sPlayer.ExistItem(76, 1) >= 0) {
						Gmud.sPlayer.GainOneItem(74);
						tip_id = 144;
					} else {
						tip_id = 143;
					}
				} else {
					tip_id = 141;
				}
			}
			UI.ShowDialog(tip_id);
		}
			break;

		case 7: // draw 坛 name
		{
			Gmud.GmudDelay(200);
			for (int i = 0; i < 8; i++) {
				if (GmudData.boss_map_id[i] == m_map_id) {
					String str = GmudData.boss_map_name[i];
					UI.DrawMapTip(str);
					break;
				}
			}
		}
			break;

		case 8: // 游戏厅入口
			play_mini_game();
			break;

		case 9: // 桃花源入口
			entry_garden();
			break;

		case 10: // 铸武器
			weapon_cast();
			break;

		case 11: // 上吊
		{
			Gmud.GmudDelay(200);
			Input.ClearKeyStatus();
			if (Gmud.sPlayer.ExistItem(19, 1) < 0) {
				String str = "活得太没意思了\n真想找根绳子上吊自杀\n\n";
				UI.DialogBx(str, 8, 4);
			} else {
				String str = "如果您选择上吊自杀\n您的资料就永远删除了\n请务必考虑清楚！！" + task.yes_no;
				int last_key = UI.DialogBx(str, 8, 4);
				last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
				if ((last_key & Input.kKeyEnt) != 0) {
					/*
					 * / fclose(fopen("Gmud.sav" , "w")); //exit exit(0);//
					 */
					GmudMain.Restart();
				}
			}
		}
			break;

		}
	}

	/** 铸武器 */
	private void weapon_cast() {

		// on init
		if (Gmud.sPlayer.lasting_tasks[4] == 1) {
			// 你的武器正在铸造！
			UI.ShowDialog(107);
			return;
		}

		// 0x186a0 = 10,0000
		if (Gmud.sPlayer.exp - Gmud.sPlayer.lasting_tasks[3] < 0x186a0) {
			// 莫邪远远对你喊道：从上次铸造至今，你江湖历练还不够呀，再给你铸造也是瞎费功夫
			UI.ShowDialog(104);
			return;
		}

		if (Gmud.sPlayer.money < Gmud.sPlayer.lasting_tasks[3] * 2) {
			// 干匠远远对你喊道：铸造要花钱的，我还要养活老婆，我不会白给你做工的！
			UI.ShowDialog(105);
			return;
		}

		// select type
		String tip = UI.readDialogText(102);
		Input.ClearKeyStatus();
		Gmud.GmudDelay(200);
		int last_key = UI.DialogBx(tip, 8, 8);
		DrawMap(-1);
		int weapon_type = 0;
		if (last_key == Input.kKeyLeft)
			weapon_type = 7;
		else if (last_key == Input.kKeyRight)
			weapon_type = 9;
		else if (last_key == Input.kKeyUp)
			weapon_type = 1;
		else if (last_key == Input.kKeyDown)
			weapon_type = 6;
		if (weapon_type == 0)
			return;
		Input.ClearKeyStatus();
		Gmud.GmudDelay(200);
		if (Gmud.sPlayer.ExistItem(77, 1) >= 0
				&& Items.item_attribs[77][1] != weapon_type) {
			// "你已经有一个了，别太贪心了。"
			UI.ShowDialog(97);
			return;
		}

		Gmud.sPlayer.money -= Gmud.sPlayer.lasting_tasks[3] * 2;
		if (Gmud.sPlayer.GetForce() < 25 && util.RandomBool(50)) {
			// 失败了？
			// "你两膀一叫力，大喝一声“起”，可兵器没动分毫"
			int tip_id = util.RandomInt(3) + 98;
			UI.ShowDialog(tip_id);
		} else {
			// "下次进入游戏时就会铸好，那时记得要给你铸造的武器起个响亮的名字哟"
			UI.ShowDialog(108);
			Gmud.sPlayer.lasting_tasks[3] = Gmud.sPlayer.exp;
			Gmud.sPlayer.lasting_tasks[4] = 1;
			Gmud.sPlayer.lasting_tasks[5] = weapon_type;
		}
	}

	/** 桃花源 */
	private void entry_garden() {
		Gmud.GmudDelay(200);

		// 提示，并只接收 Ent 和 Exit
		String tip = UI.readDialogText(87);
		tip += task.yes_no;
		int last_key = UI.DialogBx(tip, 8, 8);
		last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
		if ((last_key & Input.kKeyEnt) == 0)
			return;

		if (Gmud.sPlayer.lasting_tasks[6] != 0) {
			UI.ShowDialog2(82);
			Gmud.GmudDelay(3000);
			UI.ShowDialog2(83);
			Gmud.GmudDelay(1000);
			LoadMap(89);
			SetPlayerLocation(0, CharOrientation.RIGHT);
			DrawMap(0);
			return;
		}

		UI.ShowDialog2(78);
		Gmud.GmudDelay(3000);
		if (util.RandomBool(Gmud.sPlayer.GetAgility() * 2)) {
			while (true) {
				UI.ShowDialog2(80);
				Gmud.GmudWaitAnyKey();
				UI.ShowDialog2(197);
				Gmud.GmudDelay(2000);
				if (util.RandomBool(50)) {
					if (util.RandomBool(30)) {
						UI.ShowDialog2(84);
						Gmud.GmudDelay(1500);
						UI.ShowDialog2(85);

						// 与　山大王 PK
						Battle.sBattle = new Battle(147, 187, 0);
						Battle.sBattle.BattleMain();
						Battle.sBattle = null;

						// 大王挂了
						if (NPC.NPC_attrib[147][11] <= 0) {
							UI.ShowDialog2(86);
							Gmud.GmudWaitAnyKey();
							Gmud.sPlayer.lasting_tasks[6] = 1;
							LoadMap(89);
							SetPlayerLocation(0, CharOrientation.RIGHT);
							DrawMap(0);
						}
						return;
					}
				} else {
					UI.ShowDialog2(81);
				}
			}
		} else {
			UI.ShowDialog2(79);
			Gmud.GmudDelay(15000);
		}
	}

	/** 玩小游戏 */
	private void play_mini_game() {
		Gmud.GmudDelay(200);

		// 是否进入小游戏，只响应 Ent 和 Exit
		String tip = UI.readDialogText(110) + task.yes_no;
		int last_key = UI.DialogBx(tip, UI.TITLE_X, 0);
		last_key = Gmud.GmudWaitKey(Input.kKeyEnt | Input.kKeyExit);
		if ((last_key & Input.kKeyEnt) != 0) {
			// 选择小游戏类别
			String s = "这是方圆百里最有名的游戏厅,你想玩甚麽?\n左.跳舞毯\n右.投铅球\n";// UI.readDialogText(111);
			last_key = UI.DialogBx(s, UI.TITLE_X, 0);
			last_key = Gmud.GmudWaitKey(Input.kKeyLeft | Input.kKeyRight
					| Input.kKeyExit);
			if ((last_key & Input.kKeyLeft) != 0) {
				if (Gmud.sPlayer.GetSkillLevel(7) > 0) {
					int grow = mini_game_2.GameMain(); // dance
					if (Gmud.sPlayer.GetSkillLevel(7) > 60)
						grow = 0;
					game_return_to_map(7, grow); // 基本轻功
				}
			} else if ((last_key & Input.kKeyRight) != 0) {
				if (Gmud.sPlayer.GetSkillLevel(8) > 0) {
					int grow = mini_game_1.GameMain(); // 投球
					if (Gmud.sPlayer.GetSkillLevel(8) > 60)
						grow = 0;
					game_return_to_map(8, grow); // 基本招架
				}
			}
		}
	}

	/** 从小游戏返回地图区 */
	private void game_return_to_map(int skill_id, int grow) {
		final int skill_index = Gmud.sPlayer.SetNewSkill(skill_id);
		if (skill_index == -1)
			return;

		final int[] skill_data = Gmud.sPlayer.skills[skill_index];

		if (skill_data[4] <= 0)
			Gmud.sPlayer.SetSkillUpgrate(skill_index);
		skill_data[2] += grow;

		int levels = 0;
		while (skill_data[2] >= skill_data[4]) {
			grow = skill_data[2] - skill_data[4];
			skill_data[1] += 1;
			skill_data[2] = grow;
			Gmud.sPlayer.SetSkillUpgrate(skill_index);
			levels++;
			if (levels > 255) {
				break;
			}
		}

		String str = String.format("你的%s\n进步了:%d级！\n%s\n等级:%d\n点数:%d",
				Skill.skill_name[skill_id], levels, Skill.skill_name[skill_id],
				skill_data[1], skill_data[2]);

		UI.DialogBx(str, UI.TITLE_X, UI.SYSTEM_MENU_Y);
	}

	/** 获取一个地图元素(图片或空白区)的宽度 */
	private int calc_elem_width(int imgId) {
		if ((imgId & 0x8000000) == 0)
			return m_map_image[imgId].getWidth();
		else
			return (imgId & 0xffff);
	}

	/**
	 * 绘制地图图元
	 * 
	 * @param vx
	 *            屏幕上 x 坐标
	 * @param imgId
	 *            图片ID
	 * @param index
	 *            图元在事件数组中的索引
	 * @return 返回　此图元　的宽度
	 */
	private int draw_elem(int vx, int imgId, int index) {
		final int w = calc_elem_width(imgId);

		// 这是空白区
		if ((imgId & 0x8000000) != 0)
			return w;

		int event = map_event_data.data[index];
		if (event >= 512/* || event >= 0 && event < 200 */)
			event &= 0xff;

		// 此 NPC 已经挂了，不绘制任何东西
		if (event >= 0 && event < 200 && NPC_flag[event] == 1)
			return w;

		// 与基线底对齐
		final int y = ELEM_BASE_Y - m_map_image[imgId].getHeight();
		final Bitmap img = m_map_image[imgId];
		if (vx + img.getWidth() >= Gmud.WQX_ORG_WIDTH) {
			Video.VideoDrawImage(img, vx, y);
			return Gmud.WQX_ORG_WIDTH - vx;
		} else {
			Video.VideoDrawImage(img, vx, y);
			return w;
		}
	}

	/**
	 * 绘制地图
	 * 
	 * @param index
	 *            最左边第一元素的索引
	 * @param msx
	 *            最左边第一图元的卷轴位置
	 * @param scroll_x
	 *            可见范围的卷轴位置
	 */
	private void draw_map(int index, int msx, int scroll_x) {
		m_left_index = index;
		m_img_scroll = msx;
		m_cur_scroll = scroll_x;

		final int img_size = map_image_data.size;
		final int[] data = map_image_data.data;
		for (int i = index, x = msx - scroll_x; i < img_size
				&& x < Gmud.WQX_ORG_WIDTH; i++) {
			final int imgId = data[i];
			x += draw_elem(x, imgId, i);
		}
	}

	/**
	 * 从指定的卷轴位置起绘制地图及人物
	 * 
	 * @param scroll_x
	 *            卷轴位置，<0 表示无效，即使用默认的{@link #m_cur_scroll}
	 */
	public void DrawMap(int scroll_x) {

		if (scroll_x < 0)
			scroll_x = m_cur_scroll;
		if (m_total_width - scroll_x < Gmud.WQX_ORG_WIDTH - 1)
			scroll_x = m_total_width - Gmud.WQX_ORG_WIDTH;
		if (scroll_x < 0)
			scroll_x = 0;

		// 查找符合卷轴的最左边的元素
		int msx = 0;
		int index = 0;
		final int[] data = map_image_data.data;
		final int img_size = map_image_data.size;
		while (Input.Running && index < img_size) {
			final int w = calc_elem_width(data[index]);
			if (msx + w > scroll_x) {
				Video.VideoClear();
				draw_map(index, msx, scroll_x);
				break;
			}
			msx += w;
			index++;
		}

		DrawPlayer();
	}

	/** 返回当前人物所在位置事件索引 */
	private int get_event_index() {
		s_event_x = 0;

		int index = m_left_index;
		int max_over = 0;
		int event_index = index;

		final int[] data = map_image_data.data;
		final int img_size = map_image_data.size;
		for (int x = m_img_scroll - m_cur_scroll; index < img_size
				&& x < Gmud.WQX_ORG_WIDTH; index++) {
			final int imgId = data[index];
			final int width = calc_elem_width(imgId);
			int i_sx = x; // 触发点的左端在卷轴中位置
			int i_w = width; // 触发点宽度

			// 查找触发点
			if (0 <= imgId && imgId < 256) {
				for (int i = 0; i < 90; i += 3) {
					if (IMAGE_TRIGGER[i] == imgId) {
						i_sx += IMAGE_TRIGGER[i + 1];
						i_w = IMAGE_TRIGGER[i + 2];
						break;
					}
				}
			}

			// 默认人物宽度大小
			final int base_width = CHARACTER_WIDTH;
			int cover = 0; // 人物与事件物体的覆盖大小
			if (m_player_x < i_sx) {
				if (m_player_x + base_width > i_sx) {
					// 人物左侧在触发点左边之外，但右边在触发点内
					cover = base_width - (i_sx - m_player_x);
					if (cover > i_w)
						cover = i_w;
					if (cover > base_width)
						cover = base_width;
				}
			} else if (m_player_x < i_sx + i_w) {
				cover = i_sx + i_w - m_player_x;
				if (cover > base_width) {
					cover = base_width;
				}
			}
			if (cover > max_over) {
				// 取一个占位范围最大的事件，比如在两个NPC之间，人物在谁那边多一些就取谁的事件
				event_index = index;
				max_over = cover;

				s_event_x = x;
				if (s_event_x < 0)
					s_event_x = 0;
			}
			x += width;
		}

		if (max_over < 4) {
			// 没有找到有效事件或人物与物体的交集太小
			return -1;
		}

		final int imgId = data[event_index];
		if (imgId < 255) {
			// 这是一个NPC
			s_image_id = imgId;
		}

		return event_index;
	}

	/** 定义 NPC 的菜单类型 -1普通 4交易 5拜师 6请教 {@link UI#npc_menu_words} */
	private int read_NPC_menu_type(int id) {
		UI.npc_id = id;
		UI.npc_name = NPC.NPC_names[id]; // 赋值 NPC name

		// class_id == 百姓
		if (NPCINFO.NPC_attribute[id][1] == 0) {

			// 独行大侠 or 顾炎武 显示“请教”
			if (id == 6 || id == 30) {
				return 6;
			}
		} else {
			// 可交易
			if (NPCINFO.NPC_attribute[id][1] == 255) {
				return 4;
			}
			// teacher id 匹配 显示 请教
			if (id == Gmud.sPlayer.teacher_id) {
				return 6;
			}
			return 5;
		}
		return -1;
	}
}
