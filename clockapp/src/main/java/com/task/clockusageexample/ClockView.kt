package com.task.clockusageexample

import android.content.Context
import android.graphics.*
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.task.clockusageexample.R
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.properties.Delegates

class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val centerPos: PointF = PointF(0.0f, 0.0f)
    private var base by Delegates.notNull<Int>()
    private var text by Delegates.notNull<Int>()
    private var frame by Delegates.notNull<Int>()
    private var dots by Delegates.notNull<Int>()
    private var hourArrow by Delegates.notNull<Int>()
    private var minuteArrow by Delegates.notNull<Int>()
    var secondsArrow by Delegates.notNull<Int>()
    private var radius by Delegates.notNull<Float>()
    private var centerX by Delegates.notNull<Float>()
    private var centerY by Delegates.notNull<Float>()

    init {
        context.withStyledAttributes(attrs, R.styleable.ClockView) {
            hourArrow = getColor(
                R.styleable.ClockView_hourArrow, ContextCompat.getColor(context,
                    R.color.black
                ))
            minuteArrow = getColor(
                R.styleable.ClockView_minuteArrow, ContextCompat.getColor(context,
                    R.color.black
                ))
            secondsArrow = getColor(
                R.styleable.ClockView_secondArrow, ContextCompat.getColor(context,
                    R.color.gray
                ))
            base = getColor(
                R.styleable.ClockView_base, ContextCompat.getColor(context,
                    R.color.light_gray
                ))
            text = getColor(
                R.styleable.ClockView_text, ContextCompat.getColor(context,
                    R.color.black
                ))
            frame = getColor(
                R.styleable.ClockView_frame, ContextCompat.getColor(context,
                    R.color.black
                ))
            dots = getColor(
                R.styleable.ClockView_dots, ContextCompat.getColor(context,
                    R.color.black
                ))
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textScaleX = 0.9f
        letterSpacing = -0.15f
        typeface = Typeface.DEFAULT
    }

    private fun drawClockFrame(canvas: Canvas) {
        paint.color = frame
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = radius / 12
        val boundaryRadius = radius - paint.strokeWidth / 2
        canvas.drawCircle(centerX, centerY, boundaryRadius, paint)
        paint.strokeWidth = 0f
    }

    private fun PointF.computeXYForPoints(pos: Int, radius: Float) {
        val angle = (pos * (Math.PI / 30)).toFloat()
        x = radius * cos(angle) + centerX
        y = radius * sin(angle) + centerY
    }

    private fun PointF.computeXYForHourLabels(hour: Int, radius: Float) {
        val angle = (-Math.PI / 2 + hour * (Math.PI / 6)).toFloat()
        x = radius * cos(angle) + centerX
        val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        y = radius * sin(angle) + centerY - textBaselineToCenter
    }

    private fun drawClockBase(canvas: Canvas) {
        paint.color = base
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawDots(canvas: Canvas) {
        paint.color = dots
        paint.style = Paint.Style.FILL
        val dotsDrawLineRadius = radius * 5 / 6
        for (i in 0 until 60) {
            centerPos.computeXYForPoints(i, dotsDrawLineRadius)
            val dotRadius = if (i % 5 == 0) radius / 96 else radius / 128
            canvas.drawCircle(centerPos.x, centerPos.y, dotRadius, paint)
        }
    }

    private fun drawHourLabels(canvas: Canvas) {
        paint.textSize = radius * 2 / 7
        paint.strokeWidth = 0f
        paint.color = text
        val labelsDrawLineRadius = radius * 11 / 16
        for (i in 1..12) {
            centerPos.computeXYForHourLabels(i, labelsDrawLineRadius)
            val label = i.toString()
            canvas.drawText(label, centerPos.x, centerPos.y, paint)
        }
    }

    private fun drawClockHands(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        paint.style = Paint.Style.STROKE
        drawHourHand(canvas, hour + minute / 60f)
        drawMinuteHand(canvas, minute)
        drawSecondHand(canvas, second)
    }

    private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
        paint.color = hourArrow
        paint.strokeWidth = radius / 15
        val angle = (Math.PI * hourWithMinutes / 6 + -Math.PI / 2).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * 3 / 14,
            centerY - sin(angle) * radius * 3 / 14,
            centerX + cos(angle) * radius * 7 / 14,
            centerY + sin(angle) * radius * 7 / 14,
            paint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, minute: Int) {
        paint.color = minuteArrow
        paint.strokeWidth = radius / 40
        val angle = (Math.PI * minute / 30 + -Math.PI / 2).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * 2 / 7,
            centerY - sin(angle) * radius * 2 / 7,
            centerX + cos(angle) * radius * 5 / 7,
            centerY + sin(angle) * radius * 5 / 7,
            paint
        )
    }

    private fun drawSecondHand(canvas: Canvas, second: Int) {
        paint.color = secondsArrow
        val angle = (Math.PI * second / 30 + -Math.PI / 2).toFloat()
        paint.strokeWidth = radius / 80
        canvas.drawLine(
            centerX - cos(angle) * radius * 1 / 14,
            centerY - sin(angle) * radius * 1 / 14,
            centerX + cos(angle) * radius * 5 / 7,
            centerY + sin(angle) * radius * 5 / 7,
            paint
        )
        paint.strokeWidth = radius / 50
        canvas.drawLine(
            centerX - cos(angle) * radius * 2 / 7,
            centerY - sin(angle) * radius * 2 / 7,
            centerX - cos(angle) * radius * 1 / 14,
            centerY - sin(angle) * radius * 1 / 14,
            paint
        )
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = min(width, height) / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockBase(canvas)
        drawClockFrame(canvas)
        drawDots(canvas)
        drawHourLabels(canvas)
        drawClockHands(canvas)
        postInvalidateDelayed(180L)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = (240 * resources.displayMetrics.density).toInt()
        val defaultHeight = (240 * resources.displayMetrics.density).toInt()
        val widthToSet = resolveSize(defaultWidth, widthMeasureSpec)
        val heightToSet = resolveSize(defaultHeight, heightMeasureSpec)
        setMeasuredDimension(widthToSet, heightToSet)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("hourArrow", hourArrow)
        bundle.putInt("minuteArrow", minuteArrow)
        bundle.putInt("secondArrow", secondsArrow)
        bundle.putInt("base", base)
        bundle.putInt("text", text)
        bundle.putInt("frame", frame)
        bundle.putInt("dots", dots)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            base = state.getInt("base")
            text = state.getInt("text")
            frame = state.getInt("frame")
            dots = state.getInt("dots")
            hourArrow = state.getInt("hourArrow")
            minuteArrow = state.getInt("minuteArrow")
            secondsArrow = state.getInt("secondArrow")
            superState = if (SDK_INT >= 33) state.getParcelable("superState", Parcelable::class.java)
            else @Suppress("DEPRECATION") state.getParcelable("superState")
        }
        super.onRestoreInstanceState(superState)
    }
}