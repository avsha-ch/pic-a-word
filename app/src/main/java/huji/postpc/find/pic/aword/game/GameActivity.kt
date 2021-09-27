package huji.postpc.find.pic.aword.game

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import huji.postpc.find.pic.aword.R
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class GameActivity : AppCompatActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    // Declare TTS objects
    private lateinit var ttsEn: TextToSpeech

    private lateinit var ttsFr: TextToSpeech

    // Map between language name resource id to the language's TextToSpeech object
    private val languageTtsMap: HashMap<Int, TextToSpeech> = hashMapOf()

    // Map between language name resource id and the status of their initialization
    private val languageIsTtsInitMap: HashMap<Int, Boolean> = hashMapOf(
        R.string.language_en to false,
        R.string.language_fr to false
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Picaword)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Init all TTS objects for available languages
        initializeEnTTS()
        initializeFrTTS()

        // Observe live data of current category for changing status bar color
        val currCategoryResIdObserver: Observer<Int?> = Observer { currCategoryResId -> updateStatusBarColor(currCategoryResId) }
        gameViewModel.currCategoryResIdLiveData.observe(this, currCategoryResIdObserver)
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

    private fun initializeFrTTS() {
        // Initialize TTS object and update status when initialization is done
        val ttsOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                languageIsTtsInitMap[R.string.language_fr] = true
                ttsFr.language = Locale.FRANCE
                Log.d("TTS", "French TTS init succeeded!")
            } else {
                Log.e("TTS", "French TTS init failed!")
            }
        }
        ttsFr = TextToSpeech(applicationContext, ttsOnInitListener)
        languageTtsMap[R.string.language_fr] = ttsFr
    }

    fun speak(text: String) {
        // Speak the given text in the current language
        val currLanguageResId = gameViewModel.currLanguageResIdLiveData.value ?: return
        if (currLanguageResId == R.string.language_he) {
            // TTS is disabled for Hebrew
            return
        }
        val tts = languageTtsMap[currLanguageResId] ?: return
        val ttsIsInit = languageIsTtsInitMap[currLanguageResId] ?: return
        if (!ttsIsInit) {
            Snackbar.make(findViewById(R.id.content), "TTS not ready yet!", Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
            return
        }
        // else, use the TTS object to speak the given text
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }


    private fun updateStatusBarColor(currCategoryResId: Int?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // Determine status bar update, and update with the color found
        val categoryColorResId = if (currCategoryResId != null) CATEGORY_COLOR_MAP[currCategoryResId] else R.color.black
        if (categoryColorResId != null) {
            window.statusBarColor = getColor(categoryColorResId)
        }
    }

    companion object {
        // A mapping from category resource id to color resource id, used to display a consistent color throughout the entire app
        val CATEGORY_COLOR_MAP: HashMap<Int, Int> = hashMapOf(
            R.string.category_house to R.color.around_the_house_yellow,
            R.string.category_food to R.color.food_green,
            R.string.category_vehicles to R.color.vehicles_purple,
            R.string.category_body to R.color.human_body_pink,
            R.string.category_animals to R.color.animals_blue,
            R.string.category_clothing to R.color.clothes_blue,
        )

        /** Use externalmedia if it is available, our app's file directory otherwise */
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