package com.vava33.BasicPlotPanel;

/**
 * Basic implementation of a Plottable_point
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import org.apache.commons.math3.util.FastMath;

import com.vava33.ovPlot.Plottable;
import com.vava33.ovPlot.Plottable_point;

public class BasicPoint implements Plottable_point{
    private double xvalue;
    private double yvalue;
    private double ybkgvalue;
    private double zvalue;
    private double sdyvalue;
    private String label;
    private Plottable parent;
    
    public BasicPoint(double x, double y, Plottable parent) {
        this(x,y,0,0,parent);
    }
    
    public BasicPoint(double x, double y, double z, double sdy, Plottable parent) {
        this(x,y,z,sdy,"",parent);
    }
    
    public BasicPoint(double x, double y, double z, double sdy, String label, Plottable parent) {
        this(x,y,0,z,sdy,label,parent);
    }
    
    public BasicPoint(double x, double y, double ybkg, double z, double sdy, String label, Plottable parent) {
        this.xvalue=x;
        this.yvalue=y;
        this.ybkgvalue=ybkg;
        this.parent=parent;
        this.zvalue=z;
        this.sdyvalue=sdy;
        this.label=label;
    }
    
    public BasicPoint(Plottable_point pp) {
        this(pp.getX(),pp.getY(),pp.getYbkg(),pp.getZ(),pp.getSdy(),pp.getLabel(),pp.getPlottable());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Plottable_point o) {
        if ((FastMath.abs(xvalue-o.getX())<parent.getToleranceEquals())&&(FastMath.abs(yvalue-o.getY())<parent.getToleranceEquals())) {
            return 0;
        }
        if (this.xvalue>o.getX()) {
            return 1; //otherDP va primer
        }else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) { //ONLY X
        BasicPoint dp = (BasicPoint)obj;
        if ((FastMath.abs(xvalue-dp.getX())<parent.getToleranceEquals())) {
            return true;
        }else{
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getX()
     */
    @Override
    public double getX() {
        return this.xvalue;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getY()
     */
    @Override
    public double getY() {
        return this.yvalue;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getZ()
     */
    @Override
    public double getZ() {
        return this.zvalue;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getSdy()
     */
    @Override
    public double getSdy() {
        return this.sdyvalue;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#setX(double)
     */
    @Override
    public void setX(double x) {
        this.xvalue=x;
        
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#setY(double)
     */
    @Override
    public void setY(double y) {
        this.yvalue=y;
        
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#setZ(double)
     */
    @Override
    public void setZ(double z) {
        this.zvalue=z;
        
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#addY(double)
     */
    @Override
    public void addY(double y) {
        this.yvalue=this.yvalue+y;
        
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#setSdy(double)
     */
    @Override
    public void setSdy(double sdy) {
        this.sdyvalue=sdy;
        
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getInfo()
     */
    @Override
    public String getInfo() {
        return String.format("[%.5f,%.5f]",xvalue,yvalue);
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getScaledInY(float)
     */
    @Override
    public Plottable_point getScaledInY(float factor) {
        return this.getCorrectedDataPoint(0, 0, 0, factor);
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getParent()
     */
    @Override
    public Plottable getPlottable() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getCorrectedDataPoint(double, double, double)
     */
    @Override
    public Plottable_point getCorrectedDataPoint(double incX, double incY, double incZ, double factorY) {
        return this.getCorrectedDataPoint(incX, incY, incZ, factorY,false);
    }

    @Override
    public Plottable_point getCorrectedDataPoint(double incX, double incY, double incZ, double factorY,
            boolean addYBkg) {
        Plottable_point p =  new BasicPoint(this);
        p.setX(this.getX()+incX);
        if (addYBkg) {
            p.setY(this.getY()*factorY+incY+this.getYbkg()*factorY);
        }else {
            p.setY(this.getY()*factorY+incY);    
        }
        p.setZ(this.getZ()+incZ);
        p.setSdy(this.getSdy()*factorY);
        return p;
    }
    
    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }

    /* (non-Javadoc)
     * @see com.vava33.ovPlot.Plottable_point#setLabel(java.lang.String)
     */
    @Override
    public void setLabel(String label) {
        this.label=label;
        
    }

    @Override
    public double getYbkg() {
        return this.ybkgvalue;
    }

    @Override
    public void setYbkg(double ybkg) {
        this.ybkgvalue=ybkg;
        
    }
}
