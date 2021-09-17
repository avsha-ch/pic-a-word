package huji.postpc.find.pic.aword.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import huji.postpc.find.pic.aword.R

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var signUpButton: Button
    private lateinit var usernameTextField: TextInputLayout

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find all views
        signUpButton = view.findViewById(R.id.sign_up_button)
        usernameTextField = view.findViewById(R.id.user_name_text_field)

        // Update username text field based on previous value, if exists
        usernameTextField.editText?.setText(onboardingViewModel.username)

        // Set an observer for the username text field
        usernameTextField.editText?.doOnTextChanged { text, _, _, _ ->
            // Reset username text field error when typing
            usernameTextField.error = ""
            onboardingViewModel.username = text.toString()
        }

        // Set click listener for signup button
        signUpButton.setOnClickListener {
            if (!onboardingViewModel.isUsernameValid) {
                usernameTextField.error = getString(R.string.username_field_invalid)
                return@setOnClickListener
            }
            usernameTextField.error = ""
            val action = SignUpFragmentDirections.actionSignUpFragmentToChooseLanguageFragment()
            findNavController().navigate(action)
        }

    }


}