package com.cqapp.gaodemap;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
/*
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
/*/
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements LocationSource, AMapLocationListener, AMap.OnMapTouchListener, AMap.OnMapClickListener {
    private AMap mAmap;//地图控制器
    private MapView mMapView;//地图视图
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;//地址选择器
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private boolean followMove = true;
    private Circle mCircle200 = null;
    private Circle mCircle100 = null;
    private Circle mCircle50 = null;
    //当前位置
    LatLng mNowLoc;

    //颜色
    private int mFillcolor =Color.argb(40, 105,192,255);
    private int mStrokecolor = Color.argb(70, 105,192,255);

    //视图大小
    int mZoom = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        initMap();
        //显示指南针
        mUiSettings.setCompassEnabled(true);
        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);


    }
    /**
     * 初始化AMap对象
     */
    private void initMap(){
        if (mAmap == null) {
            mAmap = mMapView.getMap();
            setUpMap();
        }
        //设置地图的放缩级别
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(mZoom));
        //实例化UiSettings类对象
        mUiSettings = mAmap.getUiSettings();


    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));// 设置小蓝点的图标

        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(0,1.0f);手动设置便偏移
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        //myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);

        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setOnMapClickListener(this);

        mAmap.setLocationSource(this);// 设置定位资源。如果不设置此定位资源则定位按钮不可点击。并且实现activate激活定位,停止定位的回调方法
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }
    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            if (aLocation != null
                    &&aLocation.getErrorCode() == 0) {
                //System.out.println(aLocation);
                Log.d("经纬度位置", "维度"+aLocation.getLatitude()+ "经度"+ aLocation.getLongitude()+"位置描述"+aLocation.getAddress());;
                mAmap.clear();
                mNowLoc =  new LatLng(aLocation.getLatitude(),aLocation.getLongitude());
                LatLng latLng = new LatLng(aLocation.getLatitude()+0.0005,aLocation.getLongitude()+0.0005);
                final Marker marker = mAmap.addMarker(new MarkerOptions().position(latLng).title("奶茶店").snippet("据您"+ AMapUtils.calculateLineDistance(latLng,mNowLoc)+"米"));
                drawCircle(new LatLng(aLocation.getLatitude(),aLocation.getLongitude()));
                mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aLocation.getErrorCode()+ ": " + aLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 绘制圆圈
     *
     * @param latLng
     */
    public void drawCircle(LatLng latLng) {
        String color = "#26b637";
        StringBuilder sb = new StringBuilder(color);// 构造一个StringBuilder对象
        sb.insert(1, "50");// 在指定的位置10，插入指定的字符串

        ///if(mCircle200 == null)
        {
            mCircle200 = mAmap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(200)
                    .fillColor(mFillcolor)
                    .strokeColor(mStrokecolor)
                    .strokeWidth(5));
        }
        ///if(mCircle100 == null)
        {
            mCircle100 = mAmap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(100)
                    .fillColor(mFillcolor)
                    .strokeColor(mStrokecolor)
                    .strokeWidth(5));
        }
        ///if(mCircle50 == null)
        {
            mCircle50 = mAmap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(50)
                    .fillColor(mFillcolor)
                    .strokeColor(mStrokecolor)
                    .strokeWidth(5));
        }

    }



    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //System.out.println(latLng);
        mAmap.addMarker(new MarkerOptions().position(latLng).title("奶茶店").snippet("据您"+ AMapUtils.calculateLineDistance(latLng,mNowLoc)+"米"));
    }


//    public LatLng getMapCenterPoint() {
//        int left = mMapView.getLeft();
//        int top = mMapView.getTop();
//        int right = mMapView.getRight();
//        int bottom = mMapView.getBottom();
//        // 获得屏幕点击的位置
//        int x = (int) (mMapView.getX() + (right - left) / 2);
//        int y = (int) (mMapView.getY() + (bottom - top) / 2);
//        Projection projection = AMap.getProjection();
//        LatLng pt = projection.fromScreenLocation(new Point(x, y));
//
//        return pt;
//    }

    /**
     * 对地图进行截屏
     mAmap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int status) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    if(null == bitmap){
    return;
    }
    try {
    FileOutputStream fos = new FileOutputStream(
    Environment.getExternalStorageDirectory() + "/test_"
    + sdf.format(new Date()) + ".png");
    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    try {
    fos.flush();
    } catch (IOException e) {
    e.printStackTrace();
    }
    try {
    fos.close();
    } catch (IOException e) {
    e.printStackTrace();
    }
    StringBuffer buffer = new StringBuffer();
    if (b)
    buffer.append("截屏成功 ");
    else {
    buffer.append("截屏失败 ");
    }
    if (status != 0)
    buffer.append("地图渲染完成，截屏无网格");
    else {
    buffer.append( "地图未渲染完成，截屏有网格");
    }
    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();

    } catch (FileNotFoundException e) {
    e.printStackTrace();
    }

    }
    });
     */
//    private void Scalecircle1(final Circle ac) {
//        ValueAnimator vm = ValueAnimator.ofFloat(0,(float)ac.getRadius());
//        vm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float curent = (float) animation.getAnimatedValue();
//                ac.setRadius(curent);
//                //mAmap.invalidate();
//            }
//        });
//        ValueAnimator vm1 = ValueAnimator.ofInt(160,0);
//        vm1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int color = (int) animation.getAnimatedValue();
//                ac.setFillColor(Color.argb(color, 98, 198, 255));
//               // mAmap.invalidate();
//            }
//        });
//        vm.setRepeatCount(Integer.MAX_VALUE);
//        vm.setRepeatMode(ValueAnimator.RESTART);
//        vm1.setRepeatCount(Integer.MAX_VALUE);
//        vm1.setRepeatMode(ValueAnimator.RESTART);
//        AnimatorSet set = new AnimatorSet();
//        set.play(vm).with(vm1);
//        set.setDuration(2500);
//        set.setInterpolator(interpolator1);
//        set.start();
//    }
//    private final Interpolator interpolator1 = new LinearInterpolator();
//    Runnable rb = new Runnable() {
//        @Override
//        public void run() {
//            Scalecircle1(mCircle200);
//        }
//    };
//    Handler handle =new Handler();
//
//    Runnable rb1 = new Runnable() {
//        @Override
//        public void run() {
//            Scalecircle1(mCircle50);
//        }
//    };
//    Handler handle1 =new Handler();
//
//    Runnable rb2 = new Runnable() {
//        @Override
//        public void run() {
//            Scalecircle1(mCircle100);
//        }
//    };
//    Handler handle2 =new Handler();


}
