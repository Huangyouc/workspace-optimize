package com.example.module_base.share;

import android.graphics.Bitmap;

public class ShareNewModel {
//  {
//    platform: "wechat" | "friends" | "mini" | "saveimage" | "sms" | "copy" | "wwk" // 微信 朋友圈  小程序  保存到本地 短信分享  复制链接 企业微信
//    type: "img" // 表示只分享图片 pointer  海报
//    title:"" // 标题
//    desc:"" // 描述
//    link:"" // 分享链接
//    imgurl:"" // 图片地址
//    username:"" // 小程序用
//    qrurl:""// 海报专用 二维码
//  }

  /*******分享支持的渠道******/
  public static final String WECHAT = "wechat";
  public static final String WECHAT_MINI = "mini";
  public static final String FRIENDS = "friends";
  public static final String WEWORK = "wwk";//企业微信
  public static final String WEWORK_MINI = "wwkmini";//企业微信的小程序
  public static final String SMS = "sms";
  public static final String COPY = "copy";
  public static final String SAVE = "save"; //仅限保存图片
  public static final String SAVEIMAGE = "saveimage"; //保存图片  SAVE 和 SAVEIMAGE效果一样，为了兼容rn和web传过来的参数
  /*******分享支持的渠道******/

  /*******分享到微信（或企业微信）渠道：分享图片、海报（背景+二维码）、链接******/
  public static final String TYPE_IMG = "img";//微信分享：传过来的是图片的url，现将url转成bitmap，再分享图片到微信
  public static final String TYPE_POINTER = "pointer";//微信微信分享：传过来的是背景图的url和二维码的url，原生生成bitmap，再分享到微信
  public static final String TYPE_WEB = "webpage";
  /*******分享到微信（或企业微信）渠道：分享图片、海报（背景+二维码）、链接******/

  public String platform;
  public String type;
  public String title;
  public String desc;
  public String link;
  public String imgurl;
  public String username;
  public String qrurl;
  public Bitmap mBitmap;


  public Bitmap getmBitmap() {
    return mBitmap;
  }

  public void setmBitmap(Bitmap mBitmap) {
    this.mBitmap = mBitmap;
  }

  public String getQrurl() {
    return qrurl;
  }

  public void setQrurl(String qrurl) {
    this.qrurl = qrurl;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getImgurl() {
    return imgurl;
  }

  public void setImgurl(String imgurl) {
    this.imgurl = imgurl;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }



}
