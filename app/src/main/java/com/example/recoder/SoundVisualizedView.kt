package com.example.recoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.random.Random

class SoundVisualizedView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var onRequestCurrentAmplitude : (()-> Int)? = null

    val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
    var drawingWidth: Int = 0
    var drawingHeigh: Int = 0
    var drawingAmplitudes: List<Int> = emptyList()
    // (0..10).map { Random.nextInt(Short.MAX_VALUE.toInt()) }
    //emptyList()

    private val visualizedRepeatAction: Runnable = object :Runnable {

        override  fun run() {
            //Amplitude , Draw



            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //이전 값은 필요없고 새로운 값을 집어 넣는다 .
        drawingWidth = w
        drawingHeigh = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeigh / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes.forEach { amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeigh * 0.8F
            offsetX -= LINE_SPACE
            if (offsetX < 0) return@forEach

            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2f,
                offsetX,
                centerY + lineLength / 2f,
                amplitudePaint
            )
        }


    }


    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F

        // float으로 나눗셈시 0이 되는 것을 막는다 .
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}