package huji.postpc.find.pic.aword.game.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R


class ChooseCategoryFragment : Fragment(R.layout.fragment_choose_category) {

    private lateinit var recyclerView: RecyclerView
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var userAreaButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Reset current category
        gameViewModel.currCategoryResIdLiveData.value = null

        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // Set onItemClick callback for adapter
        val onItemClick: (Int) -> Unit = { categoryNameResId ->
            gameViewModel.currCategoryResIdLiveData.value = categoryNameResId
            val action = ChooseCategoryFragmentDirections.actionChooseCategoryFragmentToCategoryFragment()
            findNavController().navigate(action)
        }
        val adapter = ChooseCategoryAdapter(gameViewModel.getCategories(), R.layout.choose_category_item_view)
        adapter.onItemClick = onItemClick
        recyclerView.adapter = adapter

        // Set click listener for button leading to user area
        userAreaButton = view.findViewById(R.id.user_area_button)
        userAreaButton.setOnClickListener {
            val action = ChooseCategoryFragmentDirections.actionChooseCategoryFragmentToMyAreaFragment()
            findNavController().navigate(action)
        }
    }
}