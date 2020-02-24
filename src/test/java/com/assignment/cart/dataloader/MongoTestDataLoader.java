package com.assignment.cart.dataloader;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoTestDataLoader extends TestDataLoader {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void loadTestDataIntoDb(String documentName) throws Exception {

        List<PreLoadData> dataList = null;
        dataList = readConfig();
        for (PreLoadData data : dataList) {
            if (!StringUtils.isEmpty(documentName) &&
                    data.getName().equals(documentName)) {

                insertDataIntoDb(data);
                break;

            } else if (StringUtils.isEmpty(documentName)) {
                insertDataIntoDb(data);
            }
        }

    }

    /**
     * Read data from json and insert into the database
     *
     * @param data
     * @return
     * @throws Exception
     */
    private void insertDataIntoDb(PreLoadData data) throws Exception {

        Class<?> cls = Class.forName(data.getMapper());
        List<?> initialData = (List) readData(data);

        // drop Collection before save the data
        mongoTemplate.dropCollection(cls);
        // Save all documents fetched from json File
        mongoTemplate.insert(initialData, cls);
    }
}
