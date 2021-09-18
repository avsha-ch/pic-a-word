package huji.postpc.find.pic.aword.game

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.models.Level
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class GameFragment : Fragment(R.layout.fragment_game) {

    // The lens of camera the app uses (front/back)
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    // Preview - used to display the camera's feed
    private var preview: Preview? = null

    // Capture and Analyzer for CameraX
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null

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

    // Activity for getColorStateList
    private lateinit var mainActivity: MainActivity



    // View models
    private val gameViewModel: GameViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    // All levels for this category by level resource id
    private lateinit var levels : List<Level>
    // Current level displayed
    private var levelIdx : Int = 0
    // Word to display for this game-level
    private var word: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get activity context
        mainActivity = (activity as MainActivity)
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

        // Find all views
        wordListenButton = view.findViewById(R.id.word_listen_button)
        captureButton = view.findViewById(R.id.capture_fab)
        previousImgButton = view.findViewById(R.id.previous_img_button)
        nextImgButton = view.findViewById(R.id.next_img_button)

        // Get information from view model
        val currCategoryResId = mainViewModel.currCategoryResId
        if (currCategoryResId != null) {
            // Get all levels
            val currCategory = mainViewModel.gameData[currCategoryResId]
            if (currCategory != null) {
                levels = currCategory.levels
                // Initialize current level to be the first one
                updateDisplayLevel()
            }
            // Update category information
            val categoryColorResId = (activity as MainActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = mainActivity.getColorStateList(categoryColorResId)
                previousImgButton.backgroundTintList = mainActivity.getColorStateList(categoryColorResId)
                nextImgButton.backgroundTintList = mainActivity.getColorStateList(categoryColorResId)
            }
            }




        // Set click listener for button
        wordListenButton.setOnClickListener {
            mainActivity.speak(word)
        }
        // Set click listener for capture picture button
        captureButton.setOnClickListener {
            captureImage()
        }
        // Todo set listeners for previous and next image button
        previousImgButton.setOnClickListener {
            if (levelIdx > 0){
                levelIdx--
                updateDisplayLevel()
            }
        }
        nextImgButton.setOnClickListener {
            if (levelIdx < levels.size - 1){
                levelIdx++
                updateDisplayLevel()
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
        gameViewModel.labelLiveData.observe(viewLifecycleOwner, labelObserver)
    }

    private fun updateDisplayLevel(){
        val currLevel = levels.getOrNull(levelIdx)
        word = currLevel?.let { getString(it.nameResId) }.toString()
        wordListenButton.text = word
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
        imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                Log.e("ImageCapture", "Photo capture success: ${imageProxy.imageInfo.timestamp}")
                // unclear "error" on image - "This declaration is opt-in and its usage should be marked with". Does not cause code to crash.
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImageForMLKIT = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    Log.e("ImageCapture", "inputImage prepared: ${imageProxy.imageInfo.timestamp}")
                    // Label image
                    gameViewModel.analyzeImage(inputImageForMLKIT, word)
                }
                // close image when done with it. after this nothing can be done with the captured image
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraXBasic", "Photo capture failed: ${exception.message}", exception)
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
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


}