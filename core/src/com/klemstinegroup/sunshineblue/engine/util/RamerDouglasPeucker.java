package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * The Ramer–Douglas–Peucker algorithm (RDP) is an algorithm for reducing the number of points in a 
 * curve that is approximated by a series of points.
 * <p>
 * @see <a href="https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm">Ramer–Douglas–Peucker Algorithm (Wikipedia)</a>
 * <br>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class RamerDouglasPeucker {

    private RamerDouglasPeucker() { }

    private static final float sqr(float x) { 
        return x*x;
    }

    private static final float distanceBetweenPoints(float vx, float vy, float wx, float wy) {
        return sqr(vx - wx) + sqr(vy - wy);
    }

    private static final float distanceToSegmentSquared(float px, float py, float vx, float vy, float wx, float wy) {
        final float l2 = distanceBetweenPoints(vx, vy, wx, wy);
        if (l2 == 0) 
            return distanceBetweenPoints(px, py, vx, vy);
        final float t = ((px - vx) * (wx - vx) + (py - vy) * (wy - vy)) / l2;
        if (t < 0) 
            return distanceBetweenPoints(px, py, vx, vy);
        if (t > 1) 
            return distanceBetweenPoints(px, py, wx, wy);
        return distanceBetweenPoints(px, py, (vx + t * (wx - vx)), (vy + t * (wy - vy)));
    }

    private static final float perpendicularDistance(float px, float py, float vx, float vy, float wx, float wy) {
        return (float) Math.sqrt(distanceToSegmentSquared(px, py, vx, vy, wx, wy));
    }

    private static final void douglasPeucker(Array<Float[]> list, int s, int e, float epsilon, Array<Float[]> resultList) {
        // Find the point with the maximum distance
        float dmax = 0;
        int index = 0;

        final int start = s;
        final int end = e-1;
        for (int i=start+1; i<end; i++) {
            // Point
            final float px = list.get(i)[0];
            final float py = list.get(i)[1];
            // Start
            final float vx = list.get(start)[0];
            final float vy = list.get(start)[1];
            // End
            final float wx = list.get(end)[0];
            final float wy = list.get(end)[1];
            final float d = perpendicularDistance(px, py, vx, vy, wx, wy); 
            if (d > dmax) {
                index = i;
                dmax = d;
            }
        }
        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            // Recursive call
            douglasPeucker(list, s, index, epsilon, resultList);
            douglasPeucker(list, index, e, epsilon, resultList);
        } else {
            if ((end-start)>0) {
                resultList.add(list.get(start));
                resultList.add(list.get(end));   
            } else {
                resultList.add(list.get(start));
            }
        }
    }

    /**
     * Given a curve composed of line segments find a similar curve with fewer points.
     * 
     * @param epsilon Distance dimension
     * @return Similar curve with fewer points
     */
//    public static final Array<Float[]> douglasPeucker(Array<Float[]> list, float epsilon) {
//        final Array<Float[]> resultList = new Array<Float[]>();
//        douglasPeucker(list, 0, list.size, epsilon, resultList);
//        return resultList;
//    }

    public static final Array<Vector2> douglasPeucker(Array<Vector2> vectorlist, float epsilon){
        Array<Float[]> list=new Array<>();
        for (Vector2 v:vectorlist){
            list.add(new Float[]{v.x,v.y});
        }
        final Array<Float[]> resultList = new Array<>();
        douglasPeucker(list, 0, list.size, epsilon, resultList);
        Array<Vector2> outList=new Array<>();
        for (Float[] f:resultList){
            outList.add(new Vector2(f[0],f[1]));
        }
        return outList;
    }
}