package huji.postpc.find.pic.aword.game

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
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

    // Current language live data todo...
    // Config for the current language
    @SuppressLint("StaticFieldLeak")
    var configContext : Context = updateConfigContext()

    fun getUserLanguages(): MutableList<Language> {
        return user?.userGameData?.keys?.toMutableList() ?: mutableListOf()
    }

    private fun updateConfigContext() : Context {
        val configuration = Configuration(PicAWordApp.instance.resources.configuration)
        configuration.setLocale(PicAWordApp.LANGUAGE_LOCAL_MAP[user!!.currUserLanguage])
        return PicAWordApp.instance.createConfigurationContext(configuration)
    }

    fun addNewLanguage(language : Language){
        if (user != null) {
            user.userGameData[language] = GameData()
        }
    }

    fun switchLanguage(language: Language) {
        if (user == null) {
            return
        }
        if (!user.userGameData.keys.contains(language)) {
            return
        }
        user.currUserLanguage = language
        gameData = user.getCurrGameData()
//        user.currUserLanguage = language
//        gameData = user.getCurrGameData()
        // todo move currUserLanguage to gameviewmmodel livedata?..
        configContext = updateConfigContext()
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