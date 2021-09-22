package huji.postpc.find.pic.aword.game.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Category

class ChooseCategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder>() {


    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.item_button)
    }

    override fun getItemCount(): Int = categories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context as GameActivity
        // Select the bound item in position, which is a category
        val category = categories[position]
        val categoryNameResId = category.nameResId
        holder.button.text = context.getString(categoryNameResId)
        // Get the color matching this category
        val categoryColorResId = context.CATEGORY_COLOR_MAP[categoryNameResId]
        holder.button.backgroundTintList = categoryColorResId?.let { context.getColorStateList(it) }
        holder.button.setOnClickListener {
            val action = ChooseCategoryFragmentDirections.actionChooseCategoryFragmentToCategoryFragment(categoryNameResId = categoryNameResId)
            holder.view.findNavController().navigate(action)
        }
    }

}