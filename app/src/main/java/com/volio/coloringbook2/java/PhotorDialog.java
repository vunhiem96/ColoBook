package com.volio.coloringbook2.java;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;


public class PhotorDialog {

    private static PhotorDialog mSelf;
    private ProgressDialog mProgressDialog, mProgressDialogDownload;

    public static PhotorDialog getInstance() {
        if (mSelf == null) {
            mSelf = new PhotorDialog();
        }
        return mSelf;
    }

    public void showLoading(final Activity mActivity) {
        //showLoading(mActivity, null);
    }



    public void showLoadingWithMessage(final Activity mActivity, final String message) {
        if (mActivity != null) {
            PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
                @Override
                public void onWork() {
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(mActivity);
                        mProgressDialog.setMessage(message);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                    }
                }
            });
        }
    }

    //---------------------------------------------------
    public void hideLoading() {
        PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
            @Override
            public void onWork() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    //---------------------------------------------------
    public void showLoadingProgress(final Activity activity) {
        if (activity != null) {
            PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
                @Override
                public void onWork() {
                    if (mProgressDialogDownload == null) {
                        mProgressDialogDownload = new ProgressDialog(activity);
                        mProgressDialogDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        //mProgressDialogDownload.setMessage(activity.getString(R.string.message_download));
                        mProgressDialogDownload.setCancelable(false);
                        mProgressDialogDownload.show();
                    } else {
                        if (!mProgressDialog.isShowing()) {
                            mProgressDialog.show();
                        }
                    }
                }
            });
        }
    }

    //----------------------------------------------------------------------------------------------
    public void showLoadingProgress(final Activity activity, final String message) {
        if (activity != null) {
            PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
                @Override
                public void onWork() {
                    if (mProgressDialogDownload == null) {
                        mProgressDialogDownload = new ProgressDialog(activity);
                        mProgressDialogDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialogDownload.setMessage(message);
                        mProgressDialogDownload.setCancelable(false);
                        mProgressDialogDownload.show();
                    } else {
                        if (!mProgressDialog.isShowing()) {
                            mProgressDialog.show();
                        }
                    }
                }
            });
        }
    }

    //----------------------------------------------------------------------------------------------
    public void updateDialogProgress(final int progress) {
        PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
            @Override
            public void onWork() {
                if (mProgressDialogDownload != null && mProgressDialogDownload.isShowing()) {
                    mProgressDialogDownload.setIndeterminate(false);
                    mProgressDialogDownload.setProgress(progress);
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------
    public void hideLoadingDownload() {
        PhotorThread.getInstance().runOnUI(new PhotorThread.IHandler() {
            @Override
            public void onWork() {
                if (mProgressDialogDownload != null && mProgressDialogDownload.isShowing()) {
                    mProgressDialogDownload.dismiss();
                    mProgressDialogDownload = null;
                }
            }
        });
    }

    public void showDialogConfirm(Activity activity, int message,
                                  String yesText,
                                  String noText,
                                  DialogInterface.OnClickListener onYes,
                                  DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        builder.setNegativeButton(noText, onNo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogConfirm(Activity activity, int message,
                                  int idYes,
                                  int idNo,
                                  boolean cancelAble,
                                  DialogInterface.OnClickListener onYes,
                                  DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(cancelAble);
        builder.setMessage(message);
        builder.setPositiveButton(idYes, onYes);
        builder.setNegativeButton(idNo, onNo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogConfirmTheme(Activity activity, int message,
                                       int idYes,
                                       int idNo,
                                       int theme,
                                       DialogInterface.OnClickListener onYes,
                                       DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, theme);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(idYes, onYes);
        builder.setNegativeButton(idNo, onNo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogBack(Activity activity, int message,
                               int idCancel,
                               int idYes,
                               int idNo,
                               DialogInterface.OnClickListener onCancel,
                               DialogInterface.OnClickListener onYes,
                               DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(idYes, onYes);
        builder.setNegativeButton(idNo, onNo);
        builder.setNeutralButton(idCancel, onCancel);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogBackTheme(Activity activity, int message,
                                    int idCancel,
                                    int idYes,
                                    int idNo,
                                    int theme,
                                    DialogInterface.OnClickListener onCancel,
                                    DialogInterface.OnClickListener onYes,
                                    DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, theme);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(idYes, onYes);
        builder.setNegativeButton(idNo, onNo);
        builder.setNeutralButton(idCancel, onCancel);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showInfoDialog(Activity activity, int message,
                               int yesText,
                               DialogInterface.OnClickListener onYes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showInfoDialog(Activity activity, boolean isCancel, int message,
                               int yesText,
                               DialogInterface.OnClickListener onYes, int theme) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, theme);
        builder.setCancelable(isCancel);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
