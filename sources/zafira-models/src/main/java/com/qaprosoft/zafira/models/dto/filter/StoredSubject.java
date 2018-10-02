package com.qaprosoft.zafira.models.dto.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoredSubject
{
	private Subject testRunSubject = new Subject()
	{
		{
			setName(Name.TEST_RUN);
			setCriterias(new ArrayList<Criteria>()
				 {
					private static final long serialVersionUID = -3470218714244856071L;

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
											 Operator.AFTER, Operator.LAST_24_HOURS, Operator.LAST_7_DAYS, Operator.LAST_14_DAYS,
											 Operator.LAST_30_DAYS));
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
		private static final long serialVersionUID = 3499422182444939573L;

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
