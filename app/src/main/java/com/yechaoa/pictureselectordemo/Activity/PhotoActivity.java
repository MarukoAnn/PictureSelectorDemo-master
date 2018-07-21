package com.yechaoa.pictureselectordemo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;
import com.yechaoa.pictureselectordemo.Modle.GridImageAdapter;
import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.PostlistData;
import com.yechaoa.pictureselectordemo.Modle.SelectData;
import com.yechaoa.pictureselectordemo.Modle.SelectDatadb;
import com.yechaoa.pictureselectordemo.R;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.Util.FullyGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.luck.picture.lib.permissions.RxPermissions.TAG;

public class PhotoActivity extends Activity {
    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private RecyclerView mRecyclerView;
    private PopupWindow pop;
    GridView gridView;
    ListView listView ;
    Button button ;
    EditText describe;
    List<String> Path = new ArrayList<>();
    DataDBHepler dbHepler;
    List<String> listitem= new ArrayList<>();//设备单选框列表
    List<String> listcheck= new ArrayList<>();//多选框
    List<String>valueslist = new ArrayList<>();//装载点击设备的状态
    List<String>namelist = new ArrayList<>();//装载参与巡检的人员
    String[] valuse = new String[]{};//装载点击设备的状态
    String[] name=new String[]{};//装载参与巡检的人员
    List<File> file = new ArrayList<>();
    double longitude;
    double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);
        initView();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        dbHepler = new DataDBHepler(getBaseContext());

        if (dbHepler.isItemorData()) {
            ArrayList<ListData> DataList = dbHepler.FindItemData();
            final ListData data = new ListData(DataList.get(0).getId(),DataList.get(0).getItemcode(),DataList.get(0).getItemname(),DataList.get(0).getItemdetail(),DataList.get(0).getUnitcode(),DataList.get(0).getItemmembers(),DataList.get(0).getLongitude(),DataList.get(0).getLatitude());
            String itemdetail = data.getItemdetail();
            String itemmembers = data.getItemmembers();
            Log.i(TAG,"itemmembers"+itemmembers);
            Log.i(TAG,"设备"+data.getItemdetail());
            String[] itemlist= itemdetail.split("，");
            for (int i=0;i<itemlist.length;i++)
            {
                listitem.add(itemlist[i]);
            }
            String[] checklist=itemmembers.split("，");
            for(int j = 0;j<checklist.length;j++){
                listcheck.add(checklist[j]);
                Log.i(TAG,"巡检人员"+checklist[j]);
            }
            Log.i(TAG,"列表设备"+listitem);
            Log.i(TAG,"巡检人员"+listcheck);
            PostAdapter postAdapter =new PostAdapter(getBaseContext(),listitem);
            listView.setAdapter(postAdapter);
            GridAdapter gridAdapter = new GridAdapter(getBaseContext(),listcheck);
            gridView.setAdapter(gridAdapter);
        }
        initWidget();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Sysout();

            }
        });
    }

    public void initView(){
         button = (Button) findViewById(R.id.Submit);
         describe = (EditText)findViewById(R.id.describe_text);
         listView = (ListView)findViewById(R.id.list_item);
         gridView = (GridView)findViewById(R.id.grid_check);
    }

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.notifyDataSetChanged();
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PhotoActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PhotoActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PhotoActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {

            //第一种方式，弹出选择和拍照的dialog
            showPop();

            //第二种方式，直接进入相册，但是 是有拍照得按钮的
            //参数很多，根据需要添加

//            PictureSelector.create(MainActivity.this)
//                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
//                    .minSelectNum(1)// 最小选择数量
//                    .imageSpanCount(4)// 每行显示个数
//                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
//                    .previewImage(true)// 是否可预览图片
//                    .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
//                    .isCamera(true)// 是否显示拍照按钮
//                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
//                    .enableCrop(true)// 是否裁剪
//                    .compress(true)// 是否压缩
//                    .compressMode(LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                    .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    //.selectionMedia(selectList)// 是否传入已选图片
//                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
//                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
//                    //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
//                    //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
//                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
//                    .rotateEnabled(false) // 裁剪是否可旋转图片
//                    //.scaleEnabled()// 裁剪是否可放大缩小图片
//                    //.recordVideoSecond()//录制视频秒数 默认60s
//                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };

    private void showPop() {
        View bottomView = View.inflate(PhotoActivity.this, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = (TextView) bottomView.findViewById(R.id.tv_album);
        TextView mCamera = (TextView) bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = (TextView) bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        PictureSelector.create(PhotoActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(maxSelectNum)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_camera:
                        //拍照
                        PictureSelector.create(PhotoActivity.this)
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调

                    images = PictureSelector.obtainMultipleResult(data);
                    selectList.addAll(images);
//                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
//                    Path.clear();
//                    List<LocalMedia>listpath=adapter.returnurl();
//                    for (int i=0;i<listpath.size();i++){
//                        LocalMedia media = listpath.get(i);
//                        Path.add(i, media.getPath());
//                    }
//
//                    for (int j=0;j<Path.size();j++) {
//                        file.add(new File(Path.get(j)));
//                    }
//                    Log.i(TAG,"文件个数为："+file.size());
                    Log.i(String.valueOf(PhotoActivity.this),"图片地址为："+Path);
                    break;
            }
        }
    }

    public class PostAdapter extends BaseAdapter {

        private ArrayList<String> List;
        private Context context;

        PostAdapter(Context context, List<String> list) {
            this.context = context;
            this.List = (ArrayList<String>) list;
        }

        @Override
        public int getCount() {
            return List.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = view.inflate(context, R.layout.list_item, null);
            }
            final TextView tv = (TextView) view.findViewById(R.id.equipment);
            tv.setText(List.get(position));
            RadioGroup radioGroup =(RadioGroup)view.findViewById(R.id.Radiogroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String tip = checkedId ==R.id.checkBox?"正常":"异常";
//                    valuse[position]=tip;

                    valueslist.add(List.get(position));
                    valueslist.add(tip);
//                    valueslist.add("\""+List.get(position)+"\""+":"+"\""+tip+"\"");

//                    System.out.println(List.get(position)+":"+tip);

//                    List<String> dsdd=new ArrayList<>();
//                    dsdd.add("\""+List.get(position)+"\""+":"+"\""+tip+"\"");
//                    valueslist.add(dsdd);
//                    System.out.println(dsdd.toString().replace("[","").replace("]",""));
//                    List<Object> dsd = new ArrayList<>();
//
//                    dsd.add(dsdd);
//
//                    System.out.println(dsd);

                    Log.i(TAG,"设备状态"+tip);

                }
            });


            return view;
        }
    }


    public class GridAdapter extends BaseAdapter {

        private ArrayList<String> List;
        private Context context;

        GridAdapter(Context context, List<String> list) {
            this.context = context;
            this.List = (ArrayList<String>) list;
        }

        @Override
        public int getCount() {
            return List.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = view.inflate(context, R.layout.checkbox_item, null);
            }
           final CheckBox tv = (CheckBox) view.findViewById(R.id.check);
            tv.setText(List.get(position));
             tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if (isChecked)
                     {
//                         name[position]=buttonView.getText().toString();

//                         if (namelist.size()>1) {
//                             if (namelist.get(position).equals(buttonView.getText().toString())) {
//                                 Log.i(TAG,"取消选中"+buttonView.getText().toString());
//
//                                 namelist.remove(position);
//                                 namelist.notify();
//
//                             } else {
//                                 namelist.add(buttonView.getText().toString());
//                             }
//                         }else {
                             Log.i(TAG,"选中："+buttonView.getText().toString());
                             namelist.add(buttonView.getText().toString());
//                         }
                     }
                 }
             });
            return view;
        }
    }
    public void Sysout(){

        Log.i(TAG,"设备状态"+valueslist);
        Log.i(TAG,"数据："+valueslist.get(0));
        file.clear();
        Path.clear();
        List<LocalMedia>listpath=adapter.returnurl();
        for (int i=0;i<listpath.size();i++){
            LocalMedia media = listpath.get(i);
            Path.add(i, media.getPath());
        }

        for (int j=0;j<Path.size();j++) {
            file.add(new File(Path.get(j)));
        }

        Log.i(TAG,"文件个数为："+file.size());
        SelData();


    }
    public void SelData() {
        final String[] result = new String[1];
        GspData();
        ArrayList<ListData> Datalist = dbHepler.FindItemData();
        final ListData data = new ListData(Datalist.get(0).getId(), Datalist.get(0).getItemcode(), Datalist.get(0).getItemname(), Datalist.get(0).getItemdetail(), Datalist.get(0).getUnitcode(), Datalist.get(0).getItemmembers(), Datalist.get(0).getLongitude(), Datalist.get(0).getLatitude());
        String itemcode = data.getItemcode();
        String itemname = data.getItemname();
        String unitcode = data.getUnitcode();
        String longitudeIP = data.getLongitude();
        String latitudeIP = data.getLatitude();
        final SelectData selectData = new SelectData();
        SelectDatadb selectDatadb = new SelectDatadb();
        if (describe.getText().toString().equals(""))
        {
            selectDatadb.setDescription("正常");
        }else {
            selectDatadb.setDescription(describe.getText().toString());
        }
        selectDatadb.setItemcode(itemcode);
        selectDatadb.setItemname(itemname);
        selectDatadb.setLatitudeIP(latitudeIP);
        selectDatadb.setLongitudeIP(longitudeIP);
        selectDatadb.setNormal(valueslist);
        selectDatadb.setInspector(namelist);
        selectDatadb.setLongitude(longitude);
        selectDatadb.setLatitude(latitude);
        Gson gson = new Gson();
        final String json = gson.toJson(selectDatadb);
        Log.i(TAG, "shuju :" + json);
        selectData.setInspectiondata(json);
        final File[] files = file.toArray(new File[file.size()]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    SpostHttpMapSubmit spostHttpMapSubmit = new SpostHttpMapSubmit();
                    if (file.size()==0)
                    {
                        result[0] = spostHttpMapSubmit.posthttpmap(selectData.getInspectiondata());
                    }else if (file.size()==1){
                        result[0] = spostHttpMapSubmit.posthttpmap1(selectData.getInspectiondata(),files);
                    }else if (file.size()==2) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==3) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==4) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==5) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==6) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==7) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==8) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else if (file.size()==9) {
                        result[0] = spostHttpMapSubmit.posthttpmap2(selectData.getInspectiondata(), files);
                    }else
                    {
                        Toast.makeText(PhotoActivity.this,"图片超过数量",Toast.LENGTH_LONG).show();
                    }
//                    String result = spostHttpMapSubmit.posthttpmap(selectData.getInspectiondata(), files);
                    if (result[0].equals("10")) {
                        Toast.makeText(PhotoActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(PhotoActivity.this,SuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(PhotoActivity.this, "失败", Toast.LENGTH_LONG).show();
                    }
                    Looper.loop();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void GspData() {
        final LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //从GPS获取最近信息
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //使用location 来更新EditText的显示
        updateView(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //当GPS 定位信息发生改变时，更新位置
                updateView(location);
            }

            @Override
            public void onStatusChanged(String provider, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return;
                }
                updateView(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

                updateView(null);
            }

        });
    }
     public void updateView(Location newLocation){
            if (newLocation!=null){
                StringBuffer sb = new StringBuffer();
                sb.append("实时的位置信息：\n");
                sb.append("\n经度");
                sb.append(newLocation.getLongitude());
                sb.append("\n纬度：");
                sb.append(newLocation.getLatitude());
                Log.i(TAG,"经度:"+newLocation.getLongitude());
                Log.i(TAG,"纬度:"+newLocation.getLatitude());
                longitude = newLocation.getLongitude();
                latitude  = newLocation.getLatitude();
            }
        }

    public class SpostHttpMapSubmit {
        String path = "http://120.78.137.182/element-admin/picture-upload";
        public String posthttpmap(String inspectiondata) {
            //okhttp Post请求传输Json数据

//            File[] files = file.listFiles();
//            File[] files = file.listFiles();


//            String path = "http://120.78.137.182/element-admin/picture-upload";
//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("inspectiondata",inspectiondata)
//                        .addFormDataPart("file", "head_image", fileBody)
                        .addFormDataPart("imagetype", imageType)
                        .build();
                Request request = new Request.Builder()
                        .url(path)
                        .post(requestBody)
                        .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap1(String inspectiondata,File[] file) {
            //okhttp Post请求传输Json数据

//            File[] files = file.listFiles();
//            File[] files = file.listFiles();


            String path = "http://120.78.137.182/element-admin/picture-upload";
//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap2(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

//            File[] files = file.listFiles();
//            File[] files = file.listFiles();



//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image", fileBody1)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap3(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

//            File[] files = file.listFiles();
//            File[] files = file.listFiles();



//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap4(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

//            File[] files = file.listFiles();
//            File[] files = file.listFiles();



//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap5(String inspectiondata, File[] file) {


//            String path = "http://120.78.137.182/element-admin/picture-upload";
            String SpostStatus = null;
            String imageType = "multipart/form-data";
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image3", fileBody4)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap6(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);
            RequestBody fileBody5 = RequestBody.create(MediaType.parse("image/*"), file[5]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image4", fileBody4)
                    .addFormDataPart("file", "head_image5", fileBody5)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }
        public String posthttpmap7(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);
            RequestBody fileBody5 = RequestBody.create(MediaType.parse("image/*"), file[5]);
            RequestBody fileBody6 = RequestBody.create(MediaType.parse("image/*"), file[6]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image4", fileBody4)
                    .addFormDataPart("file", "head_image5", fileBody5)
                    .addFormDataPart("file", "head_image5", fileBody6)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }


        public String posthttpmap8(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);
            RequestBody fileBody5 = RequestBody.create(MediaType.parse("image/*"), file[5]);
            RequestBody fileBody6 = RequestBody.create(MediaType.parse("image/*"), file[6]);
            RequestBody fileBody7 = RequestBody.create(MediaType.parse("image/*"), file[7]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image4", fileBody4)
                    .addFormDataPart("file", "head_image5", fileBody5)
                    .addFormDataPart("file", "head_image5", fileBody6)
                    .addFormDataPart("file", "head_image5", fileBody7)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap9(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);
            RequestBody fileBody5 = RequestBody.create(MediaType.parse("image/*"), file[5]);
            RequestBody fileBody6 = RequestBody.create(MediaType.parse("image/*"), file[6]);
            RequestBody fileBody7 = RequestBody.create(MediaType.parse("image/*"), file[7]);
            RequestBody fileBody8 = RequestBody.create(MediaType.parse("image/*"), file[8]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image4", fileBody4)
                    .addFormDataPart("file", "head_image5", fileBody5)
                    .addFormDataPart("file", "head_image5", fileBody6)
                    .addFormDataPart("file", "head_image5", fileBody7)
                    .addFormDataPart("file", "head_image5", fileBody8)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }

        public String posthttpmap10(String inspectiondata, File[] file) {
            //okhttp Post请求传输Json数据

            String SpostStatus = null;
            String imageType = "multipart/form-data";
//            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //将接收到的JSON数据放到实体类里
            Gson gson =new Gson();
            OkHttpClient client = new OkHttpClient();
//            for(int i=0;i<file.length;i++)
//            {
//                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[i]);
//            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file[0]);
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/*"), file[1]);
            RequestBody fileBody2 = RequestBody.create(MediaType.parse("image/*"), file[2]);
            RequestBody fileBody3 = RequestBody.create(MediaType.parse("image/*"), file[3]);
            RequestBody fileBody4 = RequestBody.create(MediaType.parse("image/*"), file[4]);
            RequestBody fileBody5 = RequestBody.create(MediaType.parse("image/*"), file[5]);
            RequestBody fileBody6 = RequestBody.create(MediaType.parse("image/*"), file[6]);
            RequestBody fileBody7 = RequestBody.create(MediaType.parse("image/*"), file[7]);
            RequestBody fileBody8 = RequestBody.create(MediaType.parse("image/*"), file[8]);
            RequestBody fileBody9 = RequestBody.create(MediaType.parse("image/*"), file[9]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("inspectiondata",inspectiondata)
                    .addFormDataPart("file", "head_image", fileBody)
                    .addFormDataPart("file", "head_image1", fileBody1)
                    .addFormDataPart("file", "head_image2", fileBody2)
                    .addFormDataPart("file", "head_image3", fileBody3)
                    .addFormDataPart("file", "head_image4", fileBody4)
                    .addFormDataPart("file", "head_image5", fileBody5)
                    .addFormDataPart("file", "head_image5", fileBody6)
                    .addFormDataPart("file", "head_image5", fileBody7)
                    .addFormDataPart("file", "head_image5", fileBody8)
                    .addFormDataPart("file", "head_image5", fileBody9)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
//            }

            try {
                Response response = client.newCall(request).execute();

                //获取后台传输的额status状态码
                String result = response.body().string();
                PostlistData postlistData = gson.fromJson(result,PostlistData.class);
                //定义一个参数来获取状态码
                SpostStatus = postlistData.getStatus();

            }catch (Exception e){
                e.printStackTrace() ;
                System.out.println("异常"+e);
            }
            return SpostStatus;
        }
    }

}
