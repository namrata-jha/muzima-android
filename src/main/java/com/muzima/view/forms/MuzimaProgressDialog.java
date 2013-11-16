package com.muzima.view.forms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.webkit.JavascriptInterface;

public class MuzimaProgressDialog {
    private ProgressDialog dialog;

    public MuzimaProgressDialog(Activity activity) {
        this(new ProgressDialog(activity));
    }

    MuzimaProgressDialog(ProgressDialog dialog) {
        this.dialog = dialog;
        this.dialog.setCancelable(false);
    }

    @JavascriptInterface
    public void show(String title) {
        dialog.setTitle(title);
        dialog.setMessage("This might take a while");
        dialog.show();
    }

    @JavascriptInterface
    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
