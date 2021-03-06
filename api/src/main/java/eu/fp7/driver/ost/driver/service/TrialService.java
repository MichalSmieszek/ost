package eu.fp7.driver.ost.driver.service;

import eu.fp7.driver.ost.driver.dto.AdminTrialDTO;
import eu.fp7.driver.ost.driver.model.Trial;
import eu.fp7.driver.ost.driver.model.enums.Languages;
import eu.fp7.driver.ost.driver.repository.TrialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TrialService {

    @Autowired
    private TrialRepository trialRepository;

    public Iterable<Trial> getAllTrials() {
        return trialRepository.findAll();
    }


    public Trial getTrialById(Long trialId) {
        Trial trial = trialRepository.findById(trialId).get();
        return trial;
    }

    @Transactional
    public Trial delete(long id) {
        Trial trial = getTrialById(id);
        trial.setIsArchived(true);
        return trialRepository.save(trial);
    }

    @Transactional
    public Trial update(AdminTrialDTO.ListItem adminTrialDTO) {

        Trial trial = getTrialById(adminTrialDTO.getTrialId());
        trial.setName(adminTrialDTO.getTrialName());
        trial.setDescription(adminTrialDTO.getTrialDescription());
        return   trialRepository.save(trial);
    }
    @Transactional
    public Trial insert(AdminTrialDTO.ListItem adminTrialDTO) {

        Trial trial = Trial.builder()
                .description(adminTrialDTO.getTrialDescription())
                .languageVersion(Languages.ENGLISH)
                .name(adminTrialDTO.getTrialName())
                .isDefined(true)
                .isArchived(false)
                .build();
        return  trialRepository.save(trial);
    }




}
