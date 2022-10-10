package com.im.sdk.ui.view.message

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.im.sdk.R

open class MessageContentView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private val LEFT = 0
    private val RIGHT = 1
    private val TOP = 2
    private val BOTTOM = 3

    private var mWidth = 0
    private var mHeight = 0

    private lateinit var mRect: Rect
    private lateinit var mRectF: RectF
    private lateinit var mPaint: Paint
    private lateinit var mPaintBorder: Paint

    // Triangular path
    private lateinit var mPathTrg: Path
    private var mGravity = 0
    private var mColorBg = 0
    private var mRectRadius = 0f
    private var mRectRadiusTop = 0f

    // Triangle left offset
    private var mOffset = 0f

    // Triangle bottom length/2
    private var mTrgHalfWidth = 0f

    // Triangle heigh
    private var mTrgHeight = 0f

    private val paint = Paint()
    private val linePaint = Paint()
    private val newPath = Path()

    private var showBottom = false

    private var bottomOffset = 0f

    fun setMessageBackgroundColor(
        color: Int,
        bottomColor: Boolean = true,
        borderColor: Int? = null
    ) {
        mColorBg = color
        mPaint.color = mColorBg
        mPaintBorder.color = borderColor ?: mColorBg
        if (bottomColor) {
            linePaint.color = ContextCompat.getColor(context, R.color.color_ECEDF2_4E5670)
            paint.color = mColorBg
        }
        requestLayout()
    }

    fun showBottom(showBottom: Boolean) {
        this.showBottom = showBottom
        invalidate()
    }

    fun setMessageBottomOffset(offset: Float){
        bottomOffset = offset
    }

    fun setMessageGravity(gravity: Int){
        mGravity = gravity
    }

    fun setMessageLayoutRadius(radius: Float){
        mRectRadius = radius
        mRectRadiusTop = radius
    }


    init {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.MessageContentView, defStyleAttr, 0)
        try {

            showBottom = array.getBoolean(R.styleable.MessageContentView_message_show_bottom, false)
            bottomOffset =
                array.getDimension(
                    R.styleable.MessageContentView_message_bottom_offset,
//                    80f.dpToPx().toFloat()
                    ConvertUtils.dp2px(80f).toFloat()
                )
            mGravity = array.getInteger(R.styleable.MessageContentView_message_gravity, TOP);
            mColorBg = array.getColor(
                R.styleable.MessageContentView_message_color,
                Color.WHITE
            );
            mRectRadius = array.getDimension(
                R.styleable.MessageContentView_message_radius,
//                10f.dpToPx().toFloat()
                ConvertUtils.dp2px(10f).toFloat()
            );
            mRectRadiusTop = array.getDimension(
                R.styleable.MessageContentView_message_radius,
//                10f.dpToPx().toFloat()
                ConvertUtils.dp2px(10f).toFloat()
            );
            mOffset = array.getDimension(
                R.styleable.MessageContentView_message_offset,
//                8f.dpToPx().toFloat()
                ConvertUtils.dp2px(8f).toFloat()
            );
            mTrgHalfWidth = array.getDimension(
                R.styleable.MessageContentView_message_trgWidth,
//                10f.dpToPx().toFloat()
                ConvertUtils.dp2px(10f).toFloat()
            ) / 2;
            mTrgHeight = array.getDimension(
                R.styleable.MessageContentView_message_trgHeight,
//                7f.dpToPx().toFloat()
                ConvertUtils.dp2px(7f).toFloat()
            );

        } finally {
            array.recycle()
        }

        mPathTrg = Path()
        mRect = Rect()
        mRectF = RectF()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.color = mColorBg
        mPaint.isAntiAlias = true

        mPaintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBorder.style = Paint.Style.STROKE
        mPaintBorder.color = mColorBg
        mPaintBorder.strokeWidth = ConvertUtils.dp2px(0.5f).toFloat()
        mPaintBorder.isAntiAlias = true

        paint.color = Color.WHITE
        linePaint.color = ContextCompat.getColor(context, R.color.bg_ECEDF2)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = ConvertUtils.dp2px(0.5f).toFloat()
        linePaint.isAntiAlias = true
        linePaint.isDither = true

        setWillNotDraw(false)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setWillNotDraw(false)
    }

    private val radiiLeft = floatArrayOf(
//        0f,
        mRectRadiusTop,
//        mRectRadius,
        mRectRadiusTop,
        mRectRadius,
        mRectRadius,
        mRectRadius,
        mRectRadius,
        mRectRadius,
        mRectRadius
    )

    private val radiiRight = floatArrayOf(
        mRectRadius,
        mRectRadius,
//        0f,
        mRectRadiusTop,
//        mRectRadius,
        mRectRadiusTop,
        mRectRadius,
        mRectRadius,
        mRectRadius,
        mRectRadius
    )

    private val radii = floatArrayOf(
        0f,
        0f,
        0f,
        0f,
        mRectRadius,
        mRectRadius,
        mRectRadius,
        mRectRadius
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mWidth = width
        mHeight = height

        when (mGravity) {
            LEFT -> {
                mPathTrg.reset()
                mRect[0, 0, mWidth] = mHeight
                mRectF.set(mRect)
                mPathTrg.addRoundRect(mRectF, radiiLeft, Path.Direction.CCW)

//                mPathTrg.moveTo(mTrgHeight + mRectRadius, 0f)
//                mPathTrg.lineTo(0f, 0f)
//                mPathTrg.lineTo(mTrgHeight, mOffset)
//                mPathTrg.close()

                canvas.drawPath(mPathTrg, mPaint)
//                canvas.drawPath(mPathTrg, mPaintBorder)

                drawBottomText(canvas, mGravity)

            }
            RIGHT -> {
                mPathTrg.reset()
                mRect[0, 0, mWidth] = mHeight
                mRectF.set(mRect)
                mPathTrg.addRoundRect(mRectF, radiiRight, Path.Direction.CCW)

//                mPathTrg.moveTo(mWidth - mTrgHeight, mOffset)
//                mPathTrg.lineTo(mWidth.toFloat(), 0f)
//                mPathTrg.lineTo(mWidth - mTrgHeight - mRectRadius, 0f)
//                mPathTrg.close()

                canvas.drawPath(mPathTrg, mPaint)
//                canvas.drawPath(mPathTrg, mPaintBorder)
                drawBottomText(canvas, mGravity)
            }
        }
    }

    private fun drawBottomText(canvas: Canvas, gravity: Int) {

        if (!showBottom) {
            return
        }

        val toTop = (mHeight - bottomOffset)

        val startX = if (gravity == LEFT) mTrgHeight else 0f
        val stopX =
            if (gravity == LEFT) mWidth.toFloat() + mTrgHeight else mWidth.toFloat() - mTrgHeight

        canvas.drawLine(0F, toTop, stopX.toFloat(), toTop, linePaint)

        newPath.addRoundRect(
            mRectF.left,
            toTop,
            mRectF.right,
            mRectF.bottom,
            radii,
            Path.Direction.CCW
        )
        canvas.drawPath(newPath, paint)
    }
//
//    override fun addView(child: View?) {
//        child?.setBackgroundResource(R.color.white)
//        child?.alpha=0.6F
//        super.addView(child)
//    }
}