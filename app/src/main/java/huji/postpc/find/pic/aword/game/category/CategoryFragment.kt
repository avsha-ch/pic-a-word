package huji.postpc.find.pic.aword.game.category

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var categoryNameTextView: TextView
    private lateinit var categoryProgressBar: ProgressBar
    private lateinit var myCollectionButton: MaterialButton
    private lateinit var goButton: MaterialButton
    private lateinit var categoryProgressPercentage: TextView
    private lateinit var gameActivity: GameActivity
    private var isCategoryFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameActivity = (activity as GameActivity)
        // Get the category name resource-id argument
        arguments?.let {
            val categoryNameResId = it.get("categoryNameResId") as Int
            // Update the view model
            gameViewModel.currCategoryResId = categoryNameResId
            gameActivity.updateStatusBarColor()
            isCategoryFinished = it.get("isCategoryFinished") as Boolean
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the all the views in the fragment
        categoryNameTextView = view.findViewById(R.id.category_name_text_view)
        categoryProgressBar = view.findViewById(R.id.category_progress_bar)
        myCollectionButton = view.findViewById(R.id.my_collection_button)
        categoryProgressPercentage = view.findViewById(R.id.category_progress_percentage)
        goButton = view.findViewById(R.id.go_button)


        // If the progress is 100% or we received through arguments
        isCategoryFinished = isCategoryFinished || (gameViewModel.getCurrCategoryProgress() == 100) || (gameViewModel.getCurrCategory()?.levels?.isEmpty() == true)
        // Show confetti to congratulate user
        if (isCategoryFinished) {
            goButton.isEnabled = false
            goButton.backgroundTintList = gameActivity.getColorStateList(R.color.background_bright_gray)
            val categoryFinishedTextView : TextView = view.findViewById(R.id.category_finished_text_view)
            categoryFinishedTextView.text = getString(R.string.category_finished_stay_tuned)
            showConfetti(view)
        }


        // Get current category resource id to update the texts and colors of the views
        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            categoryNameTextView.text = getString(currCategoryResId)
            categoryProgressBar.progress = gameViewModel.getCurrCategoryProgress()
            categoryProgressPercentage.text = getString(R.string.progress_percentage_string, gameViewModel.getCurrCategoryProgress())
            val categoryColorResId = gameActivity.CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                categoryNameTextView.setBackgroundResource(categoryColorResId)
                categoryProgressBar.progressTintList = gameActivity.getColorStateList(categoryColorResId)
                myCollectionButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
            // Set click listener to the 'My Collection' button for transitioning to collection
            myCollectionButton.setOnClickListener {
                val action = CategoryFragmentDirections.actionCategoryFragmentToCollectionFragment(categoryNameResId = currCategoryResId)
                findNavController().navigate(action)
            }
        }
        // Set click listener to the 'GO' button for transitioning to the game itself
        goButton.setOnClickListener {
            val action = CategoryFragmentDirections.actionCategoryFragmentToPlayFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            categoryNameTextView.text = getString(currCategoryResId)
            categoryProgressBar.progress = gameViewModel.getCurrCategoryProgress()
            categoryProgressPercentage.text = getString(R.string.progress_percentage_string, gameViewModel.getCurrCategoryProgress())
        }
    }

    private fun showConfetti(view: View) {
        val viewKonfetti = view.findViewById<KonfettiView>(R.id.viewKonfetti)
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(300L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 2000L)
    }

}
