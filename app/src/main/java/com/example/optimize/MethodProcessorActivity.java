package com.example.optimize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.javalib.HycTest;
import com.example.processor.MethodProcessor;

@MethodProcessor(name = "Hyc")
public class MethodProcessorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_processor);

        Toast.makeText(this,new HycTest().getMessage(),Toast.LENGTH_SHORT).show();

    }
}