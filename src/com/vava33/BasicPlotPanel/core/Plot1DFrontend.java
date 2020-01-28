package com.vava33.BasicPlotPanel.core;

import com.vava33.jutils.Options;
import com.vava33.jutils.VavaLogger;

/*
 * Aquesta interface l'hauria d'implementar el UI que faci de pont entre Plot1DPanel i DataToPlot.
 * Ha de servir per la interacció amb l'usuari, mostrar/introduir valors, etc...
 * També és l'encarregat de salvar un estat concret del programa (estils visualització Plot1DPanel i opcionalment Dades)
 *
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

public interface Plot1DFrontend {

    public void updateFields(); //fields or whatever from the info in Plot1DPanel
    public void fillDivisionsValues();
    public void fillWindowValues();
    public void updateOtherValues();
    public Plot1DPanel<?> getGraphPanel();
    public Options getVisualParametersToSave();
    public void setVisualParametersFromSaved(Options opt);
    public VavaLogger getLog();
    public void setMouseButtons(int clicar, int moure, int zoom);
    public int[] getMouseButtons(); //returns clicar, moure, zoom
}
