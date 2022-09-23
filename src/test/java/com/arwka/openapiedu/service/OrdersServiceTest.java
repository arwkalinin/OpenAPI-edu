package com.arwka.openapiedu.service;

import com.arwka.openapiedu.persistent.repository.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrdersServiceTest {

  @Autowired
  OrdersRepository ordersRepository;

  @Autowired
  OrdersService ordersService;

  @Test
  @DisplayName("")
  void test() {
    // no tests here, look in ui.controller ;)
  }

}
