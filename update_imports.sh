#!/bin/bash

# Script to update import statements in legacy Java files
# This will update import statements to match the new package structure

LEGACY_DIR="/home/admin-unix/Deedm/backend/src/main/java/com/deedm/legacy"

# Update import statements
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import disbook\./import com.deedm.legacy.disbook./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import proplogic\./import com.deedm.legacy.proplogic./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import setrelfun\./import com.deedm.legacy.setrelfun./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import counting\./import com.deedm.legacy.counting./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import algebra\./import com.deedm.legacy.algebra./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import graph\./import com.deedm.legacy.graph./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import dataTable\./import com.deedm.legacy.dataTable./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import util\./import com.deedm.legacy.util./g' {} \;

# Update subpackage imports
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import formula\./import com.deedm.legacy.proplogic.formula./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import equiv\./import com.deedm.legacy.proplogic.equiv./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import normalFormula\./import com.deedm.legacy.proplogic.normalFormula./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import reason\./import com.deedm.legacy.proplogic.reason./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import ASTGraph\./import com.deedm.legacy.proplogic.formula.ASTGraph./g' {} \;

find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import filter\./import com.deedm.legacy.counting.filter./g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/import generator\./import com.deedm.legacy.counting.generator./g' {} \;

echo "Import statements updated successfully"