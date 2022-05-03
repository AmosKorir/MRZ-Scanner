package com.android.mrzcardreader.camera.overlay

import android.content.Context
import android.graphics.*
import com.google.mlkit.vision.text.Text
import kotlin.math.ceil

class TextGraphic(
    private val context: Context,
    private val imageRect: Rect,
    private val textBlock: Text.TextBlock,
    private val textOverlay: TextOverlay
) {

    private val painter = Paint()

    fun draw(canvas: Canvas?) {
        canvas?.let { _canvas ->
            painter.color = Color.GREEN
            painter.strokeWidth = 4f
            painter.style = Paint.Style.STROKE
            val textRect = textBlock.boundingBox

            _canvas.drawRect(
                calculateTextRect(
                    textRect!!
                ), painter
            )
        }
    }

    fun Float.toDp(): Float {
        val density = context.resources.displayMetrics.density
        return this / density
    }

    fun isValidTextBlock(): Boolean {
        textBlock.boundingBox?.let { rect ->

            val text = calculateTextRect(
                rect
            )
            if (text.top < textOverlay.frameTop) {
                return false
            }
            if (text.bottom > textOverlay.frameBottom) {
                return false
            }
            return true

        } ?: return false

    }

    private fun calculateTextRect(textRect: Rect): RectF {
        val scaleX = textOverlay.width.toFloat() / imageRect.width()
        val scaleY = textOverlay.height.toFloat() / imageRect.height()
        val scale = scaleX.coerceAtLeast(scaleY)

        val offsetX = (textOverlay.width.toFloat() - ceil(imageRect.width() * scale)) / 2.0f
        val offsetY = (textOverlay.height.toFloat() - ceil(imageRect.height() * scale)) / 2.0f

        return RectF().apply {
            left = textRect.left * scale + offsetX
            top = textRect.top * scale + offsetY
            right = textRect.right * scale + offsetX
            bottom = textRect.bottom * scale + offsetY
        }
    }


}