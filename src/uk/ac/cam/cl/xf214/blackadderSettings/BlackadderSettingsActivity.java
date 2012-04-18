package uk.ac.cam.cl.xf214.blackadderSettings;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BlackadderSettingsActivity extends Activity {
	public static final String TAG = "BlackadderSettingsActivity";
	public static final String DEFAULT_CONF_FILE = "/sdcard/blackadder/00000003.conf";
	
	private Process p;
	private DataOutputStream os;
	private String confFilePath;
	private TextView lblCurConfFile;
	private EditText confFileInput;
	private CheckBox baSwitch; 
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        requestSu();	// request superuser permission
        initBASwitch();
    }
    
    private void initBASwitch() {
    	lblCurConfFile = (TextView)findViewById(R.id.lbl_cur_conf_file);
    	confFileInput = (EditText)findViewById(R.id.input_conf_file);
    	confFileInput.setText(DEFAULT_CONF_FILE);
    	setConfFilePath(DEFAULT_CONF_FILE);
    	
    	confFileInput.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null) {
					setConfFilePath(confFileInput.getText().toString());
					return true;
				} else {
					return false;
				}
			}
    	});
    	
    	baSwitch = (CheckBox)findViewById(R.id.chk_ba_switch);
    	baSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startBlackadder();
				} else {
					killBlackadder();
				}
			}
    	});
    }
    
    private void requestSu() {
    	try {
			p = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(p.getOutputStream());
			
			// environment init
			exec("LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/data/click/lib/");	// set library path
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private void startBlackadder() {
		exec("/data/click/bin/click " + confFilePath + "&");
		toastMessage("Blackadder started!");
    }
    
    private void killBlackadder() {
    	exec("killall click");
    	toastMessage("Blackadder stopped!");
    }
    
    private void setConfFilePath(String path) {
    	confFilePath = path;
    	lblCurConfFile.setText(path);
    }
    
    private void exec(String cmd) {
    	if (os != null) {
    		try {
    			debug("Executing: " + cmd);
				os.writeBytes(cmd + "\n");
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private void debug(String msg) {
    	Log.i(TAG, msg);
    }
    
    private void toastMessage(String msg) {
    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}