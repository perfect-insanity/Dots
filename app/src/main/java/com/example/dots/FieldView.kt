package com.example.dots

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.dots.core.Game
import com.example.dots.core.Player
import java.text.DateFormat
import java.util.*

class FieldView : View {
    private val dotsPaint = Paint()
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = FieldView.strokeWidth
    }
    private val surrenderPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val path = Path()
    private var widthDots = Config.widthDots!!
    private var heightDots = Config.heightDots!!
    var game = Game(widthDots, heightDots)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    companion object {
        val radius = Config.distance / 5
        val strokeWidth = radius / 2
        const val transparency = 63
    }

    override fun onDraw(canvas: Canvas?) {
        (context as GameActivity).setScore(
            game.player,
            Player.FIRST.ownership,
            Player.SECOND.ownership
        )

        var x = 0
        while (x < game.width) {
            var y = 0
            while (y < game.height) {
                dotsPaint.color = game.field[x][y].player.color
                canvas!!.drawCircle(Config.indexToCoord(x, Config.Type.X), Config.indexToCoord(y, Config.Type.Y), radius, dotsPaint)
                y++
            }
            x++
        }

        for (border in game.borders) {
            path.reset()
            path.moveTo(Config.indexToCoord(border.last().x, Config.Type.X), Config.indexToCoord(border.last().y, Config.Type.Y))
            for (dot in border) {
                path.lineTo(Config.indexToCoord(dot.x, Config.Type.X), Config.indexToCoord(dot.y, Config.Type.Y))
            }
            canvas!!.apply {
                surrenderPaint.color = transparent(border[0].player.color)
                borderPaint.color = border[0].player.color
                drawPath(path, surrenderPaint)
                drawPath(path, borderPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val i = Config.coordToIndex(event.x, Config.Type.X)
        val j = Config.coordToIndex(event.y, Config.Type.Y)
        try {
            if (game.makeMove(i, j, true))
                return performClick()
        }
        catch (ignored: IndexOutOfBoundsException) {}

        return false
    }

    override fun performClick(): Boolean {
        super.performClick()

        invalidate()
        if (game.isFinished) {
            if (Player.FIRST.ownership == Player.SECOND.ownership) {
                showEndgameDialog(context.getString(R.string.draw))
            }
            else {
                val winner = when (Player.FIRST.ownership.compareTo(Player.SECOND.ownership)) {
                    1 -> Player.FIRST.nick
                    -1 -> Player.SECOND.nick
                    else -> null
                }
                showEndgameDialog(context.getString(R.string.player_win, winner))
            }
            History.add(History.GameResult(
                DateFormat.getDateTimeInstance().format(Date()),
                Player.FIRST.nick!!, Player.SECOND.nick!!,
                context.getString(R.string.score, Player.FIRST.ownership, Player.SECOND.ownership)
            ))

        }
        return true
    }

    fun undoLastMove() {
        if (game.undoMove())
            invalidate()
        else
            Toast.makeText(context, R.string.undo_to_beginning, Toast.LENGTH_SHORT).show()
    }

    fun clear() {
        game = Game(widthDots, heightDots)
        invalidate()
    }

    private fun transparent(color: Int): Int =
        Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color))

    private fun showEndgameDialog(text: String) {
        AlertDialog.Builder(context).apply {
            setMessage(text)
            setTitle(R.string.game_over)

            setPositiveButton(R.string.retry) { dialog, id ->
                clear()
                invalidate()
            }
            setNegativeButton(R.string.to_menu) { dialog, id ->
                getParentActivity().finish()
            }

            create()
        }.show()
    }

    private fun getParentActivity(): Activity {
        var parent = context
        while (parent is ContextWrapper) {
            if (parent is Activity) break
            parent = (context as ContextWrapper).baseContext
        }

        return parent as Activity
    }
}