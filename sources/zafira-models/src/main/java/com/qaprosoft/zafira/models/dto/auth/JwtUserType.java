package com.qaprosoft.zafira.models.dto.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.qaprosoft.zafira.models.db.Group.Role;

/**
 * All user information handled by the JWT token
 */
public class JwtUserType implements UserDetails
{
	private static final long serialVersionUID = 2105145272583220476L;

	private long id;
	
	private String username;

	private String password;
	
	private List<GrantedAuthority> authorities = new ArrayList<>();

	public JwtUserType(long id, String username, List<Role> roles)
	{
		this.id = id;
		this.username = username;
		for(Role role : roles)
		{
			this.authorities.add(new SimpleGrantedAuthority(role.name()));
		}
		// TODO: removed when default role populated for all
		if(this.authorities.isEmpty())
		{
			this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}
	
	public JwtUserType(long id, String username, String password, List<Role> roles)
	{
		this(id, username, roles);
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return authorities;
	}

	@Override
	public String getPassword()
	{
		return password;
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