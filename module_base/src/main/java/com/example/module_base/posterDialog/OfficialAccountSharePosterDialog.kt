package com.noahwm.crm.ui.share

import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import com.example.module_base.R
import com.example.module_base.resutil.ResUtil

/**
 * @Title:
 * @Package
 * @Description: 交易明细中的合格投资者确认分享微信公众号/企业微信公众号
 * @author hyc
 * @date 2022/7/19
 * @version V1.0
 */
class OfficialAccountSharePosterDialog(act:Activity,bgUrl:String,qrUrl:String,qr_desc:String,name:String,share_track:String)
    : AbstractPosterDialog(act,bgUrl,qrUrl,name,"","",qr_desc,share_track) {
    private val TAG = "InfoDisclosurePosterDialog"
    private var showFriend = false;
    val IMG_SHADOW_WIDTH = ResUtil.dp2px(216f).toFloat()
    init {
        IMG_WIDTH = ResUtil.dp2px( 295f)
        IMG_HEIGHT = ResUtil.dp2px(450f)
        QR_WIDTH = ResUtil.dp2px(180f).toFloat()
    }

    override fun setEvent() {
        super.setEvent()
        if(showFriend){
            llWxfriend?.visibility = View.VISIBLE
        }else{
            llWxfriend?.visibility = View.GONE
        }
    }
    override fun generatePoster(bg: Bitmap?, qr: Bitmap?): Bitmap? {
        try {

            /*****绘制背景图*****/
            val poster = Bitmap.createBitmap(
                    IMG_WIDTH,
                    IMG_HEIGHT,
                    Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(poster)
            if(bg!=null){
                canvas.drawColor(Color.TRANSPARENT)
                canvas.save()
                val resizeBitmap = resizeImage(bg)
                val startPadding = (IMG_WIDTH-resizeBitmap.width)/2
                canvas.drawBitmap(resizeBitmap, startPadding.toFloat(), 0f, null)
                canvas.save()
            }else{
                canvas.drawColor(Color.TRANSPARENT)
                canvas.save()
                val res: Resources = activity!!.getResources()
                val bgBitmap = BitmapFactory.decodeResource(res, R.drawable.official_account_share_poster_bg)
                canvas.drawBitmap(resizeImage(bgBitmap), 50f, 0f, null)
                canvas.save()
            }


            if(qr!=null){
                /*****绘制二维码的shadow*****/
                val shadowBitmap = BitmapFactory.decodeResource(activity!!.getResources(), R.drawable.poster_qr_shadow_bg)
                val matrix_shadow = Matrix()
                val shadow_padding_left = IMG_WIDTH.toFloat()/2 - IMG_SHADOW_WIDTH/2
                matrix_shadow.postScale(
                    IMG_SHADOW_WIDTH / shadowBitmap.width,
                    IMG_SHADOW_WIDTH / shadowBitmap.height
                )
                val shadowBitmapNew = Bitmap.createBitmap(shadowBitmap, 0, 0, shadowBitmap.width, shadowBitmap.height, matrix_shadow, true)
                canvas.drawBitmap(
                    shadowBitmapNew,
                    shadow_padding_left,
                    ResUtil.dp2px(104f).toFloat(),
                    null
                )
                canvas.save()
                shadowBitmap.recycle()
                shadowBitmapNew.recycle()

                /*****绘制二维码*****/
                val matrix = Matrix()
                QR_LEFT_PADDING = IMG_WIDTH.toFloat()/2 - QR_WIDTH/2
                matrix.postScale(
                        QR_WIDTH / qr.width,
                        QR_WIDTH / qr.height
                )
                val qrNew = Bitmap.createBitmap(qr, 0, 0, qr.width, qr.height, matrix, true)
                canvas.drawBitmap(
                        qrNew,
                        QR_LEFT_PADDING,
                    ResUtil.dp2px( 120f).toFloat(),
                        null
                )
                canvas.save()
                qrNew.recycle()
//            qr.recycle()//这两个不要回收，在内存中，下次海报分享还是会用到的
//            bg.recycle()
            }

            /*****绘制信披名称*****/
            val textPaint = TextPaint()
            textPaint.color = Color.parseColor("#2f2f2f")
            textPaint.textSize = ResUtil.sp2px( 18f).toFloat()
            //文字自动换行
            val layout = StaticLayout(
                    infoName,
                    textPaint,
                ResUtil.dp2px( 100f),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    true
            )
            canvas.save()
            textPaint.textAlign = Paint.Align.LEFT
            //文字的位置
            canvas.translate(ResUtil.dp2px( 64f).toFloat(), ResUtil.dp2px( 23f).toFloat())
            layout.draw(canvas)
            canvas.restore()

            /*****绘制二维码的说明文案*****/
            val strings: List<String> = contentStr.split("\n")
            for (index in strings.indices) {
                var textPaint = TextPaint()
                textPaint.textSize = ResUtil.sp2px(18f).toFloat()
                textPaint.setStyle(Paint.Style.FILL)
                //该方法即为设置基线上那个点究竟是left,center,还是right 这里我设置为center
                textPaint.setTextAlign(Paint.Align.CENTER)
                textPaint.color = Color.parseColor("#2f2f2f")
                canvas.drawText(strings[index],IMG_WIDTH.toFloat()/2, ResUtil.dp2px( 332f+20*index).toFloat(),textPaint)
            }
            canvas.save()
            canvas.restore()
            return poster
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

    fun setShowFriend(sf:Boolean){
        showFriend = sf
    }
}