package com.humblesoftware.reddit;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Event Class
 * 
 * An event class for handling custom events.
 * 
 * An observable object is any object which at some point invokes 
 * Event.trigger(event).  Observing objects are any objects with Listener 
 * members which have invoked Event.observe(observable, event, listener).  To 
 * remove listeners, invoke the stopObserving functions.  These can be invoked
 * to remove all listeners from an object, all listeners of a particular event
 * from an object, ot one observables listener of an event from an object.
 */
public class Event 
{
	/**
	 * A table containing observed objects and event tables of those objects
	 * 
	 * @member objectTable
	 */
	protected static Hashtable objectTable 
		= new Hashtable();
//	
//	/**
//	 * Singleton instance
//	 * @member Event
//	 */
	protected static Event singleton;
	
	public Event()
	{
	}
//	
//	/**
//	 * Singleton event getter.
//	 * 
//	 * This is used in creating new EventTables.  Not sure if it is needed
//	 * @todo There is an error in observe(): Event.getInstance().new fixes
//	 * @return
//	 */
	protected static Event getInstance() 
	{
		if (singleton == null) 
		{
			singleton = new Event();
		}
		
		return singleton;
	}
	
	/**
	 * Add a listener of event to object
	 * 
	 * @param object  The observed object
	 * @param event  The event name
	 * @param listener  The observing objects listener object
	 */
	public static synchronized void observe(Object object, String event, Listener listener)
	{
		// This object already has registered events/listeners.
		if (Event.objectTable.containsKey(object))
		{
			((EventTable) Event.objectTable.get(object)).addListener(event, listener);
		}
		// This object has no listeners
		else
		{
			EventTable eventTable = Event.getInstance().new EventTable();
			eventTable.addListener(event, listener);
			Event.objectTable.put(object, eventTable);
		}
	}
	/**
	 * Trigger an event in an object.
	 * 
	 * Runs all listeners of an event in an object.
	 * 
	 * @param object  Observed object.
	 * @param event  Observed event.
	 */
	public static synchronized void trigger(Object object, String event)
	{
		// Get the objects event table
		EventTable eventTable = (EventTable) Event.objectTable.get(object);
		if (eventTable != null) {
			// Get the events listener table
			ListenerTable listenerTable = (ListenerTable) eventTable.get(event);
			if (listenerTable != null) {
				// Execute all listener callbacks.
				Enumeration set = listenerTable.elements();
				Listener listener;
				while (set.hasMoreElements()) {
					listener = (Listener) set.nextElement();
					listener.callback(object);
				}
			}
		}
	}
	/**
	 * Removes all observers of object.
	 * 
	 * @param object
	 */
	public static synchronized void stopObserving(Object object)
	{
		Event.objectTable.remove(object);
	}
	/**
	 * Removes all observers observing event of object.
	 * 
	 * @param object
	 * @param event
	 */
	public static synchronized void stopObserving(Object object, String event)
	{
		if (Event.objectTable.containsKey(object))
		{
			((EventTable) Event.objectTable.get(object)).remove(event);
		}
	}
	/**
	 * Removes observer listener observing event of object. 
	 * 
	 * @param object
	 * @param event
	 * @param listener
	 */
	public static synchronized void stopObserving(Object object, String event, Listener listener)
	{
		if (Event.objectTable.containsKey(object))
		{
			EventTable eventTable = (EventTable) Event.objectTable.get(object);
			if (eventTable.containsKey(event))
			{
				((ListenerTable) eventTable.get(event)).remove(listener);
			}
		}
	}
	/**
	 * A list of events for an object.
	 * 
	 * @author carl
	 */
	private class EventTable extends Hashtable
	{
		/**
		 * Adds an event listener.
		 * 
		 * Adds a listener for a particular event to that events listener table.
		 * 
		 * @param event
		 * @param listener
		 */
		public void addListener(String event, Listener listener)
		{
			if (this.containsKey(event)) 
			{
				((ListenerTable) this.get(event)).addListener(listener);
			}
			else
			{
				ListenerTable observerTable = new ListenerTable();
				observerTable.addListener(listener);
				this.put(event, observerTable);
			}
		}
	}
	/**
	 * A list of observers of a particular event in a particular object
	 * 
	 * @author carl
	 */
	private class ListenerTable extends Hashtable
	{
		/**
		 * Adds a listener to the event.  Listeners are unique.
		 * 
		 * @param listener
		 */
		public void addListener(Listener listener)
		{
			if (!this.containsKey(listener))
			{
				this.put(listener, listener);
			}
		}
	}
}