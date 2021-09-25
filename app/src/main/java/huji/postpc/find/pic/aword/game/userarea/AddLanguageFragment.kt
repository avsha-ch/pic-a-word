package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.AddLanguageAdapter
import huji.postpc.find.pic.aword.game.GameViewModel


class AddLanguageFragment : Fragment(R.layout.fragment_add_language) {

    private lateinit var recyclerView: RecyclerView
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.add_language_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val adapter = AddLanguageAdapter(PicAWordApp.AVAILABLE_LANGUAGES)
        adapter.onItemClick = { language ->
            gameViewModel.addNewLanguage(language)
            val action = AddLanguageFragmentDirections.actionAddLanguageFragmentToUserAreaFragment()
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter
    }

}