package huji.postpc.find.pic.aword.game.category

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import huji.postpc.find.pic.aword.game.play.PlayFragment

import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var categoryNameTextView: TextView
    private lateinit var categoryProgressBar: ProgressBar
    private lateinit var myCollectionButton: MaterialButton
    private lateinit var goButton: MaterialButton
    private lateinit var categoryProgressPercentage: TextView
    private lateinit var gameActivity: GameActivity

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of ActivityResultLauncher
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                val action = CategoryFragmentDirections.actionCategoryFragmentToPlayFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(goButton, getString(R.string.need_permissions), Snackbar.LENGTH_SHORT).setAction(getString(R.string.ok)) {}.show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameActivity = (activity as GameActivity)

        // Find the all the views in the fragment
        categoryNameTextView = view.findViewById(R.id.category_name_text_view)
        categoryProgressBar = view.findViewById(R.id.category_progress_bar)
        myCollectionButton = view.findViewById(R.id.my_collection_button)
        categoryProgressPercentage = view.findViewById(R.id.category_progress_percentage)
        goButton = view.findViewById(R.id.go_button)


        // If the progress is 100% or we received through arguments
        val isCategoryFinished = gameViewModel.isCurrCategoryFinished()
        // Show confetti to congratulate user
        if (isCategoryFinished) {
            goButton.isEnabled = false
            goButton.backgroundTintList = gameActivity.getColorStateList(R.color.background_bright_gray)
            val categoryFinishedTextView: TextView = view.findViewById(R.id.category_finished_text_view)
            categoryFinishedTextView.text = getString(R.string.category_finished_stay_tuned)
            showConfetti(view)
        }

        // Observe current category to update changes in the UI
        val currCategoryResIdObserver: Observer<Int?> = Observer { currCategoryResId ->
            if (currCategoryResId == null) {
                return@Observer
            }
            // Update views
            categoryNameTextView.text = getString(currCategoryResId)
            categoryProgressBar.progress = gameViewModel.getCurrCategoryProgress()
            categoryProgressPercentage.text = getString(R.string.progress_percentage_string, gameViewModel.getCurrCategoryProgress())
            val categoryColorResId = GameActivity.CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                categoryNameTextView.setBackgroundResource(categoryColorResId)
                categoryProgressBar.progressTintList = gameActivity.getColorStateList(categoryColorResId)
                myCollectionButton.backgroundTintList = gameActivity.getColorStateList(categoryColorResId)
            }
        }
        gameViewModel.currCategoryResIdLiveData.observe(viewLifecycleOwner, currCategoryResIdObserver)

        // Set click listener to the 'My Collection' button for transitioning to collection
        myCollectionButton.setOnClickListener {
            val action = CategoryFragmentDirections.actionCategoryFragmentToCollectionFragment()
            findNavController().navigate(action)
        }

        // Set click listener to the 'GO' button for transitioning to the game itself
        goButton.setOnClickListener {
            if (allPermissionsGranted()) {
                val action = CategoryFragmentDirections.actionCategoryFragmentToPlayFragment()
                findNavController().navigate(action)
            } else {
                //  Directly ask for the permission - the registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
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


    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}
