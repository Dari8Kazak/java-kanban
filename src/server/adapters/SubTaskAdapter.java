package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.SubTask;
import model.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class SubTaskAdapter extends TypeAdapter<SubTask> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter out, SubTask value) throws IOException {
        out.beginObject();
        out.name("id").value(value.getId());
        out.name("name").value(value.getName());
        out.name("description").value(value.getDescription());
        out.name("status").value(value.getStatus().toString());
        out.name("duration").value(value.getDuration().toString());
        out.name("startTime").value(value.getStartTime().format(dtf));
        out.name("epicId").value(value.getEpicId());
        out.endObject();
    }

    @Override
    public SubTask read(JsonReader in) throws IOException {
        Integer id = null;
        String name = null;
        String description = null;
        TaskStatus status = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        Integer epicId = null;

        in.beginObject();
        while (in.hasNext()) {
            String field = in.nextName();

            if (field.equals("id")) {
                try {
                    id = in.nextInt();
                } catch (Exception ex) {
                    id = null;
                }

            } else if (field.equals("name")) {
                name = in.nextString();

            } else if (field.equals("description")) {
                description = in.nextString();

            } else if (field.equals("status")) {
                status = switch (in.nextString()) {
                    case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
                    case "DONE" -> TaskStatus.DONE;
                    default -> TaskStatus.NEW;
                };

            } else if (field.equals("duration")) {
                try {
                    duration = Duration.parse(in.nextString());
                } catch (DateTimeParseException ex) {
                    duration = Duration.ZERO;
                }

            } else if (field.equals("startTime")) {
                try {
                    startTime = LocalDateTime.parse(in.nextString(), dtf);
                } catch (DateTimeParseException ex) {
                    startTime = null;
                }

            } else if (field.equals("epicId")) {
                try {
                    epicId = in.nextInt();
                } catch (Exception ex) {
                    epicId = null;
                }

            } else {
                in.skipValue();
            }
        }
        in.endObject();

        if (Objects.isNull(id)) {
            return new SubTask(name, description, status.ordinal(), duration, startTime);
        } else {
            return new SubTask(id, name, description, status, duration, startTime, epicId);
        }
    }
}