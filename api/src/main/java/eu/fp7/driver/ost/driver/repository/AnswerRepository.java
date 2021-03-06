package eu.fp7.driver.ost.driver.repository;

import eu.fp7.driver.ost.driver.model.Answer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository
        extends PagingAndSortingRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {
    Optional<Answer> findById(Long answerId);

    List<Answer> findAllByTrialSessionId(Long trialSessionId);

    List<Answer> findAllByTrialSessionIdAndObservationTypeId(Long trialSessionId, Long observationTypeId);
}
