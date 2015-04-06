package sv.cmu.edu.weamobile.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.utility.Logger;

/**
 * Created by sumeet on 4/6/15.
 */
public class Configuration {

    private String json;
    private List<Message> messages;

    public static Configuration fromJson(String jsonString) {
        Configuration configuration= new Configuration();
        if(isValidJson(jsonString)){
            try {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray jArray = parser.parse(jsonString).getAsJsonArray();

                List<Message> messages = new ArrayList<Message>() ;
                for(JsonElement obj : jArray )
                {
                    messages.add(gson.fromJson( obj , Message.class));
                }

                configuration.setMessages(messages);
                configuration.setJson(jsonString);
            }catch (Exception ex){
                Logger.log(ex.getMessage());
            }
        }
        return configuration;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static boolean isValidJson(String json){
        return true;
    }
}
