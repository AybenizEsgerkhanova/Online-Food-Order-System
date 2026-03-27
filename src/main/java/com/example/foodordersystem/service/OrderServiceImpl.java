package com.example.foodordersystem.service;

import ch.qos.logback.classic.Logger;
import com.example.foodordersystem.exception.InvalidOrderStatusException;
import com.example.foodordersystem.exception.MenuItemNotFoundException;
import com.example.foodordersystem.exception.OrderNotFoundException;
import com.example.foodordersystem.exception.UserNotFoundException;
import com.example.foodordersystem.mapper.OrderMapper;
import com.example.foodordersystem.model.dto.request.OrderRequest;
import com.example.foodordersystem.model.dto.response.OrderResponse;
import com.example.foodordersystem.model.entity.MenuItem;
import com.example.foodordersystem.model.entity.Order;
import com.example.foodordersystem.model.entity.OrderItem;
import com.example.foodordersystem.model.entity.User;
import com.example.foodordersystem.repository.MenuItemRepository;
import com.example.foodordersystem.repository.OrderRepository;
import com.example.foodordersystem.repository.UserRepository;
import com.example.foodordersystem.util.MessageUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            MenuItemRepository menuItemRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, String username, String idempotencyKey) {

        if (idempotencyKey != null) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return orderMapper.toResponse(existing.get());
            }
        }

        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new RuntimeException(MessageUtil.get("error.order.empty"));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(MessageUtil.get("error.user.not.found")));

        Order order = new Order(user);

        for (var itemRequest : orderRequest.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new MenuItemNotFoundException(MessageUtil.get("error.menu.item.not.found.id", itemRequest.getMenuItemId())));

            if (!menuItem.isAvailable()) {
                throw new RuntimeException(MessageUtil.get("error.menu.item.not.available", menuItem.getName()));
            }

            if (itemRequest.getQuantity() <= 0) {
                throw new RuntimeException(MessageUtil.get("error.order.quantity.invalid", menuItem.getName()));
            }

            OrderItem orderItem = new OrderItem(menuItem, itemRequest.getQuantity());
            order.addOrderItem(orderItem);
        }

        order.calculateTotalAmount();
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id, String username) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(MessageUtil.get("error.order.not.found")));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(MessageUtil.get("error.user.not.found")));

        if (!order.getUser().getId().equals(user.getId()) && user.getRole() == User.Role.CUSTOMER) {
            throw new RuntimeException(MessageUtil.get("error.access.denied"));
        }

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(MessageUtil.get("error.user.not.found")));
        if (user.getRole() != User.Role.CUSTOMER) {
            return orderMapper.toResponseList(orderRepository.findAllOrderByOrderDateDesc());
        }
        return orderMapper.toResponseList(orderRepository.findByUserId(user.getId()));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(MessageUtil.get("error.user.not.found")));

        if (user.getRole() == User.Role.CUSTOMER) {
            throw new RuntimeException(MessageUtil.get("error.access.denied"));
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(MessageUtil.get("error.order.not.found")));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            if (!order.getStatus().canTransitionTo(newStatus)) {
                throw new InvalidOrderStatusException(order.getStatus() + " → " + newStatus + " keçidi mümkün deyil.");
            }
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(MessageUtil.get("error.order.status.invalid", status));
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
}
