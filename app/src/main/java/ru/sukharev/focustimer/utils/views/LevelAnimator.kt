package ru.sukharev.focustimer.utils.views

import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.TextView
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.Level
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.SPACE
import ru.sukharev.focustimer.utils.animation.ProgressAnimation

class LevelAnimator(val levelProgressBar: ProgressBar, val levelTextView: TextView) {

    lateinit var currentLevelEntry : LevelEntry
    var initialized = false

    fun setValues(levelEntry: LevelEntry) {
        if (!initialized) {
            instantSetLevel(levelEntry)
            initialized = true
            return
        }
        val totalExp = levelEntry.level.getMinPoints() + levelEntry.exp
        val baseExp = currentLevelEntry.exp + currentLevelEntry.level.getMinPoints()
        if (baseExp < totalExp) {
            val totalDiff = Math.abs(totalExp - baseExp)
            smoothSetLevel(levelEntry, totalDiff)
        } else {
            instantSetLevel(levelEntry)
        }

    }

    private fun smoothSetLevel(levelEntry: LevelEntry, totalDiff: Int){
        val totalExp = levelEntry.level.getMinPoints() + levelEntry.exp
        val baseExp = currentLevelEntry.exp + currentLevelEntry.level.getMinPoints()
        val currentLevelMax = currentLevelEntry.level.maxPoints + currentLevelEntry.level.getMinPoints()
        val animation = ProgressAnimation(levelProgressBar,
                baseExp - currentLevelEntry.level.getMinPoints(),
                Math.min(totalExp, currentLevelMax) - currentLevelEntry.level.getMinPoints())
        animation.duration = TOTAL_ANIMATION_DURATION * (Math.min(totalExp, currentLevelMax) - baseExp) / totalDiff
        animation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (currentLevelMax <= totalExp) {
                    val newLevelIndex = currentLevelEntry.level.ordinal + 1
                    if (newLevelIndex < Level.values().size) {
                        val newLevel = Level.values()[newLevelIndex]
                        instantSetLevel(LevelEntry(newLevel, 0))
                        smoothSetLevel(levelEntry, totalDiff)
                    } else {
                        instantSetLevel(levelEntry)
                    }
                } else {
                    instantSetLevel(levelEntry)
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        levelProgressBar.startAnimation(animation)
    }

    private fun instantSetLevel(levelEntry: LevelEntry){
        levelProgressBar.max = levelEntry.level.maxPoints
        levelProgressBar.progress = levelEntry.exp
        val textString = levelTextView.context.getString(R.string.level) + SPACE + levelEntry.level.getName(levelTextView.context)
        levelTextView.text = textString
        currentLevelEntry = levelEntry
    }


    companion object {
        const val TOTAL_ANIMATION_DURATION = 1000L
    }

}