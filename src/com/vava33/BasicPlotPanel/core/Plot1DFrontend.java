package com.vava33.BasicPlotPanel.core;

/**
 *
 * Aquesta interface l'hauria d'implementar el UI que faci de pont entre Plot1DPanel i DataToPlot.
 * Ha de servir per la interacció amb l'usuari, mostrar/introduir valors, etc...
 * També és l'encarregat de salvar un estat concret del programa (estils visualització Plot1DPanel i opcionalment Dades)
 *
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import com.vava33.jutils.Options;


public interface Plot1DFrontend {

    public void updateFields(); //fields or whatever from the info in Plot1DPanel
    public void fillDivisionsValues();
    public void fillWindowValues();
    public void updateOtherValues();
    public Plot1DPanel<?> getGraphPanel();
    public Options getVisualParametersToSave();
    public void setVisualParametersFromSaved(Options opt);
}
