package com.shuyu.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.BaseRecorder;

import java.util.ArrayList;


/**
 * 声音波形的view
 * Created by shuyu on 2016/11/15.
 */

public class AudioWaveView extends View {


    public static final String MAX = "max_volume"; //map中的key
    public static final String MIN = "min_volume";//map中的key

    final protected Object mLock = new Object();

    private Context mContext;

    private Bitmap mBitmap, mBackgroundBitmap;

    private Paint mPaint;

    private Paint mViewPaint;

    private Canvas mCanvas = new Canvas();

    private Canvas mBackCanVans = new Canvas();

    private ArrayList<Short> mRecDataList = new ArrayList<>();

    private drawThread mInnerThread;

    private BaseRecorder mBaseRecorder;

    private int mWidthSpecSize;
    private int mHeightSpecSize;
    private int mScale = 1;
    private int mBaseLine;

    private int mOffset = -11;//波形之间线与线的间隔

    private boolean mAlphaByVolume; //是否更具声音大小显示清晰度

    private boolean mIsDraw = true;

    private int mWaveCount = 2;

    private int mWaveColor = Color.WHITE;

    private int mColorPoint = 1;

    private int mPreFFtCurrentFrequency;

    private int mColorChangeFlag;

    private int mColor1 = Color.argb(0xfa, 0x6f, 0xff, 0x81);

    private int mColor2 = Color.argb(0xfa, 0xff, 0xff, 0xff);

    private int mColor3 = Color.argb(0xfa, 0x42, 0xff, 0xff);


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AudioWaveView.this.invalidate();
        }
    };

    public AudioWaveView(Context context) {
        super(context);
        init(context, null);
    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AudioWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsDraw = false;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
            mBackgroundBitmap.recycle();
        }
    }

    public void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (isInEditMode())
            return;

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.waveView);
            mOffset = ta.getInt(R.styleable.waveView_waveOffset, dip2px(context, -11));
            mWaveColor = ta.getColor(R.styleable.waveView_waveColor, Color.WHITE);
            mWaveCount = ta.getInt(R.styleable.waveView_waveCount, 2);
            ta.recycle();
        }

        if (mOffset == dip2px(context, -11)) {
            mOffset = dip2px(context, 1);
        }

        if (mWaveCount < 1) {
            mWaveCount = 1;
        } else if (mWaveCount > 2) {
            mWaveCount = 2;
        }

        mPaint = new Paint();
        mViewPaint = new Paint();
        mPaint.setColor(mWaveColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && mBackgroundBitmap == null) {
            ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (getWidth() > 0 && getHeight() > 0) {
                        mWidthSpecSize = getWidth();
                        mHeightSpecSize = getHeight();
                        mBaseLine = mHeightSpecSize / 2;
                        mBackgroundBitmap = Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
                        mBitmap = Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
                        mBackCanVans.setBitmap(mBackgroundBitmap);
                        mCanvas.setBitmap(mBitmap);
                        ViewTreeObserver vto = getViewTreeObserver();
                        vto.removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }


    //内部类的线程
    class drawThread extends Thread {
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            while (mIsDraw) {
                ArrayList<Short> dataList = new ArrayList<>();
                synchronized (mRecDataList) {
                    if (mRecDataList.size() != 0) {
                        dataList = (ArrayList<Short>) mRecDataList.clone();// 保存  接收数据
                    }
                }
                if (mBackgroundBitmap == null) {
                    continue;
                }
                resolveToWaveData(dataList);
                if (dataList.size() > 0) {
                    updateColor();
                }
                if (mBackCanVans != null) {
                    mBackCanVans.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mBackCanVans.drawLine(0, mBaseLine, mWidthSpecSize, mBaseLine, mPaint);
                    int drawBufsize = dataList.size();
                    /*判断大小，是否改变显示的比例*/
                    for (int i = 0, j = 0; i < drawBufsize; i++, j += mOffset) {
                        Short sh = dataList.get(i);
                        if (sh != null) {
                            short max = (short) (mBaseLine - sh / mScale);
                            short min;
                            if (mWaveCount == 2) {
                                min = (short) (sh / mScale + mBaseLine);
                            } else {
                                min = (short) (mBaseLine);
                            }
                            mBackCanVans.drawLine(j, mBaseLine, j, max, mPaint);
                            mBackCanVans.drawLine(j, min, j, mBaseLine, mPaint);
                        }

                    }
                    synchronized (mLock) {
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mPaint);
                    }

                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
                //休眠暂停资源
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        if (mIsDraw && mBitmap != null) {
            synchronized (mLock) {
                c.drawBitmap(mBitmap, 0, 0, mViewPaint);
            }
        }
    }


    /**
     * 更具当前块数据来判断缩放音频显示的比例
     *
     * @param list 音频数据
     */
    private void resolveToWaveData(ArrayList<Short> list) {
        short allMax = 0;
        for (int i = 0; i < list.size(); i++) {
            Short sh = list.get(i);
            if (sh != null && sh > allMax) {
                allMax = sh;
            }
        }
        int curScale = allMax / mBaseLine;
        if (curScale > mScale) {
            mScale = ((curScale == 0) ? 1 : curScale);
        }
    }

    /**
     * 开始绘制
     */
    public void startView() {
        if (mInnerThread != null && mInnerThread.isAlive()) {
            mIsDraw = false;
            while (mInnerThread.isAlive()) ;
            mBackCanVans.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        mIsDraw = true;
        mInnerThread = new drawThread();
        mInnerThread.start();
    }

    /**
     * 停止绘制
     */
    public void stopView() {
        mIsDraw = false;
        mRecDataList.clear();
        if (mInnerThread != null) {
            while (mInnerThread.isAlive()) ;
        }
        mBackCanVans.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private void updateColor() {

        if (mBaseRecorder == null)
            return;

        //获取音量大小
        int volume = mBaseRecorder.getRealVolume();
        //Log.e("volume ", "volume " + volume);

        //缩减过滤掉小数据
        int scale = (volume / 100);

        //是否大于给定阈值
        if (scale < 5) {
            mPreFFtCurrentFrequency = scale;
            return;
        }

        //这个数据和上个数据之间的比例
        int fftScale = 0;
        if (mPreFFtCurrentFrequency != 0) {
            fftScale = scale / mPreFFtCurrentFrequency;
        }

        //如果连续几个或者大了好多就可以改变颜色
        if (mColorChangeFlag == 4 || fftScale > 10) {
            mColorChangeFlag = 0;
        }

        if (mColorChangeFlag == 0) {
            if (mColorPoint == 1) {
                mColorPoint = 2;
            } else if (mColorPoint == 2) {
                mColorPoint = 3;
            } else if (mColorPoint == 3) {
                mColorPoint = 1;
            }
            int color;
            if (mColorPoint == 1) {
                color = Color.argb((mAlphaByVolume) ? 50 * scale : 0xff, Color.red(mColor1), Color.green(mColor1), Color.blue(mColor1));
            } else if (mColorPoint == 2) {
                color = Color.argb((mAlphaByVolume) ? 50 * scale : 0xff, Color.red(mColor2), Color.green(mColor2), Color.blue(mColor2));
            } else {
                color = Color.argb((mAlphaByVolume) ? 50 * scale : 0xff, Color.red(mColor3), Color.green(mColor3), Color.blue(mColor3));
            }
            mPaint.setColor(color);
        }
        mColorChangeFlag++;
        //保存数据
        if (scale != 0)
            mPreFFtCurrentFrequency = scale;
    }

    /**
     * 三种颜色,不设置用默认的
     */
    public void setChangeColor(int color1, int color2, int color3) {
        this.mColor1 = color1;
        this.mColor2 = color2;
        this.mColor3 = color3;
    }


    public boolean isAlphaByVolume() {
        return mAlphaByVolume;
    }

    /**
     * 是否更具声音大小显示清晰度
     */
    public void setAlphaByVolume(boolean alphaByVolume) {
        this.mAlphaByVolume = alphaByVolume;
    }

    /**
     * 设置好偶波形会变色
     */
    public void setBaseRecorder(BaseRecorder baseRecorder) {
        mBaseRecorder = baseRecorder;
    }


    /**
     * 将这个list传到Record线程里，对其不断的填充
     * <p>
     * Map存有两个key，一个对应AudioWaveView的MAX这个key,一个对应AudioWaveView的MIN这个key
     *
     * @return 返回的是一个map的list
     */
    public ArrayList<Short> getRecList() {
        return mRecDataList;
    }

    /**
     * 设置线与线之间的偏移
     *
     * @param offset 偏移值 pix
     */
    public void setOffset(int offset) {
        this.mOffset = offset;
    }


    public int getWaveColor() {
        return mWaveColor;
    }

    /**
     * 设置波形颜色
     *
     * @param waveColor 音频颜色
     */
    public void setWaveColor(int waveColor) {
        this.mWaveColor = waveColor;
    }

    /**
     * 设置波形颜色
     *
     * @param waveCount 波形数量 1或者2
     */
    public void setWaveCount(int waveCount) {
        mWaveCount = waveCount;
        if (mWaveCount < 1) {
            mWaveCount = 1;
        } else if (mWaveCount > 2) {
            mWaveCount = 2;
        }
    }

    /**
     * dip转为PX
     */
    private int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }
}
