package com.example.fxos.androidtips22;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;

public class MainActivity extends PreferenceActivity {
	private static final String KEY_EDITTEXT = "key_edittext";
	private static final String KEY_LIST = "key_list";

	Preference edittextPreference;
	Preference listPreference;
	private Resources res;
	private String currentLabel;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //XML�Œ�`���ꂽ�v���t�@�����X��ʂ��쐬����
        addPreferencesFromResource(R.xml.pref);

        res = getResources();
        //�e�L�X�g�̃v���t�@�����X����ݒ�
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        currentLabel = res.getString(R.string.common_current_value);
        
        //�e�L�X�g�̃v���t�@�����X��񂩂烉�x����ݒ�
        edittextPreference = findPreference(KEY_EDITTEXT);
        String summary = prefs.getString(KEY_EDITTEXT, null);
        //���݂̒l��ݒ�
        if(TextUtils.isEmpty(summary)) {
            edittextPreference.setSummary(res.getString(R.string.summary_text));
        } else {
            edittextPreference.setSummary(currentLabel + summary);
        }
        edittextPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//�l��ύX�������̐V�������x����ݒ�
				edittextPreference.setSummary(currentLabel + newValue.toString());
				return true;
			}
		});
        
        //���X�g�̃v���t�@�����X��񂩂烉�x����ݒ�
        listPreference = findPreference(KEY_LIST);
        String index = prefs.getString(KEY_LIST, "");
        //���݁A�I�����Ă���l��ݒ�
        if(!index.equals("")) {
        	listPreference.setSummary(currentLabel + res.getStringArray(R.array.list_pref_labels)[Integer.parseInt(index)]);
        }
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//�l��ύX�������̐V�������x����ݒ�
	        	listPreference.setSummary(currentLabel + res.getStringArray(R.array.list_pref_labels)[Integer.parseInt(newValue.toString())]);
				return true;
			}
		});
    }
}
