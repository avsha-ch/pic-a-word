package huji.postpc.find.pic.aword

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.util.*

class PicAWordApp : Application() {

    private lateinit var sp: SharedPreferences

    var username: String = ""
    private var userId: String = ""
    // todo user chosen language
    var onboardingDone: Boolean = false

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        // Load the saved data about onboarding and user, if exists
        onboardingDone = sp.getBoolean(SP_ONBOARDING_DONE_KEY, false)
        username = sp.getString(SP_USER_NAME_KEY, "").toString()
        userId = sp.getString(SP_USER_ID_KEY, "").toString()

    }

    fun completeOnboarding(newUsername : String){
        // Update the name and onboarding status
        username = newUsername
        onboardingDone = true
        // Create id for user
        userId = UUID.randomUUID().toString()
        // Save to SP
        sp.edit().putBoolean(SP_ONBOARDING_DONE_KEY, onboardingDone).apply()
        sp.edit().putString(SP_USER_NAME_KEY, username).apply()
        sp.edit().putString(SP_USER_ID_KEY, userId).apply()
    }

    companion object {
        private const val SP_NAME = "sp_pic_a_word"
        private const val SP_ONBOARDING_DONE_KEY = "sp_key_onboarding_done"
        private const val SP_USER_NAME_KEY = "sp_key_user_name"
        private const val SP_USER_ID_KEY = "sp_key_user_id"
    }
}