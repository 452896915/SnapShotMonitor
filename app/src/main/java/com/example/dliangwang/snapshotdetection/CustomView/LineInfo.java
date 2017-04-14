package com.example.dliangwang.snapshotdetection.CustomView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class LineInfo {
    private List<PointInfo> pointList;
    private LineType lineType;

    public enum LineType {
        NormalLine,
        MosaicLine
    }

    public LineInfo(LineType type) {
        pointList = new ArrayList<>();
        lineType = type;
    }

    public void addPoint(PointInfo point) {
        pointList.add(point);
    }

    public List<PointInfo> getPointList() {
        return pointList;
    }

    public LineType getLineType() {
        return lineType;
    }
}
