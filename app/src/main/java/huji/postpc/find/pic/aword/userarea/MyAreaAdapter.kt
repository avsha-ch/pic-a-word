package huji.postpc.find.pic.aword.userarea

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.MainActivity
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.models.Category

class MyAreaAdapter(private val gameData: HashMap<Int, Category>) : RecyclerView.Adapter<MyAreaAdapter.ProgressViewHolder>() {


    private val categories = gameData.values.toList()

    class ProgressViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val categoryNameTextView : TextView = view.findViewById(R.id.category_progress_name)
        val categoryProgressBar : ProgressBar = view.findViewById(R.id.category_progress_bar)
        val categoryProgressPercentage : TextView = view.findViewById(R.id.category_progress_percentage)
    }

    override fun getItemCount(): Int = categories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.progres_item_view, parent, false)
        return ProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder:  ProgressViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context as MainActivity
        // Select the bound item in position, which is a category
        val category = categories[position]
        val categoryNameResId = category.nameResId
        holder.categoryNameTextView.text = context.getString(categoryNameResId)
        // Get progress of current category
        holder.categoryProgressBar.progress = category.progress + 20
        holder.categoryProgressPercentage.text = "${category.progress + 20}%" // todo I know the fix, later urghh
        // Get the color matching this category
        val categoryColorResId = context.CATEGORY_COLOR_MAP[categoryNameResId]
        holder.categoryProgressBar.progressTintList = categoryColorResId?.let { context.getColorStateList(it) }

    }

}