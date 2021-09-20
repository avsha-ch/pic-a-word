package huji.postpc.find.pic.aword.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Language

//
//class ChooseLanguageAdapter(private val languages : List<Language>) : RecyclerView.Adapter<ChooseLanguageAdapter.LanguageViewHolder>()  {
//
//    class LanguageViewHolder(val view: View): RecyclerView.ViewHolder(view){
//        val languageIconImgView : ImageView = view.findViewById(R.id.language_icon_img_view)
//        val languageTextView : TextView = view.findViewById(R.id.language_name_text_view)
//    }
//
//    override fun getItemCount(): Int = languages.size
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.language_item_view, parent, false)
//        return LanguageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
//        // Get context in order to get resources (R)
//        val context = holder.itemView.context
//        // Select the bound item in position, a Language object
//        val language = languages[position]
//        // Get language name and icon
//        val languageNameResId = language.nameResId
//        val languageIconResId = language.iconResId
//        // Set name and icon to view holder
//        holder.languageTextView.text = context.getString(languageNameResId)
//        holder.languageIconImgView.setImageResource(languageIconResId)
//    }
//
//}


class LanguageAdapter(private val adapterContext: Context, private val languages: List<Language>) : ArrayAdapter<Language>(adapterContext, R.layout.language_item_view, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var languageItemView = convertView

        if (languageItemView == null)
            languageItemView = LayoutInflater.from(adapterContext).inflate(R.layout.language_item_view, parent, false)

        val language = languages[position]
        if (languageItemView != null){
            val languageIconImgView : ImageView = languageItemView.findViewById(R.id.language_icon_img_view)
            val languageTextView : TextView = languageItemView.findViewById(R.id.language_name_text_view)
            languageTextView.text = context.getString(language.nameResId)
            languageIconImgView.setImageResource(language.iconResId)
        }

        return languageItemView!!
    }
}

