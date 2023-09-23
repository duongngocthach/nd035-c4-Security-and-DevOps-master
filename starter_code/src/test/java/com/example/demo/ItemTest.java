package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemTest {

	@InjectMocks
	private ItemController itemController;

	@Mock
	private final ItemRepository itemRepo = mock(ItemRepository.class);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
    public void getItemByIdTest(){
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createItem()));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        Item item = response.getBody();
        assertNotNull(item);
    }

	@Test
	public void getItemsTest() {
		ResponseEntity<List<Item>> response = itemController.getItems();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Item> itemList = response.getBody();
		assertNotNull(itemList);
	}

	@Test
	public void getItemByNameTest() {
		List<Item> items = new ArrayList<>();

		items.add(createItem());
		when(this.itemRepo.findByName(Constant.TEST_ITEM)).thenReturn(items);
		ResponseEntity<List<Item>> response = itemController.getItemsByName(Constant.TEST_ITEM);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(items, response.getBody());
	}

	public static Item createItem() {
		Item item = new Item();

		item.setId(1L);
		item.setName(Constant.TEST_ITEM);
		item.setDescription("This is item for test.");
		item.setPrice(BigDecimal.valueOf(100.0));

		return item;
	}

}