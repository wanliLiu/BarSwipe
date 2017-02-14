/******************************************************************************
 * Copyright 2011 Tavendo GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.barswipe.web;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

public class WebSocketActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AutobahnAndroidBroadcast";

    static EditText mHostname;
    static EditText mPort;
    static TextView mStatusline;
    static Button mStart;

    static EditText mMessage;
    static Button mSendMessage;

    static TextView mLog;
    static ScrollView mLogScroller;
    private CheckBox select;
    private ImageView pic;

    private Button sendVolume;
    private AudioPlay record, play;
    private boolean isRecord = false, isSend = true;

//    AsyncHttpClient client  = new AsyncHttpClient()
//            WebSocketClient client = new WebSocketClient() {
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//
//    }
//
//    @Override
//    public void onMessage(String message) {
//
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//
//    }
//
//    @Override
//    public void onError(Exception ex) {
//
//    }
//};

    private final WebSocket mConnection = new WebSocketConnection();
    private okhttp3.ws.WebSocket webSocket;

    private SharedPreferences mSettings;

    final static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .readTimeout(3000, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3000, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3000, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    private void alert(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void loadPrefs() {

        mHostname.setText(mSettings.getString("hostname", ""));
        mPort.setText(mSettings.getString("port", "9000"));
    }

    private void savePrefs() {

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("hostname", mHostname.getText().toString());
        editor.putString("port", mPort.getText().toString());
        editor.commit();
    }

    private void setButtonConnect() {
        mHostname.setEnabled(true);
        mPort.setEnabled(true);
        mStart.setText("Connect");
        mStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startConnect();
            }
        });
    }

    private void setButtonDisconnect() {
        mHostname.setEnabled(false);
        mPort.setEnabled(false);
        mStart.setText("Disconnect");
        mStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dissconnect();
            }
        });
    }

    String str = null;
    byte[] data = null;

    /**
     *
     */
    public void OkhttpwebSocketTest() {
        final String wsuri = mHostname.getText().toString();
        Request request = new Request.Builder().url(wsuri).build();
        WebSocketCall webSocketCall = WebSocketCall.create(mOkHttpClient, request);
        webSocketCall.enqueue(new WebSocketListener() {

            @Override
            public void onOpen(okhttp3.ws.WebSocket webSomcket, Response response) {
                webSocket = webSomcket;
                if (isSend) {
                    if (record == null) {
                        record = new AudioPlay();
                        record.initRecord(webSocket);
                    }
                } else {
                    if (!isRecord && play == null) {
                        play = new AudioPlay().initTrack();
                    }
                }

                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        mStatusline.setText("Status: Connected to " + wsuri);
                        savePrefs();
                        mSendMessage.setEnabled(true);
                        mMessage.setEnabled(true);
                        setButtonDisconnect();
                    }
                });
            }

            /**
             * 连接失败
             * @param e
             * @param response Present when the failure is a direct result of the response (e.g., failed
             * upgrade, non-101 response code, etc.). {@code null} otherwise.
             */
            @Override
            public void onFailure(IOException e, Response response) {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        alert("Connection lost.");
                        mStatusline.setText("Status: Ready.");
                        setButtonConnect();
                        mSendMessage.setEnabled(false);
                        mMessage.setEnabled(false);
                    }
                });
            }

            //定义一个根据图片url获取InputStream的方法
            public byte[] getBytes(InputStream is) throws IOException {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024]; // 用数据装
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    outstream.write(buffer, 0, len);
                }
                outstream.close();
                // 关闭流一定要记得。
                return outstream.toByteArray();
            }

            /**
             * @param b
             * @param ret
             * @return
             */
            public void getFileFromBytes(byte[] b) {

                File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                if (file.exists())
                    file.delete();
                BufferedOutputStream stream = null;
                try {
                    FileOutputStream fstream = new FileOutputStream(file);
                    stream = new BufferedOutputStream(fstream);
                    stream.write(b);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            /**
             * 接收到消息
             * @param message
             * @throws IOException
             */
            @Override
            public void onMessage(ResponseBody message) throws IOException {
                str = "";
                if (message.contentType() == okhttp3.ws.WebSocket.TEXT) {//
                    str = message.source().readByteString().utf8();
                } else {
                    str = "";
                    if (play != null) {
                        play.onPlaying(message.bytes());
//                        play.playAudioData(message.bytes());
                    }

//
//                    data = message.bytes();
//                    if (data != null) {
//                        play.playAudioData(data);
//                        data = null;
//                    }
                }
                message.source().close();

                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        try {
                            if (!TextUtils.isEmpty(str)) {//
                                mLog.setText(mLog.getText() + "\n" + str);
                                mLogScroller.post(new Runnable() {
                                    public void run() {
                                        mLogScroller.smoothScrollTo(0, mLog.getBottom());
                                    }
                                });
                            }

//                            if (data != null) {
////                                Log.e("data",new String(data));
//                                play.onPlaying(data);
//////                                getFileFromBytes(data);
//////                                Bitmap b1itmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//////                                if (b1itmap != null) {
//////                                    pic.setVisibility(View.VISIBLE);
//////                                    pic.setImageBitmap(b1itmap);
//////                                }
//                                data = null;
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onPong(Buffer payload) {
                Log.d("WebSocketCall", "onPong:");
            }


            /**
             * 关闭
             * @param code The <a href="http://tools.ietf.org/html/rfc6455#section-7.4.1">RFC-compliant</a>
             * status code.
             * @param reason Reason for close or an empty string.
             */
            @Override
            public void onClose(int code, String reason) {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        alert("Connection lost.");
                        mStatusline.setText("Status: Ready.");
                        setButtonConnect();
                        mSendMessage.setEnabled(false);
                        mMessage.setEnabled(false);
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void startConnect() {
        if (isOkhttpWebSocket()) {
            OkhttpwebSocketTest();
        } else {
            start();
        }
    }

    /**
     *
     */
    private void dissconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isOkhttpWebSocket()) {
                    if (webSocket != null) {
                        try {
                            webSocket.close(1000, "主动关闭");
                            webSocket = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (mConnection != null && mConnection.isConnected()) {
                        mConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * @param test
     */
    private void sendMessage(final String test) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isOkhttpWebSocket()) {
                    if (webSocket != null) {
                        try {
                            final RequestBody response = RequestBody.create(okhttp3.ws.WebSocket.TEXT, test);//文本格式发送消息
                            webSocket.sendMessage(response);
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                } else {
                    mConnection.sendTextMessage(test);
//                mConnection.sendBinaryMessage(mMessage.getText().toString().getBytes());
                }
            }
        }).start();

    }

    private void sendMessageBin(final String filePath) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] stream = null;
                try {
                    stream = readStream(new FileInputStream(filePath));
                } catch (FileNotFoundException e) {

                }

                if (stream == null)
                    return;

                if (isOkhttpWebSocket()) {
                    if (webSocket != null) {
                        try {
                            final RequestBody response = RequestBody.create(okhttp3.ws.WebSocket.BINARY, stream);//文本格式发送消息 new File(filePath)
                            webSocket.sendMessage(response);
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                } else {
                    mConnection.sendBinaryMessage(stream);
                }
            }
        }).start();

    }

    /**
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            inStream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    private void start() {

//        final String wsuri = "ws://" + mHostname.getText() + ":" + mPort.getText();
        final String wsuri = mHostname.getText().toString();

        mStatusline.setText("Status: Connecting to " + wsuri + " ..");

        setButtonDisconnect();

        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    mStatusline.setText("Status: Connected to " + wsuri);
                    savePrefs();
                    mSendMessage.setEnabled(true);
                    mMessage.setEnabled(true);
                }

                @Override
                public void onRawTextMessage(byte[] payload) {
                    super.onRawTextMessage(payload);
//                    mLog.setText(mLog.getText() + "\n" + payload);
                    mLog.setText(new String(payload));
                    mLogScroller.post(new Runnable() {
                        public void run() {
                            mLogScroller.smoothScrollTo(0, mLog.getBottom());
                        }
                    });
                }

                @Override
                public void onBinaryMessage(byte[] payload) {
                    super.onBinaryMessage(payload);
                    Bitmap b1itmap = BitmapFactory.decodeByteArray(payload, 0, payload.length);
                    if (b1itmap != null) {
                        pic.setVisibility(View.VISIBLE);
                        pic.setImageBitmap(b1itmap);
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    mLog.setText(mLog.getText() + "\n" + payload);
                    mLogScroller.post(new Runnable() {
                        public void run() {
                            mLogScroller.smoothScrollTo(0, mLog.getBottom());
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason) {
                    alert("Connection lost.");
                    mStatusline.setText("Status: Ready.");
                    setButtonConnect();
                    mSendMessage.setEnabled(false);
                    mMessage.setEnabled(false);
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ws_main);

        pic = (ImageView) findViewById(R.id.pic);
        pic.setVisibility(View.GONE);

        sendVolume = (Button) findViewById(R.id.sendVolume);

        mHostname = (EditText) findViewById(R.id.hostname);
        mPort = (EditText) findViewById(R.id.port);
        select = (CheckBox) findViewById(R.id.select);

        mPort.setVisibility(View.GONE);
        mStatusline = (TextView) findViewById(R.id.statusline);
        mStart = (Button) findViewById(R.id.start);
        mMessage = (EditText) findViewById(R.id.msg);
        mSendMessage = (Button) findViewById(R.id.sendMsg);
        mLog = (TextView) findViewById(R.id.log);
        mLogScroller = (ScrollView) findViewById(R.id.logscroller);

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        loadPrefs();

        setButtonConnect();
        mSendMessage.setEnabled(false);
        mMessage.setEnabled(false);

        mSendMessage.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendMessage(mMessage.getText().toString());
            }
        });

        select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                select.setText(isChecked ? "用autobahn" : "用Okhttp-websocket");
                mStart.setText("Disconnect");
                dissconnect();
                setButtonConnect();
            }
        });

        findViewById(R.id.sendPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(WebSocketActivity.this);
            }
        });

        sendVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecord) {
                    isRecord = true;
                    record.startRecording();
//                    tst();
                    sendVolume.setText("结束语音");
                } else {
                    isRecord = false;
                    record.stopRecording();
                    sendVolume.setText("开始语音");
                }
            }
        });

        final CheckBox box = (CheckBox) findViewById(R.id.action);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSend = isChecked;
                box.setText(isChecked ? "发送" : "接收");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null && photos.size() > 0) {
                sendMessageBin(photos.get(0));
            }
        }
    }

    /**
     * @return
     */
    private boolean isOkhttpWebSocket() {
        return !select.isChecked();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dissconnect();

        if (play != null) {
            play.destory();
        }
    }
}
