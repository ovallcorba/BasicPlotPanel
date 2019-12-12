package com.vava33.BasicPlotPanel;

/**
 * Basic implementation of the DataToPlot interface
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.vava33.BasicPlotPanel.core.DataToPlot;
import com.vava33.BasicPlotPanel.core.Plot1DGlobal;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.BasicPlotPanel.core.Plottable;
import com.vava33.BasicPlotPanel.core.SerieType;
import com.vava33.BasicPlotPanel.table.BasicPatternsTableCellRenderer;
import com.vava33.BasicPlotPanel.table.BasicPatternsTableModel;
import com.vava33.BasicPlotPanel.table.BatchEditDialog;
import com.vava33.BasicPlotPanel.table.ColorEditor;
import com.vava33.BasicPlotPanel.table.ColorRenderer;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;


public class BasicDataToPlot<T extends BasicSerie<BasicPoint>> implements DataToPlot<T>{

    protected List<T> series;
    protected List<T> selectedSeries;
    protected int nColoredSeries;
    protected JTable pltTable;
    protected Plot1DPanel<?> plotpanel;
    private static final String className = "BasicDataToPlot";
    private static VavaLogger log = Plot1DGlobal.getVavaLogger(className);
    public static void setVavaLogger(VavaLogger l) {log = l;}
    public static VavaLogger getLog() {return log;}
    
    public BasicDataToPlot() {
        series = new ArrayList<T>();
        selectedSeries = new ArrayList<T>();
        initTablePatterns(new BasicPatternsTableModel());
        nColoredSeries=0;
    }
    
    public void initTablePatterns(TableModel dm) {
        pltTable = new JTable(dm);

        pltTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pltTable.setColumnSelectionAllowed(true);
        pltTable.setCellSelectionEnabled(true);

        pltTable.setDefaultRenderer(String.class, new BasicPatternsTableCellRenderer());
        pltTable.setDefaultRenderer(Integer.class, new BasicPatternsTableCellRenderer());
        pltTable.setDefaultRenderer(Float.class, new BasicPatternsTableCellRenderer());
        pltTable.setDefaultRenderer(Double.class, new BasicPatternsTableCellRenderer());

        pltTable.getModel().addTableModelListener(new TableModelListener(){
            public void tableChanged(TableModelEvent e) {
                switch (e.getType()) {
                case TableModelEvent.UPDATE:
                    applicarModificacioTaula(e.getColumn(),e.getFirstRow(),e.getLastRow());
                    break;
                default:
                    //do nothing
                    break;
                }
            }
        });
        
        //Set up renderer and editor for the Favorite Color column.
        pltTable.setDefaultRenderer(Color.class,
                new ColorRenderer(true));
        pltTable.setDefaultEditor(Color.class,
                new ColorEditor());

        pltTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = pltTable.getTableHeader().columnAtPoint(mouseEvent.getPoint());
                if (index >= 0) {
                    if (pltTable.getRowCount()>0){
                        pltTable.setRowSelectionInterval(0, pltTable.getRowCount()-1);
                        pltTable.setColumnSelectionInterval(index, index);
                    }
                }
            };
        });
        
        pltTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                aplicarselecciotaula(arg0);
            }
        });

        try {
            JComboBox<String> comboStypeTable = new JComboBox<String>();
            for (SerieType s :SerieType.values()){
                comboStypeTable.addItem(s.name());
            }
            pltTable.getColumn(BasicPatternsTableModel.columns.Type.toString()).setCellEditor(new DefaultCellEditor(comboStypeTable));           
        }catch(IllegalArgumentException ex) {
            log.debug("Type column is not shown");
        }

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editValues = new JMenuItem("Edit Selected Values");
        editValues.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editMultipleValuesTable(e);
            }
            
        });
        popupMenu.add(editValues);
        JMenuItem editSeries = new JMenuItem("Edit Selected Series");
        editSeries.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editMultipleSeriesTable(e);
            }
            
        });
        popupMenu.add(editSeries);
        JMenuItem moveup = new JMenuItem("Move up");
        moveup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveUpSerieTable(e);
            }
        });
        popupMenu.add(moveup);
        JMenuItem movedown = new JMenuItem("Move down");
        movedown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDownSerieTable(e);
            }
        });
        popupMenu.add(movedown);
        JMenuItem remove = new JMenuItem("Remove");
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contextRemove(e);
            }
        });
        popupMenu.add(remove);
        pltTable.setComponentPopupMenu(popupMenu);

        //columnes mides

        BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
        for (int i=0; i<pltTable.getColumnCount(); i++){
            pltTable.getColumnModel().getColumn(i).setPreferredWidth(model.getColumnPrefSize(i));
            pltTable.getColumnModel().getColumn(i).setMaxWidth(model.getColumnMaxSize(i));
            pltTable.getColumnModel().getColumn(i).setMinWidth(model.getColumnMinSize(i));
        }
    }
    
    public T getSerie(int i) {
        return series.get(i);
    }
    
    public void addSerie(T ser) {
        this.addSerie(ser, -1,true, true);
    }
    
    public void addSerie(T ser, boolean updatePlot, boolean paintIt) {
        this.addSerie(ser, -1,updatePlot, paintIt);
    }
    
    public void addSerie(T ser, int position, boolean updatePlot, boolean paintIt) {
        if (paintIt)this.paintIt(nColoredSeries,ser);
        if (position<0) {
            this.series.add(ser);
            this.addToTable(ser);
        }else {
            this.series.add(position,ser);
            this.addToTable(ser,position);
        }
        if (this.series.size()==1)plotpanel.fitGraph();
        if (BasicSerie.isPaintedType(ser.getSerieType()))nColoredSeries++;
        if (updatePlot)plotpanel.actualitzaPlot();
    }
    
    public void removeSerie(T ser, boolean updatePlot) {
        this.series.remove(ser);
        this.removeFromTable(ser);
        if (BasicSerie.isPaintedType(ser.getSerieType()))nColoredSeries--;
        if (updatePlot)plotpanel.actualitzaPlot();
    }
    
    public void removeSeries(List<T> p) {
        for (T p1:p) {
          if (BasicSerie.isPaintedType(p1.getSerieType()))nColoredSeries--;
        }
        this.series.removeAll(p);
        this.updateFullTable();
        plotpanel.actualitzaPlot();
    }
    
    public void removeSerie(int index) {
        this.removeSerie(series.get(index), true);
    }
    
    public void removeAllSeries() {
        this.series.clear();
        this.nColoredSeries=0;
        this.updateFullTable();
        plotpanel.actualitzaPlot();
    }
    
    public JTable getTablePatterns() {
        return pltTable;
    }
    
    private Object[] createRow(T p) {
        return new Object[] {p.getName().trim(),p.getColor(),p.isPlotThis(),p.getLineWidth(),p.getMarkerSize(), p.getYOffset(), p.getSerieType().name()};
    }
    private void addToTable(T p) {
        BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
        model.addRow(createRow(p));
    }
    private void addToTable(T p, int row) {
        BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
        model.insertRow(row,createRow(p));
    }

    private void removeFromTable(T p) {
        BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
        int i = this.getPlottables().indexOf(p);
        if (i>=0) {
            model.removeRow(i);                
        }
    }
    
    @SuppressWarnings("unused")
    private void updateRowFromData(int i) {
        if (i>=0) {
            BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
            model.removeRow(i);
            model.insertRow(i,this.createRow(series.get(i)));
        }
    }
    
    private void applicarModificacioTaula(int columna, int filaIni, int filaFin){
        if (!areSelectedPlottables())return;
        if (columna<0)return;

        for (int i=filaIni; i<=filaFin;i++){
           
            Plottable<BasicPoint> selPatt = this.getPlottables().get(i);
            BasicPatternsTableModel.columns colName = FileUtils.searchEnum(BasicPatternsTableModel.columns.class,pltTable.getColumnName(columna));
            
            switch(colName){ 
            case Color:
                selPatt.setColor((Color) pltTable.getValueAt(i, columna));
                break;
            case Marker:
                selPatt.setMarkerSize((Float) pltTable.getValueAt(i, columna));
                break;
            case Line:
                selPatt.setLineWidth((Float) pltTable.getValueAt(i, columna));
                break;
            case Show:
                selPatt.setPlotThis((Boolean) pltTable.getValueAt(i, columna));
                break;
            case Name:
                selPatt.setName((String) pltTable.getValueAt(i, columna));
                break;
            case Yoffset:
                selPatt.setYOffset((Double) pltTable.getValueAt(i, columna));
                break;
            case Type:
                //dialog preguntant si estem segurs?
                SerieType stype = SerieType.getEnum((String) pltTable.getValueAt(i, columna));
                if (stype==null)break;
                SerieType currStype = selPatt.getSerieType();
                if (stype!=currStype) { //aixi si responem no un cop no ens ho torna a repetir infiniatment
                    final boolean doit = FileUtils.YesNoDialog(pltTable, "Are you sure you want to change serie Type?");
                    if (doit) {
                        selPatt.setSerieType(stype);
                        break;
                    }else {
                        //we put back the old value
                        pltTable.setValueAt(selPatt.getSerieType().name(), i, columna);
                    }
                }
                break;
            default:
                break;
            }
        }
        plotpanel.actualitzaPlot();
    }
    
    private void moveDownSerieTable(ActionEvent e) {
        if (!areSelectedPlottables())return;
        
        //perque funcioni be hem d'iterar al reves
        for (int i=selectedSeries.size()-1;i>=0;i--) {
            int indx = this.getPlottables().indexOf(selectedSeries.get(i));
            if ((indx<0)||(indx>=this.getPlottables().size()))continue;
            Collections.swap(this.getPlottables(), indx, indx+1);
        }

        this.updateFullTable();//TODO idealment nomes moure una fila amb moveRow pero feia el tonto...
        plotpanel.actualitzaPlot();        
    }

    private void moveUpSerieTable(ActionEvent e) {
        if (!areSelectedPlottables())return;
        
        for (Plottable<BasicPoint> p:selectedSeries) {
            int indx = this.getPlottables().indexOf(p);
            if (indx<=0)continue;
            Collections.swap(this.getPlottables(), indx, indx-1);
//            this.moveRow(indx, indx-1);
        }
        
        this.updateFullTable();//TODO idealment nomes moure una fila amb moveRow pero feia el tonto...
        plotpanel.actualitzaPlot();
        
    }

    private void editMultipleSeriesTable(ActionEvent e) {
        if (selectedSeries.size()>0) {
            BatchEditDialog<T> be = new BatchEditDialog<T>(selectedSeries);
            be.setModal(true);
            be.setVisible(true);         
            
            //when closed we update table -- millor si actualitzo selectedPlottables i despres la full table
            this.updateFullTable();
            plotpanel.actualitzaPlot();
            
        }else {
            System.out.println("Select the series(s) that you want to edit");
        }

    }

    private void aplicarselecciotaula(ListSelectionEvent arg0) {
        if (pltTable.getSelectedRow()<0)return;
        if (pltTable.getRowCount()<=0)return;
        //prova amb selected rows:
        int[] selRows = pltTable.getSelectedRows();
        if (selRows.length==0)return;
        this.getSelectedPlottables().clear();
        for (int i=0; i<selRows.length;i++){
            int selRow = selRows[i];
            selectedSeries.add(this.series.get(selRow));    
        }        
    }

    private void contextRemove(ActionEvent e) {
        if (!areSelectedPlottables())return;
        this.removeSeries(selectedSeries);
        plotpanel.actualitzaPlot();
    }

    private void editMultipleValuesTable(ActionEvent e) {
        if (pltTable.getSelectedRow()<0)return;
        if (pltTable.getRowCount()<=0)return;

        int[] selRows = pltTable.getSelectedRows();
        int selCol = pltTable.getSelectedColumn();
        BasicPatternsTableModel.columns colName = FileUtils.searchEnum(BasicPatternsTableModel.columns.class,pltTable.getColumnName(selCol));

        Color newColor = null;
        boolean ask = true;
        String s = null;
        String title = "Change values column-wise";
        String labl = "New value=";
        switch (colName){
        case Color:
            newColor = JColorChooser.showDialog(
                    pltTable,
                    "Choose Color",
                    Color.BLACK);
            s = "changing color column";
            if(newColor == null){
                System.out.println("Select a valid color");
                return;
            }
            ask=false;
            break;
        case Line:
            title = "Change line width";
            labl = "New value (line width)=";
            break;
        case Marker:
            title = "Change marker size";
            labl = "New value (marker size)=";
            break;
        case Name:
            title = "Change Name";
            labl = "New value (name)=";
            break;
        case Show:
            title = "Change show data";
            labl = "New value (true/false)=";
            break;
        case Yoffset:
            title = "Change Y offset";
            labl = "New value (Yoffset)=";
            break;
        case Type:
            title = "Change data serie type";
            labl = String.format("New value (%s)", SerieType.getStringAllNames("/"));
            break;
        default:
            break;

        }
        if (ask) {
            s = (String)JOptionPane.showInputDialog(
                    pltTable,
                    labl,
                    title,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
        }

        //If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            //edit all the selected cells
            //prova amb selected rows:
            for (int i=0; i<=selRows.length-1;i++){
                int selRow = selRows[i];
                switch (colName){
                case Color:
                    pltTable.setValueAt(newColor, selRow, selCol);
                    break;
                case Line:
                    pltTable.setValueAt(Float.parseFloat(s), selRow, selCol);
                    break;
                case Marker:
                    pltTable.setValueAt(Float.parseFloat(s), selRow, selCol);
                    break;
                case Show:
                    pltTable.setValueAt(Boolean.parseBoolean(s), selRow, selCol);
                    break;
                case Yoffset:
                    pltTable.setValueAt(Double.parseDouble(s), selRow, selCol);
                    break;
                case Type:
                    pltTable.setValueAt(SerieType.getEnum(s), selRow, selCol);
                    break;
                default:
                    System.out.println("Column not identified");
                    break;
                }
            }
            return;
        }
        
    }
    
///IMPLEMENTATIONS
    
    @Override
    public List<T> getPlottables() {
        return series;
    }

    @Override
    public List<T> getSelectedPlottables() {
        return selectedSeries;
    }

    @Override
    public boolean arePlottables() {
        if(series.size()>0)return true;
        return false;
    }

    @Override
    public boolean areSelectedPlottables() {
        if(selectedSeries.size()>0)return true;
        return false;
    }

    @Override
    public void updateFullTable() { //TODO aquest no se si cal...
        BasicPatternsTableModel model = (BasicPatternsTableModel) pltTable.getModel();
        model.setRowCount(0);
        for (T p:series) {
            this.addToTable(p);
        }

    }

    @Override
    public <T1 extends Plot1DPanel<?>> void setPlotPanel(T1 ppanel) {
        this.plotpanel=ppanel;
        
    }
    
    public void reAssignColorToAllDataSeries() { //null for all
        reAssignColorPlottables(false,SerieType.values());
    }

    public void reAssignColorToAllPaintedTypes() {
        reAssignColorPlottables(false,BasicSerie.getPaintedTypes().toArray(new SerieType[0]));

    }

    @Override
    public void reAssignColorPlottables(boolean onlyPlotted, SerieType... st) {
        int nserie = 0;
        for (T ds: series) {
            //nomes visibles
            if ((onlyPlotted)&&(!ds.isPlotThis()))continue;
            for (SerieType st1 :st) {
                if (ds.getSerieType()==st1) {
                    this.paintIt(nserie,ds);
                    nserie++;
                    continue;
                }
            }
        }
        this.updateFullTable();//IDEALMENT NOMES ACTUALITZAR ELS COLORS DE LA TAULA... COM? TODO
        plotpanel.actualitzaPlot();
        
    }
    
    private void paintIt(int nserie, T ds) {
        if (plotpanel.isLightTheme()){
            int ncol = (nserie)%Plot1DGlobal.lightColors.length;
            ds.setColor(FileUtils.parseColorName(Plot1DGlobal.lightColors[ncol]));    
        }else {
            int ncol = (nserie)%Plot1DGlobal.DarkColors.length;
            ds.setColor(FileUtils.parseColorName(Plot1DGlobal.DarkColors[ncol]));
        } 
    }

    @Override
    public int getNPlottables() {
        return series.size();
    }

    @Override
    public int getNSelectedPlottables() {
        return selectedSeries.size();
    }
    
    @Override
    public int getNplottablesOfSerieTypes(boolean onlyVisibles, SerieType...sts) {
        int nserie = 0;
        for (Plottable<BasicPoint> ds: series) {
            //nomes visibles?
            if ((onlyVisibles)&&(!ds.isPlotThis()))continue;
            for (SerieType st1 :sts) {
                if (ds.getSerieType()==st1) {
                    nserie++;
                    continue;
                }
            }
        }
        return nserie;
    }