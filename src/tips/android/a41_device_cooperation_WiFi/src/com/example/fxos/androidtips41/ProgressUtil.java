
package com.example.fxos.androidtips41;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtil {
    private static ProgressDialog mProgressDialog = null;

    // show progress dialog
    public static void show(Context context, CharSequence title, CharSequence message,
            boolean indeterminate, boolean cancelable) {
        // hide bofore show
        hideProgress();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(indeterminate);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.show();
    }

    // hide progress dialog
    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
