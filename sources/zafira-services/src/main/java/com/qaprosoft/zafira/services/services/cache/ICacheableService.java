package com.qaprosoft.zafira.services.services.cache;

import java.util.function.Function;

public interface ICacheableService<T, R>
{

	Function<T, R> getValue();
}
