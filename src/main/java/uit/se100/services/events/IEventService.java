package uit.se100.services.events;

public interface IEventService {
    void publish(String topic, String key, Object event);
}
