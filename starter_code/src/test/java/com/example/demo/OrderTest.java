package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderTest {

	@InjectMocks
	private OrderController orderController;

	@Mock
	private final UserRepository userRepo = mock(UserRepository.class);

	@Mock
	private final OrderRepository orderRepo = mock(OrderRepository.class);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void submitTest() {
		User user = createUser();
		Item item = createItem();

		Cart cart = user.getCart();

		List<Item> itemList = new ArrayList<>();
		itemList.add(item);
		cart.setItems(itemList);

		cart.setUser(user);
		user.setCart(cart);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(user);

		ResponseEntity<UserOrder> response = orderController.submit(Constant.USER_NAME);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		UserOrder retrievedUserOrder = response.getBody();
		assertNotNull(retrievedUserOrder);
		assertNotNull(retrievedUserOrder.getItems());
		assertNotNull(retrievedUserOrder.getTotal());
		assertNotNull(retrievedUserOrder.getUser());
	}

	@Test
	public void testSubmitNullUser() {
		User user = createUser();
		Item item = createItem();
		Cart cart = user.getCart();

		cart.setUser(user);
		user.setCart(cart);

		List<Item> itemList = new ArrayList<>();
		itemList.add(item);
		cart.setItems(itemList);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(null);
		ResponseEntity<UserOrder> response = orderController.submit(Constant.USER_NAME);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void ordersByUserTest() {
		User user = createUser();
		Item item = createItem();
		Cart cart = user.getCart();

		List<Item> itemList = new ArrayList<>();
		itemList.add(item);
		cart.setItems(itemList);

		cart.setUser(user);
		user.setCart(cart);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(user);
		orderController.submit(Constant.USER_NAME);

		ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(Constant.USER_NAME);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		List<UserOrder> userOrders = responseEntity.getBody();
		assertNotNull(userOrders);
		assertEquals(0, userOrders.size());
	}

	@Test
	public void ordersByUserNullUser() {
		User user = createUser();
		Item item = createItem();
		Cart cart = user.getCart();

		cart.setUser(user);
		user.setCart(cart);

		List<Item> itemList = new ArrayList<>();
		itemList.add(item);
		cart.setItems(itemList);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(null);

		orderController.submit(Constant.USER_NAME);

		ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(Constant.USER_NAME);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

	private Item createItem() {
		Item item = new Item();

		item.setId(1L);
		item.setName(Constant.TEST_ITEM);
		item.setDescription("This is a fake item for test.");
		item.setPrice(BigDecimal.valueOf(100.0));

		return item;
	}

	private User createUser() {
		User user = new User();

		user.setId(1);
		user.setUsername(Constant.USER_NAME);
		user.setPassword(Constant.PASSWORD);

		Cart emptyCart = new Cart();
		emptyCart.setId(1L);
		emptyCart.setUser(null);
		emptyCart.setItems(new ArrayList<Item>());
		emptyCart.setTotal(BigDecimal.valueOf(0.0));
		user.setCart(emptyCart);

		return user;
	}

}