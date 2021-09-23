package huji.postpc.find.pic.aword.game.play

import huji.postpc.find.pic.aword.game.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.OnSwipeTouchListener
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Level
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

class PlayFragment : Fragment(R.layout.fragment_play) {

    // The lens of camera the app uses (front/back)
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    // Preview - used to display the camera's feed
    private var preview: Preview? = null

    // Capture and Analyzer for CameraX
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null

    // Camera output
    private lateinit var  outputDirectory: File


    // camera provides access to CameraControl & CameraInfo
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraViewFinder: PreviewView

    // UI components
    private lateinit var captureButton: FloatingActionButton
    private lateinit var wordListenButton: MaterialButton
    private lateinit var wordImgView: ImageView
    private lateinit var tryAgainMsg: TextView
    private lateinit var levelSuccessMsg: TextView
    private lateinit var categoryCompleteMsg: TextView
    private lateinit var waitForLabelerProgressBar: ProgressBar

    // Activity for getColorStateList
    private lateinit var gameActivity: GameActivity

    // View models
    private val playViewModel: PlayViewModel by viewModels()
    private val gameViewModel: GameViewModel by activityViewModels()

    // All levels for this category by level resource id
    private lateinit var levels: List<Level>

    // Current level displayed
    private var levelIdx: Int = 0

    // Word to display for this game-level
    private var word: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get activity context
        gameActivity = (activity as GameActivity)
        // Initialize our background executor, which is used for camera options that are blocking
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraViewFinder = view.findViewById(R.id.img_placeholder)
        // Request camera permissions if not already granted
        if (!allPermissionsGranted()) {
            activity?.let {
                ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
        // Wait for the views to be properly laid out and then set up camera and use cases
        cameraViewFinder.post { setUpCamera() }

        // set output directory
        outputDirectory = GameActivity.getOutputDirectory(requireContext())

        // Find all views
        wordListenButton = view.findViewById(R.id.word_listen_button)
        captureButton = view.findViewById(R.id.capture_fab)
        wordImgView = view.findViewById(R.id.word_image_view)
        tryAgainMsg = view.findViewById(R.id.try_again_message)
        levelSuccessMsg = view.findViewById(R.id.level_success_message)
        categoryCompleteMsg = view.findViewById(R.id.category_complete_message)
        waitForLabelerProgressBar = view.findViewById(R.id.wait_for_labeler)


        // Get information from view model
        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            // Get all levels
            val currCategory = gameViewModel.getCurrCategory()
            if (currCategory != null) {
                levels = currCategory.levels
                // Initialize current level to be the first one
                updateDisplayLevel(Direction.NOMOVE)
            }
            // Update category information
            val categoryColorResId = (activity as GameActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }


        // Set click listener for button
        wordListenButton.setOnClickListener {
            gameActivity.speak(word)
        }
        // Set click listener for capture picture button
        captureButton.setOnClickListener {
            waitForLabelerProgressBar.visibility = View.VISIBLE
            captureImage()
        }

        // Set swipe listeners for next and previous levels
        wordImgView.setOnTouchListener(object : OnSwipeTouchListener(gameActivity){
            override fun onSwipeLeft(){
                if (levelIdx < levels.size - 1) {
                    levelIdx++
                    updateDisplayLevel(Direction.NEXT)
                }
            }
            override fun onSwipeRight(){
                if (levelIdx > 0) {
                    levelIdx--
                    updateDisplayLevel(Direction.PREV)
                }
            }

        })
        // TODO - choose on what view will the swipe listener be
//        cameraViewFinder.setOnTouchListener(object : OnSwipeTouchListener(mainActivity){
//            override fun onSwipeLeft(){
//                if (levelIdx < levels.size - 1) {
//                    levelIdx++
//                    updateDisplayLevel()
//                }
//            }
//            override fun onSwipeRight(){
//                if (levelIdx > 0) {
//                    levelIdx--
//                    updateDisplayLevel()
//                }
//            }
//
//        })


        // Set an observer for the labeler live data
        val labelObserver = Observer<Boolean?> { label ->
            if (label == false){
                // Wrong label found
                appearDisappearView(tryAgainMsg, SUCCESS_FAIL_MSG_DURATION, ::disappearWaitForLabelerProgressBar)
            }
            else if (label == true){
                // Else, found the correct label!
                appearDisappearView(levelSuccessMsg, SUCCESS_FAIL_MSG_DURATION, ::disappearWaitForLabelerProgressBar)
                Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
                // Set level completed if found a correct label
//            mainViewModel.setCurrLevelCompleted()
            }
        }
        playViewModel.labelLiveData.observe(viewLifecycleOwner, labelObserver)
    }

    private fun disappearWaitForLabelerProgressBar(){
        waitForLabelerProgressBar.visibility = View.GONE
    }

    private fun changeLevelUi(currLevel : Level) {
        word = getString(currLevel.nameResId)
        changeTextAnimation(wordListenButton, word)
        // Update image
        wordImgView.setImageResource(currLevel.imgResId)
        tryAgainMsg.text = "It doesn't look like $word...Try again!"
    }


    private fun updateDisplayLevel(dir : Direction) {
        val currLevel = levels.getOrNull(levelIdx)

        if (currLevel != null) {
            val translationDir = when (dir){
                Direction.NEXT -> {resources.getDimension(R.dimen.translationTemplateOutIn)}
                Direction.PREV -> {-resources.getDimension(R.dimen.translationTemplateOutIn)}
                else -> {0f}
            }
            animateViewOutIn(wordImgView, translationDir, ::changeLevelUi, currLevel)

            // Alternative way for animation - somewhat smoother but only animates the view in (not out and in)
            /*
            // old out
            when (dir){
                Direction.NEXT -> {wordImgView.startAnimation(outToLeftAnimation())}
                Direction.PREV -> {wordImgView.startAnimation(outToRightAnimation())}
                else -> {}
            }
            word = getString(currLevel.nameResId)
            wordListenButton.text = word
            // Update image
            wordImgView.setImageResource(currLevel.imgResId)
            // new in
            when (dir){
                Direction.NEXT -> {wordImgView.startAnimation(inFromRightAnimation())}
                Direction.PREV -> {wordImgView.startAnimation(inFromLeftAnimation())}
                else -> {}
            }
            */
        }
    }


    private fun setUpCamera() {
        // ProcessCameraProvider is used to bind the lifecycle of cameras to the lifecycle owner
        // Eliminates the task of opening and closing the camera since CameraX is lifecycle-aware
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnable = {
            // CameraProvider binds the lifecycle of the camera to the LifecycleOwner
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("No cameras are available!")
            }
            // Enable or disable switching between cameras
//            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }
        // After this, cameraProvider is guaranteed to be available
        cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        // CameraProvider - throw an exception in case setUpCamera() has failed (in that case cameraProvider is null)
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        // CameraSelector - used to select which lens to use for camera
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        preview = Preview.Builder().build()
        // ImageCapture and ImageAnalysis objects for CameraX
        imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
        imageAnalyzer = ImageAnalysis.Builder().build()
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(cameraViewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(CAMERA_X_TAG, "Use case binding failed", exc)
        }
    }

    private fun captureImage() {
        val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            // .setMetadata() TODO: setting metadata here is optional
            .build()

        // handle ML image recognition
        imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                Log.e("ImageCapture", "Photo capture success: ${imageProxy.imageInfo.timestamp}")
                // unclear "error" on image - "This declaration is opt-in and its usage should be marked with". Does not cause code to crash.
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImageForMLKIT = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    Log.e("ImageCapture", "inputImage prepared: ${imageProxy.imageInfo.timestamp}")
                    // Label image
                    playViewModel.analyzeImage(inputImageForMLKIT, word)
                }
                // close image when done with it. after this nothing can be done with the captured image
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraXBasic", "Photo capture failed: ${exception.message}", exception)
            }
        })

        // handle saving image
        val capturedImageBitmap = cameraViewFinder.bitmap
        val templateImageBitmap = wordImgView.drawToBitmap()
        val mergedImageBitmap = capturedImageBitmap!!.with(templateImageBitmap)

        saveImage(mergedImageBitmap, requireContext(), "PicAWord")
    }

    private fun Bitmap.with(bmp: Bitmap): Bitmap {
        // Create new bitmap based on the size and config of the old
        val newBitmap: Bitmap = Bitmap.createBitmap(this)

        // Instantiate a canvas and prepare it to paint to the new bitmap
        val canvas = Canvas(newBitmap)

        // Draw the old bitmap onto of the new white one
        canvas.drawBitmap(bmp, 0f, 0f, null)

        return newBitmap
    }

    /// @param folderName can be your app's name
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            val values = contentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
        PicAWordApp.instance.fbManager.uploadImageFromBitmap(bitmap, fileName)
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }


    private fun hasBackCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    private fun hasFrontCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false


// TODO Ron: add camera switch button to the fragment layout
//    private fun updateCameraSwitchButton() {
//        // Enable or disable a button to switch cameras depending on the available cameras
//        try {
//            cameraSwitchButton?.isEnabled = hasBackCamera() && hasFrontCamera()
//        } catch (exception: CameraInfoUnavailableException) {
//            cameraSwitchButton?.isEnabled = false
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        private const val CAMERA_X_TAG = "CameraX"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val SUCCESS_FAIL_MSG_DURATION = 2000L

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)


    }
    enum class Direction {
        NEXT, PREV, NOMOVE
    }

}