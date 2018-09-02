package ru.sukharev.focustimer.utils

import android.content.Context
import ru.sukharev.focustimer.R

enum class Level(val maxPoints: Int, val reductionModifier: Int) {
    ZERO(toSeconds(40), 100),
    ONE(toSeconds(60),80),
    TWO(toSeconds(100),60),
    THREE(toSeconds(150),50),
    FOUR(toSeconds(250), 30),
    FIVE(toSeconds(500), 10);

    fun getMinPoints():Int{
        var total = 0
        for (level in Level.values()) {
            if (ordinal > level.ordinal) {
                total += level.maxPoints
            } else {
                break
            }
        }
        return total
    }

    fun getName(context : Context) : String{
        return context.resources.getStringArray(R.array.levels)[ordinal]
    }

    companion object {
        fun calculateDecrease(curExp : Int, time : Int) : Int{
            var curLevel = ZERO
            for (level in Level.values()) {
                if (curExp > level.getMinPoints()) {
                    curLevel = level
                } else {
                    break
                }
            }
            val newExp = Math.max(curExp - time / curLevel.reductionModifier, 0)
            if (newExp < curLevel.getMinPoints()) {
                val intermediateExp = curLevel.getMinPoints()
                val intermediateTime = time - (curExp - intermediateExp) * curLevel.reductionModifier
                return calculateDecrease(intermediateExp, intermediateTime)
            }
            return newExp
        }

        fun getLevelEntry(curExp: Int):LevelEntry{
            var curLevel = ZERO
            for (level in Level.values()) {
                if (curExp >= level.getMinPoints()) {
                    curLevel = level
                } else {
                    break
                }
            }
            return LevelEntry(curLevel, curExp - curLevel.getMinPoints())
        }

        fun getValidExpValue(value: Int): Int {
            val maxLevel = Level.values()[Level.values().size - 1]
            return Math.max(Math.min(value,
                    maxLevel.getMinPoints() + maxLevel.maxPoints), 0)
        }
    }
}

data class LevelEntry(val level: Level, val exp: Int)