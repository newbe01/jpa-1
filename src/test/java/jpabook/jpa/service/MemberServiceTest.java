package jpabook.jpa.service;

import jpabook.jpa.domain.Member;
import jpabook.jpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    void join() {
        //  given
        Member member = new Member();
        member.setName("kim");

        //  when
        Long savedId = memberService.join(member);

        //  then
        assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }

    @Test
    void joinDupleException() {
        //  given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //  when
        memberService.join(member1);

        //  then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }

}
