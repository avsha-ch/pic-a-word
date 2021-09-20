package huji.postpc.find.pic.aword.game.play

import huji.postpc.find.pic.aword.game.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.OnSwipeTouchListener
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Level
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import huji.postpc.find.pic.aword.*

class PlayFragment : Fragment(R.layout.fragment_game) {

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
    private lateinit var previousImgButton: MaterialButton
    private lateinit var nextImgButton: MaterialButton
    private lateinit var wordImgView: ImageView

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

        cameraViewFinder = view.findViewById(R.id.camera_view_finder)
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
        previousImgButton = view.findViewById(R.id.previous_img_button)
        nextImgButton = view.findViewById(R.id.next_img_button)
        wordImgView = view.findViewById(R.id.word_image_view)


        // Get information from view model
        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            // Get all levels
            val currCategory = gameViewModel.gameData[currCategoryResId]
            if (currCategory != null) {
                levels = currCategory.levels
                // Initialize current level to be the first one
                updateDisplayLevel(Direction.NOMOVE)
            }
            // Update category information
            val categoryColorResId = (activity as GameActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
                previousImgButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
                nextImgButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }


        // Set click listener for button
        wordListenButton.setOnClickListener {
            gameActivity.speak(word)
        }
        // Set click listener for capture picture button
        captureButton.setOnClickListener {
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
        // Todo set listeners for previous and next image button
        previousImgButton.setOnClickListener {
            if (levelIdx > 0) {
                levelIdx--
                updateDisplayLevel(Direction.NEXT)
            }
        }
        nextImgButton.setOnClickListener {
            if (levelIdx < levels.size - 1) {
                levelIdx++
                updateDisplayLevel(Direction.PREV)
            }
        }

        // Set an observer for the labeler live data
        val labelObserver = Observer<ImageLabel?> { label ->
            if (label == null) {
                return@Observer
            }
            // Else, found the correct label!
            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
            // Set level completed if found a correct label
//            mainViewModel.setCurrLevelCompleted()
        }
        playViewModel.labelLiveData.observe(viewLifecycleOwner, labelObserver)
    }

    private fun changeLevelUi(currLevel : Level) {
        word = getString(currLevel.nameResId)
        wordListenButton.text = word
        // Update image
        wordImgView.setImageResource(currLevel.imgResId)
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
        imageCapture?.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                Log.d(CAMERA_X_TAG, "Photo capture succeeded: $savedUri")

                // Implicit broadcasts will be ignored for devices running API level >= 24
                // so if you only target API level 24+ you can remove this statement
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    requireActivity().sendBroadcast(
                        Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                    )
                }


                // If the folder selected is an external media directory, this is
                // unnecessary but otherwise other apps will not be able to access our
                // images unless we scan them using [MediaScannerConnection]
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(savedUri.toFile().extension)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(savedUri.toFile().absolutePath),
                    arrayOf(mimeType)
                ) { _, uri ->
                    Log.d(CAMERA_X_TAG, "Image capture scanned into media store: $uri")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(CAMERA_X_TAG, "Photo capture failed: ${exception.message}", exception)
            }
        })
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

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)


    }
    enum class Direction {
        NEXT, PREV, NOMOVE
    }

}