package com.shoppingcart.controller;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shoppingcart.common.GenericResponse;
import com.shoppingcart.config.ExtraConfig;
import com.shoppingcart.exception.AppException;
// import com.shoppingcart.mail.MailDetails;
import com.shoppingcart.model.ApiResponse;
import com.shoppingcart.model.ConfirmationToken;
import com.shoppingcart.model.JwtAuthenticationResponse;
import com.shoppingcart.model.LoginRequest;
import com.shoppingcart.model.Role;
import com.shoppingcart.model.RoleName;
import com.shoppingcart.model.SignUpRequest;
import com.shoppingcart.model.User;
import com.shoppingcart.repository.ConfirmationTokenRepository;
import com.shoppingcart.repository.RoleRepository;
import com.shoppingcart.repository.UserRepository;
import com.shoppingcart.security.CustomUserDetailsService;
import com.shoppingcart.security.JwtTokenProvider;
import com.shoppingcart.security.UserPrincipal;
// import com.shoppingcart.services.impl.EmailSenderService;




@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	
//	@Autowired
//	private EmailSenderService emailSenderService;
	
//	@Autowired
//    private JavaMailSender mailSender;
	
	private JavaMailSender mailSender;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
	
	@Autowired
    private MessageSource messages;
	
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	@GetMapping("/wec")
	public String welcome() {
		return "Hello";
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {

//		Authentication authentication = authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
//
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		String jwt = tokenProvider.generateToken(authentication);
//		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		final UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService
				.loadUserByUsername(loginRequest.getUsernameOrEmail());
		final String jwt = jwtTokenProvider.generateToken(userPrincipal);

		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				signUpRequest.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role not set."));

		user.setRoles(Collections.singleton(userRole));

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getUsername()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}


//	@RequestMapping(value="/forgot-password", method=RequestMethod.GET)
//	public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
//		modelAndView.addObject("user", user);
//		modelAndView.setViewName("forgotPassword");
//		return modelAndView;
//	}
	
	@RequestMapping(value="/forgot-password", method=RequestMethod.GET)
	public ModelAndView displayResetPassword(ModelAndView modelAndView, String email) {
		modelAndView.addObject("Email", email);
		modelAndView.setViewName("forgotPassword");
		return modelAndView;
	}

	/**
	 * Receive email of the user, create token and send it via email to the user
	 */
	@RequestMapping(value="/forgot-password", method=RequestMethod.POST)
	public ModelAndView forgotUserPassword(ModelAndView modelAndView, String email) {
		// User existingUser = userRepository.findByEmail(user.getEmail());
		User existingUser = userRepository.findByEmail(email);
		if(existingUser != null) {
			// create token
			ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
			
			// save it
			confirmationTokenRepository.save(confirmationToken);
			
			// create the email
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(existingUser.getEmail());
			mailMessage.setSubject("Complete Password Reset!");
			mailMessage.setFrom("nairobley@gmail.com");
			mailMessage.setText("To complete the password reset process, please click here: "
			+"http://localhost:5000/confirm-reset?token="+confirmationToken.getConfirmationToken());
			
			 // emailSenderService.sendEmail(mailMessage);
			mailSender.send(mailMessage);
			// javaMailSender.send(email);
			
			modelAndView.addObject("message", "Request to reset password received. Check your inbox for the reset link.");
			modelAndView.setViewName("successForgotPassword");

		} else {	
			modelAndView.addObject("message", "This email does not exist!");
			modelAndView.setViewName("error");
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
	{
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
		
		if(token != null) {
			User user = userRepository.findByEmail(token.getUser().getEmail());
			// user.setEnabled(true);
			userRepository.save(user);
			modelAndView.addObject("user", user);
			modelAndView.addObject("emailId", user.getEmail());
			modelAndView.setViewName("resetPassword");
		} else {
			modelAndView.addObject("message", "The link is invalid or broken!");
			modelAndView.setViewName("error");
		}
		
		return modelAndView;
	}
	
	/**
	 * Receive the token from the link sent via email and display form to reset password
	 */
	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
		// ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
		
		if(user.getEmail() != null) {
			// use email to find user
			User tokenUser = userRepository.findByEmail(user.getEmail());
			// tokenUser.setEnabled(true);
			tokenUser.setPassword(encoder.encode(user.getPassword()));
			// System.out.println(tokenUser.getPassword());
			userRepository.save(tokenUser);
			modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
			modelAndView.setViewName("successResetPassword");
		} else {
			modelAndView.addObject("message","The link is invalid or broken!");
			modelAndView.setViewName("error");
		}
		
		return modelAndView;
	}
	
//	public EmailSenderService getEmailSenderService() {
//		return emailSenderService;
//	}
//	
//	public void setEmailSenderService(EmailSenderService emailSenderService) {
//		this.emailSenderService = emailSenderService;
//	}


}
