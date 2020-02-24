package com.assignment.cart.dataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader {

    private static String testDataPath = "initialDbData";

    private ObjectMapper objectMapper;


    @Autowired
    private ResourceLoader resourceLoader;

    public DataLoader() {
        this.objectMapper = new ObjectMapper();
    }


    /**
     * Read data from json and convert to the type specified in the mapper attribute as an Array
     *
     * @param data
     * @return
     * @throws Exception
     */
    protected Object readData(PreLoadData data) throws Exception {
        return readData(data, false);
    }

     /**
     *
     * Read data from json and convert to the type specified in the mapper attribute
     *
     * if readDataAsSingleElement is false maps the value as an array
     * otherwise maps the value as a single element
     *
     * @param data
     * @param readDataAsSingleElement
     * @return
     * @throws Exception
     */
    protected Object readData(PreLoadData data, Boolean readDataAsSingleElement) throws Exception {

        Class<?> cls = Class.forName(data.getMapper());

        Resource resource = resourceLoader.getResource("classpath:" + testDataPath + "/" + data.getFile());
        InputStream dataStream = resource.getInputStream();

        if (readDataAsSingleElement) {
            return objectMapper.readValue(dataStream, cls.newInstance().getClass());
        } else {
            return objectMapper.readValue(dataStream, objectMapper.getTypeFactory().constructCollectionType(List.class, cls.newInstance().getClass()));
        }
    }

    /**
     * Read the com.assignment.product.config.json file from the mockData or preLoadData folders
     *
     * @param
     * @return
     * @throws
     */
    protected List<PreLoadData> readConfig() throws IOException {

        Resource resource = resourceLoader.getResource("classpath:" + testDataPath + "/config.json");
        InputStream configStream = resource.getInputStream();

        return objectMapper.readValue(configStream, objectMapper.getTypeFactory().constructCollectionType(List.class, PreLoadData.class));
    }
}

