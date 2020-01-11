package org.mve.event.core;

import org.mve.event.Event;
import org.mve.event.EventException;
import org.mve.event.EventExecutorFactory;
import org.mve.event.EventHandler;
import org.mve.event.HandlerList;
import org.mve.event.Listener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleEventManager implements EventManager
{
	private final Logger logger;
	private final HandlerList handlers = new HandlerList();

	public SimpleEventManager(Logger logger)
	{
		this.logger = logger;
	}

	@Override
	public void registerEvents(Listener listener)
	{
		Map<Class<? extends Event>, Set<RegisteredListener>> registeredListeners = createRegisteredListeners(listener);
		handlers.registerAll(registeredListeners);
	}

	@Override
	public void unregisterEvents(Listener listener)
	{
		handlers.unregister(listener);
	}

	@Override
	public final void callEvent(Event event)
	{
		if (event.isAsynchronous())
		{
			if (Thread.holdsLock(this))
			{
				throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
			}
			fireEvent(event);
		}
		else
		{
			synchronized (this)
			{
				fireEvent(event);
			}
		}
	}

	private void fireEvent(Event event)
	{
		RegisteredListener[] listeners = handlers.getRegisteredListeners(event.getClass());
		for (RegisteredListener listener : listeners)
		{
			try
			{
				listener.callEvent(event);
			}
			catch (EventException ee)
			{
				logger.log(Level.SEVERE, "Can not pass event " + event.getEventName(), ee);
			}
		}
	}

	private Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener)
	{
		Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
		Set<Method> methods;
		Method[] declaredMethods = listener.getClass().getDeclaredMethods();
		methods = new HashSet<>(declaredMethods.length, Float.MAX_VALUE);
		methods.addAll(Arrays.asList(declaredMethods));

		for (Method method : methods)
		{
			EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null) continue;
			int modifier = method.getModifiers();
			if (!(Modifier.isPublic(modifier) && !Modifier.isStatic(modifier)))
			{
				logger.severe("Register an invalid EventHandler method which is not public or not non-static signature \"" + method.toGenericString() + "\" in " + listener.getClass());
				continue;
			}
			final Class<?> checkClass;
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1 || !Event.class.isAssignableFrom(checkClass = parameterTypes[0]))
			{
				logger.severe("Register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
				continue;
			}

			final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
			Set<RegisteredListener> eventSet = ret.computeIfAbsent(eventClass, k -> new HashSet<>());
			method.setAccessible(true);

			EventExecutor eventExecutor = EventExecutorFactory.create(listener, method);
			eventSet.add(new RegisteredListener(listener, eventExecutor, eh.priority(), eh.ignoreCancelled()));
		}
		return ret;
	}
}
