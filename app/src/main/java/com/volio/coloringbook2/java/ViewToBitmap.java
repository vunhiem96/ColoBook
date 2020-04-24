package com.volio.coloringbook2.java;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.volio.coloringbook2.R;
import com.volio.coloringbook2.common.AppConst;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ViewToBitmap {

    private static final String TAG = ViewToBitmap.class.getSimpleName();
    private static final String EXTENSION_PNG = ".png";
    private static final String EXTENSION_JPG = ".jpg";
    private static final int JPG_MAX_QUALITY = 100;
    private String fileName, fileExtension;
    private OnSaveResultListener onSaveResultListener;
    private int jpgQuality;
    private Handler handler;
    private View view;
    private Bitmap bitmap;

    private ViewToBitmap(@NonNull View view) {
        this.view = view;
    }

    private ViewToBitmap(@NonNull Bitmap bm) {
        this.bitmap = bm;
    }


    public static ViewToBitmap of(@NonNull View view) {
        return new ViewToBitmap(view);
    }

    public static ViewToBitmap of(@NonNull Bitmap bm) {
        return new ViewToBitmap(bm);
    }


    public ViewToBitmap toJPG() {
        jpgQuality = JPG_MAX_QUALITY;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    public ViewToBitmap toJPG(int jpgQuality) {
        this.jpgQuality = (jpgQuality == 0) ? JPG_MAX_QUALITY : jpgQuality;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    public ViewToBitmap toPNG() {
        setFileExtension(EXTENSION_PNG);
        return this;
    }

    public ViewToBitmap setOnSaveResultListener(OnSaveResultListener onSaveResultListener) {
        this.onSaveResultListener = onSaveResultListener;
        this.handler = new Handler(Looper.myLooper());
        return this;
    }


    public void save(Context context) {
        AsyncSaveImage asyncSaveBitmap = new AsyncSaveImage(context, getBitmap());
        asyncSaveBitmap.execute();
    }


    public void saveBitmap(Context context) {
        if (bitmap != null) {
            AsyncSaveImage asyncSaveBitmap = new AsyncSaveImage(context, bitmap);
            asyncSaveBitmap.execute();
        }
    }

    public void saveBitmap2(Context context, String name) {
        if (bitmap != null) {
            AsyncSaveImage asyncSaveBitmap = new AsyncSaveImage(context, bitmap, name);
            asyncSaveBitmap.execute();
        }
    }


    public void saveToTemp(final Context context, final String name) {
//        AsyncSaveImageToTemp asyncSaveBitmap = new AsyncSaveImageToTemp(context, getBitmap(), name);
//        asyncSaveBitmap.execute();
    }


    public static void saveBitmapToTemp(final Context context, final String name, Bitmap bitmap) {
        AsyncSaveImageToTemp asyncSaveBitmap = new AsyncSaveImageToTemp(context, bitmap, name);
        asyncSaveBitmap.execute();
    }


    private Context getAppContext() {
        if (view == null) {
            throw new NullPointerException("Null cannot passed to ViewToBitmap.of()");
        } else {
            return view.getContext().getApplicationContext();
        }
    }

    private void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }


    private String getFileExtension() {
        return fileExtension;
    }


    @NotNull
    private String getFilename() {
        if (fileName == null || fileName.isEmpty()) {
            return "color_" + System.currentTimeMillis() / 10000 + getFileExtension();
        } else {
            return fileName + getFileExtension();
        }
    }

    private Bitmap getBitmap() {
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        canvas.setBitmap(null);
        return bitmap;
    }

    private Bitmap getBitmapByCache() {
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // copy this bitmap otherwise distroying the cache will destroy
        // the bitmap for the referencing drawable and you'll not
        // get the captured view
        Bitmap b = b1.copy(Bitmap.Config.ARGB_8888, false);
        view.destroyDrawingCache();
        return b;
    }

    private void notifyListener(final boolean isSaved, final String path) {
        if (onSaveResultListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSaveResultListener.onSaveResult(isSaved, path);
                }
            });
        }
    }

    public interface OnSaveResultListener {
        void onSaveResult(boolean isSaved, String path);
    }


    private class AsyncSaveImage extends AsyncTask<Void, Void, Boolean>
            implements MediaScannerConnection.OnScanCompletedListener {
        private Context context;
        private Bitmap bitmap;
        private CustomProgressDialog dialog;
        private String name;

        private AsyncSaveImage(Context context, Bitmap bitmap, String name) {
            this.context = context;
            this.bitmap = bitmap;
            this.name = name;
            dialog = new CustomProgressDialog(context);
        }

        private AsyncSaveImage(Context context, Bitmap bitmap) {
            this.context = context;
            this.bitmap = bitmap;
            dialog = new CustomProgressDialog(context);
        }


        protected void onPreExecute() {
            this.dialog.show();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            File myDir = new File(AppConst.INSTANCE.getFOLDER_TEXT_TO_PHOTO());
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File imageFile = new File(AppConst.INSTANCE.getFOLDER_TEXT_TO_PHOTO() + getFilename());

            if (fileExtension == null) {
                throw new IllegalStateException("A file format must be chosen to ViewToBitmap before calling save()");
            } else {
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile))) {
                    switch (fileExtension) {
                        case EXTENSION_JPG:
                            bitmap.compress(Bitmap.CompressFormat.JPEG, jpgQuality, out);
                            break;
                        case EXTENSION_PNG:
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            break;
                        default:
                            bitmap.compress(Bitmap.CompressFormat.JPEG, jpgQuality, out);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bitmap = null;
                    notifyListener(false, null);
                    return false;
                }
            }

            bitmap = null;
            MediaScannerConnection.scanFile(context, new String[]{imageFile.toString()}, null, this);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                this.dialog.dismiss();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (uri != null && path != null) {
                notifyListener(true, path);
            } else {
                notifyListener(false, null);
            }
        }
    }


    private static class AsyncSaveImageToTemp extends AsyncTask<Void, Void, Boolean>
            implements MediaScannerConnection.OnScanCompletedListener {
        private Context context;
        private Bitmap bitmap;
        private CustomProgressDialog dialog;
        private String name;
        private String fileExtension;

        private AsyncSaveImageToTemp(Context context, Bitmap bitmap, String name) {
            this.context = context;
            this.bitmap = bitmap;
            dialog = new CustomProgressDialog(context);
            this.name = name;
        }

        protected void onPreExecute() {
            //this.dialog.show();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            fileExtension = EXTENSION_JPG;
            File imageFile = new File(name);
            if (fileExtension == null) {
                throw new IllegalStateException("A file format must be chosen to ViewToBitmap before calling save()");
            } else {
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile))) {
                    switch (fileExtension) {
                        case EXTENSION_JPG:
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            break;
                        case EXTENSION_PNG:
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            break;
                        default:
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bitmap = null;
                    //notifyListener(false, null);
                    return false;
                }
            }

            bitmap = null;
            MediaScannerConnection.scanFile(context, new String[]{imageFile.toString()}, null, this);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //this.dialog.dismiss();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (uri != null && path != null) {
                //notifyListener(true, path);
            } else {
                //notifyListener(false, null);
            }
        }
    }

}

class CustomProgressDialog extends Dialog {
    CustomProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        setCancelable(false);
        //ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}

