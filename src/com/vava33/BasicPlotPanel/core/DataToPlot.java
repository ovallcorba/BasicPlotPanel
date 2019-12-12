package com.vava33.BasicPlotPanel.core;

/**
 *
 * Descriu la gestio de les series. La implementació pot tenir una taula 
 * o altre element que ho guardi i el plotPanel o qui sigui pot demanar d'actualitzar-ho.
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.util.List;

public interface DataToPlot<T extends Plottable<? extends Plottable_point>> {

    public List<T> getPlottables(); //return all the plottables to consider (usually all, they include plotit)
    public List<T> getSelectedPlottables();
    public <T1 extends Plot1DPanel<?>> void setPlotPanel(T1 ppanel); // to trigger actualitza plot
    public boolean arePlottables();
    public boolean areSelectedPlottables();
    public int getNPlottables();
    public int getNSelectedPlottables();
    public int getNplottablesOfSerieTypes(boolean onlyVisibles, SerieType...sts);
    public void reAssignColorPlottables(boolean onlyPlotted, SerieType... st);
    public void updateFullTable(); // update data, can be table or whatever
}
