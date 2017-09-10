package org.richfaces.demo.datafilterslider;

import java.io.Serializable;

import org.richfaces.event.DataFilterSliderEvent;


/**
 * @author $Autor$
 *
 */
public class DemoSliderBean implements Serializable {
    private static final long serialVersionUID = 1L;
    DemoInventoryList demoInventoryList;


    public void setDemoInventoryList(DemoInventoryList demoInventoryList) {
        this.demoInventoryList = demoInventoryList;
    }

    public void doSlide(DataFilterSliderEvent event) {

           Integer oldSliderVal = event.getOldSliderVal();
           Integer newSliderVal = event.getNewSliderVal();

          // System.out.println("Old Slider Value = " + oldSliderVal.toString() + "  " + "New Slider Value = " + newSliderVal.toString());

    }

}
