package com.deedm.model.setrelfun;

public class RelationOperationRequest {
    private String setA;
    private String setB;
    private String setC;
    private String relationR;
    private String relationS;
    private int relationRFromSet;  // 0=A, 1=B, 2=C
    private int relationRToSet;    // 0=A, 1=B, 2=C
    private int relationSFromSet;  // 0=A, 1=B, 2=C
    private int relationSToSet;    // 0=A, 1=B, 2=C
    private boolean intTypeElement;
    private boolean intersection;
    private boolean union;
    private boolean subtract;
    private boolean inverse;
    private boolean composite;
    private boolean invcomp;
    private boolean useMatrix;
    private boolean useGraph;

    public RelationOperationRequest() {}

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

    public String getSetC() {
        return setC;
    }

    public void setSetC(String setC) {
        this.setC = setC;
    }

    public String getRelationR() {
        return relationR;
    }

    public void setRelationR(String relationR) {
        this.relationR = relationR;
    }

    public String getRelationS() {
        return relationS;
    }

    public void setRelationS(String relationS) {
        this.relationS = relationS;
    }

    public int getRelationRFromSet() {
        return relationRFromSet;
    }

    public void setRelationRFromSet(int relationRFromSet) {
        this.relationRFromSet = relationRFromSet;
    }

    public int getRelationRToSet() {
        return relationRToSet;
    }

    public void setRelationRToSet(int relationRToSet) {
        this.relationRToSet = relationRToSet;
    }

    public int getRelationSFromSet() {
        return relationSFromSet;
    }

    public void setRelationSFromSet(int relationSFromSet) {
        this.relationSFromSet = relationSFromSet;
    }

    public int getRelationSToSet() {
        return relationSToSet;
    }

    public void setRelationSToSet(int relationSToSet) {
        this.relationSToSet = relationSToSet;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }

    public boolean isUnion() {
        return union;
    }

    public void setUnion(boolean union) {
        this.union = union;
    }

    public boolean isSubtract() {
        return subtract;
    }

    public void setSubtract(boolean subtract) {
        this.subtract = subtract;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public boolean isInvcomp() {
        return invcomp;
    }

    public void setInvcomp(boolean invcomp) {
        this.invcomp = invcomp;
    }

    public boolean isUseMatrix() {
        return useMatrix;
    }

    public void setUseMatrix(boolean useMatrix) {
        this.useMatrix = useMatrix;
    }

    public boolean isUseGraph() {
        return useGraph;
    }

    public void setUseGraph(boolean useGraph) {
        this.useGraph = useGraph;
    }

    @Override
    public String toString() {
        return "RelationOperationRequest{" +
                "setA='" + setA + '\'' +
                ", setB='" + setB + '\'' +
                ", setC='" + setC + '\'' +
                ", relationR='" + relationR + '\'' +
                ", relationS='" + relationS + '\'' +
                ", relationRFromSet=" + relationRFromSet +
                ", relationRToSet=" + relationRToSet +
                ", relationSFromSet=" + relationSFromSet +
                ", relationSToSet=" + relationSToSet +
                ", intTypeElement=" + intTypeElement +
                ", intersection=" + intersection +
                ", union=" + union +
                ", subtract=" + subtract +
                ", inverse=" + inverse +
                ", composite=" + composite +
                ", invcomp=" + invcomp +
                ", useMatrix=" + useMatrix +
                ", useGraph=" + useGraph +
                '}';
    }
}