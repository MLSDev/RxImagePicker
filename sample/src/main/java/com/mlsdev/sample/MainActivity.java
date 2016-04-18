package com.mlsdev.sample;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPickedImage;
    private RadioGroup converterRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPickedImage = (ImageView) findViewById(R.id.iv_picked_image);
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_pick_camera);
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_pick_gallery);
        converterRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        converterRadioGroup.check(R.id.radio_uri);

        fabCamera.setOnClickListener(view -> pickImageFromSource(Sources.CAMERA));
        fabGallery.setOnClickListener(view -> pickImageFromSource(Sources.GALLERY));
    }

    private void pickImageFromSource(Sources source) {
        RxImagePicker.with(this).requestImage(source)
                .flatMap(uri -> {
                    switch (converterRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_file:
                            return RxImageConverters.uriToFile(this, uri, createTempFile());
                        case R.id.radio_bitmap:
                            return RxImageConverters.uriToBitmap(this, uri);
                        default:
                            return Observable.just(uri);
                    }
                })
                .subscribe(result -> {
                    Toast.makeText(MainActivity.this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
                    if (result instanceof Bitmap) {
                        ivPickedImage.setImageBitmap((Bitmap) result);
                    } else {
                        Glide.with(MainActivity.this)
                                .load(result) // works for File or Uri
                                .crossFade()
                                .into(ivPickedImage);
                    }
                }, throwable -> {
                    Toast.makeText(MainActivity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                });
    }

    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }
}
