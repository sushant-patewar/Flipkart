package com.shoppingcart.services;

import java.util.Optional;

import com.shoppingcart.model.User;

public interface IUserService {
	
	void createPasswordResetTokenForUser(User user, String token);

}
