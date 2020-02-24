package com.assignment.cart.dataloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MongoDataLoader extends DataLoader {

    private static String initialDbData = "initialDbData";

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    private void init() throws Exception {
        loadDataIntoDb();
    }

    public void loadDataIntoDb() throws Exception {

        List<PreLoadData> dataList = null;
        dataList = readConfig(initialDbData);
        for (PreLoadData data : dataList) {
            if (!mongoTemplate.collectionExists(data.getName())) insertDataIntoDb(data);
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
