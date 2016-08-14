package com.barswipe;

import android.app.Application;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoLi on 2015/12/11.
 */
public class Myapplication extends Application {

    private HttpClient httpClient;


    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        httpClient = this.createHttpClient();

        JSONObject object = new JSONObject();
        object.put("UserToken","sssssdsdwqeewewe");
        object.put("KeyID","list");
        object.put("Total",2);
        object.put("UseLogs", getRecordData(""));
        Log.e("json1", JSON.toJSONString(object));

        object.put("UseLogs", getRecordDatdsa());
        object.put("KeyID", "Array");
        Log.e("json2",JSON.toJSONString(object));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.shutdownHttpClient();
    }

    /**
     * 创建HttpClient实例
     *
     * @return
     */
    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
                params, schReg);

        return new DefaultHttpClient(connMgr, params);
    }

    /**
     * 关闭连接管理器并释放资源
     */
    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * 对外提供HttpClient实例
     *
     * @return
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * @return
     */
    private  DoorRecordBean[] getRecordDatdsa()
    {
        int length = 2;
        DoorRecordBean[] temp = new DoorRecordBean[length];
        while (length > 0)
        {
            temp[length - 1] = new DoorRecordBean();
            temp[length - 1].setOrderID(15152312);
            temp[length - 1].setDoorID(1526523);
            temp[length - 1].setOpenDoorTime("2015-15-12 12:3" + length);
            length --;
        }
        return temp;
    }

    /**
     *
     * @param data
     * @return
     */
    private List<DoorRecordBean> getRecordData(String data)
    {
        List<DoorRecordBean> temp = new ArrayList<>();
        int length = 3;
        while (length > 0)
        {
            DoorRecordBean child = new DoorRecordBean();
            child.setOrderID(152312);
            child.setDoorID(1526523);
            child.setOpenDoorTime("2015-15-12 12:3" + length);
            temp.add(child);
            length --;
        }
        return temp;
    }
}
