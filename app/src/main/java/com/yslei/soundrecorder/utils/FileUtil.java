package com.yslei.soundrecorder.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * create by author：yslei
 * create time : 2017/4/6 10:49
 * author email : leiyongsheng_cd@keruyun.com
 */
public class FileUtil {

    private static final String RECORD_CACHE_RELATIVE_DIR_PATH = File.separator + "kry" + File.separator + "recordcache/";
    public static final String RECORD_CACHE_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + RECORD_CACHE_RELATIVE_DIR_PATH;
    public static final String RECORD_CACHE_FILE_PATH = RECORD_CACHE_DIR_PATH + File.separator + "default.mp4";

    public static void deleteFile(String path) {
        File file = new File(path);
        if (!file.delete()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
                deleteFile(path);
            }
        }
    }

    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.delete()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f == null) {
                            continue;
                        }
                        deleteFile(f);
                    }
                }
                deleteFile(file);
            }
        }
    }

    public static boolean isExistSD() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getRecordCacheDirPath(Context context) {
        return getRecordCacheDirPath(context, RECORD_CACHE_RELATIVE_DIR_PATH);
    }

    public static String getRecordCacheDirPath(Context context, String path) {
        File fileDir = null;
        // 判断是否有内置SD卡存在
        if (isExistSD()) {
            fileDir = new File(Environment.getExternalStorageDirectory(), path);
            if (isFileDirExist(fileDir) || fileDir.mkdirs()) {
                return fileDir.getAbsolutePath();
            }
        } else if (context != null) {
            fileDir = new File(context.getCacheDir(), path);
            if (isFileDirExist(fileDir) || fileDir.mkdirs()) {
                return fileDir.getAbsolutePath();
            }
        }
        return null;
    }

    public static String[] getRecordFiles(String absolutePath) {
        if (!TextUtils.isEmpty(absolutePath)) {
            File fileDir = new File(absolutePath);
            if (isFileDirExist(fileDir)) {
                return fileDir.list();
            }
        }
        return null;
    }

    public static boolean isFileDirExist(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean isFileExist(String path){
        if(!TextUtils.isEmpty(path)){
            return new File(path).exists();
        }
        return false;
    }

    public static void deleteFiles(List<String> filePathes) {
        if (filePathes != null && filePathes.size() > 0) {
            for (String filePath : filePathes) {
                deleteFile(filePath);
            }
        }
    }

    public static boolean fileCanRead(String filename) {
        File f = new File(filename);
        return f.canRead();
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file

        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;

            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
