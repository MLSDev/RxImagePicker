package com.mlsdev.rximagepicker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxImageConverters {
    public static Observable<File> uriToFile(final Context context, final Uri uri, final File file) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    copyInputStreamToFile(inputStream, file);
                    subscriber.onNext(file);
                } catch (Exception e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static void copyInputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[10 * 1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public static Observable<Bitmap> uriToBitmap(final Context context, final Uri uri) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    subscriber.onNext(bitmap);
                } catch (IOException e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> uriToFullUri(final Context context, final Uri originalUri) {
        return Observable
            .create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    Cursor imageCursor = null;
                    try {
                        String imageId = originalUri.getLastPathSegment().split("%3A")[0].split(":")[1];
                        final String[] imageColumns = {MediaStore.Images.Media.DATA};

                        Uri uri;
                        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else {
                            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                        }

                        imageCursor = context.getContentResolver().query(uri, imageColumns, MediaStore.Images.Media._ID + "=" + imageId, null, null);
                        if (imageCursor != null && imageCursor.moveToFirst()) {
                            subscriber.onNext(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                        } else {
                            subscriber.onError(new Throwable("No image found"));
                        }
                    } catch (Exception e) {
                        subscriber.onError(e);
                    } finally {
                        if (imageCursor != null) {
                            imageCursor.close();
                        }
                    }
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
