package huji.postpc.find.pic.aword.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Category

class GameViewModel(application: Application): AndroidViewModel(application) {


    var gameData = hashMapOf(
        R.string.category_house to Category(R.string.category_house),
        R.string.category_food to Category(R.string.category_food),
        R.string.category_vehicles to Category(R.string.category_vehicles),
        R.string.category_body to Category(R.string.category_body),
        R.string.category_animals to Category(R.string.category_animals),
        R.string.category_clothing to Category(R.string.category_clothing),
//        R.string.category_misc to Category(R.string.category_misc)
    )
//    private var sp = application.getSharedPreferences(SP_GAME_NAME, Context.MODE_PRIVATE)
//    private val gson = Gson()

    var currCategoryResId : Int? = null
    var currLevelResId : Int? = null

//    init {
//        loadFromSP()
//    }

//    fun saveToSP(){
//        val gameDataJson = gson.toJson(gameData)
//        sp.edit().putString(SP_GAME_DATA_KEY, gameDataJson).apply()
//    }
//
//    private fun loadFromSP(){
//        val gameDataJson = sp.getString(SP_GAME_DATA_KEY, "")
//        if (gameDataJson != ""){
//            // Create a new database
//            gameData = gson.fromJson(gameDataJson, gameData.javaClass)
//        }
//    }

    fun getCurrCategoryProgress(): Int{
        if (currCategoryResId == null){
            return 0
        }
        val category = gameData[currCategoryResId]
        return category?.progress ?: 0
    }

//    fun setCurrLevelCompleted(){
//        currLevelResId?.let { gameData[currCategoryResId]?.markLevelCompleted(it) }
//    }

    companion object {
        private const val SP_GAME_NAME = "sp_game"
        private const val SP_GAME_DATA_KEY = "sp_game_data_key"
    }
}