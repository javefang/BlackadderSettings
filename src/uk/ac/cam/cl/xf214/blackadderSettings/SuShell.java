package uk.ac.cam.cl.xf214.blackadderSettings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

public class SuShell extends Thread {
	public static final String TAG = "SuShell";
	public static final String BA_SETTING_START_FLAG = "==BA_SETTING_START==";
	public static final String BA_SETTING_COMPLETE_FLAG = "==BA_SETTING_COMPLETE==";
	
	private Process p;
	private DataOutputStream os;
	private DataInputStream is;
	private BlockingQueue<String> outputBuf;
	private boolean finished;
	private Thread echoThread;
	
	private Object cmdMon;
	private boolean processing = true;
	
	public SuShell() throws IOException {
		p = Runtime.getRuntime().exec("su");
		os = new DataOutputStream(p.getOutputStream());
		is = new DataInputStream(p.getInputStream());
		
		outputBuf = new LinkedBlockingQueue<String>();
		cmdMon = new Object();
		
		echoThread = new Thread(new Runnable() {
			public void run() {
				Log.i(TAG, "Echo thread started!");
				while (!finished) {
					String line;
					try {
						line = is.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (line != null) {
						Log.i(TAG, "readLine(): " + line);
						if (line.endsWith(BA_SETTING_COMPLETE_FLAG)) {
							//Log.i(TAG, "Command complete!");
							synchronized(cmdMon) {
								processing = false;
								cmdMon.notifyAll();	// notify all waiting thread
							}
						} else {
							try {
								outputBuf.put(line);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	// add output to buffer
						}
					}
				}
			}
		});
		echoThread.start();
	}
	
	private void exec(String cmd) throws IOException {
		Log.i(TAG, "exec(): " + cmd);
		processing = true;	// set start processing
		outputBuf.clear();	// clear buffer on start
		
		os.writeBytes(cmd + "\n");
		os.writeBytes("echo " + BA_SETTING_COMPLETE_FLAG + "\n");
		os.flush();
	}
	
	public Vector<String> execSync(String cmd) throws IOException, InterruptedException {
		exec(cmd);
		waitFor();
		return getOutput();
	}
	
	
	public void waitFor() throws InterruptedException {
		if (processing) {
			synchronized(cmdMon) {
				Log.i(TAG, "Waiting for output");
				cmdMon.wait();
			}
		}
	}
	
	public Vector<String> getOutput() {
		Vector<String> echos = new Vector<String>();
		outputBuf.drainTo(echos);
		Log.i(TAG, "Output line = " + echos.size());
		return echos;
	}
	
	public void finish() {
		if (!finished) {
			finished = true;
			p.destroy();
			interrupt();
		}
	}
}
