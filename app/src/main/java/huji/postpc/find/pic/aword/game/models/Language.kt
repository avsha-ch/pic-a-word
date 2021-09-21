package huji.postpc.find.pic.aword.game.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.PicAWordApp

data class Language(
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int
) {

    override fun toString(): String = PicAWordApp.instance.getString(nameResId)
}