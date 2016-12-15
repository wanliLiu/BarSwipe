package com.barswipe.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Soli on 2016/12/15.
 */

public class CycleView extends View{

    private int width ,height = 0;
    private Paint paint;

    public CycleView(Context context) {
        this(context, null);
    }

    public CycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        // 创建画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#609BFF"));// 设置红色
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getMeasuredWidth() / 2;
        float cy = getMeasuredHeight() + getMeasuredHeight() / 3;
        float radius = (getMeasuredHeight() * 2) / 3;
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
