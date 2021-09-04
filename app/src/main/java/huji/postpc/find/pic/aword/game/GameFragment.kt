package huji.postpc.find.pic.aword.game

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import huji.postpc.find.pic.aword.MainActivity
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

    // mlkit image labeler
    private lateinit var labeler: ImageLabeler

    // UI components
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var wordTextView: TextView
    private lateinit var captureButton: FloatingActionButton

    // Word to display for this game-level
    private lateinit var word : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            word = it.getString("word").toString()
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

        // setup labeler
        val labelingOptions = ImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build()
        labeler = ImageLabeling.getClient(labelingOptions)

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

    private fun captureImage(){

        imageCapture?.let { imageCapture ->

            // capture a picture and store it in memory only (image is not(!) saved to local disk)
            imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        Log.e("ImageCapture", "Photo capture success: ${imageProxy.imageInfo.timestamp}")
                        // unclear "error" on image - "This declaration is opt-in and its usage should be marked with". Does not cause code to crash.
                        val mediaImage = imageProxy.image
                        if (mediaImage != null){
                            val inputImageForMLKIT = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            Log.e("ImageCapture", "inputImage prepared: ${imageProxy.imageInfo.timestamp}")

                            // classify captured image
                            labeler.process(inputImageForMLKIT).addOnSuccessListener { labels ->
                                Log.e("ImageLabel", "received labels!")
                                // TODO: check we got the correct label
                                // idea - only labels with confidence over the given threshold are received in 'labels'.
                                //  maybe consider checking if the label we are looking for is in one of the returned labels(even if not the one with the highest confidence)
                                for (label in labels) {
                                    val text = label.text
                                    val confidence = label.confidence
                                    val index = label.index
                                    Log.e("ImageLabel", "text: ${text}\n, conf: ${confidence}\n, index: ${index}")
                                }
                            }.addOnFailureListener { e -> Log.e("ImageLabel", "failed to label. ${e}") }
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