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

public class SuccessActivity extends Activity {
    public void onCreate(Bundle savdInstanceStated) {
        super.onCreate(savdInstanceStated);
        setContentView(R.layout.success_layout);
        UltimateBar.newColorBuilder()
                .statusColor(Color.parseColor("#444e5a")) // 状态栏颜色
                .statusDepth(50)                // 状态栏颜色深度
                .build(this)
                .apply();
        final ImageView imageView = (ImageView) findViewById(R.id.fahui);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("index","0");
                intent.setClass(SuccessActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
//    /**
//     * @param keyCode
//     * @param event   监听手机back键 点击返回界面
//     * @return
//     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            //启动一个意图,回到桌面
//            Intent backHome = new Intent(Intent.ACTION_MAIN);
//            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            backHome.addCategory(Intent.CATEGORY_HOME);
//            startActivity(backHome);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
@Override
public void finish() {
    super.finish();
    overridePendingTransition(R.anim.down_in, R.anim.down_out);
}
}
