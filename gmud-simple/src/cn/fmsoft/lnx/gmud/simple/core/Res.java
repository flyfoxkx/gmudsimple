/**
 * Copyright (C) 2011, FMSoft.LNX.Gmud
 */

package cn.fmsoft.lnx.gmud.simple.core;

import java.io.*;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

public class Res {
	static final String STR_YOU_STRING = "你";
	static final String STR_NO_INNER_KONGFU_STRING = "你必须先选择你要用的内功心法.";
	static final String STR_NO_FASHU_STRING = "你必须先选择你要用的法术.";
	static final String STR_FP_PLUS_LIMIT_STRING = "你目前的加力上限为%d";
	static final String STR_MP_PLUS_LIMIT_STRING = "你目前法力点数上限为%d";
	static final String STR_INNER_KONGFU_TOO_LOW = "你的内功等级不够";
	static final String STR_FASHU_TOO_LOW = "你的法术等级不够";

	static final String STR_FLY_NEED_MORE_FP = "你的内力不足，无法施展轻功。";

	static final String STR_BATTLE_WIN = "大获全胜！战斗获得\n金钱:%d\n物品:%s";

	/* 打坐 */

	/* 练功 */
	static final String STR_PRACTICE_NO_BASE_SKILL = "想一步登天,可是你的基本功夫还没学会呢!";
	static final String STR_PRACTICE_NEED_MASTER = "你的功夫很难再有所提高了,还是向师傅请教一下吧";
	static final String STR_PRACTICE_NEED_FP_LEVEL = "你的内力修为不足,要勤修内功!";
	static final String STR_PRACTICE_NEED_WEAPON = "趁手的兵器都没有一把,瞎比划什么!";
	static final String STR_PRACTICE_NEED_HEALTHFUL = "你受伤了,还是先治疗要紧.";

	/* 向师傅请教 */
	static final String STR_STUDY_NEED_MORE_PONTENTIAL = "你的潜能已经发挥到极限了,没有办法再成长了";
	static final String STR_STUDY_NEED_MORE_EXPERIENCE = "你的武学经验不足,无法领会更深的功夫";
	static final String STR_STUDY_NEED_MORE_MONEY = "没钱读什么书啊，回去准备够学费再来吧!";
	static final String STR_STUDY_YOU_ARE_MASTER = "你的功夫已经不输为师了，真是可喜可贺呀";
	static final String STR_STUDY_YOUR_SKILL_PROGRESS = "你的功夫进步了！";
	static final String STR_STUDY_QUERY_CONTINUE = "　　　继续学习吗?\n（[输入]确认,[跳出]放弃）";

	/** 独行大侠，要求 exp > 20W */
	static final String STR_LONE_HEROE_REQUEST_ENOUGH_EXPERIENCE = "去去去，赚够经验再来学吧！";

	/* 退出游戏 */
	static final String STR_EXIT_QUERY = "　　真的退出游戏吗?\n（[输入]确认,[跳出]放弃）";
	static final String STR_EXIT_QUERY_SAVE = "你已经很久没存档了,现在保存吗?\n([输入]确认,[跳出]放弃)";

	/* 月下老人 */
	static final String STR_MATCHMAKER_TOO_YOUNG = "这么嫩的小伢仔也要结婚？早恋可不好呦！";
	static final String STR_MATCHMAKER_MAOSHAN = "茅山一派属方外之士，岂能再动凡心？";
	static final String STR_MATCHMAKER_NEED_HOUSE = "房子都没有就想结婚？我可不能眼看你们婚后露宿街头！";

	/* 疗伤 */
	static final String STR_RECOVER_HEALTHFUL = "你并没有受伤.";
	static final String STR_RECOVER_KONGFU_LEVEL_TOO_LOW = "你运功良久,一抖衣袖,长叹一声站起身来.";
	static final String STR_RECOVER_INJURED_TOO_HEAVY = "你已经受伤过重,只怕一运真气就会有生命危险.";
	static final String STR_RECOVER_NEED_MORE_FP = "你的真气不够,还不能用来疗伤.";
	static final String STR_RECOVER_SUCCESS = "你摧动真气,脸上一阵红一阵白,哇的一声吐出一口淤血,脸色看起来好多了.";
	static final String STR_RECOVER_START = "你全身放松，坐下来开始运功疗伤！";

	/* 吸气 */
	static final String STR_BREATH_HEALTHFUL = "你现在体力充沛.";
	static final String STR_BREATH_NEED_MORE_FP = "你的内力不够.";
	static final String STR_BREATH_SUCCESS = "你深深吸了几口气,脸色看起来好多了.";

	/* 配偶 */
	static final String STR_CONSORT_NONE_F = "你还待字闺中";
	static final String STR_CONSORT_NONE_M = "你还是光棍一条";
	static final String STR_CONSORT_IS_F = "你老公是%s";
	static final String STR_CONSORT_IS_M = "你老婆是%s";

	static final String STR_NPC_DESC = "%s看起来约%d多岁\n武艺看起来%s\n出手似乎%s\n带着:%s\n%s";

	/** 物品描述 */
	static final int TYPE_ITEMDESC = 0;
	/** 对话 */
	static final int TYPE_DIALOG = 5;

	static final String sText[] = new String[] { "0.txt", "1.txt", "2.txt",
			"3.txt", "4.txt", "5.txt", };

	/* image id: 244~254 */
	static final String sImage[] = new String[] { "PNG/hp.png", "PNG/fp.png",
			"PNG/mp.png", "PNG/hit.png", "PNG/num.png", "PNG/bd.png",
			"PNG/ctr.png", "PNG/d.png", "PNG/l.png", "PNG/u.png", "PNG/r.png", };

	static final String sData[] = new String[] { "MapElem.dat", "MapEvent.dat",
			"NPCSkill.dat", };

	// private static Context mContext;
	private static AssetManager mAssetManager;

	static public void init(Context context) {
		// mContext = context;
		mAssetManager = context.getAssets();
	}

	// id:0-物品描述 1-人物描述 2-招式描述 3-绝招描述 4-伤害描述 5-对话
	static String readtext(int id, int startpoint, int endpoint) {

		if (id < 0 || id > 5) {
			return "";
		}

		final int length = endpoint - startpoint;

		final String fileName = sText[id];

		try {
			InputStream is = mAssetManager.open(fileName,
					AssetManager.ACCESS_RANDOM);
			is.skip(startpoint);
			byte[] data = new byte[length + 0];
			is.read(data, 0, length);
			is.close();

			final String text = new String(data, "utf8");

			return text;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	static Bitmap loadimage(int id) {
		if (id < 0 || id > 255) {
			return null;
		}

		Bitmap image;

		String fileName;
		if (id >= 244) {
			fileName = sImage[id - 244];
		} else {
			fileName = String.format("PNG/%d.png", id);
		}

		try {
			InputStream is = mAssetManager.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			image = null;
		}

		return image;
	}

	// id:0-map元素 1-map事件 2-招式
	@TargetApi(Build.VERSION_CODES.FROYO)
	static byte[] getarraydata(int id, int startpoint, int endpoint) {
		if (id < 0 || id > 3) {
			return null;
		}

		final int length = endpoint - startpoint;
		final String fileName = sData[id];
		try {
			InputStream is = mAssetManager.open(fileName,
					AssetManager.ACCESS_RANDOM);
			is.skip(startpoint);
			byte[] b = new byte[length];
			is.read(b, 0, length);
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			outputStream.write(b, 0, length);
//			outputStream.close();
//			System.out.println(outputStream.toString());
//			String DATABASE_PATH = android.os.Environment
//					.getExternalStorageDirectory().getAbsolutePath();
//			File dir  = new File(DATABASE_PATH);
//			if (!dir.exists()){
//				dir.mkdir();
//			}
//			String databaseFilename = DATABASE_PATH + "/" + "test.txt";
//			String str1 = outputStream.toString("gbk");
//			File file1 = new File(databaseFilename);
//			FileOutputStream fos = new FileOutputStream(file1);
//			fos.write(str1.getBytes("utf-8"));
			int[] i = Res.convert(b, 0, b.length);
			String ditu = "";
			for(int a = 0; a < i.length;a++){
				ditu += String.valueOf(i[a])+",";
			}
			Log.v("地图", ditu);
			String s1 = new String(b,"ISO-8859-1");
			byte[] isoret = s1.getBytes("ISO-8859-1");
			String s2 = new String(b,"UTF-8");
			String s3 = new String(Base64.encode(b, Base64.DEFAULT),"UTF-8");
			String s4 = new String(Base64.encode(b, Base64.DEFAULT),"ISO-8859-1");
//			Log.d("地图::", new String(Base64.encode(b, Base64.DEFAULT),"UTF-8"));
//			fos.close();
			is.close();

			// System.arraycopy(value, offset, buffer, 0, count);

			return b;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	static int[] convert(byte[] buff, int startpoint, int endpoint) {
		final int size = (endpoint - startpoint + 3) >> 2;
		final int[] out = new int[size];

		int i, j;
		for (i = startpoint, j = 0; i < endpoint; i += 4, j++) {
			// out[j] = ((buff[i] & 0xff) << 24) | ((buff[i + 1] & 0xff) << 16)
			// | ((buff[i + 2] & 0xff) << 8) | (buff[i + 3] & 0xff);
			out[j] = ((buff[i + 3] & 0xff) << 24)
					| ((buff[i + 2] & 0xff) << 16)
					| ((buff[i + 1] & 0xff) << 8) | (buff[i + 0] & 0xff);
		}

		return out;
	}
}
