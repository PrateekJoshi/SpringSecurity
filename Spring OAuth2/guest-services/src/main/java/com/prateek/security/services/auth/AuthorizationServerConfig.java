package com.prateek.security.services.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/*
 * We will configure our authorization server here
 * Refer: https://www.lynda.com/Web-tutorials/Creating-OAuth-authorization-service/704153/746424-4.html
 */

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(NoOpPasswordEncoder.getInstance())
			.checkTokenAccess("permitAll()")
			.tokenKeyAccess("permitAll()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("guest_app")										//client1 id
			.scopes("READ_ALL_GUESTS","WRITE_GUEST","UPDATE_GUEST")
			.secret("secret")											 	//client1 secret
			.autoApprove(true)
			.authorities("ROLE_GUESTS_AUTHORIZED_CLIENT")
			.authorizedGrantTypes("client_credentials")
			.and()
			.withClient("api_audit")										//client2 id
			.scopes("READ_ALL_GUESTS")
			.secret("secret")												//client2 secret
			.autoApprove(true)
			.authorities("ROLE_GUESTS_AUTHORIZED_CLIENT")
			.authorizedGrantTypes("client_credentials");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(new InMemoryTokenStore());
	}
	
	
	

	
}
