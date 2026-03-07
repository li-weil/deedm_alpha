package com.deedm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deedm.validation")
public class InputValidationProperties {

    private boolean enabled = true;
    private int maxRequestBytes = 65536;
    private int defaultMaxStringLength = 1024;
    private int extendedMaxStringLength = 4096;
    private int filenameMaxLength = 128;
    private int maxCollectionSize = 200;
    private int maxMapEntries = 200;
    private int maxObjectDepth = 8;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxRequestBytes() {
        return maxRequestBytes;
    }

    public void setMaxRequestBytes(int maxRequestBytes) {
        this.maxRequestBytes = maxRequestBytes;
    }

    public int getDefaultMaxStringLength() {
        return defaultMaxStringLength;
    }

    public void setDefaultMaxStringLength(int defaultMaxStringLength) {
        this.defaultMaxStringLength = defaultMaxStringLength;
    }

    public int getExtendedMaxStringLength() {
        return extendedMaxStringLength;
    }

    public void setExtendedMaxStringLength(int extendedMaxStringLength) {
        this.extendedMaxStringLength = extendedMaxStringLength;
    }

    public int getFilenameMaxLength() {
        return filenameMaxLength;
    }

    public void setFilenameMaxLength(int filenameMaxLength) {
        this.filenameMaxLength = filenameMaxLength;
    }

    public int getMaxCollectionSize() {
        return maxCollectionSize;
    }

    public void setMaxCollectionSize(int maxCollectionSize) {
        this.maxCollectionSize = maxCollectionSize;
    }

    public int getMaxMapEntries() {
        return maxMapEntries;
    }

    public void setMaxMapEntries(int maxMapEntries) {
        this.maxMapEntries = maxMapEntries;
    }

    public int getMaxObjectDepth() {
        return maxObjectDepth;
    }

    public void setMaxObjectDepth(int maxObjectDepth) {
        this.maxObjectDepth = maxObjectDepth;
    }
}
