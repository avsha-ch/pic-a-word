package huji.postpc.find.pic.aword.game

import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.Manifest
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import huji.postpc.find.pic.aword.R
import java.util.*
import java.util.concurrent.ExecutorService


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false


    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder : PreviewView



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewFinder = view.findViewById(R.id.camera_view_finder)

        val wordTextView: TextView = view.findViewById(R.id.word_text_view)
        // initialize text to speech
        // Todo - TTS object takes a few seconds to initialize, might be better to have it initialized in MainActivity and expose a speak() function, or some kind of view-model?
        // from MainActivity to all child fragments instead of initializing it everytime when creating a new GameFragment
        tts = TextToSpeech(activity) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // set text-to-speech init flag as true to allow use of it
                ttsInitialized = true
                tts.language = Locale.US
            } else {
                Log.e("TTS", "FAILED TO INIT TTS")
            }
        }

        // set button on-click listener to play the text via TTS
        val playButton: ImageButton = view.findViewById(R.id.img_button_hear_word)
        playButton.setOnClickListener {
            tts.speak(wordTextView.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }

        // Request camera permissions if not already granted
        if (!allPermissionsGranted()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }

    }


    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnable =  {
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            // Build and bind the camera use cases
            bindCameraUseCases()
        }
        cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().build()

        // Preview
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
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(CAMERA_X_TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

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