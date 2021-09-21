package huji.postpc.find.pic.aword.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import huji.postpc.find.pic.aword.PicAWordApp
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.data.GameData
import huji.postpc.find.pic.aword.game.models.Category
import huji.postpc.find.pic.aword.game.models.User

class GameViewModel(application: Application): AndroidViewModel(application) {


    // Get user current language and load the matching game data
    val user : User? = PicAWordApp.instance.user
    var gameData : GameData = user!!.getCurrGameData()
    var currCategoryResId : Int? = null
    var currLevelResId : Int? = null

    fun getCurrCategory(): Category? {
        return currCategoryResId?.let { gameData.getCategory(it) }
    }

    fun getCurrCategoryProgress() : Int{
        if (currCategoryResId == null){
            return 0
        }
        return gameData.getCategoryProgress(currCategoryResId!!)
    }
    fun setLevelCompleted(){
        // todo use curr category and curr level res id to update gamedata
    }


    companion object {
        private const val SP_GAME_NAME = "sp_game"
        private const val SP_GAME_DATA_KEY = "sp_game_data_key"
    }
}