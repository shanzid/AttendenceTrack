package com.attendance.tracker.activity.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.attendance.tracker.MainActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.company.CompanyActivity;
import com.attendance.tracker.activity.master.MasterActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.activity.user.MapTestUserActivity;
import com.attendance.tracker.activity.user.UserMainActivity;
import com.attendance.tracker.agent.AgentDashboardActivity;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    AppSessionManager appSessionManager;
    AppCompatImageView map,man;
    TextView wlcome;
    Animation top, bottom,slideup,leftto_right;
    private static int SPLASH_SCREEN = 2000;
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        appSessionManager = new AppSessionManager(this);
        initView();
        loadContent();
    }

    private void initView() {
        map=findViewById(R.id.mapImg);
        man=findViewById(R.id.man);
        top = AnimationUtils.loadAnimation(this, R.anim.bottom);
        bottom = AnimationUtils.loadAnimation(this, R.anim.top);
        slideup = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        leftto_right = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        man.setAnimation(slideup);
        map.setAnimation(leftto_right);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data !=null){

        }
    }

    private void loadContent() {

            Timer runtimer = new Timer();
            TimerTask showSplash = new TimerTask() {
                @Override
                public void run() {
                    //finish();
                    if (appSessionManager.isLoggedIn()) {
                        // company master admin
                        if (appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY).equals("3")){
                            Intent intent = new Intent(SplashActivity.this, MasterActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY).equals("2")){
                            // company
                            Intent intent = new Intent(SplashActivity.this, CompanyActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY).equals("4")){
                            // company
                            Intent intent = new Intent(SplashActivity.this, AgentDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY).equals("1")){
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
/*                            Intent intent = new Intent(SplashActivity.this, UserMainActivity.class);
                            startActivity(intent);
                            finish();*/

                            Intent mIntent = new Intent(SplashActivity.this, MapTestUserActivity.class);
                            // mIntent.putExtra("userId",appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
                            startActivity(mIntent);
                        }

                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                       // overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
            };
            runtimer.schedule(showSplash, 3500);

    }
}