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
        
        //XMLで定義されたプリファレンス画面を作成する
        addPreferencesFromResource(R.xml.pref);

        res = getResources();
        //テキストのプリファレンス情報を設定
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        currentLabel = res.getString(R.string.common_current_value);
        
        //テキストのプリファレンス情報からラベルを設定
        edittextPreference = findPreference(KEY_EDITTEXT);
        String summary = prefs.getString(KEY_EDITTEXT, null);
        //現在の値を設定
        if(TextUtils.isEmpty(summary)) {
            edittextPreference.setSummary(res.getString(R.string.summary_text));
        } else {
            edittextPreference.setSummary(currentLabel + summary);
        }
        edittextPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//値を変更した時の新しいラベルを設定
				edittextPreference.setSummary(currentLabel + newValue.toString());
				return true;
			}
		});
        
        //リストのプリファレンス情報からラベルを設定
        listPreference = findPreference(KEY_LIST);
        String index = prefs.getString(KEY_LIST, "");
        //現在、選択している値を設定
        if(!index.equals("")) {
        	listPreference.setSummary(currentLabel + res.getStringArray(R.array.list_pref_labels)[Integer.parseInt(index)]);
        }
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//値を変更した時の新しいラベルを設定
	        	listPreference.setSummary(currentLabel + res.getStringArray(R.array.list_pref_labels)[Integer.parseInt(newValue.toString())]);
				return true;
			}
		});
    }
}
