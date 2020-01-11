package org.mve.event.core;

import org.mve.event.Event;
import org.mve.event.EventException;
import org.mve.event.Listener;

public interface EventExecutor
{
	void execute(Listener listener, Event event) throws EventException;
}
