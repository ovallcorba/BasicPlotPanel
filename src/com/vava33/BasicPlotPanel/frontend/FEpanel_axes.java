package com.vava33.BasicPlotPanel.frontend;

/**
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.util.FastMath;

import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.Options;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class FEpanel_axes extends JPanel {

    private static final long serialVersionUID = 1L;
    Plot1DFrontend fe;
    public Plot1DPanel<?> graphPanel;

    private JLabel lblInix;
    private JTextField txtXmin;
    private JLabel lblIniy;
    private JTextField txtYmin;
    private JLabel lblIncx;
    private JTextField txtXdiv;
    private JTextField txtYdiv;
    private JLabel lblSubx;
    private JLabel lblIncy;
    private JTextField txtNdivx;
    private JLabel lblSuby;
    private JTextField txtNdivy;
    private JCheckBox chckbxFixedAxis;
    private JCheckBox chckbxNegativeYLabels;
    private JCheckBox chckbxVerticalYAxis;
    private JCheckBox chckbxVerticalYLabel;
    

    public FEpanel_axes() {
        initGUI();
    }
    
    public void setFrontend(Plot1DFrontend fe) {
        this.fe=fe;
        graphPanel = fe.getGraphPanel();
    }
    
    private void initGUI() {

        this.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Axes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new MigLayout("insets 2", "[][grow][][grow]", "[][][][][]"));
        
        lblInix = new JLabel("iniX");
        lblInix.setToolTipText("initial value X axis");
        this.add(lblInix, "cell 0 0,alignx trailing");
        
        txtXmin = new JTextField();
        this.add(txtXmin, "cell 1 0,growx");
        txtXmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXmin_actionPerformed(e);
            }
        });
        txtXmin.setText("Xmin");
        txtXmin.setColumns(3);
        
        lblIniy = new JLabel("iniY");
        lblIniy.setToolTipText("initial value Y axis");
        this.add(lblIniy, "cell 2 0,alignx trailing");
        
        txtYmin = new JTextField();
        this.add(txtYmin, "cell 3 0,growx");
        txtYmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYmin_actionPerformed(e);
            }
        });
        txtYmin.setText("ymin");
        txtYmin.setColumns(3);
        
        lblIncx = new JLabel("incX");
        lblIncx.setToolTipText("increment X major ticks");
        this.add(lblIncx, "cell 0 1,alignx trailing");
        
        txtXdiv = new JTextField();
        txtXdiv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXdiv_actionPerformed(e);
            }
        });
        this.add(txtXdiv, "cell 1 1,growx");
        txtXdiv.setText("xdiv");
        txtXdiv.setColumns(3);
        
        lblIncy = new JLabel("incY");
        lblIncy.setToolTipText("increment Y major ticks");
        this.add(lblIncy, "cell 2 1,alignx trailing");
        
        txtYdiv = new JTextField();
        txtYdiv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYdiv_actionPerformed(e);
            }
        });
        this.add(txtYdiv, "cell 3 1,growx");
        txtYdiv.setText("ydiv");
        txtYdiv.setColumns(3);
        
        lblSubx = new JLabel("divX");
        lblSubx.setToolTipText("subdivisions in X axis");
        this.add(lblSubx, "cell 0 2,alignx trailing");
        
        txtNdivx = new JTextField();
        txtNdivx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtNdivx_actionPerformed(e);
            }
        });
        this.add(txtNdivx, "cell 1 2,growx");
        txtNdivx.setText("NdivX");
        txtNdivx.setColumns(3);
        
        lblSuby = new JLabel("divY");
        lblSuby.setToolTipText("subdivisions in Y axis");
        this.add(lblSuby, "cell 2 2,alignx trailing");
        
        txtNdivy = new JTextField();
        txtNdivy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtNdivy_actionPerformed(e);
            }
        });
        this.add(txtNdivy, "cell 3 2,growx");
        txtNdivy.setText("NdivY");
        txtNdivy.setColumns(3);
        
        chckbxFixedAxis = new JCheckBox("Fix X values");
        this.add(chckbxFixedAxis, "cell 0 3 2 1,alignx left");
        chckbxFixedAxis.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxFixedAxis_itemStateChanged(e);
            }
        });
        
        chckbxNegativeYLabels = new JCheckBox("Neg. Y values");
        chckbxNegativeYLabels.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxNegativeYLabels_itemStateChanged(e);
            }
        });
        
        chckbxVerticalYAxis = new JCheckBox("Show Y axis");
        chckbxVerticalYAxis.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxVerticalYAxis_itemStateChanged(e);
            }
        });
        this.add(chckbxVerticalYAxis, "cell 2 3 2 1,alignx left");
        
        chckbxVerticalYLabel = new JCheckBox("Vert. Y label");
        chckbxVerticalYLabel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxVerticalYLabel_itemStateChanged(e);
            }
        });
        this.add(chckbxVerticalYLabel, "cell 0 4 2 1,alignx left");
        this.add(chckbxNegativeYLabels, "cell 2 4 2 1");
    }
    
    public void inicia() {
        this.chckbxFixedAxis.setSelected(graphPanel.isFixAxes());
        this.chckbxNegativeYLabels.setSelected(graphPanel.isNegativeYAxisLabels());
        this.chckbxVerticalYAxis.setSelected(graphPanel.isVerticalYAxe());
        this.chckbxVerticalYLabel.setSelected(graphPanel.isVerticalYlabel());
        
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

    public void fillDivisionsValues(){
        double[] divValues = graphPanel.getDivValues();
        this.txtXmin.setText(FileUtils.dfX_3.format(divValues[0]));
        this.txtXdiv.setText(FileUtils.dfX_3.format(divValues[1]));
        this.txtNdivx.setText(FileUtils.dfX_3.format(divValues[2]));
        this.txtYmin.setText(FileUtils.dfX_3.format(divValues[3]));
        this.txtYdiv.setText(FileUtils.dfX_3.format(divValues[4]));
        this.txtNdivy.setText(FileUtils.dfX_3.format(divValues[5]));
    }
    
    public void applyDivisions(){
        double iniX=Double.parseDouble(txtXmin.getText());
        double iniY=Double.parseDouble(txtYmin.getText());
        double incX=Double.parseDouble(txtXdiv.getText());
        double incY=Double.parseDouble(txtYdiv.getText());
        double ndivX=Double.parseDouble(txtNdivx.getText());
        double ndivY=Double.parseDouble(txtNdivy.getText());
        graphPanel.applyDivisions(iniX, FastMath.abs(incX), FastMath.abs(ndivX), iniY, FastMath.abs(incY), FastMath.abs(ndivY)); //TODO he fet fastmath perque petava
        
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

    private void do_chckbxNegativeYLabels_itemStateChanged(ItemEvent e) {
        graphPanel.setNegativeYAxisLabels(chckbxNegativeYLabels.isSelected());
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
    
    public void readOptions(Options opt) {
        this.chckbxFixedAxis.setSelected(opt.getValAsBoolean("fixAxes", this.chckbxFixedAxis.isSelected()));
        this.chckbxNegativeYLabels.setSelected(opt.getValAsBoolean("yVertNeg", this.chckbxNegativeYLabels.isSelected()));
        this.chckbxVerticalYAxis.setSelected(opt.getValAsBoolean("yVert", this.chckbxVerticalYAxis.isSelected()));
        this.chckbxVerticalYLabel.setSelected(opt.getValAsBoolean("yVertLabel", this.chckbxVerticalYLabel.isSelected()));
        
        double[] divs = graphPanel.getDivValues();
        double iniX=opt.getValAsDouble("startValX",divs[0]);
        double incX=opt.getValAsDouble("incXprim",divs[1]);
        double ndivX=opt.getValAsDouble("ndivX",divs[2]);
        double iniY=opt.getValAsDouble("startValY",divs[3]);
        double incY=opt.getValAsDouble("incYprim",divs[4]);
        double ndivY=opt.getValAsDouble("ndivY",divs[5]);
        graphPanel.applyDivisions(iniX, incX, ndivX, iniY, incY, ndivY);
    }
}