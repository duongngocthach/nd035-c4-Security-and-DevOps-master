package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	private static final int PASSWORD_MINIMUM_SIZE = 8;
	
	private final static Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable("id") Long id) {
		Optional<User> user = userRepository.findById(id);
		if(!user.isPresent()) {
			log.info("UserController | findById | User Not Found. id {}", id);
			return ResponseEntity.notFound().build();
		} else {
			log.info("UserController | findById | User Found. id {}", id);
			return ResponseEntity.ok(user.get());
		}
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("UserController | findByUsername | Fail: | User Not Found. username : {}", username);
			return ResponseEntity.notFound().build();
		} else {
			log.info("UserController | findByUsername | Success: | User Found. username : {}", username);
			return ResponseEntity.ok(user);
		}
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		if (userRepository.findByUsername(user.getUsername()) != null) {
			log.info("UserController | CreateUser | Fail: | User is exist.");
			return ResponseEntity.badRequest().build();
		}
		
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if (!validPassword(createUserRequest.getPassword()) ||
			!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.info("UserController | CreateUser | Fail: | Password did not meet minimum requirements.");
			return ResponseEntity.badRequest().build();
		}

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);

		user.setPassword(null);
		log.info("UserController | CreateUser | Success: | username: {}", user.getUsername());
		return ResponseEntity.ok(user);
	}

	private boolean validPassword(String password) {
		return password != null && password.length() >= PASSWORD_MINIMUM_SIZE;
	}

}