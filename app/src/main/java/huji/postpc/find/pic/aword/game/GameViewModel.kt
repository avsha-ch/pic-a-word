package huji.postpc.find.pic.aword.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

    val currLanguageResIdLiveData : MutableLiveData<Int> = MutableLiveData(gameData.currLanguage.nameResId)
    val currCategoryResIdLiveData : MutableLiveData<Int?> = MutableLiveData(null)
    val currLevelResIdLiveData : MutableLiveData<Int?> = MutableLiveData(null)



    fun getUserLanguages() : MutableList<Language> = gameData.getUserLanguages()

    fun addNewLanguage(language : Language){
        gameData.addLanguage(language)
        // Update the new language to be the current one
        currLanguageResIdLiveData.value = language.nameResId
    }

    fun switchLanguage(language: Language) {
        gameData.switchLanguage(language)
        currLanguageResIdLiveData.value = language.nameResId

    }

    fun getCategories() : List<Category> = gameData.getCategories()

    fun getCurrCategory(): Category? {
        return currCategoryResIdLiveData.value?.let { gameData.getCategory(it) }
    }

    fun getCurrCategoryProgress(): Int {
        return gameData.getCategory(currCategoryResIdLiveData.value!!).progress
    }

    fun isCurrCategoryFinished(): Boolean {
        return (getCurrCategoryProgress() == 100) || (getCurrCategory()?.levels?.isEmpty() == true)
    }

    fun setLevelCompleted() {
        gameData.setLevelCompleted(currCategoryResIdLiveData.value!!, currLevelResIdLiveData.value!!)
        // Save to SP
        PicAWordApp.instance.saveToSP()
    }


    fun getCategoryCompletedLevels() : List<Level> = getCurrCategory()!!.getCompletedLevels()

    fun getCategoryNotCompletedLevels() : List<Level> = getCurrCategory()!!.getNotCompletedLevels()
}