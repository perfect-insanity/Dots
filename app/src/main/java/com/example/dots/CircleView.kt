package com.example.dots

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.dots.FieldView.Companion.radius
import com.example.dots.core.Player

class CircleView : View {
    private val paint = Paint()
    var player = Player.NONE

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas?) {
        layoutParams.apply {
            width = radius.toInt() * 2
            height = radius.toInt() * 2
        }
        canvas!!.drawCircle(
            radius, radius, radius,
            paint.apply { color = player.color }
        )
        invalidate()
    }
}