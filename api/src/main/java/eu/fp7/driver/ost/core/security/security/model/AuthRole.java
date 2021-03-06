package eu.fp7.driver.ost.core.security.security.model;

import eu.fp7.driver.ost.core.persistence.db.model.DictionaryObject;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@GenericGenerator(
        name = "DefaultSeqGen",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "auth_role_seq")}
)
public class AuthRole extends DictionaryObject
        implements GrantedAuthority {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "auth_role_m2m_permissions",
            joinColumns = @JoinColumn(name = "auth_role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "auth_permission_id", referencedColumnName = "id"))
    private Set<AuthPermission> permissions;

    @Override
    public String getAuthority() {
        return getShortName();
    }

    public Set<AuthPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<AuthPermission> permissions) {
        this.permissions = permissions;
    }
}