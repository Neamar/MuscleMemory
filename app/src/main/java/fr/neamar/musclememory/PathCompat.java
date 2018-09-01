package fr.neamar.musclememory;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Backports {@link Path#approximate(float)} for pre-O devices.
 * From https://gist.github.com/alexjlockwood/7d3685fe9ce7dcfde33112c4e6c5ce4f
 */
public final class PathCompat {
    private static final int MAX_NUM_POINTS = 100;
    private static final int FRACTION_OFFSET = 0;
    private static final int X_OFFSET = 1;
    private static final int Y_OFFSET = 2;
    private static final int NUM_COMPONENTS = 3;

    /**
     * Approximate the <code>Path</code> with a series of line segments.
     * This returns float[] with the array containing point components.
     * There are three components for each point, in order:
     * <ul>
     * <li>Fraction along the length of the path that the point resides</li>
     * <li>The x coordinate of the point</li>
     * <li>The y coordinate of the point</li>
     * </ul>
     * <p>Two points may share the same fraction along its length when there is
     * a move action within the Path.</p>
     *
     * @param acceptableError The acceptable error for a line on the
     *                        Path. Typically this would be 0.5 so that
     *                        the error is less than half a pixel.
     * @return An array of components for points approximating the Path.
     */
    @NonNull
    @Size(multiple = 3)
    public static float[] approximate(@NonNull Path path, @FloatRange(from = 0f) float acceptableError) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return path.approximate(acceptableError);
        }
        if (acceptableError < 0) {
            throw new IllegalArgumentException("acceptableError must be greater than or equal to 0");
        }
        // Measure the total length the whole pathData.
        final PathMeasure measureForTotalLength = new PathMeasure(path, false);
        float totalLength = 0;
        // The sum of the previous contour plus the current one. Using the sum here
        // because we want to directly subtract from it later.
        final List<Float> summedContourLengths = new ArrayList<>();
        summedContourLengths.add(0f);
        do {
            final float pathLength = measureForTotalLength.getLength();
            totalLength += pathLength;
            summedContourLengths.add(totalLength);
        } while (measureForTotalLength.nextContour());

        // Now determine how many sample points we need, and the step for next sample.
        final PathMeasure pathMeasure = new PathMeasure(path, false);

        final int numPoints = Math.min(MAX_NUM_POINTS, (int) (totalLength / acceptableError) + 1);

        final float[] coords = new float[NUM_COMPONENTS * numPoints];
        final float[] position = new float[2];

        int contourIndex = 0;
        final float step = totalLength / (numPoints - 1);
        float cumulativeDistance = 0;

        // For each sample point, determine whether we need to move on to next contour.
        // After we find the right contour, then sample it using the current distance value minus
        // the previously sampled contours' total length.
        for (int i = 0; i < numPoints; i++) {
            // The cumulative distance traveled minus the total length of the previous contours
            // (not including the current contour).
            final float contourDistance = cumulativeDistance - summedContourLengths.get(contourIndex);
            pathMeasure.getPosTan(contourDistance, position, null);

            coords[i * NUM_COMPONENTS + FRACTION_OFFSET] = cumulativeDistance / totalLength;
            coords[i * NUM_COMPONENTS + X_OFFSET] = position[0];
            coords[i * NUM_COMPONENTS + Y_OFFSET] = position[1];

            cumulativeDistance = Math.min(cumulativeDistance + step, totalLength);

            // Using a while statement is necessary in the rare case where step is greater than
            // the length a path contour.
            while (summedContourLengths.get(contourIndex + 1) < cumulativeDistance) {
                contourIndex++;
                pathMeasure.nextContour();
            }
        }

        coords[(numPoints - 1) * NUM_COMPONENTS + FRACTION_OFFSET] = 1f;
        return coords;
    }

    private PathCompat() {}
}