package com.shoppingcart.repository;

import org.springframework.data.repository.CrudRepository;

import com.shoppingcart.model.ConfirmationToken;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String>{
	ConfirmationToken findByConfirmationToken(String confirmationToken);

}
