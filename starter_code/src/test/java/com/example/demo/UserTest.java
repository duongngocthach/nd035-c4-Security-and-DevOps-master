package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private final UserRepository userRepo = mock(UserRepository.class);

	@Mock
	private final CartRepository cartRepo = mock(CartRepository.class);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createUserHappyPath() throws Exception {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername(Constant.USER_NAME);
		userRequest.setPassword(Constant.PASSWORD);
		userRequest.setConfirmPassword(Constant.PASSWORD);

		ResponseEntity<User> response = userController.createUser(userRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		User user = response.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals(Constant.USER_NAME, user.getUsername());
	}

	@Test
	public void findByIdFoundTest() {
		User user = createUser();

		when(userRepo.findById(Constant.USER_ID)).thenReturn(Optional.of(user));
		ResponseEntity<User> response = userController.findById(Constant.USER_ID);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
	}

	@Test
	public void findByIdNotFoundTest() {
		ResponseEntity<User> response = userController.findById(Constant.USER_ID);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void findByUserNameFoundTest() {
		User user = createUser();

		when(userRepo.findByUsername(Constant.USER_NAME)).thenReturn(user);
		ResponseEntity<User> response = userController.findByUserName(Constant.USER_NAME);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
		assertEquals(Constant.USER_ID.longValue(), user.getId());
		assertEquals(Constant.USER_NAME, user.getUsername());
		assertEquals(Constant.PASSWORD, user.getPassword());
	}

	@Test
	public void findByUserNameNotFoundTest() {
		ResponseEntity<User> response = userController.findByUserName(Constant.USER_NAME);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	private User createUser() {
		User user = new User();

		user.setId(Constant.USER_ID);
		user.setUsername(Constant.USER_NAME);
		user.setPassword(Constant.PASSWORD);

		return user;
	}

}