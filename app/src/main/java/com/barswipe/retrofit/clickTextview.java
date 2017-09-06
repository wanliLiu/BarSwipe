package com.barswipe.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
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

public class clickTextview extends TextView {
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
     *
     */
    private void findUrlString() {
        String rule = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
        String str = "https://www.baidu.com/\n是代理十多块\nhttp://www.jianshu.com/p/4276163968c8是的是的是的https://github.com/打开十多块www.baidu.com熟练度";

        Matcher matcher = Pattern.compile(rule).matcher(str);
        List<String> urlList = new ArrayList<>();
        while (matcher.find()) {
            urlList.add(matcher.group());
        }

        if (urlList.isEmpty()) {
            setText(str);
            return;
        }

        for (int i = 0; i < urlList.size(); i++) {
            String temp = urlList.get(i);
            int urlIndex = str.indexOf(temp);
            if (i == 0 && urlIndex == 0) {
                SpannableString sp = new SpannableString("网页链接");
                sp.setSpan(new clickSpan(temp), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
                setText(sp);
            } else {
                String text = str.substring(0, urlIndex);
                if (i == 0) {
                    setText(text);
                } else {
                    append(text);
                }
                SpannableString sp = new SpannableString("网页链接");
                sp.setSpan(new clickSpan(temp), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
                append(sp);
            }
            str = str.substring(urlIndex + temp.length());
        }

        append(str);
    }

    /**
     *
     */
    private void init() {
        findUrlString();

        int unicodeJoy = 0x1F602;
        String emojiString = getEmojiStringByUnicode(unicodeJoy);
        append(emojiString);

        //这句话很重要
        /**用自定义这个 点击图片友按压效果没有**/
        LocalLinkMovementMethod method = LocalLinkMovementMethod.getInstance();
        method.setClickColor(getResources().getColor(R.color.colorPrimary_press));
        setMovementMethod(method);
        /**用默认 点击图片友按压效果**/
//        setMovementMethod(LinkMovementMethod.getInstance());
        append("开始到好似看电视了贷款 ");

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
        append(sp);

        append("有开始了哦 sssssssssssssssssssss不是慢");

        String tes = "@刘万里 我也iwelkelwke了看完饿了";
        SpannableString s23p = new SpannableString(tes);
        s23p.setSpan(new clickSpan(tes), 0, tes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
        append(s23p);
        append("  内容开始了哦");
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

        public clickSpan(String temp) {
            attention = temp;
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(getContext(), attention, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.colorPrimary));
            ds.setUnderlineText(false);
        }
    }
}
