package com.example.kysu.googletest2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main2Activity extends Activity implements OnMapReadyCallback {
    GoogleMap mMap;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
    }*/
        /*BackTask bt=new BackTask();

        bt.execute();*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.3945,
                126.9605), 10));// 처음부터 위치를 잡아주고??
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(new LatLng(37.45595323143157, 127.12713718414307))
                .title("등촌칼국수").snippet("031-752-3414"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(new LatLng(37.457219, 127.12681))
                .title("토시래").snippet("031-755-7825"));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(new LatLng(37.448696585910376, 127.12692260742188))
                .title("취룡").snippet("031-721-5688"));



    }

    /*private class BackTask extends AsyncTask<Void,Void,Void> {

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

                    p.setpUrl(json_data.getString("firstimage"));// json데이터의 키를 해줘야 가져올 수 있음.

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
                mMap.addMarker(new MarkerOptions()
                        .title(records.get(i).getpTitle())
                        .position(new LatLng(records.get(i).getPwido(), records.get(i).getPgugdo()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                );

            }
        }
        private void drawMarker(LatLng point, String title){
            MarkerOptions markeroption = new MarkerOptions();
            markeroption.position(point);
            markeroption.title(title);
            markeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
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
    }*/
}
