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
 * @Description: 信披海报分享框
 * @author hyc
 * @date 2022/6/28 1:08 PM
 * @version V1.0
 */
class InfoDisclosurePosterDialog(
    acy: Activity?, bg_url: String, qrcode_url: String,
    info: String, product: String, date: String, content: String, share_track: String
) :
    AbstractPosterDialog(acy, bg_url,qrcode_url,info,product,date,content,share_track), View.OnClickListener {

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
                canvas.drawColor(Color.WHITE)
                canvas.save()
                canvas.drawBitmap(resizeImage(bg), 0f, 0f, null)
                canvas.save()
            }else{
                canvas.drawColor(Color.WHITE)
                canvas.save()
                val res: Resources = activity!!.getResources()
                val bgBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_background)
                canvas.drawBitmap(resizeImage(bgBitmap), 0f, 0f, null)
                canvas.save()
            }

            /*****绘制二维码*****/
            if(qr!=null){
                val matrix = Matrix()
                QR_WIDTH = ResUtil.dp2px(60f).toFloat()
                QR_LEFT_PADDING = IMG_WIDTH.toFloat()/2 - QR_WIDTH/2
                matrix.postScale(
                        QR_WIDTH / qr.width,
                        QR_WIDTH / qr.height
                )
                val qrNew = Bitmap.createBitmap(qr, 0, 0, qr.width, qr.height, matrix, true)
                canvas.drawBitmap(
                        qrNew,
                        QR_LEFT_PADDING,
                    ResUtil.dp2px( 474f).toFloat(),
                        null
                )
                canvas.save()
                qrNew.recycle()
//            qr.recycle()//这两个不要回收，在内存中，下次海报分享还是会用到的
//            bg.recycle()
            }

            /*****绘制中间白色背景框*****/
            var mPaint = Paint()
            mPaint.setColor(Color.WHITE)
            var leftMargin = ResUtil.dp2px( 15f).toFloat()
            var topMargin = ResUtil.dp2px(140f).toFloat()
            var whiteHeight = ResUtil.dp2px( 430f).toFloat()
            var mBackGroundRect = RectF(
                    leftMargin,
                    topMargin,
                    IMG_WIDTH-leftMargin,
                    whiteHeight
            )
            canvas.drawRoundRect(mBackGroundRect, 15f, 15f, mPaint)
            canvas.save()

            /*****绘制 信息披露 icon*****/
            val res: Resources = activity!!.getResources()
            val bmp = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_background)
            var matrix_ = Matrix()
            matrix_.postScale(
                ResUtil.dp2px( 64f).toFloat() / bmp.width,
                ResUtil.dp2px( 20f).toFloat() / bmp.height
            )
            val bmpNew = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix_, true)

            canvas.drawBitmap(
                    bmpNew,
                ResUtil.dp2px( 35f).toFloat(),
                ResUtil.dp2px( 160f).toFloat(),
                    null
            )
            canvas.save()
            bmp.recycle()
            bmpNew.recycle()

            /*****绘制信披名称*****/
            val textPaint = TextPaint()
            textPaint.color = Color.parseColor("#190F07")
            textPaint.textSize = ResUtil.dp2px(15f).toFloat()
            val rect = Rect(
                ResUtil.dp2px( 35f),
                ResUtil.dp2px( 185f),
                    IMG_WIDTH - ResUtil.dp2px(35f),
                ResUtil.dp2px(
                            250f
                    )
            )
            //文字自动换行
            val layout = StaticLayout(
                    infoName,
                    textPaint,
                    rect.width(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    true
            )
            canvas.save()
            textPaint.textAlign = Paint.Align.LEFT
            //文字的位置
            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
            layout.draw(canvas)
            canvas.restore()

            /*****绘制分割线*****/
            val paint_line = Paint()
            paint_line.color = Color.parseColor("#fff2f0ec")
            canvas.drawLine(
                    leftMargin,
                ResUtil.dp2px(241f).toFloat(),
                    IMG_WIDTH.toFloat(),
                ResUtil.dp2px( 241.5f).toFloat(),
                    paint_line
            )
            canvas.save()

            /*****绘制产品名称*****/
            val textPaint_2 = Paint()
            textPaint_2.textSize = ResUtil.dp2px(14f).toFloat()
            textPaint_2.isAntiAlias = true
//            textPaint_2.typeface = Typeface.DEFAULT_BOLD
            textPaint_2.color = Color.parseColor("#190F07")
            canvas.drawText(
                    "产品名称：",
                ResUtil.dp2px( 35f).toFloat(),
                ResUtil.dp2px(280f).toFloat(),
                    textPaint_2
            )
            canvas.save()

//            val textPaint2 = Paint()
//            textPaint2.textSize =  DisplayUtils.sp2px(activity, 14f).toFloat()
//            textPaint2.isAntiAlias = true
////            textPaint2.typeface = Typeface.DEFAULT_BOLD
//            textPaint2.color = Color.parseColor("#ff746e6a")
//            canvas.drawText(
//                productName,
//                DisplayUtils.dp2px(activity, 35f).toFloat(),
//                DisplayUtils.dp2px(activity, 300f).toFloat(),
//                textPaint2
//            )
//            canvas.save()

            val textPaint2 = TextPaint()
            textPaint2.color = Color.parseColor("#ff746e6a")
            textPaint2.textSize = ResUtil.dp2px(14f).toFloat()
            val rect2 = Rect(
                ResUtil.dp2px( 35f),
                ResUtil.dp2px(293f),
                    IMG_WIDTH - ResUtil.dp2px( 35f),
                ResUtil.dp2px(
                            360f
                    )
            )
            //文字自动换行
            val layout2 = StaticLayout(
                    productName,
                    textPaint2,
                    rect2.width(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    true
            )
            canvas.save()
            textPaint2.textAlign = Paint.Align.LEFT
            //文字的位置
            canvas.translate(rect2.left.toFloat(), rect2.top.toFloat())
            layout2.draw(canvas)
            canvas.restore()

            /*****绘制披露日期*****/
            val textPaint_3= Paint()
            textPaint_3.textSize =  ResUtil.dp2px( 14f).toFloat()
            textPaint_3.isAntiAlias = true
//            textPaint2.typeface = Typeface.DEFAULT_BOLD
            textPaint_3.color = Color.parseColor("#190F07")
            canvas.drawText(
                    "披露日期：",
                ResUtil.dp2px(35f).toFloat(),
                ResUtil.dp2px(365f).toFloat(),
                    textPaint_3
            )
            canvas.save()

            val textPaint3 = Paint()
            textPaint3.textSize = ResUtil.dp2px(14f).toFloat()
            textPaint3.isAntiAlias = true
            textPaint3.typeface = Typeface.DEFAULT_BOLD
            textPaint3.color = Color.parseColor("#746E6A")
            canvas.drawText(
                    dateStr,
                ResUtil.dp2px( 35f).toFloat(),
                ResUtil.dp2px( 395f).toFloat(),
                    textPaint3
            )
            canvas.save()


            /*****绘制二维码的说明文案*****/
            val strings: List<String> = contentStr.split("\n")
            for (index in strings.indices) {
                var textPaint = TextPaint()
                textPaint.textSize = ResUtil.dp2px( 14f).toFloat()
                textPaint.setStyle(Paint.Style.FILL)
                //该方法即为设置基线上那个点究竟是left,center,还是right 这里我设置为center
                textPaint.setTextAlign(Paint.Align.CENTER)
                textPaint.color = Color.parseColor("#746E6A")
                canvas.drawText(strings[index],IMG_WIDTH.toFloat()/2, ResUtil.dp2px( 555f+20*index).toFloat(),textPaint)
            }
            canvas.save()
            canvas.restore()
            return poster
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

}