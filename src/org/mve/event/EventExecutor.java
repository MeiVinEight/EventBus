package org.mve.event;

public interface EventExecutor
{
	void execute(Listener listener, Event event) throws EventException;
}
