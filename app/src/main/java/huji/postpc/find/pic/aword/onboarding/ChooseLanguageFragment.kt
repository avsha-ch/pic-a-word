package huji.postpc.find.pic.aword.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Language
import android.widget.AdapterView.OnItemClickListener
import huji.postpc.find.pic.aword.PicAWordApp


class ChooseLanguageFragment : Fragment(R.layout.fragment_choose_language) {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private lateinit var finishOnBoardingButton: MaterialButton
    private lateinit var languageMenuTextField: TextInputLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find all UI views
        finishOnBoardingButton = view.findViewById(R.id.finish_onboarding_button)
        languageMenuTextField = view.findViewById(R.id.language_menu_text_field)

        // Disable button until user choose language
        finishOnBoardingButton.isEnabled = false


        // Create custom adapter for displaying available languages
        val adapter = LanguageAdapter(requireContext(), PicAWordApp.AVAILABLE_LANGUAGES)
        (languageMenuTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Set onclick listener to get the chosen language
        (languageMenuTextField.editText as AutoCompleteTextView).onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val selectedLanguage: Language? = adapter.getItem(position)
                if (selectedLanguage != null) {
                    // Set the first language the user chose
                    onboardingViewModel.currUserLanguage = selectedLanguage
                    // Enable the button to finish onboarding
                    finishOnBoardingButton.isEnabled = true
                }
            }

        finishOnBoardingButton.setOnClickListener {
            // Activity will observe this and finish the onboarding process
            onboardingViewModel.onboardingDoneLiveData.value = true
        }
    }
}