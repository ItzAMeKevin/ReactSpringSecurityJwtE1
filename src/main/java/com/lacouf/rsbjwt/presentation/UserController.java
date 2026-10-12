package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AuthErrorResponse;
import com.lacouf.rsbjwt.service.dto.JWTAuthResponse;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserAppService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDto) {
		try {
			String accessToken = userService.authenticateUser(loginDto);
			final JWTAuthResponse authResponse = new JWTAuthResponse(accessToken);
			return ResponseEntity.accepted()
					.contentType(MediaType.APPLICATION_JSON)
					.body(authResponse);
		} catch (UserNotFoundException | AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new AuthErrorResponse(
							"Invalid email or password",
							"AUTH_FAILED"
					));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new AuthErrorResponse(
							"Something went wrong. Please try again later.",
							"SERVER_ERROR"
					));
		}
	}

	@GetMapping("/me")
	public ResponseEntity<UserCreateDTO> getMe(HttpServletRequest request) {
		return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
				userService.getMe(request.getHeader("Authorization")));
	}

	@PostMapping("/inscription")
	public ResponseEntity<?> inscription(@RequestBody UserCreateDTO userCreateDTO) {
		UserCreateDTO existingUser = userService.getUserByEmail(userCreateDTO.getEmail());
		if (existingUser != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("field", "email"));
		}

		if (userCreateDTO.getMatricule() != null && userService.matriculeExists(userCreateDTO.getMatricule())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("field", "matricule"));
		}

		if (userCreateDTO.getEmployerWorkId() != null && userService.employerIdExists(userCreateDTO.getEmployerWorkId())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("field", "identifiant"));
		}

		userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
		return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
				userService.inscription(userCreateDTO));
	}
}
