package ui

import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.optimize.R
import kotlinx.android.synthetic.main.activity_screen_orientation.*

class ScreenOrientationActivity : AppCompatActivity() {

    private var mOrientationEventListener: OrientationEventListener?=null //旋转监听
    private var lastDeviceOrientationValue = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_orientation)

        change.setOnClickListener {
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

    override fun onDestroy() {
        mOrientationEventListener?.disable()
        super.onDestroy()
    }

    // 导航浪顶部返回按钮事件
//    onNavigationBarLeftPress = () => {
//        Orientation.getOrientation((t) => {
//            if (t === 'PORTRAIT') {
//                this.props.navigation.pop()
//            } else {
//                this.setOrientation('PORTRAIT');
//            }
//        });
//    }
}