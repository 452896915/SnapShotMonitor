package com.example.dliangwang.snapshotdetection;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.dliangwang.snapshotdetection.CustomView.LineInfo;
import com.example.dliangwang.snapshotdetection.CustomView.PaintableImageView;
import com.example.dliangwang.snapshotdetection.Utils.BitmapUtils;

import static com.example.dliangwang.snapshotdetection.Utils.FileObserverUtils.SNAP_SHOT_FOLDER_PATH;
import static com.example.dliangwang.snapshotdetection.CallBack.SnapShotTakeCallBack.SNAP_SHOT_PATH_KEY;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class SnapShotEditActivity extends AppCompatActivity {
    private String snapShotPath;
    private PaintableImageView imageView;

    private Button withDrawBtn;
    private Button normalLineBtn;
    private Button mosaicLineBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap_shot_layout);
        withDrawBtn = (Button) findViewById(R.id.withdraw_btn);
        normalLineBtn = (Button) findViewById(R.id.normal_line_btn);
        mosaicLineBtn = (Button) findViewById(R.id.mosai_btn);

        withDrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.withDrawLastLine();
            }
        });

        normalLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setLineType(LineInfo.LineType.NormalLine);
            }
        });

        mosaicLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setLineType(LineInfo.LineType.MosaicLine);
            }
        });

        imageView = (PaintableImageView) findViewById(R.id.image_view);
        snapShotPath = getIntent().getStringExtra(SNAP_SHOT_PATH_KEY);

        final ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                autoFitImageView();

                ViewTreeObserver vto = imageView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    vto.removeOnGlobalLayoutListener(this);
                } else {
                    vto.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 根据ImageView实际高度对图片进行等比例压缩，并调整IamgeView尺寸和Bitmap尺寸一致
     */
    private void autoFitImageView() {
        int imageViewHeight = imageView.getHeight();

        Bitmap compressedBitmap = BitmapUtils.getCompressedBitmap(SNAP_SHOT_FOLDER_PATH + snapShotPath, imageViewHeight);

        if (null != compressedBitmap) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(compressedBitmap.getWidth(), compressedBitmap.getHeight());
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            imageView.requestLayout();
            imageView.setImageBitmap(compressedBitmap);
        }
    }
}
