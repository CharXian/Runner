package com.example.xian.requestlocationandshow;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/6/9.
 */

public class OffTeamAlgorithm {

    Point userPoint, basePoint, linePoint;
    List<Point> listOfPoints = new ArrayList<Point>();
    double shortestDistance = 999999, calculatedDistance, checkedDistance;

    public boolean isOutOfTeam(LatLng userLocation, List<LatLng> listOfLocations, double checkedValue){

        userPoint = new Point(userLocation.latitude, userLocation.longitude);
        checkedDistance = checkedValue;

        for (LatLng location: listOfLocations) {

            listOfPoints.add(new Point(location.latitude, location.longitude));
        }

        for (int i = 0; i< listOfPoints.size() - 1; i++){

            basePoint = userPoint.getShorterPoint(listOfPoints.get(i), listOfPoints.get(i+1));
            linePoint = userPoint.getLongerPoint(listOfPoints.get(i), listOfPoints.get(i+1));

            if (basePoint.calculateDotProduct(userPoint, linePoint) >= 0)
                calculatedDistance = distanceBetweenPointToLine(userPoint, basePoint, linePoint);

            else
                calculatedDistance = userPoint.calculateDistance(basePoint);

            if(calculatedDistance < shortestDistance)
                shortestDistance = calculatedDistance;
        }

        if (shortestDistance > checkedDistance)
            return true;

        else
            return false;
    }

    private double distanceBetweenPointToLine(Point userPoint, Point basePoint, Point linePoint){

        Vector vectorBU = new Vector(userPoint.getxValue()-basePoint.getxValue(),
                                     userPoint.getyValue()-basePoint.getyValue());

        Vector vectorBL = new Vector(linePoint.getxValue()-basePoint.getxValue(),
                                     linePoint.getyValue()-basePoint.getyValue());

        return vectorBU.crossArea(vectorBL) / vectorBL.getLength();
    }

}
