package huji.postpc.find.pic.aword.game.models

import huji.postpc.find.pic.aword.game.data.GameData
import java.util.*

data class User(
    var username: String = "",
    var currUserLanguage: Language
) {
    var userGameData: HashMap<Language, GameData> = hashMapOf()
    val userId : String = UUID.randomUUID().toString()
    init {
        userGameData[currUserLanguage] = GameData()
    }


    fun getCurrGameData(): GameData {
        return userGameData[currUserLanguage]!!
    }
}