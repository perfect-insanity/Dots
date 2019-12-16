package com.example.dots.core

import android.graphics.Color

enum class Player(
    var ownership: Int,
    var nick: String,
    var color: Int
) {
    NONE(0, "Никто", Color.GRAY) {
        override fun enemy(): Player = NONE
    },
    FIRST(0, "Игрок 1", Color.RED) {
        override fun enemy(): Player = SECOND
    },
    SECOND(0, "Игрок 2", Color.BLUE) {
        override fun enemy(): Player = FIRST
    };

    abstract fun enemy(): Player
}
