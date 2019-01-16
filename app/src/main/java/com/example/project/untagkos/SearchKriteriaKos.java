package com.example.project.untagkos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapter.AdpListUserKos;
import control.Anim;
import control.AppController;
import control.Link;
import model.ColFasilitas;
import model.ColHomeDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchKriteriaKos extends AppCompatActivity {

    private ArrayList<ColFasilitas> seletedItems=new ArrayList();
    private ArrayList<ColFasilitas> mArrayList;
    private CharSequence[] items = null;
    private List<CharSequence> charSequences = new ArrayList<>();
    private Spinner spTipe;
    private Button btnProses;
    private ProgressDialog dialog;
    private EditText eFasilitas, eMulai, eSampai;
    private ImageView imgBack, imgExpand, imgFasilitas;
    private TextInputLayout ilFasilitas, ilHargaMulai, ilHargaSampai;
    private String daftarKodeFasilitas, daftarNamaFasilitas, idUser, querySisa;
    private CheckBox ckAllFasilitas, ckAllHarga;
    private LinearLayout linear;
    private ListView lsvlist;
    private TextView tvstatus;
    private ArrayList<ColHomeDetail> columnlist= new ArrayList<ColHomeDetail>();
    private NumberFormat rupiah	= NumberFormat.getNumberInstance(new Locale("in", "ID"));
    private String getData	="getListKriteriaKos.php";
    private AdpListUserKos adapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kriteria_searching_kos);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ilFasilitas = (TextInputLayout)findViewById(R.id.input_layout_viewfasilitas);
        ilHargaMulai = (TextInputLayout)findViewById(R.id.input_layout_viewharga_mulai);
        ilHargaSampai = (TextInputLayout)findViewById(R.id.input_layout_viewharga_sampai);

        eFasilitas  	= (EditText)findViewById(R.id.eKriteriaFasilitasKos);
        eMulai  		= (EditText)findViewById(R.id.eKriteriaMulaiHargaKos);
        eSampai  		= (EditText)findViewById(R.id.eKriteriaSampaiHargaKos);

        spTipe          = (Spinner) findViewById(R.id.spKriteriaTipeKos);

        btnProses 		= (Button) findViewById(R.id.btnProsesKriteriaKos);

        imgBack 		= (ImageView) findViewById(R.id.imgBackKriteriaKos);
        imgExpand = (ImageView)findViewById(R.id.imgExpandKriteria);
        imgFasilitas = (ImageView) findViewById(R.id.imgPilihFasilitas);

        ckAllFasilitas  = (CheckBox) findViewById(R.id.ckAllFasilitas);
        ckAllHarga      = (CheckBox) findViewById(R.id.ckAllHarga);

        linear = (LinearLayout) findViewById(R.id.linLayout);

        lsvlist = (ListView) findViewById(R.id.LsvKriteriaKos);
        tvstatus = (TextView) findViewById(R.id.TvStatusKriteriaKos);

        loadFasilitas();

        adapter		= new AdpListUserKos(SearchKriteriaKos.this, R.layout.col_listuser, columnlist);
        lsvlist.setAdapter(adapter);

        querySisa = "a.i_jmlkamar-(SELECT COUNT(*) FROM home_transaction d WHERE d.c_stat='A' AND d.c_statusbooking='A' AND d.id_kos = a.id_kos) AS i_sisa, " +
                "0 AS total_rating, 0 AS count_user ";

        ckAllFasilitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ckAllFasilitas.isChecked()){
                    eFasilitas.setText("");
                    eFasilitas.setEnabled(false);
                }else{
                    eFasilitas.setEnabled(true);
                }
            }
        });

        ckAllHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ckAllHarga.isChecked()){
                    eMulai.setText("0");
                    eSampai.setText("0");
                    eMulai.setEnabled(false);
                    eSampai.setEnabled(false);
                }else{
                    eMulai.setEnabled(true);
                    eSampai.setEnabled(true);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear.isShown()) {
                    Anim.slide_up(SearchKriteriaKos.this, linear);
                    linear.setVisibility(View.GONE);
                } else {
                    Anim.slide_down(SearchKriteriaKos.this, linear);
                    linear.setVisibility(View.VISIBLE);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a="";
                String statusSewa = String.valueOf(spTipe.getSelectedItem());
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT a.id_kos, a.i_idcust, b.vc_namalengkap, b.c_telp, b.c_email , a.i_idsewastatus, ")
                        .append("a.vc_namakost, a.vc_alamat, a.c_kodekota, c.vc_namakota, c.d_lat AS lat_kota, c.d_lng AS long_kota, ")
                        .append("a.vc_gambar, a.i_lebar, a.vc_gambar2, a.vc_gambar3, a.vc_gambar4, a.vc_gambar5, ")
                        .append("a.i_panjang, a.c_statuslistrik, a.i_jmlkamar, a.t_fasilitas, a.d_latitude, a.d_longtitude, a.n_harga, ")
                        .append(querySisa)
                        .append("FROM home_detail a ")
                        .append("JOIN cust_account b ON a.i_idcust = b.i_idcust ")
                        .append("LEFT OUTER JOIN city c ON a.c_kodekota=c.c_kodekota ")
                        .append("WHERE a.i_idsewastatus = ")
                        .append(statusSewa.equals("Man Only")?"1 ":statusSewa.equals("Women Only")?"2 ":"3 ");
                if(!ckAllHarga.isChecked()){
                    sb.append("AND a.n_harga >= "+Integer.valueOf(eMulai.getText().toString())+" AND a.n_harga <= "+Integer.valueOf(eSampai.getText().toString())+" ");
                }
                //.append(!ckAllKota.isChecked()?"AND a.c_kodekota = '"+kodeKota+"' ":"");
                if(!ckAllFasilitas.isChecked()){
                    sb.append("AND");
                    String fasilitas = eFasilitas.getText().toString();
                    List<String> listFasilitas = Arrays.asList(fasilitas.split(","));
                    for(String key:listFasilitas){
                        sb.append(" a.t_fasilitas LIKE '%"+key.trim()+"%' OR");
                    }
                    a= sb.toString().substring(0, sb.toString().length()-2);
                    //sb.append(") ");
                }else{
                    a=sb.toString();
                }
                a = a + " ORDER BY a.id_kos ASC";

                getDataUpload(Link.FilePHP+getData, a);
            }
        });

        lsvlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchKriteriaKos.this, InfoKos.class);
                i.putExtra("id_kos", columnlist.get(position).getId_kos());
                i.putExtra("idUser", idUser);
                i.putExtra("i_idcust", columnlist.get(position).getId_cust());
                i.putExtra("namaCust", columnlist.get(position).getNamaCust());
                i.putExtra("i_jmlkamar", columnlist.get(position).getJmlhKamar());
                i.putExtra("vc_alamat", columnlist.get(position).getAlamat());
                i.putExtra("vc_namakost", columnlist.get(position).getNamaKos());
                i.putExtra("i_lebar", columnlist.get(position).getLebar());
                i.putExtra("i_panjang", columnlist.get(position).getPanjang());
                i.putExtra("c_statuslistrik", columnlist.get(position).getStatusListrik().equals("Y")?"Include":"Exclude");
                i.putExtra("i_idsewastatus", columnlist.get(position).getId_sewaStatus()==1?"Man Only":
                        columnlist.get(position).getId_sewaStatus()==2?"Women Only":"All");
                i.putExtra("tlpCust", columnlist.get(position).getTlpCust());
                i.putExtra("fasilitas", columnlist.get(position).getFasilitas());
                i.putExtra("lat", columnlist.get(position).getLatitude());
                i.putExtra("longt", columnlist.get(position).getLongtitude());
                i.putExtra("harga", "Rp. "+rupiah.format(columnlist.get(position).getHarga()));
                i.putExtra("hargaAsli", columnlist.get(position).getHarga());
                i.putExtra("gambar", columnlist.get(position).getGambar());
                i.putExtra("gambar2", columnlist.get(position).getGambar2());
                i.putExtra("gambar3", columnlist.get(position).getGambar3());
                i.putExtra("gambar4", columnlist.get(position).getGambar4());
                i.putExtra("gambar5", columnlist.get(position).getGambar5());
                i.putExtra("c_kodekota", columnlist.get(position).getKodeKota());
                i.putExtra("i_sisa", columnlist.get(position).getSisa());
                i.putExtra("rating", columnlist.get(position).getRating());
                i.putExtra("countUser", columnlist.get(position).getCountUser());
                startActivity(i);
            }
        });

        imgFasilitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFasilitas();
            }
        });
    }

    private void getDataUpload(String Url, final String sql){
        pDialog.setMessage("Harap Tunggu...");
        showDialog();
        tvstatus.setVisibility(View.GONE);
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){
                                tvstatus.setVisibility(View.GONE);
                                adapter.clear();
                                JSONArray JsonArray = response.getJSONArray("kriteriaKos");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    ColHomeDetail colums 	= new ColHomeDetail();
                                    colums.setId_kos(object.getInt("id_kos"));
                                    colums.setId_cust(object.getInt("id_cust"));
                                    colums.setId_sewaStatus(object.getInt("id_sewaStatus"));
                                    colums.setNamaKos(object.getString("namaKos"));
                                    colums.setAlamat(object.getString("alamat"));
                                    colums.setGambar(object.getString("gambar"));
                                    colums.setLebar(object.getInt("lebar"));
                                    colums.setPanjang(object.getInt("panjang"));
                                    colums.setStatusListrik(object.getString("statusListrik"));
                                    colums.setJmlhKamar(object.getInt("jmlhKamar"));
                                    colums.setFasilitas(object.getString("fasilitas"));
                                    colums.setLatitude(object.getDouble("latitude"));
                                    colums.setLongtitude(object.getDouble("longtitude"));
                                    colums.setHarga(object.getInt("harga"));
                                    colums.setNamaCust(object.getString("namaCust"));
                                    colums.setTlpCust(object.getString("tlpCust"));
                                    colums.setEmailCust(object.getString("emailCust"));
                                    colums.setGambar2(object.getString("gambar2"));
                                    colums.setGambar3(object.getString("gambar3"));
                                    colums.setGambar4(object.getString("gambar4"));
                                    colums.setGambar5(object.getString("gambar5"));
                                    colums.setKodeKota(object.getString("kodeKota"));
                                    colums.setSisa(object.getInt("sisa"));
                                    colums.setRating(object.getDouble("total_rating"));
                                    colums.setCountUser(object.getInt("count_user"));
                                    // list gmbar bl
                                    columnlist.add(colums);
                                }
                            }else{
                                tvstatus.setVisibility(View.VISIBLE);
                                tvstatus.setText("Tidak Ada Data");
                            }
                            hideDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check Koneksi Internet Anda");
                } else if (error instanceof AuthFailureError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("AuthFailureError");
                } else if (error instanceof ServerError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check ServerError");
                } else if (error instanceof NetworkError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check NetworkError");
                } else if (error instanceof ParseError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText(error.toString());
                }
                hideDialog();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("querysql", sql);
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void dialogFasilitas(){
        seletedItems = new ArrayList<ColFasilitas>();
        AlertDialog dialog = new AlertDialog.Builder(SearchKriteriaKos.this)
                .setTitle("Pilih Fasilitas")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(mArrayList.get(indexSelected));
                        } else {
                            // Else, if the item is already in the array, remove it
                            for(ColFasilitas entity:seletedItems){
                                ColFasilitas klik = mArrayList.get(indexSelected);
                                if(entity.getKodeFasilitas().equals(klik.getKodeFasilitas())){
                                    seletedItems.remove(entity);
                                    break;
                                }
                            }
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        eFasilitas.setText("");
                        if(!seletedItems.isEmpty()){
                            daftarKodeFasilitas=null;
                            daftarNamaFasilitas=null;
                            StringBuilder sbKode = new StringBuilder();
                            StringBuilder sbNama = new StringBuilder();
                            for(ColFasilitas entity : seletedItems){
                                sbKode.append(entity.getKodeFasilitas()).append(",");
                                sbNama.append(entity.getNamaFasilitas()).append(",");
                            }
                            daftarKodeFasilitas=sbKode.toString().substring(0, sbKode.toString().length() -1);
                            daftarNamaFasilitas=sbNama.toString().substring(0, sbNama.toString().length() -1);
                            eFasilitas.setText(daftarNamaFasilitas);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == 7) {//kota
            if(resultCode == RESULT_OK) {
                kodeKota = data.getStringExtra("kodeKota");
                namaKota = data.getStringExtra("namaKota");
                eKota.setText(namaKota);
            }
        }*/
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void loadFasilitas(){
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
                for(ColFasilitas entity : mArrayList){
                    charSequences.add(new String("("+entity.getKodeFasilitas()+") "+entity.getNamaFasilitas()));
                }
                items = charSequences.toArray(new CharSequence[charSequences.size()]);
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
