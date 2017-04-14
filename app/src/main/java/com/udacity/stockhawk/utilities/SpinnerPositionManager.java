package com.udacity.stockhawk.utilities;

import android.widget.Spinner;

/**
 * Created by spoooon on 4/14/17.
 */

public class SpinnerPositionManager {

   private static Integer sOriginalSpinnerPosition;
    private static Integer sNewSpinnerPosition;

    public static int getOriginalSpinnerPosition() {
        return sOriginalSpinnerPosition;
    }

    public static void setOriginalSpinnerPosition(int originalSpinnerPosition) {
        sOriginalSpinnerPosition = originalSpinnerPosition;
    }

    public static int getNewSpinnerPosition() {
        return sNewSpinnerPosition;
    }

    public static void setNewSpinnerPosition(int newSpinnerPosition) {
        if (sOriginalSpinnerPosition == null) {
            setOriginalSpinnerPosition(newSpinnerPosition);
            return;
        }
        if(sNewSpinnerPosition == null){
            sNewSpinnerPosition = newSpinnerPosition;
        }else {
            setOriginalSpinnerPosition(sNewSpinnerPosition);
            sNewSpinnerPosition = newSpinnerPosition;
        }
    }

    public static  boolean isInOriginalPosition(int currentSpinnerPosition){
        if(sOriginalSpinnerPosition == null){
            return true;
        }

        if(sOriginalSpinnerPosition == currentSpinnerPosition){
            return true;
        } else return false;
    }

    public static void clearOriginalPosition(){
        sOriginalSpinnerPosition = null;
    }
}
