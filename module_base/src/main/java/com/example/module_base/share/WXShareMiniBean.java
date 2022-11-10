package com.example.module_base.share;

import android.graphics.Bitmap;

import com.example.module_base.BuildConfig;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

public class WXShareMiniBean extends WXShareBaseBean {

  public int shareType = WXShareAction.SHARE_MINI;

  private String title;

  private String description;

  private Bitmap bitmap; // 必填字段

  private String userName = WXShareAction.MINI_ID; //小程序id

  private String path; //小程序页面路径，没有则为主页

  private String webpageUrl; //低版本微信备用H5页面 必填字段

  private boolean withShareTicket = true; // 是否使用带shareTicket的分享

  private int miniprogramType = "release".equals(BuildConfig.BUILD_TYPE) ? WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE : WXMiniProgramObject.MINIPROGRAM_TYPE_TEST; //0正式 1测试 2体验

  public String getTitle() {
    return title;
  }

  public WXShareMiniBean setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public WXShareMiniBean setDescription(String description) {
    this.description = description;
    return this;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public WXShareMiniBean setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
    return this;
  }

  public String getUserName() {
    return userName;
  }

  public WXShareMiniBean setUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public String getPath() {
    return path;
  }

  public WXShareMiniBean setPath(String path) {
    this.path = path;
    return this;
  }

  public String getWebpageUrl() {
    return webpageUrl;
  }

  public WXShareMiniBean setWebpageUrl(String webpageUrl) {
    this.webpageUrl = webpageUrl;
    return this;
  }

  public boolean getWithShareTicket() {
    return withShareTicket;
  }

  public WXShareMiniBean setWithShareTicket(boolean withShareTicket) {
    this.withShareTicket = withShareTicket;
    return this;
  }

  public int getMiniprogramType() {
    return miniprogramType;
  }

  public WXShareMiniBean setMiniprogramType(int miniprogramType) {
    this.miniprogramType = miniprogramType;
    return this;
  }
}
