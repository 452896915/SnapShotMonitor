package com.example.dliangwang.snapshotdetection.CallBack;

import android.content.Context;
import android.content.Intent;

import com.example.dliangwang.snapshotdetection.SnapShotEditActivity;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class SnapShotTakeCallBack implements ISnapShotCallBack {
    public static final String SNAP_SHOT_PATH_KEY = "snap_shot_path_key";
    private Context context;

    public SnapShotTakeCallBack(Context context) {
        this.context = context;
    }

    @Override
    public void snapShotTaken(String path) {
        Intent intent = new Intent(context, SnapShotEditActivity.class);
        intent.putExtra(SNAP_SHOT_PATH_KEY, path);
        context.startActivity(intent);
    }
}
