/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.tool;

import org.scijava.display.event.input.KyPressedEvent;
import org.scijava.display.event.input.KyReleasedEvent;
import org.scijava.display.event.input.MsClickedEvent;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsMovedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.display.event.input.MsWheelEvent;
import org.scijava.input.MouseCursor;
import org.scijava.plugin.AbstractRichPlugin;

/**
 * Abstract base class for {@link Tool} implementations.
 * 
 * @author Curtis Rueden
 * @author Grant Harris
 */
public abstract class AbstractTool extends AbstractRichPlugin implements Tool {

	@Override
	public boolean isAlwaysActive() {
		return false;
	}

	@Override
	public boolean isActiveInAppFrame() {
		return false;
	}

	@Override
	public MouseCursor getCursor() {
		return MouseCursor.DEFAULT;
	}

	@Override
	public void activate() {
		// do nothing by default
	}

	@Override
	public void deactivate() {
		// do nothing by default
	}

	@Override
	public void onKeyDown(final KyPressedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onKeyUp(final KyReleasedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseDown(final MsPressedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseUp(final MsReleasedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseClick(final MsClickedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseMove(final MsMovedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseDrag(final MsDraggedEvent evt) {
		// do nothing by default
	}

	@Override
	public void onMouseWheel(final MsWheelEvent evt) {
		// do nothing by default
	}

	@Override
	public void configure() {
		// do nothing by default
	}

	@Override
	public String getDescription() {
		return getInfo().getDescription();
	}

}
