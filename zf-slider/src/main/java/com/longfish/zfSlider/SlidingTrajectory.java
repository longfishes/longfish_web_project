package com.longfish.zfSlider;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

class Point {
    int x, y;
    long t;

    Point(int x, int y, long t) {
        this.x = x;
        this.y = y;
        this.t = t;
    }

    @Override
    public String toString() {
        return String.format("{\"x\":%d,\"y\":%d,\"t\":%d}", x, y, t);
    }
}

public class SlidingTrajectory {

    public static List<Point> generateTrajectory(int startX, int startY, int endX, int endY, long startTime, long endTime) {
        List<Point> trajectory = new ArrayList<>();

        int steps = (int) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        long timeInterval = (endTime - startTime) / steps;

        for (int i = 0; i <= steps; i++) {
            int currentX = startX + (endX - startX) * i / steps;
            int currentY = startY + (endY - startY) * i / steps;
            long currentTime = startTime + timeInterval * i;

            trajectory.add(new Point(currentX, currentY, currentTime));
        }

        return trajectory;
    }

    public static String simulate(int distance) {
        StringBuilder mt = new StringBuilder();
        int startX = 994, startY = 184;
        int endX, endY = 187;
        endX = startX + distance;
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + 1328;

        List<Point> trajectory = generateTrajectory(startX, startY, endX, endY, startTime, endTime);

        mt.append('[');
        for (int i = 0; i < trajectory.size(); i++) {
            mt.append(trajectory.get(i));
            if (i < trajectory.size() - 1) {
                mt.append(',');
            }
        }
        mt.append(']');
        return mt.toString();
    }

    public static void main(String[] args) {
        String input = simulate(165);
        System.out.println(input);
        String encryptedStr = Base64.getEncoder().encodeToString(input.getBytes());
        System.out.println(encryptedStr);


    }


}
