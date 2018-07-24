package com.yechaoa.pictureselectordemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yechaoa.pictureselectordemo.R;


/**
 * Created by moonshine on 2018/4/12.
 */

public class BbenActivity extends Activity {
    @Override
    public void onCreate(Bundle savdInstanceStated) {
        super.onCreate(savdInstanceStated);
        setContentView(R.layout.banben_layout);
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
