package huji.postpc.find.pic.aword.models

import androidx.annotation.StringRes

import huji.postpc.find.pic.aword.data.loadDataSet

class Category(@StringRes val nameResId: Int){

    val levels : HashMap<Int, Level> = loadDataSet(nameResId)
    private var levelsCompletedNum : Int = 0
    private var isCompleted : Boolean = false
    var progress = 0
    var levelsNum = 0

    init {
        levelsNum = levels.size
    }

    fun markLevelCompleted(@StringRes levelResId: Int){
        // todo set level is completed
        levelsCompletedNum++
        progress = if (levelsNum != 0) (levelsCompletedNum / levelsNum ) else 0
        if (levels.containsKey(levelResId)){
            levels[levelResId]!!.isCompleted  = true
        }
    }

}
