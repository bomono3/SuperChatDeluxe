package SuperChatDeluxe.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import SuperChatDeluxe.model.AuthenticationRequest;
import SuperChatDeluxe.model.AuthenticationResponse;
import SuperChatDeluxe.util.JwtUtil;


@RestController
public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	

	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createJwtToken(@RequestBody AuthenticationRequest request) throws Exception {
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} 
		catch (Exception e) {
			return ResponseEntity.status(401).body("Invalid Credentials");
		}


		final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

		final String jwt = jwtUtil.generateTokens(userDetails);

		return ResponseEntity.status(201).body( new AuthenticationResponse(jwt) );

	}
	
}
