package com.OAuthPractice.repository;

import com.OAuthPractice.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>
{
    public Optional<Member> findByEmail(String email);
}
