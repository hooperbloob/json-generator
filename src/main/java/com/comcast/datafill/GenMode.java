package com.comcast.datafill;

/**
 *
 * @author bremed200
 */
public enum GenMode {

    Single("single"),
    Array("array");

    public final String label;

    private GenMode(String theLabel) {
        label = theLabel;
    }
}
