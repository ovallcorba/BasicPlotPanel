package com.vava33.BasicPlotPanel;

/**
 * Basic Plotting project to be extended (it uses generic classes, e.g. dataToPlot)
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import javax.swing.JFrame;
import com.vava33.ovPlot.BasicPlotPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import com.vava33.jutils.Options;
import com.vava33.jutils.VavaLogger;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JScrollPane;


public class BasicPlotMain {

    private static String LandF = "system";
    public static final String userDir = System.getProperty("user.dir");
    public static final int build_date = 191121;
    public static final String welcomeMSG = "Basic OVplot v"+build_date+" by OV\n";
    public static final String lineSeparator = System.getProperty("line.separator");
    public static String loglevel = "info";
    public static boolean logConsole = false;
    public static boolean logTA = true;
    private static int def_Width=1024;
    private static int def_Height=768;
    static VavaLogger log = BasicPlotMain.getVavaLogger("BasicPlotMain");
    
    BasicPlotPanel plotpanel;
    XYData dataToPlot;
    JFrame mainf;
    private JPanel panel;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmOpen;
    private JSplitPane splitPane_1;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JTable table_patterns;
    private LogJTextArea tAOut;
    
    /**
     * Launch the application.
     */
    public static void main(final String[] args) {

        //first thing to do is read PAR files if exist
        FileUtils.getOS();
        FileUtils.setLocale(null);

        //LOGGER
        System.out.println(log.logStatus());

        try {
            if (FileUtils.containsIgnoreCase(LandF, "system")){
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            if (FileUtils.containsIgnoreCase(LandF, "gtk")){
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }
            if (FileUtils.containsIgnoreCase(LandF, "metal")){
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Throwable e) {
            log.warning("Error initializing System look and feel");
        }


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicPlotMain frame = new BasicPlotMain();
                    frame.showMainFrame();
                } catch (Exception e) {
                    log.severe("Error initializing main window");
                }
            }
        });
    }

//LOGGER FOR THE APPLICATION
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name,logConsole,false,logTA);
        l.setLogLevel(loglevel);
        if (isAnyLogging()) {
            l.enableLogger(true);
        }else {
            l.enableLogger(false);
        }
        return l;
    }
    public static boolean isAnyLogging() {
        if (logConsole || logTA) return true;
        return false;
    }

//CONSTRUCTOR
    public BasicPlotMain() {
        dataToPlot = new XYData();
        plotpanel = new BasicPlotPanel(new Options(), dataToPlot);
        this.initGUI();
        
        if (logTA)VavaLogger.setTArea(tAOut);
        BasicPlotPanel.setVavaLogger(BasicPlotMain.getVavaLogger("BasicPlotMain_PlotPanel"));
        log.info(welcomeMSG);
    }
    
    private void initGUI() {
        mainf = new JFrame();
        mainf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_mainf_windowClosing(e);
            }
        });
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.7);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainf.getContentPane().add(splitPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(plotpanel);
        
        panel = new JPanel();
        splitPane.setRightComponent(panel);
        panel.setLayout(new MigLayout("", "[grow]", "[grow]"));
        
        splitPane_1 = new JSplitPane();
        splitPane_1.setResizeWeight(0.7);
        panel.add(splitPane_1, "cell 0 0,grow");
        
        scrollPane = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane);
        
        table_patterns = dataToPlot.getTablePatterns();
        scrollPane.setViewportView(table_patterns);
        
        scrollPane_1 = new JScrollPane();
        splitPane_1.setRightComponent(scrollPane_1);
        
        tAOut = new LogJTextArea();
        scrollPane_1.setViewportView(tAOut);
        
        menuBar = new JMenuBar();
        mainf.setJMenuBar(menuBar);
        
        mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        mntmOpen = new JMenuItem("Open...");
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmOpen_actionPerformed(e);
            }
        });
        mnFile.add(mntmOpen);
        
        //WINDOW SIZE
        int posXY=50;
        mainf.setBounds(posXY,posXY,def_Width,def_Height); // TODO centrar-ho?
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //HO FEM CABRE
        while(mainf.getWidth()>(screenSize.width-posXY)){
            mainf.setSize(mainf.getWidth()-150, mainf.getHeight());
        }
        while(mainf.getHeight()>(screenSize.height-posXY)){
            mainf.setSize(mainf.getWidth(), mainf.getHeight()-150);
        }
    }

    public void showMainFrame(){
        mainf.setVisible(true);
        this.plotpanel.showHideButtonsPanel();//amagara el menu lateral
    }
    
    private void do_mainf_windowClosing(WindowEvent e) {
        this.disposeMainFrame();
    }
    
    public void disposeMainFrame(){
        mainf.dispose();
    }

    private void do_mntmOpen_actionPerformed(ActionEvent e) {
        File f = FileUtils.fchooserOpen(this.mainf, new File(userDir), null, 0);
        BasicSerie bds = DataFileUtils.readDAT(f);
        dataToPlot.addSerie(bds);
        
    }

}
