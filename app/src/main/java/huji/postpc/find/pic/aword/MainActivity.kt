package huji.postpc.find.pic.aword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.onboarding.OnboardingActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val app = application as PicAWordApp

        // Start onboarding process if the user hasn't finished it yet, else start the game activity
        if (!app.onboardingDone) {
            val onboardingActivityIntent = Intent(this@MainActivity, OnboardingActivity::class.java)
            startActivity(onboardingActivityIntent)
        } else {
            val gameActivityIntent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(gameActivityIntent)
        }
        this.finish()
    }
}