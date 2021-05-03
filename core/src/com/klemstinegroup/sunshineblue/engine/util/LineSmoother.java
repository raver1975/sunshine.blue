package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A method to smooth a hand-drawn line based on the McMaster 
 * line smoothing algorithm
 * 
 * @author Derek Springer
 */
public class LineSmoother {

    /**
     * @param lineSegments A Array of line segments representing a line
     * @return A Array line segments representing the smoothed line
     */
    public static Array<Line> smoothLine(Array<Line> lineSegments) {
        if(lineSegments.size < 4) return lineSegments;

        Array<Line> smoothedLine = new Array<Line>();
        Array<Vector2> Vector2s = getVector2s(lineSegments);
        smoothedLine.add(lineSegments.get(0));

        Vector2 newVector2 = Vector2s.get(1);
        for(int i = 2; i < Vector2s.size-2; i++) {
            Vector2 lastVector2 = newVector2;
            Array<Vector2> temp=new Array<>();
            for (int ii=i-2;ii<i+3;ii++){
                temp.add(Vector2s.get(ii));
            }
            newVector2 = smoothVector2(temp);
            smoothedLine.add(new Line(lastVector2, newVector2));
        }

        Line lastSegment = lineSegments.get(lineSegments.size-1);
        smoothedLine.add(new Line(
                newVector2, 
                new Vector2(lastSegment.a.x, lastSegment.a.y)));
        smoothedLine.add(lastSegment);

        return smoothedLine;
    }

    /**
     * @param lineSegments A Array of line segments representing a line
     * @return A Array of Vector2s representing the Vector2s in a series of 
     *  line segments
     */
    public static Array<Vector2> getVector2s(Array<Line> lineSegments) {
        Array<Vector2> Vector2s = new Array<>();
        for(Line segment : lineSegments) {
            Vector2s.add(new Vector2(segment.a.x, segment.a.y));
        }
        Vector2s.add(new Vector2(
                lineSegments.get(lineSegments.size-1).b.x,
                lineSegments.get(lineSegments.size-1).b.y));

        return Vector2s;
    }

    /**
     * Find the new Vector2 for a smoothed line segment 
     * @param Vector2s The five Vector2s needed
     * @return The new Vector2 for the smoothed line segment
     */
    public static Vector2 smoothVector2(Array<Vector2> Vector2s) {
        int avgX = 0;
        int avgY = 0;
        for(Vector2 Vector2 : Vector2s) {
            avgX += Vector2.x;
            avgY += Vector2.y;
        }

        avgX = avgX/Vector2s.size;
        avgY = avgY/Vector2s.size;
        Vector2 newVector2 = new Vector2(avgX, avgY);
        Vector2 oldVector2 = Vector2s.get(Vector2s.size/2);
        float newX = (newVector2.x + oldVector2.x)/2f;
        float newY = (newVector2.y + oldVector2.y)/2f;

        return new Vector2(newX, newY);
    }

    public static Array<Vector2> smoothLine2(Array<Vector2> currentPath) {
        Array<Line> temp=new Array<>();
        for (int i=0;i<currentPath.size-1;i++){
            Line line=new Line(currentPath.get(i).cpy(),currentPath.get(i+1).cpy());
            temp.add(line);
        }
        return getVector2s(smoothLine(temp));
    }
}

/**
 * A class to represent a line created from two Vector2s
 * @author Derek Springer
 */
  class Line {

    public Vector2 a;
    public Vector2 b;

    public Line(Vector2 x, Vector2 y) {
        this.a = x;
        this.b = y;
    }

    public double slope() {
        if(a.x- b.x == 0) return Double.NaN;
        return (double)(b.y- a.y)/(double)(b.x- a.x);
    }

    public double intercept() {
        return a.y - slope() * a.x;
    }

    public static float slope(float x1, float y1, float x2, float y2) {
        return (y2-y1)/(x2-x1);
    }

    public static float slope(Vector2 Vector21, Vector2 Vector22) {
        return slope(Vector21.x, Vector21.y, Vector22.x, Vector22.y);
    }

    public static float intercept(float x1, float y1, float x2, float y2) {
        return y1 - slope(x1, y1, x2, y2) * x1;
    }

    public static float intercept(Vector2 Vector21, Vector2 Vector22) {
        return intercept(Vector21.x, Vector21.y, Vector22.x, Vector22.y);
    }

    @Override
    public String toString() {
        return "[(" + a +  "), (" + b + ")] " +
                "m=" + slope() + ", b=" + intercept();
    }
}