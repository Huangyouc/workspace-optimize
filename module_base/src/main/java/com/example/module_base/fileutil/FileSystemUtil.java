package com.example.module_base.fileutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileSystemUtil {

    private static final String APP_NAME = "noahcrm";

    /**
     * 自动生成一个File对象。若存在SD卡，则放到SD卡的dirName目录下，若不存在SD卡或者SDK已满，则直接放到APP的cache目录下，
     * 此时dirName不起作用。
     *
     * @param context
     * @param dirName
     * @param fileName
     * @return
     */
    public static File createFilePath(Context context, String dirName,
                                      String fileName) {
        if (isExistSDCard() && getSDFreeSize() > 10) {

            // TODO Android 4.4 KitKat，WRITE_MEDIA_STORAGE 权限仅提供给系统应用，不再授予第三方App
            // TODO 研究一下是否受影响

            // 如果存在SD卡且大于10M剩余空间
            File sdPath = Environment.getExternalStorageDirectory();
            File appPath = new File(sdPath, APP_NAME);
            File dirPath = new File(appPath, dirName);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            return new File(dirPath, fileName);
        }

        // SD卡不可用 放在cache目录里
        File cacheDir = context.getExternalCacheDir();
        return new File(cacheDir, fileName);
    }

    /**
     * 判断sd卡和cache目录下的文件是否存在
     * add by 刘成
     * @param context
     * @param dirName
     * @param fileName
     * @return
     */
    public static boolean isFileExits(Context context, String dirName, String fileName) {
        if (isExistSDCard() && getSDFreeSize() > 10) {

            // TODO Android 4.4 KitKat，WRITE_MEDIA_STORAGE 权限仅提供给系统应用，不再授予第三方App
            // TODO 研究一下是否受影响

            // 如果存在SD卡且大于10M剩余空间
            File sdPath = Environment.getExternalStorageDirectory();
            File appPath = new File(sdPath, APP_NAME);
            File dirPath = new File(appPath, dirName);
            if (!dirPath.exists()) {//判断文件夹是否存在
                return false;
            }

            File file = new File(dirPath, fileName);
            if (file.exists()) {//判断文件是否存在
                return true;
            }
            return false;
        }
        /**
         * 如果sd不存在，就判断cache目录
         * 如果目录下的文件存在，返回true
         */
        File cacheDir = context.getExternalCacheDir();
        File cacheFile = new File(cacheDir, fileName);
        if (cacheFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串、json 写入文件
     * add by 刘成
     * @param json
     * @param filePath
     */
    public static void writeStringToFile(String json, String filePath) {
        File txt = new File(filePath);
        if (!txt.exists()) {
            try {
                txt.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] bytes = json.getBytes(); //新加的
        int b = json.length(); //改
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(txt);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 字符串、json 写入文件
     * add by 刘成
     * @param json
     * @param txt
     */
    public static void writeStringToFile(String json, File txt) {
        byte[] bytes = json.getBytes(); //新加的
        int b = json.length(); //改
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(txt);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getImagePath(Context context, String dirName,
                                      String fileName) {
        if (isExistSDCard() && getSDFreeSize() > 10) {

            // TODO Android 4.4 KitKat，WRITE_MEDIA_STORAGE 权限仅提供给系统应用，不再授予第三方App
            // TODO 研究一下是否受影响

            // 如果存在SD卡且大于10M剩余空间
            File sdPath = Environment.getExternalStorageDirectory();
            File appPath = new File(sdPath, APP_NAME);
            File dirPath = new File(appPath, dirName);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            return new File(dirPath, fileName).getAbsolutePath();
        }

        // SD卡不可用 放在cache目录里
        File cacheDir = context.getExternalCacheDir();
        return new File(cacheDir, fileName).getAbsolutePath();
    }

    public static String genRandomFileName(String suffix) {
        Date now = new Date();
        return now.getTime() + "." + suffix;
    }

    public static String genRandomFileNameNew(String suffix, String type) {
        Date now = new Date();
        return type + now.getTime() + "." + suffix;
    }

    public static String getMd5FileName(String uri, String suffix) {
        return MD5Util.md5(uri) + "." + suffix;
    }

    public static String getMd5ImageName(String uri) {
        return MD5Util.md5(uri);
    }

    private static boolean isExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static String imageToBase64String(String filePath) {
        String base64 = "";
        Bitmap bm = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return base64;
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;// 只获取图片的尺寸,图片本身不会加载到内存
            BitmapFactory.decodeFile(filePath, options);// 此时返回值为空
            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(filePath, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (file.getName().toLowerCase().endsWith("jpg")) {
                // 50表示压缩率，100表示不压缩
                bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            } else {
                bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
            }
            byte[] bytes = baos.toByteArray();
            base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            base64 = "";
        } catch (Error er) {
            er.printStackTrace();
            base64 = "";
        } finally {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
                System.gc();
            }
        }

        return base64;
    }

    public static boolean writeFile(File file, String write_str) {
        boolean flag = false;
        if (file == null) {
            return flag;
        } else {
            try {
                FileOutputStream fout = new FileOutputStream(file);
                byte[] bytes = write_str.getBytes();
                fout.write(bytes);
                fout.close();
                flag = true;
                return flag;
            } catch (Exception e) {
                // TODO: handle exception
                return flag;
            }
        }
    }

    public static String readFile(File file) {
        String result = "";
        if (file == null) {
            return result;
        } else {
            try {
                FileInputStream fin = new FileInputStream(file);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                result = EncodingUtils.getString(buffer, "UTF-8");
                fin.close();
                return result;
            } catch (Exception e) {
                // TODO: handle exception
                return result;
            }
        }
    }

    public static List<String> readFileByLine(File file) {

        String line = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                Log.d("jwjFileTest",
                        "list =" + i + " " + list.get(i));
            }
            reader.close();
        } catch (Exception e) {

        }
        return list;
    }

    /*
     * 容许图片的最大宽高
     */

    private static final int maxHeight = 2000, maxWidth = 2000;

    /**
     * 计算图片的缩放比例
     *
     * @param options
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > maxHeight || width > maxWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) maxHeight);
            final int witdhRatio = Math.round((float) width / (float) maxWidth);
            inSampleSize = heightRatio < witdhRatio ? heightRatio : witdhRatio;
        }
        return inSampleSize;
    }

    /**
     * 返回可用空间大于1G以G为单位，小于以兆为单位
     *
     * @return
     */
    public static String getFreeSize() {
        String freeSizeStr;
        long freeSize = getSDFreeSize();
        if (freeSize < 1024) {
            freeSizeStr = freeSize + "M";
        } else {
            freeSizeStr = new DecimalFormat("0.00").format(freeSize / 1024) + "G";
        }
        return freeSizeStr;
    }

    /**
     * 删除目录下所有文件
     *
     * @param dir
     */
    public static void deleteAllFiles(File dir) {
        if (dir.exists()) {
            File files[] = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory()) { // 判断是否为文件夹
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    } else {
                        if (f.exists()) { // 判断是否存在
                            deleteAllFiles(f);
                            try {
                                f.delete();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
        }
    }

    /**
     * 获取双录地址
     *
     * @param context
     * @return
     */
    public static File getDoubleRecordPath(Context context) {
        if (isExistSDCard() && getSDFreeSize() > 10) {

            // TODO Android 4.4 KitKat，WRITE_MEDIA_STORAGE 权限仅提供给系统应用，不再授予第三方App
            // TODO 研究一下是否受影响

            // 如果存在SD卡且大于10M剩余空间
            File sdPath = Environment.getExternalStorageDirectory();
            File appPath = new File(sdPath, APP_NAME);
            File dirPath = new File(appPath, "doubleRecord");
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            return dirPath;
        }
        // SD卡不可用 放在cache目录里
        File cacheDir = context.getExternalCacheDir();
        return cacheDir;
    }
}
