package com.account.repository;

import com.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByClientId (Long clientId);

    Account findByNumber(String number);

    Account findByCardNumber(String number);
}
