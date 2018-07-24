package com.yechaoa.pictureselectordemo.Fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yechaoa.pictureselectordemo.Activity.PhotoActivity;
import com.yechaoa.pictureselectordemo.Activity.ScanActivity;


import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.PostlistData;
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

    private String path = "http://120.78.137.182/element-admin/item-info/find";
//    private String path ="http://192.168.28.74:8080/EquipmentInspection/item-info/find";
    private View view;
    private static final String TAG = "MainActivity";
    private Handler mHandler;
    private ReceiveBroadCast receiveBroadCast;
    String result;
    DataDBHepler dbHepler;
//   String oid="";

    /**
     * 启用onattach来获取activity调用的 onActivityresult 传递过来的参数
     * @param**/
    @Override
    public void onAttach(Activity activity) {
       /** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gasFragment");    //只有持有相同的action的接受者才能接收此广播
        activity.registerReceiver(receiveBroadCast, filter);
        super.onAttach(activity);
        activity.unregisterReceiver(receiveBroadCast);//LS:重点！
    }



    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {

            result = intent.getExtras().getString("result");
            Log.i("log", "在discoverFragment中获取的扫描值:" + result);

//            Intent intent1 = new Intent();
//            intent1.setClass(getActivity(), PhotoActivity.class);
//            startActivity(intent1);

            new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                SpostHttpMap spostHttpMap = new SpostHttpMap();
                try {
                    String result1 =   spostHttpMap.posthttpmap(path,result);
                    if(result1.equals("10"))
                    {
                        ArrayList<ListData> DataList = dbHepler.FindItemData();
                        final ListData data = new ListData(DataList.get(0).getId(),DataList.get(0).getItemcode(),DataList.get(0).getItemname(),DataList.get(0).getItemdetail(),DataList.get(0).getUnitcode(),DataList.get(0).getItemmembers(),DataList.get(0).getLongitude(),DataList.get(0).getLatitude());
                        if (data.getItemdetail()==null)
                        {
                            Toast.makeText(getContext(),"请重新扫描",Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent1 = new Intent();
                            intent1.setClass(getActivity(), PhotoActivity.class);
                            startActivity(intent1);
                        }
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
                Log.i(TAG,"名字为："+listData.getItemname());
                Log.i(TAG,"设备："+listData.getItemdetail());
                if (dbHepler.isItemorData())
                {
                    dbHepler.updateItemdata("1",listData.getItemcode(),listData.getItemname(),listData.getItemdetail(),listData.getUnitcode(),listData.getItemmembers(),listData.getLongitude(),listData.getLatitude());
                }else {
                    dbHepler.addItemData("1",listData.getItemcode(),listData.getItemname(),listData.getItemdetail(),listData.getUnitcode(),listData.getItemmembers(),listData.getLongitude(),listData.getLatitude());
                }
            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }
    }


//    private void initView() {
//        listView = view.findViewById(R.id.list_view);
//        new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
//            @Override
//            public void run() {
//                new AnotherTask().execute("");
//            }
//        }).start();
//        postAdapter = new PostAdapter(getContext(), postlist);
//        listView.setAdapter(postAdapter);
//    }

//    public void init(){
//        postlist.clear();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new AnotherTask().execute("");
//            }
//        }).start();
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private class AnotherTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected void onPostExecute(String result) {
//            //对UI组件的更新操作
//            Gson gson = new Gson();
//            PostlistData postlistData = gson.fromJson(result,PostlistData.class);
//            try {
//                List<ListData> listData = postlistData.getValues();
//                if (listData == null) {
//                    Toast.makeText(getActivity(), "数据为空", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    for (int i = 0; i < listData.size(); i++) {
//                        String id = listData.get(i).getOid();
//                        String num = listData.get(i).getAluminumcode();
//                        String time = listData.get(i).getIdt().substring(0, 10);
//                        ListData p = new ListData(id, num, time);
//                        postlist.add(p);
//                    }
//                }
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//                Toast.makeText(getContext(),"网络无连接", Toast.LENGTH_SHORT).show();
//            }
//            try{
//                postAdapter.notifyDataSetChanged();
//            }catch (Exception e){
//                Log.e(TAG, "postlisthttp: ",e );
//            }
//        }
//        @Override
//        protected String doInBackground(String... params) {
//            //耗时的操作
//            String url = "http://120.78.137.182/element-plc/find-produce-information";
//
//            OkHttpClient client = new OkHttpClient();
//            Gson gson = new Gson();
//            String json = "";
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            RequestBody requestBody = FormBody.create(JSON,json);//放进requestBoday中
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//            String result ="";
//            try {
//                Response response = client.newCall(request).execute();
//                //获取后台传输的额status状态码
//                result = response.body().string();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "doInBackground: ",e );
//            }
//            return result;
//        }
//    }
//
}