package com.vava33.BasicPlotPanel;

/**
 * Basic extension of the FrontEnd panel
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 * TODO Una opcio es fer-ho modular amb panels creats apart (he començat al pkg frontend) pero no sé si val la pena i si és realment millor.
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
import com.vava33.BasicPlotPanel.core.SerieType;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.Options;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import org.apache.commons.math3.util.FastMath;

import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;

public class BasicPlotPanelFrontEnd<T extends Plot1DPanel<?>> extends JPanel implements Plot1DFrontend{

    private static final long serialVersionUID = 1L;
    private static VavaLogger log;
    public static VavaLogger getLog() {return log;}
    
    private double splitPanePosition = -1;

    protected static final int minZoomPixels = 5;

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
    
    private JTextField txtXdiv;
    private JTextField txtYdiv;
    private JCheckBox chckbxFixedAxis;
    private JTextField txtXmin;
    private JTextField txtYmin;
    private JTextField txtNdivx;
    private JTextField txtNdivy;
    private JPanel statusPanel;
    protected JLabel lblTth;

    private JPanel panel;
    private JPanel panel_1;
    private JPanel plotPanelContainer;
    private JTextField txtXminwin;
    private JTextField txtXmaxwin;
    private JTextField txtYminwin;
    private JTextField txtYmaxwin;
    protected JLabel lblInten;
    private JScrollPane scrollPane;
    private JSplitPane splitPane;
    private JPanel buttons_panel;
    private JPanel panelStatusCursorInfo;
    private JButton btnFitWindowStatus;
    private JButton btnShowhideOpts;
    private JLabel lblXmin;
    private JLabel lblYmin_1;
    private JLabel lblInix;
    private JLabel lblIncx;
    private JLabel lblSubx;
    private JLabel lblIniy;
    private JLabel lblIncy;
    private JLabel lblSuby;
    private JLabel lblXTitle;
    private JLabel lblYTitle;
    private JLabel lblTheme;
    private JPanel panel_2;
    private JCheckBox chckbxShow;
    private JCheckBox chckbxAutopos;
    private JTextField txtXlegend;
    private JTextField txtYlegend;
    private JPanel panel_3;
    protected JTextField txtXtitle;
    private JTextField txtYtitle;
    private JComboBox<String> comboTheme;
    private JCheckBox chckbxApply;
    private JLabel lblTIni;
    private JLabel lblFactor;
    private JTextField txtZonescalexini;
    private JTextField txtZonescalefactor;
    private JLabel lblGrid;
    private JCheckBox chckbxX;
    private JCheckBox chckbxY;
    private JCheckBox chckbxVerticalYAxis;
    private JCheckBox chckbxVerticalYLabel;
    private JCheckBox chckbxNegativeYLabels;
    private JButton btnReassigncolors;
    private JCheckBox chckbxOpaque;
    private JLabel lblTitle;
    private JTextField txtPlottitle;
    private JCheckBox chckbxShowPointLabels;
    private JCheckBox chckbxShowSerieName;
    private JPanel panel_4;
    private JCheckBox chckbxAddYbkg;
    private JCheckBox chckbxHklInfo;
    private JCheckBox chckbxRefIntensity;
    private JCheckBox chckbxSplitYref;
    protected JLabel lblDsp;
    protected JLabel lblHkl;

    protected T graphPanel;
    private JLabel lblYoffsetName;
    private JTextField txtYoffName;
    private JTextField txtXoffName;
    private JLabel lblPosxY;
    
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
        plotPanelContainer.add(graphPanel, "cell 0 0,grow");
        
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
        statusPanel.setLayout(new MigLayout("insets 0", "[][grow]", "[][][][][][][][][]"));
        
        lblTitle = new JLabel("Title");
        statusPanel.add(lblTitle, "cell 0 0,alignx trailing");
        
        txtPlottitle = new JTextField();
        txtPlottitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtPlottitle_actionPerformed(e);
            }
        });
        statusPanel.add(txtPlottitle, "cell 1 0,growx");
        txtPlottitle.setColumns(10);
        
        lblXTitle = new JLabel("X title");
        statusPanel.add(lblXTitle, "cell 0 1,alignx trailing");
        
        txtXtitle = new JTextField();
        txtXtitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXtitle_actionPerformed(e);
            }
        });
        txtXtitle.setText("xtitle");
        statusPanel.add(txtXtitle, "cell 1 1,growx");
        txtXtitle.setColumns(5);
        
        lblYTitle = new JLabel("Y title");
        statusPanel.add(lblYTitle, "cell 0 2,alignx trailing");
        
        txtYtitle = new JTextField();
        txtYtitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYtitle_actionPerformed(e);
            }
        });
        txtYtitle.setText("ytitle");
        statusPanel.add(txtYtitle, "cell 1 2,growx");
        txtYtitle.setColumns(5);
        
        lblTheme = new JLabel("Theme");
        statusPanel.add(lblTheme, "cell 0 3,alignx trailing");
        
        comboTheme = new JComboBox<String>();
        comboTheme.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_comboTheme_itemStateChanged(e);
            }
        });
        comboTheme.setModel(new DefaultComboBoxModel<String>(new String[] {"Light", "Dark"}));
        statusPanel.add(comboTheme, "cell 1 3,growx");
        
        panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Legend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        statusPanel.add(panel_2, "cell 0 4 2 1,grow");
        panel_2.setLayout(new MigLayout("insets 2", "[][grow][grow]", "[][]"));
        
        chckbxShow = new JCheckBox("Show");
        chckbxShow.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShow_itemStateChanged(e);
            }
        });
        panel_2.add(chckbxShow, "cell 0 0,alignx left");
        
        txtXlegend = new JTextField();
        txtXlegend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXlegend_actionPerformed(e);
            }
        });
        
        chckbxOpaque = new JCheckBox("Opaque");
        chckbxOpaque.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxOpaque_itemStateChanged(e);
            }
        });
        
        chckbxAutopos = new JCheckBox("AutoPos");
        chckbxAutopos.setToolTipText("Automatic Position");
        chckbxAutopos.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxAutopos_itemStateChanged(e);
            }
        });
        panel_2.add(chckbxAutopos, "cell 1 0,alignx left");
        panel_2.add(chckbxOpaque, "cell 2 0");
        
        lblPosxY = new JLabel("Pos (x y)");
        panel_2.add(lblPosxY, "cell 0 1,alignx trailing");
        txtXlegend.setText("Xlegend");
        panel_2.add(txtXlegend, "cell 1 1,growx");
        txtXlegend.setColumns(2);
        
        txtYlegend = new JTextField();
        txtYlegend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYlegend_actionPerformed(e);
            }
        });
        txtYlegend.setText("Ylegend");
        panel_2.add(txtYlegend, "cell 2 1,growx");
        txtYlegend.setColumns(2);
        
        panel = new JPanel();
        statusPanel.add(panel, "cell 0 5 2 1,growx");
        panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Axes divisions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setLayout(new MigLayout("insets 2", "[][grow][][grow]", "[][][][]"));
        
        lblInix = new JLabel("iniX");
        lblInix.setToolTipText("initial value X axis");
        panel.add(lblInix, "cell 0 0,alignx trailing");
        
        txtXmin = new JTextField();
        panel.add(txtXmin, "cell 1 0,growx");
        txtXmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXmin_actionPerformed(e);
            }
        });
        txtXmin.setText("Xmin");
        txtXmin.setColumns(3);
        
        lblIniy = new JLabel("iniY");
        lblIniy.setToolTipText("initial value Y axis");
        panel.add(lblIniy, "cell 2 0,alignx trailing");
        
        txtYmin = new JTextField();
        panel.add(txtYmin, "cell 3 0,growx");
        txtYmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYmin_actionPerformed(e);
            }
        });
        txtYmin.setText("ymin");
        txtYmin.setColumns(3);
        
        lblIncx = new JLabel("incX");
        lblIncx.setToolTipText("increment X major ticks");
        panel.add(lblIncx, "cell 0 1,alignx trailing");
        
        txtXdiv = new JTextField();
        txtXdiv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXdiv_actionPerformed(e);
            }
        });
        panel.add(txtXdiv, "cell 1 1,growx");
        txtXdiv.setText("xdiv");
        txtXdiv.setColumns(3);
        
        lblIncy = new JLabel("incY");
        lblIncy.setToolTipText("increment Y major ticks");
        panel.add(lblIncy, "cell 2 1,alignx trailing");
        
        txtYdiv = new JTextField();
        txtYdiv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYdiv_actionPerformed(e);
            }
        });
        panel.add(txtYdiv, "cell 3 1,growx");
        txtYdiv.setText("ydiv");
        txtYdiv.setColumns(3);
        
        lblSubx = new JLabel("divX");
        lblSubx.setToolTipText("subdivisions in X axis");
        panel.add(lblSubx, "cell 0 2,alignx trailing");
        
        txtNdivx = new JTextField();
        txtNdivx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtNdivx_actionPerformed(e);
            }
        });
        panel.add(txtNdivx, "cell 1 2,growx");
        txtNdivx.setText("NdivX");
        txtNdivx.setColumns(3);
        
        lblSuby = new JLabel("divY");
        lblSuby.setToolTipText("subdivisions in Y axis");
        panel.add(lblSuby, "cell 2 2,alignx trailing");
        
        txtNdivy = new JTextField();
        txtNdivy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtNdivy_actionPerformed(e);
            }
        });
        panel.add(txtNdivy, "cell 3 2,growx");
        txtNdivy.setText("NdivY");
        txtNdivy.setColumns(3);
        
        chckbxFixedAxis = new JCheckBox("Fix Axes");
        panel.add(chckbxFixedAxis, "cell 0 3 2 1,alignx left");
        chckbxFixedAxis.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxFixedAxis_itemStateChanged(e);
            }
        });
        
        chckbxNegativeYLabels = new JCheckBox("Neg. Y labels");
        chckbxNegativeYLabels.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxNegativeYLabels_itemStateChanged(e);
            }
        });
        panel.add(chckbxNegativeYLabels, "cell 2 3 2 1");
        
        panel_1 = new JPanel();
        statusPanel.add(panel_1, "cell 0 6 2 1,growx");
        panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "X,Y window", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setLayout(new MigLayout("insets 2", "[][grow][grow][grow]", "[][][][][][][]"));
        
        txtXminwin = new JTextField();
        txtXminwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXminwin_actionPerformed(e);
            }
        });
        
        lblXmin = new JLabel("Xmin max");
        panel_1.add(lblXmin, "cell 0 0,alignx trailing");
        panel_1.add(txtXminwin, "cell 1 0 2 1,growx");
        txtXminwin.setColumns(5);
        
        txtXmaxwin = new JTextField();
        txtXmaxwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXmaxwin_actionPerformed(e);
            }
        });
        panel_1.add(txtXmaxwin, "cell 3 0,growx");
        txtXmaxwin.setColumns(5);
        
        lblYmin_1 = new JLabel("Ymin max");
        panel_1.add(lblYmin_1, "cell 0 1,alignx trailing");
        
        txtYminwin = new JTextField();
        txtYminwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYminwin_actionPerformed(e);
            }
        });
        panel_1.add(txtYminwin, "cell 1 1 2 1,growx");
        txtYminwin.setColumns(5);
        
        txtYmaxwin = new JTextField();
        txtYmaxwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYmaxwin_actionPerformed(e);
            }
        });
        panel_1.add(txtYmaxwin, "cell 3 1,growx");
        txtYmaxwin.setColumns(5);
        
        lblGrid = new JLabel("Grid");
        panel_1.add(lblGrid, "cell 0 2,alignx trailing");
        
        chckbxX = new JCheckBox("X");
        chckbxX.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxX_itemStateChanged(e);
            }
        });
        panel_1.add(chckbxX, "cell 1 2,alignx center");
        
        chckbxVerticalYAxis = new JCheckBox("Y axis");
        chckbxVerticalYAxis.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxVerticalYAxis_itemStateChanged(e);
            }
        });
        
        chckbxY = new JCheckBox("Y");
        chckbxY.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxY_itemStateChanged(e);
            }
        });
        panel_1.add(chckbxY, "cell 2 2 2 1,alignx left");
        panel_1.add(chckbxVerticalYAxis, "cell 0 3 2 1,alignx left");
        
        chckbxShowPointLabels = new JCheckBox("Point labels");
        chckbxShowPointLabels.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShowPointLabels_itemStateChanged(e);
            }
        });
        
        chckbxVerticalYLabel = new JCheckBox("Vert. label");
        chckbxVerticalYLabel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxVerticalYLabel_itemStateChanged(e);
            }
        });
        panel_1.add(chckbxVerticalYLabel, "cell 2 3 2 1,alignx left");
        panel_1.add(chckbxShowPointLabels, "cell 0 4 2 1");
        
        chckbxShowSerieName = new JCheckBox("Series Name");
        chckbxShowSerieName.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShowSerieName_itemStateChanged(e);
            }
        });
        panel_1.add(chckbxShowSerieName, "cell 2 4 2 1");
        
        lblYoffsetName = new JLabel("Name Offset (x,y)");
        panel_1.add(lblYoffsetName, "cell 0 5 4 1,alignx center");
        
        txtXoffName = new JTextField();
        panel_1.add(txtXoffName, "cell 0 6 2 1,growx");
        txtXoffName.setColumns(10);
        
        txtYoffName = new JTextField();
        txtYoffName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYoffName_actionPerformed(e);
            }
        });
        panel_1.add(txtYoffName, "cell 2 6 2 1,growx");
        txtYoffName.setColumns(5);
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Zone scale", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        statusPanel.add(panel_3, "cell 0 7 2 1,grow");
        panel_3.setLayout(new MigLayout("insets 2", "[][][grow][][grow]", "[]"));
        
        chckbxApply = new JCheckBox("");
        chckbxApply.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxApply_itemStateChanged(e);
            }
        });
        panel_3.add(chckbxApply, "cell 0 0,alignx left");
        
        lblTIni = new JLabel("Xini");
        panel_3.add(lblTIni, "cell 1 0,alignx trailing");
        
        txtZonescalexini = new JTextField();
        txtZonescalexini.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                do_txtZonescalexini_keyReleased(e);
            }
        });
        txtZonescalexini.setText("zoneScaleXini");
        panel_3.add(txtZonescalexini, "cell 2 0,growx");
        txtZonescalexini.setColumns(5);
        
        lblFactor = new JLabel("Fac");
        panel_3.add(lblFactor, "cell 3 0,alignx trailing");
        
        txtZonescalefactor = new JTextField();
        txtZonescalefactor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                do_txtZonescalefactor_keyReleased(e);
            }
        });
        txtZonescalefactor.setText("zoneScaleFactor");
        panel_3.add(txtZonescalefactor, "cell 4 0,growx");
        txtZonescalefactor.setColumns(5);
        
        panel_4 = new JPanel();
        panel_4.setBorder(new TitledBorder(null, "Other options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        statusPanel.add(panel_4, "cell 0 8 2 1,grow");
        panel_4.setLayout(new MigLayout("", "[grow][grow]", "[][]"));
        
        chckbxAddYbkg = new JCheckBox("Add Ybkg");
        chckbxAddYbkg.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxAddYbkg_itemStateChanged(e);
            }
        });
        panel_4.add(chckbxAddYbkg, "cell 0 0");
        
        chckbxHklInfo = new JCheckBox("HKL info");
        chckbxHklInfo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxHklInfo_itemStateChanged(e);
            }
        });
        panel_4.add(chckbxHklInfo, "cell 1 0");
        
        chckbxRefIntensity = new JCheckBox("REF intensity");
        chckbxRefIntensity.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxRefIntensity_itemStateChanged(e);
            }
        });
        panel_4.add(chckbxRefIntensity, "cell 0 1");
        
        chckbxSplitYref = new JCheckBox("Split Yref");
        chckbxSplitYref.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxSplitYref_itemStateChanged(e);
            }
        });
        panel_4.add(chckbxSplitYref, "cell 1 1");

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
        if (graphPanel.isLightTheme()) {
            comboTheme.setSelectedIndex(0);
        }else {
            comboTheme.setSelectedIndex(1);
        }
        this.txtXtitle.setText(graphPanel.getXlabel());
        this.txtYtitle.setText(graphPanel.getYlabel());
        this.txtPlottitle.setText(graphPanel.getPlotTitle());
        this.chckbxShow.setSelected(graphPanel.isShowLegend());
        this.chckbxAutopos.setSelected(graphPanel.isAutoPosLegend());
        this.txtXlegend.setText(Integer.toString(graphPanel.getLegendX())); //TODO mirar que passa quan hi ha canvi (que es crida i com actua)
        this.txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
        this.txtZonescalexini.setText(FileUtils.dfX_2.format(graphPanel.getScaleFactorT2ang()));
        this.txtZonescalefactor.setText(FileUtils.dfX_2.format(graphPanel.getScaleFactorT2fact()));
        this.chckbxX.setSelected(graphPanel.isShowGridX());
        this.chckbxY.setSelected(graphPanel.isShowGridY());
        this.chckbxShow.setSelected(graphPanel.isShowLegend());
        this.chckbxVerticalYAxis.setSelected(graphPanel.isVerticalYAxe());
        this.chckbxVerticalYLabel.setSelected(graphPanel.isVerticalYlabel());
        this.chckbxFixedAxis.setSelected(graphPanel.isFixAxes());
        this.chckbxNegativeYLabels.setSelected(graphPanel.isNegativeYAxisLabels());
        this.chckbxOpaque.setSelected(graphPanel.isLegend_opaque());
        this.chckbxShowPointLabels.setSelected(graphPanel.isShowPointLabels());
        this.chckbxShowSerieName.setSelected(graphPanel.isShowSerieNameInGraph());
        this.chckbxHklInfo.setSelected(graphPanel.isHkllabels());
        this.txtYoffName.setText(Integer.toString(graphPanel.getSerieNameOffsetPixY()));

        this.txtNdivx.setText("");
        this.txtNdivy.setText("");
        this.txtXdiv.setText("");
        this.txtXmin.setText("");
        this.txtYdiv.setText("");
        this.txtYmin.setText("");
        
        if (!graphPanel.isFixAxes()) {
            txtNdivx.setEditable(false);
            txtNdivy.setEditable(false);
            txtXdiv.setEditable(false);
            txtYdiv.setEditable(false);
        }
        
    }
    
    protected void do_btnShowhideOpts_actionPerformed(ActionEvent e) {
        this.showHideButtonsPanel();
    }

    public void showHideButtonsPanel() {
        int iconheight = 16;
        if (this.buttons_panel.isVisible()) {
            splitPanePosition=splitPane.getDividerLocation();    
            splitPanePosition = splitPanePosition/(double)splitPane.getWidth();
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

    public void reAssignColorToAllPaintedTypes() {
        graphPanel.getDataToPlot().reAssignColorPlottables(true, BasicSerie.getPaintedTypes().toArray(new SerieType[0]));
    }

    @Override
    public void updateFields() {
        fillWindowValues();
        fillDivisionsValues();
    }
    
    @Override
    public void updateOtherValues() {
        this.txtXtitle.setText(graphPanel.getXlabel());
    }
    
    private void applyWindow(){
        double xrangeMin=Double.parseDouble(txtXminwin.getText());
        double xrangeMax=Double.parseDouble(txtXmaxwin.getText());
        double yrangeMin=Double.parseDouble(txtYminwin.getText());
        double yrangeMax=Double.parseDouble(txtYmaxwin.getText());
        graphPanel.applyWindowLimits(xrangeMin,xrangeMax,yrangeMin,yrangeMax);

    }
    
    @Override
    public void fillWindowValues(){
        double[] minmaxXYrange = graphPanel.getWindowValues();
        this.txtXminwin.setText(FileUtils.dfX_3.format(minmaxXYrange[0]));
        this.txtXmaxwin.setText(FileUtils.dfX_3.format(minmaxXYrange[1]));
        this.txtYminwin.setText(FileUtils.dfX_3.format(minmaxXYrange[2]));
        this.txtYmaxwin.setText(FileUtils.dfX_3.format(minmaxXYrange[3]));
    }
    
    @Override
    public void fillDivisionsValues(){
        double[] divValues = graphPanel.getDivValues();
        this.txtXmin.setText(FileUtils.dfX_3.format(divValues[0]));
        this.txtXdiv.setText(FileUtils.dfX_3.format(divValues[1]));
        this.txtNdivx.setText(FileUtils.dfX_3.format(divValues[2]));
        this.txtYmin.setText(FileUtils.dfX_3.format(divValues[3]));
        this.txtYdiv.setText(FileUtils.dfX_3.format(divValues[4]));
        this.txtNdivy.setText(FileUtils.dfX_3.format(divValues[5]));
    }
    
    private void applyDivisions(){
        double iniX=Double.parseDouble(txtXmin.getText());
        double iniY=Double.parseDouble(txtYmin.getText());
        double incX=Double.parseDouble(txtXdiv.getText());
        double incY=Double.parseDouble(txtYdiv.getText());
        double ndivX=Double.parseDouble(txtNdivx.getText());
        double ndivY=Double.parseDouble(txtNdivy.getText());
        graphPanel.applyDivisions(iniX, FastMath.abs(incX), FastMath.abs(ndivX), iniY, FastMath.abs(incY), FastMath.abs(ndivY)); //TODO he fet fastmath perque petava
        
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

    //AQUEST HKLINFO ES L'UNIC QUE NO AFECTA EL DRAWING...
    protected void do_chckbxHklInfo_itemStateChanged(ItemEvent e) {
        graphPanel.setHkllabels(chckbxHklInfo.isSelected());
        graphPanel.actualitzaPlot();
    }
    
    private void do_chckbxFixedAxis_itemStateChanged(ItemEvent e) {
        graphPanel.setFixAxes(chckbxFixedAxis.isSelected());
        if (!graphPanel.isFixAxes()){
            txtNdivx.setEditable(false);
            txtNdivy.setEditable(false);
            txtXdiv.setEditable(false);
            txtYdiv.setEditable(false);
        }else{
            txtNdivx.setEditable(true);
            txtNdivy.setEditable(true);
            txtXdiv.setEditable(true);
            txtYdiv.setEditable(true);            
        }
    }

    private void do_txtNdivy_actionPerformed(ActionEvent e) {
         this.applyDivisions();
    }
    private void do_txtYdiv_actionPerformed(ActionEvent e) {
        this.applyDivisions();
    }
    private void do_txtNdivx_actionPerformed(ActionEvent e) {
        this.applyDivisions();
    }
    private void do_txtXdiv_actionPerformed(ActionEvent e) {
        this.applyDivisions();
    }
    private void do_txtYmin_actionPerformed(ActionEvent e) {
        this.applyDivisions();
    }
    private void do_txtXmin_actionPerformed(ActionEvent e) {
        this.applyDivisions();
    }
    private void do_txtXminwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    protected void do_txtYminwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    private void do_txtXmaxwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    private void do_txtYmaxwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    private void do_btnReassigncolors_actionPerformed(ActionEvent e) {
        reAssignColorToAllPaintedTypes();
    }
    
    protected void do_txtXtitle_actionPerformed(ActionEvent e) {
        if (txtXtitle.getText().isEmpty()){
            graphPanel.setCustomXtitle(false);
        }else{
            graphPanel.setXlabel(txtXtitle.getText());
            graphPanel.setCustomXtitle(true);
        }   
        graphPanel.actualitzaPlot();
    }
    
    private void do_txtYtitle_actionPerformed(ActionEvent e) {
        graphPanel.setYlabel(txtYtitle.getText());
        graphPanel.actualitzaPlot();
    }
    
    private void do_txtYoffName_actionPerformed(ActionEvent e) {
        if (this.chckbxShowSerieName.isSelected()) {
            int off=0;
            try {
                off = Integer.parseInt(txtYoffName.getText());
            }catch(NumberFormatException ex) {
                log.info("Invalid offset, introduce an integer number");
            }
            graphPanel.setSerieNameOffsetPixY(off);
            graphPanel.actualitzaPlot();
        }
    }
    
    private void do_txtPlottitle_actionPerformed(ActionEvent e) {
        graphPanel.setPlotTitle(txtPlottitle.getText());
        graphPanel.actualitzaPlot();
    }
    
    private void do_comboTheme_itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.DESELECTED)return;
        if (comboTheme.getSelectedItem().toString().equalsIgnoreCase("Light")){
            graphPanel.setLightTheme(true);
        }else{
            graphPanel.setLightTheme(false);
        }

        if (graphPanel.arePlottables()){
            boolean repaint = FileUtils.YesNoDialog(null, "Repaint current patterns?");
            if(repaint){
                this.reAssignColorToAllPaintedTypes();
            }
        }
    }
    
    private void do_chckbxShow_itemStateChanged(ItemEvent e) {
        graphPanel.setShowLegend(chckbxShow.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxAutopos_itemStateChanged(ItemEvent e) {
        graphPanel.setAutoPosLegend(chckbxAutopos.isSelected());
        if (chckbxAutopos.isSelected()){
            txtXlegend.setEditable(false);
            txtYlegend.setEditable(false);
        }else{
            txtXlegend.setEditable(true);
            txtYlegend.setEditable(true);
        }
        //legend pos Actualitzem sempre
        txtXlegend.setText(Integer.toString(graphPanel.getLegendX()));
        txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
        graphPanel.actualitzaPlot();
    }
    private void do_txtXlegend_actionPerformed(ActionEvent e) {
        try{
            int lx = Integer.parseInt(txtXlegend.getText());
            graphPanel.setLegendX(lx);
            txtXlegend.setText(Integer.toString(graphPanel.getLegendX()));
            graphPanel.actualitzaPlot();
        }catch(Exception ex){
            log.warning("Error reading legend X position");
        }
    }
    private void do_txtYlegend_actionPerformed(ActionEvent e) {
        try{
            int ly = Integer.parseInt(txtYlegend.getText());
            graphPanel.setLegendY(ly);
            txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
            graphPanel.actualitzaPlot();
        }catch(Exception ex){
            log.warning("Error reading legend Y position");
        }
    }
    private void do_chckbxApply_itemStateChanged(ItemEvent e) {
        applyPartialYscale();

    }
    private void do_txtZonescalexini_keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER) {
            applyPartialYscale();
        }
    }
    private void do_txtZonescalefactor_keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER) {
            applyPartialYscale();
        }
    }
    private void applyPartialYscale() {
        //default values
        float t2ini=30;
        float t2fac=1.0f;
        try {
            t2ini=Float.parseFloat(txtZonescalexini.getText());
            t2fac=Float.parseFloat(txtZonescalefactor.getText());
        }catch(Exception ex) {
            log.warning("Error reading t2 value or factor");
            txtZonescalexini.setText("30.0");
            txtZonescalefactor.setText("1.0");
        }
        graphPanel.setPartialYScale(chckbxApply.isSelected(),t2ini,t2fac);
        graphPanel.actualitzaPlot();
    }
    
    private void do_chckbxX_itemStateChanged(ItemEvent e) {
        graphPanel.setShowGridX(chckbxX.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxY_itemStateChanged(ItemEvent e) {
        graphPanel.setShowGridY(chckbxY.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxVerticalYAxis_itemStateChanged(ItemEvent e) {
        graphPanel.setVerticalYAxe(chckbxVerticalYAxis.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxVerticalYLabel_itemStateChanged(ItemEvent e) {
        graphPanel.setVerticalYlabel(chckbxVerticalYLabel.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxNegativeYLabels_itemStateChanged(ItemEvent e) {
        graphPanel.setNegativeYAxisLabels(chckbxNegativeYLabels.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxOpaque_itemStateChanged(ItemEvent e) {
        graphPanel.setLegend_opaque(chckbxOpaque.isSelected());
        graphPanel.actualitzaPlot();
    }

    private void do_chckbxShowPointLabels_itemStateChanged(ItemEvent e) {
        graphPanel.setShowPointLabels(chckbxShowPointLabels.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxShowSerieName_itemStateChanged(ItemEvent e) {
        graphPanel.setShowSerieNameInGraph(chckbxShowSerieName.isSelected());
        graphPanel.actualitzaPlot();
    }
    
    private void do_chckbxAddYbkg_itemStateChanged(ItemEvent e) {
        graphPanel.setPlotwithbkg(chckbxAddYbkg.isSelected());
        graphPanel.actualitzaPlot();
    }

    private void do_chckbxRefIntensity_itemStateChanged(ItemEvent e) {
        graphPanel.setShowDBCompoundIntensity(chckbxRefIntensity.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxSplitYref_itemStateChanged(ItemEvent e) {
        graphPanel.setSplitDBCompound(chckbxSplitYref.isSelected());
        graphPanel.actualitzaPlot();
    }
    
    public void setPlotwithbkg(boolean plotwithbkg) {
        graphPanel.setPlotwithbkg(plotwithbkg);
        graphPanel.actualitzaPlot();
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
            boolean light = true;
            if(this.comboTheme.getSelectedIndex()==1)light=false;
            light = opt.getValAsBoolean("theme", light);
            if (light) {
                this.comboTheme.setSelectedIndex(0);
            }else {
                this.comboTheme.setSelectedIndex(1);
            }
            
            //TODO si no es vol (o peta) fent servir els chckbox per default, millor agafar els de graphpanel
            this.chckbxX.setSelected(opt.getValAsBoolean("gridX", this.chckbxX.isSelected()));
            this.chckbxY.setSelected(opt.getValAsBoolean("gridY", this.chckbxY.isSelected()));
            if (!graphPanel.isAutoPosLegend()) {
              this.txtXlegend.setText(opt.getValAsString("legX", this.txtXlegend.getText()));
              this.txtYlegend.setText(opt.getValAsString("legY", this.txtYlegend.getText()));
              this.do_txtXlegend_actionPerformed(null);
              this.do_txtYlegend_actionPerformed(null);
            }
            this.chckbxShow.setSelected(opt.getValAsBoolean("legend", this.chckbxShow.isSelected()));
            this.chckbxAutopos.setSelected(opt.getValAsBoolean("autoLeg", this.chckbxAutopos.isSelected()));
            this.chckbxOpaque.setSelected(opt.getValAsBoolean("legBkg", this.chckbxOpaque.isSelected()));
            this.chckbxVerticalYAxis.setSelected(opt.getValAsBoolean("yVert", this.chckbxVerticalYAxis.isSelected()));
            this.chckbxVerticalYLabel.setSelected(opt.getValAsBoolean("yVertLabel", this.chckbxVerticalYLabel.isSelected()));
            this.chckbxNegativeYLabels.setSelected(opt.getValAsBoolean("yVertNeg", this.chckbxNegativeYLabels.isSelected()));
            this.chckbxFixedAxis.setSelected(opt.getValAsBoolean("fixAxes", this.chckbxFixedAxis.isSelected()));
            this.chckbxHklInfo.setSelected(opt.getValAsBoolean("hkl", this.chckbxHklInfo.isSelected()));
            this.chckbxShowPointLabels.setSelected(opt.getValAsBoolean("pointLabels", this.chckbxShowPointLabels.isSelected()));
            this.txtXoffName.setText(opt.getValAsString("serieNameInGraphOffX", Integer.toString(graphPanel.getSerieNameOffsetPixX())));
            this.txtYoffName.setText(opt.getValAsString("serieNameInGraphOffY", Integer.toString(graphPanel.getSerieNameOffsetPixY())));
            this.chckbxShowSerieName.setSelected(opt.getValAsBoolean("serieNameInGraph", this.chckbxShowSerieName.isSelected()));
            //TODO offset name
            double[] ranges = graphPanel.getWindowValues();
            double xrangeMin=opt.getValAsDouble("xRangeMin",ranges[0]);
            double xrangeMax=opt.getValAsDouble("xRangeMax",ranges[1]);
            double yrangeMin=opt.getValAsDouble("yRangeMin",ranges[2]);
            double yrangeMax=opt.getValAsDouble("yRangeMax",ranges[3]);
            graphPanel.applyWindowLimits(xrangeMin,xrangeMax,yrangeMin,yrangeMax);
            double scalefitX=opt.getValAsDouble("scaleX",graphPanel.getScaleFitXY()[0]);
            double scalefitY=opt.getValAsDouble("scaleY",graphPanel.getScaleFitXY()[1]);
            graphPanel.setScaleFitXY(scalefitX,scalefitY);

            this.txtZonescalexini.setText(opt.getValAsString("zoneScaleIni", txtZonescalexini.getText()));
            this.txtZonescalefactor.setText(opt.getValAsString("zoneScaleFac", txtZonescalefactor.getText()));
            this.chckbxApply.setSelected(opt.getValAsBoolean("zoneScale", this.chckbxApply.isSelected()));
            
            double[] divs = graphPanel.getDivValues();
            
            double iniX=opt.getValAsDouble("startValX",divs[0]);
            double incX=opt.getValAsDouble("incXprim",divs[1]);
            double ndivX=opt.getValAsDouble("ndivX",divs[2]);
            double iniY=opt.getValAsDouble("startValY",divs[3]);
            double incY=opt.getValAsDouble("incYprim",divs[4]);
            double ndivY=opt.getValAsDouble("ndivY",divs[5]);
            graphPanel.applyDivisions(iniX, incX, ndivX, iniY, incY, ndivY);
            
            String xlabel = opt.getValAsString("xLabel", graphPanel.getXlabel());
            String ylabel = opt.getValAsString("yLabel", graphPanel.getYlabel());
            String plotTitle = opt.getValAsString("plotTitle", graphPanel.getPlotTitle());
            graphPanel.setXlabel(xlabel);
            graphPanel.setYlabel(ylabel);
            graphPanel.setPlotTitle(plotTitle);
            this.txtXtitle.setText(xlabel);
            this.txtYtitle.setText(ylabel);
            this.txtPlottitle.setText(plotTitle);
            
        }catch(Exception e) {
            log.warning("Error recovering visual parameters");
        }
        
        fillDivisionsValues();
        fillWindowValues();
        
        applyDivisions();
        applyWindow();

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
