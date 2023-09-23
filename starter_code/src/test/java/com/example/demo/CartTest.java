package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CartTest {

	@InjectMocks
	private CartController cartController;

	@Mock
	private final UserRepository userRepo = mock(UserRepository.class);

	@Mock
	private final ItemRepository itemRepo = mock(ItemRepository.class);

	@Mock
	private final CartRepository cartRepo = mock(CartRepository.class);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void addToCartNoUserError() {

		ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
		ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

		assertNotNull(responseEntity);
		assertEquals(404, responseEntity.getStatusCodeValue());
	}

	@Test
    public void addToCartNoItemError(){

        when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(new User());
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest(Constant.USER_NAME, 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        verify(itemRepo, times(1)).findById(1L);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

	@Test
	public void addToCartTest() {

		User user = createUser();
		Item item = createItem();
		Cart cart = user.getCart();
		cart.addItem(item);
		cart.setUser(user);
		user.setCart(cart);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(user);
		when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

		ModifyCartRequest modifyCartRequest = createModifyCartRequest(Constant.USER_NAME, 1, 1);
		ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

		assertNotNull(responseEntity);
		assertEquals(200, responseEntity.getStatusCodeValue());

		Cart responseCart = responseEntity.getBody();

		assertNotNull(responseCart);

		List<Item> items = responseCart.getItems();
		assertNotNull(items);

		assertEquals(Constant.USER_NAME, responseCart.getUser().getUsername());

		verify(cartRepo, times(1)).save(responseCart);
	}

	@Test
	public void removeFromCartNoUserError() {

		ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
		ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

		assertNotNull(responseEntity);
		assertEquals(404, responseEntity.getStatusCodeValue());
	}

	@Test
    public void removeFromCartNoItemError(){

        when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(new User());
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest(Constant.USER_NAME, 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        verify(itemRepo, times(1)).findById(1L);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

	@Test
	public void removeFromCartTest() {

		User user = createUser();
		Item item = createItem();
		Cart cart = user.getCart();
		cart.addItem(item);
		cart.setUser(user);
		user.setCart(cart);

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(user);
		when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

		ModifyCartRequest modifyCartRequest = createModifyCartRequest(Constant.USER_NAME, 1, 1);
		ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

		assertNotNull(responseEntity);
		assertEquals(200, responseEntity.getStatusCodeValue());

		Cart responseCart = responseEntity.getBody();

		assertNotNull(responseCart);
		List<Item> items = responseCart.getItems();
		assertNotNull(items);
		assertEquals(0, items.size());
		assertEquals(Constant.USER_NAME, responseCart.getUser().getUsername());

		verify(cartRepo, times(1)).save(responseCart);

	}

	public static User createUser() {
		User user = new User();
		user.setId(1);
		user.setUsername(Constant.USER_NAME);
		user.setPassword(Constant.PASSWORD);

		Cart cart = new Cart();
		cart.setId(1L);
		cart.setUser(null);
		cart.setItems(new ArrayList<Item>());
		cart.setTotal(BigDecimal.valueOf(0.0));
		user.setCart(cart);

		return user;
	}

	public static Item createItem() {
		Item item = new Item();
		item.setId(1L);
		item.setName(Constant.TEST_ITEM);
		item.setDescription("This is item for test.");
		item.setPrice(BigDecimal.valueOf(10.0));
		return item;
	}

	public static ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity) {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setUsername(username);
		modifyCartRequest.setItemId(itemId);
		modifyCartRequest.setQuantity(quantity);
		return modifyCartRequest;
	}

}