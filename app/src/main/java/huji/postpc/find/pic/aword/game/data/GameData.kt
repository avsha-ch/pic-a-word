package huji.postpc.find.pic.aword.game.data

import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Category
import huji.postpc.find.pic.aword.game.models.Language
import huji.postpc.find.pic.aword.game.models.Level

class GameData(
    var initialLanguage: Language
) {
    // For each language we have a a separate set of categories
    // Each language is mapped to another map between category identifier and the category itself
    var data : HashMap <Language, HashMap<Int, Category>> = hashMapOf()
    var currLanguage : Language = initialLanguage

    init {
        addLanguage(initialLanguage)
    }

    fun getUserLanguages() : MutableList<Language> = data.keys.toMutableList()


    fun addLanguage(language: Language) : Boolean{
        if (data.containsKey(language)){
            // Don't override existing language
            return false
        }
        // Create a new entry for the language
        data[language] = createCategories()
        // Set current language to be the new one
        currLanguage = language
        return true
    }

    // Switch to the given language
    fun switchLanguage(language: Language){
        if (data.containsKey(language)){
            currLanguage = language
        }
    }

    private fun createCategories(): HashMap<Int,Category> = hashMapOf(
        R.string.category_house to Category(R.string.category_house),
        R.string.category_food to Category(R.string.category_food),
        R.string.category_vehicles to Category(R.string.category_vehicles),
        R.string.category_body to Category(R.string.category_body),
        R.string.category_animals to Category(R.string.category_animals),
        R.string.category_clothing to Category(R.string.category_clothing)
    )

    // Returns a list of categories matching the identifier for the current language
    fun getCategories() : List<Category> = data[currLanguage]!!.values.toList()

    // Returns the category matching the identifier for the current language
    fun getCategory(@StringRes categoryNameResId: Int) : Category = data[currLanguage]!![categoryNameResId]!!


    // Set the given level as completed for the current language
    fun setLevelCompleted(@StringRes categoryNameResId: Int, @StringRes levelNameResId : Int){
        data[currLanguage]!![categoryNameResId]!!.setLevelCompleted(levelNameResId)
    }


 // -------------- old to delete
    var categoriesMap: HashMap<Int, Category> = loadGameData()
//
//    fun getAllCategories(): List<Category> = categoriesMap.values.toList()
//
//    fun getCategory(@StringRes categoryNameResId: Int): Category? = categoriesMap[categoryNameResId]
//
//    fun getCategoryProgress(@StringRes categoryNameResId: Int): Int {
//        val category = getCategory(categoryNameResId) ?: return 0
//        return category.progress
//    }
//
//
    private fun loadGameData(): HashMap<Int, Category> = hashMapOf(
        R.string.category_house to Category(R.string.category_house),
        R.string.category_food to Category(R.string.category_food),
        R.string.category_vehicles to Category(R.string.category_vehicles),
        R.string.category_body to Category(R.string.category_body),
        R.string.category_animals to Category(R.string.category_animals),
        R.string.category_clothing to Category(R.string.category_clothing),
    )

}