package com.example.module_base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *  * 启动分区存储的情况下，使用：ViewConvertBitmap.addBitmapToAlbum
 *  * 未启动分区存储，使用：AlbumHelper.saveImageToGallery
 */
public class AlbumHelper {

  public static boolean addSignatureToGallery(Context context, Bitmap signature) {
    String fileName = "noah_" + String.valueOf(System.currentTimeMillis()).hashCode() + ".jpg";
    boolean result = false;
    try {
      File photo = new File(getAlbumStorageDir("Noah"), fileName);
      saveBitmapToJPG(signature, photo);
      Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      Uri contentUri = Uri.fromFile(photo);
      mediaScanIntent.setData(contentUri);
      context.sendBroadcast(mediaScanIntent);
      result = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  //保存图片
  private static File getAlbumStorageDir(String albumName) {
    File file = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), albumName);
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  private static void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
    Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(newBitmap);
    canvas.drawColor(Color.BLACK);//Color.WHITE
    canvas.drawBitmap(bitmap, 0, 0, null);
    OutputStream stream = new FileOutputStream(photo);
    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    newBitmap.recycle();
    stream.close();
  }

  /**
   * 保存图片到相册
   */
  public static boolean saveImageToGallery(Context context,Bitmap mBitmap) {
    boolean result = false;
    if (mBitmap==null)
    {
      return false;
    }
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

      return result;
    }
    // 首先保存图片
    File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    String fileName = System.currentTimeMillis() + ".jpg";
    File file = new File(appDir, fileName);
    try {
      FileOutputStream fos = new FileOutputStream(file);
      mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
      return result;
    }

    // 其次把文件插入到系统图库
    try {
      MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } // 最后通知图库更新

    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
    result = true;
    return result;

  }

  /**
   * 保存图片到相册
   */
  public static File saveImageToGallery2(Context context,Bitmap mBitmap) {
    boolean result = false;
    if (mBitmap==null)
    {
      return null;
    }
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

      return null;
    }
    // 首先保存图片
    File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    String fileName = System.currentTimeMillis() + ".jpg";
    File file = new File(appDir, fileName);
    try {
      FileOutputStream fos = new FileOutputStream(file);
      mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    // 其次把文件插入到系统图库
    try {
      MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } // 最后通知图库更新

    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
    result = true;

    return file;
  }
}
