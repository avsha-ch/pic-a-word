package huji.postpc.find.pic.aword.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.fragment.app.activityViewModels
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.button.MaterialButton


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var categoryNameTextView: TextView
    private lateinit var categoryProgressBar : ProgressBar
    private lateinit var myCollectionButton : MaterialButton
    private lateinit var goButton : MaterialButton
    private lateinit var categoryProgressPercentage : TextView
    private lateinit var activity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = (getActivity() as MainActivity)
        // Get the category name resource-id argument
        arguments?.let {
            val categoryNameResId = it.get("categoryNameResId") as Int
            // Update the view model
            mainViewModel.currCategoryResId = categoryNameResId
            activity.updateStatusBarColor()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goButton = view.findViewById(R.id.go_button)

        // Find the all the views in the fragment
        categoryNameTextView = view.findViewById(R.id.category_name_text_view)
        categoryProgressBar = view.findViewById(R.id.category_progress_bar)
        myCollectionButton = view.findViewById(R.id.my_collection_button)
        categoryProgressPercentage = view.findViewById(R.id.category_progress_percentage)
        // Get current category resource id to update the texts and colors of the views
        val currCategoryResId = mainViewModel.currCategoryResId
        if (currCategoryResId != null) {
            categoryNameTextView.text = getString(currCategoryResId)
            categoryProgressBar.progress = mainViewModel.getCurrCategoryProgress() + 20
            categoryProgressPercentage.text =  "${mainViewModel.getCurrCategoryProgress() + 20}%"
            val categoryColorResId = activity.CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                categoryNameTextView.setBackgroundResource(categoryColorResId)
                categoryProgressBar.progressTintList = activity.getColorStateList(categoryColorResId)
                myCollectionButton.backgroundTintList = activity.getColorStateList(categoryColorResId)
            }
        }

    }
}
