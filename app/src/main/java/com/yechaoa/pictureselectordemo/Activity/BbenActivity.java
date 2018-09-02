package com.yechaoa.pictureselectordemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.yechaoa.pictureselectordemo.R;


/**
 * Created by moonshine on 2018/4/12.
 */

public class BbenActivity extends Activity {
    @Override
    public void onCreate(Bundle savdInstanceStated) {
        super.onCreate(savdInstanceStated);
        setContentView(R.layout.banben_layout);
        UltimateBar.newColorBuilder()
                .statusColor(Color.parseColor("#6eb295")) // 状态栏颜色
                .statusDepth(50)                // 状态栏颜色深度
                .build(this)
                .apply();
        ImageView imageView = (ImageView) findViewById(R.id.returnview2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("index", "1");
                intent.setClass(BbenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
