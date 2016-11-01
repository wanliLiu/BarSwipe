package com.tbruyelle.rxpermissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.M)
public class ShadowActivity extends EnsureSameProcessActivity {

    private final int DangerousPermissions_request_code = 32;
    private final int SystemAlerWindow_request_code = 33;
    private final int WriteSetting_request_code = 34;

    private List<String> permissions, specialpermissions;
    private int[] specialGrantReult;
    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * @param intent
     */
    private void handleIntent(Intent intent) {
        String[] tempPermissions = intent.getStringArrayExtra("permissions");

        if (tempPermissions != null) {
            for (int i = 0; i < tempPermissions.length; i++) {
                String temp = tempPermissions[i];
                if (temp.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) || temp.equals(Manifest.permission.WRITE_SETTINGS)) {
                    if (specialpermissions == null)
                        specialpermissions = new ArrayList<>();
                    specialpermissions.add(temp);
                } else {
                    if (permissions == null)
                        permissions = new ArrayList<>();
                    permissions.add(temp);
                }
            }
        }

        if (specialpermissions != null && specialpermissions.size() > 0) {
            specialGrantReult = new int[specialpermissions.size()];
            index = 0;
            startRequest(index);
        } else {
            requestPerm();
        }
    }

    /**
     * @return
     */
    private boolean isHaveSpeicalGrant() {
        return index != -1;
    }

    /**
     *
     */
    private void requestPerm() {
        if (permissions != null && permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), DangerousPermissions_request_code);
        } else {
            if (isHaveSpeicalGrant()) {
                RxPermissions.getInstance(this).onRequestPermissionsResult(DangerousPermissions_request_code, specialpermissions.toArray(new String[specialpermissions.size()]), specialGrantReult, new boolean[specialGrantReult.length]);
            }
            finish();
        }
    }

    /**
     * @param position
     */
    private void startRequest(int position) {
        if (position < specialpermissions.size()) {
            if (specialpermissions.get(position).equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                startSpecialGrant(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, SystemAlerWindow_request_code);
            } else if (specialpermissions.get(position).equals(Manifest.permission.WRITE_SETTINGS)) {
                startSpecialGrant(Settings.ACTION_MANAGE_WRITE_SETTINGS, WriteSetting_request_code);
            }
        }
    }

    /**
     * @param action
     * @param requestCode
     */
    private void startSpecialGrant(String action, int requestCode) {
        Intent intent = new Intent(action, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SystemAlerWindow_request_code) {
            specialGrantReult[index] = Settings.canDrawOverlays(this) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        } else if (requestCode == WriteSetting_request_code) {
            specialGrantReult[index] = Settings.System.canWrite(this) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        }

        index++;
        if (index == specialpermissions.size()) {
            requestPerm();
        } else {
            startRequest(index);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = isHaveSpeicalGrant() ? permissions.length + specialpermissions.size() : permissions.length;
        boolean[] shouldShowRequestPermissionRationale = new boolean[length];
        String[] requestPermissions = new String[length];
        int[] requestGrantResults = new int[length];

        for (int i = 0; i < shouldShowRequestPermissionRationale.length; i++) {
            if (i >= permissions.length) {
                shouldShowRequestPermissionRationale[i] = false;
                requestPermissions[i] = specialpermissions.get(i - permissions.length);
                requestGrantResults[i] = specialGrantReult[i - permissions.length];
            } else {
                shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
                requestPermissions[i] = permissions[i];
                requestGrantResults[i] = grantResults[i];
            }
        }

        RxPermissions.getInstance(this).onRequestPermissionsResult(requestCode, requestPermissions, requestGrantResults, shouldShowRequestPermissionRationale);
        finish();
    }
}
