package com.example.module_base.share;


public class COpenMini {


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String userName; //小程序原始id

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 拉起小程序页面的可带参路径，不填默认拉起小程序首页
     */
    public String path;




}
