package com.example.dliangwang.snapshotdetection.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class PaintableImageView extends ImageView {
    private List<LineInfo> lineList; // 线条列表

    private LineInfo currentLine; // 当前线条
    private LineInfo.LineType currentLineType = LineInfo.LineType.NormalLine; // 当前线条类型

    private Paint normalPaint = new Paint();
    private static final float NORMAL_LINE_STROKE = 5.0f;

    private Paint mosaicPaint = new Paint();
    private static final int MOSAIC_CELL_LENGTH = 30; // 马赛克每个大小40*40像素，共三行

    private Drawable drawable;
    private Bitmap bitmap;

    private boolean mosaics[][]; // 马赛克绘制中用于记录某个马赛克格子的数值是否计算过
    private int mosaicRows; // 马赛克行数
    private int mosaicColumns; // 马赛克列数

    {
        lineList = new ArrayList<>();
        normalPaint.setColor(Color.RED);
        normalPaint.setStrokeWidth(NORMAL_LINE_STROKE);
    }

    public PaintableImageView(Context context) {
        super(context);
    }

    public PaintableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置线条类型
     * @param type
     */
    public void setLineType(LineInfo.LineType type) {
        currentLineType = type;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentLine = new LineInfo(currentLineType);
                currentLine.addPoint(new PointInfo(xPos, yPos));
                lineList.add(currentLine);
                invalidate();
                return true; // return true消费掉ACTION_DOWN事件，否则不会触发ACTION_UP
            case MotionEvent.ACTION_MOVE:
                currentLine.addPoint(new PointInfo(xPos, yPos));
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                currentLine.addPoint(new PointInfo(xPos, yPos));
                invalidate();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mosaicRows; i++) {
            for (int j = 0; j < mosaicColumns; j++) {
                mosaics[i][j] = false;
            }
        }

        for (LineInfo lineinfo : lineList) {
            if (lineinfo.getLineType() == LineInfo.LineType.NormalLine) {
                drawNormalLine(canvas, lineinfo);
            } else if (lineinfo.getLineType() == LineInfo.LineType.MosaicLine) {
                drawMosaicLine(canvas, lineinfo);
            }
        }
    }

    /**
     * 绘制马赛克线条
     * @param canvas
     * @param lineinfo
     */
    private void drawMosaicLine(Canvas canvas, LineInfo lineinfo) {
        if (null == bitmap) {
            init();
        }

        if (null == bitmap) {
            return;
        }

        for (PointInfo pointInfo : lineinfo.getPointList()) {
            // 对每一个点，填充所在的小格子以及上下两个格子（如果有上下格子）
            int currentRow = (int) ((pointInfo.y -1) / MOSAIC_CELL_LENGTH);
            int currentCol = (int) ((pointInfo.x -1) / MOSAIC_CELL_LENGTH);

            fillMosaicCell(canvas, currentRow, currentCol);
            fillMosaicCell(canvas, currentRow - 1, currentCol);
            fillMosaicCell(canvas, currentRow + 1, currentCol);
        }
    }

    /**
     * 填充一个马赛克格子
     * @param cavas
     * @param row 马赛克格子行
     * @param col 马赛克格子列
     */
    private void fillMosaicCell(Canvas cavas, int row, int col) {
        if (row >= 0 && row < mosaicRows && col >= 0 && col < mosaicColumns) {
            if (!mosaics[row][col]) {
                mosaicPaint.setColor(bitmap.getPixel(col * MOSAIC_CELL_LENGTH, row * MOSAIC_CELL_LENGTH));

//                normalPaint.setColor(Color.RED);
//                normalPaint.setStrokeWidth(5);
//                cavas.drawPoint(col * MOSAIC_CELL_LENGTH, row * MOSAIC_CELL_LENGTH, normalPaint);
                cavas.drawRect(col * MOSAIC_CELL_LENGTH, row * MOSAIC_CELL_LENGTH, (col + 1) * MOSAIC_CELL_LENGTH, (row + 1) * MOSAIC_CELL_LENGTH, mosaicPaint);
                mosaics[row][col] = true;
            }
        }
    }

    /**
     * 绘制普通线条
     * @param canvas
     * @param lineinfo
     */
    private void drawNormalLine(Canvas canvas, LineInfo lineinfo) {
        if (lineinfo.getPointList().size() <= 1) {
            return;
        }

        for (int i = 0; i < lineinfo.getPointList().size() - 1; i++) {
            PointInfo startPoint  = lineinfo.getPointList().get(i);
            PointInfo endPoint  = lineinfo.getPointList().get(i + 1);

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, normalPaint);
        }
    }

    /**
     * 初始化马赛克绘制相关
     */
    private void init() {
        drawable = getDrawable();

        try {
            bitmap = ((BitmapDrawable)drawable).getBitmap();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }

        mosaicColumns = (int)Math.ceil(bitmap.getWidth() / MOSAIC_CELL_LENGTH);
        mosaicRows = (int)Math.ceil(bitmap.getHeight() / MOSAIC_CELL_LENGTH);
        mosaics = new boolean[mosaicRows][mosaicColumns];
    }

    /**
     * 删除最后添加的线
     */
    public void withDrawLastLine() {
        if (lineList.size() > 0) {
            lineList.remove(lineList.size() - 1);
            invalidate();
        }
    }

    /**
     * 判断是否可以继续撤销
     * @return
     */
    public boolean canStillWithdraw() {
        return lineList.size() > 0;
    }
}
