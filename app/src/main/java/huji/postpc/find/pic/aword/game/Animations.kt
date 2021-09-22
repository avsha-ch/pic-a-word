package huji.postpc.find.pic.aword.game

import android.view.View
import android.view.animation.*
import huji.postpc.find.pic.aword.game.models.Level

fun inFromRightAnimation(): Animation {
    val inFromRight: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, +1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    inFromRight.duration = 500
    inFromRight.interpolator = AccelerateInterpolator()
    return inFromRight
}

fun outToLeftAnimation(): Animation {
    val outtoLeft: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, -1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    outtoLeft.duration = 500
    outtoLeft.interpolator = AccelerateInterpolator()
    return outtoLeft
}

fun inFromLeftAnimation(): Animation {
    val inFromLeft: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, -1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    inFromLeft.duration = 500
    inFromLeft.interpolator = AccelerateInterpolator()
    return inFromLeft
}

fun outToRightAnimation(): Animation {
    val outtoRight: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, +1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    outtoRight.duration = 500
    outtoRight.interpolator = AccelerateInterpolator()
    return outtoRight
}

/**
 * Animates a view to exit the screen from one side and enter from the other side
 * Can apply and function for updating Ui elements if wanted
 * @param view The view to animate
 * @param translation how much to move the view and in what direction.
 *                    For exit to left and enter from right - send positive value
 *                    For exit to right and enter from left - send negative value
 * @param updateUi function that updates Ui according to wanted level
 * @param level level to update to
 */
fun animateViewOutIn(view: View, translation : Float, updateUi: (Level) -> Unit, level: Level){
    if (translation != 0f) {
        // animate old view out
        view.animate()
            .translationX(-translation)
            .setDuration(450L)
            .withEndAction {
                // update Ui if needed
                updateUi(level)

                // move view to other side to enter from
                view.visibility = View.INVISIBLE
                view.animate()
                    .translationX(translation)
                    .setDuration(1L)
                    .withEndAction {
                        // animate new view in
                        view.visibility = View.VISIBLE
                        view.animate()
                            .translationX(0f)
                            .setDuration(450L)
                            .start()
                    }
                    .start()
            }
            .start()
    }
    else{
        updateUi(level)
    }
}

/**
 * Animates a view to appear for sometime and then disappear
 * @param view The view to animate
 * @param duration how much time should the animation take
 */
fun appearDisappearView(view: View, duration : Long){
    view.alpha = 0f
    view.visibility = View.VISIBLE
    view.animate()
        .alpha(1f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { view.visibility = View.GONE }
                .start()
        }
        .start()
}