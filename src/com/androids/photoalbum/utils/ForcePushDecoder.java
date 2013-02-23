
package com.androids.photoalbum.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import android.util.Base64;
import android.util.Log;

public class ForcePushDecoder {

    /**
     * @param args
     * @throws UnsupportedEncodingException
     */
//    public static String decoder(String encoded) {
//
//        String decoded = "";
//        String s = "fdfdsfdsdffsd反对反对声《》韩国复合肥";
//        BASE64Encoder basee = new BASE64Encoder();
//        try {
//            String ss = basee.encode(s.getBytes("utf-8"));
//            System.out.println(ss);
//
//            BASE64Decoder base = new BASE64Decoder();
//            byte b[] = base.decodeBuffer(ss);
//            decoded = new String(b, "utf-8");
//            System.out.println(decoded);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return decoded;
//    }
    
    public static String decode(String encoded) {
        Log.d("peter", "encoded:" + encoded);
        String decoded = "";
        try {
            byte b[] = Base64.decode(encoded, Base64.DEFAULT);
            decoded = new String(b, "utf-8");
            System.out.println(decoded);
            Log.d("peter", "result: " + decoded);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return decoded;
    }
}
