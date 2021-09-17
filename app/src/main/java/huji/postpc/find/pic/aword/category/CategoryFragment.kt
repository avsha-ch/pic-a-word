package huji.postpc.find.pic.aword.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.fragment.app.activityViewModels
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import android.view.*


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the category name resource-id argument
        arguments?.let{
            val categoryNameResId = it.get("categoryNameResId") as Int
            // Update the view model
            mainViewModel.currCategoryResId.value = categoryNameResId
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button : Button = view.findViewById(huji.postpc.find.pic.aword.R.id.button)
        button.setOnClickListener{

        }
    }
}