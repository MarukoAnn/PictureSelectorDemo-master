package com.yechaoa.pictureselectordemo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import com.yechaoa.pictureselectordemo.R;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;





/**
 * Created by moonshine on 2018/2/4.
 */
public class SecondFragment extends Fragment {
    private View view;
    String url = "http://120.78.137.182/element-admin/user/logout";
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg2, container, false);

        return view;
    }

    /**
     * @param keyCode
     * @param event   监听手机back键 点击返回界面
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //启动一个意图,回到桌面
            Intent backHome = new Intent(Intent.ACTION_MAIN);
            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backHome.addCategory(Intent.CATEGORY_HOME);
            startActivity(backHome);
            return true;
        }
        return view.onKeyDown(keyCode, event);
    }


}
