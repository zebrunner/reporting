package com.qaprosoft.zafira.ws.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SecuredUser
 * 
 * @author Alex Khursevich
 */
public class SecuredUser implements UserDetails
{
	private static final long serialVersionUID = 1024356863633107004L;

	private long id;
	private String userName;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private String role;

	public SecuredUser(String userName, String role)
	{
		this.userName = userName;
		this.role = role;
	}

	public SecuredUser(long id, String userName, String password, String email, String firstName, String lastName, String role)
	{
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(role != null)
		{
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

	public long getId()
	{
		return id;
	}

	@Override
	public String getUsername()
	{
		return userName;
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

	public String getRole()
	{
		return role;
	}

	public String getEmail()
	{
		return email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}
}
