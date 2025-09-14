#!/bin/bash

# Script to update package declarations in legacy Java files
# This will update all package declarations to match the new package structure

LEGACY_DIR="/home/admin-unix/Deedm/backend/src/main/java/com/deedm/legacy"

# Update package declarations using sed
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package disbook;/package com.deedm.legacy.disbook;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package proplogic;/package com.deedm.legacy.proplogic;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package setrelfun;/package com.deedm.legacy.setrelfun;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package counting;/package com.deedm.legacy.counting;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package algebra;/package com.deedm.legacy.algebra;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package graph;/package com.deedm.legacy.graph;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package dataTable;/package com.deedm.legacy.dataTable;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package util;/package com.deedm.legacy.util;/g' {} \;

# Update subpackages
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package formula;/package com.deedm.legacy.proplogic.formula;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package equiv;/package com.deedm.legacy.proplogic.equiv;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package normalFormula;/package com.deedm.legacy.proplogic.normalFormula;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package reason;/package com.deedm.legacy.proplogic.reason;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package ASTGraph;/package com.deedm.legacy.proplogic.formula.ASTGraph;/g' {} \;

find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package filter;/package com.deedm.legacy.counting.filter;/g' {} \;
find "$LEGACY_DIR" -name "*.java" -type f -exec sed -i 's/^package generator;/package com.deedm.legacy.counting.generator;/g' {} \;

echo "Package declarations updated successfully"