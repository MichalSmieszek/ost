package pl.com.itti.app.driver.repository.specification;

import co.perpixel.security.model.AuthUser;
import org.springframework.data.jpa.domain.Specification;
import pl.com.itti.app.driver.model.*;
import pl.com.itti.app.driver.util.RepositoryUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;

public class AnswerSpecification {

    public static Specification<Answer> isConnectedToAuthUser(AuthUser authUser) {
        if (authUser == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Answer, TrialUser> trialUserJoin = RepositoryUtils.getOrCreateJoin(root, Answer_.trialUser, JoinType.LEFT);

            return cb.equal(trialUserJoin.get(TrialUser_.authUser), authUser);
        };
    }

    public static Specification<Answer> inTrialSession(Long trialSessionId) {
        if (trialSessionId == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Answer, TrialSession> trialSessionJoin = RepositoryUtils.getOrCreateJoin(root, Answer_.trialSession, JoinType.LEFT);

            return cb.equal(trialSessionJoin.get(TrialSession_.id), trialSessionId);
        };
    }

    public static Specification<Answer> isAnswerForObservationType(Long observationTypeId) {
        if (observationTypeId == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Answer, ObservationType> observationTypeJoin = RepositoryUtils.getOrCreateJoin(root, Answer_.observationType, JoinType.LEFT);

            return cb.equal(observationTypeJoin.get(ObservationType_.id), observationTypeId);
        };
    }

    public static Specification<Answer> inLastTrialStage(Long trialSessionId) {
        if (trialSessionId == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Answer, TrialSession> trialSessionJoin = RepositoryUtils.getOrCreateJoin(root, Answer_.trialSession, JoinType.LEFT);
            Path<TrialStage> lastTrialStage = trialSessionJoin.get(TrialSession_.lastTrialStage);

            Join<Answer, ObservationType> observationTypeJoin = RepositoryUtils.getOrCreateJoin(root, Answer_.observationType, JoinType.LEFT);
            Path<TrialStage> actualTrialStage = observationTypeJoin.get(ObservationType_.trialStage);

            return cb.or(cb.equal(lastTrialStage, actualTrialStage),
                    cb.isNull(actualTrialStage));
        };
    }
}
