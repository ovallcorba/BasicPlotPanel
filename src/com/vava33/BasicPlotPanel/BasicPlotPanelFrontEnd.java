package com.vava33.BasicPlotPanel;

/**
 * Basic extension of the FrontEnd panel
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */
 
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.BasicPlotPanel.frontend.FEpanel_XYwindow;
import com.vava33.BasicPlotPanel.frontend.FEpanel_axes;
import com.vava33.BasicPlotPanel.frontend.FEpanel_general;
import com.vava33.BasicPlotPanel.frontend.FEpanel_otherOptions;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.Options;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import org.apache.commons.math3.util.FastMath;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import javax.swing.border.EmptyBorder;

public class BasicPlotPanelFrontEnd<T extends Plot1DPanel<?>> extends JPanel implements Plot1DFrontend{

    private static final long serialVersionUID = 1L;
    protected static final int minZoomPixels = 5; //el faig final i general perque no el modifiquem
    private VavaLogger log;
    public VavaLogger getLog() {return log;}
    private double splitPanePosition = -1;

    // DEFINICIO BUTONS DEL MOUSE
    protected int MOURE = MouseEvent.BUTTON2;
    protected int CLICAR = MouseEvent.BUTTON1;
    protected int ZOOM_BORRAR = MouseEvent.BUTTON3;
    
    //parametres interaccio/contrast
    protected boolean sqSelect = false;
    protected boolean shiftPressed = false;
    protected boolean mouseDrag = false;
    protected boolean mouseMove = false;
    protected boolean mouseZoom = false;
    protected Point2D.Double dragPoint;
    protected Point2D.Double clickPoint;
    
    private JPanel statusPanel;
    public JScrollPane graphScroll;
    protected JLabel lblTth;
    private JPanel plotPanelContainer;
    protected JLabel lblInten;
    private JScrollPane scrollPane;
    private JSplitPane splitPane;
    private JPanel buttons_panel;
    private JPanel panelStatusCursorInfo;
    private JButton btnFitWindowStatus;
    private JButton btnShowhideOpts;
    private JButton btnReassigncolors;
    protected JLabel lblDsp;
    protected JLabel lblHkl;
    protected T graphPanel;

    private FEpanel_general panel_general;
    private FEpanel_axes panel_axes;
    private FEpanel_XYwindow panel_xywindow;
    private FEpanel_otherOptions panel_otherOptions;
    
    /**
     * Create the panel.
     */
    public BasicPlotPanelFrontEnd(Options opt, T plot1dpanel,VavaLogger logger) {
        super();
        log=logger;
        if (plot1dpanel==null)return;
        plot1dpanel.setFrontEnd(this); // TODO this crashes el WindowBuilder
        graphPanel = plot1dpanel;
        this.readOptions(opt);
        
        this.setBackground(Color.WHITE);
        this.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
        
        graphPanel.setBackground(Color.WHITE);
        graphPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent arg0) {
                do_graphPanel_mouseWheelMoved(arg0);
            }
        });
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                do_graphPanel_mousePressed(arg0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                do_graphPanel_mouseReleased(e);
            }
        });
        graphPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                do_graphPanel_mouseDragged(arg0);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                do_graphPanel_mouseMoved(e);
            }
        });
        
        
        splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        this.add(splitPane, "cell 0 0,grow");
        plotPanelContainer = new JPanel();
        splitPane.setLeftComponent(plotPanelContainer); //to design
        plotPanelContainer.setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));

        graphScroll = new JScrollPane();
        graphScroll.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        plotPanelContainer.add(graphScroll, "cell 0 0,grow");
        graphScroll.setViewportView(graphPanel);
        
        panelStatusCursorInfo = new JPanel();
        plotPanelContainer.add(panelStatusCursorInfo, "cell 0 1,grow");
        panelStatusCursorInfo.setLayout(new MigLayout("insets 1 n 1 n", "[][][][grow][][][]", "[]"));
        
        lblTth = new JLabel("X");
        panelStatusCursorInfo.add(lblTth, "cell 0 0");
        lblTth.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
        
        lblInten = new JLabel("Y");
        panelStatusCursorInfo.add(lblInten, "cell 1 0");
        
        btnFitWindowStatus = new JButton("FitV");
        btnFitWindowStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnFitWindowStatus_actionPerformed(e);
            }
        });
        
        lblDsp = new JLabel("");
        panelStatusCursorInfo.add(lblDsp, "cell 2 0");
        
        lblHkl = new JLabel("");
        panelStatusCursorInfo.add(lblHkl, "cell 3 0");
        btnFitWindowStatus.setToolTipText("Reset View");
        panelStatusCursorInfo.add(btnFitWindowStatus, "cell 4 0");
        
        btnShowhideOpts = new JButton("LateralOpts");
        btnShowhideOpts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnShowhideOpts_actionPerformed(e);
            }
        });
        
        btnReassigncolors = new JButton("reassignColors");
        btnReassigncolors.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnReassigncolors_actionPerformed(e);
            }
        });
        panelStatusCursorInfo.add(btnReassigncolors, "cell 5 0");
        panelStatusCursorInfo.add(btnShowhideOpts, "cell 6 0");
        
        buttons_panel = new JPanel();
        buttons_panel.setPreferredSize(new Dimension(245, 300));
        buttons_panel.setMinimumSize(new Dimension(5, 100));
        splitPane.setRightComponent(buttons_panel);
        buttons_panel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        buttons_panel.add(scrollPane, "cell 0 0,grow");
        
        statusPanel = new JPanel();
        scrollPane.setViewportView(statusPanel);
        statusPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][][][]"));
        
        panel_general = new FEpanel_general();
        panel_general.setFrontend(this); //ho poso per separat perque sino windowbuilder peta
        statusPanel.add(panel_general,"cell 0 0 1 1,grow");

        panel_xywindow = new FEpanel_XYwindow();
        panel_xywindow.setFrontend(this);
        statusPanel.add(panel_xywindow, "cell 0 1 1 1,grow");
        
        panel_axes = new FEpanel_axes();
        panel_axes.setFrontend(this);
        statusPanel.add(panel_axes, "cell 0 2 1 1,grow");

        panel_otherOptions = new FEpanel_otherOptions();
        panel_otherOptions.setFrontend(this);
        statusPanel.add(panel_otherOptions, "cell 0 3 1 1,grow");

        inicia();
    }
    
    
    protected void inicia(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                            shiftPressed = true;
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                            shiftPressed = false;
                        }
                        break;
                    }
                    return false;
            }
        });
        int iconheight = 16;
        int inset = 2;
        int buttonSize = iconheight+2*inset;
        Image FIT = new ImageIcon(BasicPlotPanelFrontEnd.class.getResource("/com/vava33/BasicPlotPanel/img/fitWindow.png")).getImage().getScaledInstance(-100, iconheight, java.awt.Image.SCALE_SMOOTH);
        btnFitWindowStatus.setText("");
        btnFitWindowStatus.setIcon(new ImageIcon(FIT));
        Image LAT = new ImageIcon(BasicPlotPanelFrontEnd.class.getResource("/com/vava33/BasicPlotPanel/img/hideLateral.png")).getImage().getScaledInstance(-100, iconheight, java.awt.Image.SCALE_SMOOTH);
        btnShowhideOpts.setText("");
        btnShowhideOpts.setIcon(new ImageIcon(LAT));
        Image COL = new ImageIcon(BasicPlotPanelFrontEnd.class.getResource("/com/vava33/BasicPlotPanel/img/reassignColors.png")).getImage().getScaledInstance(-100, iconheight, java.awt.Image.SCALE_SMOOTH);
        btnReassigncolors.setText("");
        btnReassigncolors.setIcon(new ImageIcon(COL));
        
        btnFitWindowStatus.setPreferredSize(new Dimension(buttonSize,buttonSize));
        btnFitWindowStatus.setMaximumSize(new Dimension(buttonSize,buttonSize));
        btnFitWindowStatus.setMinimumSize(new Dimension(buttonSize,buttonSize));
        btnFitWindowStatus.setMargin(new Insets(inset,inset,inset,inset));
        btnShowhideOpts.setToolTipText("Reset View (fit patterns to window)");
        btnShowhideOpts.setPreferredSize(new Dimension(buttonSize,buttonSize));
        btnShowhideOpts.setMaximumSize(new Dimension(buttonSize,buttonSize));
        btnShowhideOpts.setMinimumSize(new Dimension(buttonSize,buttonSize));
        btnShowhideOpts.setMargin(new Insets(inset,inset,inset,inset));
        btnShowhideOpts.setToolTipText("Show/Hide Lateral Options Panel");
        btnReassigncolors.setPreferredSize(new Dimension(buttonSize,buttonSize));
        btnReassigncolors.setMaximumSize(new Dimension(buttonSize,buttonSize));
        btnReassigncolors.setMinimumSize(new Dimension(buttonSize,buttonSize));
        btnReassigncolors.setMargin(new Insets(inset,inset,inset,inset));
        btnReassigncolors.setToolTipText("Reassign colors to patterns");
        
        //HEM DE POSAR BE ELS CHECKBOX DEL LATERAL AMB ELS VALORS CORRECTES 
        //TODO:els inicia no se si posar-ho aqui o dins panel_XXX despres del constructor...
        this.panel_general.inicia();
        this.panel_axes.inicia();
        this.panel_xywindow.inicia();
        this.panel_otherOptions.inicia();
        
    }
    
    protected void do_btnShowhideOpts_actionPerformed(ActionEvent e) {
        this.showHideButtonsPanel();
    }

    public void showHideButtonsPanel() {
        int iconheight = 16;
        if (this.buttons_panel.isVisible()) {
            splitPanePosition=splitPane.getDividerLocation();    
            splitPanePosition = splitPanePosition/(double)(splitPane.getWidth()-splitPane.getDividerSize());
            this.buttons_panel.setVisible(false);
            //icona i tooltip per MOSTRAR
            Image LAT = new ImageIcon(BasicPlotPanelFrontEnd.class.getResource("/com/vava33/BasicPlotPanel/img/showLateral.png")).getImage().getScaledInstance(-100, iconheight, java.awt.Image.SCALE_SMOOTH);
            btnShowhideOpts.setIcon(new ImageIcon(LAT));

        }else {
            //recuperem
            this.buttons_panel.setVisible(true);
            if(splitPanePosition<0) {
                int posBut = splitPane.getWidth() - buttons_panel.getPreferredSize().width;
                splitPanePosition = posBut/(double)splitPane.getWidth();
                if (splitPanePosition<0)splitPanePosition=0.85;
            }
            splitPane.setDividerLocation(splitPanePosition);
            Image LAT = new ImageIcon(BasicPlotPanelFrontEnd.class.getResource("/com/vava33/BasicPlotPanel/img/hideLateral.png")).getImage().getScaledInstance(-100, iconheight, java.awt.Image.SCALE_SMOOTH);
            btnShowhideOpts.setIcon(new ImageIcon(LAT));
        }
    }


    @Override
    public void updateFields() {
        fillWindowValues();
        fillDivisionsValues();
    }
    
    @Override
    public void updateOtherValues() {
        panel_general.updateXtitleFromGraphPanel();
        panel_general.updateLegendPosFromGraphPanel();
    }
    
    @Override
    public void fillWindowValues(){
        this.panel_xywindow.fillWindowValues();
    }
    
    @Override
    public void fillDivisionsValues(){
        panel_axes.fillDivisionsValues();
    }
    
    private void do_graphPanel_mouseDragged(MouseEvent e) {
    
        Point2D.Double currentPoint = new Point2D.Double(e.getPoint().x, e.getPoint().y);
    
        if (this.mouseDrag == true && this.mouseMove) {//AQUEST BLOC JA ACTUALITZA PLOT
            double incX, incY;
            // agafem el dragpoint i l'actualitzem
            incX = currentPoint.x - dragPoint.x;
            incY = currentPoint.y - dragPoint.y;
            this.dragPoint = currentPoint;
            graphPanel.movePattern(incX, incY);
        }
        
        //WE DO SCROLL OR ZOOMOUT DEPENDING
        if (this.mouseDrag == true && this.mouseZoom) { //AQUEST BLOC JA ACTUALITZA PLOT
            double incY,incX;
            // agafem el dragpoint i l'actualitzem
            incX = currentPoint.x - dragPoint.x;
            incY = currentPoint.y - dragPoint.y;
            this.dragPoint = currentPoint;
            
            if (FastMath.abs(incX)>FastMath.abs(incY)){
                //fem scrolling
                graphPanel.scrollX(-incX);
            }else{
                //fem unzoom
                boolean zoomIn = (incY < 0);
                graphPanel.zoomX(zoomIn, FastMath.abs(incY));
            }
        }
        
        if (this.mouseDrag == true && graphPanel.isMouseBox() == true){
            Rectangle2D.Double rarea = graphPanel.getRectangleGraphArea();
            double rwidth = FastMath.abs(dragPoint.x-currentPoint.x);
            if (rwidth<minZoomPixels)return;
            double rheight = rarea.height;
            double yrect = rarea.y;
            if (sqSelect){ //afecta a x i a y
                rheight = FastMath.abs(dragPoint.y-currentPoint.y);
            }
            //drag
            //defecte drag cap a la dreta
            double xrect = dragPoint.x;
            if (dragPoint.x > currentPoint.x){
                //estem fent el drag cap a la esquerra, corregim vertex
                xrect = currentPoint.x;
            }
            if (sqSelect){ //afecta a x i a y
                //defecte drag cap avall
                yrect = dragPoint.y;
                if (dragPoint.y > currentPoint.y){
                    //drag cap amunt, corregim vertex
                    yrect = currentPoint.y;
                }
            }
            graphPanel.setZoomRect(new Rectangle2D.Double(xrect,yrect,rwidth,rheight));
        }
        graphPanel.actualitzaPlot();
    }

    protected void do_graphPanel_mouseMoved(MouseEvent e) {
        if (graphPanel.arePlottables()){
            Point2D.Double dp = graphPanel.getDataPointFromFramePoint(new Point2D.Double(e.getPoint().x, e.getPoint().y));
            if (dp!=null){
                lblTth.setText(String.format("%.4f", dp.getX()));
                lblInten.setText(String.format("%.1f", dp.getY()));
            }
        }
    }

    // Identificar el bot� i segons quin sigui moure o fer zoom
    protected void do_graphPanel_mousePressed(MouseEvent arg0) {
        if (!graphPanel.arePlottables())return;
        this.dragPoint = new Point2D.Double(arg0.getPoint().x, arg0.getPoint().y);

        if (arg0.getButton() == MOURE) {
            this.clickPoint = new Point2D.Double(arg0.getPoint().x, arg0.getPoint().y);
            this.mouseDrag = true;
            this.mouseMove = true;
        }
        if (arg0.getButton() == ZOOM_BORRAR) {
            this.mouseDrag = true;
            this.mouseZoom = true;
        }
        if (arg0.getButton() == CLICAR) {
            //abans d'aplicar el moure mirem si s'està fent alguna cosa
            if(this.shiftPressed){
                this.sqSelect=true;
            }else{
                this.sqSelect=false;
            }
            this.mouseDrag = true;
            graphPanel.setZoomRect(null); //reiniciem rectangle
            graphPanel.setMouseBox(true);
        }
        graphPanel.actualitzaPlot();

    }

    protected void do_graphPanel_mouseReleased(MouseEvent e) {
        if (!graphPanel.arePlottables())return;
        
        if (e.getButton() == MOURE){
            this.mouseDrag = false;
            this.mouseMove = false;
            Point2D.Double currentPoint = new Point2D.Double(e.getPoint().x, e.getPoint().y);
            if ((FastMath.abs(this.clickPoint.x-currentPoint.x)<0.5) && (FastMath.abs(this.clickPoint.y-currentPoint.y)<0.5)){
                graphPanel.fitGraph();
            }
        }
        if (e.getButton() == ZOOM_BORRAR){
            this.mouseDrag = false;
            this.mouseZoom = false;            
        }
        if (e.getButton() == CLICAR){
            graphPanel.setMouseBox(false);
        }

        if (e.getButton() == CLICAR) {
            //comprovem que no s'estigui fent una altra cosa          
            //COMPROVEM QUE HI HAGI UN MINIM D'AREA ENTREMIG (per evitar un click sol)
            if (FastMath.abs(e.getPoint().x-dragPoint.x)<minZoomPixels)return;
            if (this.sqSelect)if (FastMath.abs(e.getPoint().y-dragPoint.y)<minZoomPixels)return;

            Point2D.Double dataPointFinal = graphPanel.getDataPointFromFramePoint(new Point2D.Double(e.getPoint().x, e.getPoint().y));
            Point2D.Double dataPointInicial = graphPanel.getDataPointFromFramePoint(dragPoint);

            if (dataPointFinal == null && dataPointInicial==null){//els dos punts a fora
                return;
            }

            if (dataPointFinal == null){
                dataPointFinal = graphPanel.getDataPointFromFramePoint(new Point2D.Double(graphPanel.checkFrameXValue(e.getPoint().x),graphPanel.checkFrameYValue(e.getPoint().y)));
            }
            if (dataPointInicial==null){
                dataPointInicial = graphPanel.getDataPointFromFramePoint(new Point2D.Double(graphPanel.checkFrameXValue(dragPoint.x),graphPanel.checkFrameYValue(dragPoint.y)));
            }

            if (dataPointFinal == null || dataPointInicial==null){//algun punt final encara a fora!
                return;
            }

            double xrmin = FastMath.min(dataPointFinal.x, dataPointInicial.x);
            double xrmax = FastMath.max(dataPointFinal.x, dataPointInicial.x);
            if (this.sqSelect){
                double yrmin = FastMath.min(dataPointFinal.y, dataPointInicial.y);
                double yrmax = FastMath.max(dataPointFinal.y, dataPointInicial.y);
                graphPanel.applyWindowLimits(xrmin,xrmax,yrmin,yrmax);//ja fa actualitzar Plot
            }else {
                graphPanel.applyWindowLimitsX(xrmin,xrmax); //ja fa actualitzar Plot
            }
        }
        this.sqSelect=false;
    }

    private void do_graphPanel_mouseWheelMoved(MouseWheelEvent e) {
        Point2D.Double p = new Point2D.Double(e.getPoint().x, e.getPoint().y);
        boolean zoomIn = (e.getWheelRotation() < 0);
        graphPanel.zoomY(zoomIn, p); //ja actualitza plot
    }

    private void do_btnFitWindowStatus_actionPerformed(ActionEvent e) {
        graphPanel.fitGraph();
    }
    
    private void do_btnReassigncolors_actionPerformed(ActionEvent e) {
        panel_general.reAssignColorToAllPaintedTypes();
    }
    
    @Override
    public Options getVisualParametersToSave() {
        Options opt = new Options();
        
        opt.put("theme", Boolean.toString(graphPanel.isLightTheme()));
        opt.put("gridX", Boolean.toString(graphPanel.isShowGridX()));
        opt.put("gridY", Boolean.toString(graphPanel.isShowGridY()));
        opt.put("legend", Boolean.toString(graphPanel.isShowLegend()));
        opt.put("autoLeg", Boolean.toString(graphPanel.isAutoPosLegend()));
        if (!graphPanel.isAutoPosLegend()) {
            //save position
            opt.put("legX", Integer.toString(graphPanel.getLegendX()));
            opt.put("legY", Integer.toString(graphPanel.getLegendY()));
        }
        opt.put("legBkg", Boolean.toString(graphPanel.isLegend_opaque()));
        opt.put("yVert", Boolean.toString(graphPanel.isVerticalYAxe()));
        opt.put("yVertLabel", Boolean.toString(graphPanel.isVerticalYlabel()));
        opt.put("yVertNeg", Boolean.toString(graphPanel.isNegativeYAxisLabels()));
        opt.put("fixAxes", Boolean.toString(graphPanel.isFixAxes()));
        opt.put("hkl", Boolean.toString(graphPanel.isHkllabels()));
        
        double[] ranges = graphPanel.getWindowValues();
        opt.put("xRangeMin", FileUtils.dfX_4.format(ranges[0]));
        opt.put("xRangeMax", FileUtils.dfX_4.format(ranges[1]));
        opt.put("yRangeMin", FileUtils.dfX_4.format(ranges[2]));
        opt.put("yRangeMax", FileUtils.dfX_4.format(ranges[3]));
        opt.put("scaleX", FileUtils.dfX_4.format(graphPanel.getScaleFitXY()[0]));
        opt.put("scaleY", FileUtils.dfX_4.format(graphPanel.getScaleFitXY()[1]));
        opt.put("pointLabels",  Boolean.toString(graphPanel.isShowPointLabels()));
        opt.put("serieNameInGraph",  Boolean.toString(graphPanel.isShowSerieNameInGraph()));
        opt.put("serieNameInGraphOffX", Integer.toString(graphPanel.getSerieNameOffsetPixX()));
        opt.put("serieNameInGraphOffY", Integer.toString(graphPanel.getSerieNameOffsetPixY()));
        opt.put("zoneScale", Boolean.toString(graphPanel.isApplyScaleFactorT2()));
        opt.put("zoneScaleIni", FileUtils.dfX_4.format(graphPanel.getScaleFactorT2ang()));
        opt.put("zoneScaleFac", FileUtils.dfX_4.format(graphPanel.getScaleFactorT2fact()));
        
        double[] divs = graphPanel.getDivValues();
        opt.put("startValX", FileUtils.dfX_4.format(divs[0]));
        opt.put("incXprim", FileUtils.dfX_4.format(divs[1]));
        opt.put("ndivX", FileUtils.dfX_4.format(divs[2]));
        opt.put("startValY", FileUtils.dfX_4.format(divs[3]));
        opt.put("incYprim", FileUtils.dfX_4.format(divs[4]));
        opt.put("ndivY", FileUtils.dfX_4.format(divs[5]));
        opt.put("xLabel", graphPanel.getXlabel());
        opt.put("yLabel", graphPanel.getYlabel());
        opt.put("plotTitle", graphPanel.getPlotTitle());
        
        return opt;
    }
    
    @Override
    public void setVisualParametersFromSaved(Options opt) {
        try {
            panel_general.readOptions(opt);
            panel_axes.readOptions(opt);
            panel_xywindow.readOptions(opt);
            panel_otherOptions.readOptions(opt);
        }catch(Exception e) {
            log.warning("Error recovering visual parameters");
        }
        
        fillDivisionsValues(); //TODO move to the panel? or needed here at the end?
        fillWindowValues(); //TODO move to the panel? or needed here at the end?
        
        panel_axes.applyDivisions(); //TODO move to the panel? or needed here at the end?
        panel_xywindow.applyWindow(); //TODO move to the panel? or needed here at the end?

    }
    

    public int getMOURE() {
        return MOURE;
    }

    public void setMOURE(int mOURE) {
        MOURE = mOURE;
    }

    public int getCLICAR() {
        return CLICAR;
    }

    public void setCLICAR(int cLICAR) {
        CLICAR = cLICAR;
    }

    public int getZOOM_BORRAR() {
        return ZOOM_BORRAR;
    }

    public void setZOOM_BORRAR(int zOOM_BORRAR) {
        ZOOM_BORRAR = zOOM_BORRAR;
    }

    @Override
    public void setMouseButtons(int clicar, int moure, int zoom) {
        this.setCLICAR(clicar);
        this.setMOURE(moure);
        this.setZOOM_BORRAR(zoom);
    }

    @Override
    public int[] getMouseButtons() {
        return new int[] {CLICAR,MOURE,ZOOM_BORRAR};
    }
    
    @Override
    public T getGraphPanel() {
        return graphPanel;
    }

    //TODO move readOptions to plot1Dpanel?
    public void readOptions(Options opt) {

        if (opt==null)return;
        
        String but = opt.getValAsString("mouseButtonSelect", "Left");
        if (but.equalsIgnoreCase("Left"))CLICAR=MouseEvent.BUTTON1;
        if (but.equalsIgnoreCase("Middle"))CLICAR=MouseEvent.BUTTON2;
        if (but.equalsIgnoreCase("Right"))CLICAR=MouseEvent.BUTTON3;
        but = opt.getValAsString("mouseButtonMove", "Middle");
        if (but.equalsIgnoreCase("Left"))MOURE=MouseEvent.BUTTON1;
        if (but.equalsIgnoreCase("Middle"))MOURE=MouseEvent.BUTTON2;
        if (but.equalsIgnoreCase("Right"))MOURE=MouseEvent.BUTTON3;
        but = opt.getValAsString("mouseButtonZoom", "Right");
        if (but.equalsIgnoreCase("Left"))ZOOM_BORRAR=MouseEvent.BUTTON1;
        if (but.equalsIgnoreCase("Middle"))ZOOM_BORRAR=MouseEvent.BUTTON2;
        if (but.equalsIgnoreCase("Right"))ZOOM_BORRAR=MouseEvent.BUTTON3;

        String col = opt.getValAsString("colorTheme", "Light");
        if (col.equalsIgnoreCase("Light")){
            graphPanel.setLightTheme(true);
        }else {
            graphPanel.setLightTheme(false);
        }
        graphPanel.setIncXPrimPIXELS(opt.getValAsDouble("axisSepPrimaryXDivInAutoMode", graphPanel.getIncXPrimPIXELS()));
        graphPanel.setIncXSecPIXELS(opt.getValAsDouble("axisSepSecundaryXDivInAutoMode", graphPanel.getIncXSecPIXELS()));
        graphPanel.setIncYPrimPIXELS(opt.getValAsDouble("axisSepPrimaryYDivInAutoMode", graphPanel.getIncYPrimPIXELS()));
        graphPanel.setIncYSecPIXELS(opt.getValAsDouble("axisSepSecundaryYDivInAutoMode", graphPanel.getIncYSecPIXELS()));
        graphPanel.setDiv_PrimPixSize(opt.getValAsInteger("axisPrimDivSizePx", graphPanel.getDiv_PrimPixSize()));
        graphPanel.setDiv_SecPixSize(opt.getValAsInteger("axisSecunDivSizePx", graphPanel.getDiv_SecPixSize()));
        graphPanel.setFacZoom(opt.getValAsDouble("zoomFactor", graphPanel.getFacZoom()));
        graphPanel.setVerticalYlabel(opt.getValAsBoolean("axisVerticalYlabel", graphPanel.isVerticalYlabel()));
        graphPanel.setDef_axis_fsize(opt.getValAsFloat("axisFontSizeRelative", graphPanel.getDef_axis_fsize()));
        graphPanel.setDef_axisL_fsize(opt.getValAsFloat("axisLabelFontSizeRelative", graphPanel.getDef_axisL_fsize()));
        graphPanel.setDef_nDecimalsX(opt.getValAsInteger("axisDecimalsX", graphPanel.getDef_nDecimalsX()));
        graphPanel.setDef_nDecimalsY(opt.getValAsInteger("axisDecimalsY", graphPanel.getDef_nDecimalsY()));
        graphPanel.setDef_legend_fsize(opt.getValAsFloat("legendFontSizeRelative", graphPanel.getDef_legend_fsize()));
    }

    //per tal de saber tot el que es pot personalitzar
    public Options createOptionsObject() {
    
        Options opt = new Options();
        
        String but = "Left";
        if (CLICAR==MouseEvent.BUTTON2)but="Middle";
        if (CLICAR==MouseEvent.BUTTON3)but="Right";
        opt.put("mouseButtonSelect", but);
        but = "Middle";
        if (MOURE==MouseEvent.BUTTON1)but="Left";
        if (MOURE==MouseEvent.BUTTON3)but="Right";
        opt.put("mouseButtonMove", but);
        but = "Right";
        if (ZOOM_BORRAR==MouseEvent.BUTTON2)but="Middle";
        if (ZOOM_BORRAR==MouseEvent.BUTTON1)but="Left";
        opt.put("mouseButtonZoom", but);
        
        String col = "Light";
        if (!graphPanel.isLightTheme())col="Dark";
        opt.put("colorTheme", col);
        opt.put("axisSepPrimaryXDivInAutoMode", String.format("%.2f", graphPanel.getIncXPrimPIXELS()));
        opt.put("axisSepSecundaryXDivInAutoMode", String.format("%.2f", graphPanel.getIncXSecPIXELS()));
        opt.put("axisSepPrimaryYDivInAutoMode", String.format("%.2f", graphPanel.getIncYPrimPIXELS()));
        opt.put("axisSepSecundaryYDivInAutoMode", String.format("%.2f", graphPanel.getIncYSecPIXELS()));
        
        opt.put("axisPrimDivSizePx", String.format("%d", graphPanel.getDiv_PrimPixSize()));
        opt.put("axisSecunDivSizePx", String.format("%d", graphPanel.getDiv_SecPixSize()));
        opt.put("zoomFactor", String.format("%.2f", graphPanel.getFacZoom()));
        opt.put("axisVerticalYlabel", Boolean.toString(graphPanel.isVerticalYlabel()));
        opt.put("axisFontSizeRelative",  String.format("%.1f", graphPanel.getDef_axis_fsize()));
        opt.put("axisLabelFontSizeRelative",  String.format("%.1f", graphPanel.getDef_axisL_fsize()));
        opt.put("axisDecimalsX", String.format("%d", graphPanel.getDef_nDecimalsX()));
        opt.put("axisDecimalsY", String.format("%d", graphPanel.getDef_nDecimalsY()));
        opt.put("legendFontSizeRelative", String.format("%.1f", graphPanel.getDef_legend_fsize()));
        return opt;
    }


}
