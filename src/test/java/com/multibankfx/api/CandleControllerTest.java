package com.multibankfx.api;

import com.multibankfx.dto.CandleData;
import com.multibankfx.service.CandleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CandleControllerTest {

    @Mock
    private CandleService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CandleController controller = new CandleController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCandleReturnsBadRequestWhenFromGreaterThanTo() throws Exception {
        mockMvc.perform(get("/history")
                        .param("symbol", "EURUSD")
                        .param("interval", "1m")
                        .param("from", "100")
                        .param("to", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCandleReturnsData() throws Exception {
        CandleData data = new CandleData("Ok", Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        when(service.getCandle("EURUSD", "1m", 0L, 10L)).thenReturn(data);

        mockMvc.perform(get("/history")
                        .param("symbol", "EURUSD")
                        .param("interval", "1m")
                        .param("from", "0")
                        .param("to", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.s").value("Ok"));
    }
}