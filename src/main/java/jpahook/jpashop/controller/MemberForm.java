package jpahook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
// validation 에러메세지
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {


    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;


}
