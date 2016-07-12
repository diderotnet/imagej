package net.diderot.scijava.command;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.diderot.scijava.api.Scijava;
import osgi.enroute.debug.api.Debug;

/**
 * This is the implementation. It registers the Scijava interface and calls it
 * through a Gogo command.
 * 
 */
@Component(service=ScijavaCommand.class, property = { Debug.COMMAND_SCOPE + "=scijava",
		Debug.COMMAND_FUNCTION + "=scijava" }, name="net.diderot.scijava.command")
public class ScijavaCommand {
	private Scijava target;

	public void scijava(String message) {
		target.say(message);
	}

	@Reference
	void setScijava(Scijava service) {
		this.target = service;
	}

}
