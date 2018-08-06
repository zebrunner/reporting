package com.qaprosoft.zafira.services.services.cache;

import java.io.Serializable;
import java.util.function.Function;

public interface ICacheableService<T, R> extends Serializable
{

	long serialVersionUID = -1915862222225912222L;

	Function<T, R> getValue();
}
