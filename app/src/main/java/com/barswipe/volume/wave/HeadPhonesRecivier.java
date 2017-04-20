package com.barswipe.volume.wave;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * Created by Soli on 2017/4/20.
 */

public class HeadPhonesRecivier extends BroadcastReceiver {

    //有线耳机
    public String HeadSetAction = Intent.ACTION_HEADSET_PLUG;

    //蓝牙耳机
    public String BluetoothHeadSet = BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED;

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
                        isHaveHeadSet = false;
                    }
                }
            } else if (action.equals(BluetoothHeadSet)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null)
                    isHaveHeadSet = adapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_DISCONNECTED ? false : true;
            } else if (action.equals(AnotherAction)) {
                isHaveHeadSet = false;
            }
            Log.e("耳机状态",isHaveHeadSet ? "插入" : "拔出");
            EventBus.getDefault().post(new HeadSetEvent(isHaveHeadSet));
        }

    }
}
