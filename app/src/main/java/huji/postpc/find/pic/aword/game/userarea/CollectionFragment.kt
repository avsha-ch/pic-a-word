package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameActivity


class CollectionFragment : Fragment(R.layout.fragment_collection) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var gameActivity: GameActivity
    private lateinit var collectionNameTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameActivity = (activity as GameActivity)
        // Get the category name resource-id argument
        arguments?.let {
            val categoryNameResId = it.get("categoryNameResId") as Int
            // Update the view model
            gameViewModel.currCategoryResId = categoryNameResId
            gameActivity.updateStatusBarColor()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collectionNameTextView = view.findViewById(R.id.my_collection_text_view)

        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            collectionNameTextView.text = getString(currCategoryResId)
        }
        // TODO - compete fragment

    }
}