package huji.postpc.find.pic.aword.models

import androidx.annotation.StringRes

import huji.postpc.find.pic.aword.data.loadDataSet

class Category(@StringRes val nameResourceId: Int){

//    val levels : List<Level> = loadDataSet(nameResourceId)
    val levels : HashMap<Int, Level> = loadDataSet(nameResourceId)
    private var levelsCompletedNum : Int = 0
    private var isCompleted : Boolean = false

    fun markLevelCompleted(@StringRes levelResId: Int){
        // todo set level is completed
        levelsCompletedNum++
        if (levels.containsKey(levelResId)){
            levels[levelResId]!!.isCompleted  = true
        }
        // TODO maybe do same logic here as we did in the todo items exercise, nice api and will be easy to write tests for it as a bonus to our project
    }

}
