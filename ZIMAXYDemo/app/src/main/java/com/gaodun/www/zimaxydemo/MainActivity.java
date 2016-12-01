package com.gaodun.www.zimaxydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText edittext;
    private ZhiMaView zhimaview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.btn);
        edittext = (EditText) findViewById(R.id.edittext);
        zhimaview = (ZhiMaView) findViewById(R.id.zhimaview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int num =  Integer.valueOf(edittext.getText().toString().trim());
                zhimaview.setCurrentNumAnim(num);
            }
        });
    }

}
