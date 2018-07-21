package com.yechaoa.pictureselectordemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yechaoa.pictureselectordemo.Modle.SidSelectData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;
import com.yechaoa.pictureselectordemo.Util.UpdateHttp;

import java.util.ArrayList;

/**
 * Created by moonshine on 2018/2/5.
 */

public class LaunchActivity extends Activity {
    String Loginurl = "http://120.78.137.182/element-admin/user/logout";
    String Msid = null;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);

        /**
         * 查询数据库里的sid
         */
        try {
            DataDBHepler dataDBHepler = new DataDBHepler(getBaseContext());
            if (dataDBHepler.isIdorSid()) {
                ArrayList<SidSelectData> DataList = dataDBHepler.FindSidData();
                final SidSelectData data = new SidSelectData(DataList.get(0).getId(), DataList.get(0).getSid());
                Msid = data.getSid();//获取数据库里的sid
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Integer time = 1100;
        Handler handler = new Handler();
        try {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updatehttp();
                    }
                }, time);

        }catch (Exception e)
        {

                e.printStackTrace();
                updatehttp();

        }

        //加载启动图片

        //后台处理耗时任务

    }


    public void  updatehttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                try {
                    try {
                        UpdateHttp updateHttp = new UpdateHttp();
                        result = updateHttp.updatePostHttp();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Msid!=null) {
                                if (result == null) {

                                    Intent intent = new Intent();
                                    intent.putExtra("url", "null");
                                    //跳转至 MainActivity
                                    intent.setClass(LaunchActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束当前的 Activity
                                    LaunchActivity.this.finish();
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("url", result);
                                    //跳转至 MainActivity
                                    intent.setClass(LaunchActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束当前的 Activity
                                    LaunchActivity.this.finish();
                                }
                            }else {
                                    if (result == null) {
                                        Intent intent = new Intent();
                                        intent.putExtra("url", "null");
                                        //跳转至 MainActivity
                                        intent.setClass(LaunchActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        //结束当前的 Activity
                                        LaunchActivity.this.finish();
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra("url", result);
                                        //跳转至 MainActivity
                                        intent.setClass(LaunchActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        //结束当前的 Activity
                                        LaunchActivity.this.finish();
                                    }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setClass(LaunchActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();
    }
}