package huji.postpc.find.pic.aword.game.models

import huji.postpc.find.pic.aword.game.data.GameData
import huji.postpc.find.pic.aword.game.models.Language
import java.util.*
import kotlin.collections.HashMap

data class User(
    var username: String = "",
    var currUserLanguage: Language
) {
    var userGameData: HashMap<Int, GameData> = hashMapOf()
    val userId : String = UUID.randomUUID().toString()
    init {
        userGameData[currUserLanguage.nameResId] = GameData()
    }

    fun getCurrGameData(): GameData {
        return userGameData[currUserLanguage.nameResId]!!
    }
}