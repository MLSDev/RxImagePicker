package com.mlsdev.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import rx.functions.Action1;

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
                RxImagePicker.with(MainActivity.this).requestImage(Sources.CAMERA).subscribe(imagePickSubscriber);
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxImagePicker.with(MainActivity.this).requestImage(Sources.GALLERY).subscribe(imagePickSubscriber);
            }
        });
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
