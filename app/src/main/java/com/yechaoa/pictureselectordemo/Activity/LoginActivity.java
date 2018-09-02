package com.yechaoa.pictureselectordemo.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.yechaoa.pictureselectordemo.Modle.LoginData;
import com.yechaoa.pictureselectordemo.Modle.ReturnStatusData;
import com.yechaoa.pictureselectordemo.Modle.UserpassData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;
import com.yechaoa.pictureselectordemo.Util.DownloadUtil;
import com.yechaoa.pictureselectordemo.Util.UpdateDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * Created by moonshine on 2018/1/26.
 */


public class LoginActivity extends Activity {

    private DownloadUtil downloadUtils;

    String url;
    EditText username;
    EditText password;
    Button login_btn;
    String path = "http://123.249.28.108:8081/element-admin/user/login";
    String Loginurl = "http://123.249.28.108:8081/element-admin/user/logout";
    DataDBHepler dbHepler;
    String result;
    String Username;
    String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //从GPS获取最近信息
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        ViewLayout();
        init();
        dbHepler = new DataDBHepler(getBaseContext());
        try {
            if (dbHepler.isIdoruserpass()) {
                ArrayList<UserpassData> DataList1 = dbHepler.FinduserpassData();
                final UserpassData data1 = new UserpassData(DataList1.get(0).getId(), DataList1.get(0).getUser(), DataList1.get(0).getPassword());
                Username = data1.getUser();
                Password = data1.getPassword();

            }
            else {
                Log.i(TAG,"没有用户名");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        username.setCursorVisible(false);
        password.setCursorVisible(false);
        username.setText(Username);
        password.setText(Password);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setCursorVisible(true);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.setCursorVisible(true);
            }
        });

        login_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()){
                        Toast.makeText(getApplicationContext(),"网络未连接", Toast.LENGTH_SHORT).show();
                }
               else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            if(username.getText().toString().equals("")||password.getText().toString().equals(""))
                            {
                                Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                            }
                            result = GetPostLogin(username.getText().toString(), password.getText().toString(), path);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"网络无连接", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            if (result.equals("10")) {
//                                String postsid = selectSid();
//                                if(postSidhttp(postsid)!=null) {

                                    if (dbHepler.isIdoruserpass())
                                    {
                                        dbHepler.updateUserpass(username.getText().toString(),password.getText().toString());
                                    }else {
                                        dbHepler.addUserpass("1",username.getText().toString(),password.getText().toString());
                                    }
//                                    if (dbHepler.isIdorUser()) {
//                                        dbHepler.updateUser(username.getText().toString(), realName, homeTelephone, birthday, phone);
//                                    } else {
//                                        dbHepler.addUser("1", username.getText().toString(), realName, homeTelephone, birthday, phone);
//                                    }
//                                }
                                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();//提示用户登录成功
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (result.equals("12")) {
                                Toast.makeText(getApplicationContext(), "登录失败，服务器故障", Toast.LENGTH_SHORT).show();//提示用户登录失败
                            } else if (result.equals("14")) {
                                Toast.makeText(getApplicationContext(), "登录失败，用户已存在不能重复登录", Toast.LENGTH_SHORT).show();//提示用户登录失败
                            } else {
                                Toast.makeText(getApplicationContext(), "登录失败，用户名或密码错误", Toast.LENGTH_SHORT).show();//提示用户登录失败
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                }).start();
               }
            }
        });
    }
    //控件初始化
    public void ViewLayout() {
        login_btn = (Button) findViewById(R.id.login_btn);
        username = (EditText) findViewById(R.id.login_user);
        password = (EditText) findViewById(R.id.login_pass);
    }

    public void init() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        try{
            if (url.equals("null")) {
                Log.i(TAG, "已经为最新版本");

            } else {
                Log.i(TAG, "该下载链接为:" + url);
                showDialog();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void showDialog() {
        downloadUtils =   new DownloadUtil(LoginActivity.this);
        final UpdateDialog Dialog = new UpdateDialog(LoginActivity.this);
        Dialog.setTitle("消息提示");
        Dialog.setMessage("发现新版本，是否更新?");
        Dialog.setYesOnclickListener("确定", new UpdateDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                downloadUtils.downloadAPK(url, "apk");
                Toast.makeText(LoginActivity.this,"应用正在后台下载",Toast.LENGTH_LONG).show();
                Dialog.dismiss();
            }
        });
        Dialog.setNoOnclickListener("取消", new UpdateDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                Dialog.dismiss();
            }
        });
        Dialog.show();
    }

    public String GetPostLogin(String uname, String upsd, String path) {
        //okhttp Post请求传输Json数据
        OkHttpClient client = new OkHttpClient();
        LoginData Ldata = new LoginData();
        String postStatus = null;

        Ldata.setUname(uname);
        Ldata.setUpwd(upsd);
        Ldata.setModule("AN6");
        Gson gson = new Gson();
        String json = gson.toJson(Ldata);//将其转换为JSON数据格式

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(mediaType, json);//放进requestBoday中
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            ReturnStatusData resultStatusData= gson.fromJson(result,ReturnStatusData.class);
            String postSid = resultStatusData.getSid();

            Log.i(TAG,"SID为"+postSid);
            if (dbHepler.isIdorSid()){
                dbHepler.update(postSid);
            }
            else {
                dbHepler.add("1",postSid);
            }
            postStatus= resultStatusData.getStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postStatus;
    }


    /**
     * 获取数据到数据库
     * @param Sid
     * @return
     */
//    public String postSidhttp(String Sid) {
//        String  SidStatus = null;
//        String url = "http://120.78.137.182/element-admin/user/query-self";
//        OkHttpClient client = new OkHttpClient();
//        Gson gson = new Gson();
//
//        ResultData mdata = new ResultData();
//
//        mdata.setSid(Sid);
//
//        String json = gson.toJson(mdata);//将其转换为JSON数据格式
//
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//
//        RequestBody requestBody = RequestBody.create(mediaType, json);//放进requestBoday中
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            String result = response.body().string();
//
//            Log.i(TAG,"访问网络"+result);
//
//            RSpostData rSpostData= gson.fromJson(result, RSpostData.class);
//            Log.i(TAG,"data数据为："+rSpostData.getData());
//
//            ReturnPostData returnPostData = rSpostData.getData();
//
//            realName = returnPostData.getRealName();
//            homeTelephone = returnPostData.getHomeTelephone();
//            phone = returnPostData.getPhone();
//            birthday = returnPostData.getBirthday();
//            SidStatus = rSpostData.getStatus();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return SidStatus;
////    }
//
//    public String  selectSid(){
//        DataDBHepler dataDBHepler = new DataDBHepler(getBaseContext());
//        ArrayList<SidSelectData> DataList = dataDBHepler.FindSidData();
//        final SidSelectData data = new SidSelectData(DataList.get(0).getId(),DataList.get(0).getSid());
//        Log.i(TAG,"数据库的sid为："+data.getSid());
//        final String Msid = data.getSid();
//        return Msid;
//    }
}
