package com.barswipe.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Soli on 2016/9/27.
 */

public class clickTextview extends TextView implements View.OnTouchListener {

    public clickTextview(Context context) {
        super(context);
        init();
    }

    public clickTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public clickTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private String getEmojiStringByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    /**
     * @param url
     * @return
     */
    private SpannableString getUrlSpan(String url) {
        SpannableString sp = new SpannableString("网页链接");
        sp.setSpan(new clickSpan(url), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /**
     *
     */
    private SpannableStringBuilder findUrlString() {
        SpannableStringBuilder builder = new SpannableStringBuilder("");

        String rule = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
        String str = "https://www.baidu.com/是代理十多块是代理十多块http://www.jianshu.com/p/4276163968c8是的是理十多块是代理十多块是的是的https://github.com/打开十多块www.baidu.com熟练度代理十多块是代理十多块是代理十多块是代理十多块是代理十多块是代理十多";

        Matcher matcher = Pattern.compile(rule).matcher(str);
        List<String> urlList = new ArrayList<>();
        while (matcher.find()) {
            urlList.add(matcher.group());
        }

        if (urlList.isEmpty()) {
            builder.append(str);
            return builder;
        }


        for (int i = 0; i < urlList.size(); i++) {
            String temp = urlList.get(i);
            int urlIndex = str.indexOf(temp);
            if (i == 0 && urlIndex == 0) {
                builder.append(getUrlSpan(temp));
            } else {
                builder.append(str.substring(0, urlIndex));
                builder.append(getUrlSpan(temp));
            }
            str = str.substring(urlIndex + temp.length());
        }

        builder.append(str);

        return builder;
    }

    /**
     *
     */
    private void init() {

//        setHighlightColor(getContext().getResources().getColor(R.color.red_press));
//        setOnTouchListener(this);

        SpannableStringBuilder builder = findUrlString();

        int unicodeJoy = 0x1F602;
        String emojiString = getEmojiStringByUnicode(unicodeJoy);
        builder.append(emojiString);

//        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));

        //这句话很重要
        /**用自定义这个 点击图片友按压效果没有**/
//        LocalLinkMovementMethod method = LocalLinkMovementMethod.getInstance();
//        method.setClickColor(getResources().getColor(R.color.colorPrimary_press));
//        setMovementMethod(method);
        /**用默认 点击图片友按压效果**/
        setMovementMethod(MyLinkMovementMethod.getInstance());
//        setFocusable(true);
//        setClickable(true);
//        setOnLongClickListener(view -> true);
//        setLongClickable(true);
//        setBackgroundResource(R.drawable.default_view_press_selector);
        builder.append("开始到好似看电视了贷款 ");

        String temp = "   " + " " + "@许嵩";
        SpannableString sp = new SpannableString(temp);
        Drawable d = ContextCompat.getDrawable(getContext(), R.mipmap.icon_see_image);
        int pannding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, getResources().getDisplayMetrics());
        d.setBounds(0, 0 - pannding, d.getIntrinsicWidth() - pannding, d.getIntrinsicHeight() - pannding);
        ClickableImageSpan span = new ClickableImageSpan(d) {
            @Override
            public void onClick(View view) {
//                //点击图片
//                if (listener != null) {
//                    listener.OnClick(view);
//                }
            }
        };
        sp.setSpan(span, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//图片
        sp.setSpan(new clickSpan(temp), 0, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
        builder.append(sp);

        builder.append("有开始了哦 sssssssssssssssssssss不是慢");

        String tes = "@刘万里 我也iwelkelwke了看完饿了";
        SpannableString s23p = new SpannableString(tes);
        s23p.setSpan(new clickSpan(tes), 0, tes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
        builder.append(s23p);
        builder.append("  内容开始了哦 \n 内容开始了哦内容开始了哦内容开始了哦内容开始了哦内容开始了哦内容开始了哦");

        setText(builder);
    }

    /**
     * @param textView
     * @param spannable
     * @param event
     * @return
     */
    private clickSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        clickSpan[] link = spannable.getSpans(off, off, clickSpan.class);
        clickSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }

    /**
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CharSequence text = ((TextView) v).getText();
        Spannable buffer = Spannable.Factory.getInstance().newSpannable(text);
        TextView widget = (TextView) v;
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

            clickSpan[] link = buffer.getSpans(off, off, clickSpan.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].setPressed(false);
                    link[0].onClick(v);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    link[0].setPressed(true);
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return false;
    }


    /**
     * 默认的图片点击是没有事件的，这里我们自己添加事件
     */
    public static abstract class ClickableImageSpan extends ImageSpan {


        public ClickableImageSpan(Drawable b) {
            super(b);
        }

        public ClickableImageSpan(Context context, Bitmap b) {
            super(context, b);
        }

        public ClickableImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
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
            int transY = (y + fm.descent + y + fm.ascent) / 2
                    - b.getBounds().bottom / 2;

            canvas.save();
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();

        }

        public abstract void onClick(View view);
    }

    /**
     * @author milanoouser
     */
    private class clickSpan extends ClickableSpan {

        String attention = "";
        private TextPaint paint;
        private boolean mIsPressed;

        public clickSpan(String temp) {
            attention = temp;
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(getContext(), attention, Toast.LENGTH_SHORT).show();
        }

        /**
         * @param isSelected
         */
        public void setPressed(boolean isSelected) {
//            mIsPressed = isSelected;
//            if (paint != null)
//                paint.bgColor = mIsPressed ? 0xffFF3B00 : 0x00000000;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            paint = ds;
//            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.colorPrimary));
//            ds.bgColor = mIsPressed ? 0xffFF3B00 : 0x00000000;
            ds.setUnderlineText(false);
            Log.e("IsPressed", String.valueOf(mIsPressed));
        }
    }
}
