package com.cookingtimer.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var totalMillis = 0L
    private var remainingMillis = 0L

    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val circleBorderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val handPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val tickPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.STROKE
        strokeWidth = 12f
        isAntiAlias = true
    }

    fun setTime(totalMillis: Long, remainingMillis: Long) {
        this.totalMillis = totalMillis
        this.remainingMillis = remainingMillis
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - 40f

        // Draw circle
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        canvas.drawCircle(centerX, centerY, radius, circleBorderPaint)

        // Draw hour ticks
        for (i in 0..11) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val startX = centerX + (radius - 30f) * cos(angle).toFloat()
            val startY = centerY + (radius - 30f) * sin(angle).toFloat()
            val endX = centerX + (radius - 10f) * cos(angle).toFloat()
            val endY = centerY + (radius - 10f) * sin(angle).toFloat()
            canvas.drawLine(startX, startY, endX, endY, tickPaint)
        }

        // Draw progress arc
        if (totalMillis > 0) {
            val progressAngle = (remainingMillis.toFloat() / totalMillis.toFloat()) * 360f
            canvas.drawArc(
                centerX - radius + 20f,
                centerY - radius + 20f,
                centerX + radius - 20f,
                centerY + radius - 20f,
                -90f,
                -progressAngle,
                false,
                progressPaint
            )
        }

        // Draw hand representing remaining time
        if (totalMillis > 0) {
            val angle = Math.toRadians(
                ((remainingMillis.toFloat() / totalMillis.toFloat()) * 360f - 90f).toDouble()
            )
            val handLength = radius - 50f
            val endX = centerX + handLength * cos(angle).toFloat()
            val endY = centerY + handLength * sin(angle).toFloat()
            canvas.drawLine(centerX, centerY, endX, endY, handPaint)

            // Draw center dot
            canvas.drawCircle(centerX, centerY, 10f, handPaint)
        }
    }
}
