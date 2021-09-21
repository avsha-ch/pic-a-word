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


class ChooseLanguageFragment : Fragment(R.layout.fragment_choose_language) {

    // TODO implement menu for language, add to view model, application class
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

        // Load available languages
        val enLanguage = Language(R.string.language_en, R.drawable.ic_usa_flag)
        val heLanguage = Language(R.string.language_he, R.drawable.ic_israel_flag)
        val items = listOf(enLanguage, heLanguage)

        // Create custom adapter for displaying language's country flag and name
        val adapter = LanguageAdapter(requireContext(), items)
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