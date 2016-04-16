package com.mlsdev.sample;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPickedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPickedImage = (ImageView) findViewById(R.id.iv_picked_image);
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_pick_camera);
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_pick_gallery);

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxImagePicker.with(MainActivity.this).requestImage(Sources.CAMERA)
                        .flatMap(new Func1<Uri, Observable<File>>() {
                            @Override
                            public Observable<File> call(Uri uri) {
                                return RxImageConverters.uriToFile(MainActivity.this, uri, createTempFile());
                            }
                        })
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                Toast.makeText(MainActivity.this, String.format("Got this: %s", file.getAbsolutePath()), Toast.LENGTH_LONG).show();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxImagePicker.with(MainActivity.this).requestImage(Sources.GALLERY)
                        .flatMap(new Func1<Uri, Observable<File>>() {
                            @Override
                            public Observable<File> call(Uri uri) {
                                return RxImageConverters.uriToFile(MainActivity.this, uri, createTempFile());
                            }
                        })
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                Toast.makeText(MainActivity.this, String.format("Got this: %s", file.getAbsolutePath()), Toast.LENGTH_LONG).show();
                                Glide.with(MainActivity.this)
                                        .load(file)
                                        .crossFade()
                                        .into(ivPickedImage);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(MainActivity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }

    Action1<Uri> imagePickSubscriber = new Action1<Uri>() {
        @Override
        public void call(Uri uri) {
            Glide.with(MainActivity.this)
                    .load(uri)
                    .crossFade()
                    .into(ivPickedImage);
        }
    };
}
