package org.techtown.recoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(context: Context, attr: AttributeSet) : View(context, attr) {

    var onRequestCurrentAmplitude: Int? = null

    //anti_aliat_flag -> 곡선으로 보이게
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayinPosition: Int = 0

    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if (!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude ?: 0
                drawAmplitudes = listOf(currentAmplitude) + drawAmplitudes
            } else {
                replayinPosition++
            }
            invalidate() //무효화(추가 될때 드로잉하게)==초기화

            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2F
        var offsetX = drawingWidth.toFloat()

        drawAmplitudes
            .let { amplitudes ->
                if (isReplaying) {
                    amplitudes.takeLast(replayinPosition)
                } else {
                    amplitudes
                }
            }
            .forEach { amlitude ->
                val lineLength = amlitude / MAX_AMPLITUDE * drawingHeight * 0.8F

                offsetX -= LINE_SPACE
                if (offsetX < 0) {
                    return@forEach
                }

                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
                )

            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying

        handler?.post(visualizeRepeatAction)

    }

    fun stopVisualizing() {
        replayinPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization() {
        drawAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}