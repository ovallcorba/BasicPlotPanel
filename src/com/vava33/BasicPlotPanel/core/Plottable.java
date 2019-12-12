package com.vava33.BasicPlotPanel.core;
/**
 * Interface for a plottable object (containing plottable_points)
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;
import java.util.List;

public interface Plottable<T extends Plottable_point> {

    public List<T> getPoints();
    public void setPoints(List<T> punts);
    public void addPoint(T pp);
    public T getRawPoint(int index); //RETORNA EL PUNT TAL QUAL -- COMPTE
    public Plottable_point getCorrectedPoint(int index, double incX, double incY, double incZ, double scaleFactorY, boolean addYBkg); //CREA NOU PUNT amb correccions manuals
    public Plottable_point getCorrectedPoint(int index, boolean addYBkg); //CREA NOU PUNT amb les correccions que estiguin ja al Plottable
    public T getClosestPointX(double xval, double tol); //gets the point closest to xval as long it is inside tolerance, otherwise null
    public int getNPoints();
    public boolean isEmpty();
    
    //parametres "visuals"
    public String getName();
    public void setName(String name);
    public boolean isPlotThis();
    public void setPlotThis(boolean plotit);
    public Color getColor();
    public void setColor(Color c);
    public double[] getPuntsMaxXMinXMaxYMinY();
    public float getLineWidth();
    public float getMarkerSize();
    public void setLineWidth(float value);
    public void setMarkerSize(float value);
    public SerieType getSerieType();
    public void setSerieType(SerieType st);
    public double getToleranceEquals(); //to compare coordinates of two points (only x and y)
    public void setToleranceEquals(double tol);
    public boolean isContinuous(); //NO continua vol dir que no s'ha de fer linia si no hi ha un punt a distancia stepsize
    public double getToleranceContinuous();
    public void setToleranceContinuous(double tol);
    public boolean isShowInLegend();
    public void setShowInLegend(boolean show);
    public boolean isShowErrBars();
    public void setShowErrBars(boolean showErrBars);

        //correccions
    public double getYOffset();
    public void setYOffset(double yoff);
    public double getXOffset();
    public void setXOffset(double xoff);
    public double getZOffset();
    public void setZOffset(double zoff);
    public double getScaleY();
    public void setScaleY(double scaleY);
}
