package huji.postpc.find.pic.aword.game.data

import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Category
import huji.postpc.find.pic.aword.game.models.Level

class GameData {

    var categoriesMap: HashMap<Int, Category> = loadGameData()

    fun getAllCategories() : List<Category> = categoriesMap.values.toList()

    fun getCategory(@StringRes categoryNameResId : Int) : Category? = categoriesMap[categoryNameResId]

    fun getCategoryProgress(@StringRes categoryNameResId: Int) : Int {
        val category = getCategory(categoryNameResId) ?: return 0
        return category.progress
    }

    private fun loadGameData(): HashMap<Int, Category> = hashMapOf(
        R.string.category_house to Category(R.string.category_house, loadHouseDataset()),
        R.string.category_food to Category(R.string.category_food, loadFoodDataset()),
        R.string.category_vehicles to Category(R.string.category_vehicles, loadVehiclesDataset()),
        R.string.category_body to Category(R.string.category_body, loadHumanBodyDataset()),
        R.string.category_animals to Category(R.string.category_animals, loadAnimalsDataset()),
        R.string.category_clothing to Category(R.string.category_clothing, loadClothingDataset()),
    )


    private fun loadHouseDataset(): List<Level> =
        listOf(
            Level(R.string.level_sink, R.drawable.sink),
            Level(R.string.level_chair, R.drawable.chair),
            Level(R.string.level_clock, R.drawable.clock),
            Level(R.string.level_cutlery, R.drawable.cutlery),
            Level(R.string.level_pillow, R.drawable.pillow),
            Level(R.string.level_mobile_phone, R.drawable.shoes_stencil)
        )


    private fun loadFoodDataset(): List<Level> =
        listOf(
        )


    private fun loadVehiclesDataset(): List<Level> =
        listOf(
            Level(R.string.level_bicycle, R.drawable.bicycle),
            Level(R.string.level_wheel, R.drawable.wheel1),
            Level(R.string.level_van, R.drawable.van),
            Level(R.string.level_skateboard, R.drawable.skateboard),
        )


    private fun loadHumanBodyDataset(): List<Level> =
        listOf(
            Level(R.string.level_beard, R.drawable.beard),
            Level(R.string.level_smile, R.drawable.smile),
            Level(R.string.level_mouth, R.drawable.smile),
            Level(R.string.level_ear, R.drawable.ear),
            Level(R.string.level_hair, R.drawable.hair)
        )

    private fun loadAnimalsDataset(): List<Level> =
        listOf(
            Level(R.string.level_bird, R.drawable.bird),
            Level(R.string.level_bear, R.drawable.bear),
            Level(R.string.level_cat, R.drawable.cat),
            Level(R.string.level_dog, R.drawable.dog)
        )


    private fun loadClothingDataset(): List<Level> =
        listOf(
            Level(R.string.level_sunglasses, R.drawable.sunglasses),
            Level(R.string.level_necklace, R.drawable.necklace),
            Level(R.string.level_dress, R.drawable.dress),
            Level(R.string.level_hat, R.drawable.hat),
            Level(R.string.level_jersey, R.drawable.jersey),
            Level(R.string.level_jewellery, R.drawable.jewellery),
            Level(R.string.level_ring, R.drawable.ring),
            Level(R.string.level_jeans, R.drawable.jeans),
            Level(R.string.level_umbrella, R.drawable.umbrella),
            Level(R.string.level_glasses, R.drawable.glasses),
            Level(R.string.level_bag, R.drawable.bag),
        )


    private fun loadMiscellaneousDataset(): List<Level> =
        listOf(
        )

}