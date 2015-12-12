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

## Authors
* [Sergey Glebov](mailto:glebov@mlsdev.com) ([frederikos][github-frederikos]), MLSDev 

## About MLSDev

[<img src="/app/src/main/assets/mlsdev-logo.png" alt="MLSDev.com">][mlsdev]

Realm Android Example are maintained by MLSDev, Inc. We specialize in providing all-in-one solution in mobile and web development. Our team follows Lean principles and works according to agile methodologies to deliver the best results reducing the budget for development and its timeline. 

Find out more [here][mlsdev] and don't hesitate to [contact us][contact]!

[mlsdev]: http://mlsdev.com
[contact]: http://mlsdev.com/contact_us
[github-frederikos]: https://github.com/frederikos

