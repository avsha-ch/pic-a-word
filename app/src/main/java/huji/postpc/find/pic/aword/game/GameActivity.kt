package huji.postpc.find.pic.aword.game

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import huji.postpc.find.pic.aword.R
import java.io.File
import java.util.*


class GameActivity : AppCompatActivity() {


    // Declare TTS object and boolean indicator
    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    private val gameViewModel: GameViewModel by viewModels()

    // A mapping from category to color, used to display a consistent color throughout the entire app
    // The mapping is from a string resource id to the color resource id
    val CATEGORY_COLOR_MAP : HashMap<Int, Int> = hashMapOf(
        R.string.category_house to R.color.around_the_house_yellow,
        R.string.category_food to R.color.food_green,
        R.string.category_vehicles to R.color.vehicles_purple,
        R.string.category_body to R.color.human_body_pink,
        R.string.category_animals to R.color.animals_blue,
        R.string.category_clothing to R.color.clothes_blue,
//        R.string.category_misc to TODO not appearing in figma, we have enough categories as it is no?
        // Todo add category mix to view model and then color here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Picaword)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initializeTTS()
        updateStatusBarColor()

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
        val categoryColorResId = if (gameViewModel.currCategoryResId != null) CATEGORY_COLOR_MAP[gameViewModel.currCategoryResId] else R.color.black
        if (categoryColorResId != null){
            window.statusBarColor = getColor(categoryColorResId)
        }

    }

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}