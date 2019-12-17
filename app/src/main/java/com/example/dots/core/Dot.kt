package com.example.dots.core

data class Dot(
    var x: Int,
    var y: Int
) {
    var player: Player = Player.NONE
    var isSurrounded: Boolean = false
    var checkedCount: Int = 0
}
