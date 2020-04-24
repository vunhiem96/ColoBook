package com.volio.coloringbook2.java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.StrictMode;
import com.volio.coloringbook2.R;

import java.lang.reflect.Method;
import java.util.List;


public class SharePhotoUntils {
    private static SharePhotoUntils shareUtils;

    public static SharePhotoUntils getInstance() {
        if (shareUtils == null) {
            shareUtils = new SharePhotoUntils();
        }
        return shareUtils;
    }

    public SharePhotoUntils() {
    }


    public void sendShareMore(Context context, String filePath) {
        disableExposure();
        Intent share = new Intent(Intent.ACTION_SEND);
        // share.setName("text/plain");
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TEXT, getTextShare(context));
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + filePath));
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share_app)));
    }

    public void sendShareApplication(Context context, String packageName, String file) {

        disableExposure();
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setName("text/plain");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, getTextShare(context));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + file));
        // intent.putExtra(Intent.EXTRA_STREAM, getUriForFile(file));
        boolean isAppFound = false;
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(packageName)) {
                intent.setPackage(info.activityInfo.packageName);
                isAppFound = true;
                break;
            }
        }
        if (isAppFound) {
            context.startActivity(intent);
        }
    }

    public void disableExposure() {
        if (PhotorTool.getSdkVersion() >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private String getTextShare(Context context) {
        String strName = context.getString(R.string.app_name);
        String shareLink = BASE_GOOGLE_PLAY + context.getPackageName();
        return context.getString(R.string.share_app, strName, shareLink);
    }
    private   final String BASE_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

}
