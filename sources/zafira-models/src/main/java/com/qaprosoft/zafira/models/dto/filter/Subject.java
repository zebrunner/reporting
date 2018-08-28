package com.qaprosoft.zafira.models.dto.filter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Subject
{
	@NotNull(message = "Subject name required")
	private Name name;
	@Valid
	private List<Criteria> criterias;

	public enum Name
	{
		TEST_RUN
	}

	public Name getName()
	{
		return name;
	}

	public void setName(Name name)
	{
		this.name = name;
	}

	public List<Criteria> getCriterias()
	{
		return criterias;
	}

	public void setCriterias(List<Criteria> criterias)
	{
		this.criterias = criterias;
	}


	public void sortCriterias()
	{
		Collections.sort(this.criterias, Comparator.comparing(Criteria::getName));
	}
}
