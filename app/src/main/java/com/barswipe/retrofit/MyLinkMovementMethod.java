package com.barswipe.retrofit;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * TODO: 2017/9/7 此处需要输入描述文字
 *
 * @author Soli
 * @Time 2017/9/7
 */
public class MyLinkMovementMethod extends BaseMovementMethod {

    private static MyLinkMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new MyLinkMovementMethod();

        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
                ViewGroup parent = (ViewGroup) widget.getParent();
                if (parent != null) {
                    parent.onTouchEvent(event);
                }
            }
        }

        return false;
    }
}
