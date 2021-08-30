package huji.postpc.find.pic.aword

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {


    // Declare TTS object and boolean indicator
    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            return
            // TODO maybe show a quick toast
        }
        // else, use the TTS object to speak the given text
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

    }

}