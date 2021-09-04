package huji.postpc.find.pic.aword.levels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.category.CategoryAdapter


class LevelsFragment : Fragment(R.layout.fragment_levels) {

    private lateinit var category: String
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            category = it.getString("category").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.levels_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(activity,4)
        recyclerView.adapter = LevelsAdapter(category)
    }
}