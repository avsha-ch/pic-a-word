package huji.postpc.find.pic.aword.game.models

import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.R


data class Category(
    @StringRes val nameResId: Int
) {
    // Create the list of levels matching the category name resource id
    val levels: List<Level> = createCategoryLevels()
    private var isCompleted: Boolean = false
    private var levelsCompletedNum: Int = 0
    private var levelsNum: Int = levels.size
    var progress: Int = 0

    init {
        updateProgress()
        updateIsCompleted()
    }


    fun setLevelCompleted(@StringRes levelResId: Int) {
        for (level in levels) {
            if (level.nameResId == levelResId) {
                // Mark level as completed
                level.isCompleted = true
                levelsCompletedNum++
                updateProgress()
                updateIsCompleted()
                return
            }
        }
    }

    fun getCompletedLevels() : List<Level> = levels.filter { level -> level.isCompleted }

    fun getNotCompletedLevels() : List<Level> = levels.filter { level -> !level.isCompleted }

    private fun updateProgress() {
        // Empty categories are considered finished
        progress = if (levelsNum == 0) 100 else ((levelsCompletedNum.toFloat() / levelsNum.toFloat()) * 100 ).toInt()
    }

    private fun updateIsCompleted() {
        isCompleted = (levelsCompletedNum == levelsNum)
    }




    // ----------------------------------------------------------------------
    // Private methods for loading the correct list of levels to the category

    private fun createCategoryLevels(): List<Level> =
        when (nameResId) {
            R.string.category_house -> createHouseLevels()
            R.string.category_food -> createFoodLevels()
            R.string.category_vehicles -> createVehiclesLevels()
            R.string.category_body -> createBodyLevels()
            R.string.category_animals -> createAnimalsLevels()
            R.string.category_clothing -> createClothingLevels()
            else -> listOf()

        }

    private fun createHouseLevels(): List<Level> =
        listOf(
            Level(R.string.level_sink, R.drawable.sink),
            Level(R.string.level_chair, R.drawable.chair),
            Level(R.string.level_clock, R.drawable.clock),
            Level(R.string.level_cutlery, R.drawable.cutlery),
            Level(R.string.level_pillow, R.drawable.pillow),
            Level(R.string.level_mobile_phone, R.drawable.mobile_phone)
        )


    private fun createFoodLevels(): List<Level> =
        listOf(
        )


    private fun createVehiclesLevels(): List<Level> =
        listOf(
            Level(R.string.level_bicycle, R.drawable.bicycle),
            Level(R.string.level_wheel, R.drawable.wheel1),
            Level(R.string.level_van, R.drawable.van),
            Level(R.string.level_skateboard, R.drawable.skateboard),
        )


    private fun createBodyLevels(): List<Level> =
        listOf(
            Level(R.string.level_beard, R.drawable.beard),
            Level(R.string.level_smile, R.drawable.smile),
            Level(R.string.level_mouth, R.drawable.smile),
            Level(R.string.level_ear, R.drawable.ear),
            Level(R.string.level_hair, R.drawable.hair)
        )

    private fun createAnimalsLevels(): List<Level> =
        listOf(
//            Level(R.string.level_bird, R.drawable.bird),
//            Level(R.string.level_bear, R.drawable.bear),
            Level(R.string.level_cat, R.drawable.cat),
            Level(R.string.level_dog, R.drawable.dog)
        )


    private fun createClothingLevels(): List<Level> =
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

    // ----------------------------------------------------------------------


}
