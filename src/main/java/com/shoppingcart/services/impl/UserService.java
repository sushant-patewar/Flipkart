//package com.shoppingcart.services.impl;
//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.shoppingcart.model.PasswordResetToken;
//import com.shoppingcart.model.User;
//import com.shoppingcart.repository.PasswordResetTokenRepository;
//import com.shoppingcart.services.IUserService;
//
//@Service
//@Transactional
//public class UserService implements IUserService{
//	
//	@Autowired
//	private PasswordResetTokenRepository passwordTokenRepository;
//
//	public void createPasswordResetTokenForUser(User user, String token) {
//	    PasswordResetToken myToken = new PasswordResetToken(token, user);
//	    passwordTokenRepository.save(myToken);
//	}
//}
