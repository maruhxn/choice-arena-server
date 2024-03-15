package com.maruhxn.choicearena.domain.member.dao;

import com.maruhxn.choicearena.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
