package com.example.module_file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.module_base.fileutil.FileSizeUtil
import com.example.module_base.fileutil.FileSystemUtil
import com.example.module_base.fileutil.FileUtil
import com.hjq.toast.ToastUtils
import com.tencent.smtt.sdk.TbsReaderView
import java.io.File
import java.lang.ref.WeakReference

/**
 * 使用X5来展示各种格式的文件，并提供下载功能
 */
class TBSFileReaderActivity : Activity(),TbsReaderView.ReaderCallback {
    private val TAG = "TBSFileReaderActivity"
    private var url: String? = null
//    private var name: String? = null
    private var suffix: String? = null
    private var need_share: Boolean = false
    private var news_title: String? = null
    private var item_id: String? = null

    private var fileName: String? = null
    private var dataFile: File? = null

    private var mTbsReaderView:TbsReaderView?=null
    private var mRelativeLayout:RelativeLayout?=null

    companion object {

        const private val FILE_READER_URL = "FILE_READER_URL"
        const private val FILE_READER_NAME = "FILE_READER_NAME"
        const private val FILE_READER_SUFFIX = "FILE_READER_SUFFIX"
        const private val FILE_READER_SHOW_PDF = "FILE_READER_SHOW_PDF"
        const private val NEED_SHARE = "need_share"
        const private val NEWS_TITLE = "news_title"
        const private val ITEM_ID = "item_id"

        protected var runningFlag = true

        /**
         * 取消下载；true=取消下载
         */
        @Volatile
        var cancelDownLoad = false
        var downloadJobFlag = false
        var destroyFlag = false
        const val SHOW_PROGRESS = 1
        const val REMOVE_PROGRESS = 2
        const val NETWORK_ERROR = 5
        const val DOWNLOAD_FINISHED = 27
        const val DOWNLOAD_ERROR = 28
        const val DOWNLOAD_PROGRESS = 29

        fun startTBSFileReaderActivity(ctx: Context?, url: String?, name: String?, suffix: String?,
                                       need_share: String?, news_title: String?, item_id: String?) {
            ctx?.let {
                val intent = Intent(ctx, TBSFileReaderActivity::class.java)
                intent.putExtra(FILE_READER_URL, url)
                intent.putExtra(FILE_READER_NAME, name)
                intent.putExtra(FILE_READER_SUFFIX, suffix)
                intent.putExtra(NEED_SHARE, need_share)
                intent.putExtra(NEWS_TITLE, news_title)
                intent.putExtra(ITEM_ID, item_id)
                ctx.startActivity(intent)
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tbsfilereaderactivity)
        // 初始化Looger工具
        initView()
        initData()
        initOrientationListener()
    }

    override fun onResume() {
        super.onResume()
//        StatusBarUtil.setColor(this, resources.getColor(R.color.all_title_background), 0)
    }



    private fun initData() {
        intent?.let {
            url = intent.getStringExtra(FILE_READER_URL)
            fileName = intent.getStringExtra(FILE_READER_NAME)
            suffix = intent.getStringExtra(FILE_READER_SUFFIX)
            findViewById<TextView>(R.id.title_tv).text = fileName

            need_share = if(intent.hasExtra(NEED_SHARE)){
                TextUtils.equals("1",intent.getStringExtra(NEED_SHARE))
            }else{
                false
            }
            findViewById<RelativeLayout>(R.id.allow_download).visibility = if(need_share){View.VISIBLE}else{View.GONE}

            news_title = intent.getStringExtra(NEWS_TITLE)
            item_id = intent.getStringExtra(ITEM_ID)
        }
        checkDownload()
    }
    private fun initView(){
        mTbsReaderView = TbsReaderView(this, this)
        mRelativeLayout = findViewById<RelativeLayout>(R.id.tbs_view)
        mRelativeLayout?.addView(mTbsReaderView, RelativeLayout.LayoutParams(-1, -1))

        setEvent()

    }

    private fun setEvent() {
        findViewById<RelativeLayout>(R.id.go_back).setOnClickListener {
            if (TextUtils.equals(lastDeviceOrientationValue,"PORTRAIT")) {
                finish()
            } else {
                lastDeviceOrientationValue = "PORTRAIT"
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
        }
        findViewById<RelativeLayout>(R.id.go_back).setOnClickListener {
            DownloadDialog(this@TBSFileReaderActivity,object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    var downloadFile =  FileSystemUtil.createFilePath(this@TBSFileReaderActivity, "download", fileName)
                    if(!downloadFile.exists()){
                        downloadFile.createNewFile()
                    }
                    if(dataFile?.exists() == true){
                        FileUtil.copyFile(downloadFile,dataFile)
                        ToastUtils.show("文件已下载至${downloadFile.path}")
                    }
                    this@TBSFileReaderActivity.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://" + "")
                        )
                    )
                }
            }).show()
        }
    }

    /**
     * 检查是否已经下载
     */
    private fun checkDownload() {
        if (TextUtils.isEmpty(fileName)) {
            return
        }
        if (TextUtils.isEmpty(suffix)) {
            //如果suffix为空 从文件名中寻找
            findSuffix()
        }
//        fileName = FileSystemUtil.getMd5FileName(name, suffix)

        // SD卡不可用 放在cache目录里
        val cacheDir: String = this.externalCacheDir?.absolutePath+File.separator+"tbs"
        val parentFile = File(cacheDir)
        if(!parentFile.exists()){
            parentFile.mkdir()
        }
//        val cacheDir: File? = this.externalCacheDir
        dataFile = File(parentFile, fileName)
        /**
         * 判断文件是否存在，不存在就删除重新下载
         */
        when (dataFile!!.exists() && dataFile!!.isFile && FileSizeUtil.getFileOrDirectorySize(dataFile, 1) > 0) {
            true -> {
                Log.e(TAG,fileName+"该文件存在")
                displayFile(dataFile!!.path)
            }
            else -> {
                if (dataFile!!.exists()) {
                    dataFile!!.delete()
                }
                download()
            }
        }
    }

    /**
     * 如果文件存在会直接认为下载成功
     */
    fun download() {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val tmpFileName = FileSystemUtil.getMd5FileName(url, "tmp")
        val tmpFile = FileSystemUtil.createFilePath(this, "download", tmpFileName)
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        Thread(object : Download(tmpFile, url!!, false, true) {}).start()
        findViewById<LinearLayout>(R.id.ll_file_reader_progress).visibility = View.VISIBLE
    }

    fun findSuffix() {
        val _fileName = fileName.toString().toLowerCase()
        if (_fileName.contains(FileType.DOCX.getValue())) {
            suffix = FileType.DOCX.getValue()
        } else if (_fileName.contains(FileType.DOC.getValue())) {
            suffix = FileType.DOC.getValue()
        } else if (_fileName.contains(FileType.XLSX.getValue())) {
            suffix = FileType.XLSX.getValue()
        } else if (_fileName.contains(FileType.XLS.getValue())) {
            suffix = FileType.XLS.getValue()
        } else if (_fileName.contains(FileType.PPTX.getValue())) {
            suffix = FileType.PPTX.getValue()
        } else if (_fileName.contains(FileType.PPT.getValue())) {
            suffix = FileType.PPT.getValue()
        } else {
            //这里默认给个docx
            suffix = FileType.DOCX.getValue()
        }
    }

    private val tbsReaderTemp: String = Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp"
    private fun displayFile(filePath: String) {

        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        val bsReaderTemp = tbsReaderTemp
        val bsReaderTempFile = File(bsReaderTemp)
        if (!bsReaderTempFile.exists()) {
            Log.d(TAG, "准备创建/TbsReaderTemp！！")
            val mkdir = bsReaderTempFile.mkdir()
            if (!mkdir) {
                Log.d(TAG, "创建/TbsReaderTemp失败！！！！！")
            }
        }
        val bundle = Bundle()
        bundle.putString("filePath", filePath)
        Log.e(TAG,"filePath = $filePath")
        bundle.putString("tempPath", bsReaderTemp)
        val result = mTbsReaderView!!.preOpen(suffix, false)
        Log.d(TAG, "查看文档---$result")
        if (result) {
            mTbsReaderView!!.openFile(bundle)
        } else {
        }
    }

    fun onDownloadFinished(downFile: File?) {
        findViewById<LinearLayout>(R.id.ll_file_reader_progress).visibility = View.INVISIBLE
        when (downFile?.exists()) {
            true -> {
                try {
                    downFile.renameTo(dataFile)
                    downFile.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        displayFile(dataFile!!.path)
    }

    fun onDownloadError(downFile: File?) {
        when (downFile?.exists()) {
            true -> {
                try {
                    downFile.delete()
                } catch (e: Exception) {
                    //do nothing
                }
            }
        }
        findViewById<LinearLayout>(R.id.ll_file_reader_progress).visibility = View.INVISIBLE
        ToastUtils.show("下载失败，请重试！")
    }

    fun onDownloadProgress(progress: Int) {
        if (progress > 0) {
            findViewById<TextView>(R.id.tv_file_reader_progress).text = "${progress}%"
        }
        findViewById<ProgressBar>(R.id.pb_file_reader_progress).progress = progress
    }


    override fun onDestroy() {
        destroyFlag = true
        //销毁界面的时候一定要加上，否则后面加载文件会发生异常。
        mTbsReaderView?.onStop()
        mOrientationEventListener?.disable()
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {

    }


    fun sendMessage(what: Int, obj: Any?) {
        mHandler.sendMessage(mHandler.obtainMessage(what, obj))
    }
    fun sendMessage(what: Int) {
        mHandler.sendMessage(mHandler.obtainMessage(what))
    }

    internal class MyHandler(act: TBSFileReaderActivity) : Handler(Looper.getMainLooper()){
        private var activity:WeakReference<TBSFileReaderActivity>?=null
        init {
            activity = WeakReference<TBSFileReaderActivity>(act)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (destroyFlag) {
                return
            }

            when (msg.what) {

                REMOVE_PROGRESS -> {

                    this.removeMessages(SHOW_PROGRESS)
//                        getProgressBar().hide()
                }
                NETWORK_ERROR -> {
//                        doHideProgressBar()
//                        doHideProgressDialog()
                    ToastUtils.show(
                        "网络连接失败，请稍后再试"
                    )
                }

                DOWNLOAD_FINISHED -> activity?.get()?.onDownloadFinished(
                    msg.obj as File
                )
                DOWNLOAD_PROGRESS -> try {
                    activity?.get()?.onDownloadProgress(msg.obj.toString().toInt())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                DOWNLOAD_ERROR -> activity?.get()?.onDownloadError(msg.obj as File)

                else -> {}
            }
        }
    }
    var mHandler:Handler = MyHandler(this)

    enum class FileType {
        DOC, DOCX, XLS, XLSX, PPT, PPTX;

        fun getValue(): String {
            return this.name.toLowerCase()
        }
    }

    /**
     * Download thread. 如果不需要进度条重写onDownloadFinished()方法处理下载完成后的逻辑
     * 如果需要进度条重写onDownloadProgress()方法即可
     */
    open class Download : Runnable {
        private var file: File
        private var uri: String
        private var needProgressShow = true
        private var needErrorAlertShow = true
        private var activity:WeakReference<TBSFileReaderActivity>?=null

        constructor(file: File, uri: String,act:TBSFileReaderActivity) {
            this.file = file
            this.uri = uri
            cancelDownLoad = false
            activity = WeakReference<TBSFileReaderActivity>(act)
        }

        constructor(
            file: File,
            uri: String,
            needProgressShow: Boolean,
            needErrorAlertShow: Boolean
        ) {
            this.file = file
            this.uri = uri
            this.needProgressShow = needProgressShow
            this.needErrorAlertShow = needErrorAlertShow
            cancelDownLoad = false
        }

        override fun run() {
            if (!runningFlag) {
                return
            }
            if (needProgressShow) {
                activity?.get()?.sendMessage(SHOW_PROGRESS)
            }
            DownloadFileUtils.download(file, uri, 30 * 1000, 3, object : OnDownloadListener {
                override fun onDownloadSuccess() {
                    if (runningFlag) {
                        downloadJobFlag = true
                        activity?.get()?.sendMessage(
                            DOWNLOAD_FINISHED,
                            file
                        )
                    }
                    cancelDownLoad = false
                    if (needProgressShow) {
                        activity?.get()?.sendMessage(REMOVE_PROGRESS)
                    }
                }

                override fun onDownloading(totalBytes: Long, downloadedBytes: Long, progress: Int) {
                        activity?.get()?.mHandler?.sendMessage(
                            activity?.get()?.mHandler!!.obtainMessage(DOWNLOAD_PROGRESS,
                                progress
                            )
                        )
                }

                override fun onDownloadFailed() {
                    if (runningFlag) {
                        activity?.get()?.sendMessage(DOWNLOAD_ERROR, file)
                        if (needErrorAlertShow && !cancelDownLoad) {
                            activity?.get()?.sendMessage(NETWORK_ERROR)
                        }
                    }
                    cancelDownLoad = false
                    if (needProgressShow) {
                        activity?.get()?.sendMessage(REMOVE_PROGRESS)
                    }
                }
            })
        }
    }

    /****屏幕旋转逻辑 start****/
    private var mOrientationEventListener: OrientationEventListener?=null //旋转监听
    private var lastDeviceOrientationValue = ""
    fun initOrientationListener(){
        mOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI){
            override fun onOrientationChanged(orientation: Int) {
                Log.d("hyc>>>", "DeviceOrientation changed to $orientation")

                var deviceOrientationValue = lastDeviceOrientationValue


                if (orientation == -1) {
                    deviceOrientationValue = "UNKNOWN"
                } else if (orientation > 355 || orientation < 5) {
                    deviceOrientationValue = "PORTRAIT"
                    if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {
                        lastDeviceOrientationValue = deviceOrientationValue
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    }
                } else if (orientation > 85 && orientation < 95) {
                    deviceOrientationValue = "LANDSCAPE-RIGHT"
                    if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {
                        lastDeviceOrientationValue = deviceOrientationValue
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    }
                } else if (orientation > 175 && orientation < 185) {
                    deviceOrientationValue = "PORTRAIT-UPSIDEDOWN"
                    if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {
                        lastDeviceOrientationValue = deviceOrientationValue
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
                    }
                } else if (orientation > 265 && orientation < 275) {
                    deviceOrientationValue = "LANDSCAPE-LEFT"
                    if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {
                        lastDeviceOrientationValue = deviceOrientationValue
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                    }
                }
            }

        }

        mOrientationEventListener?.enable()


        findViewById<AppCompatImageView>(R.id.change).setOnClickListener {
            if(TextUtils.equals(lastDeviceOrientationValue,"PORTRAIT")){
                if(TextUtils.equals(getCurrentOrientation(),"LANDSCAPE-RIGHT")){
                    lastDeviceOrientationValue = "LANDSCAPE-RIGHT"
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                }else{
                    lastDeviceOrientationValue = "LANDSCAPE-LEFT"
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                }
            }else{
                lastDeviceOrientationValue = "PORTRAIT"
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
        }
    }
    private fun getCurrentOrientation(): String? {
        val display =
            (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        when (display.rotation) {
            Surface.ROTATION_0 -> return "PORTRAIT"
            Surface.ROTATION_90 -> return "LANDSCAPE-LEFT"
            Surface.ROTATION_180 -> return "PORTRAIT-UPSIDEDOWN"
            Surface.ROTATION_270 -> return "LANDSCAPE-RIGHT"
        }
        return "UNKNOWN"
    }
    /****屏幕旋转逻辑  end****/
}