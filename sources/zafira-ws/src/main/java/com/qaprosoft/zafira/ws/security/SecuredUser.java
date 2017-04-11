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
	private String userName;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private List<GrantedAuthority> authorities = new ArrayList<>();

	public SecuredUser(String userName, List<Role> roles)
	{
		this.userName = userName;
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

	public SecuredUser(long id, String userName, String password, String email, String firstName, String lastName, List<Role> roles)
	{
		this(userName, roles);
		this.id = id;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
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
