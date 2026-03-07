package com.deedm.validation;

import com.deedm.config.InputValidationConfiguration;
import com.deedm.controller.logic.FormulaSyntaxController;
import com.deedm.controller.logic.TruthValueController;
import com.deedm.model.logic.TruthValueResponse;
import com.deedm.service.logic.FormulaSyntaxService;
import com.deedm.service.logic.TruthValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TruthValueController.class, FormulaSyntaxController.class})
@AutoConfigureMockMvc(addFilters = true)
@Import({
    InputValidationConfiguration.class,
    RequestPayloadValidator.class,
    RequestPayloadValidationAdvice.class,
    RequestSizeLimitFilter.class,
    GlobalValidationExceptionHandler.class
})
@TestPropertySource(properties = {
    "deedm.validation.enabled=true",
    "deedm.validation.max-request-bytes=128",
    "deedm.validation.default-max-string-length=32",
    "deedm.validation.extended-max-string-length=64",
    "deedm.validation.filename-max-length=32",
    "deedm.validation.max-collection-size=10",
    "deedm.validation.max-map-entries=10",
    "deedm.validation.max-object-depth=4"
})
class RequestValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TruthValueService truthValueService;

    @MockBean
    private FormulaSyntaxService formulaSyntaxService;

    @Test
    void allowsValidTruthValueRequest() throws Exception {
        TruthValueResponse response = new TruthValueResponse();
        response.setTruthValue(true);
        when(truthValueService.calculateTruthValue(any())).thenReturn(response);

        mockMvc.perform(post("/truth-value/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "formula":"(p\\\\rightarrow q)\\\\wedge p",
                      "assignment":"10",
                      "variables":"pq",
                      "showDetailedProcess":false
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.truthValue").value(true));

        verify(truthValueService).calculateTruthValue(any());
    }

    @Test
    void rejectsIllegalControlCharacter() throws Exception {
        mockMvc.perform(post("/truth-value/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"formula\":\"abc\\u0001def\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("requestBody.formula contains illegal control characters"));
    }

    @Test
    void rejectsOverlongFormulaInMapRequest() throws Exception {
        String formula = "a".repeat(65);

        mockMvc.perform(post("/formula-syntax/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"formula\":\"" + formula + "\"}"))
            .andExpect(status().isPayloadTooLarge())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("requestBody[formula] length exceeds limit 64"));
    }

    @Test
    void rejectsOversizedJsonBody() throws Exception {
        String largePayload = "{\"formula\":\"" + "x".repeat(140) + "\"}";

        mockMvc.perform(post("/truth-value/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(largePayload))
            .andExpect(status().isPayloadTooLarge())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void passesValidFormulaSyntaxRequestToController() throws Exception {
        when(formulaSyntaxService.checkFormula(eq("(p\\vee q)"))).thenReturn(Map.of("valid", true));

        mockMvc.perform(post("/formula-syntax/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"formula\":\"(p\\\\vee q)\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }
}
