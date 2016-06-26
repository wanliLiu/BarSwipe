package com.barswipe.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.barswipe.R;

/**
 * Created by soli on 6/26/16.
 */
public class taiheView extends View {

    private BitmapDrawable mHoverView;
    private Rect mHoverViewBounds;

    public taiheView(Context context) {
        this(context, null);
    }

    public taiheView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public taiheView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int roundWidth;

    private Paint paint;
    private final Paint maskPaint = new Paint();

    public void init() {
        // 创建画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#609BFF"));// 设置红色
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        maskPaint.setAntiAlias(true);
        maskPaint.setFilterBitmap(false);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mHoverView = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.xulaoshi));
        mHoverViewBounds = new Rect(0, 0, mHoverView.getIntrinsicWidth(), mHoverView.getIntrinsicHeight());
        mHoverView.setBounds(mHoverViewBounds);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int hed = (int) ((width * 125.0f) / 100.0f);
        setMeasuredDimension(width, hed);
    }

    public void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                          int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (mHoverView != null) {
//            mHoverView.draw(canvas);
//        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.xulaoshi);
//        canvas.drawBitmap(bitmap,(int)(getMeasuredWidth() / 2),(int)(getMeasuredHeight() - (getMeasuredWidth() / 2)),null);

        drawImage(canvas, bitmap, 0, 0, getMeasuredWidth(), getMeasuredHeight(), 0, 0);

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() - (getMeasuredWidth() / 2), getMeasuredWidth() / 2, paint);
    }
}
