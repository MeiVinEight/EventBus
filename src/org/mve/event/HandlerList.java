package org.mve.event;

import org.mve.event.core.RegisteredListener;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class HandlerList
{
	/**
	 * Handler array. This field being an array is the key to this system's
	 * speed.
	 */
	private volatile RegisteredListener[] handlers = null;
	private Class<? extends Event> lastEvent;
	private final HashMap<Class<? extends Event>, RegisteredListener[]> handlerMap = new HashMap<>();

	/**
	 * Dynamic handler lists. These are changed using register() and
	 * unregister() and are automatically baked to the handlers array any time
	 * they have changed.
	 */
	private final Map<Class<? extends Event>, Map<EventPriority, List<RegisteredListener>>> handlerslots;

	/**
	 * Create a new handler list and initialize using EventPriority.
	 * <p>
	 * The HandlerList is then added to meta-list for use in bakeAll()
	 */
	public HandlerList()
	{
		handlerslots = new HashMap<>();
	}

	/**
	 * Unregister all listeners from all handler lists.
	 */
	public synchronized void unregisterAll()
	{
		handlerslots.clear();
		this.handlers = null;
	}

	/**
	 * Register a new listener in this handler list
	 *
	 * @param clazz handled event class
	 * @param listener listener to register
	 */
	public synchronized void register(Class<? extends Event> clazz, RegisteredListener listener)
	{
		if (listener == null || clazz == null) return;
		boolean check = true;
		Map<EventPriority, List<RegisteredListener>> listenerMap = handlerslots.get(clazz);
		if (listenerMap == null)
		{
			check = false;
			handlerslots.put(clazz, (listenerMap = new EnumMap<>(EventPriority.class)));
		}
		List<RegisteredListener> listeners = listenerMap.get(listener.getPriority());
		if (listeners == null)
		{
			check = false;
			listenerMap.put(listener.getPriority(), (listeners = new LinkedList<>()));
		}
		if (check && listeners.contains(listener)) throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
		listeners.add(listener);
		handlers = null;
		handlerMap.clear();
	}

	public void registerAll(Class<? extends Event> clazz, Collection<RegisteredListener> listeners)
	{
		for (RegisteredListener listener : listeners) register(clazz, listener);
	}

	/**
	 * Register a collection of new listeners in this handler list
	 *
	 * @param listeners listeners to register
	 */
	public void registerAll(Map<Class<? extends Event>, ? extends Collection<RegisteredListener>> listeners)
	{
		for (Map.Entry<Class<? extends Event>, ? extends Collection<RegisteredListener>> entry : listeners.entrySet()) registerAll(entry.getKey(), entry.getValue());
	}

	/**
	 * Remove a listener from a specific order slot
	 *
	 * @param listener listener to remove
	 */
	public synchronized void unregister(RegisteredListener listener)
	{
		for (Map<EventPriority, List<RegisteredListener>> listenerMap : handlerslots.values())
		{
			List<RegisteredListener> listeners = listenerMap.get(listener.getPriority());
			if (listeners == null) throw new IllegalArgumentException("Listener has not been registered");
			if (listeners.remove(listener)) handlers = null; handlerMap.clear();
		}
	}

	/**
	 * Remove a specific listener from this handler
	 *
	 * @param listener listener to remove
	 */
	public synchronized void unregister(Listener listener)
	{
		boolean changed = false;
		for (Map<EventPriority, List<RegisteredListener>> listenerMap : handlerslots.values())
		{
			for (List<RegisteredListener> listeners : listenerMap.values())
			{
				ListIterator<RegisteredListener> iterator = listeners.listIterator();
				while (iterator.hasNext())
				{
					RegisteredListener registeredListener = iterator.next();
					if (registeredListener.getListener().equals(listener))
					{
						iterator.remove();
						changed = true;
					}
				}
			}
		}
		if (changed) handlers = null; handlerMap.clear();
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 */
	public synchronized void bake(Class<? extends Event> clazz)
	{
		if (clazz == lastEvent && handlers != null) return;
		handlers = handlerMap.get(clazz);
		if (handlers != null) return;
		List<RegisteredListener> listeners = new LinkedList<>();
		for (Map.Entry<Class<? extends Event>, Map<EventPriority, List<RegisteredListener>>> entry : handlerslots.entrySet())
		{
			Class<? extends Event> event = entry.getKey();
			if (clazz == event || event.isAssignableFrom(clazz))
			{
				for (List<RegisteredListener> listenerList : entry.getValue().values())
				{
					listeners.addAll(listenerList);
				}
			}
		}
		handlers = new RegisteredListener[listeners.size()];
		int i = 0;
		for (RegisteredListener listener : listeners) handlers[i++] = listener;
		lastEvent = clazz;
		RegisteredListener[] inMap = new RegisteredListener[handlers.length];
		System.arraycopy(handlers, 0, inMap, 0, handlers.length);
		handlerMap.put(clazz, inMap);
	}

	/**
	 * Get the baked registered listeners associated with this handler list
	 *
	 * @return the array of registered listeners
	 */
	public RegisteredListener[] getRegisteredListeners(Class<? extends Event> clazz)
	{
		RegisteredListener[] handlers;
		while ((handlers = this.handlers) == null) bake(clazz); // This prevents fringe cases of returning null
		return handlers;
	}
}

