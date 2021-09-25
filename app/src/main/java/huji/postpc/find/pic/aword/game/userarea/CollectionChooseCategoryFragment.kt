package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.GameViewModel


class CollectionChooseCategoryFragment : Fragment(R.layout.fragment_collection_choose_category) {

    private lateinit var recyclerView: RecyclerView
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gameViewModel.currCategoryResId = null
        (activity as GameActivity).updateStatusBarColor()

        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = CollectionCategoryAdapter(gameViewModel.getCategories())

    }
}