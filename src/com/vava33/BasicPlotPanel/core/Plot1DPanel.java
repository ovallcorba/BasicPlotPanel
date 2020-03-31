package com.vava33.BasicPlotPanel.core;
/**
 *
 * Panell de representació grafica dels Plottables (a traves de DataToPlot).
 * 
 * Plot1DPanel treu la info del què ha de representar de DataToPlot
 * 
 * ATENCIO els setters (simples) NO ACTUALITZEN, s'ha de fer apart (per si cal canviar molts valors alhora)
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public abstract class Plot1DPanel<T extends DataToPlot<?>> extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final int minWidth = 300;
    private static final int minHeight = 300;
    
    private VavaLogger log;
    
    private int panelW, panelH;
    private boolean customCanvasSize = false;
    private int customCanvasWidth = 0;
    private int customCanvasHeight = 0;
    
    private boolean saveTransp = false;

    private DecimalFormat def_xaxis_format = FileUtils.dfX_3;
    private DecimalFormat def_yaxis_format = FileUtils.dfX_1;

    //TEMES (aquests son final de moment perquè no els modifico enlloc)
    private static final Color Dark_bkg = Color.BLACK;
    private static final Color Dark_frg = Color.WHITE;
    private static final Color Light_bkg = Color.WHITE;
    private static final Color Light_frg = Color.BLACK;
    private static final Color Light_Legend_line = Color.BLACK;
    private static final Color Dark_Legend_line = Color.WHITE;
    private static final Color Light_Legend_bkg = Color.WHITE;
    private static final Color Dark_Legend_bkg = Color.BLACK;

    private int gapAxisTop = 0; //12
    private int gapAxisBottom = 0; //35
    private int gapAxisRight = 0; //12
    private int gapAxisLeft = 0; //80
    private int padding5px = 5;

    public Rectangle2D.Double zoomRect;
    public boolean mouseBox = false;

    //////parametres que es poden configurar/modificar
    private boolean lightTheme = true;
    private int def_nDecimalsX = 3;
    private int def_nDecimalsY = 1;
    private String xlabel = "X";
    private String ylabel = "Y";
    private String plotTitle = "";
    private boolean verticalYlabel = false;
    private boolean verticalYAxe = true;
    private boolean showLegend = true;
    private boolean autoPosLegend = true;
    private int legendX = -99;
    private int legendY = -99;
    private boolean legend_opaque = true;
    private boolean showGridY = false;
    private boolean showGridX = false;
    private boolean applyScaleFactorT2 = false;
    private boolean showPointLabels = false;
    private boolean showSerieNameInGraph = false;
    private int serieNameOffsetPixY = 0;
    private int serieNameOffsetPixX = 0;
    private boolean customXtitle = false;
    private float scaleFactorT2ang = 30;
    private float scaleFactorT2fact = 1;
    private boolean hkllabels = true;
    private boolean plotwithbkg = true; //prf
    private boolean showDBCompoundIntensity = false; //HAURIA DE DIR REF I NO DB QUE CONFON... ja que es per totes les REF per aixo està aqui
    private boolean splitDBCompound = false;

    //CONVENI DEFECTE:
    //cada 100 pixels una linia principal i cada 25 una secundaria
    //mirem l'amplada/alçada del graph area i dividim per tenir-ho en pixels        
    private double incXPrimPIXELS = 100;
    private double incXSecPIXELS = 25;
    private double incYPrimPIXELS = 100;
    private double incYSecPIXELS = 25;
    private int div_PrimPixSize = 8; //originalment eren 8 i 4, ho faig imparell perquè hi hagi pixel "central"
    private int div_SecPixSize = 4;
    private double facZoom = 1.1f;
    
    //LINIES DIVISIO
    private double div_incXPrim, div_incXSec, div_incYPrim, div_incYSec;
    private double div_startValX, div_startValY;
    private boolean fixAxes = false;
    private boolean negativeYAxisLabels = true;

    //sizes relative to default one (12?)
    private float def_axis_fsize = 0.f;
    private float def_axisL_fsize = 0.f;
    private float def_legend_fsize = 0.f;

    private double xMin = 0;
    private double xMax = 60;
    private double yMin = 0;
    private double yMax = 100000;
    private double scalefitX = -1;
    private double scalefitY = -1;
    private double xrangeMin = -1; //rangs dels eixos X i Y en 2theta/counts per calcular scalefitX,Y
    private double xrangeMax = -1;
    private double yrangeMin = -1;
    private double yrangeMax = -1;

    private int nrefseries = 0; //for plotting as phase ID split
    
    private T dataToPlot;
    private Plot1DFrontend frontend; //alerta, pot ser null..
    
    
    public Plot1DPanel(T data, VavaLogger logger) {
        super();
        if (data!=null) { //TODO revisar, this if for compatibility with windowbuilder
            this.dataToPlot=data;
            this.dataToPlot.setPlotPanel(this);
        }
        log=logger;
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        this.setDoubleBuffered(true);
        this.setDecimalsXaxis(def_nDecimalsX);
        this.setDecimalsYaxis(def_nDecimalsY);
        div_incXPrim = 0;
        div_incXSec = 0;
        div_incYPrim = 0;
        div_incYSec = 0;
        div_startValX = 0;
        div_startValY = 0;
    }

    public void setFrontEnd(Plot1DFrontend fend) {
        this.frontend=fend;
    }
    
/////METODES PREPARACIO PLOT -- CONVERSIO DADES/PANTALLA(frame)
    
    public T getDataToPlot() {
        return dataToPlot;
    }
    
    public boolean arePlottables() {
        return dataToPlot.arePlottables();
    }
    
    public void actualitzaPlot() {
        this.repaint(); //TODO es poden afegir altres coses si es vol, per aixo he fet un metode dedicat
    }
    
    // ajusta la imatge al panell, mostrant-la tota sencera (calcula l'scalefit inicial)
    public void fitGraph() {
        this.resetView(false);
    }
    
    private void resetView(boolean resetAxes) {
        if (dataToPlot.arePlottables()) {
            this.calcMaxMinXY();
            this.xrangeMax=this.xMax;
            this.xrangeMin=this.xMin;
            this.yrangeMax=this.yMax;
            this.yrangeMin=this.yMin;

            this.calcScaleFitX();
            this.calcScaleFitY();

            if (!checkIfDiv() || resetAxes){
                this.autoDivLines(false);
            }

            this.actualitzaPlot();
        }
    }
    
    //CAL COMPROVAR QUE ESTIGUI DINS DEL RANG PRIMER I CORREGIR l'OFFSET sino torna NULL
    private Point2D.Double getFramePointFromDataPoint(Plottable_point dpoint){
          return new Point2D.Double(this.getFrameXFromDataPointX(dpoint.getX()),this.getFrameYFromDataPointY(dpoint.getY()));
            
    }
    
    private double getFrameXFromDataPointX(double xdpoint){
          double xfr = ((xdpoint-this.xrangeMin) * this.scalefitX) + gapAxisLeft;
          return xfr;    
    }
    
    private double getFrameYFromDataPointY(double ydpoint){
        double yfr = this.getHeight()-(((ydpoint-this.yrangeMin) * this.scalefitY) + gapAxisBottom);
        return yfr;    
  }
    

    public Point2D.Double getDataPointFromFramePoint(Point2D.Double framePoint){
        if (isFramePointInsideGraphArea(framePoint)){
            double xdp = ((framePoint.x - gapAxisLeft) / this.scalefitX) + this.xrangeMin;
            double ydp = (-framePoint.y+this.getHeight()-gapAxisBottom)/this.scalefitY +this.yrangeMin;
            return new Point2D.Double(xdp,ydp);
        }else{
            return null;
        }
    }
    
    private Point2D.Double getDataPointFromFramePointIgnoreIfInside(Point2D.Double framePoint){
        double xdp = ((framePoint.x - gapAxisLeft) / this.scalefitX) + this.xrangeMin;
        double ydp = (-framePoint.y+this.getHeight()-gapAxisBottom)/this.scalefitY +this.yrangeMin;
        return new Point2D.Double(xdp,ydp);
}
    
    //ens diu quant en unitats de X val un pixel (ex 1 pixel es 0.01deg de 2th)
    public double getXunitsPerPixel(){
        return (this.xrangeMax-this.xrangeMin)/this.getRectangleGraphArea().width;
    }
    
    //ens dira quant en unitats de Y val un pixels (ex. 1 pixel son 1000 counts)
    private double getYunitsPerPixel(){
        return (this.yrangeMax-this.yrangeMin)/this.getRectangleGraphArea().height;
    }
    
    private boolean isFramePointInsideGraphArea(Point2D.Double p){
        Rectangle2D.Double r = getRectangleGraphArea();
        return r.contains(p);
    }
    
    private boolean isFramePointInsideXGraphArea(double px) {
        double x_low = gapAxisLeft;
        double x_high = x_low + this.calcPlotSpaceX();
        if ((px>x_low)&&(px<x_high))return true;
        return false;
    }
    
    public boolean isXValueInsideGraphArea(double xvalue) {
        return isFramePointInsideXGraphArea(this.getFrameXFromDataPointX(xvalue));
    }
    
    public Rectangle2D.Double getRectangleGraphArea(){
        double xtop = gapAxisLeft;
        double ytop = gapAxisTop;
        return new Rectangle2D.Double(xtop,ytop,calcPlotSpaceX(),calcPlotSpaceY());
    }
    
    //it returns the same value if it is inside the graph area, otherwise it returns the closest point on the border
    public double checkFrameXValue(double xval){
        Rectangle2D.Double rect = this.getRectangleGraphArea();
        if (xval>rect.getMinX() && xval<rect.getMaxX()){ //no poso <= >= per evitar anar justos...
            return xval;
        }else{
            if (xval<rect.getMinX())return rect.getMinX()+1;
            if (xval>rect.getMaxX())return rect.getMaxX()-1;
        }
        return xval;
    }
    
    //it returns the same value if it is inside the graph area, otherwise it returns the closest point on the border
    public double checkFrameYValue(double yval){
        Rectangle2D.Double rect = this.getRectangleGraphArea();
        if (yval>rect.getMinY() && yval<rect.getMaxY()){ //no poso <= >= per evitar anar justos...
            return yval;
        }else{
            if (yval<rect.getMinY())return rect.getMinY()+1;
            if (yval>rect.getMaxY())return rect.getMaxY()-1;
        }
        return yval;
    }
    
    //NOMES S'HAURIA DE CRIDAR QUAN OBRIM UN PATTERN (per aixo private) ..  el useRange es per quan cridem des de fixAxes
    private void autoDivLines(boolean useRange){
        
        //Aqui hauriem de posar divisions tal com volem des de MIN a MAX (ignorant finestra), després ja mostrarem la zona d'interès.
        
        if (useRange) {
            this.div_startValX=this.xrangeMin;
            this.div_startValY=this.yrangeMin;            
        }else { //"normal"
            this.div_startValX=this.xMin;
            this.div_startValY=this.yMin;
        }
        
        //ara cal veure a quan es correspon en les unitats de cada eix -- a la vista actual 
        double xppix = this.getXunitsPerPixel();
        double yppix = this.getYunitsPerPixel();
                
        this.div_incXPrim=getIncXPrimPIXELS()*xppix;
        this.div_incXSec=incXSecPIXELS*xppix;
        this.div_incYPrim=incYPrimPIXELS*yppix;
        this.div_incYSec=incYSecPIXELS*yppix;
        
        if (frontend!=null)frontend.fillDivisionsValues();
        

    }
    
    //valor inicial, valor d'increment per les separacions principals (tindran número), n divisions secundaries entre principals
    //iniVal l'hem suprimit d'aqui, la "finestra" no es responsabilitat d'aquesta funcio
    private void customDivLinesX(double incrPrincipals, double nDivisionsSecund){
        
        this.div_incXPrim=incrPrincipals;
        this.div_incXSec=incrPrincipals/nDivisionsSecund;
        
        if (frontend!=null)frontend.fillDivisionsValues();
   }
    
    private void customDivLinesY(double incrPrincipals, double nDivisionsSecund){

        this.div_incYPrim=incrPrincipals;
        this.div_incYSec=incrPrincipals/nDivisionsSecund;
        
        if (frontend!=null)frontend.fillDivisionsValues();
    }

    public void applyWindowLimitsX(double xmin, double xmax) {
        this.xrangeMin=xmin;
        this.xrangeMax=xmax;
        this.calcScaleFitX();
        this.actualitzaPlot();
    }
    
    public void applyWindowLimits(double xmin, double xmax, double ymin, double ymax) {
        this.xrangeMin=xmin;
        this.xrangeMax=xmax;
        this.yrangeMin=ymin;
        this.yrangeMax=ymax;
        this.calcScaleFitX();
        this.calcScaleFitY();
        this.actualitzaPlot();
    }
    
    public double[] getWindowValues() {
        //minX, maxX, minY, maxY
        return new double[] {xrangeMin,xrangeMax,yrangeMin,yrangeMax};
    }
    
    public void applyDivisions(double iniX, double incX, double ndivX, double iniY, double incY, double ndivY) {
        this.div_startValX=iniX;
        this.div_startValY=iniY;
        this.customDivLinesX(incX, ndivX);
        this.customDivLinesY(incY, ndivY);
        this.actualitzaPlot();
    }
    
    public double[] getDivValues() {
        //minX, maxX, minY, maxY
        return new double[] {div_startValX,div_incXPrim,(int)(div_incXPrim/div_incXSec),div_startValY,div_incYPrim,(int) (div_incYPrim/div_incYSec)};
    }
    
    //ens diu si s'han calculat els limits (o s'han assignat) per les linies de divisio
    private boolean checkIfDiv(){
        if (this.div_incXPrim == 0) return false;
        if (this.div_incXSec == 0) return false;
        if (this.div_incYPrim == 0) return false;
        if (this.div_incYSec == 0) return false;
        return true;
    }
    
    public double[] getScaleFitXY() {
        return new double[] {scalefitX, scalefitY};
    }
    
    public void setScaleFitXY(double scX, double scY) {
        this.scalefitX=scX;
        this.scalefitY=scY;
        this.actualitzaPlot();
    }
    
    
    private void calcMaxMinXY(){
        double maxX = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        boolean thereIsHKL =false;
        int hklsize = 0;
        for(Plottable<? extends Plottable_point> p:dataToPlot.getPlottables()) {
            if (!p.isPlotThis()) continue;
            double[] MxXMnXMxYMnY = p.getPuntsMaxXMinXMaxYMinY();

            if (MxXMnXMxYMnY[0]>maxX) maxX = MxXMnXMxYMnY[0];
            if (MxXMnXMxYMnY[1]<minX) minX = MxXMnXMxYMnY[1];
            if (MxXMnXMxYMnY[2]>maxY) maxY = MxXMnXMxYMnY[2];
            if (MxXMnXMxYMnY[3]<minY) minY = MxXMnXMxYMnY[3];
            
            if (p.getSerieType()==SerieType.hkl) {
                hklsize=(int) p.getScaleY();
                thereIsHKL=true;
            }
        }
        
        if (thereIsHKL) {
            double newYframe = this.getFrameYFromDataPointY(minY)-hklsize;
            double newYdata = this.getDataPointFromFramePointIgnoreIfInside(new Point2D.Double(0, newYframe)).getY();
            minY = FastMath.min(minY, newYdata);
        }
        
        this.xMax=maxX;
        this.xMin=minX;
        this.yMax=maxY;
        this.yMin=minY;
    }
    
    //height in pixels of the plot area
    private double calcPlotSpaceY(){
        return this.getHeight()-gapAxisTop-gapAxisBottom;
    }
    //width in pixels of the plot area
    private double calcPlotSpaceX(){
        return this.getWidth()-gapAxisLeft-gapAxisRight;
    }
    //escala en Y per encabir el rang que s'ha de plotejar
    private void calcScaleFitY(){
        scalefitY = calcPlotSpaceY()/(this.yrangeMax-this.yrangeMin);
    }
    //escala en X per encabir el rang que s'ha de plotejar
    private void calcScaleFitX(){
        scalefitX = calcPlotSpaceX()/(this.xrangeMax-this.xrangeMin);
    }

    // FARE ZOOM NOMES EN Y?
    public void zoomY(boolean zoomIn, Point2D.Double centre) {
        Point2D.Double dpcentre = this.getDataPointFromFramePoint(centre); // miro a quin punt de dades estem fent zoom
        if (dpcentre == null)return;
        if (zoomIn) {
            this.yrangeMax=this.yrangeMax*(1/facZoom);
        } else {
            this.yrangeMax=this.yrangeMax*(facZoom);
        }
        calcScaleFitY();
        this.actualitzaPlot();
    }

    public void zoomX(boolean zoomIn, double inc) {
        if (zoomIn) {
            this.xrangeMin=this.xrangeMin+(inc/scalefitX);
            this.xrangeMax=this.xrangeMax-(inc/scalefitX);
        } else {
            this.xrangeMin=this.xrangeMin-(inc/scalefitX);
            this.xrangeMax=this.xrangeMax+(inc/scalefitX);
        }
        calcScaleFitX();
     }
    
    public void scrollX(double inc) {
        this.xrangeMin=this.xrangeMin+(inc/scalefitX);
        this.xrangeMax=this.xrangeMax+(inc/scalefitX);
        calcScaleFitX();
    }
    
    //es mouen en consonancia els limits de rang x i y
    public void movePattern(double incX, double incY){
        this.xrangeMin=this.xrangeMin-(incX/scalefitX);
        this.xrangeMax=this.xrangeMax-(incX/scalefitX);
        this.yrangeMin=this.yrangeMin+(incY/scalefitY);
        this.yrangeMax=this.yrangeMax+(incY/scalefitY);
        this.calcScaleFitX();
        this.calcScaleFitY();
        this.actualitzaPlot(); 
        
    }

    private Point2D.Double getIntersectionPoint(Line2D.Double line1, Line2D.Double line2) {
        if (! line1.intersectsLine(line2) ) return null;
          double px = line1.getX1(),
                py = line1.getY1(),
                rx = line1.getX2()-px,
                ry = line1.getY2()-py;
          double qx = line2.getX1(),
                qy = line2.getY1(),
                sx = line2.getX2()-qx,
                sy = line2.getY2()-qy;

          double det = sx*ry - sy*rx;
          if (det == 0) {
            return null;
          } else {
            double z = (sx*(qy-py)+sy*(px-qx))/det;
            if (z==0 ||  z==1) return null;  // intersection at end point!
            return new Point2D.Double((px+z*rx), (py+z*ry));
          }
     } // end intersection line-line
    
    private Point2D.Double[] getIntersectionPoint(Line2D.Double line, Rectangle2D rectangle) {

        Point2D.Double[] p = new Point2D.Double[4];

        // Top line
        p[0] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY()));
        // Bottom line
        p[1] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));
        // Left side...
        p[2] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight()));
        // Right side
        p[3] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));

        return p;

    }

    /*
     * PAINT METHODS
     */
    public void pinta(Graphics2D g2, double scale) {
        if (!this.saveTransp){
            if (lightTheme){
                g2.setBackground(Light_bkg);
            }else{
                g2.setBackground(Dark_bkg);
            }
            g2.clearRect(0, 0, (int)(panelW*scale), (int)(panelH*scale));
        }

        //TODO es pot posar una opcio quality
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setTransform(AffineTransform.getScaleInstance(scale, scale));

        if (scalefitY<0){
            calcScaleFitY();    
        }
        if (scalefitX<0){
            calcScaleFitX();    
        }

        this.customPaintBeforeData(g2); //TO OVERRIDE AND ADD WHATEVER WE WANT
        
        //1st draw axes (and optionally grid)
        this.drawAxes(g2,showGridY,showGridX);
        if (plotTitle.length()>0)this.drawTitle(g2);//TODO posar com a opcio? o no cal (ho deixem empty)
        nrefseries=0;
        for (Plottable<? extends Plottable_point> p:dataToPlot.getPlottables()) {
            if (!p.isPlotThis())continue;
            if (p.isEmpty())continue;
            switch(p.getSerieType()) { //el default ho fa tot, he tret l'especific per xy
            case ref:
                drawREF(g2,p,p.getColor(),this.isHkllabels());
                nrefseries++;
                break;
            case hkl:
                drawHKL(g2,p,p.getColor());
                break; 
            case peaks:
                //nomes els mostrem si el plottable està seleccionat? o sempre?
                drawPeaks(g2,p,p.getColor());
                break;
            default:
                drawPattern(g2,p,p.getColor());
                if (showSerieNameInGraph)this.drawSerieLabelInGraph(g2, p);
                break;
            }
        }



        if(applyScaleFactorT2) {
            BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{2,4}, 0);
            drawVerticalLine(g2, getFrameXFromDataPointX(scaleFactorT2ang), 100, 0,"x"+FileUtils.dfX_1.format(scaleFactorT2fact), Color.GRAY, stroke,true); //always label
        }

        this.customPaintAfterData(g2); //TO OVERRIDE AND ADD WHATEVER WE WANT

        if(showLegend){ //a sobre de tot
            drawLegend(g2);
        }

        if (frontend!=null) {
            frontend.fillWindowValues();
            frontend.updateOtherValues();
        }

        if (mouseBox == true && zoomRect != null) {
            //dibuixem el rectangle
            g2.setColor(Color.darkGray);
            BasicStroke stroke = new BasicStroke(3f);
            g2.setStroke(stroke);
            g2.draw(zoomRect);
            Color gristransp = new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(),Color.LIGHT_GRAY.getBlue(), 128 );
            g2.setColor(gristransp);
            g2.fill(zoomRect);
        }
        g2.dispose();
    }

    //TODO to be overriden
    protected abstract void customPaintAfterData(Graphics2D g1);
    protected abstract void customPaintBeforeData(Graphics2D g1);

    
    //pinta al panelW i panelH, podriem fer-ho més general posant com a arguments width i height - TODO (canvi aqui i a pinta que tambe utilitza panelW i panelH) per si volem fer un full allargat per exemple
    public BufferedImage pintaPatterns(double scale) {

        BufferedImage off_Image = new BufferedImage((int)(panelW*scale), (int)(panelH*scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = off_Image.createGraphics();
        this.pinta(g2,scale); //He creat el metode pinta apart pel savesvg que peta si escribim imatge
        return off_Image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //or could do...  g.clearRect(0, 0, this.getWidth(), this.getHeight());

        panelW = this.getWidth();
        panelH = this.getHeight();

        if (dataToPlot.arePlottables()) {
            g.drawImage(pintaPatterns(1.0),0,0,null); //scale 1, pintem en pantalla
            g.dispose();
        }else {
            //no patterns, podem aprofitar per reiniciar algunes coes
//            nTotalOpenedDatSeries=0;
        }
    }

    private void drawTitle(Graphics2D g1) {

        FontRenderContext frc = g1.getFontRenderContext();
        TextLayout titleTextLayout = new TextLayout(plotTitle, g1.getFont().deriveFont(g1.getFont().getSize()+def_axisL_fsize), frc);
        double titleWidth = titleTextLayout.getBounds().getWidth();
        double titleHeight =  titleTextLayout.getBounds().getHeight();
        //per defecte horitzontal a dalt a l'esquerra
        double titleX = panelW - titleWidth - padding5px*2;
        double titleY = titleHeight + padding5px;        
        //draw
        titleTextLayout.draw(g1, (float)titleX,(float)titleY);
    }

    private void drawAxes(Graphics2D g1, boolean gridY, boolean gridX){

        //REFET el 18-Sep-2019, pintarem d'esquerra a dreta i de dalt a baix aprofitant el maxim els espais
        //i definirem des d'aquí els gaps

        if(lightTheme){
            g1.setColor(Light_frg);
        }else{
            g1.setColor(Dark_frg);
        }
        BasicStroke stroke = new BasicStroke(1.0f);
        g1.setStroke(stroke);
        FontRenderContext frc = g1.getFontRenderContext();

        if (!checkIfDiv()) {
            log.info("Error drawing division lines on the axes, please check");
            return;
        }
        if (!fixAxes) autoDivLines(true);

        gapAxisTop=padding5px;
        double valY_str_height = padding5px; //s'actualitza despres
        gapAxisRight=padding5px;
        gapAxisLeft=padding5px;
        gapAxisBottom=padding5px;

        /*
         * Primer posarem els titols dels 2 eixos i mirarem espai que necessiten
         * i definirem els GAPS
         */
        //EIX X
        // X-axis (abcissa) label.
        if(xlabel.length()==0)xlabel=" ";
        double xlabelY = panelH - padding5px;
        TextLayout xLabelTextLayout = new TextLayout(xlabel, g1.getFont().deriveFont(g1.getFont().getSize()+def_axisL_fsize), frc);
        double xlabelWidth = xLabelTextLayout.getBounds().getWidth();
        double xlabelHeigth = xLabelTextLayout.getBounds().getHeight();
        //la pintarem despres per buscar exactament el centre de l'espai considerant gapLeft

        //mirem el màxim que pot ocupar d'alt el valor X
        String str_valX = this.def_xaxis_format.format(xrangeMax);
        TextLayout valXTextLayout = new TextLayout(str_valX,g1.getFont().deriveFont(g1.getFont().getSize()+def_axis_fsize),frc);
        double valX_str_height = valXTextLayout.getBounds().getHeight();
        double valX_str_width = valXTextLayout.getBounds().getWidth();

        //ara per tenir la posicio Y de l'eix X podem sumar 
        //AxisLabelsPadding + labelXheight + AxisLabelsPadding + maxHeightValues + AxisLabelsPadding + meitatLiniaPrincipalDivisio

        double coordYeixX = panelH-(padding5px*3+xlabelHeigth+valX_str_height + (div_PrimPixSize/2.f));
        gapAxisBottom = (int)FastMath.round(panelH-coordYeixX);

        //EIX Y
        double coordXeixY = padding5px; //en cas de no eix 

        if (verticalYAxe) {
            if(ylabel.length()==0)ylabel=" ";
            TextLayout yLabelTextLayout = new TextLayout(ylabel, g1.getFont().deriveFont(g1.getFont().getSize()+def_axisL_fsize), frc);
            double ylabelWidth = yLabelTextLayout.getBounds().getWidth();
            double ylabelHeight =  yLabelTextLayout.getBounds().getHeight();
            //per defecte horitzontal a dalt a l'esquerra
            double ylabelX = padding5px;
            double ylabelY = ylabelHeight + padding5px;

            if (verticalYlabel){
                ylabelY = (float)(panelH - gapAxisBottom)/2. + ylabelWidth/2; //may2019 fix
                ylabelX = (ylabelHeight/2.f)+padding5px;
                AffineTransform orig = g1.getTransform();
                g1.rotate(-Math.PI/2,ylabelX,ylabelY);
                yLabelTextLayout.draw(g1, (float)ylabelX,(float)ylabelY);
                g1.setTransform(orig);
            }else{
                //el posem sobre l'eix en horitzontal
                yLabelTextLayout.draw(g1, (float)ylabelX,(float)ylabelY);
            }

            //mirem el màxim que pot ocupar el valor Y
            double maxYval = FastMath.max(FastMath.abs(yrangeMax),FastMath.abs(yrangeMin));
            String s = this.def_yaxis_format.format(maxYval);
            TextLayout valYTextLayout = new TextLayout(s,g1.getFont().deriveFont(g1.getFont().getSize()+def_axis_fsize),frc);
            double valY_str_width = valYTextLayout.getBounds().getWidth();
            valY_str_height = valYTextLayout.getBounds().getHeight();

            //ara per tenir la posicio X de l'eix Y podem sumar 
            //AxisLabelsPadding + labelYheight (nomes si vertical) + AxisLabelsPadding + maxWidth + AxisLabelsPadding + meitatLiniaPrincipalDivisio
            coordXeixY = padding5px*3+valY_str_width + (div_PrimPixSize/2.f);

            if (verticalYlabel) {
                coordXeixY=coordXeixY+ylabelHeight;
            }else {
                gapAxisTop=(int)FastMath.round(gapAxisTop+ylabelHeight+padding5px);
            }
            gapAxisLeft=(int)FastMath.round(coordXeixY);
        }else {
            //cal afegir maxwidth/2. al gapLeft per encabir el label val X si treiem eix
            gapAxisLeft = gapAxisLeft + (int)(valX_str_width/2.);
        }

        //al gap axis top he d'afegir una mica mes perquè cal encabir la "meitat" dels valors ja que es pinten sempre que surtin sobre l'eix i a la meitat de la ratlla.
        gapAxisTop = gapAxisTop + (int)(valY_str_height/2.f);
        gapAxisRight = gapAxisRight + (int)(valX_str_width/2.);

        //ara ja podem centrar el xlabel, el pintem
        double xlabelX = (panelW - gapAxisLeft - gapAxisRight)/2.f - xlabelWidth/2.f + gapAxisLeft;
        xLabelTextLayout.draw(g1, (float)xlabelX,(float)xlabelY);

        //al haver canviat gaps fem calc scalefit
        calcScaleFitX();
        calcScaleFitY();

        /*
         * Ara ja podem pintar els eixos 
         */
        Point2D.Double vytop = new Point2D.Double(gapAxisLeft,gapAxisTop);
        Point2D.Double vybot = new Point2D.Double(gapAxisLeft,coordYeixX);
        Point2D.Double vxleft = vybot;
        Point2D.Double vxright = new Point2D.Double(panelW-gapAxisRight,coordYeixX);
        Line2D.Double ordenada = new Line2D.Double(vytop,vybot);  //Y axis vertical
        Line2D.Double abcissa = new Line2D.Double(vxleft, vxright);  //X axis horizontal
        g1.draw(abcissa);
        //la ordenada la dibuxarem si s'escau despres

        /*
         * i ara la resta de parafarnalia
         */
        ////EIX X
        //Per tots els punts les coordenades Y seran les mateixes
        double yiniPrim = coordYeixX - (div_PrimPixSize/2.f); 
        double yfinPrim = coordYeixX + (div_PrimPixSize/2.f);
        double yiniSec = coordYeixX- (div_SecPixSize/2.f); 
        double yfinSec = coordYeixX + (div_SecPixSize/2.f);

        int ndiv = (int) (div_incXPrim/div_incXSec);
        int idiv = 0;
        double xval = div_startValX;
        while (xval <= xrangeMax){
            if (xval >= xrangeMin){ //la pintem nomes si estem dins el rang, sino numés icrementem el num de divisions
                double xvalPix = getFrameXFromDataPointX(xval);
                if (idiv%ndiv==0) {
                    //primaria: linia llarga + label
                    Line2D.Double l = new Line2D.Double(xvalPix,yiniPrim,xvalPix,yfinPrim);
                    g1.draw(l);
                    //ara el label sota la linia 
                    String s = this.def_xaxis_format.format(xval);
                    valXTextLayout = new TextLayout(s,g1.getFont().deriveFont(g1.getFont().getSize()+def_axis_fsize),frc);
                    double sw = valXTextLayout.getBounds().getWidth();
                    double sh = valXTextLayout.getBounds().getHeight();
                    double xLabel = xvalPix - sw/2f; //el posem centrat a la linia
                    double yLabel = yfinPrim + padding5px + sh;
                    valXTextLayout.draw(g1, (float)xLabel, (float)yLabel);
                }else {
                    //secundaria: linia curta
                    Line2D.Double l = new Line2D.Double(xvalPix,yiniSec,xvalPix,yfinSec);
                    g1.draw(l);
                }

                //i ara el grid
                //pel grid, vytop.y sera el punt superior de la linia, yiniPrim sera el punt inferior (AIXO PER LES Y, despres les X es defineixen al bucle)
                if(gridY){
                    BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{1,2}, 0);
                    g1.setStroke(dashed);
                    Line2D.Double ld = new Line2D.Double(xvalPix,vytop.y,xvalPix,yiniSec);
                    g1.draw(ld);
                    g1.setStroke(stroke); //recuperem l'anterior
                }
            }
            xval = xval + div_incXSec;
            idiv = idiv + 1;
        }

        ////EIX Y
        if (verticalYAxe) {
            g1.draw(ordenada);
            //Y axis divisions and labels:
            //Per tots els punts les coordenades Y seran les mateixes

            double xiniPrim = coordXeixY - (div_PrimPixSize/2.f); 
            double xfinPrim = coordXeixY + (div_PrimPixSize/2.f);
            double xiniSec = coordXeixY - (div_SecPixSize/2.f); 
            double xfinSec = coordXeixY + (div_SecPixSize/2.f);

            ndiv = (int) (div_incYPrim/div_incYSec);
            idiv = 0;
            double yval = div_startValY;

            while (yval <= yrangeMax){
                if (yval >= yrangeMin){ //la pintem nomes si estem dins el rang, sino numés icrementem el num de divisions
                    double yvalPix = getFrameYFromDataPointY(yval);

                    if (!negativeYAxisLabels && (yval<0)){
                        yval = yval + div_incYSec;
                        continue;
                    }

                    if (idiv%ndiv==0) {
                        Line2D.Double l = new Line2D.Double(xiniPrim, yvalPix, xfinPrim, yvalPix);
                        g1.draw(l);
                        //ara el label a l'esquerra de la linia (atencio a negatius, depen si hi ha l'opcio)
                        String s = this.def_yaxis_format.format(yval);
                        TextLayout valYTextLayout = new TextLayout(s,g1.getFont().deriveFont(g1.getFont().getSize()+def_axis_fsize),frc);
                        double sw = valYTextLayout.getBounds().getWidth();
                        double sh = valYTextLayout.getBounds().getHeight();
                        double xLabel = xiniPrim - padding5px - sw; 
                        double yLabel = yvalPix + sh/2f; //el posem centrat a la linia.. no se perquè queda millor amb 3 que 2
                        valYTextLayout.draw(g1, (float)xLabel, (float)yLabel); //prova d'utilitzar textLayouts -- funciona be i el getbounds va millor
                    }else {
                        Line2D.Double l = new Line2D.Double(xiniSec,yvalPix,xfinSec,yvalPix);
                        g1.draw(l);
                    }

                    //i ara el grid
                    //pel grid, vytop.y sera el punt superior de la linia, yiniPrim sera el punt inferior (AIXO PER LES Y, despres les X es defineixen al bucle)
                    if(gridX){
                        BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{1,2}, 0);
                        g1.setStroke(dashed);
                        Line2D.Double ld = new Line2D.Double(vxright.x,yvalPix,xfinSec,yvalPix);
                        g1.draw(ld);
                        g1.setStroke(stroke); //recuperem l'anterior
                    }

                }
                yval = yval + div_incYSec;
                idiv = idiv + 1;
            }
        }
    }

    //dibuixa linia, punts i errorbars -- poso el color apart per si es vol fer d'un altre (triar)
    protected void drawPattern(Graphics2D g1, Plottable<? extends Plottable_point> serie, Color col){
        g1.setColor(col);
        BasicStroke stroke = new BasicStroke(serie.getLineWidth());
        g1.setStroke(stroke);

        for (int i = 0; i < serie.getNPoints(); i++){

            Plottable_point pp1=serie.getCorrectedPoint(i,plotwithbkg);

            if (applyScaleFactorT2) {              //APLICAR escala global tth si s'escau
                if (pp1.getX()>scaleFactorT2ang) {
                    pp1 = serie.getCorrectedPoint(i,serie.getXOffset(),serie.getYOffset(),serie.getZOffset(),scaleFactorT2fact,plotwithbkg);
                }
            }

            Point2D.Double fp1 = getFramePointFromDataPoint(pp1);
            boolean isP1 = isFramePointInsideGraphArea(fp1);

            //linia
            if (serie.getLineWidth()>0) {
                if (i<(serie.getNPoints()-1)){ //no considerem l'últim punt

                    Plottable_point pp2=serie.getCorrectedPoint(i+1,plotwithbkg);

                    if (applyScaleFactorT2) {              //APLICAR escala global tth si s'escau
                        if (pp2.getX()>scaleFactorT2ang) {
                            pp2 = serie.getCorrectedPoint(i+1,serie.getXOffset(),serie.getYOffset(),serie.getZOffset(),scaleFactorT2fact,plotwithbkg); 
                        }
                    }


                    if (FastMath.abs(pp1.getX()-pp2.getX())>serie.getToleranceContinuous()) {
                        //no dibuixem linia
                        continue;
                    }

                    Point2D.Double fp2 = getFramePointFromDataPoint(pp2);

                    boolean isP2 = isFramePointInsideGraphArea(fp2);

                    if (!isP1){
                        if (!isP2){
                            continue;
                        }else{
                            //P1 esta fora, cal redefinirlo amb la interseccio amb l'eix pertinent
                            Point2D.Double[] p = getIntersectionPoint(new Line2D.Double(fp1,fp2),getRectangleGraphArea());
                            for (int j=0;j<p.length;j++){
                                if (p[j]!=null){
                                    fp1 = p[j];
                                }
                            }
                        }
                    }

                    if (!isP2){
                        if (!isP1){
                            continue;
                        }else{
                            //P2 esta fora, cal redefinirlo amb la interseccio amb l'eix pertinent
                            Point2D.Double[] p = getIntersectionPoint(new Line2D.Double(fp1,fp2),getRectangleGraphArea());//
                            for (int j=0;j<p.length;j++){
                                if (p[j]!=null){
                                    fp2 = p[j];
                                }
                            }
                        }
                    } 
                    //ARA JA PODEM DIBUIXAR LA LINIA
                    Line2D.Double l = new Line2D.Double(fp1.x,fp1.y,fp2.x,fp2.y);
                    g1.draw(l);
                }
            }

            //si el punt esta fora ja podem mirar el seguent --> aixo nomes ho podem posar aqui despres de la linia perque mirem interseccions
            if(!isP1)continue;

            //MARKER
            if(serie.getMarkerSize()>0) {
                drawPatternPoint(g1,fp1,serie.getMarkerSize()/2.f,serie.getColor());
                //cal recuperar
                //              g1.setColor(col);
                stroke = new BasicStroke(serie.getLineWidth());
                g1.setStroke(stroke);
            }

            //ERROR BARS
            if(serie.isShowErrBars()) {
                drawErrorBar(g1,pp1,serie.getColor());
                //cal recuperar
                g1.setColor(col);
                stroke = new BasicStroke(serie.getLineWidth());
                g1.setStroke(stroke);
            }

            if (showPointLabels) {
                //posarem un label a sobre a la dreta?
                int x = (int) FastMath.round(fp1.x+2); //I put only 2pixels (fixed) instead of padding of 5px
                int y = (int) FastMath.round(fp1.y-2);
                if (x>panelW)x=panelW;
                if (y<0)y=0;
                g1.drawString(pp1.getLabel(),x,y);
            }
        }
    }

    private void drawSerieLabelInGraph(Graphics2D g1, Plottable<? extends Plottable_point> serie) {
        g1.setColor(serie.getColor());
        
        for (int i = serie.getNPoints()-1; i > 0; i--){
            Plottable_point pp1=serie.getCorrectedPoint(i,plotwithbkg);
            FontRenderContext frc = g1.getFontRenderContext();
            
            if (applyScaleFactorT2) {              //APLICAR escala global tth si s'escau
                if (pp1.getX()>scaleFactorT2ang) {
                    pp1 = serie.getCorrectedPoint(i,0,0,0,scaleFactorT2fact,plotwithbkg);
                }
            }

            Point2D.Double fp1 = getFramePointFromDataPoint(pp1);
            boolean isP1 = isFramePointInsideGraphArea(fp1);

            if (isP1) { 
                //hem trobat el primer punt visible de la dreta del pattern, ens interessa a quin pixel Y de pantalla correspon
                TextLayout snameTextLayout = new TextLayout(serie.getName(),g1.getFont(),frc);
                double sname_str_width = snameTextLayout.getBounds().getWidth();
                double sname_str_height = snameTextLayout.getBounds().getHeight();
                int x = (int) FastMath.round(fp1.x-this.padding5px-sname_str_width+this.getSerieNameOffsetPixX());
                int y = (int) FastMath.round(fp1.y-sname_str_height-this.getSerieNameOffsetPixY());
                snameTextLayout.draw(g1, x, y);
                return;
            }
        }
        
        
    }

    //dibuixa un sol SENSE COMPROVACIONS
    private void drawPatternPoint(Graphics2D g1, Point2D.Double framePoint, double radiPunt, Color col) {
        g1.setColor(col);
        BasicStroke stroke = new BasicStroke(0.0f);
        g1.setStroke(stroke);
        int dia=(int) FastMath.round(radiPunt*2);
        g1.fillOval((int)FastMath.round(framePoint.x-radiPunt), (int)FastMath.round(framePoint.y-radiPunt), dia,dia);
        g1.drawOval((int)FastMath.round(framePoint.x-radiPunt), (int)FastMath.round(framePoint.y-radiPunt), dia,dia);
    }

    //dibuixa una barra d'error
    private void drawErrorBar(Graphics2D g1, Plottable_point pp, Color col) {
        g1.setColor(col);
        BasicStroke stroke = new BasicStroke(1.0f);
        g1.setStroke(stroke);

        double tth = pp.getX();
        double counts = pp.getY();
        double err = pp.getSdy();

        if (err<=0.0f)return;

        Point2D.Double p1 = getFramePointFromDataPoint(pp);

        double ytop = counts+err;
        double ybot = counts-err;

        double x = getFrameXFromDataPointX(tth);
        Point2D.Double ptop = new Point2D.Double(x,getFrameYFromDataPointY(ytop));
        Point2D.Double pbot = new Point2D.Double(x,getFrameYFromDataPointY(ybot));

        double modulvert = FastMath.abs(pbot.y-ptop.y);
        double modulHor = FastMath.max(6,modulvert/4.f+1);

        Point2D.Double ptopl = new Point2D.Double(ptop.x-modulHor/2f,ptop.y);
        Point2D.Double ptopr = new Point2D.Double(ptop.x+modulHor/2f,ptop.y);

        Point2D.Double pbotl = new Point2D.Double(pbot.x-modulHor/2f,pbot.y);
        Point2D.Double pbotr = new Point2D.Double(pbot.x+modulHor/2f,pbot.y);

        //comprovem que tot estigui dins
        if (!isFramePointInsideGraphArea(p1) || !isFramePointInsideGraphArea(ptopl) || !isFramePointInsideGraphArea(ptopr) || !isFramePointInsideGraphArea(pbotl) || !isFramePointInsideGraphArea(pbotr)){
            return;
        }

        //ara dibuixem les 3 linies
        g1.draw(new Line2D.Double(ptop.x,ptop.y,pbot.x,pbot.y));
        g1.draw(new Line2D.Double(ptopl.x,ptopl.y,ptopr.x,ptopr.y));
        g1.draw(new Line2D.Double(pbotl.x,pbotl.y,pbotr.x,pbotr.y));
    }

    protected void drawHKL(Graphics2D g1, Plottable<? extends Plottable_point> serie, Color col){
        for (int i = 0; i < serie.getNPoints(); i++){
            g1.setColor(col);
            BasicStroke stroke = new BasicStroke(serie.getLineWidth());
            g1.setStroke(stroke);

            //despres del canvi a private de seriePoints
            double tth = serie.getCorrectedPoint(i,plotwithbkg).getX();

            //la X es la 2THETA pero la Y hauria de ser el punt de menor intensitat de OBS més un hkloffset (en pixels, definit a patt1d)
            double fx = getFrameXFromDataPointX(tth);
            double fy = getFrameYFromDataPointY(0.0+serie.getYOffset());
            Point2D.Double ptop = new Point2D.Double(fx, fy);
            Point2D.Double pbot = new Point2D.Double(fx, fy+serie.getScaleY());

            //comprovem que tot estigui dins
            if (!isFramePointInsideGraphArea(ptop) || !isFramePointInsideGraphArea(pbot)){
                continue;
            }

            //ara dibuixem la linia
            g1.draw(new Line2D.Double(ptop.x,ptop.y,pbot.x,pbot.y));

        }
    }

    //draw vertical lines --> atencio posa el label del punt!
    protected void drawREF(Graphics2D g1, Plottable<? extends Plottable_point> serie, Color col, boolean drawLabels){
        for (int i = 0; i < serie.getNPoints(); i++){

            BasicStroke stroke = new BasicStroke(serie.getLineWidth());
            switch (FastMath.round(serie.getMarkerSize())) {
            case 1:
                stroke = new BasicStroke(serie.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{1,2}, 0);
                break;
            case 2:
                stroke = new BasicStroke(serie.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{1,4}, 0);
                break;
            case 3:
                stroke = new BasicStroke(serie.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{2,4}, 0);
                break;
            default:
                stroke = new BasicStroke(serie.getLineWidth());
                break;

            }

            Plottable_point pp = serie.getCorrectedPoint(i, serie.getXOffset(), 0, 0, serie.getScaleY(), plotwithbkg);

            double tth = pp.getX();
            double inten = 100;
            if(showDBCompoundIntensity) {
                inten = pp.getY(); //normalitzada a 100
            }

            double fx = getFrameXFromDataPointX(tth);
            //check if yoff
            int yOffPix=(int) serie.getYOffset();

            drawVerticalLine(g1,fx,inten,yOffPix,pp.getLabel(),col,stroke,drawLabels);

            if(splitDBCompound) {
                //fem una altra linia vertical des de sota de tot d'uns 10 pixels
                drawVerticalLine(g1,fx,-10,10*nrefseries,"",col,new BasicStroke(serie.getLineWidth()),drawLabels);
            }
        }
    }

    //label to put next to line at the top,
    //frameX is the pixel in X of the vertical line
    //percentage of vertical space occupied (from bottom to top)
    //Sep2019 afegit Yofset de la serie
    // if percent<0 it will be considered number of pixels from bottom
    private void drawVerticalLine(Graphics2D g1, double frameX, double percent, int yOffset, String label, Color col, BasicStroke stroke, boolean drawLabel) {
        if (!isFramePointInsideXGraphArea(frameX)) return;
        if (percent==0)return;
        g1.setColor(col);
        g1.setStroke(stroke);
        int ytop = gapAxisTop+padding5px;
        int ybot = panelH-gapAxisBottom-padding5px-yOffset; //al ser en pixels aplico directament Yoffset
        int dist = FastMath.abs(ybot-ytop); //faig abs per si de cas...
        Point2D.Double ptop = new Point2D.Double(frameX, ybot - dist * (percent/100.)); //100% es com tenir Ytop
        Point2D.Double pbot = new Point2D.Double(frameX, ybot);
        if (percent<0) {
            ptop=new Point2D.Double(frameX, ybot + percent); //considero pixels (sumo perque es negatiu... com restar)
        }

        //linia de dalt a baix
        g1.draw(new Line2D.Double(ptop.x,ptop.y,pbot.x,pbot.y));

        //ara el label
        if (drawLabel) {
            if (!label.isEmpty()) {
                //escribim al costat de la linia
                Font font = g1.getFont();
                g1.setFont(g1.getFont().deriveFont(g1.getFont().getSize()+def_axisL_fsize));
                double[] swh = getWidthHeighString(g1,label);
                double sy = gapAxisTop+padding5px + swh[1];
                double sx = frameX + padding5px;
                g1.drawString(label, (float)sx,(float)sy);
                g1.setFont(font); //recuperem font defecte
            }
        }
    }

    private void drawLegend(Graphics2D g1){

        int margin = 5; //podria fer servir el padding5px
        int linelength = 15; //longitud de la linia a la llegenda
        float strokewidth = 3;

        FontRenderContext frc = g1.getFontRenderContext();

        //1r mirem i definim mides
        double entryMaxWidth = 1;
        double entryMaxHeight = 1;
        int entries = 0;
        for (Plottable<? extends Plottable_point> p:dataToPlot.getPlottables()) {
            if (!p.isPlotThis())continue;
            if (p.isEmpty())continue;
            if (!p.isShowInLegend())continue;

            TextLayout lay = new TextLayout(p.getName(), g1.getFont().deriveFont(g1.getFont().getSize()+def_legend_fsize), frc);
            double entryWidth = lay.getBounds().getWidth();
            double entryHeight = lay.getBounds().getHeight();
            if (entryWidth>entryMaxWidth)entryMaxWidth=entryWidth;
            if (entryHeight>entryMaxHeight)entryMaxHeight=entryHeight;
            entries++;
        }
        //ara ja podem calcular el width del quadrat == margin*2+linelength+margin+entryMaxWidth+margin*2
        int rectWidth = (int)FastMath.round(entryMaxWidth + margin*5 + linelength);
        //i el height del quadrat == margin*2 + entryMaxHeight*entries + margin*(entries-1) + margin*2
        int rectHeight = (int)FastMath.round(entryMaxHeight*entries + margin*(entries-1+4));

        //dibuixem el quadrat:
        if (autoPosLegend){
            legendX = panelW-padding5px*2-rectWidth;
            legendY = padding5px*2;
        }else{ //TODO revisar/actualitzar
            if (legendX>panelW-padding5px-2*margin) legendX=panelW-padding5px-2*margin;
            if (legendX<padding5px) legendX=padding5px;
            if (legendY<padding5px) legendY=padding5px;
            if (legendY>panelH-padding5px-2*margin) legendY=panelH-padding5px-2*margin;
        }

        if (legend_opaque) {
            if (lightTheme){
                g1.setColor(Light_Legend_bkg);    
            }else{
                g1.setColor(Dark_Legend_bkg);
            }    
            g1.fillRect(legendX,legendY,rectWidth,rectHeight);
        }

        if (lightTheme){
            g1.setColor(Light_Legend_line);    
        }else{
            g1.setColor(Dark_Legend_line);
        }
        BasicStroke stroke = new BasicStroke(1.0f);
        g1.setStroke(stroke);
        g1.drawRect(legendX,legendY,rectWidth,rectHeight);

        try {
            entries = 0;
            for (Plottable<? extends Plottable_point> p:dataToPlot.getPlottables()) {
                if (!p.isPlotThis())continue;
                if (p.isEmpty())continue;
                if (!p.isShowInLegend())continue;
                stroke = new BasicStroke(strokewidth);
                g1.setStroke(stroke);
                g1.setColor(p.getColor());

                //dibuixem primer la linia (si s'escau)
                int l_iniX = legendX+margin*2;
                int l_finX = l_iniX+linelength;
                int l_y = (int) (legendY+margin*2+entries*(entryMaxHeight)+entries*margin); //aixo es a DALT, cal posar-ho al mig per linia horitzontal
                if (p.getLineWidth()>0){
                    
                    if (p.getSerieType()==SerieType.hkl) {
                        //LINIA VERTICAL
                        int centreX = (int) ((l_iniX+l_finX)/2.f);
                        int l_iniY = (int) (l_y+(margin/4.));
                        int l_finY = (int) (l_y+entryMaxHeight-margin/4.f);
                        Line2D.Float l = new Line2D.Float(centreX,l_iniY,centreX,l_finY);
                        g1.draw(l);
                    }else {
                      //LINIA NORMAL HORITZONAL
                        int l_y_cen=l_y+(int)FastMath.round(entryMaxHeight/2.);
                        Line2D.Float l = new Line2D.Float(l_iniX,l_y_cen,l_finX,l_y_cen);
                        g1.draw(l);    
                    }
                    
                }
                //dibuixem els markers (si s'escau)
                if (p.getMarkerSize()>0){
                    int sep = (int) (FastMath.abs(l_iniX-l_finX)/5.f);
                    int x1 = l_iniX+sep;
                    int x2 = l_iniX+sep*4;
                    stroke = new BasicStroke(0.0f);
                    g1.setStroke(stroke);
                    double radiPunt = p.getMarkerSize()/2.f;
                    int l_y_cen=l_y+(int)FastMath.round(entryMaxHeight/2.);
                    g1.fillOval((int)FastMath.round(x1-radiPunt), (int)FastMath.round(l_y_cen-radiPunt), FastMath.round(p.getMarkerSize()), FastMath.round(p.getMarkerSize()));
                    g1.fillOval((int)FastMath.round(x2-radiPunt), (int)FastMath.round(l_y_cen-radiPunt), FastMath.round(p.getMarkerSize()), FastMath.round(p.getMarkerSize()));
                }
                //recuperem stroke width per si de cas hi havia markers
                stroke = new BasicStroke(strokewidth);
                g1.setStroke(stroke);

                //ara el text
                int t_X = l_finX+margin; 
                int t_Y = (int) (l_y+entryMaxHeight-strokewidth/2.);
                TextLayout lay = new TextLayout(p.getName(), g1.getFont().deriveFont(g1.getFont().getSize()+def_legend_fsize), frc);
                lay.draw(g1, t_X,t_Y);
                entries++;

            }
        } catch (Exception e) {
            legendX = legendX - 10;
            repaint();
        }
    }


    private void drawPeaks(Graphics2D g1, Plottable<? extends Plottable_point> ds, Color col){
        //only for "peaks" series of the selected series
        int gapPixels = 5; //gap between top of peak and line
        int sizePix = 20;

        if (ds!=null){ //there is a peaks serie
            if (ds.isEmpty())return; //no peaks
            for (int i=0;i<ds.getNPoints();i++) {
                Plottable_point pt = ds.getCorrectedPoint(i,plotwithbkg);
                Point2D.Double ptop = getFramePointFromDataPoint(pt);
                if(!isFramePointInsideGraphArea(ptop))continue;
                //              //ara fem una linia amunt recta
                ptop.y=ptop.y-gapPixels;
                //draw LIne
                BasicStroke stroke = new BasicStroke(2.0f);
                g1.setStroke(stroke);
                g1.setColor(col.darker());
                g1.drawLine((int)ptop.x, (int)ptop.y, (int)ptop.x, (int)ptop.y-sizePix);
            }
        }
    }


    private double[] getWidthHeighString(Graphics2D g1, String s){
        double[] w_h = new double[2];
        Font font = g1.getFont();
        FontRenderContext frc = g1.getFontRenderContext();
        w_h[0] = font.getStringBounds(s, frc).getWidth();
        w_h[1] =  font.getStringBounds(s, frc).getHeight();
        return w_h;
    }
    
    
    
//////SETTERS AND GETTERS
    public void setTransp(boolean transp){
        this.saveTransp=transp;
    }

    public DecimalFormat getDef_xaxis_format() {
        return def_xaxis_format;
    }

    public void setDef_xaxis_format(DecimalFormat def_xaxis_format) {
        this.def_xaxis_format = def_xaxis_format;
    }

    public DecimalFormat getDef_yaxis_format() {
        return def_yaxis_format;
    }

    public void setDef_yaxis_format(DecimalFormat def_yaxis_format) {
        this.def_yaxis_format = def_yaxis_format;
    }

    public void setDecimalsXaxis(int dec) {
        switch (dec) {
        case 0:
            this.setDef_xaxis_format(FileUtils.dfX_0);
            break;
        case 1:
            this.setDef_xaxis_format(FileUtils.dfX_1);
            break;
        case 2:
            this.setDef_xaxis_format(FileUtils.dfX_2);
            break;
        case 3:
            this.setDef_xaxis_format(FileUtils.dfX_3);
            break;
        case 4:
            this.setDef_xaxis_format(FileUtils.dfX_4);
            break;
        case 5:
            this.setDef_xaxis_format(FileUtils.dfX_5);
            break;
        default:
            this.setDef_xaxis_format(FileUtils.dfX_3);
        }
    }
    public void setDecimalsYaxis(int dec) {
        switch (dec) {
        case 0:
            this.setDef_yaxis_format(FileUtils.dfX_0);
            break;
        case 1:
            this.setDef_yaxis_format(FileUtils.dfX_1);
            break;
        case 2:
            this.setDef_yaxis_format(FileUtils.dfX_2);
            break;
        case 3:
            this.setDef_yaxis_format(FileUtils.dfX_3);
            break;
        case 4:
            this.setDef_yaxis_format(FileUtils.dfX_4);
            break;
        case 5:
            this.setDef_yaxis_format(FileUtils.dfX_5);
            break;
        default:
            this.setDef_yaxis_format(FileUtils.dfX_3);
            break;
        }
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        this.lightTheme = lightTheme;
    }

    public String getXlabel() {
        return xlabel;
    }

    public void setXlabel(String xlabel) {
        this.xlabel = xlabel;
    }

    public String getYlabel() {
        return ylabel;
    }

    public void setYlabel(String ylabel) {
        this.ylabel = ylabel;
    }

    public String getPlotTitle() {
        return plotTitle;
    }

    public void setPlotTitle(String plotTitle) {
        this.plotTitle = plotTitle;
    }

    public boolean isVerticalYlabel() {
        return verticalYlabel;
    }

    public void setVerticalYlabel(boolean verticalYlabel) {
        this.verticalYlabel = verticalYlabel;
    }

    public boolean isVerticalYAxe() {
        return verticalYAxe;
    }

    public void setVerticalYAxe(boolean verticalYAxe) {
        this.verticalYAxe = verticalYAxe;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public boolean isAutoPosLegend() {
        return autoPosLegend;
    }

    public void setAutoPosLegend(boolean autoPosLegend) {
        this.autoPosLegend = autoPosLegend;
    }

    public int getLegendX() {
        return legendX;
    }

    public void setLegendX(int legendX) {
        this.legendX = legendX;
    }

    public int getLegendY() {
        return legendY;
    }

    public void setLegendY(int legendY) {
        this.legendY = legendY;
    }

    public boolean isLegend_opaque() {
        return legend_opaque;
    }

    public void setLegend_opaque(boolean legend_opaque) {
        this.legend_opaque = legend_opaque;
    }

    public boolean isShowGridY() {
        return showGridY;
    }

    public void setShowGridY(boolean showGridY) {
        this.showGridY = showGridY;
    }

    public boolean isShowGridX() {
        return showGridX;
    }

    public void setShowGridX(boolean showGridX) {
        this.showGridX = showGridX;
    }

    public boolean isApplyScaleFactorT2() {
        return applyScaleFactorT2;
    }

    public void setApplyScaleFactorT2(boolean applyScaleFactorT2) {
        this.applyScaleFactorT2 = applyScaleFactorT2;
    }

    public boolean isShowPointLabels() {
        return showPointLabels;
    }

    public void setShowPointLabels(boolean showPointLabels) {
        this.showPointLabels = showPointLabels;
    }

    public boolean isShowSerieNameInGraph() {
        return showSerieNameInGraph;
    }

    public void setShowSerieNameInGraph(boolean showSerieNameInGraph) {
        this.showSerieNameInGraph = showSerieNameInGraph;
    }

    public void setPartialYScale(boolean selected, float t2, float fact) {
        this.applyScaleFactorT2=selected;
        this.scaleFactorT2ang=t2;
        this.scaleFactorT2fact=fact;
    }
    
    public boolean isPlotwithbkg() {
        return plotwithbkg;
    }

    public void setPlotwithbkg(boolean plotwithbkg) {
        this.plotwithbkg = plotwithbkg;
    }

    public boolean isShowDBCompoundIntensity() {
        return showDBCompoundIntensity;
    }

    public void setShowDBCompoundIntensity(boolean showDBCompoundIntensity) {
        this.showDBCompoundIntensity = showDBCompoundIntensity;
    }

    public boolean isCustomXtitle() {
        return customXtitle;
    }

    public void setCustomXtitle(boolean customXtitle) {
        this.customXtitle = customXtitle;
    }

    public boolean isFixAxes() {
        return fixAxes;
    }

    public void setFixAxes(boolean fixAxes) {
        this.fixAxes = fixAxes;
    }

    public boolean isNegativeYAxisLabels() {
        return negativeYAxisLabels;
    }

    public void setNegativeYAxisLabels(boolean negativeYAxisLabels) {
        this.negativeYAxisLabels = negativeYAxisLabels;
    }

    public Rectangle2D.Double getZoomRect() {
        return zoomRect;
    }

    public void setZoomRect(Rectangle2D.Double zoomRect) {
        this.zoomRect = zoomRect;
    }

    public boolean isMouseBox() {
        return mouseBox;
    }

    public void setMouseBox(boolean mouseBox) {
        this.mouseBox = mouseBox;
    }

    public boolean isSplitDBCompound() {
        return splitDBCompound;
    }

    public void setSplitDBCompound(boolean splitDBCompound) {
        this.splitDBCompound = splitDBCompound;
    }

    public double getIncXPrimPIXELS() {
        return incXPrimPIXELS;
    }

    public void setIncXPrimPIXELS(double incXPrimPIXELS) {
        this.incXPrimPIXELS = incXPrimPIXELS;
    }

    public int getDef_nDecimalsX() {
        return def_nDecimalsX;
    }

    public void setDef_nDecimalsX(int def_nDecimalsX) {
        this.def_nDecimalsX = def_nDecimalsX;
        this.setDecimalsXaxis(def_nDecimalsX);
    }

    public int getDef_nDecimalsY() {
        return def_nDecimalsY;
    }

    public void setDef_nDecimalsY(int def_nDecimalsY) {
        this.def_nDecimalsY = def_nDecimalsY;
        this.setDecimalsYaxis(def_nDecimalsY);
    }

    public double getIncXSecPIXELS() {
        return incXSecPIXELS;
    }

    public void setIncXSecPIXELS(double incXSecPIXELS) {
        this.incXSecPIXELS = incXSecPIXELS;
    }

    public double getIncYPrimPIXELS() {
        return incYPrimPIXELS;
    }

    public void setIncYPrimPIXELS(double incYPrimPIXELS) {
        this.incYPrimPIXELS = incYPrimPIXELS;
    }

    public double getIncYSecPIXELS() {
        return incYSecPIXELS;
    }

    public void setIncYSecPIXELS(double incYSecPIXELS) {
        this.incYSecPIXELS = incYSecPIXELS;
    }

    public int getDiv_PrimPixSize() {
        return div_PrimPixSize;
    }

    public void setDiv_PrimPixSize(int div_PrimPixSize) {
        this.div_PrimPixSize = div_PrimPixSize;
    }

    public int getDiv_SecPixSize() {
        return div_SecPixSize;
    }

    public void setDiv_SecPixSize(int div_SecPixSize) {
        this.div_SecPixSize = div_SecPixSize;
    }

    public double getFacZoom() {
        return facZoom;
    }

    public void setFacZoom(double facZoom) {
        this.facZoom = facZoom;
    }

    public float getDef_axis_fsize() {
        return def_axis_fsize;
    }

    public void setDef_axis_fsize(float def_axis_fsize) {
        this.def_axis_fsize = def_axis_fsize;
    }

    public float getDef_axisL_fsize() {
        return def_axisL_fsize;
    }

    public void setDef_axisL_fsize(float def_axisL_fsize) {
        this.def_axisL_fsize = def_axisL_fsize;
    }

    public float getDef_legend_fsize() {
        return def_legend_fsize;
    }

    public void setDef_legend_fsize(float def_legend_fsize) {
        this.def_legend_fsize = def_legend_fsize;
    }

    public float getScaleFactorT2ang() {
        return scaleFactorT2ang;
    }

    public float getScaleFactorT2fact() {
        return scaleFactorT2fact;
    }
    public int getSerieNameOffsetPixY() {
        return serieNameOffsetPixY;
    }

    public void setSerieNameOffsetPixY(int serieNameOffsetPixY) {
        this.serieNameOffsetPixY = serieNameOffsetPixY;
    }

    public int getSerieNameOffsetPixX() {
        return serieNameOffsetPixX;
    }

    public void setSerieNameOffsetPixX(int serieNameOffsetPixX) {
        this.serieNameOffsetPixX = serieNameOffsetPixX;
    }

    public boolean isHkllabels() {
        return hkllabels;
    }
    public void setHkllabels(boolean hkllabels) {
        this.hkllabels = hkllabels;
    }
    
    public boolean isCustomCanvasSize() {
        return customCanvasSize;
    }

    public void setCustomCanvasSize(boolean customCanvasSize) {
        this.customCanvasSize = customCanvasSize;
    }

    public int getCustomCanvasWidth() {
        return customCanvasWidth;
    }

    public void setCustomCanvasWidth(int customCanvasWidth) {
        this.customCanvasWidth = customCanvasWidth;
    }

    public int getCustomCanvasHeight() {
        return customCanvasHeight;
    }

    public void setCustomCanvasHeight(int customCanvasHeight) {
        this.customCanvasHeight = customCanvasHeight;
    }

    @Override
    public Dimension getPreferredSize() {
        if (isCustomCanvasSize()) {
            return new Dimension(this.getCustomCanvasWidth(),this.getCustomCanvasHeight());
        }else {
            return super.getPreferredSize();    
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(minWidth,minHeight);
        
    }
}




 