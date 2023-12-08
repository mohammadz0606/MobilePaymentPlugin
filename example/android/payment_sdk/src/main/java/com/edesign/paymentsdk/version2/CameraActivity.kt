package com.edesign.paymentsdk.version2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.edesign.paymentsdk.Utils.CardValidator
import com.edesign.paymentsdk.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), View.OnClickListener {
    //    private var cameraKitView: CameraKitView? = null
    private var imgClose: ImageView? = null
    private var textGetDetails: TextView? = null
    private var previewView: PreviewView? = null

    var cardNumber = ""
    var cardExpiry = ""

    var file: File? = null
    var educationfile: File? = null
    var imageUri: Uri? = null
    var educationUri: Uri? = null
    var filename = ""

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_scanning_fragment)
//        cameraKitView = findViewById(R.id.camera)
        textGetDetails = findViewById(R.id.textGetDetails)
        previewView = findViewById(R.id.previewView)
        imgClose = findViewById(R.id.imgBack)
        imgClose!!.setOnClickListener(this)
        textGetDetails!!.setOnClickListener(this)

        startCamera()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("ddMMyyyy", Locale.US).format(System.currentTimeMillis()) + ".png"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener,
        // which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    val msg = "Photo capture succeeded: $savedUri"
                    var image: InputImage? = null
                    try {
                        image = InputImage.fromFilePath(this@CameraActivity, savedUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    textRecognizer.process(image!!)
                        .addOnSuccessListener(OnSuccessListener<Text> { result ->
                            var cardNumber = ""
                            var month = ""
                            var year = ""
                            var owner = ""
                            for (block in result.textBlocks) {

//                                owner= if (extractOwner(result.text.split("\n")).isNullOrEmpty()) "" else extractOwner(result.text.split("\n"))
//                                Toast.makeText(this@CameraActivity,owner,Toast.LENGTH_LONG).show()
                                for (line in block.lines) {
                                    val lineText = line.text
                                    val (mm, yy) = getExpiry(lineText)
                                    if (CardValidator.validateCardNumber(lineText)) {
                                        cardNumber = lineText
                                    }
                                    if (!mm.isNullOrEmpty()) {
                                        month = mm
                                    }
                                    if (!yy.isNullOrEmpty()) {
                                        year = year
                                    }
                                }
                            }

                            if (cardNumber.isNullOrEmpty()) {
                                ScanCardErrorFragment().show(supportFragmentManager, "")
                            } else {
                                val i = Intent()
                                i.putExtra("number", cardNumber)
                                i.putExtra("month", month)
                                i.putExtra("year", year)
                                i.putExtra("owner", owner)
                                setResult(Activity.RESULT_OK, i)
                                finish()
                            }

                        })

                    /*
                                        val result = recognizer.process(image!!)
                                            .addOnSuccessListener { visionText ->
                                                // Task completed successfully

                                                val input = visionText.text
                                                val lines = input.split("\n")

                                               // val owner = extractOwner(lines)
                                                val number = extractNumber(lines)
                                                val (month, year) = extractExpiration(lines)
                                                if (number.isNullOrEmpty()){
                                                    ScanCardErrorFragment().show(supportFragmentManager, "")
                                                }else{
                                                    val i = Intent()

                                                    i.putExtra("number", number)
                                                    i.putExtra("month", month)
                                                    i.putExtra("year", year)
                                                    setResult(Activity.RESULT_OK, i)
                                                    finish()
                                                }

                                            }
                                            .addOnFailureListener { e ->
                                                // Task failed with an exception
                                                ScanCardErrorFragment().show(supportFragmentManager, "")

                                            }
                    */


                    /*
                                        try {
                                            val bitmap: Bitmap = BitmapFactory.decodeFile(photoFile.path)


                                            val textRecognizer =
                                                TextRecognizer.Builder(this@CameraActivity)
                                                    .build()

                                            val imageFrame: Frame = Frame.Builder()
                                                .setBitmap(bitmap) // your image bitmap
                                                .build()

                                            var imageText=""
                                            val textBlocks = textRecognizer.detect(imageFrame)

                                            for (i in 0 until textBlocks.size()) {
                                                val textBlock = textBlocks[textBlocks.keyAt(i)]
                                                imageText = imageText+textBlock.value +"\n"// return string

                                            }
                                            textGetDetails!!.setText(imageText)
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                    */
                    Log.d("TAG", msg)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView!!.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // creates a folder inside internal storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBack -> {
                finish()
//                val intent = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                )
//                startActivityForResult(intent, 2)

                /* val intent =
                     Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                 val tsLong = System.currentTimeMillis() / 1000
                 val ts = tsLong.toString()
                 filename = ts + "_temp.jpg"
                 val f =
                     File(
                         getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                         filename
                     )
                 imageUri = FileProvider.getUriForFile(
                     this, "com.edesign.stspayment.provider",
                     f
                 )
                 intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                 startActivityForResult(intent, 3)*/
            }

            R.id.textGetDetails -> {
                //ScanCardErrorFragment().show(this.supportFragmentManager, "")

                takePhoto()
                /*
                                cameraKitView!!.captureImage { cameraKitView, capturedImage ->
                                    val bitmap = BitmapFactory.decodeByteArray(
                                        capturedImage, 0,
                                        capturedImage.size
                                    )
                                    getCardDetails(bitmap)

                                }
                */

            }
        }

    }

    private fun extractExpiration1(data: String): String {
        var a = data.split(" ")
        for (i in a.indices) {
            if ((a[i].length == 5 || a[i].length == 7) && a[i][2] == '/') {
                return a[i]
            }
        }
        return ""
    }

    fun getExpiry(input: String): Pair<String, String> {
        val expirationLine = extractExpiration1(input)
        var month = ""
        var year = ""
        if (!expirationLine.isNullOrEmpty() && expirationLine.length > 2) {

            month = expirationLine?.substring(startIndex = 0, endIndex = 2)?:""
        }

        if (!expirationLine.isNullOrEmpty() && expirationLine.length > 3) {

            year = expirationLine?.substring(startIndex = 3)?:""
        }

        return Pair(month, year)
    }

    private fun extractOwner(lines: List<String>): String {
        return lines
            .filter { it.contains(" ") }
            .filter { line -> line.asIterable().none { char -> char.isDigit() } }
            .maxByOrNull { it.length }!!
    }

    private fun getCardDetails(bitmap: Bitmap) {
        /* val textRecognizer =
             TextRecognizer.Builder(this)
                 .build()

         val imageFrame: Frame = Frame.Builder()
             .setBitmap(bitmap) // your image bitmap
             .build()

         var imageText=""
         val textBlocks = textRecognizer.detect(imageFrame)

         for (i in 0 until textBlocks.size()) {
             val textBlock = textBlocks[textBlocks.keyAt(i)]
             imageText = imageText+textBlock.value +"\n"// return string

         }
         textGetDetails!!.setText(imageText)*/


        /*var recognizer =
            TextRecognizer.Builder(this).build()

        if (recognizer.isOperational) {
            val frame: Frame = Frame.Builder().setBitmap(bitmap).build()
            val items: SparseArray<TextBlock> = recognizer.detect(frame)
            if (items.size() !== 0) {
                val stringBuilder = StringBuilder()
                for (i in 0 until items.size()) {
                    val item: TextBlock = items.valueAt(i)
                    stringBuilder.append(item.value)
                    stringBuilder.append("\n")
                }
                textGetDetails!!.setText(stringBuilder.toString())
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            /* file = File(
                 getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                     .toString()
             )
             for (temp in file!!.listFiles()) {
                 if (temp.name == filename) {
                     file = temp
                     break
                 }
             }*/

            val selectedImage = data!!.data
            val filePath =
                arrayOf(MediaStore.Images.Media.DATA)
            val c = contentResolver
                .query(selectedImage!!, filePath, null, null, null)
            c!!.moveToFirst()
            val columnIndex = c.getColumnIndex(filePath[0])
            var picturePath = c.getString(columnIndex)
            c.close()
            if (picturePath != null) {
                file = File(picturePath)
            }


            /* val textRecognizer =
                 TextRecognizer.Builder(this)
                     .build()
             val bitmap = BitmapFactory.decodeFile(file!!.absolutePath)
             val imageFrame: Frame = Frame.Builder()
                 .setBitmap(bitmap) // your image bitmap
                 .build()

             var imageText = ""


             val textBlocks = textRecognizer.detect(imageFrame)

             for (i in 0 until textBlocks.size()) {
                 val textBlock = textBlocks[textBlocks.keyAt(i)]
                 imageText = imageText+textBlock.value // return string

             }
             Toast.makeText(this,imageText,Toast.LENGTH_SHORT).show()*/


        }
    }
}