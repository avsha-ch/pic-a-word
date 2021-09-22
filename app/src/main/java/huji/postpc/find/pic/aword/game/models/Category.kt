package huji.postpc.find.pic.aword.game.models

import androidx.annotation.StringRes

import huji.postpc.find.pic.aword.game.data.loadDataSet

class Category(@StringRes val nameResId: Int, val levels : List<Level>){

//    val levels : HashMap<Int, Level> = loadDataSet(nameResId)
//    val levels : List<Level> = loadDataSet(nameResId)
    private var levelsCompletedNum : Int = 0
    private var isCompleted : Boolean = false
    var progress = 0
    var levelsNum = levels.size

//    fun markLevelCompleted(@StringRes levelResId: Int){
//        // todo
//        levelsCompletedNum++
//        progress = if (levelsNum != 0) (levelsCompletedNum / levelsNum ) else 0
//        if (levels.containsKey(levelResId)){
//            levels[levelResId]!!.isCompleted  = true
//        }
//    }

}
