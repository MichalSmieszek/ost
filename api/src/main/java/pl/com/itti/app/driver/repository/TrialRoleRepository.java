package pl.com.itti.app.driver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.com.itti.app.driver.model.TrialRole;

import java.util.List;
import java.util.Optional;

public interface TrialRoleRepository
        extends PagingAndSortingRepository<TrialRole, Long>, JpaSpecificationExecutor<TrialRole> {

    Optional<TrialRole> findById(Long id);
    Optional<TrialRole> findFirstByName(String name);

    List<TrialRole> findAllByTrialId(Long trialId);
    Page<TrialRole> findAllByTrialId(Long trialId, Pageable pageable);
}
