package com.assignment.cart.dataloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MongoDataLoader extends DataLoader {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    Environment environment;

    @PostConstruct
    private void init() throws Exception {
        if(!environment.acceptsProfiles("test")) {
            loadDataIntoDb();
        }
    }

    public void loadDataIntoDb() throws Exception {

        List<PreLoadData> dataList = null;
        dataList = readConfig();
        for (PreLoadData data : dataList) {
            if (!mongoTemplate.collectionExists(data.getName()))
                insertDataIntoDb(data);
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
