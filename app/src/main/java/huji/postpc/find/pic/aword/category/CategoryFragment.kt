package huji.postpc.find.pic.aword.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import huji.postpc.find.pic.aword.R

class CategoryFragment : Fragment(R.layout.fragment_category) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button : Button = view.findViewById(R.id.button)
        button.setOnClickListener{

        }
    }
}