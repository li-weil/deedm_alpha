package com.deedm.model;

import java.util.ArrayList;
import java.util.List;

public class CleanupRequest {
    private List<String> filenames = new ArrayList<>();

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames == null ? new ArrayList<>() : filenames;
    }
}
