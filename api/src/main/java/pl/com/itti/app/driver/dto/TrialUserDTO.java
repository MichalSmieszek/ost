package pl.com.itti.app.driver.dto;

import pl.com.itti.app.driver.model.TrialUser;
import pl.com.itti.app.core.dto.EntityDto;

public final class TrialUserDTO {

    public static class ListItem implements EntityDto<TrialUser> {

        public long id;
        public String firstName;
        public String lastName;

        @Override
        public void toDto(TrialUser trialUser) {
            this.id = trialUser.getId();
            this.firstName = trialUser.getAuthUser().getFirstName();
            this.lastName = trialUser.getAuthUser().getLastName();
        }
    }

    public static class AdminEditItem extends ListItem {

        public String email;
        public String login;

        @Override
        public void toDto(TrialUser trialUser) {
            super.toDto(trialUser);
            this.email = trialUser.getAuthUser().getEmail();
            this.login = trialUser.getAuthUser().getLogin();
        }
    }

    private TrialUserDTO() {
        throw new AssertionError();
    }
}
