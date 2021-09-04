package huji.postpc.find.pic.aword.models

import androidx.annotation.StringRes

import huji.postpc.find.pic.aword.data.loadDataSet

class Category(@StringRes val nameResourceId: Int){

    private val levels = loadDataSet(nameResourceId)
    private var levelsCompletedNum : Int = 0
    private var isCompleted : Boolean = false

    fun markLevelCompleted(level : Level){
        // todo set level is completed
        levelsCompletedNum++
        // TODO maybe do same logic here as we did in the todo items exercise, nice api and will be easy to write tests for it as a bonus to our project
    }

}
