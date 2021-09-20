package huji.postpc.find.pic.aword.game.data

import androidx.annotation.StringRes
import huji.postpc.find.pic.aword.R
import huji.postpc.find.pic.aword.game.models.Level


fun loadDataSet(@StringRes categoryName : Int) : List<Level>{

    return when (categoryName){
        R.string.category_house -> loadHouseDataset()
        R.string.category_food -> loadFoodDataset()
        R.string.category_vehicles -> loadVehiclesDataset()
        R.string.category_body -> loadHumanBodyDataset()
        R.string.category_animals -> loadAnimalsDataset()
        R.string.category_clothing -> loadClothingDataset()
        R.string.category_misc -> loadMiscellaneousDataset()
        else -> listOf()
    }
}

//fun loadDataSet(@StringRes categoryName : Int) : HashMap<Int, Level>{
//
//    return when (categoryName){
////        R.string.category_house -> loadHouseDataset()
////        R.string.category_food -> loadFoodDataset()
////        R.string.category_vehicles -> loadVehiclesDataset()
////        R.string.category_body -> loadHumanBodyDataset()
//        R.string.category_animals -> loadAnimalsDataset()
////        R.string.category_clothing -> loadClothingDataset()
////        R.string.category_misc -> loadMiscellaneousDataset()
//        else -> hashMapOf()
//    }
//}

private fun loadHouseDataset() : List<Level> =
    listOf(
        Level(R.string.level_sink, R.drawable.shoes_stencil),
        Level(R.string.level_bathroom, R.drawable.shoes_stencil),
        Level(R.string.level_chair, R.drawable.shoes_stencil),
        Level(R.string.level_desk, R.drawable.shoes_stencil),
        Level(R.string.level_cutlery, R.drawable.shoes_stencil),
        Level(R.string.level_pillow, R.drawable.shoes_stencil),
        Level(R.string.level_plant, R.drawable.shoes_stencil),
        Level(R.string.level_cup, R.drawable.shoes_stencil),
        Level(R.string.level_wall, R.drawable.shoes_stencil),
        Level(R.string.level_shelf, R.drawable.shoes_stencil),
        Level(R.string.level_computer, R.drawable.shoes_stencil),
        Level(R.string.level_newspaper, R.drawable.shoes_stencil),
        Level(R.string.level_mobile_phone, R.drawable.shoes_stencil),
        Level(R.string.level_lego, R.drawable.shoes_stencil),
        Level(R.string.level_money, R.drawable.shoes_stencil),
    )


private fun loadFoodDataset() : List<Level> =
    listOf(
        Level(R.string.level_hotdog, R.drawable.shoes_stencil),
        Level(R.string.level_couscous, R.drawable.shoes_stencil),
        Level(R.string.level_cookie, R.drawable.shoes_stencil),
        Level(R.string.level_cola, R.drawable.shoes_stencil),
        Level(R.string.level_coffee, R.drawable.shoes_stencil),
        Level(R.string.level_fruit, R.drawable.shoes_stencil),
        Level(R.string.level_pizza, R.drawable.shoes_stencil),
        Level(R.string.level_wine, R.drawable.shoes_stencil),
        Level(R.string.level_bread, R.drawable.shoes_stencil),
        Level(R.string.level_vegetable, R.drawable.shoes_stencil),
        Level(R.string.level_cake, R.drawable.shoes_stencil),
        Level(R.string.level_pie, R.drawable.shoes_stencil),
    )


private fun loadVehiclesDataset() : List<Level> =
    listOf(
        Level(R.string.level_tractor, R.drawable.shoes_stencil),
        Level(R.string.level_bus, R.drawable.shoes_stencil),
        Level(R.string.level_bicycle, R.drawable.shoes_stencil),
        Level(R.string.level_boat, R.drawable.shoes_stencil),
        Level(R.string.level_vehicle, R.drawable.shoes_stencil),
        Level(R.string.level_wheel, R.drawable.shoes_stencil),
        Level(R.string.level_van, R.drawable.shoes_stencil),
        Level(R.string.level_sailboat, R.drawable.shoes_stencil),
        Level(R.string.level_car, R.drawable.shoes_stencil),
        Level(R.string.level_skateboard, R.drawable.shoes_stencil),
    )


private fun loadHumanBodyDataset() : List<Level> =
    listOf(
        Level(R.string.level_beard, R.drawable.shoes_stencil),
        Level(R.string.level_smile, R.drawable.shoes_stencil),
        Level(R.string.level_mouth, R.drawable.shoes_stencil),
        Level(R.string.level_foot, R.drawable.shoes_stencil),
        Level(R.string.level_ear, R.drawable.shoes_stencil),
        Level(R.string.level_eyelash, R.drawable.shoes_stencil),
        Level(R.string.level_hair, R.drawable.shoes_stencil),
        Level(R.string.level_hand, R.drawable.shoes_stencil),
    )

//private fun loadAnimalsDataset() : HashMap<Int,Level> =
//    hashMapOf(
//        R.string.level_bird to Level(R.string.level_bird, R.drawable.shoes_stencil),
////        Level(R.string.level_bear, R.drawable.shoes_stencil),
//        R.string.level_cat to Level(R.string.level_cat, R.drawable.shoes_stencil),
////        Level(R.string.level_horse, R.drawable.shoes_stencil),
////        Level(R.string.level_pet, R.drawable.shoes_stencil),
////        Level(R.string.level_duck, R.drawable.shoes_stencil),
////        Level(R.string.level_turtle, R.drawable.shoes_stencil),
//        R.string.level_dog to Level(R.string.level_dog, R.drawable.shoes_stencil),
//        R.string.level_butterfly to Level(R.string.level_butterfly, R.drawable.shoes_stencil)
//    )
//
private fun loadAnimalsDataset() : List<Level> =
    listOf(
        Level(R.string.level_bird, R.drawable.shoes_stencil),
        Level(R.string.level_bear, R.drawable.shoes_stencil),
        Level(R.string.level_cat, R.drawable.shoes_stencil),
        Level(R.string.level_horse, R.drawable.shoes_stencil),
        Level(R.string.level_pet, R.drawable.shoes_stencil),
        Level(R.string.level_duck, R.drawable.shoes_stencil),
        Level(R.string.level_turtle, R.drawable.shoes_stencil),
        Level(R.string.level_dog, R.drawable.shoes_stencil),
        Level(R.string.level_butterfly, R.drawable.shoes_stencil),
    )


private fun loadClothingDataset() : List<Level> =
    listOf(
        Level(R.string.level_sunglasses, R.drawable.shoes_stencil),
        Level(R.string.level_necklace, R.drawable.shoes_stencil),
        Level(R.string.level_dress, R.drawable.shoes_stencil),
        Level(R.string.level_tie, R.drawable.shoes_stencil),
        Level(R.string.level_hat, R.drawable.shoes_stencil),
        Level(R.string.level_jacket, R.drawable.shoes_stencil),
        Level(R.string.level_beanie, R.drawable.shoes_stencil),
        Level(R.string.level_jersey, R.drawable.shoes_stencil),
        Level(R.string.level_scarf, R.drawable.shoes_stencil),
        Level(R.string.level_jewellery, R.drawable.shoes_stencil),
        Level(R.string.level_shoe, R.drawable.shoes_stencil),
        Level(R.string.level_sneakers, R.drawable.shoes_stencil),
        Level(R.string.level_ring, R.drawable.shoes_stencil),
        Level(R.string.level_jeans, R.drawable.shoes_stencil),
        Level(R.string.level_umbrella, R.drawable.shoes_stencil),
        Level(R.string.level_glasses, R.drawable.shoes_stencil),
        Level(R.string.level_bag, R.drawable.shoes_stencil),
        Level(R.string.level_helmet, R.drawable.shoes_stencil),
    )


private fun loadMiscellaneousDataset() : List<Level> =
    listOf(
        Level(R.string.level_rock, R.drawable.shoes_stencil),
        Level(R.string.level_toy, R.drawable.shoes_stencil),
        Level(R.string.level_balloon, R.drawable.shoes_stencil),
        Level(R.string.level_branch, R.drawable.shoes_stencil),
        Level(R.string.level_surfboard, R.drawable.shoes_stencil),
        Level(R.string.level_hanukkah, R.drawable.shoes_stencil),
        Level(R.string.level_clown, R.drawable.shoes_stencil),
        Level(R.string.level_whiteboard, R.drawable.shoes_stencil),
        Level(R.string.level_flag, R.drawable.shoes_stencil),
    )
