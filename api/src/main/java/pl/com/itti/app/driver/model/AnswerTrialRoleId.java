package pl.com.itti.app.driver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class AnswerTrialRoleId implements Serializable {
    private long answerId;
    private long trialRoleId;
}
