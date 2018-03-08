package com.barswipe.retrofit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.barswipe.R;

/**
 * Created by Administrator on 2016/10/11.
 */

public class CustomTextView extends TextView {


//    private MyLinkMovementMethod mLinkMovementMethod;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     *
     */
    private void init() {
        setBackgroundResource(R.drawable.selector_transparent);
//
//        setOnLongClickListener(view -> true);

    }
//
//    @Override
//    protected MovementMethod getDefaultMovementMethod() {
//        return new MyLinkMovementMethod();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        CharSequence text = this.getText();
//        Spannable buffer = Spannable.Factory.getInstance().newSpannable(text);
//        int action = event.getAction();
//
//        if (action == MotionEvent.ACTION_UP ||
//                action == MotionEvent.ACTION_DOWN) {
//            int x = (int) event.getX();
//            int y = (int) event.getY();
//
//            x -= this.getTotalPaddingLeft();
//            y -= this.getTotalPaddingTop();
//
//            x += this.getScrollX();
//            y += this.getScrollY();
//
//            Layout layout = this.getLayout();
//            int line = layout.getLineForVertical(y);
//            int off = layout.getOffsetForHorizontal(line, x);
//
//            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
//            if (link.length != 0) {
//                if (action == MotionEvent.ACTION_UP) {
//                    link[0].onClick(this);
//                } else if (action == MotionEvent.ACTION_DOWN) {
//                    Selection.setSelection(buffer,
//                            buffer.getSpanStart(link[0]),
//                            buffer.getSpanEnd(link[0]));
//                }
//
//                return true;
//            } else {
//                Selection.removeSelection(buffer);
//            }
//        }

        return super.onTouchEvent(event);
    }

    /**
     *
     */
    public void setMovementMethod() {
//        LocalLinkMovementMethod method = LocalLinkMovementMethod.getInstance();
//        method.setClickColor(ContextCompat.getColor(getContext(), R.color.transparent));

        setMovementMethod(new MyLinkMovementMethod());

//        setFocusable(true);
//        setClickable(true);
//        setLongClickable(true);

//        mLinkMovementMethod = new MyLinkMovementMethod();
//        mLinkMovementMethod.setClickBgColor(ContextCompat.getColor(getContext(), R.color.spannable_bg));
//        setMovementMethod(mLinkMovementMethod);

//        setOnTouchListener((v, event) -> {
//            CharSequence text = ((TextView) v).getText();
//            Spannable buffer = Spannable.Factory.getInstance().newSpannable(text);
//            TextView widget = (TextView) v;
//            int action = event.getAction();
//
//            if (action == MotionEvent.ACTION_UP ||
//                    action == MotionEvent.ACTION_DOWN) {
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//
//                x -= widget.getTotalPaddingLeft();
//                y -= widget.getTotalPaddingTop();
//
//                x += widget.getScrollX();
//                y += widget.getScrollY();
//
//                Layout layout = widget.getLayout();
//                int line = layout.getLineForVertical(y);
//                int off = layout.getOffsetForHorizontal(line, x);
//
//                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
//                if (link.length != 0) {
//                    if (action == MotionEvent.ACTION_UP) {
//                        link[0].onClick(v);
//                    } else if (action == MotionEvent.ACTION_DOWN) {
//                        Selection.setSelection(buffer,
//                                buffer.getSpanStart(link[0]),
//                                buffer.getSpanEnd(link[0]));
//                    }
//
//                    return true;
//                } else {
//                    Selection.removeSelection(buffer);
//                }
//            }
//            return false;
//        });
    }


    /**
     * 给textview添加 查看原图 Spannable
     */
    public void addShowOriginalPicture(final OnClickTailListener listener) {
        addTail(R.mipmap.icon_see_image, "查看图片", listener);
    }


    /**
     * 给textview添加 音频文件 Spannable
     */
    public void addShowMusic(final OnClickTailListener listener) {
        addTail("[音频文件]", listener);
    }

    /**
     * @param text
     * @param listener
     */
    private void addTail(String text, final OnClickTailListener listener) {
        addTail(-1, text, listener);
    }


    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 给textview添加角标
     *
     * @param image 为-1时，表示无图片
     */
    private void addTail(int image, String text, final OnClickTailListener listener) {

        setMovementMethod();

        SpannableStringBuilder builder = new SpannableStringBuilder("");

        String str;
        if (image == -1) {
            str = " " + text;
        } else {
            str = "   " + " " + text;
        }
        SpannableString spanString = new SpannableString(str);

        if (image == -1) {

        } else {
            Drawable d = ContextCompat.getDrawable(getContext(), image);
            int pannding = dip2px(getContext(), 1);
            d.setBounds(0, 0 - pannding, d.getIntrinsicWidth() - pannding, d.getIntrinsicHeight() - pannding);
            spanString.setSpan(new CustomImageSpan(d), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    listener.OnClick(widget);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                ds.setUnderlineText(false);
            }

        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE

        builder.append("卡说的收到了上刊登");
        builder.append(spanString);
        builder.append("我说的赛多利斯的上刊登圣诞快乐是的圣诞快乐熟练度开始都是的是多少打开是多少的是的是的收到是的是的是多少的 ");

        setText(builder);

    }

    /**
     * 当尾标点击
     */
    public interface OnClickTailListener {
        void OnClick(View view);
    }

    public class CustomImageSpan extends ImageSpan {

        public CustomImageSpan(Drawable d) {
            super(d);
        }

        /**
         * 让图片居中 start
         */
        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            // font metrics of text to be replaced
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
            canvas.save();
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }
}