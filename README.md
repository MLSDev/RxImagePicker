# RxImagePicker

RxImagePicker an easy way to get image from Gallery or Camera with request runtime permission on Android M. Using RxJava

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

