package com.example.module_base.share

import android.app.Activity
import android.graphics.Bitmap
import android.widget.Toast
import com.example.module_base.ApplicationUtil
import com.tencent.wework.api.IWWAPI
import com.tencent.wework.api.WWAPIFactory
import com.tencent.wework.api.model.*


/**
 * @Title:
 * @Package
 * @Description: 企业微信分享统一管理类
 * @author hyc
 * @date 2022/3/22 3:13 PM
 * @version V1.0
 */
class WeComShareManager{
    private constructor(){
        initWeCom()
    }
    companion object{
        var SCHEMA: String = "wwauth33c91e748f1f384000063"
        var AGENTID: String = "1000063"
        var APPID: String = "ww33c91e748f1f38e4"
        val  instance = SingletonHolder.holder
    }

    private object SingletonHolder{
        var holder = WeComShareManager()
    }


    var stringId: Int=0
    var iwwapi: IWWAPI? = null

//    var SCHEMA: String?= null
//    var AGENTID: String?= null
//    var APPID: String?= null
    private fun initWeCom(){
        stringId = ApplicationUtil.getContext().getApplicationInfo().labelRes
        iwwapi = WWAPIFactory.createWWAPI(ApplicationUtil.getContext())

        iwwapi?.registerApp(SCHEMA)
    }

    //文本分享
    fun weComShareText(bean: WXShareTextBean){
        val txt = WWMediaText(bean.content)
        txt.appPkg = ApplicationUtil.getContext().getPackageName()
        txt.appName = ApplicationUtil.getContext().getString(stringId)
        txt.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段

        txt.agentId = AGENTID //应用唯一标识。显示在具体应用下的 AgentId字段

        iwwapi?.sendMessage(txt)
    }

    //图片类型分享示例
    fun weComShareImg(bean: WXShareImgBean){
        val img = WWMediaImage()
        img.fileName =""//todo
        img.filePath = bean.filePath
        img.appPkg =ApplicationUtil.getContext().getPackageName()
        img.appName = ApplicationUtil.getContext().getString(stringId)
        img.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段

        img.agentId = AGENTID
        iwwapi!!.sendMessage(img)
    }
    //图片类型分享示例
    fun weComShareImg(bp:Bitmap){
        val img = WWMediaImage(bp)
        img.fileName =""//todo
//        img.filePath = bean.filePath
        img.appPkg =ApplicationUtil.getContext().getPackageName()
        img.appName = ApplicationUtil.getContext().getString(stringId)
        img.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段

        img.agentId = AGENTID
        iwwapi!!.sendMessage(img)
    }

    //网页类型分享示例(就是图文分享)
    fun weComShareLink(bean: WXShareImgUrlBean){
        val link = WWMediaLink()
        link.thumbUrl = bean.filePath
        link.webpageUrl = bean.url
        link.title = bean.title
        link.description = bean.description
        link.appPkg = ApplicationUtil.getContext().getPackageName()
        link.appName = ApplicationUtil.getContext().getString(stringId)
        link.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段

        link.agentId = AGENTID //应用唯一标识。显示在具体应用下的 AgentId字段

        iwwapi!!.sendMessage(link)
    }

    //小程序类型分享示例
    fun weComMiniProgram(bean: WXShareMiniBean, activity: Activity){
        val miniProgram = WWMediaMiniProgram()
        miniProgram.appPkg = ApplicationUtil.getContext().getPackageName()
        miniProgram.appName = ApplicationUtil.getContext().getString(stringId)

        miniProgram.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段

        miniProgram.agentId = AGENTID //应用唯一标识。显示在具体应用下的 AgentId字段

        miniProgram.schema = SCHEMA

        bean.userName?.let {
            if(!bean.userName.endsWith("@app")){//必须是应用关联的小程序，注意要有@app后缀
                bean.userName = bean.userName+"@app"
            }
        }
        miniProgram.username = bean.userName

        miniProgram.description = bean.description
        miniProgram.path = bean.path

        var bitmap: Bitmap? = bean.bitmap
        if (bitmap != null) {
            var quality = 100
            var bytes = WXShareAction.bitmap2Byte(bitmap, quality, Bitmap.CompressFormat.PNG)
            while (bytes != null && bytes.size / 1024 > 128 && quality > 10) {
                // 这里换成jpg 因为png是无损 不受quality影响
                quality -= 10
                bytes = WXShareAction.bitmap2Byte(bitmap, quality, Bitmap.CompressFormat.JPEG)
            }
            miniProgram.hdImageData = bytes
        }

        miniProgram.title = bean.title
        iwwapi!!.sendMessage(miniProgram) { resp ->
            if (resp is WWSimpleRespMessage) {
                val rsp = resp as WWSimpleRespMessage
                var t: String? = ""
                Toast.makeText(
                    activity,
                    "发小程序," + rsp.errCode + "," + rsp.errMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}