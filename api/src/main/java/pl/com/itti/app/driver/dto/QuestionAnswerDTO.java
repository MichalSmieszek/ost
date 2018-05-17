package pl.com.itti.app.driver.dto;

import co.perpixel.dto.EntityDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.com.itti.app.driver.model.Answer;
import pl.com.itti.app.driver.model.ObservationType;
import pl.com.itti.app.driver.util.InternalServerException;
import pl.com.itti.app.driver.util.schema.SchemaCreator;

import java.io.IOException;

public final class QuestionAnswerDTO {

    public static class MinimalItem implements EntityDTO<Answer> {

        public long answerId;
        public String name;
        public String description;

        @Override
        public void toDto(Answer answer) {
            this.answerId = answer.getId();

            ObservationType observationType = answer.getObservationType();
            this.name = observationType.getName();
            this.description = observationType.getDescription();
        }
    }

    public static class FullItem extends MinimalItem {

        public JsonNode questionSchema;
        public JsonNode formData;
        public JsonNode attachments;

        @Override
        public void toDto(Answer answer) {
            super.toDto(answer);

            try {
                this.questionSchema = SchemaCreator.createSchemaForm(answer.getObservationType().getQuestions(), true);
                this.formData = new ObjectMapper().readTree(answer.getFormData());
                this.attachments = SchemaCreator.createAttachmentSchemaForm(answer.getAttachments());
            } catch (IOException ioe) {
                throw new InternalServerException("Error in jsonSchema", ioe);
            }
        }
    }

    private QuestionAnswerDTO() {
        throw new AssertionError();
    }
}
