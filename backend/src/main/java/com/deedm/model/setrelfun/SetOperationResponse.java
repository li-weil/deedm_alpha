package com.deedm.model.setrelfun;

import java.util.List;

public class SetOperationResponse {
    private boolean success;
    private String errorMessage;
    private String type;
    private String formula;

    // 输入集合信息
    private String setU;
    private String setA;
    private String setB;

    // 运算结果
    private String intersectionResult;
    private String unionResult;
    private String subtractResult;
    private String complementAResult;
    private String complementBResult;
    private String differenceResult;
    private List<String> powerSetAResult;
    private List<String> powerSetBResult;

    // 构造函数
    public SetOperationResponse() {
        this.success = true;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getSetU() {
        return setU;
    }

    public void setSetU(String setU) {
        this.setU = setU;
    }

    public String getSetA() {
        return setA;
    }

    public void setSetA(String setA) {
        this.setA = setA;
    }

    public String getSetB() {
        return setB;
    }

    public void setSetB(String setB) {
        this.setB = setB;
    }

    public String getIntersectionResult() {
        return intersectionResult;
    }

    public void setIntersectionResult(String intersectionResult) {
        this.intersectionResult = intersectionResult;
    }

    public String getUnionResult() {
        return unionResult;
    }

    public void setUnionResult(String unionResult) {
        this.unionResult = unionResult;
    }

    public String getSubtractResult() {
        return subtractResult;
    }

    public void setSubtractResult(String subtractResult) {
        this.subtractResult = subtractResult;
    }

    public String getComplementAResult() {
        return complementAResult;
    }

    public void setComplementAResult(String complementAResult) {
        this.complementAResult = complementAResult;
    }

    public String getComplementBResult() {
        return complementBResult;
    }

    public void setComplementBResult(String complementBResult) {
        this.complementBResult = complementBResult;
    }

    public String getDifferenceResult() {
        return differenceResult;
    }

    public void setDifferenceResult(String differenceResult) {
        this.differenceResult = differenceResult;
    }

    public List<String> getPowerSetAResult() {
        return powerSetAResult;
    }

    public void setPowerSetAResult(List<String> powerSetAResult) {
        this.powerSetAResult = powerSetAResult;
    }

    public List<String> getPowerSetBResult() {
        return powerSetBResult;
    }

    public void setPowerSetBResult(List<String> powerSetBResult) {
        this.powerSetBResult = powerSetBResult;
    }
}