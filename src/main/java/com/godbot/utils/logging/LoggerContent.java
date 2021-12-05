package com.godbot.utils.logging;

import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LoggerContent {

    final String methodName;
    final HashMap<String, String> fields;
    final String time;
    final String type;
    final String description;

    /**
     * This is an Object that is used for logging
     * @param methodName The name of the method that the info / warning is coming from
     * @param fields Fields are usually important variables that are necessary for the function to work correctly which
     *               are printed out to see errors more easily
     * @param type the type is usually either "info" or "warning"
     */
    public LoggerContent(String type, String methodName, String description, HashMap<String, String> fields) {
        this.methodName = methodName;
        this.fields = fields;
        this.time = DateTimeFormatter.
                ofPattern("yyyy/MM/dd HH:mm:ss").
                format(LocalDateTime.now());
        this.type = type;
        this.description = description;
    }

    public String getAsString() {
        StringBuilder builder = new StringBuilder(this.methodName);
        builder.append(String.format("<%s>", this.description));
        for (Map.Entry<String, String> entry : this.fields.entrySet()) {
            builder.append(
                    String.format(
                            "|%s-%s",
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
        return builder.toString();
    }

    public Document getDBScheme() {
        Document object = new Document();
        object.append("type", this.type);
        object.append("methodName", this.methodName);
        object.append("description", this.description);
        object.append("time", this.time);
        if (!this.fields.isEmpty()) {
            for (Map.Entry<String, String> entry : this.fields.entrySet()) {
                object.append(entry.getKey(), entry.getValue());
            }
        }
        return object;
    }
}
