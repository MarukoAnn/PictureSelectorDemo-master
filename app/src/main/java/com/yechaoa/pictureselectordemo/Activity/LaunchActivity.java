package com.yechaoa.pictureselectordemo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.yechaoa.pictureselectordemo.Modle.SidSelectData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;
import com.yechaoa.pictureselectordemo.Util.UpdateHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moonshine on 2018/2/5.
 */

public class LaunchActivity extends Activity {
    String Msid = null;
    String updateApkUrl;
    boolean start = false;
    private final int mRequestCode = 100;//权限请求码
    @SuppressLint("InlinedApi")
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,

    };
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);
        UltimateBar.newColorBuilder()
                .statusColor(Color.parseColor("#e5e1d5"))       // 状态栏颜色
                .statusDepth(50)                // 状态栏颜色深度
                .build(this)
                .apply();
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[] {permissions[i]}, 1);
                    mPermissionList.add(permissions[i]);
                }
            }
        }
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(2000);//休眠3秒
                        updatehttp();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /**
                     * 要执行的操作
                     */
                }
            }.start();
            Log.i("tag", "123");
        }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatehttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                try {
                    try {
                        UpdateHttp updateHttp = new UpdateHttp();
                        updateApkUrl = updateHttp.updatePostHttp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Msid != null) {
                                if (updateApkUrl == null) {

                                    Intent intent = new Intent();
                                    intent.putExtra("url", "null");
                                    //跳转至 MainActivity
                                    intent.setClass(LaunchActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束当前的 Activity
                                    LaunchActivity.this.finish();
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("url", updateApkUrl);
                                    //跳转至 MainActivity
                                    intent.setClass(LaunchActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束当前的 Activity
                                    LaunchActivity.this.finish();
                                }
                            } else {
                                if (updateApkUrl == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("url", "null");
                                    //跳转至 MainActivity
                                    intent.setClass(LaunchActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    //结束当前的 Activity
                                    LaunchActivity.this.finish();
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("url", updateApkUrl);
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
                            intent.setClass(LaunchActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            } else {
                //全部权限通过，可以进行下一步操作。。。
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(2000);//休眠3秒
                            updatehttp();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /**
                         * 要执行的操作
                         */
                    }
                }.start();
                Log.i("tag", "123");
            }
        }

    }

    AlertDialog mPermissionDialog;
    String      mPackName = "com.yechaoa.pictureselectordemo";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                            start = true;
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        Thread.sleep(2000);//休眠3秒
                                        updatehttp();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    /**
                                     * 要执行的操作
                                     */
                                }
                            }.start();
                            cancelPermissionDialog();

                        }
                    })
                    .setCancelable(false)
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (start == false){

        }else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(2000);//休眠3秒
                        updatehttp();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /**
                     * 要执行的操作
                     */
                }
            }.start();
        }
    }
}
