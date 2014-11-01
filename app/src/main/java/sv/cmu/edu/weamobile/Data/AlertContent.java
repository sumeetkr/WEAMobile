package sv.cmu.edu.weamobile.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sumeet on 9/24/14.
 */
public class AlertContent {

    private static List<Alert> alertItems = new ArrayList<Alert>();
    private static Map<Integer, Alert> alertsMap = new HashMap<Integer, Alert>();

    private static void addItem(Alert item) {
        alertItems.add(item);
        alertsMap.put(item.getId(), item);
    }

//    public static List<Alert> getAlerts(Context context){
//
//        alertItems.clear();
//        alertsMap.clear();
//
//        String json = AppConfigurationFactory.getStringProperty(context, "message");
//        AppConfiguration configuration = AppConfiguration.fromJson(json);
//
//        if(configuration != null && configuration.getAlerts().length>0){
//            List<Alert> alerts = Arrays.asList(configuration.getAlerts());
//            for(Alert alert:alerts){
//                addItem(alert);
//            }
//        }
//        return alertItems;
//    }

//    public static Map<Integer, Alert> getAlertsMap(){
//        return alertsMap;
//    }

}
