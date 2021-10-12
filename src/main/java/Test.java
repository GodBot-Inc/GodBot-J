import java.util.HashMap;

import utils.DBCommunication;
import utils.LoggerContent;

public class Test {

    public static void main(String[] args) {
        DBCommunication db = DBCommunication.getInstance();
        db.generalLog(new LoggerContent("testMethodName", new HashMap<String, String>() {{ put("First Field Key", "first field value"); }}, "info"));
    }
}
