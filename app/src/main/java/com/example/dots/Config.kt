package com.example.dots

object Config {
    enum class Type {
        X, Y;

        fun getMargin(): Float = when (this) {
            X -> marginHorizontal!!
            Y -> marginVertical!!
        }
    }

    const val distanceMM = 4.5f
    var distance = 0f
        set(v) {
            field = v
            defaultMargin = v
        }
    private var defaultMargin = 0f

    var widthPixels: Int? = null
        set(v) {
            field = v!!
            if (widthDots == null)
                widthDots = getDotsCount(v)
        }
    var heightPixels: Int? = null
        set(v) {
            field = v!!
            if (heightDots == null)
                heightDots = getDotsCount(v)
        }

    var widthDots: Int? = null
        set(v) {
            field = v!!
            marginHorizontal = getMargin(widthPixels!!, v)
        }
    var heightDots: Int? = null
        set(v) {
            field = v!!
            marginVertical = getMargin(heightPixels!!, v)
        }

    var marginHorizontal: Float? = null
        private set
    var marginVertical: Float? = null
        private set

    fun getDotsCount(pixels: Int): Int =
        (pixels - defaultMargin * 2).toInt() / distance.toInt() + 1

    private fun getMargin(pixels: Int, dots: Int): Float =
        (pixels - distance * (dots - 1)) / 2

    fun coordToIndex(coord: Float, type: Type): Int =
        ((coord - type.getMargin()) / distance + 0.5).toInt()

    fun indexToCoord(index: Int, type: Type): Float =
        index * distance + type.getMargin()
}