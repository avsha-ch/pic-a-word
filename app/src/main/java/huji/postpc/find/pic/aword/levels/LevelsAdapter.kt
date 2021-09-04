package huji.postpc.find.pic.aword.levels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R

class LevelsAdapter(private val category: String) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    // Todo
    private var levels = mutableListOf<String>()

    init {
        levels = when (category) {
            "Food" -> {
                mutableListOf("Coffee", "Cookie")
            }
            "Vehicles" -> {
                mutableListOf("Wheel", "Bus", "Bicycle")
            }
            "Animals" -> {
                mutableListOf("Dog", "Cat", "Bird")
            }
            else -> {
                mutableListOf<String>()
            }
        }
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
        val item = levels[position]
        holder.button.text = item.toString()
        holder.button.setOnClickListener {
            val action = LevelsFragmentDirections.actionLevelsFragmentToGameFragment(word = holder.button.text.toString())
            holder.view.findNavController().navigate(action)

        }

    }

}