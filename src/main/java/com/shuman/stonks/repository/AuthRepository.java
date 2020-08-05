package com.shuman.stonks.repository;

import com.shuman.stonks.model.Auth;
import com.shuman.stonks.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthRepository extends CrudRepository<Auth, UUID> {
//    @Query("select e from Employees e where e.salary > :salary")
//    List<Auth> findEmployeesWithMoreThanSalary(@Param("salary") Long salary);
}
