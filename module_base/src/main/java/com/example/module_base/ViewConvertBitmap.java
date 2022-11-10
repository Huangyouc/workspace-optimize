package com.example.module_base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2020/4/9 11:18
 *
 * @author hyc
 * @version 1.0
 * 文件名称： ViewConvertBitmap.java
 * 类说明： 将view转换成 图片
 *
 * 启动分区存储的情况下，使用：ViewConvertBitmap.addBitmapToAlbum
 * 未启动分区存储，使用：AlbumHelper.saveImageToGallery
 */
public class ViewConvertBitmap {
    /**
     * 获取指定Activity的截屏
     */
    public static Bitmap activityScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();

        return b;
    }

    /**
     * 获取scrollview的截屏
     */
    public static Bitmap scrollViewScreenShot(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 获取listview的截屏
     *
     * @param listview
     * @return
     */
    public static Bitmap shotListView(ListView listview) {
        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();

        //循环对listview的item进行截图， 最后拼接在一起
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();

            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
            //这里可以把listview中单独的item进行保存
//            viewSaveToImage(childView.getDrawingCache());
        }
        int w = listview.getMeasuredWidth();
        Bitmap bigbitmap = Bitmap.createBitmap(w, allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();

            bmp.recycle();
            bmp = null;
        }
        return bigbitmap;
    }

    /**
     * recycleview截图
     *
     * @param view
     * @return
     */
    public static Bitmap shotRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

    /**
     * view转bitmap
     */
    public static Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    /**
     * 将没显示在界面上的view，生成bitmap
     */
//    public static void generateLayoutView(String url, String imageUrl, Activity activity, BitmapDoneListener bitmapDoneListener) {
//        //将布局转化成view对象
//        View view = LayoutInflater.from(activity).inflate(R.layout.common_view_to_bitmap, null);
//
//        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point outSize = new Point();
//        display.getSize(outSize);
//        int width = outSize.x;
//        int height = outSize.y;
//
//        //然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。接着就可以创建位图并在上面绘制了：
//        // 整个View的大小 参数是左上角 和右下角的坐标
//        view.layout(0, 0, width, height);
//        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//
//        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
//
//        view.measure(measuredWidth, measuredHeight);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//
//
//        final ImageView imageView = view.findViewById(R.id.image_view);
//        final ImageView qrView = view.findViewById(R.id.qr_view);
//
//        //注意加载网络图片时一定要用SimpleTarget回调
//        Glide.with(activity).asBitmap().load(imageUrl).into(new CustomViewTarget<ImageView, Bitmap>(imageView) {
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                imageView.setImageBitmap(resource);
//                viewSaveToImage(view, bitmapDoneListener);
//            }
//
//            @Override
//            protected void onResourceCleared(@Nullable Drawable placeholder) {
//
//            }
//        });
//
//        Bitmap bitmap = QrCodeUtil.createImage(url);
//        if(bitmap==null){
//            qrView.setVisibility(View.VISIBLE);
//        }else{
//            qrView.setImageBitmap(bitmap);
//        }
//    }

    /**
     * 把view转成图片
     *
     * @param view
     */
    private static void viewSaveToImage(View view, BitmapDoneListener bitmapDoneListener) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = viewConversionBitmap(view);

        if (bitmapDoneListener != null) {
            bitmapDoneListener.bitmapDone(cachebmp);
        }

        view.destroyDrawingCache();
    }

    /**
     * 把上面获得的bitmap传进来就可以得到圆角的bitmap了
     */
    public void bitmapInBitmap(Bitmap bitmap, ImageView imageView) {
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tempBitmap);

        //图像上画矩形
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);//不填充
        paint.setStrokeWidth(10);  //线的宽度
        canvas.drawRect(10, 20, 100, 100, paint);
        imageView.setImageBitmap(tempBitmap);

        //画中画
        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, 300, 350);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(tempBitmap, src, dst, photoPaint);// 将photo 缩放或则扩大到
        imageView.setImageBitmap(getRoundedCornerBitmap(tempBitmap));
    }

    /**
     * 生成圆角图片
     */
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            //设置圆角大小
            final float roundPx = 30;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * webview截图
     * @param webView 注意：这里使用 腾讯的webview，不能实现截图，属实奇怪
     * @return
     */
    public static  Bitmap captureWebView(WebView webView){
        float scale = webView.getScale();

        int width = webView.getWidth();

        int height = (int) (webView.getContentHeight() * scale + 0.5);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        webView.draw(canvas);

        return bitmap;

    }

    public static boolean addBitmapToAlbum(Bitmap bitmap,String displayName, String mimeType, Bitmap.CompressFormat compressFormat,Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            values.put(MediaStore.MediaColumns.DATA, Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DCIM+"/"+displayName);
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try{
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(compressFormat, 100, outputStream);
                    outputStream.close();
                }


                String mRealPath = "";
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                if  (cursor != null && !cursor.isClosed()) {
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        if (columnIndex > -1) {
                            mRealPath = cursor.getString(columnIndex);
                        }

                        if (!cursor.isClosed()) {
                            cursor.close();
                        }

//                        Logger.e("huangyc","mRealPath="+mRealPath);
                    }
                }

                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }


        return false;
    }

    public interface BitmapDoneListener {
        void bitmapDone(Bitmap bitmap);
    }
}
