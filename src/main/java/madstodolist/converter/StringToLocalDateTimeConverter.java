package madstodolist.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime convert(String source) {
        try {
            if (source == null || source.trim().isEmpty() || source.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(source, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Please use the format 'yyyy-MM-dd'T'HH:mm'.");
        }
    }
}