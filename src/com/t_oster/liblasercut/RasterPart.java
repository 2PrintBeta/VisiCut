/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.liblasercut;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.LinkedList;

/**
 *
 * @author thommy
 */
public class RasterPart {
    
    private EngravingProperty curProp;
    
    private List<BufferedImage> images = new LinkedList<BufferedImage>();
    private List<EngravingProperty> properties = new LinkedList<EngravingProperty>();
    
    public RasterPart(EngravingProperty initialEngravingProperty){
        this.curProp = initialEngravingProperty;
    }
    
    public void setCurrentEngravingProperty(EngravingProperty eng){
        this.curProp = eng;
    }
    
    public EngravingProperty getCurrentEngravingProperty(){
        return this.curProp;
    }
    
    public void setPower(int power){
        this.curProp.setPower(power);
    }
    
    public void setSpeed(int speed){
        this.curProp.setSpeed(speed);
    }
    
    public void addImage(BufferedImage img){
        this.images.add(img);
        this.properties.add(curProp);
    }
    
    public void addImage(BufferedImage img, EngravingProperty prop){
        this.images.add(img);
        this.properties.add(prop);
    }
    
    public BufferedImage[] getImages(){
        return this.images.toArray(new BufferedImage[0]);
    }
    
    public EngravingProperty[] getPropertys(){
        return this.properties.toArray(new EngravingProperty[0]);
    }
}
