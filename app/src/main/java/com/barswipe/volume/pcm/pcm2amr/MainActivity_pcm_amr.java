package com.barswipe.volume.pcm.pcm2amr;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.volume.pcm.AmrFileDecoder;
import com.barswipe.volume.pcm.KCacheUtils;
import com.barswipe.volume.pcm.sound.SoundMan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import io.kvh.media.amr.AmrEncoder;

/**
 * Android多媒体之 wav和amr的互转
 * http://blog.csdn.net/honeybaby201314/article/details/50379040
 * <p>
 * 1.通过Android系统自带的AmrInputStream类，因为它被隐藏了，只有通过反射来操作。
 * 2.通过开源库opencore进行转换，下面是jni部分
 */
public class MainActivity_pcm_amr extends AppCompatActivity implements OnClickListener {

    final private static byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};

    Button recordButton;
    boolean isRecording;
    boolean isDecoding;

    private String TAG = "MainActivity";

    private AmrFileDecoder mAmrFileDecoder;

    private CheckBox armEncode;

    private SoundMan soundMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pcm_amr);
        KCacheUtils.init(this);
        armEncode = (CheckBox) findViewById(R.id.armEncode);

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerButton();
            }
        });

        initView();

        armEncode.setText("用amr-codec编码");
        armEncode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    armEncode.setText("用系统AmrInputStream编码");
                else
                    armEncode.setText("用amr-codec编码");
            }
        });
    }

    private TextView hintView;
    private Button startButton;

    private void initView() {
        startButton = (Button) findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);
        hintView = (TextView) findViewById(R.id.hint);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            transferButtonClicked();
        }
    }

    private void transferButtonClicked() {
        showWaitDialog();
        startTransfer();
    }

    private ProgressDialog waitDialog;

    private void showWaitDialog() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(getResources().getString(R.string.transfer_wait_title));
        waitDialog.setMessage(getResources().getString(R.string.transfer_wait_message));
        waitDialog.show();
    }

    private void startTransfer() {
        new TransferThread(this, new TransferThread.TransferCallback() {

            @Override
            public void onSuccess() {
                transferSuccess();
            }

            @Override
            public void onFailed() {
            }
        }).start();
    }

    private void transferSuccess() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                waitDialog.dismiss();
                hintView.setText(getResources().getString(R.string.transfer_result));
                ToastUtil.showShort(MainActivity_pcm_amr.this, R.string.success_hint);
            }
        });
    }


    private void triggerButton() {

        if (soundMan == null)
            soundMan = new SoundMan();

        if (isRecording) {
            soundMan.setUseSystEncode(armEncode.isChecked()).stop();
            recordButton.setText(R.string.record_start);
        } else {
            soundMan.setUseSystEncode(armEncode.isChecked()).start();
            recordButton.setText(R.string.record_stop);
            Toast.makeText(this, String.format("Check your file at: %s", KCacheUtils.getCacheDirectory() + "/record"), Toast.LENGTH_LONG).show();
        }

        isRecording = !isRecording;
    }

    /**
     * @param view
     */
    public void onDecode(View view) {
        Button decodeButton = (Button) view;

        if (mAmrFileDecoder == null) {
            mAmrFileDecoder = new AmrFileDecoder();
        }

        if (!isDecoding) {
            mAmrFileDecoder.start(getResources().openRawResource(R.raw.demo));
            decodeButton.setText(R.string.decode_stop);
        } else {
            mAmrFileDecoder.stop();
            decodeButton.setText(R.string.decode_start);
        }

        isDecoding = !isDecoding;
    }

    /**
     * 通过反射调用android系统自身AmrInputStream类进行转换
     *
     * @param inPath  源文件
     * @param outPath 目标文件
     */
    public void systemWav2Amr(String inPath, String outPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(inPath);
            FileOutputStream fileoutputStream = new FileOutputStream(outPath);
            // 获得Class
            Class<?> cls = Class.forName("android.media.AmrInputStream");
            // 通过Class获得所对应对象的方法
            Method[] methods = cls.getMethods();
            // 输出每个方法名
            fileoutputStream.write(header);
            Constructor<?> con = cls.getConstructor(InputStream.class);
            Object obj = con.newInstance(fileInputStream);
            for (Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if ("read".equals(method.getName())
                        && parameterTypes.length == 3) {
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = (int) method.invoke(obj, buf, 0, 1024)) > 0) {
                        fileoutputStream.write(buf, 0, len);
                    }
                    break;
                }
            }
            for (Method method : methods) {
                if ("close".equals(method.getName())) {
                    method.invoke(obj);
                    break;
                }
            }
            fileoutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wav2amr(final String inpath, final String outpath) {
        //	Random random = new Random();
//		File file = new File(root + "/RawAudio.raw");
//    	file.getAbsolutePath(),root + "/test" + random.nextInt(120) + ".amr"
        new Thread(new Runnable() {
            @Override
            public void run() {
                convertAMR(inpath, outpath);
            }
        }).start();
    }

    /**
     * 将wav或raw文件转换成amr
     *
     * @param inpath  源文件
     * @param outpath 目标文件
     */
    private void convertAMR(String inpath, String outpath) {
        try {
            AmrEncoder.init(0);
            File inFile = new File(inpath);
            List<short[]> armsList = new ArrayList<short[]>();
            FileInputStream inputStream = new FileInputStream(inFile);
            FileOutputStream outStream = new FileOutputStream(outpath);
            //写入Amr头文件
            outStream.write(header);

            int byteSize = 320;
            byte[] buff = new byte[byteSize];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, byteSize)) > 0) {
                short[] shortTemp = new short[160];
                //将byte[]转换成short[]
                ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortTemp);
                //将short[]转换成byte[]
//				ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortTemp);
                armsList.add(shortTemp);
            }

            for (int i = 0; i < armsList.size(); i++) {
                int size = armsList.get(i).length;
                byte[] encodedData = new byte[size * 2];
                int len = AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(), armsList.get(i), encodedData);
                if (len > 0) {
                    byte[] tempBuf = new byte[len];
                    System.arraycopy(encodedData, 0, tempBuf, 0, len);
                    outStream.write(tempBuf, 0, len);
                }
            }
            AmrEncoder.reset();
            AmrEncoder.exit();

            outStream.close();
            inputStream.close();
            System.out.println("convert success ... " + outpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveFile(File oldFile, File newFile) throws IOException {
        if (!oldFile.exists()) {
            throw new IOException("oldFile is not exists. in moveFile() fun");
        }
        if (oldFile.length() <= 0) {
            throw new IOException("oldFile size = 0. in moveFile() fun");
        }
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldFile));
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newFile,
                false));

        final byte[] AMR_HEAD = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};
        writer.write(AMR_HEAD, 0, AMR_HEAD.length);
        writer.flush();

        try {
            byte[] buffer = new byte[1024];
            int numOfRead = 0;
            Log.d(TAG, "POS...newFile.length=" + newFile.length() + "  old=" + oldFile.length());
            while ((numOfRead = reader.read(buffer, 0, buffer.length)) != -1) {
                writer.write(buffer, 0, numOfRead);
                writer.flush();
            }
            Log.d(TAG, "POS..AFTER...newFile.length=" + newFile.length());
        } catch (IOException e) {
            Log.e(TAG, "moveFile error.. in moveFile() fun." + e.getMessage());
            throw new IOException("moveFile error.. in moveFile() fun.");
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }
}
