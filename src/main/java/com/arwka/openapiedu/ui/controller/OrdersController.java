package com.arwka.openapiedu.ui.controller;

import com.arwka.openapiedu.service.OrdersService;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.OrdersApi;
import org.openapitools.model.EditedOrder;
import org.openapitools.model.NewOrder;
import org.openapitools.model.Order;
import org.openapitools.model.StatusEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class OrdersController implements OrdersApi {

  private final OrdersService ordersService;

  /**
   * Get orders.
   *
   * @param status - order status (not implemented)
   * @param from   - datetime FROM (period) (not implemented)
   * @param to     - datetime TO (period) (not implemented)
   * @return ResponseEntity with list of orders
   */
  @Override
  @GetMapping(value = "/orders", consumes = "application/json")
  public ResponseEntity<List<Order>> getOrders(StatusEnum status, OffsetDateTime from,
                                               OffsetDateTime to) {

    if (null == from && null == to && null == status) {
      return ResponseEntity
          .ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(ordersService.getOrdersWithoutParams());
    } else {
      return ResponseEntity
          .ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(ordersService.getOrders(status, from, to));
    }

  }

  @GetMapping(value = "/orders", consumes = "text/csv")
  public ResponseEntity<?> getOrdersInCsv(StatusEnum status, OffsetDateTime from,
                                          OffsetDateTime to) {

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv");
    headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

    return new ResponseEntity<>(
        ordersService.getOrdersInCsv(),
        headers,
        HttpStatus.OK
    );
  }

  /**
   * Create and save new order.
   *
   * @param newOrder - order-to-create
   * @return ResponseEntity with created and saved order
   */
  @Override
  @PostMapping(value = "/orders", consumes = "application/json")
  public ResponseEntity<Order> createNewOrder(NewOrder newOrder) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ordersService.createNewOrder(newOrder));
  }

  /**
   * Load orders from .csv
   *
   * @param inputStream - input stream of .csv file
   * @return ResponseEntity with status CREATED
   */
  @PostMapping(value = "/orders", consumes = "text/csv", produces = "application/json")
  public ResponseEntity<Void> loadOrdersFromCsv(InputStream inputStream) {
    ordersService.addOrdersFromCsv(inputStream);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .build();
  }

  /**
   * Patch existing order.
   *
   * @param id - id
   * @param editedOrder - editedOrder
   * @return ResponseEntity with Order with changes from editedOrder
   */
  @Override
  @PatchMapping("/orders/{id}")
  public ResponseEntity<Order> patchOrder(@PathVariable(name = "id") String id,
                                          EditedOrder editedOrder) {

    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(ordersService.patchOrder(id, editedOrder));
  }

  /**
   * Approve existing order.
   *
   * @param id - id
   * @return ResponseEntity (approved)
   */
  @Override
  @PutMapping("/orders/{id}/approved")
  public ResponseEntity<Order> approveOrder(@PathVariable(name = "id") String id) {
    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(ordersService.approveOrder(id));
  }

  /**
   * Deliver existing order.
   *
   * @param id - id
   * @return ResponseEntity (delivered)
   */
  @Override
  @PutMapping("/orders/{id}/delivered")
  public ResponseEntity<Order> deliverOrder(@PathVariable(name = "id") String id) {
    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(ordersService.deliverOrder(id));
  }

  /**
   * Delete existing order.
   *
   * @param id - id
   * @return - ResponseEntity with status NO_CONTENT
   */
  @Override
  @DeleteMapping("/orders/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable(name = "id") String id) {
    ordersService.deleteOrder(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
