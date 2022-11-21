package com.example.optimize

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.module_base.ui.DemoActivity
import com.example.module_floatwindow.TestUtils
import com.example.modulea.BuildConfig
//import com.example.optimize.aspectj.MethodTrace
import com.example.optimize.fps.FpsMonitor
import kotlinx.android.synthetic.main.activity_main.*
import ui.ScreenOrientationActivity

class MainActivity : AppCompatActivity() {
//    @MethodTrace
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.hello_tv).setOnClickListener {
            FpsMonitor.toggle()
        }

        findViewById<TextView>(R.id.annotation).setOnClickListener {
             startActivity(Intent(this@MainActivity,MainActivity2::class.java))
        }

        findViewById<TextView>(R.id.annotation2).setOnClickListener {
            startActivity(Intent(this@MainActivity,MethodProcessorActivity::class.java))
        }
        findViewById<TextView>(R.id.webview).setOnClickListener {
            startActivity(Intent(this@MainActivity,WebActivity::class.java))
        }

        findViewById<TextView>(R.id.orientation).setOnClickListener {
            startActivity(Intent(this@MainActivity,ScreenOrientationActivity::class.java))
        }
        findViewById<TextView>(R.id.testtest).setOnClickListener {
            startActivity(Intent(this@MainActivity,DemoActivity::class.java))
        }

        if(com.example.optimize.BuildConfig.DEBUG){
            TestUtils.envChange(this)
        }
    }

//    @MethodTrace
    private fun test() {
        try {
            Thread.sleep(2000)
        } catch (e: Exception) {
        }
    }
}