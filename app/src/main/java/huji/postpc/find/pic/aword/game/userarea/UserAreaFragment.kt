package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import androidx.navigation.fragment.findNavController
import huji.postpc.find.pic.aword.game.category.ChooseCategoryAdapter
import huji.postpc.find.pic.aword.game.category.ChooseCategoryFragmentDirections

class UserAreaFragment : Fragment(R.layout.fragment_user_area) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myCollectionButton : MaterialButton
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = gameViewModel.gameData?.let { UserAreaAdapter(it.getAllCategories()) }

        myCollectionButton = view.findViewById(R.id.my_area_collection_button)
        myCollectionButton.setOnClickListener {
            val action = UserAreaFragmentDirections.actionUserAreaFragmentToCollectionChooseCategoryFragment()
            findNavController().navigate(action)
        }
    }
}