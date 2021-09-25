package huji.postpc.find.pic.aword

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.google.gson.GsonBuilder
import huji.postpc.find.pic.aword.game.data.FirestoreDatabaseManager
import huji.postpc.find.pic.aword.game.models.Language
import huji.postpc.find.pic.aword.game.models.User
import java.util.*
import kotlin.collections.HashMap

class PicAWordApp : Application() {

    // This enable us to de/serialize complex key in maps, e.g. custom classes
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var sp: SharedPreferences
    lateinit var fbManager: FirestoreDatabaseManager

    var user: User? = null
    var onboardingDone: Boolean = false

    // Filled inside onCreate
    var configsContextMap : HashMap<Int, Context> = hashMapOf()

    override fun onCreate() {
        super.onCreate()
        instance = this
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        fbManager = FirestoreDatabaseManager()

        // Load the saved data about onboarding and user, if exists
        onboardingDone = sp.getBoolean(SP_ONBOARDING_DONE_KEY, false)
        val userJson = sp.getString(SP_USER_KEY, "")
        if (userJson != "") {
            user = gson.fromJson(userJson, User::class.java)
        }

        configsContextMap = hashMapOf(
            R.string.language_en to createConfigContext(R.string.language_en),
            R.string.language_he to createConfigContext(R.string.language_he),
            R.string.language_fr to createConfigContext(R.string.language_fr)
        )
    }


    fun completeOnboarding(username: String, language: Language) {
        // Update the name and onboarding status
        onboardingDone = true
        // Create new user
        user = User(username, language)
        // Save user to SP
        val userJson = gson.toJson(user)
        sp.edit().putString(SP_USER_KEY, userJson).apply()
        // Save onboarding status to SP
        sp.edit().putBoolean(SP_ONBOARDING_DONE_KEY, onboardingDone).apply()
    }

    fun saveToSP() {
        val userJson = gson.toJson(user)
        sp.edit().putString(SP_USER_KEY, userJson).apply()
    }

    private fun createConfigContext(@StringRes languageNameResId: Int): Context {
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(LANGUAGE_LOCAL_MAP[languageNameResId])
        return createConfigurationContext(configuration)
    }


    companion object {
        lateinit var instance: PicAWordApp private set

        // SharedPreferences name and keys
        private const val SP_NAME = "sp_pic_a_word"
        private const val SP_ONBOARDING_DONE_KEY = "sp_key_onboarding_done"
        private const val SP_USER_KEY = "sp_key_user"


        // List of all available languages in game
        val AVAILABLE_LANGUAGES: List<Language> = listOf(
            Language(R.string.language_en, R.drawable.ic_usa_flag),
            Language(R.string.language_he, R.drawable.ic_israel_flag),
            Language(R.string.language_fr, R.drawable.ic_french_flag)
        )
        val SOON_LANGUAGES: List<Language> = listOf(
            Language(R.string.language_sp, R.drawable.ic_spain_flag)
        )
        val LANGUAGE_LOCAL_MAP: HashMap<Int, Locale> = hashMapOf(
            R.string.language_en to Locale("en"),
            R.string.language_he to Locale("he"),
            R.string.language_fr to Locale("fr")
        )


    }
}





