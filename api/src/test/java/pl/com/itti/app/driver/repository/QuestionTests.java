package pl.com.itti.app.driver.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;
import pl.com.itti.app.driver.model.Question;
import pl.com.itti.app.driver.model.enums.AnswerType;

@RunWith(SpringRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@OverrideAutoConfiguration(enabled = false)
@org.springframework.transaction.annotation.Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@ImportAutoConfiguration
public class QuestionTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ObservationTypeRepository observationTypeRepository;

    @Test
    public void findOneById() {
        // when
        Question question = questionRepository.findOne(1L);

        // then
        Assert.assertEquals(1L, question.getId().longValue());
    }

    @Test
    public void findAll() {
        // when
        Page<Question> questionPage = questionRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(7L, questionPage.getTotalElements());
    }

    @Test
    public void create() {
        // given
        Question question = Question.builder()
                .answerType(AnswerType.CHECKBOX)
                .name("name")
                .description("description")
                .observationType(observationTypeRepository.findOne(1L))
                .build();

        // when
        Question savedQuestion = questionRepository.save(question);
        Page<Question> questionPage = questionRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(8L, questionPage.getTotalElements());
        Assert.assertEquals(question.getAnswerType(), savedQuestion.getAnswerType());
        Assert.assertEquals(question.getName(), savedQuestion.getName());
        Assert.assertEquals(question.getDescription(), savedQuestion.getDescription());
        Assert.assertEquals(question.getObservationType(), savedQuestion.getObservationType());
    }

    @Test
    public void update() {
        // given
        Question question = questionRepository.findOne(1L);
        question.setAnswerType(AnswerType.CHECKBOX);

        // when
        Question savedQuestion = questionRepository.save(question);

        // then
        Assert.assertEquals(AnswerType.CHECKBOX, savedQuestion.getAnswerType());
        Assert.assertEquals(question.getName(), savedQuestion.getName());
        Assert.assertEquals(question.getDescription(), savedQuestion.getDescription());
        Assert.assertEquals(question.getObservationType(), savedQuestion.getObservationType());
    }

    @Test
    public void delete() {
        // when
        questionRepository.delete(7L);
        Page<Question> questionPage = questionRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(6L, questionPage.getTotalElements());
    }
}
