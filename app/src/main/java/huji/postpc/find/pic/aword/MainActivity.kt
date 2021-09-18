package huji.postpc.find.pic.aword

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {


    // Declare TTS object and boolean indicator
    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    private val mainViewModel: MainViewModel by viewModels()

    // A mapping from category to color, used to display a consistent color throughout the entire app
    // The mapping is from a string resource id to the color resource id
    val CATEGORY_COLOR_MAP : HashMap<Int, Int> = hashMapOf(
        R.string.category_house to R.color.misty_rose,
        R.string.category_food to R.color.azure,
        R.string.category_vehicles to R.color.lavender,
        R.string.category_body to R.color.magic_mint,
        R.string.category_animals to R.color.green,
        R.string.category_clothing to R.color.baby_eyes_blue,
//        R.string.category_misc to TODO not appearing in figma, we have enough categories as it is no?
        // Todo add category mix to view model and then color here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeTTS()
        updateStatusBarColor()
//        // Set observer for current category
//        mainViewModel.currCategoryResId.observe(this, {
//            updateStatusBarColor()
//        })
    }

    private fun initializeTTS(){
        // Initialize TTS object and update status when initialization is done
        val ttsOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInitialized = true
                tts.language = Locale.US
                Log.d("TTS", "TTS init succeeded!")
            } else {
                Log.e("TTS", "TTS init failed!")
            }
        }
        tts = TextToSpeech(applicationContext, ttsOnInitListener)


    }

    fun speak(text : String){
        if (!ttsInitialized){
            Toast.makeText(applicationContext, "TTS not ready yet!", Toast.LENGTH_SHORT).show()
            return
        }
        // else, use the TTS object to speak the given text
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun updateStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val categoryColorResId = if (mainViewModel.currCategoryResId != null) CATEGORY_COLOR_MAP[mainViewModel.currCategoryResId] else R.color.black
        if (categoryColorResId != null){
            window.statusBarColor = getColor(categoryColorResId)
        }

    }

}