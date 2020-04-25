package org.mve.event;

public interface EventManager
{
	void registerEvents(Listener listener);

	void unregisterEvents(Listener listener);

	void callEvent(Event event);
}
