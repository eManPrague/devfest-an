package lv.rigadevday.android.ui.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

@SuppressLint("ViewConstructor")
class BadgeView : View {
    val padding: Float = 0.1F
    val paint: Paint = Paint()
    var heightStroke: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setColor(color: Int){
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
        paint.color = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.strokeWidth = w.toFloat()
        heightStroke = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(0.toFloat(),heightStroke*padding,0.toFloat(),heightStroke-(heightStroke*padding),paint)
    }



}