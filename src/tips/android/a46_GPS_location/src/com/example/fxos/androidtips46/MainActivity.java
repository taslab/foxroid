package com.example.fxos.androidtips46;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
	private static final long LOCATION_CHECK_DELAY = 3000;
	//保存間隔
	private static final long SAVE_LOCATION_DELAY = 1000 * 60 * 10;
	private static final String LAST_SAVE_TIME = "LAST_SAVE_TIME";
	private static final String SAVE_FILE_NAME = "location_log.txt";
	
	private long lastSaveTime;
	private double lastLatitude = 0.0F;
	private double lastLongitude = 0.0F;
	private boolean saveThreadFlag;
	private Handler handler = new Handler();
	private SharedPreferences prefs;

	// ロケーションマネージャ
    private LocationManager mLocationManager;
    private TextView txtGpsState;
	
	private Runnable saveLocationRunable = new Runnable() {
		@Override
		public void run() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			if(saveThreadFlag) {
				if(mLocationManager!= null && lastLatitude != 0.0F && lastLongitude != 0.0F) {
					long current = System.currentTimeMillis();
					//一定時間ごとに位置情報を保存する
					if(current - lastSaveTime >= SAVE_LOCATION_DELAY) {
						//保存日時を保持
						lastSaveTime = current;
						
			            //SDカードの状態をチェック
			            String state = Environment.getExternalStorageState();
			            //SDカードが使用可能
			            if(state.equals(Environment.MEDIA_MOUNTED)) {
			                FileWriter fw;
							try {
								String saveStr = format.format(new Date(current)) + " lat:" + lastLatitude + " long:" + lastLongitude + "\n";
								//現在の座標を書き込む
								fw = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SAVE_FILE_NAME, true);
				                BufferedWriter bw = new BufferedWriter(fw);
				                bw.write(saveStr);
				                bw.close();
				                fw.close();

				                //保存した日時を記録
								prefs.edit().putLong(LAST_SAVE_TIME, lastSaveTime).commit();
				                Toast.makeText(MainActivity.this,getResources().getString(R.string.save_location_message, saveStr), Toast.LENGTH_LONG).show();
							} catch (IOException e) {
								e.printStackTrace();
							}
			            //SDカードが使用不可ならエラーを出力
			            } else {
			                Toast.makeText(MainActivity.this, R.string.err_sdcard_not_available, Toast.LENGTH_LONG).show();
			            }
					}
				}
				//指定時間ごとにチェック
				handler.postDelayed(saveLocationRunable, 100);
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getPreferences(MODE_PRIVATE);

        //最後に位置情報を保存した時間を取得
        lastSaveTime = prefs.getLong(LAST_SAVE_TIME, 0);

        //GPSの状態を示すテキスト
        txtGpsState = (TextView) findViewById(R.id.txt_gps_state);

        //ロケーションマネージャーを初期化
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	//GPSが使用可になってない場合、警告を出して設定画面へ遷移させる
    	if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		txtGpsState.setText(R.string.label_gps_disabled);

    		//ダイアログを設定して表示
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this); 
    		dialog.setTitle(R.string.err_no_setting_title)
    		.setMessage(R.string.err_no_setting_message)
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						//ロケーション設定画面へ飛ばす
						startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
					} catch(Exception e) {
					}
				}
			})
			.show();
    	//GPSが使用できる場合、位置情報を取得
    	} else {
    		txtGpsState.setText(R.string.label_getting_location);
    		
    		//位置情報監視を開始
    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_CHECK_DELAY, 0, this);
    		
    		saveThreadFlag = true;
    		//位置情報保存スレッドを開始
    		handler.post(saveLocationRunable);
    	}
    }
    @Override
    protected void onPause() {
    	super.onPause();

    	//保存処理を停止
    	saveThreadFlag = false;

    	//位置情報の監視処理を停止
    	if(mLocationManager != null) {
        	mLocationManager.removeUpdates(this);
    	}
    }
	@Override
	public void onLocationChanged(Location location) {
		//取得した位置情報を保持
		lastLatitude = location.getLatitude();
		lastLongitude = location.getLongitude();
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
