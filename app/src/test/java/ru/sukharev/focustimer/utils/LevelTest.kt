package ru.sukharev.focustimer.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class LevelTest {

    @Test
    fun testMinPoints_getValidMinPoints() {
        assertEquals(0, Level.ZERO.getMinPoints())

        assertEquals(toSeconds(40), Level.ONE.getMinPoints())

        assertEquals(toSeconds(100), Level.TWO.getMinPoints())

        assertEquals(toSeconds(200), Level.THREE.getMinPoints())

        assertEquals(toSeconds(350), Level.FOUR.getMinPoints())

        assertEquals(toSeconds(600), Level.FIVE.getMinPoints())
    }

    @Test
    fun testCalculateDecrease_calculateValueDecreaseWithTime() {
        assertEquals(0,
                Level.calculateDecrease(toSeconds(1100), Int.MAX_VALUE))
        assertEquals(toSeconds(1095),
                Level.calculateDecrease(toSeconds(1100), toSeconds(50)))
        assertEquals(toSeconds(600),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10)))
        assertEquals(toSeconds(550),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 50)))
        assertEquals(toSeconds(550),
                Level.calculateDecrease(toSeconds(600), toSeconds(30 * 50)))
        assertEquals(toSeconds(350),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 250)))
        assertEquals(toSeconds(200),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 250 + 150 * 50)))
        assertEquals(toSeconds(100),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 250 + 150 * 50 + 100 * 60)))
        assertEquals(toSeconds(40),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 250 + 150 * 50 + 100 * 60 + 60 * 80)))
        assertEquals(toSeconds(0),
                Level.calculateDecrease(toSeconds(1100), toSeconds(500 * 10 + 30 * 250 + 150 * 50 + 100 * 60 + 60 * 80 + 40 * 100)))

        assertEquals(30,
                Level.calculateDecrease(toSeconds(1100),
                        toSeconds(500 * 10 + 30 * 250 + 150 * 50 + 100 * 60 + 60 * 80 + 40 * 100)-30*100))
    }

    @Test
    fun testGetLevelEntry_getAppropriateLevelEntry() {
        assertEquals(LevelEntry(Level.ZERO,0), Level.getLevelEntry(toSeconds(0)))
        assertEquals(LevelEntry(Level.ONE,0), Level.getLevelEntry(toSeconds(40)))
        assertEquals(LevelEntry(Level.TWO,0), Level.getLevelEntry(toSeconds(100)))

        assertEquals(LevelEntry(Level.THREE,0), Level.getLevelEntry(toSeconds(200)))

        assertEquals(LevelEntry(Level.FOUR,0), Level.getLevelEntry(toSeconds(350)))

        assertEquals(LevelEntry(Level.FIVE,0), Level.getLevelEntry(toSeconds(600)))

        assertEquals(LevelEntry(Level.FIVE,toSeconds(500)), Level.getLevelEntry(toSeconds(1100)))

        assertEquals(LevelEntry(Level.TWO, toSeconds(20)), Level.getLevelEntry(toSeconds(120)))

    }

    @Test
    fun testGetValidExpValue_checkBoundaries() {
        assertEquals(0, Level.getValidExpValue(-1000))
        assertEquals(Level.FIVE.maxPoints + Level.FIVE.getMinPoints(), Level.getValidExpValue(Integer.MAX_VALUE))
        assertEquals(Level.FIVE.maxPoints + Level.FIVE.getMinPoints(), Level.getValidExpValue(toSeconds(1105)))
    }

    @Test
    fun testGetValidExpValue_testEquality() {
        assertEquals(0, Level.getValidExpValue(0))
        assertEquals(toSeconds(100), Level.getValidExpValue(toSeconds(100)))
        assertEquals(toSeconds(1095), Level.getValidExpValue(toSeconds(1095)))
        assertEquals(toSeconds(1100), Level.getValidExpValue(toSeconds(1100)))
    }
}