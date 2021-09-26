package huji.postpc.find.pic.aword.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import huji.postpc.find.pic.aword.game.models.Language

class OnboardingViewModel : ViewModel() {

    // OnboardingActivity listens to this and once onboarding is done, finish the onboarding process and starts the game
    val onboardingDoneLiveData = MutableLiveData(false)

    var username: String = ""
    var currUserLanguage : Language? = null


    fun isUsernameValid(newUsername: String): Boolean {
        // Returns true iff the username's length is at least the minimum length and consists only of letters
        return (newUsername.length >= USERNAME_MIN_LENGTH) && (newUsername.length <= USERNAME_MAX_LENGTH) && (newUsername.all { it.isLetter() })
    }


    companion object {
        private const val USERNAME_MIN_LENGTH = 3
        private const val USERNAME_MAX_LENGTH = 10
    }
}