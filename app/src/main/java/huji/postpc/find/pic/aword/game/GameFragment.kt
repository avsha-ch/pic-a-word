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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class GameFragment : Fragment(R.layout.fragment_game) {

    // The lens of camera the app uses (front/back)
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraViewFinder: PreviewView


    // UI components
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var wordTextView: TextView
    private lateinit var captureButton: FloatingActionButton

    // Word to display for this game-level
    private var word: String = ""
    private var levelResId : Int = 0
    private val gameViewModel: GameViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
//            word = it.getString("word").toString()
            levelResId = it.get("levelResId") as Int
            mainViewModel.currLevelResId = levelResId
            word = getString(levelResId)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


        // Find text view with the level's word
        wordTextView = view.findViewById(R.id.word_text_view)
        wordTextView.text = word

        // Set click listeners for bottom app bar menu items
        bottomAppBar = view.findViewById(R.id.bottom_app_bar)
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.listen -> {
                    // Handle listen icon press
                    (activity as MainActivity).speak(wordTextView.text.toString())
                    true
                }
                else -> false
            }
        }

        // Set click listener for capture picture button
        captureButton = view.findViewById(R.id.capture_button)
        captureButton.setOnClickListener { captureImage() }

        // Set an observer for the labeler live data
        val labelObserver = Observer<ImageLabel?> { label ->
            if (label == null){
                return@Observer
            }
            // Else, found the correct label!
            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
            // Set level completed if found a correct label
            mainViewModel.setCurrLevelCompleted()

        }
        gameViewModel.labelLiveData.observe(viewLifecycleOwner, labelObserver)

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
        // Preview - used to display the camera's feed
        preview = Preview.Builder().build()
        // ImageCapture
        imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder().build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
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
    //                      // Label image
                    gameViewModel.analyzeImage(inputImageForMLKIT, word)
                }
                // close image when done with it. after this nothing can be done with the captured image
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraXBasic", "Photo capture failed: ${exception.message}", exception)
            }
        }
        )
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }


    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

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