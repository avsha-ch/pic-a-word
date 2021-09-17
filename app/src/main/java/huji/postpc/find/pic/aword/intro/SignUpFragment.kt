package huji.postpc.find.pic.aword.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import huji.postpc.find.pic.aword.R

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var signUpButton : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpButton = view.findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToChooseLanguageFragment()
            findNavController().navigate(action)
        }

    }
}