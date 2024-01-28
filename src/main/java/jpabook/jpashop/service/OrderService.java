package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); //CASCADE ALL 덕분에 OrderItem Delivery도 같이 persist
                                    //주의점: 여기서는 order가 만들어질때만 OrderItem과 delivery가 생성되고 order에서만 사용되기때문에
                                    //이렇게 CASCADE를 사용함. 다른 엔티티에서도 참조되는 중요한 엔티티면 쓰면 안됨
                                    //다른엔티티에서 의도하지 않게 지우고 이럴수가 있음
                                    //그래서 그런 경우에는 CASCADE 쓰지말고 persist 따로 하는게 나음

        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티를 조회
        Order order = orderRepository.findOne(orderId);
        //주문을 취소
        order.cancel(); //만약 여기서 로직이 바뀌면 관련 SQL을 다 바꿔야 하는데 JPA 쓰면 그렇게 안해도 된다는 장점
                        //이런 스타일로 엔티티에 비즈니스 로직을 가지고 있는것을 도메인 모델 패턴이라 함!!!!
    }

    //검색
    /*public List<Order> findOrders(OrderSearch orderSearch) {

    }*/
}
