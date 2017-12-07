package com.qaprosoft.zafira.ws.security.expressions;

@FunctionalInterface
public interface EvaluatorComparator
{
	boolean contains(String currentUserPermission);
}
