package com.example.dots

object Labels {
    var APP_PREFERENCES: String = "com.example.dots.${::APP_PREFERENCES.name}"
    var GAME: String = ::GAME.name
    var FIRST_PLAYER_OWNERSHIP: String = ::FIRST_PLAYER_OWNERSHIP.name
    var SECOND_PLAYER_OWNERSHIP: String = ::SECOND_PLAYER_OWNERSHIP.name
    var WIDTH_DOTS: String = ::WIDTH_DOTS.name
    var HEIGHT_DOTS: String = ::HEIGHT_DOTS.name
    var HISTORY: String = ::HISTORY.name
    var RESET: String = ::RESET.name
}