package com.barswipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by SoLi on 2016/3/16.
 */
public class TextView45 extends TextView {

    private float offset;
    private int angle = 0;
    private int width;
    private int orignalHeight;
    private int triangleBackground;
    private int extra;

    public TextView45(Context context) {
        this(context,null);
    }

    public TextView45(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextView45(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    /**
     *
     * @param ctx
     * @param attrs
     */
    private void Init(Context ctx,AttributeSet attrs)
    {
        TypedArray type =ctx.obtainStyledAttributes(attrs,R.styleable.TextView45);
        angle = type.getInteger(R.styleable.TextView45_triangleangle, 0);
        triangleBackground = type.getColor(R.styleable.TextView45_trianglebackground, Color.BLUE);

        extra =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getContext().getResources().getDisplayMetrics());
//        setPadding(extra / 2,0,0,0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() + extra;
        orignalHeight = getMeasuredHeight();
//        offset = (float)Math.sqrt(Math.pow(width,2) / 2) ;
        offset =  width *1 / 2 ;
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackGroundTriangle(canvas);
        canvas.translate(-6,offset - 5);
        canvas.rotate(angle);
//        canvas.rotate(angle,getMeasuredWidth()/2,getMeasuredHeight()/2);
//        canvas.rotate(angle,width, width);
        super.onDraw(canvas);
    }

    /**
     *
     * @param canvas
     */
    private void drawBackGroundTriangle(Canvas canvas)
    {
        int save =  canvas.save();
        Paint p = new Paint();
        p.setColor(triangleBackground);

        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(width, 0);
        path.lineTo(0, width);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
        canvas.restoreToCount(save);
    }
}
