/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class HitRatio extends Ratio {
    
    int hitNumber = -1;

    public int getHitNumber() {
        return hitNumber;
    }

    public void setHitNumber(int hitNumber) {
        this.hitNumber = hitNumber;
    }
}
