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

import java.io.File;
import java.io.IOException;
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

    Bitmap bitmap = null;
    String str = null;

    /**
     *
     */
    public void OkhttpwebSocketTest() {
        final String wsuri = mHostname.getText().toString();
        Request request = new Request.Builder().url(wsuri).build();
        WebSocketCall webSocketCall = WebSocketCall.create(mOkHttpClient, request);
        webSocketCall.enqueue(new WebSocketListener() {
//            private final ExecutorService sendExecutor = Executors.newSingleThreadExecutor();

            @Override
            public void onOpen(okhttp3.ws.WebSocket webSomcket, Response response) {
                webSocket = webSomcket;
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        mStatusline.setText("Status: Connected to " + wsuri);
                        savePrefs();
                        mSendMessage.setEnabled(true);
                        mMessage.setEnabled(true);
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

            /**
             * 接收到消息
             * @param message
             * @throws IOException
             */
            @Override
            public void onMessage(ResponseBody message) throws IOException {

                if (message.contentType() == okhttp3.ws.WebSocket.TEXT) {//
                    String dstr = message.source().readByteString().utf8();
                    str = dstr;
                }else{
                    Bitmap b1itmap = BitmapFactory.decodeStream(message.byteStream());
                    bitmap = b1itmap;
                }

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
                            }else{
                                if (bitmap != null)
                                {
                                    pic.setVisibility(View.VISIBLE);
                                    pic.setImageBitmap(bitmap);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                message.source().close();

//                final RequestBody response;
//                Log.d("WebSocketCall", "onMessage:" + message.source().readByteString().utf8());
//                if (message.contentType() == okhttp3.ws.WebSocket.TEXT) {//
//                    response = RequestBody.create(okhttp3.ws.WebSocket.TEXT, "你好");//文本格式发送消息
//                } else {
//                    BufferedSource source = message.source();
//                    Log.d("WebSocketCall", "onMessage:" + source.readByteString());
//                    response = RequestBody.create(okhttp3.ws.WebSocket.BINARY, source.readByteString());
//                }
//                message.source().close();
//                sendExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(1000 * 60);
//                            webSocket.sendMessage(response);//发送消息
//                        } catch (IOException e) {
//                            e.printStackTrace(System.out);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
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

    /**
     * @param test
     */
    private void sendMessage(String test) {
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
                    mLog.setText(new String(payload));
                    mLogScroller.post(new Runnable() {
                        public void run() {
                            mLogScroller.smoothScrollTo(0, mLog.getBottom());
                        }
                    });
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

        pic = (ImageView)findViewById(R.id.pic);
        pic.setVisibility(View.GONE);

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
                try {
                    final RequestBody response = RequestBody.create(okhttp3.ws.WebSocket.BINARY, new File(photos.get(0)));//文本格式发送消息
                    webSocket.sendMessage(response);
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
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
    }
}
