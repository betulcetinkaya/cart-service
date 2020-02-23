package com.assignment.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


public class ControllerBaseTest {
    @Autowired
    protected MockMvc mockMvc;

    protected static MediaType mediaType = MediaType.APPLICATION_JSON;
    protected static String baseAddress;

    protected static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }


}
