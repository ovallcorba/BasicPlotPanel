package com.vava33.BasicPlotPanel;

/**
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.TitledBorder;

import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.jutils.FileUtils;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Plot1DPanel<?> graphPanel;
    Plot1DFrontend fe;

    private final JPanel contentPanel = new JPanel();
    private JComboBox<String> comboMouseSelect;
    private JComboBox<String> comboMouseMove;
    private JComboBox<String> comboMouseZoom;
    private JTextField txtDivXprimIncPx;
    private JTextField txtDivXsecIncPx;
    private JTextField txtDivYprimIncPx;
    private JTextField txtDivYsecIncPx;
    private JTextField txtDivprimsizepx;
    private JTextField txtDivsecsizepx;
    private JTextField txtAxlabelfontsizerel;
    private JTextField txtAxvaluesfontsizerel;
    private JTextField txtAxxdecimals;
    private JTextField txtAxydecimals;
    private JTextField txtLegfontsizerel;
    private JTextField txtZoomfactorstep;
    private JPanel panel_axes;
    private JTextField txtCustomplotwidth;
    private JTextField txtCustomplotheight;
    private JCheckBox chckbxCustomSize;
    

    public OptionsDialog(Plot1DFrontend fe) {
        setTitle("d1Dplot additional options");
        initGUI();
        this.fe=fe;
        graphPanel=fe.getGraphPanel();
//        inicia(); ja ho fa el setvisible
    }
    private void initGUI() {
        setBounds(100, 100, 652, 480);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
        
        final JPanel panel_col1 = new JPanel();
        contentPanel.add(panel_col1, "cell 0 0,grow");
        panel_col1.setLayout(new MigLayout("", "[]", "[][]"));
        {
            final JPanel panel = new JPanel();
            panel_col1.add(panel, "cell 0 0,growx,aligny top");
            panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Axes Division Lines", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
            panel.setLayout(new MigLayout("", "[][grow]", "[][][][][][]"));
            
            final JLabel lblNewLabel = new JLabel("Separation, primary X (px)");
            lblNewLabel.setToolTipText("Separation between primary division lines of X axis in auto mode (in pixels)");
            panel.add(lblNewLabel, "cell 0 0,alignx trailing");
            
            txtDivXprimIncPx = new JTextField();
            txtDivXprimIncPx.setText("divXprimIncPx");
            panel.add(txtDivXprimIncPx, "cell 1 0,growx");
            txtDivXprimIncPx.setColumns(10);
            
            final JLabel lblSecondaryXDiv = new JLabel("Separation, secondary X (px)");
            lblSecondaryXDiv.setToolTipText("Secondary X div lines separation in auto mode (px)");
            panel.add(lblSecondaryXDiv, "cell 0 1,alignx trailing");
            
            txtDivXsecIncPx = new JTextField();
            txtDivXsecIncPx.setText("divXsecIncPx");
            panel.add(txtDivXsecIncPx, "cell 1 1,growx");
            txtDivXsecIncPx.setColumns(10);
            
            final JLabel lblMainYDiv = new JLabel("Separation, primary Y (px)");
            lblMainYDiv.setToolTipText("Primary Y div lines separation in auto mode (px)");
            panel.add(lblMainYDiv, "cell 0 2,alignx trailing");
            
            txtDivYprimIncPx = new JTextField();
            txtDivYprimIncPx.setText("divYprimIncPx");
            panel.add(txtDivYprimIncPx, "cell 1 2,growx");
            txtDivYprimIncPx.setColumns(10);
            
            final JLabel lblSecondaryYDiv = new JLabel("Separation, secondary Y (px)");
            lblSecondaryYDiv.setToolTipText("Secondary Y div lines separation in auto mode (px)");
            panel.add(lblSecondaryYDiv, "cell 0 3,alignx trailing");
            
            txtDivYsecIncPx = new JTextField();
            txtDivYsecIncPx.setText("divYsecIncPx");
            panel.add(txtDivYsecIncPx, "cell 1 3,growx");
            txtDivYsecIncPx.setColumns(10);
            
            final JLabel lblMainSepLine = new JLabel("Size, primary (px)");
            lblMainSepLine.setToolTipText("Size in pixels of the principal division lines");
            panel.add(lblMainSepLine, "cell 0 4,alignx trailing");
            
            txtDivprimsizepx = new JTextField();
            txtDivprimsizepx.setText("divPrimSizePx");
            panel.add(txtDivprimsizepx, "cell 1 4,growx");
            txtDivprimsizepx.setColumns(10);
            
            final JLabel lblSizeSecondarypx = new JLabel("Size, secondary (px)");
            lblSizeSecondarypx.setToolTipText("Size in pixels of the secondary division lines");
            panel.add(lblSizeSecondarypx, "cell 0 5,alignx trailing");
            
            txtDivsecsizepx = new JTextField();
            txtDivsecsizepx.setText("divSecSizePx");
            panel.add(txtDivsecsizepx, "cell 1 5,growx");
            txtDivsecsizepx.setColumns(10);
        }
        
        final JPanel panel_col2 = new JPanel();
        contentPanel.add(panel_col2, "cell 1 0,grow");
        panel_col2.setLayout(new MigLayout("", "[grow]", "[][][grow]"));
        {
            final JPanel panel = new JPanel();
            panel_col2.add(panel, "cell 0 0,growx,aligny top");
            panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Mouse Buttons", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
            panel.setLayout(new MigLayout("", "[][grow]", "[][][]"));
            {
                final JLabel lblSelect = new JLabel("Select");
                panel.add(lblSelect, "cell 0 0,alignx trailing");
            }
            {
                comboMouseSelect = new JComboBox<String>();
                comboMouseSelect.setModel(new DefaultComboBoxModel<String>(new String[] {"Left", "Middle", "Right"}));
                panel.add(comboMouseSelect, "cell 1 0,growx");
            }
            {
                final JLabel lblMove = new JLabel("Move");
                panel.add(lblMove, "cell 0 1,alignx trailing");
            }
            {
                comboMouseMove = new JComboBox<String>();
                comboMouseMove.setModel(new DefaultComboBoxModel<String>(new String[] {"Left", "Middle", "Right"}));
                panel.add(comboMouseMove, "cell 1 1,growx");
            }
            {
                final JLabel lblZoom = new JLabel("Zoom");
                panel.add(lblZoom, "cell 0 2,alignx trailing");
            }
            {
                comboMouseZoom = new JComboBox<String>();
                comboMouseZoom.setModel(new DefaultComboBoxModel<String>(new String[] {"Left", "Middle", "Right"}));
                panel.add(comboMouseZoom, "cell 1 2,growx");
            }
        }
        
        final JPanel panel = new JPanel();
        panel_col2.add(panel, "cell 0 1,growx,aligny top");
        panel.setBorder(new TitledBorder(null, "Other Plot Visual Aspects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setLayout(new MigLayout("", "[][grow]", "[][]"));
        
        final JLabel lblLegendFontSize = new JLabel("Legend font size (relative)");
        panel.add(lblLegendFontSize, "cell 0 0,alignx trailing");
        
        txtLegfontsizerel = new JTextField();
        txtLegfontsizerel.setText("legFontSizeRel");
        panel.add(txtLegfontsizerel, "cell 1 0,growx");
        txtLegfontsizerel.setColumns(10);
        
        final JLabel lblZoomFactorStep = new JLabel("Zoom factor step");
        panel.add(lblZoomFactorStep, "cell 0 1,alignx trailing");
        
        txtZoomfactorstep = new JTextField();
        txtZoomfactorstep.setText("zoomFactorStep");
        panel.add(txtZoomfactorstep, "cell 1 1,growx");
        txtZoomfactorstep.setColumns(10);
        
        final JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Plot area size", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_col2.add(panel_1, "cell 0 2,growx,aligny top");
        panel_1.setLayout(new MigLayout("", "[][grow]", "[][][]"));
        
        chckbxCustomSize = new JCheckBox("Force minimum size");
        chckbxCustomSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_chckbxCustomSize_actionPerformed(e);
            }
        });

        chckbxCustomSize.setToolTipText("Forces a minimum size of the plot area, otherwise it will adapt to window size");
        panel_1.add(chckbxCustomSize, "cell 0 0 2 1");
        
        final JLabel lblWidthpx = new JLabel("width (px)");
        panel_1.add(lblWidthpx, "cell 0 1,alignx trailing");
        
        txtCustomplotwidth = new JTextField();
        txtCustomplotwidth.setText("customplotwidth");
        panel_1.add(txtCustomplotwidth, "cell 1 1,growx");
        txtCustomplotwidth.setColumns(10);
        
        final JLabel lblHeight = new JLabel("height (px)");
        panel_1.add(lblHeight, "cell 0 2,alignx trailing");
        
        txtCustomplotheight = new JTextField();
        txtCustomplotheight.setText("customplotheight");
        panel_1.add(txtCustomplotheight, "cell 1 2,growx");
        txtCustomplotheight.setColumns(10);
        
        panel_axes = new JPanel();
        panel_col1.add(panel_axes, "cell 0 1,growx,aligny top");
        panel_axes.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Axes text", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_axes.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
        
        final JLabel lblFontSizerelative = new JLabel("Label font size (relative)");
        panel_axes.add(lblFontSizerelative, "cell 0 0,alignx trailing");
        
        txtAxlabelfontsizerel = new JTextField();
        txtAxlabelfontsizerel.setText("axLabelFontSizeRel");
        panel_axes.add(txtAxlabelfontsizerel, "cell 1 0,growx");
        txtAxlabelfontsizerel.setColumns(10);
        
        final JLabel lblLabelFontSize = new JLabel("Values font size (relative)");
        panel_axes.add(lblLabelFontSize, "cell 0 1,alignx trailing");
        
        txtAxvaluesfontsizerel = new JTextField();
        txtAxvaluesfontsizerel.setText("axValuesFontSizeRel");
        panel_axes.add(txtAxvaluesfontsizerel, "cell 1 1,growx");
        txtAxvaluesfontsizerel.setColumns(10);
        
        final JLabel lblNumberOfDecimals = new JLabel("Decimal positions in X");
        panel_axes.add(lblNumberOfDecimals, "cell 0 2,alignx trailing");
        
        txtAxxdecimals = new JTextField();
        txtAxxdecimals.setText("axXdecimals");
        panel_axes.add(txtAxxdecimals, "cell 1 2,growx");
        txtAxxdecimals.setColumns(10);
        
        final JLabel lblDecimalPositionsIn = new JLabel("Decimal positions in Y");
        panel_axes.add(lblDecimalPositionsIn, "cell 0 3,alignx trailing");
        
        txtAxydecimals = new JTextField();
        txtAxydecimals.setText("axYdecimals");
        panel_axes.add(txtAxydecimals, "cell 1 3,growx");
        txtAxydecimals.setColumns(10);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Apply");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Close");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
    }
    
    //get the current values from graphPanel
    private void inicia() {
        int[] mbutt = fe.getMouseButtons();
        if (mbutt[0]==MouseEvent.BUTTON1)comboMouseSelect.setSelectedIndex(0);
        if (mbutt[0]==MouseEvent.BUTTON2)comboMouseSelect.setSelectedIndex(1);
        if (mbutt[0]==MouseEvent.BUTTON3)comboMouseSelect.setSelectedIndex(2);
        
        if (mbutt[1]==MouseEvent.BUTTON1)comboMouseMove.setSelectedIndex(0);
        if (mbutt[1]==MouseEvent.BUTTON2)comboMouseMove.setSelectedIndex(1);
        if (mbutt[1]==MouseEvent.BUTTON3)comboMouseMove.setSelectedIndex(2);
        
        if (mbutt[2]==MouseEvent.BUTTON1)comboMouseZoom.setSelectedIndex(0);
        if (mbutt[2]==MouseEvent.BUTTON2)comboMouseZoom.setSelectedIndex(1);
        if (mbutt[2]==MouseEvent.BUTTON3)comboMouseZoom.setSelectedIndex(2);
        
        txtDivXprimIncPx.setText(Integer.toString((int)(graphPanel.getIncXPrimPIXELS())));
        txtDivXsecIncPx.setText(Integer.toString((int)(graphPanel.getIncXSecPIXELS())));
        txtDivYprimIncPx.setText(Integer.toString((int)(graphPanel.getIncYPrimPIXELS())));
        txtDivYsecIncPx.setText(Integer.toString((int)(graphPanel.getIncYSecPIXELS())));
        
        txtDivprimsizepx.setText(Integer.toString(graphPanel.getDiv_PrimPixSize()));
        txtDivsecsizepx.setText(Integer.toString(graphPanel.getDiv_SecPixSize()));

        txtZoomfactorstep.setText(FileUtils.dfX_2.format(graphPanel.getFacZoom()));
        
        txtAxlabelfontsizerel.setText(FileUtils.dfX_2.format(graphPanel.getDef_axisL_fsize()));
        txtAxvaluesfontsizerel.setText(FileUtils.dfX_2.format(graphPanel.getDef_axis_fsize()));
        txtAxxdecimals.setText(Integer.toString(graphPanel.getDef_nDecimalsX()));
        txtAxydecimals.setText(Integer.toString(graphPanel.getDef_nDecimalsY()));
        txtLegfontsizerel.setText(FileUtils.dfX_2.format(graphPanel.getDef_legend_fsize()));
        
        chckbxCustomSize.setSelected(graphPanel.isCustomCanvasSize());
        if (graphPanel.getCustomCanvasHeight()==0||graphPanel.getCustomCanvasWidth()==0) {
            txtCustomplotheight.setText(Integer.toString(graphPanel.getWidth()));
            txtCustomplotwidth.setText(Integer.toString(graphPanel.getHeight()));
        }else {
            txtCustomplotheight.setText(Integer.toString(graphPanel.getCustomCanvasHeight()));
            txtCustomplotwidth.setText(Integer.toString(graphPanel.getCustomCanvasWidth()));
        }
        
    }
    
    
    private void apply() {
        int[] mbut = new int[] {0,0,0};
        switch(comboMouseSelect.getSelectedIndex()) {
        case 0:
            mbut[0] = MouseEvent.BUTTON1;
            break;
        case 1:
            mbut[0] = MouseEvent.BUTTON2;
            break;
        case 2:
            mbut[0] = MouseEvent.BUTTON3;
            break;
        default:
            mbut[0] = MouseEvent.BUTTON1;
            break;
        }
        switch(comboMouseMove.getSelectedIndex()) {
        case 0:
            mbut[1] = MouseEvent.BUTTON1;
            break;
        case 1:
            mbut[1] = MouseEvent.BUTTON2;
            break;
        case 2:
            mbut[1] = MouseEvent.BUTTON3;
            break;
        default:
            mbut[1] = MouseEvent.BUTTON2;
            break;
        }
        switch(comboMouseZoom.getSelectedIndex()) {
        case 0:
            mbut[2] = MouseEvent.BUTTON1;
            break;
        case 1:
            mbut[2] = MouseEvent.BUTTON2;
            break;
        case 2:
            mbut[2] = MouseEvent.BUTTON3;
            break;
        default:
            mbut[2] = MouseEvent.BUTTON3;
            break;
        }
        fe.setMouseButtons(mbut[0], mbut[1], mbut[2]);
        
        try {
            graphPanel.setIncXPrimPIXELS(Double.parseDouble(txtDivXprimIncPx.getText()));
            graphPanel.setIncXSecPIXELS(Double.parseDouble(txtDivXsecIncPx.getText()));
            graphPanel.setIncYPrimPIXELS(Double.parseDouble(txtDivYprimIncPx.getText()));
            graphPanel.setIncYSecPIXELS(Double.parseDouble(txtDivYsecIncPx.getText()));
            graphPanel.setDiv_PrimPixSize(Integer.parseInt(txtDivprimsizepx.getText()));
            graphPanel.setDiv_SecPixSize(Integer.parseInt(txtDivsecsizepx.getText()));
            graphPanel.setFacZoom(Double.parseDouble(txtZoomfactorstep.getText()));
            graphPanel.setDef_axis_fsize(Float.parseFloat(txtAxvaluesfontsizerel.getText()));
            graphPanel.setDef_axisL_fsize(Float.parseFloat(txtAxlabelfontsizerel.getText()));
            graphPanel.setDef_nDecimalsX(Integer.parseInt(txtAxxdecimals.getText()));
            graphPanel.setDef_nDecimalsY(Integer.parseInt(txtAxydecimals.getText()));
            graphPanel.setDef_legend_fsize(Float.parseFloat(txtLegfontsizerel.getText()));

        }catch(Exception ex) {
            fe.getLog().warning("Error reading option values, please check");
        }
        
        graphPanel.setCustomCanvasSize(chckbxCustomSize.isSelected());
        graphPanel.setCustomCanvasHeight(Integer.parseInt(txtCustomplotheight.getText()));
        graphPanel.setCustomCanvasWidth(Integer.parseInt(txtCustomplotwidth.getText()));
        graphPanel.actualitzaPlot();
        //TODO we need to trigger repaint
    }
    
    protected void do_okButton_actionPerformed(ActionEvent e) {
        this.apply();
    }
    
    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.inicia(); //cal actualitzar els valors corresponents
    }

    protected void do_chckbxCustomSize_actionPerformed(ActionEvent e) {
        FileUtils.InfoDialog(this, "Resize main window for it to take effect", "Resize needed");
    }
}

