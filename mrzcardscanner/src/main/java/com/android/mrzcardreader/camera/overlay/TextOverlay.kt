package com.android.mrzcardreader.camera.overlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.android.mrzcardscanner.R


class TextOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var frameTop: Float = 0f
    var frameBottom: Float = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outPath = Path()
    private val outLinePath = Path()
    private val frameRect = RectF()
    private val graphicLock = Any()
    private val textGraphics: MutableList<TextGraphic> = ArrayList()


    private val outerRegionPaint: Paint = Paint().apply {
        color = Color.parseColor("#bdbdbd")
        style = Paint.Style.FILL
    }

    private val cardOutline: Paint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 4f.toDp()
        style = Paint.Style.STROKE
    }

    init {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.TextOverlay)
            try {
                val backGroundColor =
                    attributes.getColor(R.styleable.TextOverlay_backgroundColor, Color.BLACK)
                outerRegionPaint.apply {
                    color = backGroundColor
                }
            } finally {
                attributes.recycle()
            }
        }
    }

    fun Float.toDp(): Float {
        val density = context.resources.displayMetrics.density
        return this / density
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        outPath.reset()
        outLinePath.reset()

        val height = height.toFloat()
        val width = width.toFloat()

        outPath.apply {
            moveTo(0f, 0f)
            lineTo(0f, height)
            lineTo(width, height)
            lineTo(width, 0f)
            fillType = Path.FillType.EVEN_ODD
        }

        frameRect.apply {
            left = 30f.toDp()
            top = (height / 2) - 300f
            right = width - 30f.toDp()
            bottom = (height / 2) + 300f
        }

        frameTop = frameRect.top
        frameBottom = frameRect.bottom

        outLinePath.addRoundRect(
            frameRect,
            16f.toDp(),
            16f.toDp(),
            Path.Direction.CW
        )

        outPath.addPath(outLinePath)

        canvas?.drawPath(outPath, outerRegionPaint)
        canvas?.drawPath(outLinePath, cardOutline)

        canvas?.let { drawTextOverlays(it) }
    }

    private fun drawTextOverlays(canvas: Canvas) {
        Log.d("ak", "drawTextOverlays: " + textGraphics.size)
        synchronized(graphicLock) {
            textGraphics.forEach() { textGraphic ->
                textGraphic.draw(canvas)
            }
        }

    }

    fun clear() {
        synchronized(graphicLock) {
            textGraphics.clear()
            postInvalidate()
        }
    }

    fun add(textGraphic: List<TextGraphic>) {
        clear()
        synchronized(graphicLock) {
            textGraphics.addAll(textGraphic)
            postInvalidate()
        }
    }

}



