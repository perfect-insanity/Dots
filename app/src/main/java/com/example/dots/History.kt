package com.example.dots

object History {
    private const val MAX_SIZE = 100
    var entries = ArrayList<GameResult>()

    data class GameResult(
        var time: String,
        var firstPlayer: String,
        var secondPlayer: String,
        var score: String
    )

    fun add(entry: GameResult) {
        if (entries.size == MAX_SIZE)
            entries.removeAt(MAX_SIZE - 1)

        entries.add(0, entry)
    }
}