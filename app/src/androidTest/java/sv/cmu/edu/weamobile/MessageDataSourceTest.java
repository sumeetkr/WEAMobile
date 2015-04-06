package sv.cmu.edu.weamobile;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.utility.db.MessageDataSource;
import sv.cmu.edu.weamobile.utility.db.WEASQLiteHelper;

/**
 * Created by sumeet on 4/6/15.
 */
public class MessageDataSourceTest extends AndroidTestCase {
    private WEASQLiteHelper db;
    private MessageDataSource messageDataSource;

    public void setUp(){
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        db = new WEASQLiteHelper(context);
        messageDataSource = new MessageDataSource(getContext(),db);
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }

    public void testAddMessage(){
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": \"Alert\",\n" +
                "    \"scope\": \"Public\",\n" +
                "    \"status\": \"Exercise\",\n" +
                "    \"info\": [\n" +
                "    {\n" +
                "    \"certainty\": null,\n" +
                "    \"eventCategory\": \"Security\",\n" +
                "    \"eventType\": null,\n" +
                "    \"headline\": \"Headline\",\n" +
                "    \"eventDescription\": \"Description\",\n" +
                "    \"expires\": \"2015-04-05T09:40:57.000Z\",\n" +
                "    \"onset\": \"2015-04-05T09:10:57.000Z\",\n" +
                "    \"responseType\": \"Monitor\",\n" +
                "    \"urgency\": \"Future\",\n" +
                "    \"severity\": \"Moderate\",\n" +
                "    \"audience\": [],\n" +
                "    \"areas\": [\n" +
                "    {\n" +
                "    \"id\": 1,\n" +
                "    \"polygon\": [\n" +
                "    {\n" +
                "    \"lat\": \"37.412059573882146\",\n" +
                "    \"lng\": \"-122.06159800174404\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.40979274810897\",\n" +
                "    \"lng\": \"-122.06144779803921\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.409451866119724\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.41139487268086\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    ],\n" +
                "    \"resources\": []\n" +
                "    }\n" +
                "    ],\n" +
                "    \"parameter\": {\n" +
                "    \"isMapToBeShown\": false,\n" +
                "    \"isAlertActive\": true,\n" +
                "    \"isPhoneExpectedToVibrate\": true,\n" +
                "    \"isTextToSpeechExpected\": false,\n" +
                "    \"geoFiltering\": false,\n" +
                "    \"historyBasedFiltering\": false,\n" +
                "    \"motionPredictionBasedFiltering\": false,\n" +
                "    \"format\": null,\n" +
                "    \"testbedType\": \"Amber\"\n" +
                "    },\n" +
                "    \"incidents\": []\n" +
                "    }";

        Message data = Message.fromJson(json);
        messageDataSource.insertData(data);

        assertTrue(messageDataSource.getAllData().size()>0);
    }

    public void testAddTwoMessages(){
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": \"Alert\",\n" +
                "    \"scope\": \"Public\",\n" +
                "    \"status\": \"Exercise\",\n" +
                "    \"info\": [\n" +
                "    {\n" +
                "    \"certainty\": null,\n" +
                "    \"eventCategory\": \"Security\",\n" +
                "    \"eventType\": null,\n" +
                "    \"headline\": \"Headline\",\n" +
                "    \"eventDescription\": \"Description\",\n" +
                "    \"expires\": \"2015-04-05T09:40:57.000Z\",\n" +
                "    \"onset\": \"2015-04-05T09:10:57.000Z\",\n" +
                "    \"responseType\": \"Monitor\",\n" +
                "    \"urgency\": \"Future\",\n" +
                "    \"severity\": \"Moderate\",\n" +
                "    \"audience\": [],\n" +
                "    \"areas\": [\n" +
                "    {\n" +
                "    \"id\": 1,\n" +
                "    \"polygon\": [\n" +
                "    {\n" +
                "    \"lat\": \"37.412059573882146\",\n" +
                "    \"lng\": \"-122.06159800174404\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.40979274810897\",\n" +
                "    \"lng\": \"-122.06144779803921\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.409451866119724\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.41139487268086\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    ],\n" +
                "    \"resources\": []\n" +
                "    }\n" +
                "    ],\n" +
                "    \"parameter\": {\n" +
                "    \"isMapToBeShown\": false,\n" +
                "    \"isAlertActive\": true,\n" +
                "    \"isPhoneExpectedToVibrate\": true,\n" +
                "    \"isTextToSpeechExpected\": false,\n" +
                "    \"geoFiltering\": false,\n" +
                "    \"historyBasedFiltering\": false,\n" +
                "    \"motionPredictionBasedFiltering\": false,\n" +
                "    \"format\": null,\n" +
                "    \"testbedType\": \"Amber\"\n" +
                "    },\n" +
                "    \"incidents\": []\n" +
                "    }";

        String json2 = "{\n" +
                "    \"id\": 2,\n" +
                "    \"messageType\": \"Alert\",\n" +
                "    \"scope\": \"Public\",\n" +
                "    \"status\": \"Exercise\",\n" +
                "    \"info\": [\n" +
                "    {\n" +
                "    \"certainty\": null,\n" +
                "    \"eventCategory\": \"Security\",\n" +
                "    \"eventType\": null,\n" +
                "    \"headline\": \"Headline\",\n" +
                "    \"eventDescription\": \"Description\",\n" +
                "    \"expires\": \"2015-04-05T09:40:57.000Z\",\n" +
                "    \"onset\": \"2015-04-05T09:10:57.000Z\",\n" +
                "    \"responseType\": \"Monitor\",\n" +
                "    \"urgency\": \"Future\",\n" +
                "    \"severity\": \"Moderate\",\n" +
                "    \"audience\": [],\n" +
                "    \"areas\": [\n" +
                "    {\n" +
                "    \"id\": 1,\n" +
                "    \"polygon\": [\n" +
                "    {\n" +
                "    \"lat\": \"37.412059573882146\",\n" +
                "    \"lng\": \"-122.06159800174404\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.40979274810897\",\n" +
                "    \"lng\": \"-122.06144779803921\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.409451866119724\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.41139487268086\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    ],\n" +
                "    \"resources\": []\n" +
                "    }\n" +
                "    ],\n" +
                "    \"parameter\": {\n" +
                "    \"isMapToBeShown\": false,\n" +
                "    \"isAlertActive\": true,\n" +
                "    \"isPhoneExpectedToVibrate\": true,\n" +
                "    \"isTextToSpeechExpected\": false,\n" +
                "    \"geoFiltering\": false,\n" +
                "    \"historyBasedFiltering\": false,\n" +
                "    \"motionPredictionBasedFiltering\": false,\n" +
                "    \"format\": null,\n" +
                "    \"testbedType\": \"Amber\"\n" +
                "    },\n" +
                "    \"incidents\": []\n" +
                "    }";


        Message data1 = Message.fromJson(json);
        Message data2 = Message.fromJson(json2);
        messageDataSource.insertData(data1);
        messageDataSource.insertData(data2);

        assertTrue(messageDataSource.getAllData().size()>1);
    }

    public void testDuplicateMessages(){
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": \"Alert\",\n" +
                "    \"scope\": \"Public\",\n" +
                "    \"status\": \"Exercise\",\n" +
                "    \"info\": [\n" +
                "    {\n" +
                "    \"certainty\": null,\n" +
                "    \"eventCategory\": \"Security\",\n" +
                "    \"eventType\": null,\n" +
                "    \"headline\": \"Headline\",\n" +
                "    \"eventDescription\": \"Description\",\n" +
                "    \"expires\": \"2015-04-05T09:40:57.000Z\",\n" +
                "    \"onset\": \"2015-04-05T09:10:57.000Z\",\n" +
                "    \"responseType\": \"Monitor\",\n" +
                "    \"urgency\": \"Future\",\n" +
                "    \"severity\": \"Moderate\",\n" +
                "    \"audience\": [],\n" +
                "    \"areas\": [\n" +
                "    {\n" +
                "    \"id\": 1,\n" +
                "    \"polygon\": [\n" +
                "    {\n" +
                "    \"lat\": \"37.412059573882146\",\n" +
                "    \"lng\": \"-122.06159800174404\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.40979274810897\",\n" +
                "    \"lng\": \"-122.06144779803921\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.409451866119724\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.41139487268086\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    ],\n" +
                "    \"resources\": []\n" +
                "    }\n" +
                "    ],\n" +
                "    \"parameter\": {\n" +
                "    \"isMapToBeShown\": false,\n" +
                "    \"isAlertActive\": true,\n" +
                "    \"isPhoneExpectedToVibrate\": true,\n" +
                "    \"isTextToSpeechExpected\": false,\n" +
                "    \"geoFiltering\": false,\n" +
                "    \"historyBasedFiltering\": false,\n" +
                "    \"motionPredictionBasedFiltering\": false,\n" +
                "    \"format\": null,\n" +
                "    \"testbedType\": \"Amber\"\n" +
                "    },\n" +
                "    \"incidents\": []\n" +
                "    }";

        String json2 = "{\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": \"Alert\",\n" +
                "    \"scope\": \"Public\",\n" +
                "    \"status\": \"Exercise\",\n" +
                "    \"info\": [\n" +
                "    {\n" +
                "    \"certainty\": null,\n" +
                "    \"eventCategory\": \"Security\",\n" +
                "    \"eventType\": null,\n" +
                "    \"headline\": \"Headline\",\n" +
                "    \"eventDescription\": \"Description\",\n" +
                "    \"expires\": \"2015-04-05T09:40:57.000Z\",\n" +
                "    \"onset\": \"2015-04-05T09:10:57.000Z\",\n" +
                "    \"responseType\": \"Monitor\",\n" +
                "    \"urgency\": \"Future\",\n" +
                "    \"severity\": \"Moderate\",\n" +
                "    \"audience\": [],\n" +
                "    \"areas\": [\n" +
                "    {\n" +
                "    \"id\": 1,\n" +
                "    \"polygon\": [\n" +
                "    {\n" +
                "    \"lat\": \"37.412059573882146\",\n" +
                "    \"lng\": \"-122.06159800174404\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.40979274810897\",\n" +
                "    \"lng\": \"-122.06144779803921\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.409451866119724\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"lat\": \"37.41139487268086\",\n" +
                "    \"lng\": \"-122.05812185886074\"\n" +
                "    }\n" +
                "    ]\n" +
                "    }\n" +
                "    ],\n" +
                "    \"resources\": []\n" +
                "    }\n" +
                "    ],\n" +
                "    \"parameter\": {\n" +
                "    \"isMapToBeShown\": false,\n" +
                "    \"isAlertActive\": true,\n" +
                "    \"isPhoneExpectedToVibrate\": true,\n" +
                "    \"isTextToSpeechExpected\": false,\n" +
                "    \"geoFiltering\": false,\n" +
                "    \"historyBasedFiltering\": false,\n" +
                "    \"motionPredictionBasedFiltering\": false,\n" +
                "    \"format\": null,\n" +
                "    \"testbedType\": \"Amber\"\n" +
                "    },\n" +
                "    \"incidents\": []\n" +
                "    }";


        Message data1 = Message.fromJson(json);
        Message data2 = Message.fromJson(json2);
        messageDataSource.insertDataIfNotPresent(data1);
        messageDataSource.insertDataIfNotPresent(data2);

        assertTrue(messageDataSource.getAllData().size()<2);
    }
}
