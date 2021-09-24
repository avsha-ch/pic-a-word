package huji.postpc.find.pic.aword.game.userarea

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import huji.postpc.find.pic.aword.game.models.Language
import huji.postpc.find.pic.aword.onboarding.LanguageAdapter

class UserAreaFragment : Fragment(R.layout.fragment_user_area) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myCollectionButton: MaterialButton
    private lateinit var shareAppButton: Button
    private lateinit var languageMenuTextField: TextInputLayout
    private lateinit var helloUserTextView : TextView
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = UserAreaAdapter(gameViewModel.gameData.getAllCategories())

        // Set hello message with username
        helloUserTextView = view.findViewById(R.id.my_area_msg_text_view)
        helloUserTextView.text = getString(R.string.hello_username, gameViewModel.user?.username)

        myCollectionButton = view.findViewById(R.id.my_area_collection_button)
        myCollectionButton.setOnClickListener {
            val action = UserAreaFragmentDirections.actionUserAreaFragmentToCollectionChooseCategoryFragment()
            findNavController().navigate(action)
        }

        shareAppButton = view.findViewById(R.id.share_app_button)
        shareAppButton.setOnClickListener { shareAppLink() }

        languageMenuTextField = view.findViewById(R.id.language_menu_text_field)
        val userLanguages = gameViewModel.getUserLanguages()
        // Add the special 'Add Language' language
        userLanguages.add(Language(R.string.add_language, R.drawable.ic_baseline_add_box_36))
        // Create custom adapter for displaying language's country flag and name
        val adapter = LanguageAdapter(requireContext(), userLanguages)
        // Initialize default value to the current language
        (languageMenuTextField.editText as? AutoCompleteTextView)?.setText(gameViewModel.user?.currUserLanguage?.let { getString(it.nameResId) })
        (languageMenuTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Set onclick listener to get the chosen language
        (languageMenuTextField.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedLanguage: Language = adapter.getItem(position) ?: return@OnItemClickListener
                // If the language is the special 'Add language', navigate to the add language screen
                if (selectedLanguage.nameResId == R.string.add_language){
                    val action = UserAreaFragmentDirections.actionUserAreaFragmentToAddLanguageFragment()
                    findNavController().navigate(action)
                }
                // Else, update the user current language
                gameViewModel.switchLanguage(selectedLanguage)
                // Refresh the progress adapter to see the new language progress
                adapter.notifyDataSetChanged()

                // todo add live data to curr language ,when changes update the game data and views
                // do I still need this todo after calling notifyDataSetChanged() ??
            }
    }


    private fun shareAppLink() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Invite to Pic a Word")
            putExtra(
                Intent.EXTRA_TEXT,
                "Come learn languages interactively with Pic A Word\n https://play.google.com/store/apps/details?id=com.tutorial.personal.androidstudiopro "
            )   // TODO: put link to app store
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}