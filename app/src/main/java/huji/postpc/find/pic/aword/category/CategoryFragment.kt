package huji.postpc.find.pic.aword.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.fragment.app.activityViewModels
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import android.view.*
import android.widget.TextView


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var categoryNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the category name resource-id argument
        arguments?.let {
            val categoryNameResId = it.get("categoryNameResId") as Int
            // Update the view model
            mainViewModel.currCategoryResId.value = categoryNameResId
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find category name text view and set the category name and background color
        categoryNameTextView = view.findViewById(R.id.category_name_text_view)
        val currCategoryResId = mainViewModel.currCategoryResId.value
        if (currCategoryResId != null) {
            categoryNameTextView.text = getString(currCategoryResId)
            val categoryColorResId = (activity as MainActivity).CATEGORY_COLOR_MAP[currCategoryResId]
            if (categoryColorResId != null) {
                categoryNameTextView.setBackgroundResource(categoryColorResId)
            }
        }

    }
}
