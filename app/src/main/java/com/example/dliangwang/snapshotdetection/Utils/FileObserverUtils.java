package com.example.dliangwang.snapshotdetection.Utils;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileObserver;

import com.example.dliangwang.snapshotdetection.CallBack.ISnapShotCallBack;

import java.io.File;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class FileObserverUtils {
    private static FileObserver fileObserver;
    private static ISnapShotCallBack snapShotCallBack;
    public static String SNAP_SHOT_FOLDER_PATH;
    private static String lastShownSnapshot;
    private static final int MAX_TRYS = 2;

    public static void setSnapShotCallBack(ISnapShotCallBack callBack) {
        snapShotCallBack = callBack;
        initFileObserver();
    }

    private static void initFileObserver() {
        SNAP_SHOT_FOLDER_PATH = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator + "Screenshots" + File.separator;

        fileObserver = new FileObserver(SNAP_SHOT_FOLDER_PATH, FileObserver.CREATE) {
            @Override
            public void onEvent(int event, String path) {
                if (null != path && event == FileObserver.CREATE && (!path.equals(lastShownSnapshot))){
                    lastShownSnapshot = path; // 有些手机同一张截图会触发多个CREATE事件，避免重复展示

                    String snapShotFilePath = SNAP_SHOT_FOLDER_PATH + path;

                    int tryTimes = 0;
                    while (true) {
                        try { // 收到CREATE事件后马上获取并不能获取到，需要延迟一段时间
                            Thread.sleep(600);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            BitmapFactory.decodeFile(snapShotFilePath);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            tryTimes++;
                            if (tryTimes >= MAX_TRYS) { // 尝试MAX_TRYS次失败后，放弃
                                return;
                            }
                        }
                    }

                    snapShotCallBack.snapShotTaken(path);
                }
            }
        };
    }

    public static void startSnapshotWatching() {
        if (null == snapShotCallBack) {
            throw new ExceptionInInitializerError("Call FileObserverUtils.setSnapShotCallBack first to setup callback!");
        }

        fileObserver.startWatching();
    }

    public static void stopSnapshotWatching() {
        if (null == snapShotCallBack) {
            throw new ExceptionInInitializerError("Call FileObserverUtils.setSnapShotCallBack first to setup callback!");
        }

        fileObserver.stopWatching();
    }
}
