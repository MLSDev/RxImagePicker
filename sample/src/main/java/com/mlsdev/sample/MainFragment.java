package com.mlsdev.sample;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;

import rx.Observable;

public class MainFragment extends Fragment {

    private ImageView ivPickedImage;
    private RadioGroup converterRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);

        ivPickedImage = (ImageView) rootView.findViewById(R.id.iv_picked_image);
        FloatingActionButton fabCamera = (FloatingActionButton) rootView.findViewById(R.id.fab_pick_camera);
        FloatingActionButton fabGallery = (FloatingActionButton) rootView.findViewById(R.id.fab_pick_gallery);
        converterRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);
        converterRadioGroup.check(R.id.radio_uri);

        fabCamera.setOnClickListener(view -> pickImageFromSource(Sources.CAMERA));
        fabGallery.setOnClickListener(view -> pickImageFromSource(Sources.GALLERY));
        return rootView;
    }

    private void pickImageFromSource(Sources source) {
        RxImagePicker.with(getActivity()).requestImage(source)
                .flatMap(uri -> {
                    switch (converterRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_file:
                            return RxImageConverters.uriToFile(getActivity(), uri, createTempFile());
                        case R.id.radio_bitmap:
                            return RxImageConverters.uriToBitmap(getActivity(), uri);
                        default:
                            return Observable.just(uri);
                    }
                })
                .subscribe(result -> {
                    Toast.makeText(getActivity(), String.format("Result: %s", result), Toast.LENGTH_LONG).show();
                    if (result instanceof Bitmap) {
                        ivPickedImage.setImageBitmap((Bitmap) result);
                    } else {
                        Glide.with(getActivity())
                                .load(result) // works for File or Uri
                                .crossFade()
                                .into(ivPickedImage);
                    }
                }, throwable -> {
                    Toast.makeText(getActivity(), String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                });
    }

    private File createTempFile() {
        return new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }
}
