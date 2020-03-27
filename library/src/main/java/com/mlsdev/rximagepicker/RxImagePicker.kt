package com.mlsdev.rximagepicker

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*
import android.content.ComponentName

class RxImagePicker : Fragment() {

    private lateinit var attachedSubject: PublishSubject<Boolean>
    private lateinit var publishSubject: PublishSubject<Uri>
    private lateinit var publishSubjectMultipleImages: PublishSubject<List<Uri>>
    private lateinit var canceledSubject: PublishSubject<Int>

    private var allowMultipleImages = false
    private var imageSource: Sources? = null
    private var chooserTitle: String? = null

    fun requestImage(source: Sources, chooserTitle: String?): Observable<Uri> {
        this.chooserTitle = chooserTitle
        return requestImage(source)
    }

    fun requestImage(source: Sources): Observable<Uri> {
        initSubjects()
        allowMultipleImages = false
        imageSource = source
        requestPickImage()
        return publishSubject.takeUntil(canceledSubject)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun requestMultipleImages(): Observable<List<Uri>> {
        initSubjects()
        imageSource = Sources.GALLERY
        allowMultipleImages = true
        requestPickImage()
        return publishSubjectMultipleImages.takeUntil(canceledSubject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private fun initSubjects(){
        publishSubject = PublishSubject.create()
        attachedSubject = PublishSubject.create()
        canceledSubject = PublishSubject.create()
        publishSubjectMultipleImages = PublishSubject.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (::attachedSubject.isInitialized.not() or
                ::publishSubject.isInitialized.not() or
                ::publishSubjectMultipleImages.isInitialized.not() or
                ::canceledSubject.isInitialized.not()){
            initSubjects()
        }
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SELECT_PHOTO -> handleGalleryResult(data)
                TAKE_PHOTO -> onImagePicked(cameraPictureUrl)
                CHOOSER -> if (isPhoto(data)) {
                    onImagePicked(cameraPictureUrl)
                } else {
                    handleGalleryResult(data)
                }
            }
        } else {
            canceledSubject.onNext(requestCode)
        }
    }

    private fun isPhoto(data: Intent?): Boolean {
        return data == null || data.data == null && data.clipData == null
    }

    private fun handleGalleryResult(data: Intent?) {
        if (allowMultipleImages) {
            val imageUris = ArrayList<Uri>()
            val clipData = data!!.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } else {
                imageUris.add(data.data)
            }
            onImagesPicked(imageUris)
        } else {
            onImagePicked(data!!.data)
        }
    }

    private fun requestPickImage() {
        if (!isAdded) {
            attachedSubject.subscribe { pickImage() }
        } else {
            pickImage()
        }
    }

    private fun pickImage() {
        if (!checkPermission()) {
            return
        }

        var chooseCode = 0
        var pictureChooseIntent: Intent? = null

        when (imageSource) {
            Sources.CAMERA -> {
                cameraPictureUrl = createImageUri()
                pictureChooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)
                grantWritePermission(context!!, pictureChooseIntent, cameraPictureUrl!!)
                chooseCode = TAKE_PHOTO
            }
            Sources.GALLERY -> {
                pictureChooseIntent = createPickFromGalleryIntent()
                chooseCode = SELECT_PHOTO
            }
            Sources.DOCUMENTS -> {
                pictureChooseIntent = createPickFromDocumentsIntent()
                chooseCode = SELECT_PHOTO
            }
            Sources.CHOOSER -> {
                pictureChooseIntent = createChooserIntent(chooserTitle)
                chooseCode = CHOOSER
            }
        }

        startActivityForResult(pictureChooseIntent, chooseCode)
    }

    private fun createChooserIntent(chooserTitle: String?): Intent {
        cameraPictureUrl = createImageUri()
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)
            grantWritePermission(context!!, intent, cameraPictureUrl!!)
            cameraIntents.add(intent)
        }
        val galleryIntent = createPickFromDocumentsIntent()
        val chooserIntent = Intent.createChooser(galleryIntent, chooserTitle)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        return chooserIntent
    }

    private fun createPickFromGalleryIntent(): Intent {
        var pictureChooseIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleImages)
        }
        return pictureChooseIntent
    }

    private fun createPickFromDocumentsIntent(): Intent {
        val pictureChooseIntent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleImages)
            pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        } else {
            pictureChooseIntent = Intent(Intent.ACTION_GET_CONTENT)
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pictureChooseIntent.type = "image/*"
        return pictureChooseIntent
    }

    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
            false
        } else {
            true
        }
    }

    private fun createImageUri(): Uri? {
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun onImagesPicked(uris: List<Uri>) {
        publishSubjectMultipleImages.onNext(uris)
        publishSubjectMultipleImages.onComplete()
    }

    private fun onImagePicked(uri: Uri?) {
        uri?.let {
            publishSubject.onNext(it)
        }
        publishSubject.onComplete()
    }

    companion object {

        private const val SELECT_PHOTO = 100
        private const val TAKE_PHOTO = 101
        private const val CHOOSER = 102

        private val TAG = RxImagePicker::class.java.simpleName
        private var cameraPictureUrl: Uri? = null

        fun with(fragmentManager: FragmentManager): RxImagePicker {
            var rxImagePickerFragment = fragmentManager.findFragmentByTag(TAG) as RxImagePicker?
            if (rxImagePickerFragment == null) {
                rxImagePickerFragment = RxImagePicker()
                fragmentManager.beginTransaction()
                        .add(rxImagePickerFragment, TAG)
                        .commit()
            }
            return rxImagePickerFragment
        }
    }

}
