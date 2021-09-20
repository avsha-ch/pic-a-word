package huji.postpc.find.pic.aword.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    // OnboardingActivity listens to this and once onboarding is done, finish the onboarding process and starts the game
    val onboardingDoneLiveData = MutableLiveData(false)

    var username: String = ""
    var isUsernameValid = false


    fun updateUserDetails(newUsername: String){
        isUsernameValid = isUsernameValid(newUsername)
        if (!isUsernameValid){
            // Don't update user details
            return
        }
        // Create UUID for use and store the details
        username = newUsername
    }

    private fun isUsernameValid(newUsername: String): Boolean {
        // Returns true iff the username's length is at least the minimum length and consists only of letters
        return (newUsername.length >= USERNAME_MIN_LENGTH) && (newUsername.length <= USERNAME_MAX_LENGTH) && (newUsername.all { it.isLetter() })
    }




    companion object {
        private const val USERNAME_MIN_LENGTH = 3
        private const val USERNAME_MAX_LENGTH = 10
        private const val UUID_LEN = 12
    }
}