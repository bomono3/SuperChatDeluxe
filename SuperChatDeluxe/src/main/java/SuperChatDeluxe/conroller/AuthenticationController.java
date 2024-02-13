package SuperChatDeluxe.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import SuperChatDeluxe.model.AuthenticationRequest;
import SuperChatDeluxe.model.AuthenticationResponse;


public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserDetailsService userDetailsService;

	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createJwtToken(@RequestBody AuthenticationRequest request) throws Exception {
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} 
		catch (Exception e) {
			e.printStackTrace();
		}


//		final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());


		return ResponseEntity.status(201).body( new AuthenticationResponse("Success") );

	}
	
}
