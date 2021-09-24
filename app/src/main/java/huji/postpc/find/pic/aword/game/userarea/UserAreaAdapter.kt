package huji.postpc.find.pic.aword.game.userarea

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.game.GameActivity
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Category

class UserAreaAdapter(private val categories: List<Category>) : RecyclerView.Adapter<UserAreaAdapter.ProgressViewHolder>() {


    class ProgressViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val categoryNameTextView : TextView = view.findViewById(R.id.category_progress_name)
        val categoryProgressBar : ProgressBar = view.findViewById(R.id.category_progress_bar)
        val categoryProgressPercentage : TextView = view.findViewById(R.id.category_progress_percentage)
    }

    override fun getItemCount(): Int = categories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.progress_item_view, parent, false)
        return ProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder:  ProgressViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context as GameActivity
        // Select the bound item in position, which is a category
        val category = categories[position]
        val categoryNameResId = category.nameResId
        holder.categoryNameTextView.text = context.getString(categoryNameResId)
        // Get progress of current category
        holder.categoryProgressBar.progress = category.progress
        holder.categoryProgressPercentage.text = context.getString(R.string.progress_percentage_string, category.progress)
        // Get the color matching this category and set progress bar color
        val categoryColorResId = context.CATEGORY_COLOR_MAP[categoryNameResId]
        holder.categoryProgressBar.progressTintList = categoryColorResId?.let { context.getColorStateList(it) }

    }

}