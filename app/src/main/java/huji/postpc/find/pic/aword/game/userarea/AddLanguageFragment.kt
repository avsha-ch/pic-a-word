package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.game.models.Language


class AddLanguageFragment : Fragment(R.layout.fragment_add_language) {

    private lateinit var recyclerView: RecyclerView
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.add_language_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val adapter = AddLanguageAdapter(PicAWordApp.AVAILABLE_LANGUAGES + PicAWordApp.SOON_LANGUAGES)
        // Set onItemClick callback for adapter
        val onItemClick: (Language) -> Unit = { language ->
            if (language in PicAWordApp.SOON_LANGUAGES) {
                // Don't add languages which are not ready, show a quick message to user
                Snackbar.make(recyclerView, getString(R.string.language_not_ready_yet), Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
            } else {
                gameViewModel.addNewLanguage(language)
                // Save to SP
                PicAWordApp.instance.saveToSP()
                val action = AddLanguageFragmentDirections.actionAddLanguageFragmentToUserAreaFragment()
                findNavController().navigate(action)
            }
        }
        adapter.onItemClick = onItemClick
        recyclerView.adapter = adapter
    }
}