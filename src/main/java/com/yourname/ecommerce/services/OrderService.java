package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.dao.OrderDAO;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO;
    
    public OrderService() {
        this.orderDAO = new OrderDAO();
    }
    
    public List<Order> getAllOrders() {
        System.out.println("OrderService: Retrieving all orders"); // Debug log
        List<Order> orders = orderDAO.findAll();
        System.out.println("OrderService: Found " + orders.size() + " orders for user");
        return orders;
    }
    
    public List<Order> getOrdersByUser(User user) {
        System.out.println("OrderService: Retrieving orders for user: " + user.getUsername()); // Debug log
        List<Order> orders = orderDAO.findByUser(user);
        System.out.println("OrderService: Found " + orders.size() + " orders for user"); // Debug log
        return orders;
    }
    
    public Order getOrderById(int id) {
        System.out.println("OrderService: Retrieving order with ID: " + id); // Debug log
        Order order = orderDAO.findById(id);
        System.out.println("OrderService: Order " + (order != null ? "found" : "not found")); // Debug log
        return order;
    }
    
    public Order createOrder(Order order) {
        System.out.println("OrderService: Creating new order"); // Debug log
        return orderDAO.create(order);
    }
    
    public Order updateOrder(Order order) {
        System.out.println("OrderService: Updating order " + order.getId()); // Debug log
        return orderDAO.update(order);
    }
    
    public boolean deleteOrder(int id) {
        System.out.println("OrderService: Deleting order " + id); // Debug log
        return orderDAO.delete(id);
    }
} 