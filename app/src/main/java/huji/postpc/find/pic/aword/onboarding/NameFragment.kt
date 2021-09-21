package huji.postpc.find.pic.aword.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import huji.postpc.find.pic.aword.R

class NameFragment : Fragment(R.layout.fragment_name) {

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

        // Disable button until we have a valid username
        signUpButton.isEnabled = false

        // Set an observer for the username text field
        usernameTextField.editText?.doOnTextChanged { text, _, _, _ ->
            // Reset username text field error when typing
            usernameTextField.error = ""
            // Get newly typed username and update view model
            val newUsername = text.toString()
            // If the username is valid, store it inside the view-model and enable button
            if (onboardingViewModel.isUsernameValid(newUsername)){
                onboardingViewModel.username = newUsername
                signUpButton.isEnabled = true
            }
            else{
                signUpButton.isEnabled = false
                usernameTextField.error = getString(R.string.username_field_invalid)

            }
        }

        // Set click listener for signup button
        signUpButton.setOnClickListener {
            // Hide keyboard and continue to the choose language fragment
            hideSoftKeyboard(usernameTextField)
            val action = NameFragmentDirections.actionSignUpFragmentToChooseLanguageFragment()
            findNavController().navigate(action)
        }

    }

    private fun hideSoftKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(view.windowToken, 0)
    }


}