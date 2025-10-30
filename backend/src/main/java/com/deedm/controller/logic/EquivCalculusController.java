package com.deedm.controller.logic;

import com.deedm.model.logic.EquivCalculusRequest;
import com.deedm.model.logic.EquivCalculusResponse;
import com.deedm.service.logic.EquivCalculusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/equiv-calculus")
public class EquivCalculusController {

    @Autowired
    private EquivCalculusService equivCalculusService;

    @PostMapping("/check")
    public EquivCalculusResponse checkEquivCalculus(@RequestBody EquivCalculusRequest request) {
        return equivCalculusService.checkEquivCalculus(request);
    }

    @PostMapping("/perform")
    public EquivCalculusResponse performEquivCalculus(@RequestBody EquivCalculusRequest request) {
        return equivCalculusService.performEquivCalculus(request);
    }
}