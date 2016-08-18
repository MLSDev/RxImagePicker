# RxImagePicker

An easy way to get image from Gallery or Camera with request runtime permission on Android M using RxJava

## Setup

To use this library your ` minSdkVersion` must be >= 14.

In your build.gradle :

```gradle
dependencies {
    compile 'com.mlsdev.rximagepicker:library:1.2.2'
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
### Using converters

```java
RxImagePicker.with(context).requestImage(Sources.GALLERY)
    .flatMap(new Func1<Uri, Observable<Bitmap>>() {
             @Override
             public Observable<Bitmap> call(Uri uri) {
                 return RxImageConverters.uriToBitmap(context, uri);
             }
         })
         .subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                // Do something with Bitmap
            }
         });
```

```java
RxImagePicker.with(context).requestImage(Sources.GALLERY)
    .flatMap(new Func1<Uri, Observable<File>>() {
             @Override
             public Observable<File> call(Uri uri) {
                 return RxImageConverters.uriToFile(context, uri, new File("YOUR FILE"));
             }
         })
         .subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                // Do something with your file copy
            }
         });
```

```java
RxImagePicker.with(this)                                                  
    .requestImage(Sources.GALLERY)                                        
    .flatMap(new Func1<Uri, Observable<? extends String>>() {             
        @Override                                                         
        public Observable<? extends String> call(Uri uri) {               
            return RxImageConverters.uriToFullPath(MainActivity.this, uri);
        }                                                                 
    })                                                                    
    .subscribe(new Action1<String>() {                                    
        @Override                                                         
        public void call(String path) {                                    
            // Do something with your image path
            // Ex. /storage/emulated/0/DCIM/Camera/20160701_113408.jpg                              
        }                                                                 
    });                                                                   
```


## Sample App

<img src="https://cloud.githubusercontent.com/assets/1778155/11761109/cb70a420-a0bd-11e5-8cf1-e2b172745eab.png" width="400">

## Authors
* [Sergey Glebov](mailto:glebov@mlsdev.com) ([frederikos][github-frederikos]), MLSDev 

## License
RxImagePicker is released under the MIT license. See LICENSE for details.

## About MLSDev

[<img src="https://cloud.githubusercontent.com/assets/1778155/11761239/ccfddf60-a0c2-11e5-8f2a-8573029ab09d.png" alt="MLSDev.com">][mlsdev]

RxImagePicker is maintained by MLSDev, Inc. We specialize in providing all-in-one solution in mobile and web development. Our team follows Lean principles and works according to agile methodologies to deliver the best results reducing the budget for development and its timeline. 

Find out more [here][mlsdev] and don't hesitate to [contact us][contact]!

[mlsdev]: http://mlsdev.com
[contact]: http://mlsdev.com/contact_us
[github-frederikos]: https://github.com/frederikos

