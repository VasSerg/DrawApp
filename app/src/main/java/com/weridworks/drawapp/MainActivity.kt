package com.weridworks.drawapp

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import kotlin.math.pow


var mx = 10f
var my = 10f


class MainActivity : AppCompatActivity() {
    private val drawPaint = Paint()
    private var erasee = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //set orientation fixed in order not to loose all drawings after orientation change
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)


        val eraseBtn = findViewById<ImageButton>(R.id.eraser)
        val undoBtn = findViewById<ImageButton>(R.id.undo)
        val sliderBtn = findViewById<Slider>(R.id.sliderSize)
        val sliderColBtn = findViewById<Slider>(R.id.sliderCol)
        val mDrawLayout = findViewById<Draw2D>(R.id.Draw2D)

        undoBtn.setOnClickListener {
            mDrawLayout.undo()
        }

        eraseBtn.setOnClickListener {
            mDrawLayout.pathList.clear()
            mDrawLayout.paintList.clear()
            mDrawLayout.invalidate()
        }



        sliderBtn.value = 10/20f

        sliderBtn.addOnChangeListener { slider, value, fromUser ->
            mDrawLayout.STROKE_WIDTH = sliderBtn.value*20
        }



        sliderColBtn.value = 0.0778f


        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled) // disabled
        )
        val colors = intArrayOf(mDrawLayout.paintColor,mDrawLayout.paintColor)
        val myList = ColorStateList(states, colors)
        sliderColBtn.thumbTintList = myList



        sliderColBtn.addOnChangeListener { slider, value, fromUser ->
            val men = 100000
            val k = 0.53248
            val rag = 16.0.pow(4.0)
            val dist = (men/(rag) * 16.0.pow(2.0) -1*3*k).toInt()*2


            val m1 = 2*( (1 - 0.49) * - rag - 0 + 10).toInt()-1
            val m2 = ((2*(0.51 * - rag + 0 ).toInt()- 16.0.pow(5.0).toInt()*15)*0.99999-20).toInt()


            val normalize = (sliderColBtn.value*men - (sliderColBtn.value*men % dist))*1f/men


            if (sliderColBtn.value == 0f){
                mDrawLayout.paintColor = Color.WHITE
            }
            else if (sliderColBtn.value == 1f){
                mDrawLayout.paintColor = Color.BLACK
            }
            else if ((sliderColBtn.value > 0.49f) and (sliderColBtn.value < 0.51f )){
                mDrawLayout.paintColor = ((m1 + m2)/2)
            }
            else if (sliderColBtn.value < 0.5){
                mDrawLayout.paintColor = 2*( (1 - normalize) * - rag * 0.9986 - 0 -41).toInt()-1
            }
            else{
                mDrawLayout.paintColor = ((2*(normalize * - rag + 0 ).toInt()- 16.0.pow(5.0).toInt()*15)*0.99999-20).toInt()
            }

            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled), // enabled
                intArrayOf(-android.R.attr.state_enabled) // disabled
            )
            val colors = intArrayOf(mDrawLayout.paintColor,mDrawLayout.paintColor)
            val myList = ColorStateList(states, colors)
            sliderColBtn.thumbTintList = myList

            //Log.e("niggerc", mDrawLayout.paintColor.toString())
        }
    }
}

class Draw2D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var pathList = ArrayList<Path>()
    private var drawPath = Path()
    var paintList = ArrayList<Paint>()
    private var drawPaint = Paint()
    private var canvasPaint = Paint()
    var paintColor = -120787
    var STROKE_WIDTH = 10f
    var drawCanvas = Canvas()
    var canvasBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)


    fun undo() {
        if (pathList.size != 0){
            pathList.removeLast()
            paintList.removeLast()
            invalidate()
        }
    }


    private fun setPencil() {
        drawPath = Path()
        pathList.add(drawPath)
        drawPaint = Paint()
        paintList.add(drawPaint)
        drawPaint.color = paintColor
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = STROKE_WIDTH
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        //canvasPaint = Paint(Paint.DITHER_FLAG)
    }


    //************************************   draw view  *************************************************************
    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        for (i in pathList.indices) {
            canvas.drawPath(pathList[i], paintList[i])
            //Log.e("nigger", i.toString())
        }

        val min = 2*16*16*16

        val mm = 1*16*16*16


        val men = 100000
        val k = 0.53248f

        val rag = 16.0.pow(4.0)
        val dist = (men/(rag) * 16.0.pow(2.0) -1*k*3).toInt()*2 // 207
        for (i in 0..men step dist) {

            val normalize = i - (i % dist)

            val relx = (normalize*1f)/(men)
            Log.e("nigger", (relx).toString())
            //Log.e("nigger", normalize.toString())
            val dr = Paint()
            val dr2 = Paint()
            dr.color =(relx * - rag*0.9986 - 0 ).toInt()-1
            dr2.color =(((relx * - rag + 0 ).toInt()- 16.0.pow(5.0).toInt()*15)*0.99999).toInt()
            for (j in 1..20){
                canvas.drawCircle((( men - i)/2*k + min )*1F/(16*16*16)*10*6, 2090F+j*1F-10F, 2F, dr)
                canvas.drawCircle(((i + men)/2*k + min )*1F/(16*16*16)*10*6, 2090F+j*1F-10F, 2F, dr2 )
            }
        }
        val m1 = 2*( (1 - 0.49) * - rag - 0 + 10).toInt()-1
        val m2 = ((2*(0.51 * - rag + 0 ).toInt()- 16.0.pow(5.0).toInt()*15)*0.99999-20).toInt()
        val dr = Paint()
        val dr2 = Paint()
        val dr3 = Paint()
        dr.color =(m1+m2)/2
        dr2.color = Color.BLACK
        dr3.color = Color.WHITE
        for (j in 1..20){
            canvas.drawCircle(( ( men - 0.50f*men)*k + min )*1F/(16*16*16)*10*6, 2090F+j*1F-10F, 2F, dr)
            canvas.drawCircle(((men + men)*k + min )*1F/(16*16*16)*10*6, 2090F+j*1F-10F, 2F, dr2 )
            canvas.drawCircle(( (0)/2*k + min )*1F/(16*16*16)*10*6, 2090F+j*1F-10F, 2F, dr3)
        }

    }


    //***************************   respond to touch interaction   **************************************************
    override fun onTouchEvent(event: MotionEvent): Boolean {
        canvasPaint.color = paintColor
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setPencil()
                drawPath.moveTo(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath.lineTo(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
                drawPath.lineTo(touchX, touchY)
                drawCanvas.drawPath(drawPath, drawPaint)
            }

            else -> return false
        }
        //redraw
        invalidate()
        return true
    }


}