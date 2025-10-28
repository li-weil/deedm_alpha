package com.deedm.controller;

import com.deedm.model.NormalFormulaExpansionRequest;
import com.deedm.model.NormalFormulaExpansionResponse;
import com.deedm.service.NormalFormulaExpansionService;
import com.deedm.legacy.proplogic.normalFormula.SimpleConjunctiveFormula;
import com.deedm.legacy.proplogic.normalFormula.SimpleDisjunctiveFormula;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.normalFormula.NormalFormulaCalculator;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/normal-form-expansion")
public class NormalFormulaExpansionController {

    @Autowired
    private NormalFormulaExpansionService normalFormulaExpansionService;

    @PostMapping("/expand")
    public NormalFormulaExpansionResponse expandNormalFormula(@RequestBody NormalFormulaExpansionRequest request) {
        return normalFormulaExpansionService.expandNormalFormula(request);
    }

    @PostMapping("/check")
    public NormalFormulaExpansionResponse checkNormalFormula(@RequestBody NormalFormulaExpansionRequest request) {
        return normalFormulaExpansionService.checkNormalFormula(request);
    }

    @PostMapping("/generate")
    public List<String> generateFormulas(@RequestBody NormalFormulaExpansionRequest request) {
        List<String> formulas = new ArrayList<>();

        try {
            char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
            if (varSet == null) {
                return formulas;
            }

            boolean isDNF = request.isDNF();
            String symbolString;

            for (int i = 0; i < 4; i++) {
                if (isDNF) {
                    symbolString = NormalFormulaCalculator.randomGenerateSimpleConjunctiveFormula(varSet);
                } else {
                    symbolString = NormalFormulaCalculator.randomGenerateSimpleDisjunctiveFormula(varSet);
                }

                Formula formula = FormulaBuilder.buildFromSymbolFormulaString(symbolString);
                if (formula != null) {
                    formulas.add(formula.toSimpleLaTeXString());
                }
            }
        } catch (Exception e) {
            // Return empty list on error
        }

        return formulas;
    }
}