package mate.academy.jvbookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import mate.academy.jvbookstore.initialization.AppStartupRunner;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private RoleName name;

    public Role() {
    }

    public Role(RoleName name) {
        this.name = name;
    }
    
    /**
     * List of available roles. Roles are injected into the database 
     * at the startup in {@link AppStartupRunner}.
     */
    public static enum RoleName {
        USER,
        ADMIN
    } 
}
