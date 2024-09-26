package com.zedeck.smartoutletserver.repository;


import com.zedeck.smartoutletserver.model.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findFirstByUsername(String username);

    Optional<UserAccount> findFirstByUuid(String uuid);

    Optional<UserAccount> findFirstByRefreshToken(String token);

    Optional<UserAccount> findFirstByUuidAndUserType(String uuid, String userType);

    Page<UserAccount> findAllByUserType(String role, Pageable pageable);

    Page<UserAccount> findAllByDeletedFalse(Pageable pageable);

    Page<UserAccount> findAllByUserTypeNot(String role, Pageable pageable);

    Long   countAllByActiveIsTrue();


}
