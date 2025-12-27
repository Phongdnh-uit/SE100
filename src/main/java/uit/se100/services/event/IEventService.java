package uit.se100.services.event;

public interface IEventService {
    void publish(String topic, String key, Object event);
}
