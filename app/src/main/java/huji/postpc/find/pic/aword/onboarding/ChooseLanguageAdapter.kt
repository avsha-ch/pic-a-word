package huji.postpc.find.pic.aword.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Language

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

