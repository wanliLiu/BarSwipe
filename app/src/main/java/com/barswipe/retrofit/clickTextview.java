package com.barswipe.retrofit;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        setMovementMethod(LinkMovementMethod.getInstance());
        append("开始到好似看电视了贷款 ");

        SpannableString sp = new SpannableString("@许嵩");
        sp.setSpan(new clickSpan(), 0, "@许嵩".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        append(sp);

        append("有开始了哦 ，不是慢");
    }

    /**
     * @author milanoouser
     */
    private class clickSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            Toast.makeText(getContext(), "点击我干嘛", Toast.LENGTH_LONG).show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
