package huji.postpc.find.pic.aword

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
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


    override fun onCreate() {
        super.onCreate()
        instance = this
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        fbManager = FirestoreDatabaseManager()

        // TODO for debug
//        sp.edit().clear().apply()
        // TODO


        // Load the saved data about onboarding and user, if exists
        onboardingDone = sp.getBoolean(SP_ONBOARDING_DONE_KEY, false)
        val userJson = sp.getString(SP_USER_KEY, "")
        if (userJson != "") {
            user = gson.fromJson(userJson, User::class.java)
        }
    }


    fun completeOnboarding(newUsername: String, userInitialLanguage: Language) {
        // Update the name and onboarding status
        onboardingDone = true
        // Create new user
        user = User(newUsername, userInitialLanguage)
        // Save user to SP
        val userJson = gson.toJson(user)
        sp.edit().putString(SP_USER_KEY, userJson).apply()
        // Save onboarding status to SP
        sp.edit().putBoolean(SP_ONBOARDING_DONE_KEY, onboardingDone).apply()
    }


    companion object {
        lateinit var instance: PicAWordApp private set

        // List of all available languages in game
        val AVAILABLE_LANGUAGES: List<Language> = listOf(
            Language(R.string.language_he, R.drawable.ic_israel_flag),
            Language(R.string.language_en, R.drawable.ic_usa_flag),
            Language(R.string.language_sp, R.drawable.ic_spain_flag)
        )
        val LANGUAGE_LOCAL_MAP : HashMap<Language, Locale> = hashMapOf(
            Language(R.string.language_he, R.drawable.ic_israel_flag) to Locale("he"),
            Language(R.string.language_en, R.drawable.ic_usa_flag) to Locale("en")
        )
        // SharedPreferences name and keys
        private const val SP_NAME = "sp_pic_a_word"
        private const val SP_ONBOARDING_DONE_KEY = "sp_key_onboarding_done"
        private const val SP_USER_KEY = "sp_key_user"
    }
}





