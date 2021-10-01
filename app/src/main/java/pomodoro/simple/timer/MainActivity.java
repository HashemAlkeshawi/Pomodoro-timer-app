package pomodoro.simple.timer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

import static android.service.controls.ControlsProviderService.TAG;

public class MainActivity extends AppCompatActivity {

    TextView mMinutesTimeDown , mRunning, mSecondsTimeDown;
    ImageButton mStartButton;
    MediaPlayer aFirstRound, aSecondRound, aThirdRound, aStartPomodoro, aLongBreak, aLongBreakOver;
    Button mSkipBreak, mReset;

    CountDownTimer cdPomodoro, cdBreak, cdLongBreak;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean isPomodoroRun, isLongBreakRun, isBreakRun;

    AdView mAdView;
    InterstitialAd mInterstitialAd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5014169182389580/5722883515", adRequest1, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });





        mSecondsTimeDown = findViewById(R.id.Seconds_timeDown);
        mMinutesTimeDown = findViewById(R.id.Minutes_timeDown);
        mStartButton = findViewById(R.id.pomoButton);
        mReset = findViewById(R.id.resetButton);

        aFirstRound = MediaPlayer.create(this, R.raw.first_round);
        aSecondRound  = MediaPlayer.create(this, R.raw.second_round);
        aThirdRound = MediaPlayer.create(this,R.raw.third_round);
        aStartPomodoro = MediaPlayer.create(this, R.raw.start_pomodoro);
        aLongBreak = MediaPlayer.create(this, R.raw.long_break);
        aLongBreakOver = MediaPlayer.create(this, R.raw.long_break_over);

        mSkipBreak = findViewById(R.id.Skip);
        sharedPreferences = getSharedPreferences("settings", 0);
        mRunning = findViewById(R.id.label);


        editor = sharedPreferences.edit();




        resetTimers();

        startPomodoro();





    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.reset:
                editor.putInt("count", 0);
                editor.commit();
                mRunning.setText("Round "+((sharedPreferences.getInt("count",0))+1));

        }
        return super.onOptionsItemSelected(item);
    }

    public void resetTimers(){
        mSecondsTimeDown.setText(""+ (sharedPreferences.getInt("pomodoroPeriod",1500)% 60));
        mMinutesTimeDown.setText(""+ (sharedPreferences.getInt("pomodoroPeriod",1500)/ 60));
    }

    public void startPomodoro(){

        mStartButton.setVisibility(View.VISIBLE);
        mSkipBreak.setVisibility(View.GONE);
        findViewById(R.id.mainLayout).setBackground(getDrawable((R.color.tomato_background)));
        mRunning.setText("Round "+((sharedPreferences.getInt("count",0))+1));




         cdPomodoro = new CountDownTimer(((sharedPreferences.getInt("pomodoroPeriod", 1500)) * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                mMinutesTimeDown.setText(""+((millisUntilFinished / 1000) / 60));
                mSecondsTimeDown.setText(""+((millisUntilFinished/ 1000) % 60));
                //here you can have your logic to set text to edittext
            }





            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFinish() {
                // do when the timer finish

                // start the audio
                switch(sharedPreferences.getInt("count",0)){
                    case 0: aFirstRound.start();
                    break;

                    case 1: aSecondRound.start();
                    break;

                    case 2: aThirdRound.start();
                    break;

                    case 3: aLongBreak.start();
                    break;

                }

                mReset.setVisibility(View.GONE);
                mStartButton.setVisibility(View.VISIBLE);
                resetTimers();



                // do the vibration
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }

                isPomodoroRun = false;

                int getCount = sharedPreferences.getInt("count", 0);

                if(getCount < 3){
                    startBreak();
                }else {
                    startLongBreak();
                    Snackbar.make((View) findViewById(R.id.mainLayout),"Great work, you've finished a whole sit of pomodoro! ",9000)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                }

            }

        };
         if(sharedPreferences.getInt("count",0) > 0 && sharedPreferences.getBoolean("AutoStart", false)){
             mStartButton.setVisibility(View.GONE);
             mReset.setVisibility(View.VISIBLE);
             isPomodoroRun = true;
             cdPomodoro.start();
         }


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setVisibility(View.GONE);
                mReset.setVisibility(View.VISIBLE);
                isPomodoroRun = true;
                cdPomodoro.start();


            }
        });
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setVisibility(View.VISIBLE);
                mReset.setVisibility(View.GONE);

                if(isPomodoroRun){
                    cdPomodoro.cancel();
                    isPomodoroRun = false;
                }
                resetTimers();
            }
        });

        mSkipBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBreakRun){
                    cdBreak.cancel();
                    isBreakRun = false;
                    int getCount = sharedPreferences.getInt("count", 0);
                    getCount++;
                    editor.putInt("count", getCount);
                    editor.commit();
                }
                if(isLongBreakRun){
                    cdLongBreak.cancel();
                    isLongBreakRun = false;
                    editor.putInt("count", 0);
                    editor.commit();
                }
                mSkipBreak.setVisibility(View.GONE);
                resetTimers();

                startPomodoro();
            }
        });


    }


    public void startLongBreak(){

        findViewById(R.id.mainLayout).setBackground(getDrawable(R.color.cucumber_background));
        mRunning.setText("Long Break");
        mStartButton.setVisibility(View.GONE);
        mReset.setVisibility(View.GONE);
        mSkipBreak.setVisibility(View.VISIBLE);



         cdLongBreak = new CountDownTimer(( (sharedPreferences.getInt("LongBreakPeriod", 300)) * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                mMinutesTimeDown.setText(""+((millisUntilFinished / 1000) / 60));
                mSecondsTimeDown.setText(""+((millisUntilFinished/ 1000) % 60));
                //here you can have your logic to set text to edittext
            }





            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFinish() {
                // do when the timer finish

                aLongBreakOver.start();

                mStartButton.setVisibility(View.VISIBLE);
                resetTimers();



                // do the vibration
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }

                editor.putInt("count", 0);
                editor.commit();

                isLongBreakRun = false;

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }


                startPomodoro();

            }

        };
        isLongBreakRun = true;
        cdLongBreak.start();


    }

    public void startBreak(){

        findViewById(R.id.mainLayout).setBackground(getDrawable(R.color.cucumber_background));
        mRunning.setText("Short Break");

        mStartButton.setVisibility(View.GONE);
        mReset.setVisibility(View.GONE);
        mSkipBreak.setVisibility(View.VISIBLE);



        cdBreak = new CountDownTimer(( (sharedPreferences.getInt("BreakPeriod", 300)) * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                mMinutesTimeDown.setText(""+((millisUntilFinished / 1000) / 60));
                mSecondsTimeDown.setText(""+((millisUntilFinished/ 1000) % 60));
                //here you can have your logic to set text to edittext
            }



            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFinish() {
                // do when the timer finish

                // start the audio
                aStartPomodoro.start();


                mStartButton.setVisibility(View.VISIBLE);
                resetTimers();



                // do the vibration
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                int getCount = sharedPreferences.getInt("count", 0);
                getCount++;
                editor.putInt("count", getCount);
                editor.commit();


                isBreakRun = false;
                startPomodoro();

            }

        };
        isBreakRun = true;
        cdBreak.start();


    }


}