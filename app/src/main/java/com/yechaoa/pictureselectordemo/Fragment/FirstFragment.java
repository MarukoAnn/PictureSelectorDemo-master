package com.yechaoa.pictureselectordemo.Fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yechaoa.pictureselectordemo.Activity.PhotoActivity;


import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.PostlistData;
import com.yechaoa.pictureselectordemo.Modle.gpsData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;



import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by moonshine on 2018/2/4.
 */

public class FirstFragment extends Fragment {

    private String path = "http://119.23.219.22:80/element-admin/item-info/find";
    private View view;
    private static final String TAG = "MainActivity";
    DataDBHepler dbHepler;
    private static final int REQUEST_CODE = 1;


    /**
     * fragment的视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg1, container, false);
        ZXingLibrary.initDisplayOpinion(getActivity());
        dbHepler = new DataDBHepler(getContext());
//        initView();
//        Time();
//        StaticData staticData = new StaticData();
//        final TextView textView = (TextView) view.findViewById(R.id.laitude);
//        final TextView textView1 = (TextView) view.findViewById(R.id.longitude);
//        textView.setText(gpsData.getLatitude());
//        textView1.setText(gpsData.getLongitude());
//        final Handler handler=new Handler();
//        Runnable runnable=new Runnable() {
//            @Override
//            public void run() {
//                  Log.i("tag","改变后"+gpsData.getLatitude());
//                  textView.setText(gpsData.getLatitude());
//                  textView1.setText(gpsData.getLongitude());
//                handler.postDelayed(this, 3000);
//            }
//
//       };
//        handler.postDelayed(runnable, 3000);
        Button button = (Button)view.findViewById(R.id.saoma_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
//        Button button1 = (Button) view.findViewById(R.id.copy);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 从API11开始android推荐使用android.content.ClipboardManager
//                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
//                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                // 将文本内容放到系统剪贴板里。
//                cm.setText("纬度："+textView.getText()+"，经度："+textView1.getText());
//                Toast.makeText(getActivity(), "复制成功", Toast.LENGTH_LONG).show();
//            }
//        });

        return view;
    }


    public class SpostHttpMap {
        public String posthttpmap(String path,String itemncode) {
            //okhttp Post请求传输Json数据
            String SpostStatus = null;
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            FormBody body = new FormBody.Builder()
                    .add("itemcode",itemncode)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

                ListData listData = postlistData.getValues();
                gpsData.setLatitude(listData.getLatitude());
                gpsData.setLongitude(listData.getLongitude());
                gpsData data = new gpsData();
                data.setItemcode(listData.getItemcode());
                data.setItemdetail(listData.getItemdetail());
                data.setItemmembers(listData.getItemmembers());
                data.setUnitcode(listData.getUnitcode());
                data.setItemname(listData.getItemname());
                Log.i(TAG,"名字为："+listData.getItemname());
                Log.i(TAG,"设备："+listData.getItemdetail());
                Log.i(TAG,"设备："+listData.getItemmembers());

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            SpostHttpMap spostHttpMap = new SpostHttpMap();
                            try {
                                String result1 =   spostHttpMap.posthttpmap(path,result);
                                if(result1.equals("10"))
                                {
                                    Intent intent1 = new Intent();
                                    intent1.setClass(getActivity(), PhotoActivity.class);
                                    startActivity(intent1);
//                        }
                                }else {
                                    Toast.makeText(getActivity(),"扫描错误",Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();

                                Toast.makeText(getActivity(),"网络超时请试",Toast.LENGTH_LONG).show();
                            }

                            Looper.loop();
                        }
                    }).start();
                    Log.i("tag","结果："+result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}