package com.vava33.BasicPlotPanel;

/**
 * Extensió/implementació (nua) de Plot1DPanel
 *
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Graphics2D;

import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.jutils.VavaLogger;


public class BasicPlot1DPanel extends Plot1DPanel<BasicDataToPlot<?>> {

    private static final long serialVersionUID = 1L;
    
    public BasicPlot1DPanel(BasicDataToPlot<?> data, VavaLogger log) {
        super(data,log);
    }

    @Override
    protected void customPaintAfterData(Graphics2D g1) {
        //DO Nothing in this case
    }

    @Override
    protected void customPaintBeforeData(Graphics2D g1) {
      //DO Nothing in this case
    }

}
