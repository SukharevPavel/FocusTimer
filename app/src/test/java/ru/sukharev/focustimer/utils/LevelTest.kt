package ru.sukharev.focustimer.utils

import org.junit.Test

import org.junit.Assert.*

class LevelTest {

    @Test
    fun getTotalPoints() {
        assertEquals(0, Level.ONE.getMinPoints())

        assertEquals(toSeconds(30), Level.TWO.getMinPoints())

        assertEquals(toSeconds(80), Level.THREE.getMinPoints())

        assertEquals(toSeconds(150), Level.FOUR.getMinPoints())

        assertEquals(toSeconds(250), Level.FIVE.getMinPoints())
    }

    @Test
    fun calculateDecrease() {
        assertEquals(0,
                Level.calculateDecrease(toSeconds(500), Int.MAX_VALUE))
        assertEquals(toSeconds(495),
                Level.calculateDecrease(toSeconds(500), toSeconds(50)))
        assertEquals(toSeconds(250),
                Level.calculateDecrease(toSeconds(500), toSeconds(2500)))
        assertEquals(toSeconds(225),
                Level.calculateDecrease(toSeconds(500), toSeconds(3000)))
        assertEquals(toSeconds(225),
                Level.calculateDecrease(toSeconds(250), toSeconds(500)))
        assertEquals(toSeconds(150),
                Level.calculateDecrease(toSeconds(500), toSeconds(2500 + 2000)))
        assertEquals(toSeconds(80),
                Level.calculateDecrease(toSeconds(500), toSeconds(2500 + 2000 + 2100)))
        assertEquals(toSeconds(30),
                Level.calculateDecrease(toSeconds(500), toSeconds(2500 + 2000 + 2100 + 2000)))
        assertEquals(toSeconds(0),
                Level.calculateDecrease(toSeconds(500), toSeconds(2500 + 2000 + 2100 + 2000 + 1500)))

        assertEquals(30,
                Level.calculateDecrease(toSeconds(500), toSeconds(2500 + 2000 + 2100 + 2000 + 1500)-30*50))
    }

    @Test
    fun getLevelEntry(){
        assertEquals(LevelEntry(Level.ONE,0), Level.getLevelEntry(toSeconds(0)))
        assertEquals(LevelEntry(Level.TWO,0), Level.getLevelEntry(toSeconds(30)))
        assertEquals(LevelEntry(Level.THREE,0), Level.getLevelEntry(toSeconds(80)))

        assertEquals(LevelEntry(Level.FOUR,0), Level.getLevelEntry(toSeconds(150)))

        assertEquals(LevelEntry(Level.FIVE,0), Level.getLevelEntry(toSeconds(250)))

        assertEquals(LevelEntry(Level.THREE, toSeconds(20)), Level.getLevelEntry(toSeconds(100)))
        assertEquals(LevelEntry(Level.FIVE, toSeconds(250)), Level.getLevelEntry(toSeconds(500)))


    }
}