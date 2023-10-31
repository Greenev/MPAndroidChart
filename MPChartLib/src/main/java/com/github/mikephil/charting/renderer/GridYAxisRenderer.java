package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class GridYAxisRenderer extends YAxisRenderer {

    public GridYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        if (mYAxis.isDrawGridLinesEnabled()) {

            int clipRestoreCount = c.save();
            c.clipRect(getGridClippingRect());

            float[] positions = getTransformedGridPositions();

            mGridPaint.setColor(mYAxis.getGridColor());
            mGridPaint.setStrokeWidth(mYAxis.getGridLineWidth());
            mGridPaint.setPathEffect(mYAxis.getGridDashPathEffect());

            Path gridLinePath = mRenderGridLinesPath;
            gridLinePath.reset();

            // draw the grid
            for (int i = 0; i < positions.length; i += 2) {
                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(linePath(gridLinePath, i, positions), mGridPaint);
                gridLinePath.reset();
            }
            c.restoreToCount(clipRestoreCount);
        }

        if (mYAxis.isDrawZeroLineEnabled()) {
            drawZeroLine(c);
        }
    }

    protected float[] getTransformedGridPositions() {

        if(mGetTransformedPositionsBuffer.length != mYAxis.mEntryCount * 2){
            mGetTransformedPositionsBuffer = new float[mYAxis.mEntryCount * 2];
        }
        float[] positions = mGetTransformedPositionsBuffer;
        MPPointD pp = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed for y-labels
            if (pp.y < mYAxis.mEntries[i / 2] - 0.05f) {
                positions[i + 1] = mYAxis.mEntries[i / 2] - 0.05f;
            }
            else if (i == 0) {
                positions[i] = mYAxis.mEntries[(positions.length - 1) / 2] + 0.05f;
                positions[i + 1] = mYAxis.mEntries[(positions.length - 1) / 2] + 0.05f;
            } else {
                positions[i] = mYAxis.mEntries[i / 2] + 0.05f;
                positions[i + 1] = mYAxis.mEntries[i / 2] + 0.05f;
            }
        }

        mTrans.pointValuesToPixel(positions);
        return positions;
    }
}
