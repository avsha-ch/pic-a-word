package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.game.category.ChooseCategoryAdapter


class CollectionChooseCategoryFragment : Fragment(R.layout.fragment_collection_choose_category) {

    private lateinit var recyclerView: RecyclerView
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gameViewModel.currCategoryResIdLiveData.value = null

        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // Set onItemClick callback for adapter
        val onItemClick: (Int) -> Unit = { categoryNameResId ->
            gameViewModel.currCategoryResIdLiveData.value = categoryNameResId
            val action = CollectionChooseCategoryFragmentDirections.actionCollectionChooseCategoryFragmentToCollectionFragment()
            findNavController().navigate(action)
        }
        val adapter = ChooseCategoryAdapter(gameViewModel.getCategories(), R.layout.collection_category_item_view)
        adapter.onItemClick = onItemClick
        recyclerView.adapter = adapter
    }
}