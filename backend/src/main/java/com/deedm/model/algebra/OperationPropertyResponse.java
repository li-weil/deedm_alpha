package com.deedm.model.algebra;

import java.util.List;

public class OperationPropertyResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息
    private String type; // 类型标记
    private String formula;
    private String setAString;
    private String operator1String;
    private String operator2String;
    private boolean intTypeElement;

    // 操作符表格
    private String operator1Table;
    private String operator2Table;

    // 运算性质判断结果
    private List<PropertyResult> operator1Properties;
    private List<PropertyResult> operator2Properties;

    // 运算符之间的关系性质
    private List<RelationProperty> relationProperties;

    // 构造函数
    public OperationPropertyResponse() {
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

    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getOperator1String() {
        return operator1String;
    }

    public void setOperator1String(String operator1String) {
        this.operator1String = operator1String;
    }

    public String getOperator2String() {
        return operator2String;
    }

    public void setOperator2String(String operator2String) {
        this.operator2String = operator2String;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public String getOperator1Table() {
        return operator1Table;
    }

    public void setOperator1Table(String operator1Table) {
        this.operator1Table = operator1Table;
    }

    public String getOperator2Table() {
        return operator2Table;
    }

    public void setOperator2Table(String operator2Table) {
        this.operator2Table = operator2Table;
    }

    public List<PropertyResult> getOperator1Properties() {
        return operator1Properties;
    }

    public void setOperator1Properties(List<PropertyResult> operator1Properties) {
        this.operator1Properties = operator1Properties;
    }

    public List<PropertyResult> getOperator2Properties() {
        return operator2Properties;
    }

    public void setOperator2Properties(List<PropertyResult> operator2Properties) {
        this.operator2Properties = operator2Properties;
    }

    public List<RelationProperty> getRelationProperties() {
        return relationProperties;
    }

    public void setRelationProperties(List<RelationProperty> relationProperties) {
        this.relationProperties = relationProperties;
    }

    // 内部类：性质判断结果
    public static class PropertyResult {
        private String propertyType; // commutative, associative, idempotent, etc.
        private boolean hasProperty;
        private String reason;
        private String details; // 详细信息，如单位元、逆元等

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        public boolean isHasProperty() {
            return hasProperty;
        }

        public void setHasProperty(boolean hasProperty) {
            this.hasProperty = hasProperty;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }

    // 内部类：运算符之间关系性质
    public static class RelationProperty {
        private String relationType; // distributive, absorption
        private String operator1Name;
        private String operator2Name;
        private boolean hasRelation;
        private String reason;
        private String direction; // 对于分配律，表示哪个运算符对哪个运算符分配

        public String getRelationType() {
            return relationType;
        }

        public void setRelationType(String relationType) {
            this.relationType = relationType;
        }

        public String getOperator1Name() {
            return operator1Name;
        }

        public void setOperator1Name(String operator1Name) {
            this.operator1Name = operator1Name;
        }

        public String getOperator2Name() {
            return operator2Name;
        }

        public void setOperator2Name(String operator2Name) {
            this.operator2Name = operator2Name;
        }

        public boolean isHasRelation() {
            return hasRelation;
        }

        public void setHasRelation(boolean hasRelation) {
            this.hasRelation = hasRelation;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
}