package huji.postpc.find.pic.aword.game

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.category.ChooseCategoryAdapter
import huji.postpc.find.pic.aword.game.category.ChooseCategoryFragmentDirections
import huji.postpc.find.pic.aword.game.models.Language


class AddLanguageAdapter(private val languages: List<Language>) : RecyclerView.Adapter<AddLanguageAdapter.LanguageViewHolder>() {


    class LanguageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgView : ImageView = view.findViewById(R.id.language_icon_img_view)
        val textView : TextView = view.findViewById(R.id.language_name_text_view)
    }

    override fun getItemCount(): Int = languages.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
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
//        holder.button.setOnClickListener {
//            val action = ChooseCategoryFragmentDirections.actionChooseCategoryFragmentToCategoryFragment(categoryNameResId = categoryNameResId)
//            holder.view.findNavController().navigate(action)
//        }
    }

}