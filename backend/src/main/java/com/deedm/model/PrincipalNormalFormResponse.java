package com.deedm.model;

import java.util.List;
import java.util.Map;

public class PrincipalNormalFormResponse {
    private boolean success;
    private String message;
    private String errorMessage;

    // Formula information
    private String originalFormula;
    private String combinedFormula;

    // CNF results
    private CNFResult cnfResult;

    // DNF results
    private DNFResult dnfResult;

    // Calculation steps (when detailed mode is enabled)
    private List<CalculationStep> cnfSteps;
    private List<CalculationStep> dnfSteps;

    // Truth table (when table method is used)
    private String truthTable;

    // Metadata
    private int index;
    private Map<String, Object> metadata;

    public PrincipalNormalFormResponse() {}

    public PrincipalNormalFormResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOriginalFormula() {
        return originalFormula;
    }

    public void setOriginalFormula(String originalFormula) {
        this.originalFormula = originalFormula;
    }

    public String getCombinedFormula() {
        return combinedFormula;
    }

    public void setCombinedFormula(String combinedFormula) {
        this.combinedFormula = combinedFormula;
    }

    public CNFResult getCnfResult() {
        return cnfResult;
    }

    public void setCnfResult(CNFResult cnfResult) {
        this.cnfResult = cnfResult;
    }

    public DNFResult getDnfResult() {
        return dnfResult;
    }

    public void setDnfResult(DNFResult dnfResult) {
        this.dnfResult = dnfResult;
    }

    public List<CalculationStep> getCnfSteps() {
        return cnfSteps;
    }

    public void setCnfSteps(List<CalculationStep> cnfSteps) {
        this.cnfSteps = cnfSteps;
    }

    public List<CalculationStep> getDnfSteps() {
        return dnfSteps;
    }

    public void setDnfSteps(List<CalculationStep> dnfSteps) {
        this.dnfSteps = dnfSteps;
    }

    public String getTruthTable() {
        return truthTable;
    }

    public void setTruthTable(String truthTable) {
        this.truthTable = truthTable;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // Inner classes for structured results
    public static class CNFResult {
        private String finalFormula;
        private String pcnf; // Principal Conjunctive Normal Form
        private String pdnf; // Principal Disjunctive Normal Form (derived from PCNF)

        public CNFResult() {}

        public CNFResult(String finalFormula, String pcnf, String pdnf) {
            this.finalFormula = finalFormula;
            this.pcnf = pcnf;
            this.pdnf = pdnf;
        }

        // Getters and Setters
        public String getFinalFormula() {
            return finalFormula;
        }

        public void setFinalFormula(String finalFormula) {
            this.finalFormula = finalFormula;
        }

        public String getPcnf() {
            return pcnf;
        }

        public void setPcnf(String pcnf) {
            this.pcnf = pcnf;
        }

        public String getPdnf() {
            return pdnf;
        }

        public void setPdnf(String pdnf) {
            this.pdnf = pdnf;
        }
    }

    public static class DNFResult {
        private String finalFormula;
        private String pdnf; // Principal Disjunctive Normal Form
        private String pcnf; // Principal Conjunctive Normal Form (derived from PDNF)

        public DNFResult() {}

        public DNFResult(String finalFormula, String pdnf, String pcnf) {
            this.finalFormula = finalFormula;
            this.pdnf = pdnf;
            this.pcnf = pcnf;
        }

        // Getters and Setters
        public String getFinalFormula() {
            return finalFormula;
        }

        public void setFinalFormula(String finalFormula) {
            this.finalFormula = finalFormula;
        }

        public String getPdnf() {
            return pdnf;
        }

        public void setPdnf(String pdnf) {
            this.pdnf = pdnf;
        }

        public String getPcnf() {
            return pcnf;
        }

        public void setPcnf(String pcnf) {
            this.pcnf = pcnf;
        }
    }

    public static class CalculationStep {
        private String formula;
        private String comment;

        public CalculationStep() {}

        public CalculationStep(String formula, String comment) {
            this.formula = formula;
            this.comment = comment;
        }

        // Getters and Setters
        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}