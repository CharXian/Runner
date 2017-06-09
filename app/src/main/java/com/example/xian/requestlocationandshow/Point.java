package com.example.xian.requestlocationandshow;

import android.location.Location;

/**
 * Created by xian on 2017/6/9.
 */

public class Point {

    private double xValue, yValue;

    public Point(double xValue, double yValue){

        this.xValue = xValue;
        this.yValue = yValue;
    }

    public double getxValue(){return xValue;}

    public double getyValue(){return yValue;}

    // calculate the distance between I and another user
    public float calculateDistance(Point oppositePoint){

        float [] results = new float[1];

        Location.distanceBetween(this.xValue, this.yValue
                , oppositePoint.xValue, oppositePoint.yValue, results);
        return results[0];
    }

    public Point getShorterPoint(Point pointA, Point pointB){

        double distanceToA, distanceToB;

//        distanceToA = this.calculateDistance(pointA);
//        distanceToB = this.calculateDistance(pointB);

        distanceToA = Math.pow(this.xValue - pointA.xValue, 2)
                     +Math.pow(this.yValue - pointA.yValue, 2);

        distanceToB = Math.pow(this.xValue - pointB.xValue, 2)
                     +Math.pow(this.yValue - pointB.yValue, 2);

        if(distanceToA < distanceToB)
            return pointA;

        else
            return pointB;
    }

    public Point getLongerPoint(Point pointA, Point pointB){

        double distanceToA, distanceToB;

//        distanceToA = this.calculateDistance(pointA);
//        distanceToB = this.calculateDistance(pointB);

        distanceToA = Math.pow(this.xValue - pointA.xValue, 2)
                     +Math.pow(this.yValue - pointA.yValue, 2);

        distanceToB = Math.pow(this.xValue - pointB.xValue, 2)
                     +Math.pow(this.yValue - pointB.yValue, 2);

        if(distanceToA > distanceToB)
            return pointA;

        else
            return pointB;
    }

    public double calculateDotProduct(Point pointA, Point pointB){

        Vector vecterToA = new Vector(pointA.xValue - this.xValue , pointA.yValue - this.yValue);
        Vector vecterToB = new Vector(pointB.xValue - this.xValue, pointB.yValue - this.yValue);

        return vecterToA.dotProduct(vecterToB);
    }


}
