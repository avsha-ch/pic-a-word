package huji.postpc.find.pic.aword.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.game.data.GameData
import huji.postpc.find.pic.aword.game.models.Category
import huji.postpc.find.pic.aword.game.models.Language
import huji.postpc.find.pic.aword.game.models.User

class GameViewModel(application: Application) : AndroidViewModel(application) {


    // Get user current language and load the matching game data
    val user: User? = PicAWordApp.instance.user
    var gameData: GameData = user!!.getCurrGameData()
    var currCategoryResId: Int? = null
    var currLevelResId: Int? = null

    fun getUserLanguages(): MutableList<Language> {
        if (user != null) {
            return user.userGameData.keys.toMutableList()
        }
        return mutableListOf()
    }

    fun switchLanguage(newLanguage: Language) {
        if (user == null) {
            return
        }
        if (!user.userGameData.keys.contains(newLanguage)) {
            return
        }
        user.currUserLanguage = newLanguage
        gameData = user.getCurrGameData()
    }


    fun getCurrCategory(): Category? {
        return currCategoryResId?.let { gameData.getCategory(it) }
    }

    fun getCurrCategoryProgress(): Int {
        if (currCategoryResId == null) {
            return 0
        }
        return gameData.getCategoryProgress(currCategoryResId!!)
    }

    fun setLevelCompleted() {
        // todo use curr category and curr level res id to update gamedata
    }


    companion object {
        private const val SP_GAME_NAME = "sp_game"
        private const val SP_GAME_DATA_KEY = "sp_game_data_key"
    }
}