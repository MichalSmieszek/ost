package pl.com.itti.app.driver.util.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import pl.com.itti.app.driver.model.Attachment;
import pl.com.itti.app.driver.model.Question;
import pl.com.itti.app.driver.model.enums.AnswerType;
import pl.com.itti.app.driver.model.enums.AttachmentType;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public final class SchemaCreator {

    private static final String QUESTION_ID = "question_";

    private static final String DISABLED = "ui:disabled";

    private static final String WIDGET = "ui:widget";

    private static final String FILE = "File";

    private static final String DESCRIPTION = "Description";

    private static final String COORDINATES = "Cooridnates";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ObjectNode COMMENT_SCHEMA = MAPPER.createObjectNode()
            .put("title", "Comment")
            .put("description", "What you think about this question?")
            .put("type", "string");

    private static final ObjectNode COMMENT_UI_SCHEMA = MAPPER.createObjectNode()
            .put(DISABLED, false);

    public static ObjectNode createSchemaForm(List<Question> questions, boolean disabled) throws IOException {
        questions.sort(Comparator.comparing(Question::getPosition));
        ObjectNode schemaForm = MAPPER.createObjectNode();

        ObjectNode schema = createSchema(questions);
        schemaForm.putPOJO("schema", schema);

        ObjectNode uiSchema = createUiSchema(questions, disabled);
        schemaForm.putPOJO("uiSchema", uiSchema);

        return schemaForm;
    }

    public static JSONObject getSchemaAsJSONObject(List<Question> questions) throws IOException {
        return new JSONObject(createSchema(questions).toString());
    }

    private static ObjectNode createSchema(List<Question> questions) throws IOException {
        ObjectNode schema = MAPPER.createObjectNode();
        schema.put("type", "object");

        ObjectNode properties = MAPPER.createObjectNode();
        for (Question question : questions) {
            properties.putPOJO(QUESTION_ID + question.getId(), createPropertyContent(question));
            if (question.isCommented()) {
                properties.putPOJO(QUESTION_ID + question.getId() + "_comment", COMMENT_SCHEMA);
            }
        }
        schema.putPOJO("properties", properties);

        return schema;
    }

    private static JsonNode createPropertyContent(Question question) throws IOException {
        return MAPPER.readTree(question.getJsonSchema());
    }

    private static ObjectNode createUiSchema(List<Question> questions, boolean disabled) {
        ObjectNode schema = MAPPER.createObjectNode();

        questions.forEach(question -> {
            schema.putPOJO(QUESTION_ID + question.getId(), createUiSchema(question, disabled));
            if (question.isCommented()) {
                schema.putPOJO(QUESTION_ID + question.getId() + "_comment", COMMENT_UI_SCHEMA);
            }
        });
        return schema;
    }

    private static ObjectNode createUiSchema(Question question, boolean disabled) {
        ObjectNode ui = MAPPER.createObjectNode();
        ui.put(DISABLED, disabled);

        if (question.getAnswerType().equals(AnswerType.RADIO_BUTTON)) {
            ui.put(WIDGET, "radio");
        } else if (question.getAnswerType().equals(AnswerType.SLIDER)) {
            ui.put(WIDGET, "slider");
        }

        return ui;
    }

    public static ObjectNode createAttachmentSchemaForm(List<Attachment> attachments) throws IOException {
        ObjectNode schema = MAPPER.createObjectNode();
        ObjectNode attachmentsObjectNode = MAPPER.createObjectNode();
        ObjectNode filesObjectNode = MAPPER.createObjectNode();
        ObjectNode descriptionsObjectNode = MAPPER.createObjectNode();
        ObjectNode coordinatesObjectNode = MAPPER.createObjectNode();

        for (Attachment attachment : attachments) {
            if (attachment.getType().equals(AttachmentType.LOCATION)) {
                coordinatesObjectNode.putPOJO("Attachment_" + attachment.getId().toString(), MAPPER.readTree("[" + attachment.getLatitude().toString() + ", " + attachment.getLongitude().toString() + ", " + attachment.getAltitude().toString() + "]"));
            }
            if (attachment.getType().equals(AttachmentType.DESCRIPTION)) {
                descriptionsObjectNode.putPOJO("Attachment_" + attachment.getId().toString(), MAPPER.readTree(attachment.getDescription()));
            }
            if (attachment.getType().equals(AttachmentType.PICTURE)) {
                filesObjectNode.putPOJO("Attachment_" + attachment.getId().toString(), MAPPER.readTree('"' + attachment.getUri() + '"'));
            }
        }

        attachmentsObjectNode.putPOJO(COORDINATES, coordinatesObjectNode);
        attachmentsObjectNode.putPOJO(DESCRIPTION, descriptionsObjectNode);
        attachmentsObjectNode.putPOJO(FILE, filesObjectNode);
        schema.putPOJO("Attachments: ", attachmentsObjectNode);

        return schema;
    }
}
