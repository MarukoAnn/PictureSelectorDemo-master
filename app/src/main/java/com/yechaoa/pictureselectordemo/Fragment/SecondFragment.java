package com.yechaoa.pictureselectordemo.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.yechaoa.pictureselectordemo.Activity.BbenActivity;
import com.yechaoa.pictureselectordemo.Activity.LaunchActivity;
import com.yechaoa.pictureselectordemo.Activity.PersonalActivity;
import com.yechaoa.pictureselectordemo.Activity.Setpsw;
import com.yechaoa.pictureselectordemo.Modle.SidSelectData;
import com.yechaoa.pictureselectordemo.R;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.Util.SelecthttpUserUtil;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by moonshine on 2018/2/4.
 */
public class SecondFragment extends Fragment {
    private View view;
    String url = "http://119.23.219.22:80/element-admin/user/logout";
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg2, container, false);


        TextView tv =(TextView) view.findViewById(R.id.change_paw);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Setpsw.class));
            }
        });
        TextView tv1=(TextView) view.findViewById(R.id.pensonal);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),PersonalActivity.class));
            }
        });
        TextView textView = (TextView) view.findViewById(R.id.we);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),BbenActivity.class));
            }
        });
        /**
         *  登出
         */
        final SelecthttpUserUtil selecthttpUserUtil = new SelecthttpUserUtil();
        Button btn_out = (Button)view.findViewById(R.id.login_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            String SID = SelectSid();
                            selecthttpUserUtil.postSidhttp(SID,url);
                            DataDBHepler dataDBHepler = new DataDBHepler(getContext());
                            dataDBHepler.delete("1");
                            startActivity(new Intent(getActivity(), LaunchActivity.class));
                            getActivity().finish();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        Looper.loop();
                    }
                }).start();
            }

        });

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


    /**
     * 查询数据库里的sid
     */
    public String SelectSid(){

        DataDBHepler dataDBHepler = new DataDBHepler(getContext());
        ArrayList<SidSelectData> DataList = dataDBHepler.FindSidData();
        final SidSelectData data = new SidSelectData(DataList.get(0).getId(),DataList.get(0).getSid());

        final String Msid = data.getSid();//获取数据库里的sid
        return Msid;
    }


}
