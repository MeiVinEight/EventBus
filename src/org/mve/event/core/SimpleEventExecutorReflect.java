package org.mve.event.core;

import org.mve.event.Event;
import org.mve.event.EventException;
import org.mve.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleEventExecutorReflect implements EventExecutor
{
	private final Class<? extends Event> eventClass;
	private final Method method;

	public SimpleEventExecutorReflect(Class<? extends Event> eventClass, Method method)
	{
		this.eventClass = eventClass;
		this.method = method;
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException
	{
		try
		{
			if (!eventClass.isAssignableFrom(event.getClass()))
			{
				return;
			}
			method.invoke(listener, event);
		}
		catch (InvocationTargetException ex)
		{
			throw new EventException(ex.getCause());
		}
		catch (Throwable t)
		{
			throw new EventException(t);
		}
	}
}
