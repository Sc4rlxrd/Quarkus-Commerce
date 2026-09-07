package com.scarlxrd.ecommerce.auth.config.security;

import com.scarlxrd.ecommerce.auth.entity.UserEntity;
import com.scarlxrd.ecommerce.auth.enums.UserRole;
import com.scarlxrd.ecommerce.auth.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Set;

@ApplicationScoped
public class AdminInitializer {

    private static final Logger log = Logger.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;

    @ConfigProperty(name = "app.admin.name")
    String adminName;

    @ConfigProperty(name = "app.admin.email")
    String adminEmail;

    @ConfigProperty(name = "app.admin.password")
    String adminPassword;

    public AdminInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    void onStart(@Observes StartupEvent event) {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Usuário ADMIN já existe, não será recriado.");
            return;
        }

        UserEntity admin = new UserEntity();

        admin.name = adminName;
        admin.email = adminEmail.trim().toLowerCase();
        admin.passwordHash = BcryptUtil.bcryptHash(adminPassword);

        admin.roles.addAll(Set.of(
                UserRole.USER,
                UserRole.CUSTOMER,
                UserRole.SELLER,
                UserRole.ADMIN
        ));

        userRepository.persist(admin);

        log.infof(
                "Usuário ADMIN criado com sucesso: %s",
                admin.email
        );
    }
}