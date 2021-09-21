package huji.postpc.find.pic.aword.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameActivity

class OnboardingActivity : AppCompatActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        // Set an observer for onboarding process so activity will start the game when onboarding is done
        setOnboardingDoneObserver()
    }


    private fun setOnboardingDoneObserver() {
        val onboardingDoneObserver = Observer<Boolean> { onboardingDone ->
            PicAWordApp.instance.onboardingDone = onboardingDone
            if (onboardingDone) {
                // Complete onboarding by saving user details
                onboardingViewModel.currUserLanguage?.let { PicAWordApp.instance.completeOnboarding(onboardingViewModel.username, it) }
                // Start the game activity and kill the onboarding activity
                val gameActivityIntent = Intent(this@OnboardingActivity, GameActivity::class.java)
                startActivity(gameActivityIntent)
                this.finish()
            }
        }
        onboardingViewModel.onboardingDoneLiveData.observe(this, onboardingDoneObserver)
    }
}