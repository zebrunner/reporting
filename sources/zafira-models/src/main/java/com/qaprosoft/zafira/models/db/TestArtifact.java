package com.qaprosoft.zafira.models.db;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestArtifact extends AbstractEntity
{
	private static final long serialVersionUID = 2708440751800176584L;

	private String name;
	private String link;
	private Date expiresAt;
	private Long testId;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public Date getExpiresAt()
	{
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt)
	{
		this.expiresAt = expiresAt;
	}

	public Long getTestId()
	{
		return testId;
	}

	public void setTestId(Long testId)
	{
		this.testId = testId;
	}
	
	public boolean isValid()
	{
		return name != null && !name.isEmpty() && link != null && !link.isEmpty();
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestArtifact that = (TestArtifact) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (expiresAt != null ? !expiresAt.equals(that.expiresAt) : that.expiresAt != null) return false;
        return testId != null ? testId.equals(that.testId) : that.testId == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (expiresAt != null ? expiresAt.hashCode() : 0);
        result = 31 * result + (testId != null ? testId.hashCode() : 0);
        return result;
    }
}