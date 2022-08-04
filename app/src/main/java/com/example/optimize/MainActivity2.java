package com.example.optimize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.javalib.generated.GeneratedClassAnnotationTest;
import com.example.optimize.aspectj.MethodTrace;

import annotationtest.AnnotationTest;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        test();

        //通过GeneratedClassAnnotationTest来代理AnnotationTest的生成
        AnnotationTest annotationTest = new GeneratedClassAnnotationTest().getInstance();
        Toast.makeText(this, "annotationtest成功", Toast.LENGTH_SHORT).show();
    }


    private void test(){
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
    }
}