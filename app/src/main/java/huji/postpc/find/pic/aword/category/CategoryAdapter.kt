package huji.postpc.find.pic.aword.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.models.Category

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Todo string resources, maybe create a class for category, save number of completed levels within it and number of total levels, and then
    // color it differently if all stages are done, and create Level stage with the same reasoning
    private val categories = mutableListOf(
            Category(R.string.category_house),
            Category(R.string.category_food),
            Category(R.string.category_vehicles),
            Category(R.string.category_body),
            Category(R.string.category_animals),
            Category(R.string.category_clothing),
            Category(R.string.category_misc)
        )

    class CategoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.item_button)
    }

    override fun getItemCount(): Int = categories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context
        // Select the bound item in position, which is a category
        val item = categories[position]
        holder.button.text = context.getString(item.nameResourceId)
        holder.button.setOnClickListener {
            val action = CategoryFragmentDirections.actionChooseCategoryFragmentToLevelsFragment(category = item.nameResourceId)
            holder.view.findNavController().navigate(action)
        }
    }


}