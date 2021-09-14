package huji.postpc.find.pic.aword.levels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.IdRes
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.data.loadDataSet
import huji.postpc.find.pic.aword.models.Level


class LevelsAdapter(@IdRes private val category: Int) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {



    // Todo
    private var levels = listOf<Level>()

    init {
        levels = loadDataSet(category)

    }

    class LevelViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.item_button)
    }

    override fun getItemCount(): Int = levels.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsAdapter.LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context
        // Select the bound item in position, which is a level
        val item = levels[position]
        holder.button.text = context.getString(item.nameResId)
        holder.button.setOnClickListener {

            val action = LevelsFragmentDirections.actionLevelsFragmentToGameFragment(word = holder.button.text.toString())
            holder.view.findNavController().navigate(action)
        }

    }

}