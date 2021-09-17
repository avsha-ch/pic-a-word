package huji.postpc.find.pic.aword.intro

import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    // TODO create UUID and use it as key for user's document in Firebase?
    var username: String = ""
    set(value) {
        isUsernameValid = isUsernameValid(value)
        if (isUsernameValid) {
            field = value
        }
    }
    var isUsernameValid = false


    private fun isUsernameValid(newUsername: String): Boolean {
        // Returns true iff the username's length is at least the minimum length and consists only of letters
        return (newUsername.length >= USERNAME_MIN_LENGTH) && (newUsername.length <= USERNAME_MAX_LENGTH) && (newUsername.all { it.isLetter() })
    }

    companion object {
        private const val USERNAME_MIN_LENGTH = 3
        private const val USERNAME_MAX_LENGTH = 10
    }
}