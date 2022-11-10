package com.example.module_base.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.module_base.ApplicationUtil;
import com.example.module_base.BuildConfig;
import com.example.module_base.R;
import com.example.module_base.StringUtils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class WXShareAction {
    public static boolean hasReuest = false;
    public static String sysFlag = "0";//是否使用系统分享  1 是 0 否
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    public static final int SHARETEXT = 0001;// 纯文本分享
    public static final int SHAREIMG = 0002;// 图片分享
    public static final int SHAREIMGURL = 0003;// 图文带链接的分享
    public static final int SHARE_MINI = 0005;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    // 已通过审核APP_ID
    // public final String APP_ID = "wx2c20bb41676cf1d4";//本地签名debug
    public static final String APP_ID = "wx44e9894f20dd08fe";// 正式签名//wx44e9894f20dd08fe
    //    public static final String MINI_ID = "gh_81df1bf8523b";
    public static final String MINI_ID = "gh_98387f60c110";

    private Context context;
    private final int THUMB_SIZE = 100;

    public WXShareAction(Context context) {
        this.context = context;
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, false);
        // 将该app注册到微信
        api.registerApp(APP_ID);
    }

    public void respFromWX(Activity activity) {
        api.handleIntent(activity.getIntent(), (IWXAPIEventHandler) activity);
    }

    public boolean isWXAppInstalled() {
        return api.isWXAppInstalled();
    }

    public boolean isWXAppSupportAPI() {
//        return api.isWXAppSupportAPI();
        return true;
    }

    public boolean isSupportShareWXFriendster() {
        return true;
    }

    public void sendToSession(WXShareBaseBean bean) {
        if (!checkWx()) {
            return;
        }
        switch (bean.shareType) {
            case SHAREIMG:
                weixinShareImg(bean);
                break;
            case SHARETEXT:
                weixinShareText(bean);
                break;
            case SHAREIMGURL:
                weixinShareImgUrl(bean);
                break;
            default:
                break;
        }
    }

    private boolean checkWx() {
        boolean flag = false;
        if (isWXAppInstalled()) {
            int wxSdkVersion = api.getWXAppSupportAPI();
            if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
                flag = true;
            } else {
                flag = false;
                Toast.makeText(context, "您的微信客户端版本较低,无法分享给好友哦！", Toast.LENGTH_SHORT).show();
            }
        } else {
            flag = false;
            Toast.makeText(context, "您尚未安装微信客户端，无法分享给好友哦！", Toast.LENGTH_SHORT)
                    .show();
        }
        return flag;
    }

    private void weixinShareText(WXShareBaseBean bean) {
        WXShareTextBean txtBean = (WXShareTextBean) bean;
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = txtBean.content;
        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = txtBean.content;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = txtBean.isFriends ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        // 调用api接口发送数据到微信
        api.sendReq(req);
    }

    private void weixinShareImg(WXShareBaseBean bean) {
//         Uri imageUri = Uri.parse(getResourcesUri(R.drawable.icon,activity));
        WXShareImgBean ibean = (WXShareImgBean) bean;
        File file = new File(ibean.filePath);
        if (!file.exists()) {
            Toast.makeText(context, "分享的图片不存在", Toast.LENGTH_SHORT).show();
            // 图片不存在
            return;
        }
        if (!hasReuest) {
            WXImageObject imgObj = new WXImageObject();
            imgObj.setImagePath(ibean.filePath);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
            Bitmap bmp = BitmapFactory.decodeFile(ibean.filePath);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
                    THUMB_SIZE, true);
            bmp.recycle();
//        msg.setThumbImage(thumbBmp);
            msg.thumbData = bitmap2Byte(thumbBmp); // 设置缩略图

            int imageSize = msg.thumbData.length / 1024;
            if (imageSize > 32) {
                Toast.makeText(context, "您分享的图片过大", Toast.LENGTH_SHORT).show();
                return;
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = ibean.isFriends ? SendMessageToWX.Req.WXSceneTimeline
                    : SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);

        }
    }

    private void weixinShareImgUrl(WXShareBaseBean bean) {

        WXShareImgUrlBean ibean = (WXShareImgUrlBean) bean;
        if (StringUtils.isNotEmpty(ibean.filePath)) {
            Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                    .load(ibean.filePath).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            Bitmap thumbBmp = Bitmap.createScaledBitmap(resource, THUMB_SIZE,
                                    THUMB_SIZE, true);

                            weixinShareImgUrl2(bean, thumbBmp);
                        }

                        @Override
                        public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Bitmap bmp;
                            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
                            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
                                    THUMB_SIZE, true);
                            bmp.recycle();

                            weixinShareImgUrl2(bean, thumbBmp);
                        }
                    });
        } else {
            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
                    THUMB_SIZE, true);
            bmp.recycle();

            weixinShareImgUrl2(bean, thumbBmp);
        }
    }

    private void weixinShareImgUrl2(WXShareBaseBean bean, Bitmap thumbBmp) {
        WXShareImgUrlBean ibean = (WXShareImgUrlBean) bean;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = ibean.url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = ibean.title;
        msg.description = ibean.description;

        msg.thumbData = bitmap2Byte(thumbBmp); // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = ibean.isFriends ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

    }


    /**
     * 打开小程序中的页面
     */
    public void weixinOpenMiniProgram(COpenMini bean) {
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = bean.getUserName(); // 填小程序原始id
        req.path = bean.getPath();               ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，
        if (BuildConfig.DEBUG) {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST;// 开发版
        } else {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 正式版
        }
        api.sendReq(req);
        api.openWXApp();
    }

    /**
     * 分享小程序中的页面
     * 不支持朋友圈
     */
    @SuppressWarnings("checkResult")
    public void weixinShareMiniProgram(WXShareMiniBean bean) {

        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
        miniProgramObject.userName = bean.getUserName();
        miniProgramObject.path = bean.getPath();
        miniProgramObject.webpageUrl = bean.getWebpageUrl();
        miniProgramObject.withShareTicket = bean.getWithShareTicket();
        miniProgramObject.miniprogramType = bean.getMiniprogramType();
        WXMediaMessage mediaMessage = new WXMediaMessage(miniProgramObject);
        mediaMessage.title = bean.getTitle();
        mediaMessage.description = bean.getDescription();
        Bitmap tmpBmp = bean.getBitmap();
        Bitmap bitmap = null;
        if (tmpBmp != null) {
            // 这里不压缩图片
            // int targetSize = 300;
            // int w = tmpBmp.getWidth();
            // int h = tmpBmp.getHeight();
            // bitmap = Bitmap.createScaledBitmap(tmpBmp, targetSize, targetSize * h / w, true);
            bitmap = tmpBmp;
        }
        if (bitmap != null) {
            // 获取尺寸压缩倍数
            int ratio = getRatioSize(bitmap.getWidth(), bitmap.getHeight());
            Bitmap result;
            if (ratio == 1) {
                result = bitmap;
            } else {
                // 压缩Bitmap到对应尺寸
                result = Bitmap.createBitmap(bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                Rect rect = new Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio);
                canvas.drawBitmap(bitmap, null, rect, null);
            }

            //图片比例控制在5/4
            result = transformRatio(result);

            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int quality = 100;
            byte[] bytes = bitmap2Byte(result, quality, Bitmap.CompressFormat.JPEG);
            while (bytes != null && bytes.length / 1024 > 128 && quality > 0) {
                // 这里换成jpg 因为png是无损 不受quality影响
                bytes = bitmap2Byte(result, (quality -= 10), Bitmap.CompressFormat.JPEG);
            }
            mediaMessage.thumbData = bytes;
        }


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("miniProgram");//  transaction字段用
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

    }

    public void sharedImageByBitmap(Bitmap bp, boolean isFriends) {
        if (!checkWx()) {
            return;
        }

        //		Bitmap bp = BitmapFactory.decodeResource(context.getResources() , drawableId);
        WXImageObject wxImageObject = new WXImageObject(bp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
//        int w = bp.getWidth();
//        int h = bp.getHeight();
        //设置缩略图
//        Bitmap mBp = Bitmap.createScaledBitmap(bp, THUMB_SIZE, THUMB_SIZE*h/w, true);
        bp.recycle();
//		msg.thumbData = bitmap2Byte(mBp, true);
//        msg.thumbData = bitmap2Byte(mBp);

//        int imageSize = msg.thumbData.length / 1024;
//        if (imageSize > 32) {
//            Toast.makeText(context, "您分享的图片过大", Toast.LENGTH_SHORT).show();
//            return;
//        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");//  transaction字段用
        req.message = msg;
        req.scene = isFriends ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    public void wechatLoginAction() {//微信登录
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    /**
     * 把图片转换成字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] bitmap2Byte(Bitmap bm) {
        return bitmap2Byte(bm, 100, Bitmap.CompressFormat.PNG);
    }

    public static byte[] bitmap2Byte(Bitmap bm, int quality, Bitmap.CompressFormat format) {
        byte[] compressData = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            try {
                bm.compress(format, quality, baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            compressData = baos.toByteArray();
            baos.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return compressData;
    }

    ;

    public static int getRatioSize(int bitWidth, int bitHeight) {
// 图片最大分辨率
        int imageHeight = 1280;

        int imageWidth = 960;
// 缩放比
        int ratio = 1;
// 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
// 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;

        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
// 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
// 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }


    /**
     * 微信小程序需要的封面图是 宽/高 = 5/4
     *
     * @return
     */
    private static Bitmap transformRatio(Bitmap bitmap) {
        int heiht = bitmap.getHeight();
        int width = bitmap.getWidth();
        float ratio = ((float) width) / ((float) heiht);
        if (ratio > 1.25) {
            int needWidth = (int) (heiht * 1.25);
            int startWidth = (width - needWidth) / 2;
            int endWidth = width - startWidth;
            Bitmap newBitmap = null;
            try {
                newBitmap = Bitmap.createBitmap(bitmap, startWidth, 0, needWidth, heiht);
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
                return bitmap;
            }
            return newBitmap;

        } else if (ratio < 1.25) {
            int needHeight = (int) (width / 1.25);
            int startHeight = (heiht - needHeight) / 2;
            int endHeight = heiht - startHeight;
            Bitmap newBitmap = null;
            try {
                newBitmap = Bitmap.createBitmap(bitmap, 0, startHeight, width, needHeight);
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
                return bitmap;
            }
            return newBitmap;
        } else {
            return bitmap;
        }
    }
}
