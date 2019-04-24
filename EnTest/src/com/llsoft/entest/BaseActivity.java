package com.llsoft.entest;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;

public class BaseActivity extends Activity {
	
	@Override
    public Resources getResources() {
        Resources res = super.getResources();  
        Configuration config=new Configuration();  
        config.setToDefaults();  
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}
