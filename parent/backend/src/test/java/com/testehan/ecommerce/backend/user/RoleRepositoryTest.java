package com.testehan.ecommerce.backend.user;

import com.testehan.ecommerce.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest            // obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void whenPersistingRole_roleGetsPersisted(){
        Role admin = new Role();
        admin.setName("admin2");
        admin.setDescription("he is the one that can do anything in the app");

        Role savedRole = roleRepository.save(admin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void whenPersistingAllRoles_rolesGetPersisted(){
        Role roleAdmin = new Role("Admin2", "manage everything");
        Role roleSalesperson = new Role("Salesperson2", "manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor2", "manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("Shipper2", "view products, view orders and update order status");
        Role roleAssistant = new Role("Assistant2", "manage questions and reviews");

        roleRepository.saveAll(List.of(roleAdmin,roleSalesperson, roleEditor, roleShipper, roleAssistant));

        var roles = (List<Role>)roleRepository.findAll();
        assertThat(roles.contains(roleAdmin)).isTrue();
        assertThat(roles.contains(roleSalesperson)).isTrue();
        assertThat(roles.contains(roleEditor)).isTrue();
        assertThat(roles.contains(roleShipper)).isTrue();
        assertThat(roles.contains(roleAssistant)).isTrue();
    }
}
