package com.example.chars.photocollection;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BitmapUtils {
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/WebPic/";
    private static String TAG = "BitmapUtils";

    private static String MD5Encoder(String target) {
        byte[] temp = null;
        StringBuilder result = new StringBuilder();

        try {
            temp = MessageDigest.getInstance("MD5").digest(target.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (temp != null) {
            for (byte b : temp) {
                if ((b & 0xFF) < 0x10) {
                    result.append(0);
                }
                result.append(Integer.toHexString(b & 0xFF));
            }
        }

        return result.toString();
    }

    public static void verifyPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public static void setBitmapToLocal(Bitmap bitmap, String url) {
        try {
            String picName = MD5Encoder(url);
            File dirfile = new File(PATH);
            if (!dirfile.exists()) {
                dirfile.mkdirs();
            }
            File file = new File(PATH, picName + ".jpg");

            Log.i(TAG, "File created.");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "Write to sdcard.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromLocal(String url) {
        String fileName = null;
        fileName = MD5Encoder(url);
        File file = new File(PATH, fileName + ".jpg");
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
