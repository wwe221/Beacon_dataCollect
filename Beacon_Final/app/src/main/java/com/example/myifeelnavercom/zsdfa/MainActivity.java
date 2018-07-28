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

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.Region;
import com.estimote.coresdk.recognition.utils.EstimoteBeacons;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {
    public static final String sIP = "117.16.44.95";
    //사용할 통신 포트
    public static final int sPORT = 8888;
    //데이터 보낼 클랙스
    DatagramSocket socket;
    InetAddress serverAddr;
    //화면 표시용 TextView
    public TextView txtView;
    long now = System.currentTimeMillis();
    String deviceId;
    String formatDate;
    long time;
    int Best;
    String noiseB = "No Beacon";
    boolean BConnect= false;
    public BeaconManager beaconManager; 
    double Becaons[]=new double[20]; // 최대 비콘을 20개로 지정

    private Region regions= new BeaconRegion("pid",null,null,null);
    private TextView b1,textt;
    public int  max, numbofB, maxB;
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

        b1 = (TextView) findViewById(R.id.NearestBeacon);
        textt = (TextView) findViewById(R.id.text1);
        max = -500;
        maxB = -1;
        beaconManager = new BeaconManager(this);
        Becaons[0]=0;
        Becaons[1] = 10602;
        Becaons[2] = 10596;
        Becaons[3] = 10595;
        Becaons[4] = 20160;
        Becaons[5] = 20132;
        Becaons[6] =20047;
        Becaons[7] = 20135;
        Becaons[8] = 20208;
        Becaons[9] = 20156;
        Becaons[10] = 20159;
        Becaons[11]= 10568;
        Becaons[12]= 13285;
        Becaons[13]= 13294;
        //add this below:
        numbofB = 13;
        Main();
    }
    // 쓰레드를 이용해 비콘의 세기를 측정한다.
    public void Main(){
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
    //
    public int MMaxing(int numbofB){
        int a=0;
        for(int i=1;i<=numbofB;i++) {
            if(Best==Becaons[i])
                a=i;
            else if (i==numbofB) {
                maxB = a;
                return a;
            }
            else
                continue;
        }
        maxB=a;
        return a;
    }
    public void MMax() {
        time = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatDate = dayTime.format(new Date(time));
//        noiseB = noBid(maxB, numbofB);
//        noiseB = sencondnoise(noiseB,numbofB);
        if(noiseB=="")
            noiseB="0";
        try {
            serverAddr = InetAddress.getByName("117.16.44.95");
            //serverAddr = InetAddress.getByName("118.219.241.229");
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
                //패킷 전송!
                socket.send(packet);
                Log.d("Test", "s:send packet" + buf.length);
                socket.close();

            } catch (Exception e) {
                Log.e("UDP", "S: Error", e);
            }
        }
    }

    //비콘과의 연결
    public void beaconCheck() {
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    
                    Beacon beacon1 = list.get(0);
                    BConnect = true;
                    rss[0] = beacon1.getRssi();
                    Best=beacon1.getMinor();
                    b1.setText(beacon1.getRssi() + "||"+MMaxing(numbofB)+"");
                }
                else if(list.isEmpty()){
                    BConnect= false;

                    b1.setText("No signal");
                }
            }
        });

    }
    //2번째 노이즈 
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
    //1번째 노이즈 추가
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
                beaconManager.startRanging((BeaconRegion) regions);


            }
        });
    }
    @Override
    protected void onPause() {
        //beaconManager.stopRanging(region);
        super.onPause();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging((BeaconRegion) regions);


            }
        });

    }
    protected void onStop() {
        super.onStop();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging((BeaconRegion) regions);


            }
        });
    }
    protected void onRestart() {
        super.onRestart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging((BeaconRegion) regions);


            }
        });
    }

}
