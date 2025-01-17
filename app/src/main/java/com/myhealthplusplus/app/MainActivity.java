package com.myhealthplusplus.app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.myhealthplusplus.app.CheckIn.CheckInHome;
import com.myhealthplusplus.app.LoginSignup.SignIn;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    String str_active, str_recovered, str_death;
    private TextView tv_active, tv_recovered, tv_death, nameOnDashboard;
    private ProgressDialog p_bar;
    private PieChart pieChart;
    Button m_Global, m_sri_lanka;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    int i = 1, isAlertBoxAlreadyRunning = 0;
    ImageView menu_btn, profilePictureView;
    static final float END_SCALE = 0.7f;
    LinearLayout moving_content;
    FirebaseAuth mAuth;
    private long backPressedTime;
    CardView profileColorRing;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.dark_black));

        if (!isConnected(this)) {
            showInternetDialog();
        }
        Init();
        mAuth = FirebaseAuth.getInstance();

        FetchData();
        getDataFromSharedPreferences();

        int refreshCycleColor = Color.parseColor("#c01722");
        swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        moving_content = findViewById(R.id.moving_content);

        navigationDrawer();

        m_Global = findViewById(R.id.main_btnGlobal);
        m_Global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 2;
                Init();
                FetchData();
            }
        });

        profileColorRing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewProfile.class);
                startActivity(intent);
            }
        });

        m_sri_lanka = findViewById(R.id.main_btn_sri_lanka);
        m_sri_lanka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 1;
                Init();
                FetchData();
            }
        });

        Button knowMore = findViewById(R.id.btnKnowMore);
        knowMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.epid.gov.lk/web/index.php?option=com_content&view=article&id=228&lang=en";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button callHotline = findViewById(R.id.callHotline);
        callHotline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1999"));
                startActivity(intent);
            }
        });

        Button showMore = findViewById(R.id.btnShowMore);
        showMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, DetailedView_COVID.class);
                startActivity(intent1);
            }
        });

        Button know_more = findViewById(R.id.know_if_your_sick_more);
        know_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IfYoureFeelingSick.class);
                startActivity(intent);
            }
        });

        Button view_all = findViewById(R.id.btnViewAll);
        view_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, News_Main.class);
                startActivity(intent);
            }
        });

        Button proceed = findViewById(R.id.btnsi_proceed);
        proceed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IsolationCountDown.class);
                startActivity(intent);
            }
        });

        Button getVacc = findViewById(R.id.btngetVaccinated);
        getVacc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GetVaccined.class);
                startActivity(intent);
            }
        });

        Button pcrTEST = findViewById(R.id.btnPcrTest);
        pcrTEST.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GetYourPcrTest.class);
                startActivity(intent);
            }
        });

        Button checkIn = findViewById(R.id.btnCheckIn);
        checkIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CheckInHome.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataFromSharedPreferences() {
        String fullName, profilePicture;
        boolean isGoogle;

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        isGoogle = getIntent().getBooleanExtra("GOOGLE", false);
        Log.d(TAG, "onCreate: "+isGoogle);

        if (!isGoogle) {
            fullName = preferences.getString("fullName", "");
            profilePicture = preferences.getString("userPhoto", "");
        }
        else {
            fullName = getIntent().getStringExtra("NAME");
            profilePicture = getIntent().getStringExtra("PHOTO");
        }

        nameOnDashboard.setText(fullName+" \uD83D\uDC4B");
        Glide.with(this).load(profilePicture).into(profilePictureView);
        Log.d(TAG, "onCreate: "+fullName+" " +profilePicture);
    }

    private void navigationDrawer() {

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        menu_btn = findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        drawerLayout.setScrimColor(getResources().getColor(R.color.red_pie));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                moving_content.setScaleX(offsetScale);
                moving_content.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = moving_content.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                moving_content.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else{
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finishAffinity();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    private void showInternetDialog() {

        ShowDialog(this);

        isAlertBoxAlreadyRunning = 1;

        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_no_connection, findViewById(R.id.no_connection_layout));
                builder.setCancelable(false);
                builder.setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                view.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isConnected(MainActivity.this)) {
                            alertDialog.dismiss();
                            showInternetDialog();
                        } else {
                            isAlertBoxAlreadyRunning = 0;
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                });

                alertDialog.show();

                DismissDialog();
            }
        }, 1000);
    }

    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    public void ShowDialog(Context context) {
        p_bar = new ProgressDialog(context);
        p_bar.show();
        p_bar.setContentView(R.layout.activity_p_bar);
        p_bar.setCanceledOnTouchOutside(false);
        p_bar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void DismissDialog() {
        p_bar.dismiss();
    }

    private void Init() {
        tv_active = findViewById(R.id.main_active_num_txt);
        tv_recovered = findViewById(R.id.main_recovered_num_txt);
        tv_death = findViewById(R.id.main_deaths_num_txt);
        pieChart = findViewById(R.id.main_piechart);
        swipeRefreshLayout = findViewById(R.id.main_refresh);
        nameOnDashboard = findViewById(R.id.main_name);
        profilePictureView = findViewById(R.id.main_profile_picture);
        profileColorRing = findViewById(R.id.main_color_ring);
    }

    private void FetchData() {

        if (isConnected(this)) {

            ShowDialog(this);

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String apiUrl = "https://disease.sh/v3/covid-19/all";
            Button btn1 = findViewById(R.id.main_btn_sri_lanka);
            Button btn2 = findViewById(R.id.main_btnGlobal);
            View d_sri_lanka_view = findViewById(R.id.main_btn_sri_lanka_view);
            View d_Global_view = findViewById(R.id.main_btnGlobal_view);

            if (i == 1) {
                if (btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)) {
                    btn1.setTextColor(getResources().getColor(R.color.white));
                    btn2.setTextColor(getResources().getColor(R.color.gray));
                    d_sri_lanka_view.setBackgroundResource(R.color.red_pie);
                    d_Global_view.setBackgroundResource(R.color.gray);
                }

                apiUrl = "https://disease.sh/v3/covid-19/countries/lk";
            } else {
                if (btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)) {
                    btn2.setTextColor(getResources().getColor(R.color.white));
                    btn1.setTextColor(getResources().getColor(R.color.gray));
                    d_Global_view.setBackgroundResource(R.color.red_pie);
                    d_sri_lanka_view.setBackgroundResource(R.color.gray);
                }

                apiUrl = "https://disease.sh/v3/covid-19/all";
            }

            pieChart.clearChart();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    apiUrl,
                    null,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                str_active = response.getString("active");
                                str_recovered = response.getString("recovered");
                                str_death = response.getString("deaths");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Handler delay = new Handler();
                            delay.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    tv_active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));
                                    tv_recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                                    tv_death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));

                                    pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(str_active), Color.parseColor("#007afe")));
                                    pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(str_recovered), Color.parseColor("#08a045")));
                                    pieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(str_death), Color.parseColor("#F6404F")));

                                    pieChart.startAnimation();

                                    DismissDialog();

                                }
                            }, 1000);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);

        } else {
            if (isAlertBoxAlreadyRunning == 1) {

            } else {
                showInternetDialog();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_settings:
                Intent i1 = new Intent(MainActivity.this, Settings.class);
                startActivity(i1);
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_token_scanner:
                Intent i3 = new Intent(MainActivity.this, VaccineTokenScan.class);
                startActivity(i3);
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Intent i2 = new Intent(MainActivity.this, SignIn.class);
                startActivity(i2);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}