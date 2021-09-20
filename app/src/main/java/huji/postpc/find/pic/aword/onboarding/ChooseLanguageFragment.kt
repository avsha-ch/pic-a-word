package huji.postpc.find.pic.aword.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.R


class ChooseLanguageFragment : Fragment(R.layout.fragment_choose_language) {

    // TODO implement menu for language, add to view model, application class
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private lateinit var finishOnBoardingButton : MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find all UI views
        finishOnBoardingButton = view.findViewById(R.id.finish_onboarding_button)

        finishOnBoardingButton.setOnClickListener {
            // Activity will observe this and finish the onboarding process
            onboardingViewModel.onboardingDoneLiveData.value = true
        }
    }
}