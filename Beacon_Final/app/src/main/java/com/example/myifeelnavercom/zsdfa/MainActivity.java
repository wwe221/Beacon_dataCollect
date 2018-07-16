package com.example.myifeelnavercom.zsdfa;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {
    public static final String sIP = "118.219.241.229";
    //사용할 통신 포트
    public static final int sPORT = 4421;
    //데이터 보낼 클랙스
    DatagramSocket socket;
    InetAddress serverAddr;
    //화면 표시용 TextView
    public TextView txtView;
    long now = System.currentTimeMillis();
    String deviceId;
    String formatDate;
    long time;
    String ABID = "No Beacon";
    String noiseB = "No Beacon";
    boolean BConnect = false;
    boolean connecting=false;
    TextView dateNow;
    public BeaconManager beaconManager;
    public BeaconManager beaconManager2;
    public BeaconManager beaconManager3;
    public BeaconManager beaconManager4;
    public BeaconManager beaconManager5;
    public BeaconManager beaconManager6;
    public BeaconManager beaconManager7;
    public BeaconManager beaconManager8;
    public BeaconManager beaconManager9;

    private Region regions[] = new Region[500];
    private TextView b1,b2,b3,b4,b5,b6,b7,b8,b9,textt;
    public int rss1, rss2, rss3, max, numbofB, maxB;
    //           각 비콘의 신호세기 , 가장큰세기, 비콘갯수, 가장큰비콘의 넘버
    public int rss[]=new int[500];
    public String line;
    private List<Beacon> beaconList = new ArrayList<>();
    public static final int f = 10, P=10, Q= 90; // noise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String androidId;
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), (long) 0xbb << 32);
        deviceId = androidId.toString();
        TextView textView1 = (TextView) findViewById(R.id.text1);
        textView1.setText(deviceId);
        Log.d("Test", "device");

        //setContentView(R.layout.activity_main);
        b1 = (TextView) findViewById(R.id.b1);
        b2= (TextView) findViewById(R.id.b2);
        b3= (TextView) findViewById(R.id.b3);
        b4= (TextView) findViewById(R.id.b4);
        b5= (TextView) findViewById(R.id.b5);
        b6= (TextView) findViewById(R.id.b6);
        b7 = (TextView) findViewById(R.id.b7);
        b8= (TextView) findViewById(R.id.b8);
        b9= (TextView) findViewById(R.id.b9);
        textt = (TextView) findViewById(R.id.text1);


        max = -500;
        maxB = -1;
        beaconManager = new BeaconManager(this);
        beaconManager2 = new BeaconManager(this);
        beaconManager3 = new BeaconManager(this);
        beaconManager4 = new BeaconManager(this);
        beaconManager5 = new BeaconManager(this);
        beaconManager6 = new BeaconManager(this);
        beaconManager7 = new BeaconManager(this);
        beaconManager8 = new BeaconManager(this);
        beaconManager9 = new BeaconManager(this);
        //add this below:

        regions[1] = new Region("Beacon 7",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 10602);
        regions[2] = new Region("Beacon 8",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 10596);
        regions[3] = new Region("Beacon 9",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 10595);
        regions[4] = new Region("Beacon 1",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 20160);
        regions[5] = new Region("Beacon 2",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 20132);
        regions[6] = new Region("Beacon 3",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 20047);
        regions[7] = new Region("Beacon 4",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 10568);
        regions[8] = new Region("Beacon 5",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 13285);
        regions[9] = new Region("Beacon 6",
                UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 13294);



        numbofB = 10;
        for(int i=0;i<=numbofB;i++)
            rss[i]=-500;
        beaconCheck();
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                textt.setText(line);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        MMaxing();
                        MMax();
                        Message msg= handler.obtainMessage();
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.e("run", "S: Error", e);
                    }

                }
            }
        });
        thread.start();

    }
    public void MMaxing(){
        int a, b=0;
        int i;
        for (i = 0; i <=numbofB ; i++) {
            if(max<rss[i]) {
                max=rss[i];
                maxB=i+1;
            }

        }
    }
    public void MMax() {

//        mmax.setText("max:" + max + "||" + maxB);

        time = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatDate = dayTime.format(new Date(time));
//        noiseB = noBid(maxB, numbofB);
//        noiseB = sencondnoise(noiseB,numbofB);
        if(noiseB=="")
            noiseB="0";
        //현재 시간 측정 저장.
        //SendData 클래스 생성
        try {
            serverAddr = InetAddress.getByName("118.219.241.229");
        } catch (Exception e) {
            Log.e("InetAddress", "S: Error", e);
        }

        if(BConnect) {
            try {
                //UDP 통신용 소켓 생성
                socket = new DatagramSocket();
                //서버 주소 변수
                //보낼 데이터 생성
                byte[] buf = ("Hello, World").getBytes();
                //패킷으로 변경
                line = deviceId + '|' + maxB + '|' + formatDate + '|';
                DatagramPacket packet = new DatagramPacket(line.getBytes(), line.getBytes().length, serverAddr, sPORT);
                //DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 4421);
                //패킷 전송!
                socket.send(packet);


                Log.d("Test", "s:send packet" + buf.length);
                socket.close();

            } catch (Exception e) {
                Log.e("UDP", "S: Error", e);
            }
        }
    }

    public void beaconCheck() {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override

            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon1 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon1.getRssi());
                    rss[0] = beacon1.getRssi();
                    b1.setText(rss[0] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b1.setText("No signal");
                }
            }
        });
        beaconManager2.setRangingListener(new BeaconManager.RangingListener() {
            public void onBeaconsDiscovered(Region region2, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon2 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "Second Beacon: " + beacon2.getRssi());
                    rss[1] = beacon2.getRssi();
                    b2.setText(rss[1] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b2.setText("No signal");
                }
            }
        });
        beaconManager3.setRangingListener(new BeaconManager.RangingListener() {
            public void onBeaconsDiscovered(Region region3, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon3 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "Third Beacon " + beacon3.getRssi());
                    rss[2] = beacon3.getRssi();
                    b3.setText(rss[2] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b3.setText("No signal");
                }
            }
        });
        beaconManager4.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region4, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon4 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon4.getRssi());
                    rss[3] = beacon4.getRssi();
                    b4.setText(rss[3] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b4.setText("No signal");
                }
            }
        });
        beaconManager5.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region5, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon5 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon5.getRssi());
                    rss[4] = beacon5.getRssi();
                    b5.setText(rss[4] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b5.setText("No signal");
                }
            }
        });
        beaconManager6.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region6, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon6 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon6.getRssi());
                    rss[5] = beacon6.getRssi();
                    b6.setText(rss[5] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b6.setText("No signal");
                }
            }
        });
        beaconManager7.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region7, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon7 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon7.getRssi());
                    rss[6] = beacon7.getRssi();
                    b7.setText(rss[6] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b7.setText("No signal");
                }
            }
        });
        beaconManager8.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region8, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon8 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon8.getRssi());
                    rss[7] = beacon8.getRssi();
                    b8.setText(rss[7] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b8.setText("No signal");
                }
            }
        });
        beaconManager9.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region9, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon9 = list.get(0);
                    BConnect = true;

                    Log.d("Airport", "First Beacon " + beacon9.getRssi());
                    rss[8] = beacon9.getRssi();
                    b9.setText(rss[8] + "");
                }
                else if(list.isEmpty()){
                    BConnect = false;

                    b9.setText("No signal");
                }
            }
        });

    }
    public static String sencondnoise(String s, int numbofB) {
        String t = "";
        for (int i = 0; i < numbofB; i++) {
            int a = (int) (Math.random() * 100 + 1); // 1~ 100
            //진실이 아닌결우
            if (a <= P) {
                int b = (int) (Math.random() * 100 + 1);
                if (b > Q)
                    t = t + (i + 1) + " ";
            }
            //진실을 말할경우
            else {
                if(s.charAt(i)=='1')
                    t = t + (i + 1) + " ";
            }
        }
        t = t.trim();
        return t;
    }

    public static String noBid(int maxB, int numbofB) {
        String s = "", t = "";
        for (int i = 0; i < numbofB; i++) {
            int a = (int) (Math.random() * 100 + 1);   // 1~ 100
            if (a <= f) {
                int b = (int) (Math.random() * 2);
                if (b == 0)
                    s = s + "0";
                else {
                    s = s + "1";
                    t = t + (i + 1) + " ";
                }

            } else {
                if (maxB != i + 1)
                    s = s + "0";
                else {
                    s = s + "1";
                    t = t + (i + 1) + " ";
                }
            }
        }
        t = t.trim();
        return s;
    }
    protected void onResume() {

        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(regions[1]);


            }
        });
        beaconManager2.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager2.startRanging(regions[2]);

            }
        });
        beaconManager3.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager3.startRanging(regions[3]);

            }
        });
        beaconManager4.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager4.startRanging(regions[4]);
            }
        });
        beaconManager5.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager5.startRanging(regions[5]);

            }
        });
        beaconManager6.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager6.startRanging(regions[6]);

            }
        });


        beaconManager7.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(regions[7]);

            }
        });
        beaconManager8.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(regions[8]);

            }
        });
        beaconManager9.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(regions[9]);

            }
        });
    }
    @Override
    protected void onPause() {
        //beaconManager.stopRanging(region);

        super.onPause();
    }
}