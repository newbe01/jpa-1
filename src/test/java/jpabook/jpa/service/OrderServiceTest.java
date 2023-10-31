package jpabook.jpa.service;

import jpabook.jpa.domain.Address;
import jpabook.jpa.domain.Member;
import jpabook.jpa.domain.Order;
import jpabook.jpa.domain.OrderStatus;
import jpabook.jpa.domain.item.Book;
import jpabook.jpa.domain.item.Item;
import jpabook.jpa.exception.NotEnoughStockException;
import jpabook.jpa.repository.OrderRepository;
import jpabook.jpa.repository.OrderSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;


    @Test
    void itemOrder() {
        //  given
        Member member = createMember("name");

        Item book = createBook("name", 10000, 10);

        //  when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //  then
        Order result = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).isEqualTo(result.getStatus());
        assertThat(1).isEqualTo(result.getOrderItems().size());
        assertThat(10000 * orderCount).isEqualTo(result.getTotalCount());
        assertThat(8).isEqualTo(book.getStockQuantity());
    }


    @Test
    void order_OverStockQuantity() {
        //  given
        Member member = createMember("name");
        Item item = createBook("name", 10000, 10);
        int orderCount = 11;

        //  when
//        orderService.order(member.getId(), item.getId(), orderCount);

        //  then
        assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class)
                .hasMessage("need more stock");
    }

    @Test
    void orderCancel() {
        //  given
        Member member = createMember("name");
        Item item = createBook("name", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //  when
        orderService.cancelOrder(orderId);

        //  then
        Order result = orderRepository.findOne(orderId);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(10).isEqualTo(item.getStockQuantity());
    }

    @Test
    void orderSearchTest() {
        //  given
        Member member = createMember("name");
        Item item = createBook("name", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("name");


        //  when
        List<Order> result = orderService.findOrders(orderSearch);

        //  then
        System.out.println("-==============================-");
        System.out.println("result = " + result.get(0).toString());
        System.out.println("result = " + result.get(0).getStatus());
        System.out.println("result = " + result.get(0).getMember().getName());
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(result.get(0).getMember().getName()).isEqualTo("name");


    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("city", "street", "123"));
        em.persist(member);
        return member;
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}
