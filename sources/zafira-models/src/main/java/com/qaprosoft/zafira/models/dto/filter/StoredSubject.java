package com.qaprosoft.zafira.models.dto.filter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class StoredSubject
{
	private Subject testRunSubject = new Subject()
	{
		{
			setName(Name.TEST_RUN);
			setCriterias(new ArrayList<Criteria>()
				 {
					 {
						add(new Criteria()
							{
								{
									setName(Name.STATUS);
									setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS));
								}
							}
						);
						 add(new Criteria()
							 {
								 {
									 setName(Name.TEST_SUITE);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS, Operator.CONTAINS, Operator.NOT_CONTAINS));
								 }
							 }
						 );
						 add(new Criteria()
							 {
								 {
									 setName(Name.JOB_URL);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS, Operator.CONTAINS, Operator.NOT_CONTAINS));
								 }
							 }
						 );
						 add(new Criteria()
							 {
								 {
									 setName(Name.ENV);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS));
								 }
							 }
						 );
						 add(new Criteria()
							 {
								 {
									 setName(Name.PLATFORM);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS));
								 }
							 }
						 );
						 add(new Criteria()
							 {
								 {
									 setName(Name.DATE);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS, Operator.BEFORE,
											 Operator.AFTER, Operator.LAST_SEVEN_DAYS, Operator.LAST_FOURTEEN_DAYS,
											 Operator.LAST_THIRTY_DAYS));
								 }
							 }
						 );
						 add(new Criteria()
							 {
								 {
									 setName(Name.PROJECT);
									 setOperators(Arrays.asList(Operator.EQUALS, Operator.NOT_EQUALS));
								 }
							 }
						 );
					 }
				 }
			);
		}
	};

	private List<Subject> subjects = new ArrayList<Subject>()
	{
		{
			add(testRunSubject);
		}
	};


	public Subject getTestRunSubject()
	{
		return testRunSubject;
	}

	public Subject getSubjectByName(Subject.Name name)
	{
		return subjects.stream().filter(subject -> subject.getName().equals(name)).findFirst().orElse(null);
	}
}
