package jpahook.jpashop.service;

import jpahook.jpashop.domain.Member;
import jpahook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

// junit 시작할때 스프링이랑 같이 실행할래!
@RunWith(SpringRunner.class)
@SpringBootTest
// 이게 있어야 롤백이 된다.
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    // 두가지 테스트
    //회원가입
    @Test
    // 롤백 막아줌
//    @Rollback(false)
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setName("Kim");

        //when > 조인을 하면 persist 하지만 기본적으로 db에 insert되지는 않음ㅁ.
        Long saveId = memberService.join(member);

        //then
        // fulsh 하면 디비에 쿼리를 insert 날린다.  하지만 transctional 때문에 롤백됨.
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));

    }
    //중복 회원 예외
    //IllegalArgumentException IllegalStateException 차이점 찾아보기
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        
        //when
        memberService.join(member1);
        memberService.join(member2);//예외가 발생해야한다.


        
        //then
        // 코드가 돌다가 여기로 오면 안됨. 그래서 fail 뜸
        fail("예외가 발생해야 한다.");
    }

}