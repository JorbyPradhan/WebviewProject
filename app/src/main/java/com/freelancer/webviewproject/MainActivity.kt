package com.freelancer.webviewproject

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.freelancer.webviewproject.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var isGotoFb = false
    private var mWebviewPop: WebView? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var userID = "0"
    private val mCM: String? = null
    private val mUM: ValueCallback<Uri>? = null
    private val mUMA: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>?>? = null
    private var mCameraPhotoPath: String? = null
    companion object {
        private const val FCR = 1
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        var results: Array<Uri>? = null
        // Check that the response is a good one
        if (resultCode == RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = arrayOf<Uri>(Uri.parse(mCameraPhotoPath))
                }
            } else {
                val dataString = data.dataString
                if (dataString != null) {
                    results = arrayOf<Uri>(Uri.parse(dataString))
                }
            }
        }
        mFilePathCallback!!.onReceiveValue(results)
        mFilePathCallback = null
        return
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            grantResults.forEach {
                if (it == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "请授予所需的权限", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this, "已获得权限", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val decor = window.decorView
//        decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if(BuildConfig.TYPE == 1){
            val webLinkService = RetrofitHelper.getInstance(this).create(WebSiteLinkApi::class.java)
            val buyerRepository = WebSiteRepostory(webLinkService)
            mainViewModel = ViewModelProvider(
                this,
                MainViewModelFactory(buyerRepository)
            )[MainViewModel::class.java]

            mainViewModel.webSite.observe(this) {
                if (it != null) {
                    binding.webView.loadUrl(it.data)
                }
            }
        }else{
            binding.webView.loadUrl("http://yj.aizhongxin.xyz/")
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1
            )
        }
        val setting = binding.webView.settings
        setting.allowFileAccessFromFileURLs = true
        setting.allowUniversalAccessFromFileURLs = true
        setting.javaScriptCanOpenWindowsAutomatically = true
        setting.loadsImagesAutomatically = true
        //setting.setAppCacheEnabled(true)
        //  setting.setSupportMultipleWindows(true)
        //setLoadsImagesAutomatically
        setting.javaScriptEnabled = true
        setting.allowContentAccess = true

        setting.domStorageEnabled = true
        setting.javaScriptCanOpenWindowsAutomatically = true
        //  setting.builtInZoomControls=true
        setting.allowFileAccess = true
        setting.userAgentString = "android_web_view"
        setting.mixedContentMode = 0
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        // this will load the url of the website

        mContext = this.applicationContext
        binding.webView.webViewClient = Callback()
        binding.webView.webChromeClient = object : WebChromeClient() {

            @Throws(IOException::class)
            private fun createImageFile(): File {
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir: File = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                return File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",  /* suffix */
                    storageDir /* directory */
                )
            }

            override fun onShowFileChooser(
                view: WebView?,
                filePath: ValueCallback<Array<Uri>?>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback!!.onReceiveValue(null)
                }
                mFilePathCallback = filePath
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?> =
                    takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
                return true
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String?) {
                mUploadMessage = uploadMsg
                // Create AndroidExampleFolder at sdcard
                // Create AndroidExampleFolder at sdcard
                val imageStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ), "AndroidExampleFolder"
                )
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs()
                }
                // Create camera captured image file path and name
                val file = File(
                    imageStorageDir.toString() + File.separator.toString() + "IMG_" + System.currentTimeMillis()
                        .toString() + ".jpg"
                )
                mCapturedImageURI = Uri.fromFile(file)
                // Camera capture image intent
                val captureIntent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                // Create file chooser intent
                val chooserIntent = Intent.createChooser(i, "Image Chooser")
                // Set camera intent to file chooser
                chooserIntent.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
                )
                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
            }

            fun openFileChooser(
                uploadMsg: ValueCallback<Uri?>?,
                acceptType: String?,
                capture: String?
            ) {
                openFileChooser(uploadMsg, acceptType)
            }
        }
    }

    inner class Callback : WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            Toast.makeText(applicationContext, "Failed to load", Toast.LENGTH_LONG).show()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

        if(request?.url.toString().contains("facebook.com") ||
                request?.url.toString().contains("messenger.com") ||
                request?.url.toString().lowercase().contains("viber") ||
                request?.url.toString().lowercase().contains("telegram")||
                request?.url.toString().lowercase().contains("tg://resolve")){
                val viewIntent = Intent(
                    "android.intent.action.VIEW",
                    request?.url
                )

                startActivity(viewIntent)
                return true
            }else if(request?.url.toString().contains("tel")){
                val phNum = request?.url.toString().removePrefix("tel:")
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phNum, null))
                startActivity(intent)
                return true
            }
            else {
                    view?.loadUrl(request?.url.toString())
                    return true
            }

        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if(view?.progress!! >= 100) {
                binding.ivLogo.isVisible = false
                binding.loadingLayout.isVisible= false
                binding.webView.isVisible = true

            }
        }


    }

}