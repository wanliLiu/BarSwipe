package com.barswipe.volume.wave;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * Created by Soli on 2017/4/20.
 */

public class HeadPhonesRecivier extends BroadcastReceiver {

    //有线耳机插入
    public String HeadSetAction = Intent.ACTION_HEADSET_PLUG;
    //蓝牙耳机
    public String BluetoothHeadSet = BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED;
    //如果是拨打电话
    public String CallPhoneAction = Intent.ACTION_NEW_OUTGOING_CALL;
    //状态
    public String PhoneStateAction = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    //有线耳机拔掉
    public String AnotherAction = AudioManager.ACTION_AUDIO_BECOMING_NOISY;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            boolean isHaveHeadSet = false;
            if (action.equals(HeadSetAction)) {
                if (intent.hasExtra("state")) {
                    // 插入设备
                    if (intent.getIntExtra("state", 0) == 1) {
                        isHaveHeadSet = true;
                    }
                    // 拔出设备
                    else if (intent.getIntExtra("state", 0) == 0) {
//                        isHaveHeadSet = false;
                        return;
                    }
                }
            } else if (action.equals(BluetoothHeadSet)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null)
                    isHaveHeadSet = adapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_DISCONNECTED ? false : true;
            } else if (action.equals(AnotherAction)) {
                isHaveHeadSet = false;
            } else if (action.equals(CallPhoneAction)) {
                //如果是拨打电话
                Log.i("HeadPhonesRecivier", CallPhoneAction);
            } else if (action.equals(PhoneStateAction)) {
                // 如果是来电
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephony.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.e("HeadPhonesRecivier", "[Broadcast]等待接电话=" + phoneNumber);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.e("HeadPhonesRecivier", "[Broadcast]电话挂断=" + phoneNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.e("HeadPhonesRecivier", "[Broadcast]通话中=" + phoneNumber);
                        break;
                }
            }
            Log.e("中断状态", isHaveHeadSet ? "插入" : "拔出");
            EventBus.getDefault().post(new HeadSetEvent());
        }
    }

}
