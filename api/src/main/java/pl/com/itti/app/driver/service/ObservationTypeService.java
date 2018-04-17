package pl.com.itti.app.driver.service;

import co.perpixel.dto.DTO;
import co.perpixel.exception.EntityNotFoundException;
import co.perpixel.security.model.AuthUser;
import co.perpixel.security.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.itti.app.driver.model.ObservationType;
import pl.com.itti.app.driver.model.TrialSession;
import pl.com.itti.app.driver.model.TrialUser;
import pl.com.itti.app.driver.repository.*;
import pl.com.itti.app.driver.util.RepositoryUtils;
import pl.com.itti.app.driver.web.dto.ObservationTypeDTO;
import pl.com.itti.app.driver.web.dto.TrialUserDTO;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ObservationTypeService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private ObservationTypeRepository observationTypeRepository;

    @Autowired
    private TrialSessionRepository trialSessionRepository;

    @Autowired
    private TrialRoleRepository trialRoleRepository;

    @Autowired
    private TrialUserRepository trialUserRepository;

    @Transactional(readOnly = true)
    public Page<ObservationType> find(Long trialSessionId, Pageable pageable) {
        AuthUser authUser = authUserRepository.findOneCurrentlyAuthenticated()
                .orElseThrow(() -> new IllegalArgumentException("Session for current user is closed"));

        TrialSession trialSession = Optional.ofNullable(trialSessionRepository.findOne(trialSessionId))
                .orElseThrow(() -> new EntityNotFoundException(TrialSession.class, trialSessionId));

        return observationTypeRepository.findAll(
                getObservationTypeSpecifications(
                        authUser,
                        trialSession
                ), pageable
        );
    }

    public ObservationTypeDTO.SchemaItem generateSchema(Long observationTypeId, Long trialSessionId) {
        AuthUser authUser = authUserRepository.findOneCurrentlyAuthenticated()
                .orElseThrow(() -> new IllegalArgumentException("Session for current user is closed"));

        TrialSession trialSession = Optional.ofNullable(trialSessionRepository.findOne(trialSessionId))
                .orElseThrow(() -> new EntityNotFoundException(TrialSession.class, trialSessionId));

        ObservationType observationType = Optional.ofNullable(observationTypeRepository.findOne(observationTypeId))
                .orElseThrow(() -> new EntityNotFoundException(TrialSession.class, observationTypeId));

        ObservationTypeDTO.SchemaItem schemaItem = DTO.from(observationType, ObservationTypeDTO.SchemaItem.class);

        schemaItem.users = DTO.from(
                trialUserRepository.findAll(
                        getTrialUserSpecifications(authUser, trialSession, observationType)
                ),
                TrialUserDTO.ListItem.class
        );

        return schemaItem;
    }

    private Specifications<ObservationType> getObservationTypeSpecifications(AuthUser authUser,
                                                                             TrialSession trialSession) {
        Set<Specification<ObservationType>> conditions = new HashSet<>();
        conditions.add(ObservationTypeSpecification.user(authUser));
        conditions.add(ObservationTypeSpecification.trialSession(trialSession));
        return RepositoryUtils.concatenate(conditions);
    }

    private Specifications<TrialUser> getTrialUserSpecifications(AuthUser authUser,
                                                                 TrialSession trialSession,
                                                                 ObservationType observationType) {
        Set<Specification<TrialUser>> conditions = new HashSet<>();
        conditions.add(TrialUserSpecification.findByObserver(authUser, trialSession, observationType));
        return RepositoryUtils.concatenate(conditions);
    }
}
