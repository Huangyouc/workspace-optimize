package com.example.optimize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.optimize.aspectj.MethodTrace;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        test();
    }


    private void test(){
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
    }
}