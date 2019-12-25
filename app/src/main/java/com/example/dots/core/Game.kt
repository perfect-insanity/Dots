package com.example.dots.core

import java.util.*
import kotlin.collections.ArrayList

class Game(val width: Int, val height: Int) {
    val field = ArrayList<MutableList<Dot>>()
    var player = Player.FIRST
    val borders = ArrayList<MutableList<Dot>>()
    val moves = Stack<MoveInfo>()
    var isFinished = false
    private var traps = ArrayList<Surrender>()
    private var iteration = 0

    init {
        Player.FIRST.ownership = 0
        Player.SECOND.ownership = 0

        for (i in 0 until width) {
            val row = ArrayList<Dot>()
            for (j in 0 until height) {
                row.add(Dot(i, j))
            }
            field.add(row)
        }
    }

    companion object {
        private const val NEIGHBORHOOD_SIZE = 8
    }

    data class Surrender(
        var indemnity: Int = 0,
        var surrounded: MutableSet<Dot> = HashSet(),
        var border: MutableList<Dot> = ArrayList()
    ) {
        fun addAll(newData: Surrender) {
            indemnity += newData.indemnity
            surrounded.addAll(newData.surrounded)
            border.addAll(newData.border)
        }
    }

    data class MoveInfo(
        val dot: Dot,
        var firstPlayerOwnership: Int = Player.FIRST.ownership,
        var secondPlayerOwnership: Int = Player.SECOND.ownership,
        var surrenders: ArrayList<Surrender> = ArrayList(),
        var traps: ArrayList<Surrender> = ArrayList()
    )

    fun makeMove(i: Int, j: Int, changePlayer: Boolean): Boolean {
        val dot = field[i][j]
        if (dot.player != Player.NONE || dot.isSurrounded)
            return false

        val moveInfo = MoveInfo(dot = dot, traps = ArrayList(traps))
        dot.player = player.apply { ownership++ }

        if (changePlayer)
            player = player.enemy()

        fun Surrender.capitulate() {
            if (indemnity == 0)
                traps.add(this)
            else {
                surrounded.forEach { it.isSurrounded = true }
                setIndemnity(border[0].player, indemnity)

                borders.add(border)
                moveInfo.surrenders.add(this)
            }
        }

        val notAllyNeighbors = getNeighbors(dot, true, dot.player).filter {
            it.player != dot.player
        }
        val startIteration = ++iteration
        var isCloser = false
        for (neighbor in notAllyNeighbors) {
            if (neighbor.checkedCount < startIteration) {
                surround(neighbor, dot.player, ++iteration)?.apply {
                    border = findBorder(border, dot, dot, -iteration)!!
                    capitulate()
                    isCloser = true
                }
            }
        }

        val trap = checkIfTrapped(dot)
        if (trap != null) {
            if (!isCloser) {
                trap.indemnity = 1
                trap.capitulate()
            }
            traps.remove(trap)
        }

        moves.push(moveInfo)
        isFinished = checkIfFinished()

        return true
    }

    fun undoMove(): Boolean {
        if (moves.empty()) return false
        val (lastDot, firstPlayerOwnership, secondPlayerOwnership, lastSurrenders, lastTraps) =
            moves.pop()

        player = lastDot.player
        field[lastDot.x][lastDot.y] = lastDot.apply { player = Player.NONE }

        Player.FIRST.ownership = firstPlayerOwnership
        Player.SECOND.ownership = secondPlayerOwnership

        for (lastSurrender in lastSurrenders) {
            lastSurrender.surrounded.forEach { field[it.x][it.y].isSurrounded = false }
            borders.remove(lastSurrender.border)
        }

        traps = lastTraps
        return true
    }

    private fun getNeighbors(
        dot: Dot,
        ignoreDiagonal: Boolean = false,
        player: Player = Player.NONE
    ): MutableList<Dot> {
        val res = ArrayList<Dot>()
        fun <T> safeGet(from: List<List<T>>, x: Int, y: Int): T? =
            try { from[x][y] }
            catch (ignore: IndexOutOfBoundsException) { null }

        for (i in -1..1) {
            for (j in -1..1) {
                if (i != 0 || j != 0) {
                    if (
                        !ignoreDiagonal ||
                        i == 0 || j == 0 ||
                        safeGet(field, dot.x + i, dot.y)?.player != player ||
                        safeGet(field, dot.x, dot.y + j)?.player != player
                    ) {
                        safeGet(field, dot.x + i, dot.y + j)?.let {
                            res.add(it)
                        }
                    }
                }
            }
        }
        return res
    }

    private fun setIndemnity(player: Player, indemnity: Int) {
        player.ownership += indemnity
        player.enemy().ownership -= indemnity
    }

    private fun checkIfTrapped(dot: Dot): Surrender? {
        for (trap in traps) {
            if (dot.player == trap.border[0].player.enemy() && dot in trap.surrounded)
                return trap
        }

        return null
    }

    private fun checkIfFinished(): Boolean {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val dot = field[i][j]
                if (!dot.isSurrounded && dot.player == Player.NONE)
                    return false
            }
        }
        return true
    }

    private fun surround(dot: Dot, player: Player, iteration: Int): Surrender? {
        dot.checkedCount = iteration
        val res = Surrender().apply {
            if (dot.player != Player.NONE)
                indemnity++
            if (!dot.isSurrounded)
                surrounded.add(dot)
        }

        val neighbors = getNeighbors(dot)
        if (neighbors.size != NEIGHBORHOOD_SIZE)
            return null

        for (nextDot in getNeighbors(dot, true, player)) {
            if (nextDot.player == player && !nextDot.isSurrounded)
                res.border.add(nextDot)
            else if (nextDot.checkedCount != iteration)
                res.addAll(surround(nextDot, player, iteration)?: return null)
        }

        return res
    }

    private fun findBorder(
        possibleBorder: MutableCollection<Dot>?,
        startDot: Dot,
        currentDot: Dot,
        iteration: Int
    ): MutableList<Dot>? {
        currentDot.checkedCount = iteration
        val next = getNeighbors(currentDot).apply {
            retainAll(possibleBorder!!)
        }

        if (next.size >= 2) {
            val notCheckedNext = next.filter { it.checkedCount != iteration }
            if (notCheckedNext.isEmpty() && startDot in next)
                return ArrayList(listOf(currentDot))

            for (nextDot in notCheckedNext) {
                val branch = findBorder(possibleBorder, startDot, nextDot, iteration)
                if (branch != null) {
                    branch.add(currentDot)
                    return branch
                }
            }
        }

        return null
    }
}
