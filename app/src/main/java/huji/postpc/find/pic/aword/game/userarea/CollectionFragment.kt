package huji.postpc.find.pic.aword.game.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.OnSwipeTouchListener
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.game.animateViewOutIn
import huji.postpc.find.pic.aword.game.models.Level
import huji.postpc.find.pic.aword.game.play.PlayFragment


class CollectionFragment : Fragment(R.layout.fragment_collection) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var gameActivity: GameActivity

    // UI components
    private lateinit var collectionNameTextView : TextView
    private lateinit var levelNotCompleteMsg : TextView
    private lateinit var wordListenButton : MaterialButton
    private lateinit var wordImage : ImageView

    // All levels for this category by level resource id
    private lateinit var levels: List<Level>

    // Current level displayed
    private var levelIdx: Int = 0

    // Word to display for this game-level
    private var word: String = ""

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

        // Find all views
        collectionNameTextView = view.findViewById(R.id.my_collection_text_view)
        levelNotCompleteMsg = view.findViewById(R.id.level_not_complete_msg)
        wordListenButton = view.findViewById(R.id.word_listen_button)
        wordImage = view.findViewById(R.id.word_image_view)

        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            val currCategory = gameViewModel.getCurrCategory()
            if (currCategory != null){
                levels = currCategory.levels

                updateDisplayLevel(PlayFragment.Direction.NOMOVE)
            }
            collectionNameTextView.text = getString(currCategoryResId)

            val categoryColorResId = (activity as GameActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }

        // Set click listener for button
        wordListenButton.setOnClickListener {
            gameActivity.speak(word)
        }

        wordImage.setOnTouchListener(object : OnSwipeTouchListener(gameActivity){
            override fun onSwipeLeft(){
                if (levelIdx < levels.size - 1) {
                    levelIdx++
                    updateDisplayLevel(PlayFragment.Direction.NEXT)
                }
            }
            override fun onSwipeRight(){
                if (levelIdx > 0) {
                    levelIdx--
                    updateDisplayLevel(PlayFragment.Direction.PREV)
                }
            }
        })

        levelNotCompleteMsg.setOnTouchListener(object : OnSwipeTouchListener(gameActivity){
            override fun onSwipeLeft(){
                if (levelIdx < levels.size - 1) {
                    levelIdx++
                    updateDisplayLevel(PlayFragment.Direction.NEXT)
                }
            }
            override fun onSwipeRight(){
                if (levelIdx > 0) {
                    levelIdx--
                    updateDisplayLevel(PlayFragment.Direction.PREV)
                }
            }
        })
    }

    private fun changeLevelUi(currLevel : Level) {
        word = getString(currLevel.nameResId)
        wordListenButton.text = word
        // Update image
        if (currLevel.isCompleted){
            levelNotCompleteMsg.visibility = View.INVISIBLE
            // TODO: get user's image of this level and display it
            wordImage.setImageResource(currLevel.imgResId)
        }
        else{
            // level not completed
            levelNotCompleteMsg.visibility = View.VISIBLE
            wordImage.setImageResource(currLevel.imgResId)
        }

    }

    private fun updateDisplayLevel(dir : PlayFragment.Direction) {
        val currLevel = levels.getOrNull(levelIdx)

        if (currLevel != null) {
            val translationDir = when (dir){
                PlayFragment.Direction.NEXT -> {resources.getDimension(R.dimen.translationTemplateOutIn)}
                PlayFragment.Direction.PREV -> {-resources.getDimension(R.dimen.translationTemplateOutIn)}
                else -> {0f}
            }
            animateViewOutIn(wordImage, translationDir, ::changeLevelUi, currLevel)
        }
    }
}