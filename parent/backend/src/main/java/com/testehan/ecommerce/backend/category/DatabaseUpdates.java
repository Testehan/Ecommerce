package com.testehan.ecommerce.backend.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DatabaseUpdates{

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUpdates.class);

    @PersistenceContext
    private EntityManager entityManager;

    // TODO For some reason, when the parent_id column is created for Category, it is made UNIQUE. I do not understand why,
    // as the default for UNIQUE is false if you look inside JoinColumn. Tried to find a solution to drop that
    // constraint but it is complicated because I need to find out the name of the constraint, which is itself
    // complicated "select * from information_schema.table_constraints where table_name='category';" because
    // for example this query does not return the column for which the constraint is applied.
    // So whenever this table for Categories get created, i need to drop constraint
    // uk_81thrbnb8c08gua7tvqj7xdqk; This unique constraint is not created on other DBs from what I have seen.
    // When app is deployed on another machine, one will need to see if
    @Transactional
    public void alterTableCategoryDropUnwantedUniqueConstraint() {

        String findConstraintNames = """
                 SELECT conname FROM pg_constraint WHERE conrelid = 
                    (SELECT oid FROM pg_class WHERE relname LIKE 'category')
                 """;

        List<String> constraintNames = entityManager.createNativeQuery(findConstraintNames).getResultList();

        if (constraintNames.contains("uk_81thrbnb8c08gua7tvqj7xdqk")) {
            String query = "ALTER TABLE category DROP CONSTRAINT uk_81thrbnb8c08gua7tvqj7xdqk";
            entityManager.createNativeQuery(query).executeUpdate();
            LOGGER.info("The unique constraint uk_81thrbnb8c08gua7tvqj7xdqk for table category column parent_id was DELETED.");
        } else {
            LOGGER.error("The unique constraint for table category column parent_id was not found. You must drop this constraint manually if it exists!");
            LOGGER.error("Existing constraints for table category are :");
            constraintNames.forEach(LOGGER::error);
        }
    }
}
