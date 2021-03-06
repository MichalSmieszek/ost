package eu.fp7.driver.ost.driver.util.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.fp7.driver.ost.core.security.security.model.AuthUser;
import eu.fp7.driver.ost.driver.model.AnswerTrialRole;
import eu.fp7.driver.ost.driver.model.Attachment;
import eu.fp7.driver.ost.driver.model.Question;
import eu.fp7.driver.ost.driver.model.TrialRole;
import eu.fp7.driver.ost.driver.model.TrialStage;
import eu.fp7.driver.ost.driver.model.enums.AnswerType;
import eu.fp7.driver.ost.driver.util.SchemaCreatorProperties;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class SchemaCreator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ObjectNode COMMENT_SCHEMA = MAPPER.createObjectNode()
            .put("title", "Please add justification here")
            .put("description", "")
            .put("type", "string");

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
            properties.putPOJO(SchemaCreatorProperties.QUESTION_ID + question.getId(), createPropertyContent(question));
            if (question.isCommented()) {
                properties.putPOJO(SchemaCreatorProperties.QUESTION_ID + question.getId() + "_comment", COMMENT_SCHEMA);
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
            schema.putPOJO(SchemaCreatorProperties.QUESTION_ID + question.getId(), createUiSchema(question, disabled));
            if (question.isCommented()) {
                schema.putPOJO(SchemaCreatorProperties.QUESTION_ID + question.getId() + "_comment", createCommentUiSchema(disabled));
            }
        });
        return schema;
    }

    private static ObjectNode createUiSchema(Question question, boolean disabled) {
        ObjectNode ui = MAPPER.createObjectNode();
        ui.put(SchemaCreatorProperties.FIELD_DISABLED, disabled);

        if (question.getAnswerType().equals(AnswerType.RADIO_BUTTON)) {
            ui.put(SchemaCreatorProperties.FIELD_WIDGET, "radio");
        } else if (question.getAnswerType().equals(AnswerType.SLIDER)) {
            ui.put(SchemaCreatorProperties.FIELD_WIDGET, "slider");
        } else if (question.getAnswerType().equals(AnswerType.CHECKBOX)) {
            ui.put(SchemaCreatorProperties.FIELD_WIDGET, "checkboxes");
            ui.put(SchemaCreatorProperties.FIELD_CLASS_NAME, "checkboxes-class");
        } else if (question.getAnswerType().equals(AnswerType.BOX_LIST)) {
            ui.putPOJO(SchemaCreatorProperties.FIELD_OPTIONS, createOptions(AnswerType.BOX_LIST));
        } else if (question.getAnswerType().equals(AnswerType.RADIO_LINE)) {
            ui.put(SchemaCreatorProperties.FIELD_WIDGET, "radio");
            ui.putPOJO(SchemaCreatorProperties.FIELD_OPTIONS, createOptions(AnswerType.RADIO_LINE));
        }

        return ui;
    }

    private static ObjectNode createOptions(AnswerType answerType) {
        ObjectNode options = MAPPER.createObjectNode();

        if (answerType.equals(AnswerType.BOX_LIST)) {
            options.put(SchemaCreatorProperties.FIELD_ADDABLE, false);
            options.put(SchemaCreatorProperties.FIELD_ORDERABLE, false);
            options.put(SchemaCreatorProperties.FIELD_REMOVABLE, false);
        } else if (answerType.equals(AnswerType.RADIO_LINE)) {
            options.put(SchemaCreatorProperties.FIELD_INLINE, true);
        }

        return options;
    }

    private static ObjectNode createCommentUiSchema(boolean disabled) {
        ObjectNode comment = MAPPER.createObjectNode();

        comment.put(SchemaCreatorProperties.FIELD_DISABLED, disabled);
        comment.put(SchemaCreatorProperties.FIELD_CLASS_NAME, "comment-class");
        comment.put(SchemaCreatorProperties.FIELD_WIDGET, "textarea");
        ObjectNode options = MAPPER.createObjectNode();
        comment.put(SchemaCreatorProperties.FIELD_OPTIONS, options.put("rows", 5));
        return comment;
    }

    public static ObjectNode createAttachmentSchemaForm(List<Attachment> attachments) {
        ArrayNode descriptionsNode = MAPPER.createArrayNode();
        ArrayNode filesNode = MAPPER.createArrayNode();
        ArrayNode coordinatesNode = MAPPER.createArrayNode();

        for (Attachment attachment : attachments) {
            switch (attachment.getType()) {
                case DESCRIPTION:
                    descriptionsNode.addPOJO(new DescriptionNode(attachment));
                    break;
                case LOCATION:
                    coordinatesNode.addPOJO(new LocationNode(attachment));
                    break;
                case PICTURE:
                    filesNode.addPOJO(new FileNode(attachment));
                    break;
                default:
                    break;
            }
        }

        return MAPPER.createObjectNode()
                .putPOJO(SchemaCreatorProperties.FIELD_DESCRIPTION, descriptionsNode)
                .putPOJO(SchemaCreatorProperties.FIELD_COORDINATES, coordinatesNode)
                .putPOJO(SchemaCreatorProperties.FIELD_FILE, filesNode);
    }

    public static ObjectNode createTrialRolesSchemaForm(List<AnswerTrialRole> trialRoles) {
        ArrayNode trialNode = MAPPER.createArrayNode();
        ArrayNode checkNode = MAPPER.createArrayNode();

        if (!CollectionUtils.isEmpty(trialRoles)) {
            List<TrialRole> lastTrialRoles = getLastTrialRoles(trialRoles.stream().findFirst().get().getTrialRole());

            for (TrialRole trial : lastTrialRoles) {
                trialNode.addPOJO(trial.getName());
            }

            for (AnswerTrialRole answerTrialRole : trialRoles) {
                checkNode.addPOJO(answerTrialRole.getTrialRole().getName());
            }
        }

        return MAPPER.createObjectNode()
                .putPOJO(SchemaCreatorProperties.FIELD_TRIAL, trialNode)
                .putPOJO(SchemaCreatorProperties.FIELD_TRIAL_CHECK, checkNode);
    }

    private static List<TrialRole> getLastTrialRoles(TrialRole trialRole) {
        if (CollectionUtils.isEmpty(trialRole.getTrialRolesParents())) {
            return trialRole.getTrialRoles();
        }

        return getLastTrialRoles(trialRole.getTrialRolesParents().stream().findFirst().get());
    }

    public static String getValueFromJSONObject(String jsonString, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Object keyObject = jsonObject.get(key);

            if (keyObject instanceof Integer) {
                return String.valueOf(keyObject);
            } else if (keyObject instanceof JSONArray) {
                return "[" + ((JSONArray) keyObject).toList().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + "]";
            }

            return jsonObject.getString(key);
        } catch (Exception e) {
            return "";
        }
    }

    public static ObjectNode createNewSessionSchemaForm(List<TrialStage> trialStages,
                                                        List<TrialRole> trialRoles,
                                                        List<AuthUser> authUsers) {

        ArrayNode trialStageNode = MAPPER.createArrayNode();
        ArrayNode usersNode = MAPPER.createArrayNode();
        ArrayNode trialRoleNode = MAPPER.createArrayNode();

        for (TrialStage trialStage : trialStages) {
            trialStageNode.addPOJO(trialStage.getName());
        }

        for (AuthUser authUser : authUsers) {
            ObjectNode userNode = MAPPER.createObjectNode()
                    .putPOJO(SchemaCreatorProperties.FIELD_ID, authUser.getId())
                    .putPOJO(SchemaCreatorProperties.FIELD_FIRST_NAME, authUser.getFirstName())
                    .putPOJO(SchemaCreatorProperties.FIELD_LAST_NAME, authUser.getLastName());

            usersNode.addPOJO(userNode);
        }

        for (TrialRole trialRole : trialRoles) {
            trialRoleNode.addPOJO(trialRole.getName());
        }

        return MAPPER.createObjectNode()
                .putPOJO(SchemaCreatorProperties.FIELD_TRIAL_STAGE, trialStageNode)
                .putPOJO(SchemaCreatorProperties.FIELD_USER, usersNode)
                .putPOJO(SchemaCreatorProperties.FIELD_TRIAL_ROLE, trialRoleNode);
    }
}
