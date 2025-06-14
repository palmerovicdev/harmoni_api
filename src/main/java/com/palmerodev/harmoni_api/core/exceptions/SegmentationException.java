package com.palmerodev.harmoni_api.core.exceptions;

public class SegmentationException extends PrimaryException {

    public SegmentationException(String message) {
        super(message, "Segmentation Error");
    }

}
