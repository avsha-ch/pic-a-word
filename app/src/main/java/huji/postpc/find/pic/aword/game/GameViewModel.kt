package huji.postpc.find.pic.aword.game

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.game.data.GameData
import huji.postpc.find.pic.aword.game.models.Category
import huji.postpc.find.pic.aword.game.models.Language
import huji.postpc.find.pic.aword.game.models.Level
import huji.postpc.find.pic.aword.game.models.User


class GameViewModel(app: Application) : AndroidViewModel(app) {

    // Get user current language and load the matching game data
    val user: User = PicAWordApp.instance.user!!
    private val gameData: GameData = user.gameData
    var currLanguageResId : Int = gameData.currLanguage.nameResId
    var currCategoryResId: Int? = null
    var currLevelResId: Int? = null


    fun getUserLanguages() : MutableList<Language> = gameData.getUserLanguages()

    fun addNewLanguage(language : Language){
        gameData.addLanguage(language)
        // Update the new language to be the current one
        currLanguageResId = language.nameResId
    }

    fun switchLanguage(language: Language) {
        gameData.switchLanguage(language)
        currLanguageResId = language.nameResId
    }


    fun getCategories() : List<Category> = gameData.getCategories()

    fun getCurrCategory(): Category? {
        return currCategoryResId?.let { gameData.getCategory(it) }
    }

    fun getCurrCategoryProgress(): Int {
        return gameData.getCategory(currCategoryResId!!).progress
    }

    fun setLevelCompleted() {
        gameData.setLevelCompleted(currCategoryResId!!, currLevelResId!!)
        // Save to SP
        PicAWordApp.instance.saveToSP()
    }

    fun getCategoryCompletedLevels() : List<Level> = getCurrCategory()!!.getCompletedLevels()

    fun getCategoryNotCompletedLevels() : List<Level> = getCurrCategory()!!.getNotCompletedLevels()
}