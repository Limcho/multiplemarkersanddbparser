package com.example.kysu.googletest2;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;//구글맵 관련.
    String Tag = "메인액티비티티";




    ArrayList<Product> records = null;
    CustomAdapter adapter;
    Activity context;

    HttpPost httppost;

    StringBuffer buffer;

    HttpResponse response;

    HttpClient httpclient;

    ProgressDialog pd;


    ListView listProduct;
// 리스트뷰 및 db파싱하기 위한 것들.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button listhideis = (Button)findViewById(R.id.hide);
        Button listshow = (Button)findViewById(R.id.hidesee);
        Button move1 = (Button)findViewById(R.id.buttonm1);
        move1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move12 = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(move12);
            }
        });

        Log.i(Tag,"onCreate");
        context=this; // 현 액티비티
        records = new ArrayList<Product>();
        final ListView listProduct = (ListView)findViewById(R.id.listView);
        listhideis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Tag,"onClick: listhideis");
                listProduct.setVisibility(View.INVISIBLE);
            }
        });
        listshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Tag,"onClick_listshow ");
                listProduct.setVisibility(View.VISIBLE);
            }
        });
        listProduct.setFocusable(false);
        registerForContextMenu(listProduct);
        adapter = new CustomAdapter(context, records.size(), records);
        Log.i(Tag,"adapter: "+ adapter);
        listProduct.setAdapter(adapter);
        Log.i(Tag,"listProduct:" +listProduct);
        //어댑터 끝
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //마시멜로일 경우 권한 체크.M= 마시멜로.
            checkLocationPermission();//권한 요구하는거.
            Log.i(Tag,"checkLocationPermission");
        }
        // SupportMapFragment 가지고 지도 사용할 준비가 되었으면. 알림을 보내줌.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);
        Log.i(Tag,"mapFragment.getMapAsync");
        BackTask bt=new BackTask();

        bt.execute();

       /* SharedPreferences pref = getSharedPreferences("dlist", MODE_PRIVATE);
        SharedPreferences.Editor led = pref.edit();
        buildGoogleApiClient();
        if(this.getSharedPreferences("dlist", MODE_PRIVATE)!= null) {
            pref = getSharedPreferences("dlist", MODE_PRIVATE);
            Double widois = null, Double gugdois = null;
            for (int d = 0; d < pref.getInt("anumber", 0); d++) {
                try {

                    widois = Double.parseDouble(pref.getString("mamemo" + d, "0"));
                    gugdois = Double.parseDouble(pref.getString("masday" + d, "0"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("쉐어드에서 꺼내는거.", "" + widois);
                mMap.addMarker(new MarkerOptions().position(new LatLng(widois, gugdois))
                        .title(pref.getString("matitle" + d, ""))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markertest1))
                );
            }
        }*/
        //이것도 아님.

        Log.e("크리에이트",""+records.size());

    }// 리스트뷰에 어댑터 연결해줌.

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(Tag,"onStart");
       /* BackTask bt=new BackTask();

        bt.execute();// onStart때 어싱크 테스크를 돌려서 정보를 파싱해온다.
*/

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(Tag,"onConnected");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Tag,"onConnectionSuspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(Tag,"onLocationChanged");
        mLastLocation = location;
        Log.i(Tag,""+location);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
            Log.i(Tag,"현재위치 마커 제거.");
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i(Tag,"MylatLng");
        MarkerOptions markerOptions = new MarkerOptions();
        Log.i(Tag,"markerOptions 내위치 생성.");
        markerOptions.position(latLng);
        markerOptions.title("현재위치");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        Log.i(Tag,""+mCurrLocationMarker);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            Log.i(Tag,""+mGoogleApiClient);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Tag,"onResume");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(Tag,"onConnectionFailed");
    }
    protected Marker createmaker(Double widoc, Double gugdoc, String titlec ){
        return mMap.addMarker(new MarkerOptions()
        .position(new LatLng( widoc, gugdoc))
        .title(titlec)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(Tag,"onMapReady");
        SharedPreferences pref = getSharedPreferences("dlist", MODE_PRIVATE);
        Log.i(Tag,"getSharedPreferences");
        SharedPreferences.Editor led = pref.edit();
        Log.i(Tag,"led");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//마시멜로우 이상일 때.
            Log.i(Tag,"");
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)// 매니페스트 선택할 때 안드로이드로 선택해서 해야 권한이 나옴 (자기 패키지 ㄴㄴㄴ)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i(Tag,"PackageManager, 퍼미션 체크.");
                buildGoogleApiClient();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.3945,
                        126.9605), 10));
                Log.i(Tag,"moveCamera 서울지역 보여줌." );
                mMap.setMyLocationEnabled(true);

                Log.i(Tag,"setMyLocationEnabled");
                /*if (this.getSharedPreferences("dlist", MODE_PRIVATE) != null) {*/
                    pref = getSharedPreferences("dlist", MODE_PRIVATE);
                Log.i(Tag,"");
                    Double widois = null;
                Log.i(Tag,"");
                    Double gugdois = null;
                Log.i(Tag,"");
                    Log.d("사이즈는?", pref.getInt("anumber", 0)+"");




                for (int d = 0; d < pref.getInt("anumber", 0); d++) {
                    Marker[] marker = new Marker[pref.getInt("anumber", 0)];
                    try {



                        widois = Double.parseDouble(pref.getString("mamemo" + d, "0"));
                        gugdois = Double.parseDouble(pref.getString("masday" + d, "0"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("쉐어드에서 꺼내는거.", "" + widois);

                    marker[d] = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(gugdois,widois))
                            .title(pref.getString("matitle" + d, ""))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    ) ;
                    Log.d(Tag, "마커 는 "+ marker[d]);
                }
               /* widois = Double.parseDouble(pref.getString("mamemo" + 7, "0"));
                gugdois = Double.parseDouble(pref.getString("masday" + 7, "0"));
                Log.d(Tag, "as1"+ widois+ "1 "+gugdois);
                Marker as1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(widois,gugdois))
                .title(pref.getString("matitle" + 7, ""))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
*/
                   /* mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(37.479097, 127.011784))
                            .title("되라.")
                       *//* .anchor(0,5)//회전
                        .rotation(90)//마커회전*//*
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markertest))//마커 아이콘
                    );*/
                   /* for(int i=0; i<=3; i++){
                            mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .position(new LatLng(37.45595323143157+i, 127.12713718414307+i))
                                .title("등촌칼국수"+i).snippet("031-752-3414"));

                    }*/
                // 폴문 동작안함. 0까지만 동작.
                    Marker as = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(new LatLng(37.457219, 127.12681))
                            .title("토시래").snippet("031-755-7825"));
                Log.d(Tag, "ㅁㄴ" + as);
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(new LatLng(37.448696585910376, 127.12692260742188))
                            .title("취룡").snippet("031-721-5688"));
                /*for (int d = 0; d < pref.getInt("anumber", 0); d++) {
                    widois = Double.parseDouble(pref.getString("mamemo" + d, "0"));
                    gugdois = Double.parseDouble(pref.getString("masday" + d, "0"));
                    MarkerOptions optsecond = new MarkerOptions();
                    optsecond.position(new LatLng(widois, gugdois));
                    optsecond.title(pref.getString("matitle" + d, ""));
                    optsecond.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(optsecond);
                }*/

        /*        for (int d = 0; d < pref.getInt("anumber", 0); d++) {


                       widois = Double.parseDouble(pref.getString("mamemo" + d, "0"));
                       gugdois = Double.parseDouble(pref.getString("masday" + d, "0"));

                       Log.e("쉐어드에서 꺼내는거.", "위도 :" + widois+ " 경도 :" + gugdois);
                       LatLng latLng1 = new LatLng(widois, gugdois);

                       mMap.addMarker(new MarkerOptions()
                               .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                               .position(new LatLng(widois,gugdois))
                               .snippet("제발")
                               .title(pref.getString("matitle" + d, ""))

                       );
                       Log.d("쉐어드 타이틀", pref.getString("matitle" + d, ""));

                }
*/
                }
            } else {
            Log.i(Tag,"buildGoogleApiClient");
                buildGoogleApiClient();
            Log.i(Tag,"");
                mMap.setMyLocationEnabled(true);
            Log.i(Tag,"setMyLocationEnabled");
                Marker t1 = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.479097, 127.011784))
                        .title("되라.")

                );
            /*Marker t2 = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(40.479097, 142.011784))
                    .title("되네"));*/
                //마커를 여러개 쓸 때 이렇게는 아닌 방식인듯.
            }
        }


    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i(Tag,""+mGoogleApiClient);
        mGoogleApiClient.connect();
        Log.i(Tag,"api클라이언트와 연결.");
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        Log.i(Tag,"위치정보 권한 확인");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i(Tag,"권한 묻기.");
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(Tag,"권한 승인.");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                Log.i(Tag,"requestPermissions");

            } else {
                Log.i(Tag,"승인 거절하거나 없는경우?");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                Log.i(Tag,"");
            }
            return false;
        } else {
            return true;
        }
    }
    private void drawMarker(LatLng point, String title){
        MarkerOptions markeroption = new MarkerOptions();
        markeroption.position(point);
        markeroption.title(title);
        markeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        Log.e("이상해마커생성", markeroption+"");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.i(Tag,"MY_PERMISSIONS_REQUEST_LOCATION :"+MY_PERMISSIONS_REQUEST_LOCATION);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(Tag,""+grantResults[0]);
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i(Tag,"checkSelfPermission권한 승낙.");
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                            Log.i(Tag,"buildGoogleApiClient 없을 경우 만들어줌");
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Log.i(Tag,"권한 거절.");
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "권한 승인이 이루어지지 않아 시작할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }





    private class BackTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(Tag,"");
            InputStream is=null;

            String result="";

            try{
                Log.i(Tag,"");


                httpclient=new DefaultHttpClient();

                httppost= new HttpPost("http://128.199.159.158/andtest1.php");
                //접속할 php 주소 url

                response=httpclient.execute(httppost);
                //실행하고 결과 responese로 받아오기.
                HttpEntity entity = response.getEntity();

                //전달받은 entity 얻어오기.

                is = entity.getContent();
                //인풋스트림에 얻은것을 넣어준다.

            }catch(Exception e){


                Log.i(Tag,"");
                if(pd!=null)
                    Log.i(Tag,"");
                    pd.dismiss();

                Log.e("안되 에러..", e.getMessage());
                // 프로그래스를 종료함.


            }



            //convert response to string

            try{
                Log.i(Tag,"");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                // 버퍼 리더에서 얻고 인코딩 처리.
                Log.i(Tag,"");
                StringBuilder sb = new StringBuilder();
                Log.i(Tag,"");
                String line = null;
                Log.i(Tag,"");
                while ((line = reader.readLine()) != null) {
                    Log.i(Tag,"");
                    sb.append(line+"\n");
                    //라인단위로 읽어서 스트링 빌더에 담음.
                }
                Log.i(Tag,"");
                is.close();
                Log.i(Tag,"");
                //인풋스트림 닫고
                result=sb.toString();  Log.i(Tag,"");
                // 스트링빌더에 담겨진 내용 스트링으로 변환
            }catch(Exception e){

                Log.e("에러다.", "결과 변환하는 에러 "+e.toString());



            }



            // json 데이터 파서 한것 여기까지. 아래부터 후처리.

            try{

                // 예상못한 문자 제거하고 시작점을 정해줌.
                Log.i(Tag,"");
                result=result.substring(result.indexOf("["));
                Log.i(Tag,"");
                JSONArray jArray =new JSONArray(result);
                Log.i(Tag,"");
                for(int i=0;i<jArray.length();i++){
                    Log.i(Tag,"");
                    JSONObject json_data =jArray.getJSONObject(i);
                    Log.i(Tag,"");
                    Product p=new Product();
                    Log.i(Tag,"");
                    p.setpTitle(json_data.getString("title"));

                    p.setPwido(json_data.getDouble("mapx"));

                    p.setPgugdo(json_data.getDouble("mapy"));

                    p.setPmlevel(json_data.getString("mlevel"));
                    String compareimage = "";
                    String jsonimagestring = json_data.getString("firstimage");
                    if(jsonimagestring.matches(compareimage)){
                        jsonimagestring = "http://www.jirisannb.com/img/no_detail_img.gif";
                    }
                    Log.d(Tag, "널일때"+jsonimagestring);
                    p.setpUrl(jsonimagestring);// json데이터의 키를 해줘야 가져올 수 있음.

                    records.add(p);
                    //어레이리스트에 추가함.


                }
            }

            catch(Exception e){

                Log.e("에러닷.", "json데이터 파싱하고 후처리하는되서. "+e.toString());





            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(Tag,"");
            if(pd!=null) pd.dismiss(); //close dialog

            Log.e("어싱크테스크 리스트사이즈", records.size() + "");


            adapter.notifyDataSetChanged(); //notify the ListView to get new records
            SharedPreferences pref = getSharedPreferences("dlist", MODE_PRIVATE);
            SharedPreferences.Editor led = pref.edit();
            led.clear();
            for(int d=0; d<records.size(); d++) {
                led.putString("matitle"+d, records.get(d).getpTitle());
                try{
                    led.putString("mamemo"+d,(records.get(d).getPwido().toString()));
                    led.putString("masday"+d, (records.get(d).getPgugdo().toString()));
                    Log.e("데이터넣는중.", records.get(d).getPwido().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                led.putString("mastime"+d, records.get(d).getPmlevel());
                led.putInt("anumber", records.size());

            }
            Log.e("tag", "왜 안되지퓨즈"+records.size());
            led.commit();
            Log.e("맵상태 체크","" +mMap);
            for(int i=0; i <records.size(); i++){
                drawMarker(new LatLng(records.get(i).getPwido(), records.get(i).getPgugdo()), records.get(i).getpTitle());
                /*mMap.addMarker(new MarkerOptions()
                        .title(records.get(i).getpTitle())
                        .position(new LatLng(records.get(i).getPwido(), records.get(i).getPgugdo()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                );*/

            }
        }
        private void drawMarker(LatLng point, String title){
            MarkerOptions markeroption = new MarkerOptions();
            markeroption.position(point);
            markeroption.title(title);
            markeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            mMap.addMarker(markeroption);
            Log.e("이상해마커생성", markeroption+"");
        }

        protected void onPreExecute() {
            Log.i(Tag,"");
            super.onPreExecute();

            pd = new ProgressDialog(context);

            pd.setTitle("데이터 삽입중");

            pd.setMessage("잠시만 기다려 주세여!");

            pd.setCancelable(true);

            pd.setIndeterminate(true);

            pd.show();

            Log.i(Tag,"");
        }
    }
}
