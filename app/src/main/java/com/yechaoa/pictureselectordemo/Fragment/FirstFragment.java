package com.yechaoa.pictureselectordemo.Fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.google.gson.Gson;
import com.yechaoa.pictureselectordemo.Activity.MainActivity;
import com.yechaoa.pictureselectordemo.Activity.PhotoActivity;
import com.yechaoa.pictureselectordemo.Activity.ScanActivity;


import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.PostlistData;
import com.yechaoa.pictureselectordemo.Modle.StaticData;
import com.yechaoa.pictureselectordemo.Modle.gpsData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by moonshine on 2018/2/4.
 */

public class FirstFragment extends Fragment {

    private String path = "http://123.249.28.108:8081/element-admin/item-info/find";
//    private String path ="http://192.168.28.74:8080/EquipmentInspection/item-info/find";
    private View view;
    private static final String TAG = "MainActivity";
    private Handler mHandler;
    private ReceiveBroadCast receiveBroadCast;
    String result;
    DataDBHepler dbHepler;
    String latitude;
    String longitude;

//   String oid="";

    /**
     * 启用onattach来获取activity调用的 onActivityresult 传递过来的参数
     * @param**/
    @Override
    public void onAttach(final Activity activity) {
       /** 注册广播 */

        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gasFragment");    //只有持有相同的action的接受者才能接收此广播
        activity.registerReceiver(receiveBroadCast, filter);
        super.onAttach(activity);

    }



    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {

            result = intent.getExtras().getString("result");
            Log.i("log", "在discoverFragment中获取的扫描值:" + result);
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

   }

  }

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
                IntentIntegrator integrator =new IntentIntegrator(getActivity());
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.setPrompt("请扫描二维码"); //底部的提示文字，设为""可以置空
                integrator.setCameraId(0); //前置或者后置摄像头
                integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);//是否保留扫码成功时候的截图
                integrator.initiateScan();
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
}