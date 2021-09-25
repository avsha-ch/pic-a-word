package huji.postpc.find.pic.aword.game.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Level(
    @StringRes val nameResId: Int,
    @DrawableRes val imgResId: Int,
    var isCompleted: Boolean = false,
    var completedImgLocalPath: String = ""
)
