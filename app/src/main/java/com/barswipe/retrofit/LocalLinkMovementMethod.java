package com.barswipe.retrofit;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Soli on 2016/9/29.
 */

public class LocalLinkMovementMethod extends LinkMovementMethod {
    static LocalLinkMovementMethod sInstance;

    private ClickableSpan tempLick;
    /**
     * 点击效果默认无点击效果（透明点击效果）
     */
    private int clickColor = Color.TRANSPARENT;

    public static LocalLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new LocalLinkMovementMethod();

        return sInstance;
    }

    /**
     * @param color
     */
    public void setClickColor(int color) {
        clickColor = color;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN ) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            // 返回textView的偏移量，如textView设置了padding＝3px，则返回3
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            // 左侧超出屏幕的偏移量，如一个view可左右滑动，由于此view比较大，左侧的view被挡住了，被挡住的偏移量就是getScrollX()，同理getScrollY()
            x += widget.getScrollX();
            y += widget.getScrollY();

            //上述的目的是定位出点击的位置  在整个view组件的绝对坐标（不是屏幕的相对坐标）

            Layout layout = widget.getLayout();
            // 获取点击位置的 text的行数
            int line = layout.getLineForVertical(y);
            // 获取点击位置的偏移量
            int off = layout.getOffsetForHorizontal(line, x);

            //画外题，上述两个参数是不是很像一个textView的书签？

            //通过偏移量来获取span （具体是通过偏移量对应的span来确定）
            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                tempLick = link[0];
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                    setSelect(false,buffer);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    setSelect(true,buffer);
                }
                return true;
            } else {
                setSelect(false,buffer);
                Selection.removeSelection(buffer);
                Touch.onTouchEvent(widget, buffer, event);
                return false;
            }
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }

    /**
     *
     * @param isSelect
     * @param buffer
     */
    private void setSelect(boolean isSelect,Spannable buffer)
    {
        if (tempLick != null)
        {
            buffer.setSpan(new BackgroundColorSpan(isSelect ? clickColor : Color.TRANSPARENT), buffer.getSpanStart(tempLick),
                    buffer.getSpanEnd(tempLick), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
