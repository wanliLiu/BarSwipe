package com.tbruyelle.rxpermissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;


public class RxPermissionsFragment extends Fragment {
    private final int DangerousPermissions_request_code = 32;
    private final int SystemAlerWindow_request_code = 33;
    private final int WriteSetting_request_code = 34;

    private ArrayList<String> specialpermissions, permissions;
    private int[] specialGrantResult, grantResults;
    private int index = -1;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();
    private boolean mLogging;

    public RxPermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     *
     */
    private void initParams() {
        specialGrantResult = grantResults = null;
        specialpermissions = permissions = null;
        index = -1;
    }

    /**
     * @param requestPermissions
     */
    void handleRequest(@NonNull String[] requestPermissions) {
        initParams();

        if (requestPermissions != null) {
            for (int i = 0; i < requestPermissions.length; i++) {
                String temp = requestPermissions[i];
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
            specialGrantResult = new int[specialpermissions.size()];
            index = 0;
            startRequest(index);
        } else {
            requestPerm();
        }
    }

    /**
     *
     */
    private void requestPerm() {
        if (permissions != null && permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), DangerousPermissions_request_code);
        } else {
            if (isHaveSpeicalGrant()) {
                onRequestPermissionsResult(DangerousPermissions_request_code, specialpermissions.toArray(new String[specialpermissions.size()]), specialGrantResult, new boolean[specialGrantResult.length]);
            }
        }
    }

    /**
     * @return
     */
    private boolean isHaveSpeicalGrant() {
        return index != -1;
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
        Intent intent = new Intent(action, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    /**
     * 获取权限组的key,属于哪个权限组
     *
     * @param permissions
     * @return
     */
    private String getPermissionGroupKey(String permissions) {
        String key = "";
        switch (permissions) {
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                key = Manifest.permission_group.CALENDAR;
                break;
            case Manifest.permission.CAMERA:
                key = Manifest.permission_group.CAMERA;
                break;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                key = Manifest.permission_group.CONTACTS;
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                key = Manifest.permission_group.LOCATION;
                break;
            case Manifest.permission.RECORD_AUDIO:
                key = Manifest.permission_group.MICROPHONE;
                break;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.USE_SIP:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                key = Manifest.permission_group.PHONE;
                break;
            case Manifest.permission.BODY_SENSORS:
                key = Manifest.permission_group.SENSORS;
                break;
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
            case Manifest.permission.RECEIVE_MMS:
                key = Manifest.permission_group.SMS;
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                key = Manifest.permission_group.STORAGE;
                break;
        }

        return key.replace("-", "_");
    }

    /**
     * 给出合理提示文本
     *
     * @param ctx
     * @param permissions
     * @return
     */
    private String getNotGrantedPermissionMsg(Context ctx, List<String> permissions) {
        StringBuffer buffer = new StringBuffer(ctx.getResources().getString(R.string.str_permission_grant_needed));
        if (permissions != null && permissions.size() > 0) {
            //只有一个
            if (permissions.size() == 1) {
                int resouceid = ctx.getResources().getIdentifier("str_permission_" + getPermissionGroupKey(permissions.get(0)), "string", ctx.getPackageName());
                if (resouceid > 0) {
                    buffer.append(ctx.getResources().getString(resouceid));
                }
            } else {
                for (int i = 0; i < permissions.size(); i++) {
                    int resouceid = ctx.getResources().getIdentifier("str_permission_multe_" + getPermissionGroupKey(permissions.get(i)), "string", ctx.getPackageName());
                    if (resouceid > 0) {
                        buffer.append(ctx.getResources().getString(resouceid));
                        buffer.append("、");
                    }
                }

                buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("权限。");
            }
        }

        return buffer.toString();
    }

    /**
     *
     */
    private void dealNotShowAgain() {
        if (permissions != null)
            onRequestPermissionsResultFailure(DangerousPermissions_request_code, getActivity(), permissions.toArray(new String[permissions.size()]));
    }

    /**
     *
     */
    private void dealResult() {
        int length = isHaveSpeicalGrant() ? permissions.size() + specialpermissions.size() : permissions.size();

        boolean[] shouldShowRequestPermissionRationale = new boolean[length];
        String[] requestPermissions = new String[length];
        int[] requestGrantResults = new int[length];

        for (int i = 0; i < shouldShowRequestPermissionRationale.length; i++) {
            if (i >= permissions.size()) {
                shouldShowRequestPermissionRationale[i] = false;
                requestPermissions[i] = specialpermissions.get(i - permissions.size());
                requestGrantResults[i] = specialGrantResult[i - permissions.size()];
            } else {
                shouldShowRequestPermissionRationale[i] = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions.get(i));
                requestPermissions[i] = permissions.get(i);
                requestGrantResults[i] = grantResults[i];
            }
        }

        onRequestPermissionsResult(DangerousPermissions_request_code, requestPermissions, requestGrantResults, shouldShowRequestPermissionRationale);
    }

    /**
     * 在不在提示的情况下，给出合理的解释
     *
     * @param content
     */
    private void showPermissionAlert(String content) {
        new AlertDialog.Builder(getActivity())
                .setMessage(content)
                .setPositiveButton(R.string.str_permission_conform, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dealNotShowAgain();
                })
                .setNegativeButton(R.string.str_permission_cancle, (dialogInterface, i) -> dealResult())
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SystemAlerWindow_request_code) {
            specialGrantResult[index] = Settings.canDrawOverlays(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        } else if (requestCode == WriteSetting_request_code) {
            specialGrantResult[index] = Settings.System.canWrite(getActivity()) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED;
        }

        index++;
        if (index == specialpermissions.size()) {
            requestPerm();
        } else {
            startRequest(index);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] mGrantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        grantResults = mGrantResults;
        if (requestCode == DangerousPermissions_request_code) {
            if (isAllCanShowRequestPermissionRationale(permissions)) {
                dealResult();
            }
        }
    }

    /**
     * @param permissions
     * @return
     */
    private boolean isAllCanShowRequestPermissionRationale(String[] permissions) {
        List<String> notShowRequestPermission = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            //可以给出授权提示
            if (!isGranted(permissions[i]) && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {
                notShowRequestPermission.add(permissions[i]);
            }
        }

        if (notShowRequestPermission.size() > 0) {
            showPermissionAlert(getNotGrantedPermissionMsg(getActivity(), notShowRequestPermission));
            return false;
        }

        return true;
    }


    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param shouldShowRequestPermissionRationale
     */
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        try {
            for (int i = 0, size = permissions.length; i < size; i++) {
                log("onRequestPermissionsResult  " + permissions[i]);
                // Find the corresponding subject
                PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
                if (subject != null) {
                    mSubjects.remove(permissions[i]);
                    boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    subject.onNext(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]));
                    subject.onComplete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param requestCode
     * @param activity
     * @param permissions
     */
    void onRequestPermissionsResultFailure(int requestCode, Activity activity, String[] permissions) {
        int[] grantResults = new int[permissions.length];
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_DENIED;
            shouldShowRequestPermissionRationale[i] = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
        }

        onRequestPermissionsResult(requestCode, permissions, grantResults, shouldShowRequestPermissionRationale);
    }


    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        //special grant permissions
        if (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return Settings.canDrawOverlays(getActivity());
        } else if (Manifest.permission.WRITE_SETTINGS.equals(permission)) {
            return Settings.System.canWrite(getActivity());
        }

        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    public PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public PublishSubject<Permission> setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        return mSubjects.put(permission, subject);
    }

    void log(String message) {
        if (mLogging) {
            Log.d(RxPermissions.TAG, message);
        }
    }

}
