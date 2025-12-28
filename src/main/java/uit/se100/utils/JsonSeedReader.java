package uit.se100.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonSeedReader {

    private final ObjectMapper objectMapper;

    public <T> List<T> readList(String path, Class<T> clazz) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            JavaType type = objectMapper
                    .getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read seed file: " + path, e);
        }
    }
}
