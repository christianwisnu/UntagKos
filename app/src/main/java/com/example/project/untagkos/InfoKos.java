package com.example.project.untagkos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
//import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

import control.AppController;
import control.Link;
import model.ColFasilitas;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.BaseApiService;
import session.SessionManager;

public class InfoKos extends AppCompatActivity implements OnMapReadyCallback,
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, RatingDialogListener {

    private ProgressDialog pDialog;
    private GoogleMap mGoogleMap;
    private SessionManager session;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private ImageView imgBack, imgGiveRate;
    private TextView txtJudul, txtNama, txtHarga, txtId, txtJmlh, txtPnjgLbr, txtListrik, txtOcupant, txtKontak,
            txtTelp, txtFasilitas, txtAlamat, telefon, sms, txtSisa, txtCekin, txtNilaiRating, txtCountUser;
    private String namaKos, listrik, ocupant, namaCust, telp, fasilitas, alamat, harga, slasid, idUser,
            gambar1, gambar2, gambar3, gambar4, gambar5;
    private Integer idKos, jmlh, panjang, lebar, idCust, hargaAsli;
    private double lat, longt;
    private Button btnBooking, btnCekin;
    private static final long INTERVAL = 1000 * 2 * 1; //2 detik
    private static final long FASTEST_INTERVAL = 1000 * 1 * 1; // 1 detik
    private float zoomLevel = (float) 16.0;
    private String addBooking	="addBooking.php";
    private String cekBooking = "getCountBookingUser.php";
    private String cekBookingAktif = "getCountBookingUserAktif.php";
    private ProgressDialog dialog;
    private Integer countId, sisa;
    private SliderLayout sliderLayout;
    private TreeMap<Integer,String> treeMap;
    private Integer a=0, countUser;
    private SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar dateAndTime = Calendar.getInstance();
    private AlertDialog alert;
    private ArrayList<ColFasilitas> mArrayList;
    StringBuilder sb = new StringBuilder();
    private ScaleRatingBar ratingDatabase;
    private HashMap<String, Object> user;
    private Object sstatus;
    private double rating=0;
    private BigDecimal hasilRating = BigDecimal.ZERO;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_kos);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        mApiService         = Link.getAPIService();
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                idKos = i.getInt("id_kos");
                idUser = i.getString("idUser");
                idCust = i.getInt("i_idcust");
                namaCust = i.getString("namaCust");
                jmlh = i.getInt("i_jmlkamar");
                alamat = i.getString("vc_alamat");
                namaKos = i.getString("vc_namakost");
                lebar = i.getInt("i_lebar");
                panjang = i.getInt("i_panjang");
                listrik = i.getString("c_statuslistrik");
                ocupant = i.getString("i_idsewastatus");
                telp = i.getString("tlpCust");
                fasilitas = i.getString("fasilitas");
                lat = i.getDouble("lat");
                longt = i.getDouble("longt");
                harga = i.getString("harga");
                hargaAsli = i.getInt("hargaAsli");
                gambar1 = i.getString("gambar");
                gambar2 = i.getString("gambar2");
                gambar3 = i.getString("gambar3");
                gambar4 = i.getString("gambar4");
                gambar5 = i.getString("gambar5");
                sisa = i.getInt("i_sisa");
                rating = i.getDouble("rating");
                countUser = i.getInt("countUser");
            } catch (Exception e) {}
        }
        session =new SessionManager(getApplicationContext());
        try {
            user = session.getUserDetails();
            sstatus = user.get(SessionManager.sStatusLogin);
        } catch (Exception e) {
            e.getMessage();
        }

        loadJSON();

        treeMap = new TreeMap<Integer,String>();
        txtCountUser = (TextView)findViewById(R.id.txtUserRating);
        txtNilaiRating = (TextView) findViewById(R.id.txtNilaiRating);
        ratingDatabase = (ScaleRatingBar)findViewById(R.id.simpleRatingBar);
        imgGiveRate = (ImageView)findViewById(R.id.imgGiveRating);
        txtJudul		= (TextView)findViewById(R.id.TvTittleInfoKos);
        txtNama		= (TextView)findViewById(R.id.txtNamaInfoKos);
        txtHarga		= (TextView)findViewById(R.id.txtHargaInfoKos);
        txtId		= (TextView)findViewById(R.id.txtIdInfoKos);
        txtJmlh		= (TextView)findViewById(R.id.txtJmlKamarInfoKos);
        txtPnjgLbr		= (TextView)findViewById(R.id.txtPnjgLbrInfoKos);
        txtListrik		= (TextView)findViewById(R.id.txtListrikInfoKos);
        txtOcupant		= (TextView)findViewById(R.id.txtSewaInfoKos);
        txtKontak		= (TextView)findViewById(R.id.txtKontakInfoKos);
        txtTelp		= (TextView)findViewById(R.id.txtTelfInfoKos);
        txtFasilitas		= (TextView)findViewById(R.id.txtFasilitasInfoKos);
        txtAlamat = (TextView)findViewById(R.id.txtAlamatInfoKos);
        txtSisa = (TextView)findViewById(R.id.txtSisaKamarInfoKos);
        telefon = (TextView)findViewById(R.id.txtTelp);
        sms = (TextView)findViewById(R.id.txtSms);
        btnBooking = (Button)findViewById(R.id.btnBookingInfoKos);
        imgBack = (ImageView)findViewById(R.id.ImbBackInfoKos);
        sliderLayout = (SliderLayout) findViewById(R.id.slider);

        txtJudul.setText(namaKos);
        txtNama.setText(namaKos);
        txtId.setText("Id Kos: "+idKos);
        txtHarga.setText("Harga Rp. "+harga);
        txtJmlh.setText("Jumlah Kamar: "+String.valueOf(jmlh));
        txtPnjgLbr.setText("Ukuran Kamar: "+String.valueOf(panjang)+"X"+String.valueOf(lebar));
        txtListrik.setText("Include Listrik: "+listrik);
        txtOcupant.setText("Occupant: "+ ocupant);
        txtKontak.setText("Contact Person: "+namaCust);
        txtTelp.setText("Phone: "+telp);
        txtAlamat.setText(alamat);
        txtSisa.setText("Sisa Kamar: "+String.valueOf(sisa));

        treeMap.put(1, gambar1==null||gambar1.equals("null")||gambar1.equals("")?"":Link.FileImage+gambar1);
        treeMap.put(2, gambar2==null||gambar2.equals("null")||gambar2.equals("")?"":Link.FileImage+gambar2);
        treeMap.put(3, gambar3==null||gambar3.equals("null")||gambar3.equals("")?"":Link.FileImage+gambar3);
        treeMap.put(4, gambar4==null||gambar4.equals("null")||gambar4.equals("")?"":Link.FileImage+gambar4);
        treeMap.put(5, gambar5==null||gambar5.equals("null")||gambar5.equals("")?"":Link.FileImage+gambar5);

        for(Integer name : treeMap.keySet()){
            String gambar=treeMap.get(name);
            TextSliderView textSliderView = new TextSliderView(InfoKos.this);
            if(gambar.equals("")){
                textSliderView
                        .description("Gambar "+String.valueOf(name))
                        .image(R.drawable.no_image)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
            }else{
                textSliderView
                        .description("Gambar "+String.valueOf(name))
                        .image(gambar)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
            }

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra","Gambar "+String.valueOf(name));
            sliderLayout.addSlider(textSliderView);
            a++;
        }

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(6000);
        sliderLayout.addOnPageChangeListener(this);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();*/
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frgMapsInfoKos);
        fm.getMapAsync(this);

        reloadDataRating();

        if(sstatus.equals("USER")){
            imgGiveRate.setVisibility(View.INVISIBLE);
        }else{
            imgGiveRate.setVisibility(View.INVISIBLE);
        }
        telefon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:"+telp));
                startActivity(callIntent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",telp);
                startActivity(smsIntent);
            }
        });

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sisa==0){
                    Toast.makeText(getApplicationContext(),
                            "Kamar kos sudah penuh!", Toast.LENGTH_LONG)
                            .show();
                }else{
                    save();
                }
            }
        });

        imgGiveRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountUser();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int rate, String comment) {
        requestRegister(idKos, Integer.valueOf(idUser), comment, rate);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    private void requestRegister(final Integer idBeritaku, final Integer idUserku, final String isi,
                                 final Integer rate){
        pDialog.setMessage("Please Wait ...");
        showDialog();
        mApiService.saveRating(idBeritaku, idUserku, isi, rate).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("value").equals("false")){
                            if (jsonRESULTS.getString("value").equals("false")){
                                rating = jsonRESULTS.getJSONObject("rate").getInt("total_nilai");
                                countUser = jsonRESULTS.getJSONObject("rate").getInt("user_count");
                                reloadDataRating();
                                Toast.makeText(InfoKos.this, "Terima kasih atas penilaian anda", Toast.LENGTH_LONG).show();
                            } else {
                                String error_message = jsonRESULTS.getString("message");
                                Toast.makeText(InfoKos.this, error_message, Toast.LENGTH_LONG).show();
                            }
                            hideDialog();
                        }
                    }catch (JSONException e) {
                        hideDialog();
                        Toast.makeText(InfoKos.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }catch (IOException e) {
                        hideDialog();
                        Toast.makeText(InfoKos.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }else{
                    hideDialog();
                    Toast.makeText(InfoKos.this, "GAGAL", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                Toast.makeText(InfoKos.this, "GAGAL", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectCountUser(){
        pDialog.setMessage("Please Wait, Load Data...");
        showDialog();
        mApiService.countUser(idKos, Integer.valueOf(idUser)).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try{
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    Integer nilaiCount = jsonRESULTS.getJSONObject("user").getInt("nilai");
                                    if(nilaiCount.intValue()>0){
                                        Toast.makeText(InfoKos.this, "Anda sudah berpartisipasi memberikan rating terhadap tempat kos ini!", Toast.LENGTH_LONG).show();
                                        hideDialog();
                                    }else{//<=0
                                        hideDialog();
                                        showDialogRate();
                                    }
                                }else{
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(InfoKos.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            }catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                                Toast.makeText(InfoKos.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                hideDialog();
                                e.printStackTrace();
                                Toast.makeText(InfoKos.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            hideDialog();
                            Toast.makeText(InfoKos.this, "GAGAL LOAD DATA", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(InfoKos.this, "GAGAL LOAD DATA", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDialogRate() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(3)
                .setTitle("Rate this Place")
                .setDescription("Please select some stars and give your feedback")
                //.setDefaultComment("This app is pretty cool !")
                .setStarColor(R.color.md_cyan_a400)
                .setNoteDescriptionTextColor(R.color.md_black)
                .setTitleTextColor(R.color.md_black)
                .setDescriptionTextColor(R.color.md_black)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.md_grey_500)
                .setCommentTextColor(R.color.md_white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(InfoKos.this)
                .show();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void reloadDataRating(){
        if(rating>0){
            hasilRating = new BigDecimal(rating).divide(new BigDecimal(countUser),1, RoundingMode.HALF_UP);
        }
        if(hasilRating.compareTo(BigDecimal.ZERO) == 0){
            txtNilaiRating.setText("0.0");
        }else{
            txtNilaiRating.setText(String.valueOf(hasilRating));
        }
        txtCountUser.setText(String.valueOf(countUser));

        /*ratingDatabase.setClickable(false);
        ratingDatabase.setScrollable(false);
        ratingDatabase.setRating(hasilRating.floatValue());*/
    }

    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Link.FilePHP2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getFasilitas();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, retrofit2.Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                mArrayList = new ArrayList<>(Arrays.asList(jsonResponse.getFasilitas()));
                String[] fasilitySplit = fasilitas.split(",");
                for(String key:fasilitySplit){
                    for(ColFasilitas fas : mArrayList){
                        if(key.equals(fas.getKodeFasilitas().trim())){
                            sb.append(fas.getNamaFasilitas()).append(", ");
                            break;
                        }
                    }
                }
                txtFasilitas.setText("Fasilitas: "+sb.substring(0, sb.length()-2).toString());
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void save(){
        dialog = new ProgressDialog(InfoKos.this);
        dialog.setCancelable(true);
        dialog.setMessage("Loading ...\nPlease Wait!");
        dialog.show();
        GetCountData(Link.FilePHP+cekBooking);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        LatLng latLng = new LatLng(lat, longt);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                .title(namaKos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,200));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel), 200, null);


        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        // Showing / hiding your current location
        //mGoogleMap.setMyLocationEnabled(true);
        // Enable / Disable zooming controls
        // Enable / Disable my location button
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        // Enable / Disable Rotate gesture
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        //Enable / Disable Button Zooming
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 200, null);
        createLocationRequest();
    }

    private void GetCountData(String Url){
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                JSONObject object = JsonArray.getJSONObject(0);
                                countId = object.getInt("count");
                                if(countId>0){
                                    Toast.makeText(getApplicationContext(),
                                            "Kos sedang proses booking. \nHarap hubungi pemilik kos untuk konfirmasi lebih lanjut.", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.hide();
                                }else{
                                    GetCountDataAktif(Link.FilePHP+cekBookingAktif);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null){

            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("idKos", String.valueOf(idKos));
                params.put("idCust", String.valueOf(idCust));
                params.put("idUser", idUser);
                return params;
            }

            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    private void GetCountDataAktif(String Url){
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                JSONObject object = JsonArray.getJSONObject(0);
                                countId = object.getInt("count");
                                if(countId>0){
                                    Toast.makeText(getApplicationContext(),
                                            "Anda sekarang sedang dalam kos ini", Toast.LENGTH_LONG)
                                            .show();
                                }else{
                                    dialogBox();
                                }
                                dialog.hide();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null){

            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("idKos", String.valueOf(idKos));
                params.put("idCust", String.valueOf(idCust));
                params.put("idUser", idUser);
                return params;
            }

            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    private void dialogBox(){
        LayoutInflater li = LayoutInflater.from(InfoKos.this);
        View promptsView = li.inflate(R.layout.cek_in_booking, null);
        //btnCekin = (Button) promptsView.findViewById(R.id.btnTglCekIn);
        txtCekin = (TextView) promptsView.findViewById(R.id.eTglCekIn);
        txtCekin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingTanggalFrom();
            }
        });
        AlertDialog.Builder msMaintance = new AlertDialog.Builder(InfoKos.this);
        msMaintance.setView(promptsView);
        msMaintance.setCancelable(false);
        msMaintance.setNegativeButton("Book Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(txtCekin.getText().toString().equals("")){
                    Toast.makeText(InfoKos.this,
                            "Tanggal cek-in harap diisi", Toast.LENGTH_LONG)
                            .show();
                }else{
                    addBooking(Link.FilePHP+addBooking);
                }
            }
        });
        msMaintance.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert = msMaintance.create();
        alert.show();
    }

    private void addBooking(String save){
        dialog.show();
        StringRequest register = new StringRequest(Request.Method.POST, save, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d("Respone", response.toString());
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    int Sucsess = jsonrespon.getInt("success");
                    slasid		=String.valueOf(Sucsess);
                    Log.i("Suceses", String.valueOf(Sucsess));
                    if (Sucsess > 0 ){
                        sendNotification(idCust+"O", "Ada user yang booking kos");
                        Toast.makeText(InfoKos.this,
                                "Data berhasil disimpan", Toast.LENGTH_LONG)
                                .show();
                        alert.dismiss();
                        finish();
                    }else{
                        Toast.makeText(InfoKos.this,
                                "Gagal Coba Lagi", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception e) {
                    Toast.makeText(InfoKos.this,
                            "Gagal Coba Lagi", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"Pembayaran DP gagal!\nCheck Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(),"Pembayaran DP gagal!\nAuthFailureError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(),"Pembayaran DP gagal!\nCheck ServerError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),"Pembayaran DP gagal!\nCheck NetworkError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(),"Pembayaran DP gagal!\nCheck ParseError", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("idKos", String.valueOf(idKos));
                params.put("idCust", String.valueOf(idCust));
                params.put("idUser", idUser);
                params.put("harga", String.valueOf(hargaAsli));
                params.put("cekIn", txtCekin.getText().toString()+" 00:00:00");
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(register);
    }

    private void sendNotification(final String userId, final String message){
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Link.BASE_URL_NOTIF, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){

                            }else{
                                //tvstatus.setText("Tidak Ada Data");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //tvstatus.setText("Check Koneksi Internet Anda");
                } else if (error instanceof AuthFailureError) {
                    //tvstatus.setText("AuthFailureError");
                } else if (error instanceof ServerError) {
                    //tvstatus.setText("Check ServerError");
                } else if (error instanceof NetworkError) {
                    //tvstatus.setText("Check NetworkError");
                } else if (error instanceof ParseError) {
                    //tvstatus.setText("Check ParseError");
                }
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("topics", userId);
                params.put("message", message);
                params.put("judul", "Booking Kos");
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Link.BASE_URL_NOTIF, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    DatePickerDialog.OnDateSetListener dFrom =new DatePickerDialog.OnDateSetListener(){

        @Override

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // TODO Auto-generated method stub
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelFrom();
        }
    };

    private void updatelabelFrom(){
        txtCekin.setText(df1.format(dateAndTime.getTime()));
    }

    private void settingTanggalFrom() {
        // TODO Auto-generated method stub
        new DatePickerDialog(InfoKos.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }
}
