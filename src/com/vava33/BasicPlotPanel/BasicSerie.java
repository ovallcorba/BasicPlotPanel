package com.vava33.BasicPlotPanel;

/**
 * Basic implementation of a Plottable object
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import com.vava33.BasicPlotPanel.core.Plottable;
import com.vava33.BasicPlotPanel.core.Plottable_point;
import com.vava33.BasicPlotPanel.core.SerieType;


public class BasicSerie<T extends Plottable_point> implements Plottable<T>{
    
    public static float def_lineWidth = 1.2f;
    public static float def_markerSize = 0.0f;
    public static int def_hklticksize=12;
    public static int def_hklYOff=-16; //ho està a les opcions
    
    private double tolEquals=0.001;
    private double tolContinuous=Double.MAX_VALUE; //si dist_entre_dos_punts>tolContinuous es considera NO continu per dibuixar linia
    
    private List<T> points;
    private String serieName;
    private boolean plotThis;
    private float lineWidth;
    private float markerSize;
    private double yOffset;
    private double xOffset;
    private double zOffset;
    private double scaleY;
    private Color color;
    private SerieType stype;
    private boolean continuous;
    private boolean showInLegend;
    private boolean showErrBars;
    
    //creates a XY serie
    public BasicSerie(String name) {
        this(name, SerieType.dat);
    }
    
    //creates a SerieType serie
    public BasicSerie(String name, SerieType st) {
            points = new ArrayList<T>();
            this.serieName = name;
            this.plotThis=true;
            this.continuous=true;
            this.showInLegend=true;
            this.xOffset=0;
            this.yOffset=0;
            this.zOffset=0;
            this.scaleY=1.0;
            this.showErrBars=false;
            this.setSerieType(st); //color, marker, line i scaleY/offset per hkl
    }
    
    public BasicSerie(BasicSerie<T> inds, SerieType tipusSerie, boolean copyIntensities) {
        this(inds,new ArrayList<T>());
        this.setSerieType(tipusSerie);
        if (copyIntensities)this.copypoints(inds);
    }
    
    public BasicSerie(BasicSerie<T> inds, List<T> punts) {
        this(inds.serieName,inds.stype);
        this.points=punts;
        this.scaleY=inds.getScaleY();
        this.xOffset=inds.getXOffset();
        this.yOffset=inds.getYOffset();
        this.color=inds.color;
        this.markerSize=inds.markerSize;
        this.lineWidth=inds.lineWidth;
        this.serieName=inds.serieName;
    }
    
    //COPIEM (duplica)
    public void copypoints(Plottable<T> inDS) {
        this.points=new ArrayList<T>(inDS.getPoints());
    }

    public void copyVisualParameters(BasicSerie<T> otherSerie) {
        this.color=otherSerie.color;
        this.lineWidth=otherSerie.lineWidth;
        this.markerSize=otherSerie.markerSize;
    }
    
    public int getIndexOfDP(T dp){
        return this.points.indexOf(dp);
    }
    
    public void addYToDataPoint(int index, double y) {
        this.points.get(index).addY(y);
    }

    public void setYToDataPoint(int index, double y, double sdy) {
        this.points.get(index).setY(y);
        this.points.get(index).setSdy(sdy);
    }
    
    public void resetYtoZeroAllPoints() {
        for (int i=0;i<this.getNPoints();i++){
            this.setYToDataPoint(i, 0, 0);
        }
    }

    public void clearPoints() {
        this.points.clear();
    }
    
    public void sortPoints() {
        Collections.sort(points);
    }
    
    public void removePoint(T dp){
        this.points.remove(dp);
    }

    public void removePoint(int index){
        this.points.remove(index);
    }
    
    public static boolean containNansOrInf(List<Plottable_point> data) {
        for (Plottable_point p:data) {
            if (Double.isInfinite(p.getX()))return true;
            if (Double.isNaN(p.getX()))return true;
        }
        return false;
    }
    
    public Plottable_point[] getSurroundingPoints(double xvalue){
        
        for (int i=0;i<this.getNPoints()-1;i++){
            if ((this.getCorrectedPoint(i,false).getX()<=xvalue) && (this.getCorrectedPoint(i+1,false).getX()>=xvalue)){
                Plottable_point[] dps = {this.getCorrectedPoint(i,false), this.getCorrectedPoint(i+1,false)};
                return dps;
            }
        }
        return null;
    }
    
    
    //return Y
    public double interpolateY(double xval, Plottable_point dp1, Plottable_point dp2){
        double yleft = dp1.getY();
        double yright = dp2.getY();
        double xleft = dp1.getX();
        double xright = dp2.getX();
        
        double pen = (yright-yleft)/(xright-xleft);
        double ord = pen*(xleft)*-1+yleft;
        return pen*xval + ord;
    }
    //return SDY
    public double interpolateSDY(double xval, Plottable_point dp1, Plottable_point dp2){
        double yleft = dp1.getSdy();
        double yright = dp2.getSdy();
        double xleft = dp1.getX();
        double xright = dp2.getX();
        
        double pen = (yright-yleft)/(xright-xleft);
        double ord = pen*(xleft)*-1+yleft;
        return pen*xval + ord;
    }
    
    public double calcStep(){
        return (this.getMaxX() - this.getMinX())/(this.getNPoints()-1);
    }

    //TODO: MIN i MAX podrien no estar ordenats!!
    public double getMinX() {
        return this.getCorrectedPoint(0,false).getX();
    }

    public double getMaxX() {
        return this.getCorrectedPoint(points.size()-1,false).getX();
    }
    
    public List<Plottable_point> getClosestPointsToAGivenX(double centralX, double tol){
        if (tol<0) tol = 0.025;
        List<Plottable_point> found = new ArrayList<Plottable_point>();
        for (int i=0;i<this.getNPoints();i++){
            if (FastMath.abs(centralX-this.getCorrectedPoint(i,false).getX())<tol){
                found.add(this.getCorrectedPoint(i,false));
            }
        }
        return found;
    }
    
    //+-halftol
    public int[] getMaxMinIndicesDataPointsRange(double centralX, double halfrange) {
        double minval = centralX-halfrange;
        T p = this.getClosestPointX(minval, -1);
        int min = 0;
        if (p!=null) min = this.getIndexOfDP(p);
        double maxval = centralX+halfrange;
        p = this.getClosestPointX(maxval, -1);
        int max = this.getNPoints()-1;
        if (p!=null) max = this.getIndexOfDP(p);
        return new int[] {min,max};
    }

    public void normalizeIntensitiesToValue(double value) {
        //calculem el factor de normalitzacio de les intensitats a value
        double maxInten = -1;
        for (Plottable_point dp:points) {
            double inten=dp.getY();
            if (inten>maxInten)maxInten=inten;
        }
        double factor = value/maxInten;
        //i corregim intensitats
        for (Plottable_point dp:points) {
            dp.setY(dp.getY()*factor);
        }
    }
    
    public double[] getXasDoubleArray() {
        double[] d = new double[this.getNPoints()];
        for (int i=0;i<this.getNPoints();i++) {
            d[i]=this.getCorrectedPoint(i, false).getX();
        }
        return d;
    }
    
    public double[] getYasDoubleArray() {
        double[] d = new double[this.getNPoints()];
        for (int i=0;i<this.getNPoints();i++) {
            d[i]=this.getCorrectedPoint(i, false).getY();
        }
        return d;
    }
    
    public Plottable_point getMinYDataPoint(int puntIni, int puntFin, boolean takeIntoAccountYBkg){
        double ymin = Double.MAX_VALUE;
        Plottable_point dpMin = null;
        for (int i=puntIni;i<=puntFin;i++){
            Plottable_point dp = this.getCorrectedPoint(i,takeIntoAccountYBkg); //podria ser true...
            if (dp.getY()<ymin){
                ymin=dp.getY();
                dpMin = dp;
            }
        }
        return dpMin;
    }
    
    //returns the closest DP to the one entered (usually by clicking)
    public Plottable_point getClosestPointXY(Plottable_point click, double tolX, double tolY, boolean takeIntoAccountYBkg){
        if (tolX<0)tolX=1.0;
        if (tolY<0)tolY=5000;
        Plottable_point closest = null;
        double minDiffX = Double.MAX_VALUE/2.5;
        double minDiffY = Double.MAX_VALUE/2.5;
        for (int i=0; i<this.getNPoints();i++){
            Plottable_point dp = this.getCorrectedPoint(i,takeIntoAccountYBkg);
            double diffX = FastMath.abs(dp.getX()-click.getX());
            double diffY = FastMath.abs(dp.getY()-click.getY());
            if ((diffX<tolX)&&(diffY<tolY)){
                if ((diffX+diffY)<(minDiffX+minDiffY)){
                    minDiffX=diffX;
                    minDiffY=diffY;
                    closest = dp;
                }
            }
        }
        return closest;
    }
    
    protected double[] getListXvaluesCorrected() {
        double[] xvals = new double[this.getNPoints()];
        for (int i=0;i<this.getNPoints();i++) {
            xvals[i]=this.getCorrectedPoint(i,false).getX();
            
        }
        return xvals;
    }

    public double[] calcYmeanYDesvYmaxYmin(boolean takeIntoAccountYBkg){
        int puntIni = 0;
        int puntFin = this.getNPoints()-1;
        return calcYmeanYDesvYmaxYmin(puntIni,puntFin,takeIntoAccountYBkg);
    }
    
    //punt Fin està inclos
    public double[] calcYmeanYDesvYmaxYmin(int puntIni, int puntFin, boolean takeIntoAccountYBkg){
        double[] ymean_ydesv_ymax_ymin = new double[4];
        ymean_ydesv_ymax_ymin[2] = Double.MIN_VALUE;
        ymean_ydesv_ymax_ymin[3] = Double.MAX_VALUE;
        int npoints = FastMath.abs(puntFin - puntIni + 1);
        double sumY = 0;
        for (int i=puntIni;i<=puntFin;i++){
            Plottable_point dp = this.getCorrectedPoint(i,takeIntoAccountYBkg);
            sumY = sumY + dp.getY();
            if (dp.getY()<ymean_ydesv_ymax_ymin[3])ymean_ydesv_ymax_ymin[3]=dp.getY();
            if (dp.getY()>ymean_ydesv_ymax_ymin[2])ymean_ydesv_ymax_ymin[2]=dp.getY();
        }
        ymean_ydesv_ymax_ymin[0] = sumY/npoints;
        //ara desviacio
        sumY=0;
        for (int i=puntIni;i<=puntFin;i++){
            Plottable_point dp = this.getCorrectedPoint(i,takeIntoAccountYBkg);
            sumY = sumY + (dp.getY()-ymean_ydesv_ymax_ymin[0])*(dp.getY()-ymean_ydesv_ymax_ymin[0]);
        }
        ymean_ydesv_ymax_ymin[1] = FastMath.sqrt(sumY/(npoints-1));
        return ymean_ydesv_ymax_ymin;
    }
    
//////GENERAL IMPLEMENTATIONS OF THE INTEFACE
    
    @Override
    public void setSerieType(SerieType st) {
        this.stype = st;
        this.color=getDefColor(st);
        this.lineWidth=getDefLineWidth(st);
        this.markerSize=getDefMarkerSize(st);
        if (st==SerieType.hkl) {
            this.setScaleY(def_hklticksize);
            this.setYOffset(def_hklYOff);
        }
    }

    @Override
    public List<T> getPoints() {
        return points;
    }

    @Override
    public void addPoint(T pp) {
        pp.setPlottable(this);
        this.points.add(pp);
    }

    @Override
    public T getRawPoint(int index) {
        return points.get(index);
    }

    @Override
    public int getNPoints() {
        return points.size();
    }

    @Override
    public String getName() {
        return this.serieName;
    }

    @Override
    public void setName(String name) {
        this.serieName=name;
    }

    @Override
    public boolean isPlotThis() {
        return this.plotThis;
    }

    @Override
    public void setPlotThis(boolean plotit) {
        this.plotThis=plotit;
    }

    @Override
    public boolean isEmpty() {
        if (this.getNPoints()<=0)return true;
        return false;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color c) {
        this.color=c;
    }

    @Override
    public double[] getPuntsMaxXMinXMaxYMinY() {
        if (points!=null){
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int i=0;i<points.size();i++){
                Plottable_point punt = this.getCorrectedPoint(i,true); //true perque busquem el maxim/minim Y cal considerar-ho
                if (punt.getX() < minX){
                    minX = punt.getX();
                }
                if (punt.getX() > maxX){
                    maxX = punt.getX();
                }
                if (punt.getY() < minY){
                    minY = punt.getY();
                }
                if (punt.getY() > maxY){
                    maxY = punt.getY();
                }
            }
            return new double[]{maxX,minX,maxY,minY};
        }else{
            return null;
        }
    }

    @Override
    public float getLineWidth() {
        return this.lineWidth;
    }

    @Override
    public float getMarkerSize() {
        return this.markerSize;
    }

    @Override
    public void setLineWidth(float value) {
        this.lineWidth=value;
    }

    @Override
    public void setMarkerSize(float value) {
        this.markerSize=value;
    }

    @Override
    public SerieType getSerieType() {
        return this.stype;
    }

    @Override
    public double getToleranceEquals() {
        return tolEquals;
    }

    @Override
    public void setToleranceEquals(double tol) {
        tolEquals=tol;
    }

    @Override
    public boolean isContinuous() {
        return continuous;
    }

    @Override
    public double getToleranceContinuous() {
        return tolContinuous;
    }

    @Override
    public void setToleranceContinuous(double tol) {
        tolContinuous=tol;
    }

    @Override
    public T getClosestPointX(double xval, double tol) { //TODO probably it is slow...
        T found = null;
        if (tol<1)tol = 0.2;
        double mindist=tol;
        for (T pp:points) {
            double diff = FastMath.abs(pp.getX()-xval);
            if (diff<mindist){
                found = pp;
                mindist=diff;
            }
        }
        return found;
    }

    @Override
    public boolean isShowInLegend() {
        return showInLegend;
    }

    @Override
    public void setShowInLegend(boolean show) {
        showInLegend=show;
        
    }

    @Override
    public Plottable_point getCorrectedPoint(int index, double incX, double incY, double incZ, double scaleFactorY, boolean addYBkg) {
        return this.points.get(index).getCorrectedDataPoint(incX,incY,incZ,scaleFactorY,addYBkg);
    }

    @Override
    public Plottable_point getCorrectedPoint(int index, boolean addYBkg) {
        return this.points.get(index).getCorrectedDataPoint(this.xOffset, this.yOffset, this.zOffset, this.scaleY, addYBkg);
    }

    @Override
    public double getYOffset() {
        return yOffset;

    }

    @Override
    public void setYOffset(double yoff) {
        this.yOffset=yoff;
        
    }

    @Override
    public double getXOffset() {
        return xOffset;

    }

    @Override
    public void setXOffset(double xoff) {
        this.xOffset=xoff;
        
    }

    @Override
    public double getZOffset() {
        return zOffset;
    }

    @Override
    public void setZOffset(double zoff) {
        this.zOffset=zoff;
        
    }

    @Override
    public double getScaleY() {
        return scaleY;
    }

    @Override
    public void setScaleY(double scaleY) {
        this.scaleY=scaleY;        
    }

    @Override
    public boolean isShowErrBars() {
        return showErrBars;
    }

    @Override
    public void setShowErrBars(boolean showErrBars) {
        this.showErrBars = showErrBars;
    }

    @Override
    public void setPoints(List<T> punts) {
        this.points=punts;
    }

    public static Color getDefColor(SerieType st) {
        switch (st) {
        case peaks:
            return Color.GRAY;
        case bkg:
            return Color.PINK;
        case bkgEstimP:
            return Color.PINK;
        case obs:
            return Color.RED;
        case cal:
            return Color.BLACK;
        case diff:
            return Color.BLUE;
        case hkl:
            return Color.GREEN.darker();
        case ref:
            return Color.GRAY;
        default:
            return Color.BLACK;
        }
    }
    
    public static float getDefMarkerSize(SerieType st) {
        switch (st) {
        case peaks:
            return 0;
        case bkg:
            return 0;
        case bkgEstimP:
            return def_markerSize+10;
        case obs:
            return 4;
        case cal:
            return 0;
        case diff:
            return 0;
        case hkl:
            return 0;
        case ref:
            return 0;
        default:
            return def_markerSize;
        }
    }

    public static float getDefLineWidth(SerieType st) {
        switch (st) {
        case peaks:
            return def_lineWidth+1f;
        case bkg:
            return def_lineWidth+1f;
        case bkgEstimP:
            return 0;
        case obs:
            return 0;
        case diff:
            return def_lineWidth-0.5f;
        default: //INCLUDES DAT
            return def_lineWidth;
        }
    }
    
    //Si s'ha de contar o no al fer repaints de color
    public static boolean isPaintedType(SerieType st) {
        switch (st) {
        case dat:
            return true;
        case gr:
            return true;
        default:
            return false;
        }
    }
    
    public static List<SerieType> getPaintedTypes() {
        List<SerieType> stpainted = new ArrayList<SerieType>();
        for (SerieType st:SerieType.values()) {
            if (isPaintedType(st))stpainted.add(st);
        }
        return stpainted;
    }


}
