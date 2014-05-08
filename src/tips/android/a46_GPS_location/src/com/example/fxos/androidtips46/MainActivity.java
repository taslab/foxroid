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
	//�ۑ��Ԋu
	private static final long SAVE_LOCATION_DELAY = 1000 * 60 * 10;
	private static final String LAST_SAVE_TIME = "LAST_SAVE_TIME";
	private static final String SAVE_FILE_NAME = "location_log.txt";
	
	private long lastSaveTime;
	private double lastLatitude = 0.0F;
	private double lastLongitude = 0.0F;
	private boolean saveThreadFlag;
	private Handler handler = new Handler();
	private SharedPreferences prefs;

	// ���P�[�V�����}�l�[�W��
    private LocationManager mLocationManager;
    private TextView txtGpsState;
	
	private Runnable saveLocationRunable = new Runnable() {
		@Override
		public void run() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			if(saveThreadFlag) {
				if(mLocationManager!= null && lastLatitude != 0.0F && lastLongitude != 0.0F) {
					long current = System.currentTimeMillis();
					//��莞�Ԃ��ƂɈʒu����ۑ�����
					if(current - lastSaveTime >= SAVE_LOCATION_DELAY) {
						//�ۑ�������ێ�
						lastSaveTime = current;
						
			            //SD�J�[�h�̏�Ԃ��`�F�b�N
			            String state = Environment.getExternalStorageState();
			            //SD�J�[�h���g�p�\
			            if(state.equals(Environment.MEDIA_MOUNTED)) {
			                FileWriter fw;
							try {
								String saveStr = format.format(new Date(current)) + " lat:" + lastLatitude + " long:" + lastLongitude + "\n";
								//���݂̍��W����������
								fw = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SAVE_FILE_NAME, true);
				                BufferedWriter bw = new BufferedWriter(fw);
				                bw.write(saveStr);
				                bw.close();
				                fw.close();

				                //�ۑ������������L�^
								prefs.edit().putLong(LAST_SAVE_TIME, lastSaveTime).commit();
				                Toast.makeText(MainActivity.this,getResources().getString(R.string.save_location_message, saveStr), Toast.LENGTH_LONG).show();
							} catch (IOException e) {
								e.printStackTrace();
							}
			            //SD�J�[�h���g�p�s�Ȃ�G���[���o��
			            } else {
			                Toast.makeText(MainActivity.this, R.string.err_sdcard_not_available, Toast.LENGTH_LONG).show();
			            }
					}
				}
				//�w�莞�Ԃ��ƂɃ`�F�b�N
				handler.postDelayed(saveLocationRunable, 100);
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getPreferences(MODE_PRIVATE);

        //�Ō�Ɉʒu����ۑ��������Ԃ��擾
        lastSaveTime = prefs.getLong(LAST_SAVE_TIME, 0);

        //GPS�̏�Ԃ������e�L�X�g
        txtGpsState = (TextView) findViewById(R.id.txt_gps_state);

        //���P�[�V�����}�l�[�W���[��������
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	//GPS���g�p�ɂȂ��ĂȂ��ꍇ�A�x�����o���Đݒ��ʂ֑J�ڂ�����
    	if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		txtGpsState.setText(R.string.label_gps_disabled);

    		//�_�C�A���O��ݒ肵�ĕ\��
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this); 
    		dialog.setTitle(R.string.err_no_setting_title)
    		.setMessage(R.string.err_no_setting_message)
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						//���P�[�V�����ݒ��ʂ֔�΂�
						startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
					} catch(Exception e) {
					}
				}
			})
			.show();
    	//GPS���g�p�ł���ꍇ�A�ʒu�����擾
    	} else {
    		txtGpsState.setText(R.string.label_getting_location);
    		
    		//�ʒu���Ď����J�n
    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_CHECK_DELAY, 0, this);
    		
    		saveThreadFlag = true;
    		//�ʒu���ۑ��X���b�h���J�n
    		handler.post(saveLocationRunable);
    	}
    }
    @Override
    protected void onPause() {
    	super.onPause();

    	//�ۑ��������~
    	saveThreadFlag = false;

    	//�ʒu���̊Ď��������~
    	if(mLocationManager != null) {
        	mLocationManager.removeUpdates(this);
    	}
    }
	@Override
	public void onLocationChanged(Location location) {
		//�擾�����ʒu����ێ�
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
