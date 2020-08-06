package com.shuman.stonks.repository;

import com.shuman.stonks.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends CrudRepository<User, UUID> {
    // @Modifying if query is modifying
    @Query(value = "select * from users u where u.oauth_id = :id", nativeQuery = true)
    Optional<User> findByOauthId(@Param("id") String id);
}
