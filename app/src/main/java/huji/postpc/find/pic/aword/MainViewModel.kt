package huji.postpc.find.pic.aword

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import huji.postpc.find.pic.aword.models.Category

class MainViewModel(application: Application): AndroidViewModel(application) {

    var categories = mutableListOf(
        Category(R.string.category_house),
        Category(R.string.category_food),
        Category(R.string.category_vehicles),
        Category(R.string.category_body),
        Category(R.string.category_animals),
        Category(R.string.category_clothing),
        Category(R.string.category_misc)
        )
    var gameData = hashMapOf(
        R.string.category_house to Category(R.string.category_house),
        R.string.category_food to Category(R.string.category_food),
        R.string.category_vehicles to Category(R.string.category_vehicles),
        R.string.category_body to Category(R.string.category_body),
        R.string.category_animals to Category(R.string.category_animals),
        R.string.category_clothing to Category(R.string.category_clothing),
        R.string.category_misc to Category(R.string.category_misc)
    )
    private var sp = application.getSharedPreferences(SP_GAME_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    var currCategoryResId : Int? = null
    var currLevelResId : Int? = null

    init {
        loadFromSP()
    }

    fun saveToSP(){
        val gameDataJson = gson.toJson(categories)
        sp.edit().putString(SP_GAME_DATA_KEY, gameDataJson).apply()
    }

    private fun loadFromSP(){
        val gameDataJson = sp.getString(SP_GAME_DATA_KEY, "")
        if (gameDataJson != ""){
            // Create a new database
            categories = gson.fromJson(gameDataJson, categories.javaClass)
        }
    }

    fun setCurrLevelCompleted(){
        currLevelResId?.let { gameData[currCategoryResId]?.markLevelCompleted(it) }
    }

    companion object {
        private const val SP_GAME_NAME = "sp_game"
        private const val SP_GAME_DATA_KEY = "sp_game_data_key"
    }
}