package huji.postpc.find.pic.aword.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Todo string resources, maybe create a class for category, save number of completed levels within it and number of total levels, and then
    // color it differently if all stages are done, and create Level stage with the same reasoning
    private val categories = mutableListOf("Around the house","Fruits", "Vehicles", "Human body", "Animals", "Clothing", "Miscellaneous")

    class CategoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.item_button)
    }

    override fun getItemCount(): Int = categories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        holder.button.text = item.toString()
        holder.button.setOnClickListener {
            val action = CategoryFragmentDirections.actionChooseCategoryFragmentToLevelsFragment(category = holder.button.text.toString())
            holder.view.findNavController().navigate(action)
        }
    }


}