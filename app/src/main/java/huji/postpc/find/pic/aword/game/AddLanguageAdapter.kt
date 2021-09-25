package huji.postpc.find.pic.aword.game

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Language


class AddLanguageAdapter(private val languages: List<Language>) : RecyclerView.Adapter<AddLanguageAdapter.LanguageViewHolder>() {

    var onItemClick: ((Language) -> Unit)? = null

    inner class LanguageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById(R.id.language_icon_img_view)
        val textView: TextView = view.findViewById(R.id.language_name_text_view)

        init {
            view.setOnClickListener { onItemClick?.invoke(languages[adapterPosition]) }
        }
    }


    override fun getItemCount(): Int = languages.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLanguageAdapter.LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_language_item_view, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        // Get context in order to use getString from resources (R)
        val context = holder.itemView.context as GameActivity
        // Select the bound item in position, which is a Language and update details
        val language = languages[position]
        holder.textView.text = context.getString(language.nameResId)
        holder.imgView.setImageResource(language.iconResId)
//        if (language.nameResId == R.string.language_sp) {
//            // Grey out language not ready yet
//            holder.textView.setTextColor(Color.parseColor("#a9a9a9"))
//        }
    }

}