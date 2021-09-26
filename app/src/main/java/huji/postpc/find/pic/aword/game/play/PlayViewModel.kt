package huji.postpc.find.pic.aword.game.play

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.lang.Exception

class PlayViewModel : ViewModel() {

    // MLKit image labeler
    private var labeler: ImageLabeler
    var labelLiveData = MutableLiveData<Boolean?>(null)


    init {
        // Initialize MLKit's image labeler
        // MLKit will ignore labels found with confidence score less than the threshold
        val labelingOptions = ImageLabelerOptions.Builder().setConfidenceThreshold(LABELER_THRESHOLD).build()
        labeler = ImageLabeling.getClient(labelingOptions)
    }

    private fun labelerOnSuccess(trueLabelText: String, labels: List<ImageLabel>) {
        var currLabel: ImageLabel? = null
        for (label in labels) {
            if (label.text != trueLabelText) {
                continue
            }
            // Found correct label
            if (currLabel == null) {
                Log.d(LABELER_TAG, "Found label with text ${label.text} with confidence ${label.confidence}")
                currLabel = label
            } else {
                // Already found a label, choose the one with the higher confidence
                if (label.confidence > currLabel.confidence) {
                    Log.d(LABELER_TAG, "Found better label with text ${label.text} with confidence ${label.confidence}")
                    currLabel = label
                }
            }
        }
        // After going over all the labels, update the live-data
        labelLiveData.value = currLabel != null
    }

    private fun labelerOnFail(e: Exception) {
        Log.e(LABELER_TAG, "Failed to label $e")
        labelLiveData.value = null
    }

    fun analyzeImage(inputImage: InputImage, trueLabelText: String) {
        labeler.process(inputImage)
            .addOnSuccessListener { labels ->
                labelerOnSuccess(trueLabelText, labels)
            }
            .addOnFailureListener { e ->
                labelerOnFail(e)
            }
    }

    companion object {
        private const val LABELER_THRESHOLD = 0.7f
        private const val LABELER_TAG = "Labeler"

    }
}