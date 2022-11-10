package com.example.module_base.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.module_base.AlbumHelper;
import com.example.module_base.ApplicationUtil;
import com.example.module_base.StringUtils;
import com.hjq.toast.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 */
public class ShareNewUtil {

    private static final String PLATFORM = "platform";
    private static final String USERNAME = "username";
    private static final String TYPE = "type";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESC = "desc";
    private static final String IMG_URL = "imgUrl";

    public static void share(Activity activity, ShareNewModel shareChannel) {
        if (activity == null || shareChannel == null) {
            return;
        }
        if(StringUtils.isNotEmpty(shareChannel.link)){
            shareChannel.link = StringUtils.encodeChinese(shareChannel.link);
        }

        if (ShareNewModel.WECHAT.equals(shareChannel.getPlatform())) {
            shareToSns(activity, shareChannel, SHARE_TYPE_WECHAT);
        } else if (ShareNewModel.FRIENDS.equals(shareChannel.getPlatform())) {
            shareToSns(activity, shareChannel, SHARE_TYPE_FRIENDS);
        } else if (ShareNewModel.SAVE.equals(shareChannel.getPlatform()) || ShareNewModel.SAVEIMAGE.equals(shareChannel.getPlatform()) ) {
          saveimage(activity, shareChannel);
        } else if (ShareNewModel.SMS.equals(shareChannel.getPlatform())) {
          shareToSms(activity, shareChannel.getLink());
        } else if (ShareNewModel.COPY.equals(shareChannel.getPlatform())) {
            copy(activity, shareChannel.getLink());
        } else if (ShareNewModel.WECHAT_MINI.equals(shareChannel.getPlatform())) {
            shareToMini(activity,shareChannel);
        } else if(ShareNewModel.WEWORK.equals(shareChannel.getPlatform())){
            shareToWeCom(activity,shareChannel);
        }else if(ShareNewModel.WEWORK_MINI.equals(shareChannel.getPlatform())){
            shareToWeComMini(activity,shareChannel);
        }
    }

    public static final int SHARE_TYPE_WECHAT = SendMessageToWX.Req.WXSceneSession; //会话
    public static final int SHARE_TYPE_FRIENDS = SendMessageToWX.Req.WXSceneTimeline; //朋友圈

    private static void shareToSns(Activity activity, ShareNewModel shareChannel, final int shareType) {
        try {
            final WXShareAction wxShareAction = new WXShareAction(activity);
            if(ShareNewModel.TYPE_IMG.equals(shareChannel.type)){
              if (StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {
                  PreloadImage.Companion.load(shareChannel.getImgurl(), new PreloadImage.LoadCallback() {
                      @Override
                      public void onSuccess(Bitmap loadedBitmap) {
                          if (loadedBitmap!=null) {
                              wxShareAction.sharedImageByBitmap(loadedBitmap, shareType == SendMessageToWX.Req.WXSceneTimeline);
                          }else {
                              ToastUtils.show("分享开小差了，请重试");
                          }
                      }

                      @Override
                      public void onFailed() {
                          ToastUtils.show("分享开小差了，请重试");
                      }
                  });
              }else {
                ToastUtils.show("分享开小差了，请重试");
              }
            }else if(ShareNewModel.TYPE_POINTER.equals(shareChannel.getType())){
                if (StringUtils.isNotEmptyJava(shareChannel.getQrurl())&& StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {
                    Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                            .load(shareChannel.getImgurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmapone, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                    if (loadedBitmapone!=null) {
                                        Glide.with(activity).asBitmap()
                                                .load(shareChannel.getQrurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmaptwo, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
//                                                        if (loadedBitmaptwo!=null) {
//                                                            NewPosterHelper mNewPosterHelper = new NewPosterHelper(activity, "", shareChannel.getTitle(), shareChannel.getDesc(), "", "");
//                                                            Bitmap bitmap = mNewPosterHelper.generatePoster(loadedBitmapone,loadedBitmaptwo);
//                                                            wxShareAction.sharedImageByBitmap(bitmap, shareType == SendMessageToWX.Req.WXSceneTimeline);
//                                                        }else {
//                                                            ToastUtils.show("分享开小差了，请重试");
//                                                        }

                                                        //todo
                                                    }
                                                    @Override
                                                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                                        super.onLoadFailed(errorDrawable);
                                                        ToastUtils.show("分享开小差了，请重试");
                                                    }
                                                });
                                    }else {
                                        ToastUtils.show("分享开小差了，请重试");
                                    }
                                }
                            });
                }else {
                    ToastUtils.show("分享开小差了，请重试");
                }
            }else {
              WXShareImgUrlBean imgUrlBean = new WXShareImgUrlBean();
              imgUrlBean.isFriends = shareType == SendMessageToWX.Req.WXSceneTimeline;
              imgUrlBean.title = shareChannel.getTitle();
              imgUrlBean.description = shareChannel.getDesc();
              imgUrlBean.filePath = shareChannel.getImgurl();
              imgUrlBean.url = shareChannel.getLink();
              wxShareAction.sendToSession(imgUrlBean);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
  private static void saveimage(Activity activity, ShareNewModel shareChannel) {
    try {
      if(ShareNewModel.TYPE_IMG.equals(shareChannel.type)){
        if (StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {
            PreloadImage.Companion.load(shareChannel.getImgurl(), new PreloadImage.LoadCallback() {
                @Override
                public void onSuccess(@org.jetbrains.annotations.Nullable Bitmap loadedBitmap) {
                    if (loadedBitmap != null && AlbumHelper.saveImageToGallery(activity, loadedBitmap)) {
                        ToastUtils.show("成功保存到相册");
                    } else {
                        ToastUtils.show("保存相册失败");
                    }
                }

                @Override
                public void onFailed() {
                    ToastUtils.show( "保存相册失败");
                }
            });
        }else {
            ToastUtils.show("保存失败");
        }
      }else if(ShareNewModel.TYPE_POINTER.equals(shareChannel.getType())){
        if (StringUtils.isNotEmptyJava(shareChannel.getQrurl())&& StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {

            Glide.with(activity).asBitmap()
                    .load(shareChannel.getImgurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmapone, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                                    .load(shareChannel.getQrurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmaptwo, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                            if (loadedBitmapone!=null&&loadedBitmaptwo!=null)
                                            {
//                                                NewPosterHelper mNewPosterHelper = new NewPosterHelper(activity, "", shareChannel.getTitle(), shareChannel.getDesc(), "", "");
//                                                Bitmap bitmap = mNewPosterHelper.generatePoster(loadedBitmapone,loadedBitmaptwo);
//                                                if (AlbumHelper.saveImageToGallery(activity, bitmap)) {
//                                                    ToastUtils.show( "成功保存到相册");
//                                                } else {
//                                                    ToastUtils.show( "保存相册失败");
//                                                }
                                                //todo
                                            }
                                        }
                                        @Override
                                        public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                            super.onLoadFailed(errorDrawable);
                                            ToastUtils.show( "保存相册失败");
                                        }
                                    });
                        }
                    });
        }else {
            ToastUtils.show("保存失败");
        }
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }
    public static void shareToMini(Activity activity, ShareNewModel shareChannel) {
        try {
            final WXShareAction wxShareAction = new WXShareAction(activity);
                WXShareMiniBean miniBean = new WXShareMiniBean();
                miniBean.setTitle(shareChannel.getTitle());
                miniBean.setDescription(shareChannel.desc);
                miniBean.setPath(shareChannel.getLink());
                if(StringUtils.isNotEmpty(shareChannel.getUsername())){
                    miniBean.setUserName(shareChannel.getUsername());
                }
                miniBean.setWebpageUrl(shareChannel.getLink());

            Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                    .load(shareChannel.getImgurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmap, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            miniBean.setBitmap(loadedBitmap);
                            wxShareAction.weixinShareMiniProgram(miniBean);
                        }
                    });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    private static void shareToSms(Activity activity, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        try {
            Uri smsToUri = Uri.parse("smsto:");
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", content);
            activity.startActivity(intent);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void copy(Activity activity, String content) {
        ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    //企业微信
    private static void shareToWeCom(Activity activity, ShareNewModel shareChannel){
        try {
            if(ShareNewModel.TYPE_IMG.equals(shareChannel.type)){
                if (StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {
                    PreloadImage.Companion.load(shareChannel.getImgurl(), new PreloadImage.LoadCallback() {
                        @Override
                        public void onSuccess(@org.jetbrains.annotations.Nullable Bitmap loadedBitmap) {
                            if (loadedBitmap!=null) {
                                WeComShareManager.Companion.getInstance().weComShareImg(loadedBitmap);
                            }else {
                                ToastUtils.show("分享开小差了，请重试");
                            }
                        }

                        @Override
                        public void onFailed() {
                            ToastUtils.show("分享开小差了，请重试");
                        }
                    });
                }else {
                    ToastUtils.show("分享开小差了，请重试");
                }
            }else if(ShareNewModel.TYPE_POINTER.equals(shareChannel.getType())){
                if (StringUtils.isNotEmptyJava(shareChannel.getQrurl())&& StringUtils.isNotEmptyJava(shareChannel.getImgurl())) {
                    Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                            .load(shareChannel.getImgurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmapone, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                    if (loadedBitmapone!=null) {
                                        Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                                                .load(shareChannel.getQrurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmaptwo, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
//                                                        if (loadedBitmaptwo!=null) {
//                                                            NewPosterHelper mNewPosterHelper = new NewPosterHelper(activity, "", shareChannel.getTitle(), shareChannel.getDesc(), "", "");
//                                                            Bitmap bitmap = mNewPosterHelper.generatePoster(loadedBitmapone,loadedBitmaptwo);
//                                                            WeComShareManager.Companion.getInstance().weComShareImg(bitmap);
//                                                        }else {
//                                                            ToastUtils.show("分享开小差了，请重试");
//                                                        }
                                                        //todo
                                                    }
                                                    @Override
                                                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                                        super.onLoadFailed(errorDrawable);
                                                        ToastUtils.show( "分享开小差了，请重试");
                                                    }
                                                });
                                    }else {
                                        ToastUtils.show("分享开小差了，请重试");
                                    }
                                }
                            });
                }else {
                    ToastUtils.show("分享开小差了，请重试");
                }
            }else {
                WXShareImgUrlBean imgUrlBean = new WXShareImgUrlBean();
                imgUrlBean.title = shareChannel.getTitle();
                imgUrlBean.description = shareChannel.getDesc();
                imgUrlBean.url = shareChannel.getLink();
                imgUrlBean.filePath = shareChannel.getImgurl();
                WeComShareManager.Companion.getInstance().weComShareLink(imgUrlBean);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //企业微信小程序
    public static void shareToWeComMini(Activity activity, ShareNewModel shareChannel){
        WXShareMiniBean miniBean = new WXShareMiniBean();
        miniBean.setTitle(shareChannel.getTitle());
        miniBean.setDescription(shareChannel.desc);
        miniBean.setPath(shareChannel.getLink());
        if(StringUtils.isNotEmpty(shareChannel.getUsername())){
            miniBean.setUserName(shareChannel.getUsername());
        }
        miniBean.setWebpageUrl(shareChannel.getLink());

        Glide.with(ApplicationUtil.Companion.getContext()).asBitmap()
                .load(shareChannel.getImgurl()).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap loadedBitmap, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        miniBean.setBitmap(loadedBitmap);
                        WeComShareManager.Companion.getInstance().weComMiniProgram(miniBean,activity);
                    }
                });
    }

    //打开微信小程序（目前没用过）
    public static void openMini(Activity activity, COpenMini shareChannel) {
        if (activity == null || shareChannel == null) {
            return;
        }
        try {
            final WXShareAction wxShareAction = new WXShareAction(activity);
            wxShareAction.weixinOpenMiniProgram(shareChannel);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }
    public static COpenMini getCOpenMini(HashMap<String,String> params) {

        COpenMini channel = new COpenMini();
        channel.setUserName(params.get("userUame"));
        channel.setPath(params.get("path"));

        return channel;
    }

    public static List<String> getExistApks(Context context) {
        List<String> list = new ArrayList<>();

        if (checkInstalled(context, "com.tencent.mm")) {
            list.add(ShareNewModel.WECHAT);
            list.add(ShareNewModel.FRIENDS);
            list.add(ShareNewModel.WECHAT_MINI);
        }

        list.add(ShareNewModel.SMS);
        list.add(ShareNewModel.COPY);
        list.add(ShareNewModel.WEWORK);
        list.add(ShareNewModel.WEWORK_MINI);
        list.add(ShareNewModel.SAVE);
        list.add(ShareNewModel.SAVEIMAGE);
        return list;
    }

    public static boolean checkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean shareChannelExist(Context context, String type) {
        List<String> existApks = getExistApks(context);
        if (existApks != null && existApks.size() > 0) {
            for (String item : existApks) {
                if (item != null && item.equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static ShareNewModel getShareNewModel(HashMap<String,String> params) {
        if (params == null
                || !params.containsKey(PLATFORM)
                || !(params.get(PLATFORM) instanceof String)
                || TextUtils.isEmpty(params.get(PLATFORM))) {
            return null;
        }

        String platform = params.get(PLATFORM);
        if (ShareNewModel.WECHAT.equals(platform)
                || ShareNewModel.FRIENDS.equals(platform)|| ShareNewModel.WEWORK.equals(platform)) {
            if (!params.containsKey(TITLE) || TextUtils.isEmpty(params.get(TITLE))
                    || !params.containsKey(LINK) || TextUtils.isEmpty(params.get(LINK))
                    || !params.containsKey(DESC) || TextUtils.isEmpty(params.get(DESC))) {
                return null;
            }
        } else if (ShareNewModel.SMS.equals(platform) || ShareNewModel.COPY.equals(platform)) {
            if (!params.containsKey(DESC)) {
                return null;
            }
        }

        ShareNewModel channel = new ShareNewModel();
        channel.platform = params.get(PLATFORM);
        channel.username = params.get(USERNAME);
        channel.type = params.containsKey(TYPE) ? params.get(TYPE) : ShareNewModel.TYPE_WEB;
        channel.title = params.containsKey(TITLE) ? params.get(TITLE) : "";
        channel.link = params.containsKey(LINK) ? params.get(LINK) : "";
        channel.desc = params.containsKey(DESC) ? params.get(DESC) : "";
        channel.imgurl = params.containsKey(IMG_URL) ? params.get(IMG_URL) : "";
        return channel;
    }


}
