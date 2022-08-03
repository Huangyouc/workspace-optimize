package com.example.optimize

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import annotationtest.AnnotationTest
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

        findViewById<TextView>(R.id.annotation).setOnClickListener {
             startActivity(Intent(this@MainActivity,MainActivity2::class.java))
        }

        findViewById<TextView>(R.id.annotation2).setOnClickListener {
            startActivity(Intent(this@MainActivity,MethodProcessorActivity::class.java))
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