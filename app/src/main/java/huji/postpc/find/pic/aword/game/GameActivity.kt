package huji.postpc.find.pic.aword.game

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import huji.postpc.find.pic.aword.R
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class GameActivity : AppCompatActivity() {


    // Declare TTS objects
    private lateinit var ttsEn: TextToSpeech
    private lateinit var ttsHe: TextToSpeech

    // Map between language name resource id to the language's TextToSpeech object
    private val languageTtsMap: HashMap<Int, TextToSpeech> = hashMapOf()

    // Map between language name resource id and the status of their initialization
    private val languageIsTtsInitMap: HashMap<Int, Boolean> = hashMapOf(
        R.string.language_en to false,
        R.string.language_he to false
    )

    private val gameViewModel: GameViewModel by viewModels()

    // A mapping from category to color, used to display a consistent color throughout the entire app
    // The mapping is from a string resource id to the color resource id
    val CATEGORY_COLOR_MAP: HashMap<Int, Int> = hashMapOf(
        R.string.category_house to R.color.around_the_house_yellow,
        R.string.category_food to R.color.food_green,
        R.string.category_vehicles to R.color.vehicles_purple,
        R.string.category_body to R.color.human_body_pink,
        R.string.category_animals to R.color.animals_blue,
        R.string.category_clothing to R.color.clothes_blue,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Picaword)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initializeEnTTS()
        updateStatusBarColor()

    }

    private fun initializeEnTTS() {
        // Initialize TTS object and update status when initialization is done
        val ttsOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                languageIsTtsInitMap[R.string.language_en] = true
                ttsEn.language = Locale.US
                Log.d("TTS", "English TTS init succeeded!")
            } else {
                Log.e("TTS", "English TTS init failed!")
            }
        }
        ttsEn = TextToSpeech(applicationContext, ttsOnInitListener)
        languageTtsMap[R.string.language_en] = ttsEn
    }

    // TODO tts doesnt support hebrew change to another language for game
    private fun initializeHeTTS() {
        // Initialize TTS object and update status when initialization is done
        val ttsOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                languageIsTtsInitMap[R.string.language_he] = true
                ttsHe.language = Locale.US
                Log.d("TTS", "Hebrew TTS init succeeded!")
            } else {
                Log.e("TTS", "Hebrew TTS init failed!")
            }
        }
        ttsHe = TextToSpeech(applicationContext, ttsOnInitListener)
        languageTtsMap[R.string.language_he] = ttsHe
    }

    fun speak(text: String, @StringRes languageNameResId: Int) {
        val tts = languageTtsMap[languageNameResId] ?: return
        val ttsIsInit = languageIsTtsInitMap[languageNameResId] ?: return
        if (!ttsIsInit) {
            Toast.makeText(applicationContext, "TTS not ready yet!", Toast.LENGTH_SHORT).show()
            return
        }
        // else, use the TTS object to speak the given text
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun updateStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val categoryColorResId = if (gameViewModel.currCategoryResId != null) CATEGORY_COLOR_MAP[gameViewModel.currCategoryResId] else R.color.black
        if (categoryColorResId != null) {
            window.statusBarColor = getColor(categoryColorResId)
        }

    }

    companion object {
        val CATEGORY_COLOR_MAP: HashMap<Int, Int> = hashMapOf(
            R.string.category_house to R.color.around_the_house_yellow,
            R.string.category_food to R.color.food_green,
            R.string.category_vehicles to R.color.vehicles_purple,
            R.string.category_body to R.color.human_body_pink,
            R.string.category_animals to R.color.animals_blue,
            R.string.category_clothing to R.color.clothes_blue,
        )

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}