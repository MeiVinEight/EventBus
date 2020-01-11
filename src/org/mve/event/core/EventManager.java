package org.mve.event.core;

import org.mve.event.Event;
import org.mve.event.Listener;

public interface EventManager
{
	void registerEvents(Listener listener);

	void unregisterEvents(Listener listener);

	void callEvent(Event event);
}
