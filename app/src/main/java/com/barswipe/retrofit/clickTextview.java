package com.barswipe.retrofit;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;

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


    private void init() {

        int unicodeJoy = 0x1F602;
        String emojiString = getEmojiStringByUnicode(unicodeJoy);
        setText(emojiString);

        //这句话很重要
        LocalLinkMovementMethod method = LocalLinkMovementMethod.getInstance();
        method.setClickColor(getResources().getColor(R.color.colorPrimary_press));
        setMovementMethod(method);
//        setMovementMethod(LinkMovementMethod.getInstance());
        append("开始到好似看电视了贷款 ");

        SpannableString sp = new SpannableString("@许嵩");
        sp.setSpan(new clickSpan("@许嵩"), 0, "@许嵩".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
        append(sp);

        append("有开始了哦 sssssssssssssssssssss不是慢");

        String tes = "@刘万里 我也iwelkelwke了看完饿了";
        SpannableString s23p = new SpannableString(tes);
        s23p.setSpan(new clickSpan(tes), 0, tes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_INCLUSIVE SPAN_EXCLUSIVE_EXCLUSIVE
        append(s23p);
        append("  内容开始了哦");
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