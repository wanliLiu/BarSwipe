package io.github.rockerhieu.emojicon.util;

import android.text.SpannableStringBuilder;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

/**
 * Created by lizhangfeng on 16/1/15.
 * 拼音帮助类
 */
public class PinYinUtil {


    /**
     * 获取第一个字符的首字母
     *
     * @param chines
     * @return
     */
    public static String getSelling(String chines) {
        SpannableStringBuilder sp = new SpannableStringBuilder("");
        char[] nameChar = chines.toCharArray();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        for (int i = 0; i < nameChar.length; i++)
        {
            char name = nameChar[i];

            if (name > 96 && name < 122) {//a-z的字母
                sp.append(name);
            } else if (name > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(name, defaultFormat);
                    sp.append(temp[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        return sp.toString();
    }
}
