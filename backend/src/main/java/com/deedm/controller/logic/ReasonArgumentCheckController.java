package com.deedm.controller.logic;

import com.deedm.model.logic.ReasonArgumentCheckRequest;
import com.deedm.model.logic.ReasonArgumentCheckResponse;
import com.deedm.service.logic.ReasonArgumentCheckService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/reason-argument-check")
public class ReasonArgumentCheckController {

    @Autowired
    private ReasonArgumentCheckService reasonArgumentCheckService;

    @PostMapping("/check")
    public ReasonArgumentCheckResponse checkReasonArgument(@RequestBody ReasonArgumentCheckRequest request) {
        return reasonArgumentCheckService.checkReasonArgument(request);
    }

    @PostMapping("/validate")
    public ReasonArgumentCheckResponse validateInput(@RequestBody ReasonArgumentCheckRequest request) {
        return reasonArgumentCheckService.validateInput(request);
    }
}