package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.JWTAuthResponse;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserAppService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDTO loginDto) {
		String accessToken = userService.authenticateUser(loginDto);
		return ResponseEntity.accepted()
				.contentType(MediaType.APPLICATION_JSON)
				.body(new JWTAuthResponse(accessToken));
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
			throw new com.lacouf.rsbjwt.exception.RegistrationConflictException("errors.emailTaken");
		}

		if (userCreateDTO.getMatricule() != null && userService.matriculeExists(userCreateDTO.getMatricule())) {
			throw new com.lacouf.rsbjwt.exception.RegistrationConflictException("errors.matriculeTaken");
		}

		if (userCreateDTO.getEmployerWorkId() != null && userService.employerIdExists(userCreateDTO.getEmployerWorkId())) {
			throw new com.lacouf.rsbjwt.exception.RegistrationConflictException("errors.identifiantTaken");
		}

		userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
		return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
				userService.inscription(userCreateDTO));
	}
}
