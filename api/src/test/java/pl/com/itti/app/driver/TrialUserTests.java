package pl.com.itti.app.driver;

import co.perpixel.security.repository.AuthUserRepository;
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
import pl.com.itti.app.driver.model.TrialUser;
import pl.com.itti.app.driver.model.enums.Languages;
import pl.com.itti.app.driver.repository.TrialSessionManagerRepository;
import pl.com.itti.app.driver.repository.TrialUserRepository;
import pl.com.itti.app.driver.repository.UserRoleSessionRepository;

@RunWith(SpringRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@OverrideAutoConfiguration(enabled = false)
@org.springframework.transaction.annotation.Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@ImportAutoConfiguration
public class TrialUserTests {

    @Autowired
    private TrialUserRepository trialUserRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private TrialSessionManagerRepository trialSessionManagerRepository;

    @Autowired
    private UserRoleSessionRepository userRoleSessionRepository;

    @Test
    public void findOneById() {
        // when
        TrialUser trialUser = trialUserRepository.findOne(1L);

        // then
        Assert.assertEquals(1L, trialUser.getId().longValue());
    }

    @Test
    public void findAll() {
        // when
        Page<TrialUser> trialUserPage = trialUserRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(6L, trialUserPage.getTotalElements());
    }

    @Test
    public void create() {
        // when
        TrialUser trialUser = TrialUser.builder()
                .authUser(authUserRepository.findOne(1L))
                .userLanguage(Languages.POLISH)
                .isTrialCreator(Boolean.FALSE)
                .build();
        trialUserRepository.save(trialUser);
        Page<TrialUser> trialUserPage = trialUserRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(7L, trialUserPage.getTotalElements());
    }

    @Test
    public void delete() {
        // when
        trialSessionManagerRepository.deleteAll();
        userRoleSessionRepository.deleteAll();
        trialUserRepository.delete(2L);
        Page<TrialUser> trialUserPage = trialUserRepository.findAll(new PageRequest(1,10));

        // then
        Assert.assertEquals(5L, trialUserPage.getTotalElements());
    }

    @Test
    public void update() {
        // when
        TrialUser trialUser = trialUserRepository.findOne(2L);
        trialUser.setUserLanguage(Languages.POLISH);
        trialUserRepository.save(trialUser);

        // then
        Assert.assertEquals(Languages.POLISH, trialUserRepository.findOne(2L).getUserLanguage());

    }
}
