package com.yechaoa.pictureselectordemo.Util;

import android.content.Context;
import android.util.Log;

import com.yechaoa.pictureselectordemo.Activity.MainActivity;
import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.PostlistData;
import com.yechaoa.pictureselectordemo.Modle.ReturnStatusData;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.luck.picture.lib.tools.DebugUtil.TAG;

/**
 * Created by moonshine on 2018/4/11.
 */

//public class SpostHttpMapSubmit {
//    public String posthttpmap(String path,String itemncode) {
//        //okhttp Post请求传输Json数据
//        String SpostStatus = null;
//        //将接收到的JSON数据放到实体类里
//        Gson gson =new Gson();
//        OkHttpClient client = new OkHttpClient();
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        FormBody body = new FormBody.Builder()
//                .add("itemcode",itemncode)
//                .build();
//        Request request = new Request.Builder()
//                .url(path)
//                .post(body)
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//
//            //获取后台传输的额status状态码
//            String result = response.body().string();
//           PostlistData  postlistData = gson.fromJson(result,PostlistData.class);
//            //定义一个参数来获取状态码
//            SpostStatus = postlistData.getStatus();
//
//            ListData listData = postlistData.getValues();
//            listData.getItemname();
//            Log.i(TAG,"名字为："+listData.getItemname());
//
//
//        }catch (Exception e){
//            e.printStackTrace() ;
//            System.out.println("异常"+e);
//        }
//        return SpostStatus;
//    }
//}
