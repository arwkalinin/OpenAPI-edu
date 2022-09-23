package com.arwka.openapiedu.persistent.repository.impl;

import com.arwka.openapiedu.persistent.repository.OrdersRepository;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.ToString;
import org.openapitools.model.Order;
import org.springframework.stereotype.Repository;

@Repository
@ToString
@Getter
public class OrdersRepositoryImpl implements OrdersRepository {

  private final HashMap<Long, Order> orders = new HashMap<>();

  /**
   * Initialize mock-repository with mock-values.
   */
  @PostConstruct
  public void init() {
    Order order1 = new Order();
    Order order2 = new Order();
    Order order3 = new Order();

    order1.setId(1L);
    order2.setId(2L);
    order3.setId(3L);

    orders.put(order1.getId(), order1);
    orders.put(order2.getId(), order2);
    orders.put(order3.getId(), order3);
  }

  /**
   * Adding order in mock DB.
   *
   * @param order - order to save
   * @return Order (saved)
   */
  @Override
  public Order addOrder(Order order) {
    return orders.put(order.getId(), order);
  }

}
