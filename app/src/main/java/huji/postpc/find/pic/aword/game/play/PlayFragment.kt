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
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.game.OnSwipeTouchListener
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
    private lateinit var outputDirectory: File

    // camera provides access to CameraControl & CameraInfo
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraViewFinder: PreviewView
    private var capturedImageBitmap: Bitmap? = null

    // UI components
    private lateinit var captureFab: FloatingActionButton
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
    private var currLevel: Level? = null
    private var word: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get activity context
        gameActivity = (activity as GameActivity)
        // Initialize our background executor, which is used for camera options that are blocking
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraViewFinder = view.findViewById(R.id.img_placeholder)
        // Request camera permissions if not already granted
//        if (!allPermissionsGranted()) {
//            activity?.let { ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS) }
//        }

        // Wait for the views to be properly laid out and then set up camera and use cases
        cameraViewFinder.post { setUpCamera() }
        // set output directory
        outputDirectory = GameActivity.getOutputDirectory(requireContext())

        // Find all views
        wordListenButton = view.findViewById(R.id.word_listen_button)
        captureFab = view.findViewById(R.id.capture_fab)
        wordImgView = view.findViewById(R.id.word_image_view)
        tryAgainMsg = view.findViewById(R.id.try_again_message)
        levelSuccessMsg = view.findViewById(R.id.level_success_message)
        categoryCompleteMsg = view.findViewById(R.id.category_complete_message)
        waitForLabelerProgressBar = view.findViewById(R.id.wait_for_labeler)


        // Get information from view model
        val currCategoryResId = gameViewModel.currCategoryResIdLiveData.value
        if (currCategoryResId != null) {
            // Get all levels
            levels = gameViewModel.getCategoryNotCompletedLevels()
            // Update current level view
            updateDisplayLevel(Direction.NO_MOVE)
            // Update category information
            val categoryColorResId = GameActivity.CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }


        // Set click listener for button
        wordListenButton.setOnClickListener {
            if (gameViewModel.currLanguageResIdLiveData.value == R.string.language_he) {
                Snackbar.make(wordListenButton, getString(R.string.heb_tts_not_supported), Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
            }
            gameActivity.speak(word)
        }
        // Set click listener for capture picture button
        captureFab.setOnClickListener {
            captureFab.isEnabled = false
            invisibleToVisible(waitForLabelerProgressBar, 1L) { captureImage() }
        }

        // Set swipe listeners for next and previous levels
        wordImgView.setOnTouchListener(object : OnSwipeTouchListener(gameActivity) {
            override fun onSwipeLeft() {
                if (levelIdx < levels.size - 1) {
                    levelIdx++
                    updateDisplayLevel(Direction.NEXT)
                }
            }

            override fun onSwipeRight() {
                if (levelIdx > 0) {
                    levelIdx--
                    updateDisplayLevel(Direction.PREV)
                }
            }
        })

        // If the current language learned is Hebrew, disable TTS option and show a message
        if (gameViewModel.currLanguageResIdLiveData.value == R.string.language_he) {
            wordListenButton.setIconResource(R.drawable.ic_baseline_volume_off_24)
        }

        // Set an observer for the labeler live data
        val labelObserver = Observer<Boolean?> { label ->
            if (label == false) {
                // Wrong label found
                appearDisappearView(tryAgainMsg, SUCCESS_FAIL_MSG_DURATION, ::disappearWaitForLabelerProgressBar)
            } else if (label == true) {
                // Else, found the correct label!
                onLevelSuccess()
            }
        }
        playViewModel.labelLiveData.observe(viewLifecycleOwner, labelObserver)
    }

    private fun updateDisplayLevel(dir: Direction) {
        currLevel = levels.getOrNull(levelIdx)
        if (currLevel == null) {
            return
        }
        gameViewModel.currLevelResIdLiveData.value = currLevel!!.nameResId
        val translationDir = when (dir) {
            Direction.NEXT -> resources.getDimension(R.dimen.translationTemplateOutIn)
            Direction.PREV -> -resources.getDimension(R.dimen.translationTemplateOutIn)
            else -> 0f
        }
        animateViewOutIn(wordImgView, translationDir, ::changeLevelUi, currLevel!!)
    }

    private fun onLevelSuccess() {
        appearDisappearView(levelSuccessMsg, SUCCESS_FAIL_MSG_DURATION, ::disappearWaitForLabelerProgressBar)

        // save image with template to device
        val templateImageBitmap = wordImgView.drawToBitmap()
        val mergedImageBitmap = capturedImageBitmap!!.with(templateImageBitmap)
        saveImage(mergedImageBitmap, requireContext(), "PicAWord")

        // Set level completed if found a correct label
        gameViewModel.setLevelCompleted()
        // Update levels and set curr level to the start
        levels = gameViewModel.getCategoryNotCompletedLevels()
        // Reset index to show the first level in the remaining levels
        levelIdx = 0
        updateDisplayLevel(Direction.NO_MOVE)
        if (levels.isEmpty()) {
            // navigate back to CategoryFragment
            onCategoryFinish()
        }
    }

    private fun changeLevelUi(currLevel: Level) {
        word = PicAWordApp.instance.configsContextMap[gameViewModel.currLanguageResIdLiveData.value]!!.getString(currLevel.nameResId)
        changeTextAnimation(wordListenButton, word)
        // Update image
        wordImgView.setImageResource(currLevel.imgResId)
        tryAgainMsg.text = getString(R.string.not_correct_try_again, word)
    }


    private fun onCategoryFinish() {
        val action = PlayFragmentDirections.actionPlayFragmentToCategoryFragment()
        findNavController().navigate(action)
    }


    private fun disappearWaitForLabelerProgressBar() {
        visibleToInvisible(waitForLabelerProgressBar, 1L) { captureFab.isEnabled = true }
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
        if (currLevel == null) {
            return
        }
        val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
        ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // handle ML image recognition
        imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                Log.d("ImageCapture", "Photo capture success: ${imageProxy.imageInfo.timestamp}")
                // unclear "error" on image - "This declaration is opt-in and its usage should be marked with". Does not cause code to crash.
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImageForMLKIT = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    Log.d("ImageCapture", "inputImage prepared: ${imageProxy.imageInfo.timestamp}")
                    // Label image
                    // MLKit's labels are in English so send the english version of our word
                    val wordInEng = PicAWordApp.instance.configsContextMap[R.string.language_en]!!.getString(currLevel!!.nameResId)
                    playViewModel.analyzeImage(inputImageForMLKIT, wordInEng)
                }
                // close image when done with it. after this nothing can be done with the captured image
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("CameraXBasic", "Photo capture failed: ${exception.message}", exception)
            }
        })

        // save captured image to DB
        capturedImageBitmap = cameraViewFinder.bitmap
        if (capturedImageBitmap != null) {
            val fileName = System.currentTimeMillis().toString() + ".png"
            PicAWordApp.instance.fbManager.uploadImageFromBitmap(capturedImageBitmap!!, fileName)
        }
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

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            values.put(MediaStore.Images.Media.IS_PENDING, true)

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                currLevel?.completedImgLocalPath = uri.toString()
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)

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
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
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

//    private fun allPermissionsGranted() =
//        REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }

    private fun hasBackCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

    private fun hasFrontCamera(): Boolean = cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    enum class Direction { NEXT, PREV, NO_MOVE }

    companion object {
        private const val CAMERA_X_TAG = "CameraX"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val SUCCESS_FAIL_MSG_DURATION = 2000L

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }

}