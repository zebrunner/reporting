package com.qaprosoft.zafira.tests.services.builders;

import com.qaprosoft.zafira.models.dto.AbstractType;
import com.qaprosoft.zafira.models.dto.JobType;

public interface IModelBuilder<T extends AbstractType>
{
	/**
	 * Get current entity, may be null
	 * @return model extends {@link AbstractType}
	 */
	T getInstance();

	/**
	 * Register new model in db
	 * @return model extends {@link AbstractType}
	 */
	T register();

	/**
	 * Choose registered model in db
	 * @return model extends {@link AbstractType}
	 */
	default T getCurrentInstance()
	{
		return getInstance().getId() == 0 ? register() : getInstance();
	}
}
