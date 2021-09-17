package huji.postpc.find.pic.aword.userarea

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.MainViewModel
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.category.ChooseCategoryAdapter

class MyAreaFragment : Fragment(R.layout.fragment_my_area) {

    private lateinit var recyclerView: RecyclerView
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = MyAreaAdapter(mainViewModel.gameData)
    }
}