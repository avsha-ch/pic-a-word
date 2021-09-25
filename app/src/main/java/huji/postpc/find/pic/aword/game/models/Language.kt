package huji.postpc.find.pic.aword.game.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.PicAWordApp

data class Language(
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int
) {

    // Override toString to display the string and not the resource value
    override fun toString(): String = PicAWordApp.instance.getString(nameResId)
}