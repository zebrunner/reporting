package com.qaprosoft.zafira.ws.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.qaprosoft.zafira.models.db.Group.Role;

/**
 * SecuredUser
 * 
 * @author akhursevich
 */
public class SecuredUser implements UserDetails
{
	private static final long serialVersionUID = 1024356863633107004L;

	private long id;
	private String username;
	private String password;
	private List<GrantedAuthority> authorities = new ArrayList<>();

	public SecuredUser(String username, List<Role> roles)
	{
		this.username = username;
		for(Role role : roles)
		{
			authorities.add(new SimpleGrantedAuthority(role.name()));
		}
		
		// TODO: Remove when ready global user role setup
		if(CollectionUtils.isEmpty(roles))
		{
			authorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
		}
	}
	
	public SecuredUser(long id, String username, String password, List<Role> roles)
	{
		this(username, roles);
		this.id = id;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return this.authorities;
	}

	public long getId()
	{
		return id;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
