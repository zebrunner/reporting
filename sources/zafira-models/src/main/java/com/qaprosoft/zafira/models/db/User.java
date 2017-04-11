package com.qaprosoft.zafira.models.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Group.Role;

@JsonInclude(Include.NON_NULL)
public class User extends AbstractEntity implements Comparable<User>
{
	private static final long serialVersionUID = 2720141152633805371L;

	private String userName;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private List<Group> groups = new ArrayList<>();

	public User()
	{
	}

	public User(long id)
	{
		super.setId(id);
	}

	public User(String userName)
	{
		this.userName = userName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public List<Group> getGroups()
	{
		return groups;
	}

	public void setGroups(List<Group> groups)
	{
		this.groups = groups;
	}
	
	public List<Role> getRoles()
	{
		Set<Role> roles = new HashSet<>();
		for(Group group : groups)
		{
			roles.add(group.getRole());
		}
		return new ArrayList<>(roles);
	}

	@Override
	public int compareTo(User user)
	{
		return userName.compareTo(user.getUserName());
	}
}
