package huji.postpc.find.pic.aword.game.userarea

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import huji.postpc.find.pic.aword.game.GameViewModel
import huji.postpc.find.pic.aword.R
import androidx.navigation.fragment.findNavController

class UserAreaFragment : Fragment(R.layout.fragment_user_area) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myCollectionButton : MaterialButton
    private lateinit var shareAppButton: Button
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = gameViewModel.gameData?.let { UserAreaAdapter(it.getAllCategories()) }

        myCollectionButton = view.findViewById(R.id.my_area_collection_button)
        myCollectionButton.setOnClickListener {
            val action = UserAreaFragmentDirections.actionUserAreaFragmentToCollectionChooseCategoryFragment()
            findNavController().navigate(action)
        }

        shareAppButton = view.findViewById(R.id.share_app_button)
        shareAppButton.setOnClickListener { shareAppLink() }
    }

    private fun shareAppLink() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Invite to Pic a Word")
            putExtra(
                Intent.EXTRA_TEXT,
                "Come learn languages interactively with Pic A Word\n https://play.google.com/store/apps/details?id=com.tutorial.personal.androidstudiopro "
            )   // TODO: put link to app store
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}