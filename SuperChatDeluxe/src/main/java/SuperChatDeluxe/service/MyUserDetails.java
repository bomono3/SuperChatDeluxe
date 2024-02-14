package SuperChatDeluxe.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import SuperChatDeluxe.model.User;


public class MyUserDetails implements UserDetails{

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private boolean expired;
	private boolean locked;
	private boolean credentialsBad;
	private boolean enabled;
	private List<GrantedAuthority> authorities;
	
	
	public MyUserDetails(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.expired = false;
		this.locked = false;
		this.credentialsBad = false;
		this.enabled = true;
		this.authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !expired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return !credentialsBad;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
