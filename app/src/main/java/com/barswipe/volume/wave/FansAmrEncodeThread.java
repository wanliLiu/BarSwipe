package com.barswipe.volume.wave;

import android.media.AmrInputStream;

import com.barswipe.Myapplication;
import com.barswipe.util.FileUtil;
import com.barswipe.volume.pcm.sound.BytesTransUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.LinkedList;

import io.kvh.media.amr.AmrEncoder;

public class FansAmrEncodeThread extends Thread {

    private onEncodeCompleteListener listener;

    private byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};
    private int mNumSamples;  // Number of samples per channel.
    private FileOutputStream mFileOutputStream;
    private String filePath;
    private int mChannels, playStart, playEnd, bufferSize = 160;
    private ShortBuffer mPcmBytes;

    //是否用系统的amr编码
    private boolean isUseSystemAmrEncode = true;

    /**
     * 保存数据的时候干脆一起把波形数据也处理了
     */
    private LinkedList<String> waveData;

    /**
     * @param pcmBytes    pcm音频流数据
     * @param numSamples  需要编码的数据
     * @param file        保存的文件
     * @param channels
     * @param mPlayStart  开始位置
     * @param mPlayEnd    结束位置
     * @param tempWateDat 波形数据
     * @param mlistener   回调接口
     * @throws FileNotFoundException
     */
    public FansAmrEncodeThread(ShortBuffer pcmBytes, int numSamples, File file, int channels, int mPlayStart, int mPlayEnd, LinkedList<String> tempWateDat, onEncodeCompleteListener mlistener) throws FileNotFoundException {
        super("DataEncodeThread");
        mPcmBytes = pcmBytes;
        mNumSamples = numSamples;
        mChannels = channels;
        playStart = mPlayStart;
        playEnd = mPlayEnd;
        this.mFileOutputStream = new FileOutputStream(file);
        filePath = file.getAbsolutePath();

        waveData = tempWateDat;

        if (!isUseSystemAmrEncode)
            AmrEncoder.init(0);
        listener = mlistener;
    }


    @Override
    public void run() {
        super.run();

        if (isUseSystemAmrEncode) {
            useSystemAmrEncode();
        } else {
            useThirdAmrEncode();
        }
    }

    /**
     * 用系统的amr编码
     */
    private void useSystemAmrEncode() {

        try {
            //把pcm保存为文件
            File tempfile = FileUtil.getFile(Myapplication.getApp(), "temp", "temp.pcm");

            FileOutputStream outputStream = new FileOutputStream(tempfile);

            short[] mBuffer = new short[4096];
            int len = 0;
            int startIndex = playStart * mChannels;
            int endIndex = playEnd * mChannels;
            mPcmBytes.position(startIndex);
            int limit = endIndex == 0 ? mNumSamples * mChannels : endIndex;
            while (mPcmBytes.position() < limit) {
                int numSamplesLeft = limit - mPcmBytes.position();
                if (numSamplesLeft >= mBuffer.length) {
                    mPcmBytes.get(mBuffer);
                    len = mBuffer.length;
                } else {
//                    for (int i = numSamplesLeft; i < mBuffer.length; i++) {
//                        mBuffer[i] = 0;
//                    }
                    mPcmBytes.get(mBuffer, 0, numSamplesLeft);
                    len = numSamplesLeft;
                }

                try {
                    outputStream.write(BytesTransUtil.getInstance().Shorts2Bytes(mBuffer, len), 0, len * 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            outputStream.close();

            //利用AmrInputStream  编码
            if (tempfile.exists()) {
                //pcm ->amr
                AmrInputStream ais = new AmrInputStream(new FileInputStream(tempfile));
                //写入amr头
                mFileOutputStream.write(header);
                byte[] temp = new byte[4096];
                while ((len = ais.read(temp)) > 0) {
                    mFileOutputStream.write(temp, 0, len);
                }
                mFileOutputStream.close();
                ais.close();

                //删除临时文件
                if (tempfile.exists())
                    tempfile.delete();

                if (listener != null) {
                    listener.onEncodeComplete(filePath, getWateDataStr());
                }

            } else
                throw new IllegalArgumentException("pcm 临时文件保存有问题，" + tempfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用第三方的amr编码
     */
    private void useThirdAmrEncode() {
        try {//写入amr头
            mFileOutputStream.write(header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        short[] mBuffer = new short[bufferSize];
        int startIndex = playStart * mChannels;
        int endIndex = playEnd * mChannels;
        mPcmBytes.position(startIndex);
        int limit = endIndex == 0 ? mNumSamples * mChannels : endIndex;
        while (mPcmBytes.position() < limit) {
            int numSamplesLeft = limit - mPcmBytes.position();
            if (numSamplesLeft >= mBuffer.length) {
                mPcmBytes.get(mBuffer);
            } else {
//                for (int i = numSamplesLeft; i < mBuffer.length; i++) {
//                    mBuffer[i] = 0;
//                }
                mPcmBytes.get(mBuffer, 0, numSamplesLeft);
            }
            byte[] encodedData = new byte[mBuffer.length];
            int encodedSize = AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(), mBuffer, encodedData);
            if (encodedSize > 0) {
                try {
                    mFileOutputStream.write(encodedData, 0, encodedSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        AmrEncoder.exit();
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (listener != null) {
            listener.onEncodeComplete(filePath, getWateDataStr());
        }
    }


    /**
     * 获取波形数据，方便后面处理
     *
     * @return
     */
    private String getWateDataStr() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < waveData.size(); i++) {
            buffer.append(waveData.get(i));
            buffer.append(",");
        }
        if (buffer.length() > 0)
            buffer.deleteCharAt(buffer.length() - 1);

        return buffer.toString();
    }

}
