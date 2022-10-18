package com.example.module_base.fileutil;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.module_base.ApplicationUtil;
import com.example.module_base.LogUtil;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * io操作工具类，未完成
 */
public class IoUtil {

    /**
     * 获取data中文件的input
     *
     * @param context
     * @param fileName 相对路径
     * @return
     */
    public static InputStream readInputFromData(Context context, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(context.getFilesDir() + "/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
    /**
     * 获取文件的input
     */
    public static InputStream readInputFromFile(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }


    public static InputStream readInputFromAssets(Context context, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    public static InputStream readInputFromRaw(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        return inputStream;
    }

    public static String readFromData(Context context, String fileName) {
        return readFromInputStream(readInputFromData(context, fileName));

    }

    public static String readFromAssets(Context context, String fileName) {
        return readFromInputStream(readInputFromAssets(context, fileName));
    }

    public static String readFromFile(String fileName) {
        return readFromInputStream(readInputFromFile(fileName));
    }

    public static String readFromRaw(Context context, int resId) {
        return readFromInputStream(readInputFromRaw(context, resId));
    }

    public static String readFromInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            for (String str; (str = reader.readLine()) != null; ) {
                sb.append(str);
            }

            reader.close();
            inputStream.close();
            return sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void write2Data(Context context, String fileName, String writeString) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(writeString.getBytes());
            LogUtil.d("write2Data success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void write2Data(Context context, String fileName, InputStream inputStream) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
            LogUtil.d("write2Data success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压，使用apache包，解决zip64问题
     * try {
     *           IoUtil.unZipApache(new FileInputStream(zipPath), CRLocalFileHelper.getDirPath(this, CRLocalFileHelper.DIR_DOWNLOAD));
     *         } catch (IOException e) {}
     */
    public static boolean unZipApache(InputStream zipFileInputStream, String destDir) throws IOException {
        destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
        ZipArchiveInputStream is = null;

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(zipFileInputStream, 2048));
            ZipArchiveEntry entry = null;
            while ((entry = is.getNextZipEntry()) != null) {
                File file = new File(destDir, entry.getName());
                LogUtil.e(file.getAbsolutePath());
                File dir = entry.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());

                if (entry.isDirectory())
                    continue;

                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file), 2048);
                    IOUtils.copy(is, os);
                } finally {
                    IOUtils.closeQuietly(os);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }
//        return false;
    }

    /**
     *tar.gz解压
     */
    public static void doUnTarGz(File srcFile, String destPath) throws IOException {
        byte[] buf = new byte[1024];
        FileInputStream fis = new FileInputStream(srcFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        GzipCompressorInputStream cis = new GzipCompressorInputStream(bis);
        TarArchiveInputStream tis = new TarArchiveInputStream(cis);
        TarArchiveEntry tae;
        while ((tae = tis.getNextTarEntry()) != null) {
            File f = new File(destPath + "/" + tae.getName());
            if (tae.isDirectory()) {
                f.mkdirs();
            } else {
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int len;
                while ((len = tis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                bos.flush();
                bos.close();
            }
        }
        tis.close();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            return uri.getPath();
        }

        return null;
    }

    /**
     *
     * @param uri
     * @return
     */
    public static byte[] getFileByte(Uri uri){
        ContentResolver contentResolver = ApplicationUtil.Companion.getContext().getContentResolver();
        byte[] byt= null;
        InputStream inputStream = null;
        try {
            inputStream =  contentResolver.openInputStream(uri);
            byt = new byte[inputStream.available()];
            inputStream.read(byt);
            return byt;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
