package jpahook.jpashop.api;



import jpahook.jpashop.domain.Address;
import jpahook.jpashop.domain.Order;

import jpahook.jpashop.domain.OrderStatus;
import jpahook.jpashop.repository.OrderRepository;
import jpahook.jpashop.repository.OrderSearch;
import jpahook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xToOne(ManyToOne , OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 * */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); //LaZY 강제 초기화
            order.getDelivery().getAddress(); // LaZY 강제 초기화
        }
        return all;

    }

    // api 스팩을 명확하게 규정해야한다. v2 문제점 : 이것도 레이지로딩으로 지연로딩 문제가 생긴다. 3개 엔티티를 조회해야하는 상황, 또 member랑 deliverly가 불러옴 총쿼리가 5개 나갔으.. ㅎㄷㄷ
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        //order 2개
        // N+1 -> 1+ 회원 N + 배송 (N) 문제 , EAGER 는 절대 하면안된다.. LAZY로 하고 패치조인해야함...!

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());


        return result;
    }

    // v3 버전 > 패치조인 jpa 강의에서 패치조인 100%
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    // v4와 v3는 우열을 가리기가 어렵다.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }


}
