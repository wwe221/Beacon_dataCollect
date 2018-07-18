package com.something.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGPoint;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;
import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Math.*;

import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class Fragment1 extends Fragment {
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    View view;
    NMapView mMapView;        //MapView 객체(지도 생성, 지도데이터)
    NMapController mMapController;    //지도 상태 컨트롤 객체
    NMapViewerResourceProvider mMapViewerResourceProvider;    //지도 뷰어 리소스 곱급자 객체 생성
    NMapOverlayManager mOverlayManager;        //오버레이 관리 객체
    //OnStateChangeListener onPOIdataStateChangeListener;		//오버레이 아이템 변화 이벤트 콜백 인터페이스
    NMapMyLocationOverlay mMyLocationOverlay;    //지도 위에 현재 위치를 표시하는 오버레이 클래스
    NMapLocationManager mMapLocationManager;    //단말기의 현재 위치 탐색 기능 사용 클래스
    NMapCompassManager mMapCompassManager;        //단말기의 나침반 기능 사용 클래스
    OnMapViewTouchEventListener onMapViewTouchEventListener;
    OnMapStateChangeListener onMapViewStateChangeListener;
    TextView name;
    double a = 0, b = 0;
    private NMapContext mMapContext;
    private static final String CLIENT_ID = "CszUaxqSM37gzrSc6sdK";// 애플리케이션 클라이언트 아이디 값
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nmapfragment, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = null;
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView = (NMapView) getView().findViewById(R.id.nmapview);
        mMapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(mMapView);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true, null);
        mMapView.displayZoomControls(true);
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapController = mMapView.getMapController();
        // 줌 인/아웃 버튼 생성
        mMapView.setBuiltInZoomControls(true, null);
        mMapViewerResourceProvider = new NMapViewerResourceProvider(getContext());
        mOverlayManager = new NMapOverlayManager(getContext(), mMapView, mMapViewerResourceProvider);
        startMyLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapContext.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }

    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }
    private void startMyLocation() {//내 위치 찾아서 이동.
        //Toast.makeText(getContext(), "찾는중...", Toast.LENGTH_SHORT).show();
        mMapLocationManager = new NMapLocationManager(getContext());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mMapLocationManager.enableMyLocation(true);
        boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
        if (!isMyLocationEnabled) {    //위치 탐색이 불가능하면
            Toast.makeText(getContext(), "GPS권한을 허용해주십시오.", Toast.LENGTH_LONG).show();
            Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(goToSettings);
            return;
        }
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
        //Toast.makeText(getContext(), "찾았습니다!", Toast.LENGTH_SHORT).show();
    }
    private void stopMyLocation() {
        mMapLocationManager.disableMyLocation();    //현재 위치 탐색 종료
        if (mMapView.isAutoRotateEnabled()) {        //지도 회전기능이 활성화 상태라면
            mMyLocationOverlay.setCompassHeadingVisible(false);    //나침반 각도표시 제거
            mMapCompassManager.disableCompass();    //나침반 모니터링 종료
            mMapView.setAutoRotateEnabled(false, false);//지도 회전기능 중지
        }
    }
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem poiItem) { //오버레이의 버튼을 눌렀을때
            Log.i("이름", poiItem.getTitle());
        }
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem poiitem) {
        }
    };
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            if (mMapController != null) {
                mMapController.animateTo(myLocation);
            }
            a = myLocation.getLongitude();
            b = myLocation.getLatitude();
            return true;
        }
        public void makeOverlay(){
            int markerId = NMapPOIflagType.PIN;    //마커 id설정
            //POI 데이터 관리 클래스 생성(POI데이터 수, 사용 리소스 공급자)
            NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
            poiData.beginPOIdata(2);    // POI 아이템 추가 시작
            poiData.addPOIitem(a+0.01, b-0.009, "좌표1", markerId, 1);

            poiData.endPOIdata();        // POI 아이템 추가 종료
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
            poiDataOverlay.showAllPOIdata(9);    //모든 POI 데이터를 화면에 표시(zomLevel)
            //POI 아이템이 선택 상태 변경 시 호출되는 콜백 인터페이스 설정
            poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
        }
        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Toast.makeText(getContext(), "시간초과", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Toast.makeText(getContext(), "GPS를 이용할 수 없는 지역입니다.", Toast.LENGTH_SHORT).show();
            stopMyLocation();
        }
    };

}
