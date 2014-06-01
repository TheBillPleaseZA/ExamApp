package cosc490.yahooSportsNHL.newsfeed.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cosc490.yahooSportsNHL.newsfeed.R;


// Code taken from www.AndroidHive.info
public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        new Handler().postDelayed(new Runnable() {
 
            public void run() {
                Intent i = new Intent(SplashScreen.this, YahooMainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
