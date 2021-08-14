package huji.postpc.find.pic.aword.game

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import huji.postpc.find.pic.aword.R
import java.util.*


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }
}