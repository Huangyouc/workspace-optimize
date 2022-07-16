package com.example.optimize

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.optimize.aspectj.MethodTrace
import com.example.optimize.fps.FpsMonitor

class MainActivity : AppCompatActivity() {
    @MethodTrace
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.helle).setOnClickListener {
            FpsMonitor.toggle()
        }
    }

    @MethodTrace
    private fun test() {
        try {
            Thread.sleep(2000)
        } catch (e: Exception) {
        }
    }
}