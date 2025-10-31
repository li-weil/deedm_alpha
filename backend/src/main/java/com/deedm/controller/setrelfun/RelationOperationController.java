package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.RelationOperationRequest;
import com.deedm.model.setrelfun.RelationOperationResponse;
import com.deedm.service.setrelfun.RelationOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/setrelfun/relation-operation")
@CrossOrigin(origins = "*")
public class RelationOperationController {

    @Autowired
    private RelationOperationService relationOperationService;

    @PostMapping("/calculate")
    public RelationOperationResponse calculateRelationOperation(@RequestBody RelationOperationRequest request) {
        return relationOperationService.executeRelationOperation(request);
    }

    @PostMapping("/validate")
    public Map<String, Object> validateRelationOperation(@RequestBody RelationOperationRequest request) {
        return relationOperationService.validateRelationOperation(request);
    }

    @PostMapping("/generate")
    public Map<String, Object> generateRandomRelations() {
        return relationOperationService.generateRandomRelations();
    }
}