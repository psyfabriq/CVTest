package pfq.ocrserver.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pfq.ocrserver.models.Document;

public class EventManager {
    Map<String, List<EventListener>> listeners = new HashMap<>();

    public EventManager(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    public void subscribe(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        users.add(listener);
    }

    public void unsubscribe(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        int index = users.indexOf(listener);
        users.remove(index);
    }

    public void notify(String eventType,Document t) {
        List<EventListener> users = listeners.get(eventType);
        for (EventListener listener : users) {
            listener.update(t);
        }
    }
}
