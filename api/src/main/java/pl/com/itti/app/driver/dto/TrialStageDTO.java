package pl.com.itti.app.driver.dto;

import pl.com.itti.app.driver.model.TrialStage;
import pl.com.itti.app.core.dto.EntityDto;

import java.time.LocalDateTime;

public final class TrialStageDTO {

    public static class MinimalItem implements EntityDto<TrialStage> {

        public long id;

        @Override
        public void toDto(TrialStage trialStage) {
            this.id = trialStage.getId();
        }
    }

    public static class ListItem extends MinimalItem {

        public long trialId;
        public String name;
        public LocalDateTime simulationTime;

        @Override
        public void toDto(TrialStage trialStage) {
            super.toDto(trialStage);
            this.trialId = trialStage.getTrial().getId();
            this.name = trialStage.getName();
            this.simulationTime = trialStage.getSimulationTime();
        }
    }

    private TrialStageDTO() {
        throw new AssertionError();
    }
}
