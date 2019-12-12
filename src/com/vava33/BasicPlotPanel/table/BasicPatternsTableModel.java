package com.vava33.BasicPlotPanel.table;

/**
 * Basic Table Model
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;

import javax.swing.table.DefaultTableModel;

/*
 * Per afegir una columna
 *  1) afegim a column
 *  2) afegim la info al switch (amb el type inclos)
 *  3) afegim a BasicPlotPanel la info a createRow
 *  5) afegir a BasicPlotPanel a applicarModificacioTaula, editMultipleValues, etc...
 *  6) si cal editar com es mostra a la taula (p.ex. combobox)
 *  7) BatchEditDialog afegir el que falti
 */

public class BasicPatternsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -4962185046634550281L;
    public enum columns {
        Name, Color, Show, Line, Marker, Yoffset, Type
    }
    
    @SuppressWarnings("rawtypes")
    Class[] columnTypes;
    boolean[] columnEditables;
    public int[] columnMinSize;
    public int[] columnMaxSize;
    public int[] columnPrefSize;
    
    public BasicPatternsTableModel(){
        this(columns.values());
    }
      
    
    public BasicPatternsTableModel(columns...visibleCols){
        super();
        if ((visibleCols==null)||(visibleCols.length==0)) {
            visibleCols = new columns[columns.values().length];
        }
        String[] cols = new String[visibleCols.length];
        columnTypes = new Class[visibleCols.length];
        columnEditables = new boolean[visibleCols.length];
        columnMinSize = new int[visibleCols.length];
        columnMaxSize = new int[visibleCols.length];
        columnPrefSize = new int[visibleCols.length];

        for (int i=0; i<visibleCols.length; i++){
            cols[i]=visibleCols[i].toString();
            switch (visibleCols[i]){
                case Name:
                    columnTypes[i]=String.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=60;
                    columnMaxSize[i]=1200;
                    columnPrefSize[i]=80;
                    break;
                case Color:
                    columnTypes[i]=Color.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=30;
                    columnMaxSize[i]=80;
                    columnPrefSize[i]=40;
                    break;
                case Marker:
                    columnTypes[i]=Float.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=45;
                    columnMaxSize[i]=110;
                    columnPrefSize[i]=55;
                    break;
                case Line:
                    columnTypes[i]=Float.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=40;
                    columnMaxSize[i]=110;
                    columnPrefSize[i]=45;
                    break;
                case Show:
                    columnTypes[i]=Boolean.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=25;
                    columnMaxSize[i]=80;
                    columnPrefSize[i]=45;
                    break;
                case Yoffset:
                    columnTypes[i]=Double.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=50;
                    columnMaxSize[i]=110;
                    columnPrefSize[i]=60;
                    break;
                case Type:
                    columnTypes[i]=String.class;
                    columnEditables[i]=true;
                    columnMinSize[i]=25;
                    columnMaxSize[i]=80;
                    columnPrefSize[i]=40;
                    break;
                default:
                    break;
            }
        }
        
        this.setDataVector(new Object[][] {}, cols);
        
    }
    
      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public Class getColumnClass(int columnIndex) {
          return columnTypes[columnIndex];
      }
      
      @Override
      public boolean isCellEditable(int row, int column) {
          return columnEditables[column];
      }
      
      public int getColumnMinSize(int columnIndex) {
          return columnMinSize[columnIndex];
      }
      public int getColumnMaxSize(int columnIndex) {
          return columnMaxSize[columnIndex];
      }
      public int getColumnPrefSize(int columnIndex) {
          return columnPrefSize[columnIndex];
      }
      
}





