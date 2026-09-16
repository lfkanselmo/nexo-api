package com.nexo.infrastructure.rest;

import static org.hamcrest.Matchers.containsString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createPropertyId() throws Exception {
        return extractId(mockMvc
                .perform(MockMvcRequestBuilders.post("/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"Calle 10 # 5-20\",\"city\":\"Bogotá\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString());
    }

    private String createTenantId() throws Exception {
        return extractId(mockMvc
                .perform(MockMvcRequestBuilders.post("/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Ana Gómez\",\"email\":\"ana@example.com\",\"documentId\":\"CC123456\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString());
    }

    private String extractId(String json) throws Exception {
        return objectMapper.readTree(json).get("id").asText();
    }

    private String createContractRequest(String propertyId, String tenantId) {
        return """
                {
                  "propertyId": "%s",
                  "tenantId": "%s",
                  "startDate": "2026-01-01",
                  "endDate": "2026-12-31",
                  "monthlyRent": 1200.00,
                  "dailyInterestRate": 0.0015
                }
                """.formatted(propertyId, tenantId);
    }

    @Test
    void createsAndActivatesAContract() throws Exception {
        String propertyId = createPropertyId();
        String tenantId = createTenantId();

        String contractJson = mockMvc
                .perform(MockMvcRequestBuilders.post("/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createContractRequest(propertyId, tenantId)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String contractId = extractId(contractJson);

        mockMvc.perform(MockMvcRequestBuilders.post("/contracts/" + contractId + "/activate"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsAnInvalidTransitionWithConflict() throws Exception {
        String propertyId = createPropertyId();
        String tenantId = createTenantId();

        String contractJson = mockMvc
                .perform(MockMvcRequestBuilders.post("/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createContractRequest(propertyId, tenantId)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String contractId = extractId(contractJson);

        mockMvc.perform(MockMvcRequestBuilders.post("/contracts/" + contractId + "/terminate"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", containsString("DRAFT")));
    }

    @Test
    void returnsNotFoundForAnUnknownContract() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contracts/00000000-0000-0000-0000-000000000000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
