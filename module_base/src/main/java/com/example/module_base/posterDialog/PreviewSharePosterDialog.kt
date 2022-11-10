package com.noahwm.crm.ui.share

import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.widget.LinearLayout
import com.example.module_base.R
import com.example.module_base.resutil.ResUtil
import kotlinx.android.synthetic.main.dialog_preview_poster.*

/**
 * @Title:
 * @Package
 * @Description: 资讯分享海报
 * @author hyc
 * @date 2022/10/24
 * @version V1.0
 */
class PreviewSharePosterDialog(act:Activity, bgUrl:String, qrUrl:String, qr_desc:String, date:String,share_title:String, share_track:String)
    : AbstractPosterDialog(act,bgUrl,qrUrl,share_title,"",date,"",share_track) {
    private val TAG = "PreviewSharePosterDialog"
    private var mQrdesc:String?=null
    init {
        mQrdesc = qr_desc
        IMG_WIDTH = ResUtil.dp2px( 375f)
        IMG_HEIGHT = ResUtil.dp2px(667f)
        QR_WIDTH = ResUtil.dp2px(70f).toFloat()
    }
    override fun getContentLayoutId(): Int {
        return R.layout.dialog_preview_poster
    }

    override fun setEvent() {
        val ll_wxfriend = findViewById<LinearLayout>(R.id.ll_wxfriend)
        ll_wxfriend.setOnClickListener {
            wxShareAction!!.sharedImageByBitmap(
                Bitmap.createBitmap(
                    poster!!
                ), true
            )

            sensorsTrack("微信朋友圈")
            dismiss()
        }

        ll_content.setOnClickListener {
            dismiss()
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

            canvas.drawColor(Color.parseColor("#F2F0EC"))
            canvas.save()

            /*****绘制顶部图片*****/
            val res: Resources = activity!!.getResources()
            val bmp = BitmapFactory.decodeResource(res, R.drawable.preview_poster_top_icon)//poster_preview_icon.png  103  16
            var matrix_ = Matrix()
            matrix_.postScale(
                ResUtil.dp2px(375f).toFloat() / bmp.width,
                ResUtil.dp2px(160f).toFloat() / bmp.height
            )
            val bmpNew = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix_, true)

            canvas.drawBitmap(
                bmpNew,
                0f,
                0f,
                null
            )
            canvas.save()
            bmp.recycle()
            bmpNew.recycle()

            /*****绘制中间白色背景框*****/
            var mPaint = Paint()
            mPaint.setColor(Color.WHITE)
            var leftMargin = ResUtil.dp2px(15f).toFloat()
            var topMargin = ResUtil.dp2px(160f).toFloat()
            var whiteHeight = ResUtil.dp2px(512f).toFloat()
            var mBackGroundRect = RectF(
                leftMargin,
                topMargin,
                IMG_WIDTH-leftMargin,
                whiteHeight
            )
            canvas.drawRoundRect(mBackGroundRect, 15f, 15f, mPaint)
            canvas.save()

            /*****绘制早报时间*****/
            dateStr.let {
                var textPaint = TextPaint()
                textPaint.textSize = ResUtil.sp2px(14f).toFloat()
                textPaint.setStyle(Paint.Style.FILL)
                //该方法即为设置基线上那个点究竟是left,center,还是right 这里我设置为center
                textPaint.setTextAlign(Paint.Align.LEFT)
                textPaint.color = Color.parseColor("#969696")
                //计算绘制文字需要的宽度
                val strWidth: Float = textPaint.measureText(dateStr)
                canvas.drawText(dateStr,(IMG_WIDTH-strWidth)/2, ResUtil.dp2px(185f).toFloat(),textPaint)

                canvas.save()
                canvas.restore()
            }

            /*****绘制资讯的封面图*****/
            bg.let {
                val resizeBitmap = resizeImage2(bg!!)

                val startPadding = (IMG_WIDTH - resizeBitmap.width)/2
                canvas.drawBitmap(resizeBitmap, startPadding.toFloat(), ResUtil.dp2px(216f).toFloat(), null)
                canvas.save()
            }

            /*****绘制资讯title*****/
            val textPaint = TextPaint()
            textPaint.color = Color.parseColor("#3F3F3F")
            textPaint.textSize = ResUtil.sp2px( 18f).toFloat()
            textPaint.strokeWidth = ResUtil.dp2px(6f).toFloat()
            //文字自动换行
            //文字自动换行
            val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(infoName, 0, infoName.length, textPaint, ResUtil.dp2px(305f))
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f) // add, multiplier
                    .setIncludePad(true)
                    .setMaxLines(3)
                    .setEllipsize(TextUtils.TruncateAt.END)
                    .build()
            } else {
                StaticLayout(
                    infoName,
                    textPaint,
                    ResUtil.dp2px(305f),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    true
                )
            }
            canvas.save()
            textPaint.textAlign = Paint.Align.LEFT
            //文字的位置
            canvas.translate(ResUtil.dp2px(40f).toFloat(), ResUtil.dp2px(416f).toFloat())
            layout.draw(canvas)
            canvas.restore()


            /*****绘制二维码*****/
            if(qr!=null){

                val matrix = Matrix()
                QR_WIDTH = ResUtil.dp2px(60f).toFloat()
                QR_LEFT_PADDING = IMG_WIDTH.toFloat() - QR_WIDTH-leftMargin
                matrix.postScale(
                    QR_WIDTH / qr.width,
                    QR_WIDTH / qr.height
                )
                val qrNew = Bitmap.createBitmap(qr, 0, 0, qr.width, qr.height, matrix, true)
                canvas.drawBitmap(
                    qrNew,
                    QR_LEFT_PADDING,
                    ResUtil.dp2px(530f).toFloat(),
                    null
                )
                canvas.save()
                qrNew.recycle()
//            qr.recycle()//这两个不要回收，在内存中，下次海报分享还是会用到的
//            bg.recycle()
            }
            /*****绘制二维码的说明文案*****/
            val strings: List<String>? = mQrdesc?.split("\n")
            strings.let {
                for (index in strings!!.indices) {
                    var textPaint = TextPaint()
                    textPaint.textSize = ResUtil.sp2px( 14f).toFloat()
                    textPaint.setStyle(Paint.Style.FILL)
                    //该方法即为设置基线上那个点究竟是left,center,还是right 这里我设置为center
//                    textPaint.setTextAlign(Paint.Align.CENTER)
                    textPaint.setTextAlign(Paint.Align.RIGHT)
                    textPaint.color = Color.parseColor("#746E6A")
                    canvas.drawText(strings[index],IMG_WIDTH.toFloat()-leftMargin, ResUtil.dp2px(605f+20*index).toFloat(),textPaint)
                }
                canvas.save()
                canvas.restore()
            }

            /*****绘制头像和名字*****/
//            var avatarUrl: String? = ""
//            if (Ifa.faInfo != null) {
//                avatarUrl = AvatarUtil.getAvatarFromJson(Ifa.faInfo.avatar)
//            }
//            val headWidth = ResUtil.dp2px(60f).toFloat()
//
//            val headBtm = UrlImageViewHelper.getCachedBitmap(avatarUrl)
//            headBtm?.let {
//                val matrix = Matrix()
//                matrix.setScale(
//                    headWidth / headBtm.width,
//                    headWidth / headBtm.height
//                )
////                val headNew = Bitmap.createBitmap(headBtm, 0, 0, headBtm.width, headBtm.height, matrix, true)
//                val paint = Paint()
////                paint.setColor(Color.BLACK)
//                paint.setAntiAlias(true)
//                val radius = headWidth/2
//                val shader = BitmapShader(headBtm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
//                paint.setShader(shader)
//                shader.setLocalMatrix(matrix)
//                canvas.translate(leftMargin,0f)
//                canvas.drawCircle(
//                    headWidth/2,
//                    ResUtil.dp2px(540f).toFloat() + radius,
//                    radius,
//                    paint
//                )
//                canvas.save()
//                canvas.restore()
////                headNew.recycle()
//            }
//            if(StringUtils.isNotEmpty(Ifa.faInfo.realName)){
//                var textPaint = TextPaint()
//                textPaint.textSize = DisplayUtils.sp2px(activity, 16f).toFloat()
//                textPaint.setStyle(Paint.Style.FILL)
//                textPaint.setTextAlign(Paint.Align.LEFT)
//                textPaint.color = Color.parseColor("#323232")
//                canvas.drawText(Ifa.faInfo.realName,leftMargin+headWidth+20,  ResUtil.dp2px()578f).toFloat(),textPaint)
//                canvas.save()
//            }


            return poster
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

    fun resizeImage2(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width == IMG_WIDTH) {
            return bitmap
        }
        val newWidth = ResUtil.dp2px( 265f)
        val newHeight = ResUtil.dp2px( 176f)
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat()/height
        //以小的为准，保证ImageView能显示全部的背景图
        val scale=if(scaleWidth<scaleHeight)  scaleWidth else scaleHeight
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(
            bitmap, 0, 0, width,
            height, matrix, true
        )
    }
}