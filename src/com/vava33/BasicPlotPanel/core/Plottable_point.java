package com.vava33.BasicPlotPanel.core;
/**
 * Plottable point interface
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

public interface Plottable_point extends Comparable<Plottable_point>{
    public double getX();
    public double getY();
    public double getZ(); //Z will be a third value assigned to a point
    public double getYbkg(); //si volem separar la Y del fons
    public double getSdy();
    public void setX(double x);
    public void setY(double y);
    public void setZ(double z);
    public void setYbkg(double ybkg);
    public void addY(double y);
    public void setSdy(double sdy);
    public String getInfo(); //with useful info of the point (e.g. hkl)
    public String getLabel(); //if we want to print a label next to peaks
    public void setLabel(String label);
    public Plottable_point getScaledInY(float factor);
    public Plottable_point getCorrectedDataPoint(double incX, double incY, double incZ, double factorY);
    public Plottable_point getCorrectedDataPoint(double incX, double incY, double incZ, double factorY, boolean addYBkg);
    public Plottable<? extends Plottable_point> getPlottable(); //al qual pertany
    public void setPlottable(Plottable<? extends Plottable_point> p); //al qual pertany
}
