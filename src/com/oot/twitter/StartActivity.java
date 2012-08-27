package com.oot.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

public class StartActivity extends Activity implements OnClickListener{
    private Handler handler;
    LinearLayout background,signup,signin;
    Drawable  d1,d2,d3;
    int i = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitter_start_layout);
        
        background = (LinearLayout)findViewById(R.id.start_background);

        Resources res = getResources();
        d1 = res.getDrawable(R.drawable.signed_out_3);
        //d2 = res.getDrawable(R.drawable.signed_out_2);
       // d3 = res.getDrawable(R.drawable.signed_out_3);
        
        background.setBackgroundDrawable(d1);
        
        signup = (LinearLayout)findViewById(R.id.btnSignUp);
        signup.setOnClickListener(this);
        
        signin = (LinearLayout)findViewById(R.id.btnSignIn);
        signin.setOnClickListener(this);
        
       // handler = new Handler();
		//handler.postDelayed(runnable, 0);
        
    }
    private final Runnable runnable = new Runnable() {
        public void run() {
        	if(i == 1)
        		background.setBackgroundDrawable(d1);
        	if(i == 2)
        		background.setBackgroundDrawable(d2);
        	if(i == 3)
        		background.setBackgroundDrawable(d3);
        	i++;
        	if(i > 3)
        		i = 1;
            handler.postDelayed(runnable, 10000);
        }
    };
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnSignUp:
				Intent i = new Intent(StartActivity.this, SignUp.class);
				startActivity(i);
				break;
			case R.id.btnSignIn:
				Intent i1 = new Intent(StartActivity.this, SignIn.class);
				startActivityForResult(i1,0);
				break;
		
		}
		
	}    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode == 2){
	        finish();
	    }
	}	
}
