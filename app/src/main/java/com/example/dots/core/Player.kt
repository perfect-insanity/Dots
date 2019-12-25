package com.example.dots.core

import android.graphics.Color

enum class Player(
    var ownership: Int,
    var nick: String?,
    var color: Int
) {
    NONE(0, null, Color.GRAY) {
        override fun enemy(): Player = NONE
    },
    FIRST(0, null, Color.RED) {
        override fun enemy(): Player = SECOND
    },
    SECOND(0, null, Color.BLUE) {
        override fun enemy(): Player = FIRST
    };

    abstract fun enemy(): Player
}
