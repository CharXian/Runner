package com.example.xian.requestlocationandshow;

/**
 * Created by xian on 2017/6/9.
 */

public class Vector {

    private double componentX, componentY, length;

    public Vector(double xValue, double yValue){

        this.componentX = xValue;
        this.componentY = yValue;
        this.length = Math.sqrt(Math.pow(this.componentX, 2) + Math.pow(this.componentY, 2));
    }

    public double dotProduct(Vector anotherVecter){

        return this.componentX*anotherVecter.componentX + this.componentY*anotherVecter.componentY;
    }

    public double crossArea(Vector anotherVector){

        return Math.abs(this.componentX*anotherVector.componentY - anotherVector.componentX*this.componentY);
    }

    public double getLength(){

        return this.length;
    }
}
