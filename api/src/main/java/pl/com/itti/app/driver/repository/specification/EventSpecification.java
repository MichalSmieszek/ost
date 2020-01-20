package pl.com.itti.app.driver.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.com.itti.app.core.security.security.model.AuthUser;
import pl.com.itti.app.driver.model.Event;
import pl.com.itti.app.driver.model.Event_;
import pl.com.itti.app.driver.model.TrialRole;
import pl.com.itti.app.driver.model.TrialRole_;
import pl.com.itti.app.driver.model.TrialSession;
import pl.com.itti.app.driver.model.TrialSession_;
import pl.com.itti.app.driver.model.TrialUser;
import pl.com.itti.app.driver.model.TrialUser_;
import pl.com.itti.app.driver.model.UserRoleSession;
import pl.com.itti.app.driver.model.UserRoleSession_;
import pl.com.itti.app.driver.util.RepositoryUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class EventSpecification {

    public static Specification<Event> isConnectedToAuthUser(AuthUser authUser) {
        if (authUser == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Event, TrialUser> trial = RepositoryUtils.getOrCreateJoin(root, Event_.trialUser, JoinType.LEFT);

            Join<Event, TrialRole> trialRoleJoin = RepositoryUtils.getOrCreateJoin(root, Event_.trialRole, JoinType.LEFT);
            Join<TrialRole, UserRoleSession> userRoleSessionJoin = trialRoleJoin.join(TrialRole_.userRoleSessions, JoinType.LEFT);
            Join<UserRoleSession, TrialUser> trialUserByRoleJoin = userRoleSessionJoin.join(UserRoleSession_.trialUser, JoinType.LEFT);


            return cb.or(
                    cb.and(
                            cb.isNull(root.get(Event_.trialUser)),
                            cb.isNull(root.get(Event_.trialRole))
                    ),
                    cb.equal(trial.get(TrialUser_.authUser), authUser),
                    cb.equal(trialUserByRoleJoin.get(TrialUser_.authUser), authUser)
            );
        };
    }

    public static Specification<Event> inTrialSession(Long trialSessionId) {
        if (trialSessionId == null) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Event, TrialSession> trialSessionJoin = RepositoryUtils.getOrCreateJoin(root, Event_.trialSession, JoinType.LEFT);

            return cb.equal(trialSessionJoin.get(TrialSession_.id), trialSessionId);
        };
    }
}
