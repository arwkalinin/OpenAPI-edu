package com.arwka.openapiedu.persistent.repository;

import java.util.HashMap;
import org.openapitools.model.Order;

public interface OrdersRepository {
  HashMap<Long, Order> getOrders();
  Order addOrder(Order order);
}
