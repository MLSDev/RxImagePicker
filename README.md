# RxImagePicker

An easy way to get image from Gallery or Camera with request runtime permission on Android M using RxJava

## Setup

To use this library your ` minSdkVersion` must be >= 14.

In your build.gradle :

```gradle
dependencies {
    compile 'com.mlsdev.rximagepicker:library:1.0.2'
    compile 'io.reactivex:rxjava:1.0.14'
}
```

## Example

```java
RxImagePicker.with(context).requestImage(Sources.CAMERA).subscribe(new Action1<Uri>() {
                @Override
                public void call(Uri uri) {
                    //Get image by uri using one of image loading libraries. I use Glide in sample app.
                }
            });
```

## Sample App

<img src="https://cloud.githubusercontent.com/assets/1778155/11761109/cb70a420-a0bd-11e5-8cf1-e2b172745eab.png" width="400">

