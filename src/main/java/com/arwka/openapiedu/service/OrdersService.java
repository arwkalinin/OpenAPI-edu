package com.arwka.openapiedu.service;

import com.arwka.openapiedu.persistent.repository.impl.OrdersRepositoryImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.openapitools.model.EditedOrder;
import org.openapitools.model.NewOrder;
import org.openapitools.model.Order;
import org.openapitools.model.StatusEnum;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersService {

  private final OrdersRepositoryImpl ordersRepository;

  /**
   * Get all orders.
   *
   * @param status - not implemented
   * @param from   - not implemented
   * @param to     - not implemented
   * @return List of all orders in DB.
   */
  public List<Order> getOrders(StatusEnum status, OffsetDateTime from, OffsetDateTime to) {
    return ordersRepository.getOrders().values().stream().toList();
    // without realization of filtering.
  }

  public List<Order> getOrdersWithoutParams() {
    return ordersRepository.getOrders().values().stream().toList();
  }

  /**
   * Create and save new order in DB.
   *
   * @param newOrder - new order
   * @return Order (saved)
   */
  public Order createNewOrder(NewOrder newOrder) {
    Order order = new Order();

    order.setId(ordersRepository.getOrders().size() + 1L);
    order.setProductId(newOrder.getProductId());
    order.setQuantity(newOrder.getQuantity());

    return ordersRepository.addOrder(order);
  }

  /**
   * Patch existing order.
   *
   * @param id          - order id to patch
   * @param editedOrder - edited orded object
   * @return Order with changes from editedOrder
   */
  public Order patchOrder(String id, EditedOrder editedOrder) {

    if (ordersRepository.getOrders().containsKey(Long.parseLong(id))) {

      long idOfOrder = Long.parseLong(id);
      Order order = ordersRepository.getOrders().get(idOfOrder);

      order.setStatus(editedOrder.getStatus());
      order.setComplete(editedOrder.getComplete());
      order.setQuantity(editedOrder.getQuantity());

      return ordersRepository.addOrder(order);

    } else {
      throw new NoSuchElementException("Order not found.");
    }
  }

  /**
   * Change status of order to approve.
   *
   * @param id - order id
   * @return Order with status approve
   */
  public Order approveOrder(String id) {
    long idOfOrder = Long.parseLong(id);
    ordersRepository
        .getOrders()
        .get(idOfOrder)
        .setStatus(StatusEnum.APPROVED);
    return ordersRepository.getOrders().get(idOfOrder);
  }

  /**
   * Change status of order to delivered.
   *
   * @param id - order id
   * @return Order with status delivered
   */
  public Order deliverOrder(String id) {
    long idOfOrder = Long.parseLong(id);
    ordersRepository
        .getOrders()
        .get(idOfOrder)
        .setStatus(StatusEnum.DELIVERED);
    return ordersRepository.getOrders().get(idOfOrder);
  }

  /**
   * Delete order from DB.
   *
   * @param id - order id
   */
  public void deleteOrder(String id) {
    ordersRepository
        .getOrders()
        .remove(Long.parseLong(id));
  }

  /**
   * Get orders from CSV.
   *
   * @return Resource
   */
  public Resource getOrdersInCsv() {

    String[] csvHeader = {"id", "productId", "quantity", "date", "status", "complete"};

    List<Order> listOfOrders = ordersRepository.getOrders().values().stream().toList();
    List<List<String>> csvBody = new ArrayList<>();

    for (Order currentOrder : listOfOrders) {
      csvBody.add(Arrays.asList(
          Objects.toString(currentOrder.getId(), "null"),
          Objects.toString(currentOrder.getProductId(), "null"),
          Objects.toString(currentOrder.getQuantity(), "null"),
          Objects.toString(currentOrder.getDate(), "null"),
          Objects.toString(currentOrder.getStatus(), "null"),
          Objects.toString(currentOrder.getComplete(), "null")
      ));
    }

    ByteArrayInputStream byteArrayOutputStream;

    try (
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(
            new PrintWriter(out),
            CSVFormat.DEFAULT.withHeader(csvHeader)
        )
    ) {
      for (List<String> record : csvBody) {
        csvPrinter.printRecord(record);
      }
      csvPrinter.flush();
      byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    return new InputStreamResource(byteArrayOutputStream);
  }

  /**
   * Add to DB list of orders from inputStream.
   *
   * @param inputStream - input stream
   */
  public void addOrdersFromCsv(InputStream inputStream) {

    List<Order> orderList = new ArrayList<>();

    try {
      Reader reader = new InputStreamReader(inputStream);
      Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

      log.info("Getting started to read records from .csv ...");
      for (CSVRecord record : records) {
        if (record.stream().toList().size() > 2) {
          Order currentOrder = new Order();

          currentOrder.setId(Long.parseLong(record.stream().toList().get(0)));
          currentOrder.setProductId(Long.parseLong(record.stream().toList().get(1)));
          currentOrder.setQuantity(Long.parseLong(record.stream().toList().get(2)));
          currentOrder.setDate(OffsetDateTime.parse(record.stream().toList().get(3)));
          currentOrder.setStatus(StatusEnum.valueOf(record.stream().toList().get(4)));
          currentOrder.setComplete(Boolean.getBoolean(record.stream().toList().get(5)));

          orderList.add(currentOrder);
          log.info("Order added to DB: " + currentOrder);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}