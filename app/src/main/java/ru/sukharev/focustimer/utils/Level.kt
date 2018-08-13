package ru.sukharev.focustimer.utils

enum class Level(val maxPoints: Int, val reductionModifier: Int) {
    ONE(toSeconds(30), 50),
    TWO(toSeconds(50),40),
    THREE(toSeconds(70),30),
    FOUR(toSeconds(100),20),
    FIVE(toSeconds(500), 10);

    fun getMinPoints():Int{
        var total = 0;
        for (level in Level.values()) {
            if (ordinal > level.ordinal) {
                total += level.maxPoints
            } else {
                break
            }
        }
        return total
    }

    companion object {
        fun calculateDecrease(curExp : Int, time : Int) : Int{
            var curLevel = ONE
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
            var curLevel = ONE
            for (level in Level.values()) {
                if (curExp >= level.getMinPoints()) {
                    curLevel = level
                } else {
                    break
                }
            }
            return LevelEntry(curLevel, curExp - curLevel.getMinPoints())
        }
    }
}

data class LevelEntry(val level: Level, val exp: Int)