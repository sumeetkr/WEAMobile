package sv.cmu.edu.weamobile.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sumeet on 9/24/14.
 */
public class AlertContent {
    public static class AlertItem {

        public String id;
        public String content;

        public AlertItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static List<AlertItem> ITEMS = new ArrayList<AlertItem>();
    public static Map<String, AlertItem> ITEM_MAP = new HashMap<String, AlertItem>();

    static {

        addItem(new AlertItem("1", "Free food in kitchen"));
        addItem(new AlertItem("2", "Seminar in Room 118"));
        addItem(new AlertItem("3", "No classes today !!"));
    }

    private static void addItem(AlertItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

}
