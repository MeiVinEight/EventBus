package org.mve.event.core;

import a.b.c.d.A;
import a.b.c.d.E;
import org.mve.event.Event;
import org.mve.event.EventException;
import org.mve.event.Listener;

public class SimpleEventExecutor implements EventExecutor
{
	@Override
	public void execute(Listener listener, Event event) throws EventException
	{
		A a;
		E e;
		try
		{
			a = (A) event;
			e = (E) listener;
		}
		catch (ClassCastException ignored)
		{
			return;
		}
		try
		{
			e.handle(a);
		}
		catch (Exception exc)
		{
			throw new EventException("Can not pass event ", exc);
		}
	}
}
