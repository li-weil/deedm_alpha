package com.deedm.model.graph;

public class HuffmanTreeRequest {
    private String leafString;
    private boolean executeHuffmanAlgorithm = true;
    private boolean showAlgorithmDetails = false;
    private boolean showLeafCodes = false;

    public HuffmanTreeRequest() {}

    public String getLeafString() {
        return leafString;
    }

    public void setLeafString(String leafString) {
        this.leafString = leafString;
    }

    public boolean isExecuteHuffmanAlgorithm() {
        return executeHuffmanAlgorithm;
    }

    public void setExecuteHuffmanAlgorithm(boolean executeHuffmanAlgorithm) {
        this.executeHuffmanAlgorithm = executeHuffmanAlgorithm;
    }

    public boolean isShowAlgorithmDetails() {
        return showAlgorithmDetails;
    }

    public void setShowAlgorithmDetails(boolean showAlgorithmDetails) {
        this.showAlgorithmDetails = showAlgorithmDetails;
    }

    public boolean isShowLeafCodes() {
        return showLeafCodes;
    }

    public void setShowLeafCodes(boolean showLeafCodes) {
        this.showLeafCodes = showLeafCodes;
    }
}