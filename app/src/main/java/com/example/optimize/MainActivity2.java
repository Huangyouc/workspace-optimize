package com.example.optimize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inject.ButterKnife;
import com.example.javalib.generated.GeneratedClassAnnotationTest;
import com.example.optimize.rv.MyAdapter;
import com.example.processor.BindView;

import java.util.ArrayList;
import java.util.List;

import annotationtest.AnnotationTest;

public class MainActivity2 extends AppCompatActivity {

    @BindView(value = R.id.tv_test)
    public TextView tv_test;
    @BindView(value = R.id.tv_btn)
    public TextView tv_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
//        test();

        //通过GeneratedClassAnnotationTest来代理AnnotationTest的生成
        AnnotationTest annotationTest = new GeneratedClassAnnotationTest().getInstance();
        Toast.makeText(this, "annotationtest成功", Toast.LENGTH_SHORT).show();


        tv_test.setText("12121212121212121");
        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "黄油刀使用自己写的成功了", Toast.LENGTH_SHORT).show();
            }
        });

        addFragment();
        initRv();
    }
    /**
     * 添加一个fragment
     */
    private void addFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new TestFragment())
                .commit();
    }
    /**
     * 初始化rv
     */
    private void initRv() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        List<String> mList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mList.add("Title" + i);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(this, mList);
        recyclerView.setAdapter(adapter);
    }


    private void test(){
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
    }
}