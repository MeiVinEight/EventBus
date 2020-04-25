package org.mve.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleReflectionEventExecutor implements EventExecutor
{
	private final Class<? extends Event> eventClass;
	private final MethodHandle method;

	public SimpleReflectionEventExecutor(Class<? extends Event> eventClass, Method method) throws IllegalAccessException
	{
		this.eventClass = eventClass;
		this.method = MethodHandles.lookup().unreflect(method);
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
