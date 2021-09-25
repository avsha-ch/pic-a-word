package huji.postpc.find.pic.aword.game.userarea

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import huji.postpc.find.pic.aword.OnSwipeTouchListener
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.*
import huji.postpc.find.pic.aword.game.models.Level
import huji.postpc.find.pic.aword.game.play.PlayFragment


class CollectionFragment : Fragment(R.layout.fragment_collection) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var gameActivity: GameActivity

    // UI components
    private lateinit var collectionNameTextView : TextView
    private lateinit var wordListenButton : MaterialButton
    private lateinit var playGameButton : MaterialButton
    private lateinit var wordImage : ImageView
    private lateinit var shareFab : FloatingActionButton
    private lateinit var noLevelsCompleteTextView: TextView

    // All levels for this category by level resource id
    private lateinit var levels: List<Level>

    // Current level displayed
    private var levelIdx: Int = 0

    // Word to display for this game-level
    private var word: String = ""

    // uri to the image of the currently displayed level in the collection
    private var imageUri: Uri? = null

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
        wordListenButton = view.findViewById(R.id.word_listen_button)
        wordImage = view.findViewById(R.id.word_image_view)
        shareFab = view.findViewById(R.id.share_fab)
        playGameButton = view.findViewById(R.id.play_game_button)
        noLevelsCompleteTextView = view.findViewById(R.id.no_levels_complete_text)

        val currCategoryResId = gameViewModel.currCategoryResId
        if (currCategoryResId != null) {
            levels = gameViewModel.getCategoryCompletedLevels()

            updateDisplayLevel(PlayFragment.Direction.NOMOVE)
            collectionNameTextView.text = getString(currCategoryResId)

            val categoryColorResId = (activity as GameActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                wordListenButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }

        // Set click listener for button
        wordListenButton.setOnClickListener {
            gameActivity.speak(word, gameViewModel.currLanguageResId)
        }

        shareFab.setOnClickListener {
            imageUri?.let { shareImage(it) }
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

        playGameButton.setOnClickListener {
            val action  = CollectionFragmentDirections.actionCollectionFragmentToPlayFragment()
            findNavController().navigate(action)
        }
    }

    private fun changeLevelUi(currLevel : Level) {
        // Update word text
        word = getString(currLevel.nameResId)
        changeTextAnimation(wordListenButton, word)
        // Update image

        // get user's image of this level and display it
        imageUri = Uri.parse(currLevel.completedImgLocalPath)
        wordImage.setImageURI(imageUri)

//        if (currLevel.isCompleted){
//            shareFab.isClickable = true
//            invisibleToVisible(shareFab)
//
//            visibleToInvisible(levelNotCompleteMsg)
//            visibleToInvisible(levelNotCompleteMsg2)
//            // TODO: get user's image of this level and display it
//            imageUri = Uri.parse(currLevel.completedImgLocalPath)
//            wordImage.setImageURI(imageUri)
//        }
//        else{
//            // level not completed
//            shareFab.isClickable = false
//            visibleToInvisible(shareFab)
//
//            invisibleToVisible(levelNotCompleteMsg)
//            invisibleToVisible(levelNotCompleteMsg2)
//            // TODO: if we want to display template of level - uncomment next line
////            wordImage.setImageResource(currLevel.imgResId)
//            imageUri = null
//        }
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
        else{
            // no completed levels
            wordListenButton.visibility = View.GONE
            visibleToInvisible(shareFab, 1L) { shareFab.isClickable = false }
            playGameButton.visibility = View.VISIBLE
            noLevelsCompleteTextView.visibility = View.VISIBLE
        }
    }

    private fun shareImage(uriToImage : Uri) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uriToImage)
            type = "image/jpeg"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}