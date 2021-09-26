package huji.postpc.find.pic.aword.game.models

import huji.postpc.find.pic.aword.game.data.GameData
import java.util.*

data class User(
    var username: String = "",
    var initialUserLanguage: Language
) {
    val userId: String = UUID.randomUUID().toString()
    val gameData: GameData = GameData(initialUserLanguage)

}