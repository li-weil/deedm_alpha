package com.deedm.controller;

import com.deedm.model.EquivCalculusRequest;
import com.deedm.model.EquivCalculusResponse;
import com.deedm.service.EquivCalculusService;
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