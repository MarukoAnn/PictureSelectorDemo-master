package com.yechaoa.pictureselectordemo.Activity;


import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.RelativeLayout;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.yechaoa.pictureselectordemo.Fragment.FirstFragment;
import com.yechaoa.pictureselectordemo.Fragment.SecondFragment;

import com.yechaoa.pictureselectordemo.R;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 定义4个Fragment对象
    private FirstFragment fg1;
    private SecondFragment fg2;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private Handler mHandler;
    // 定义几个颜色
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int dark = 0xff000000;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;
    private String Creator;
    String index;
    //    final Handler handler = new Handler();
//    private int TIME = 370000;  //每隔1s执行一次.
//    String path = "http://120.78.137.182/element-admin/user/sid-update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        index = getIntent().getStringExtra("index");
        if (index!=null) {
            setChioceItem(Integer.parseInt(index));
        }
        else {
            setChioceItem(0); // 初始化页面加载时显示第一个选项卡
//            setUser();
        }

    }

//    public void showNoProject(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                .setMessage("身份验证已失效，请重新登录!")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.setClass(MainActivity.this,LaunchActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//
//                    }
//                });
//        builder.setCancelable(false);
//        builder.show();
//    }

    /**
     * 初始化页面
     */
    private void initView() {
// 初始化底部导航栏的控件
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        firstLayout.setOnClickListener(MainActivity.this);
        secondLayout.setOnClickListener(MainActivity.this);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                try {
//                    DataDBHepler dataDBHepler = new DataDBHepler(getBaseContext());
//                    ArrayList<SidSelectData> DataList = dataDBHepler.FindSidData();
//                    final SidSelectData data = new SidSelectData(DataList.get(0).getId(),DataList.get(0).getSid());
//                    String Msid = data.getSid();//获取数据库里的sid
//
//                    SpostupdateHttp spostupdateHttp = new SpostupdateHttp();
//                    String result = spostupdateHttp.posthttpresult(Msid,path);
//                    if (result.equals("13"))
//                    {
//                        showNoProject();
//                        dataDBHepler = new DataDBHepler(getBaseContext());
//                        dataDBHepler.delete("1");
//                    }
//                    else {
//                        Log.i(TAG,"用户在线");
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                Looper.loop();
//            }
//        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_layout:
                setChioceItem(0);
                break;
            case R.id.second_layout:
                setChioceItem(1);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                firstImage.setImageResource(R.drawable.ic_shouye1);
                firstLayout.setBackgroundColor(whirt);
// 如果fg1为空，则创建一个并添加到界面上
                if (fg1 == null) {
                    fg1 = new FirstFragment();
                    fragmentTransaction.add(R.id.content,fg1);
                } else {
// 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);
                }
                break;
            case 1:
                secondImage.setImageResource(R.drawable.ic_persion1);
                secondLayout.setBackgroundColor(whirt);
                if (fg2 == null) {
                    fg2 = new SecondFragment();
                    fragmentTransaction.add(R.id.content,fg2);
                } else {
                    fragmentTransaction.show(fg2);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        firstImage.setImageResource(R.drawable.ic_shouye);
        firstLayout.setBackgroundColor(whirt);
        secondImage.setImageResource(R.drawable.ic_person);
        secondLayout.setBackgroundColor(whirt);
    }

    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
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
        return super.onKeyDown(keyCode, event);
    }

    /**
     * MainActivity 里重写onActiovityResult的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
            } else {
                String result = intentResult.getContents();
//                DiscoverFragment fragment = new DiscoverFragment();
//                fragment.setResult(result);
                Intent intent = new Intent();
                intent.setAction("com.gasFragment"); // 设置你这个广播的action
                intent.putExtra("result",result);
                sendBroadcast(intent);
                Log.i("log", "进入onActivityResult" + result);
            }
        }

    }
}
