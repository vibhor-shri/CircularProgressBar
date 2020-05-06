package com.vibhor.android.circularprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressIndicator extends View {

    private ProgressEventListener mProgressEventListener;

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mProgress;
    private int mCenter;
    private int mRadius;
    private int style;
    private int result = 0;
    private float padding = 0;
    private int ringColor;
    private int ringProgressColor;
    private float ringWidth;
    private int max;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public CircularProgressIndicator(Context context) {
        this(context, null);
    }

    public CircularProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularProgressIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        result = dp2px(100);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressIndicator);
        ringColor = mTypedArray.getColor(R.styleable.CircularProgressIndicator_ringColor, Color.LTGRAY);
        ringProgressColor = mTypedArray.getColor(R.styleable.CircularProgressIndicator_ringProgressColor,
                Color.WHITE);
        ringWidth = mTypedArray.getDimension(R.styleable.CircularProgressIndicator_ringWidth, 2);
        max = mTypedArray.getInteger(R.styleable.CircularProgressIndicator_max, 100);
        style = mTypedArray.getInt(R.styleable.CircularProgressIndicator_style, 0);
        mProgress = mTypedArray.getInteger(R.styleable.CircularProgressIndicator_progress, 0);
        padding = mTypedArray.getDimension(R.styleable.CircularProgressIndicator_ringPadding, 2);

        mTypedArray.recycle();
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCenter = getWidth() / 2;
        mRadius = (int) (mCenter - ringWidth / 2);
        drawCircle(canvas);
        drawTextContent(canvas);
        drawProgress(canvas);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setColor(ringColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mCenter, mCenter, mRadius, mPaint);
    }

    private void drawTextContent(Canvas canvas) {
        mPaint.setStrokeWidth(0);
        mPaint.setTypeface(Typeface.DEFAULT);
        int percent = (int) (((float) mProgress / (float) max) * 100);
    }


    private void drawProgress(Canvas canvas) {
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(ringProgressColor);

        RectF strokeOval = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius,
                mCenter + mRadius);
        RectF fillOval = new RectF(mCenter - mRadius + ringWidth + padding,
                mCenter - mRadius + ringWidth + padding, mCenter + mRadius - ringWidth - padding,
                mCenter + mRadius - ringWidth - padding);

        switch (style) {
            case STROKE: {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawArc(strokeOval, -90, 360 * mProgress / max, false, mPaint);
                break;
            }
            case FILL: {
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                if (mProgress != 0) {
                    canvas.drawArc(fillOval, -90, 360 * mProgress / max, true, mPaint);
                }
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = result;
        } else {
            mWidth = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = result;
        } else {
            mHeight = heightSize;
        }

        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    public synchronized void setmProgress(int mProgress) {
        if (mProgress < 0) {
            throw new IllegalArgumentException("The progress of 0");
        }
        if (mProgress > max) {
            mProgress = max;
        }
        if (mProgress <= max) {
            this.mProgress = mProgress;
            postInvalidate();
        }
        if (mProgress == max) {
            if (mProgressEventListener != null) {
                mProgressEventListener.progressComplete();
            }
        }
    }

    public int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public interface ProgressEventListener {
        void progressComplete();
    }

    public void setOnProgressListener(ProgressEventListener mProgressEventListener) {
        this.mProgressEventListener = mProgressEventListener;
    }
}
