package com.frankmoley.security.app.auth;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LandonUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	private final AuthGroupRepository authGroupRepository;
	
	public LandonUserDetailsService(UserRepository userRepository,AuthGroupRepository authGroupRepository) {
		super();
		this.userRepository = userRepository;
		this.authGroupRepository = authGroupRepository;
	}
	
	/* -> loadUserByUsername() will get user from repo. Also , it will get auth group of the user
	 * from the auth repo. 
	 * -> Using the user and user auth , it applies the security principals on the user by calling
	 *    LandonUserPrincipal(user,authGroup)
	 */
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if( user == null )
		{
			throw new UsernameNotFoundException("Cannot find username "+ username);
		}
		List<AuthGroup> authGroups = this.authGroupRepository.findByUsername(username);
		return new LandonUserPrincipal(user,authGroups);  
	}

}
