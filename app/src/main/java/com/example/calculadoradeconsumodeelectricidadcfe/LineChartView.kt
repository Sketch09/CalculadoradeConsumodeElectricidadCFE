package com.example.calculadoradeconsumodeelectricidadcfe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineChartView : View {
    data class DataPoint(val value: Float, val date: String)

    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt() // Black color
        strokeWidth = 5f // Line width
        style = Paint.Style.STROKE
    }
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt() // Black color
        textSize = 30f // Text size
    }
    private var dataPoints: List<DataPoint> = emptyList() // Updated to use DataPoint

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        textPaint.apply {
            textAlign = Paint.Align.CENTER // Center align text
            textSize = 30f // Adjust text size as needed
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.isNotEmpty()) {
            drawLineChart(canvas)
        }
    }

    private fun drawLineChart(canvas: Canvas) {
        val max = dataPoints.maxOfOrNull { it.value } ?: return
        val min = dataPoints.minOfOrNull { it.value } ?: return
        val deltaX = width.toFloat() / (dataPoints.size - 1)
        val deltaY = height.toFloat() / (max - min)

        var prevX = 0f
        var prevY = height - (dataPoints.first().value - min) * deltaY
        dataPoints.forEachIndexed { index, dataPoint ->
            val x = index * deltaX
            val y = height - (dataPoint.value - min) * deltaY
            if (index > 0) canvas.drawLine(prevX, prevY, x, y, linePaint)
            canvas.drawText("${dataPoint.value} kWh", x, y - 20, textPaint) // Value text
            canvas.drawText(dataPoint.date, x, (height - 5).toFloat(), textPaint) // Date text below the graph
            prevX = x
            prevY = y
        }
    }

    fun setDataPoints(points: List<DataPoint>) {
        this.dataPoints = points
        invalidate() // Redraw the view
    }
}

