package test;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PC on 2015/8/5.
 */
public class test {

    static final String sData[] = new String[] { "MapElem.dat", "MapEvent.dat",
            "NPCSkill.dat", };
    // private static Context mContext;
    public static AssetManager mAssetManager;


    public static void main(String[] args){
        System.out.println("数据:"+sData[0]);
        final int length = 20 - 1;

        final String fileName = sData[0];
        try {
            InputStream is = mAssetManager.open(fileName, AssetManager.ACCESS_RANDOM);
            is.skip(1);
            byte[] b = new byte[length];
            is.read(b, 0, length);
            System.out.println(is);
            is.close();

            // System.arraycopy(value, offset, buffer, 0, count);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
