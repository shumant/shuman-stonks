package com.shuman.stonks.repository;

import com.shuman.stonks.model.Auth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends CrudRepository<Auth, UUID> {
    @Query(value = "select * from auth a where a.user_id = :id", nativeQuery = true)
    Optional<Auth> findByUserId(@Param("id") UUID id);
}
