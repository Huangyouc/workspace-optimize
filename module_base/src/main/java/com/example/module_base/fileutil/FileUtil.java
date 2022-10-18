package com.example.module_base.fileutil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.module_base.LogUtil;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件的操作
 */

public class FileUtil {
  public static final int TYPE_PDF = 1000;
  public static final int TYPE_WORD = TYPE_PDF + 1;
  public static final int TYPE_PPT = TYPE_WORD + 1;
  public static final int TYPE_EXCEL = TYPE_PPT + 1;

  public static void openFile(Activity act, File localFile, int fileType) {
    String fileTypeMedthod = "application/pdf";
    switch (fileType) {
    case TYPE_PDF:
      fileTypeMedthod = "application/pdf";
      break;
    case TYPE_WORD:
      fileTypeMedthod = "application/msword";
      break;
    case TYPE_PPT:
      fileTypeMedthod = "application/vnd.ms-powerpoint";
      break;
    case TYPE_EXCEL:
      fileTypeMedthod = "application/vnd.ms-excel";
      break;
    }
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.addCategory(Intent.CATEGORY_DEFAULT);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(localFile);
      intent.setDataAndType(uri, fileTypeMedthod);
      act.startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(act, "您的设备上目前没有安装该文件阅读程序",Toast.LENGTH_SHORT).show();
    }
  }


  /**
   * 是否删除文件夹及子文件
   *
   * @param filePath
   * @param isDeleteParent 为true是删除文件夹及内部所有的文件，为false只删除子文件
   */
  public static void deleteFile(String filePath, boolean isDeleteParent) {
    if (TextUtils.isEmpty(filePath))
      return;
    deleteFile(new File(filePath), isDeleteParent);
  }

  /**
   * 是否删除文件夹及子文件
   *
   * @param file
   * @param isDeleteParent 为true是删除文件夹及内部所有的文件，为false只删除子文件
   */
  public static void deleteFile(File file, boolean isDeleteParent) {
    if (file != null && file.exists()) { // 判断文件是否存在
      if (file.isDirectory()) { // 否则如果它是一个目录
        File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
        if (files != null) {
          for (File childFile : files) { // 遍历目录下所有的文件
            deleteFile(childFile); // 把每个文件 用这个方法进行迭代
          }
        }
      }

      if (isDeleteParent) {
        // 安全删除文件
        deleteFileSafely(file);
      }
    }
  }

  /**
   * 删除文件夹所有内容
   */
  public static void deleteFile(File file) {
    deleteFile(file, true);
  }

  /**
   * 删除文件
   */
  public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return;
        deleteFile(new File(filePath), true);
    }
  /**
   * 安全删除文件.防止删除后重新创建文件，报错 open failed: EBUSY (Device or resource busy)
   */
  public static boolean deleteFileSafely(File file) {
    if (file != null) {
      String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
      File tmp = new File(tmpPath);
      file.renameTo(tmp);
      return tmp.delete();
    }
    return false;
  }


    /**
     * 创建一个新文件，确保其目录存在
     *
     * @param filePath
     * @return
     */
    public static boolean createNewFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static String getFilePath(Context context, String dir, String file) {
        return context.getFilesDir().getAbsolutePath() + "/" + dir + "/" + file;
    }

    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }



    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }



    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }


    /***
     * 另一种删除文件的方式
     * @param dir
     * @return
     */
    public static boolean deleteFileOrDir(File dir) {
        if (!dir.exists()) {
            return true;
        }

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFileOrDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static boolean move(String filePath, String targetFilePath) {
        File file = new File(filePath);
        boolean isDirectory = file.isDirectory();

        if (isDirectory) {
            return copyDir(filePath, targetFilePath);

        } else {
            return copyFile(filePath, targetFilePath);
        }
    }

    public static boolean copyFile(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (toFile.exists()) {
            toFile.delete();
        }
        if (!toFile.exists()) {
            try {
                toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return copyFile(fromFile, toFile);
    }

    public static boolean copyFile(File fromFile, File toFile) {
        if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
            return false;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists()) {
            toFile.delete();
        }

        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = new FileInputStream(fromFile);
            if (!toFile.exists()) {
                toFile.createNewFile();
            }
            fos = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }

    public static boolean copyDir(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);

        if (!fromFile.exists() || !fromFile.isDirectory() || !fromFile.canRead()) {
            return false;
        }

        if (!toFile.exists()) {
            toFile.mkdirs();
        }

        try {
            String[] file = fromFile.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (fromPath.endsWith(File.separator)) {
                    temp = new File(fromPath + file[i]);
                } else {
                    temp = new File(fromPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    boolean result = copyFile(temp, new File(toPath + "/" + temp.getName()));
                    if (!result) {
                        return false;
                    }
                } else {
                    copyDir(fromPath + "/" + file[i], toPath + "/" + file[i]);
                }
            }

            return true;
        } catch (Exception e) {
        }

        return false;
    }



    public static void renameDir(String fromDir, String toDir) {
        File from = new File(fromDir);
        if (!from.exists() || !from.isDirectory()) {
            LogUtil.d("Directory does not exist: " + fromDir);
            return;
        }

        File to = new File(toDir);
        if (from.renameTo(to)) {
            LogUtil.d("rename Success");
        } else {
            LogUtil.d("rename fail");
        }
    }
}
