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

package org.scijava.app;

import java.io.File;

import org.scijava.log.LogService;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.util.AppUtils;
import org.scijava.util.Manifest;
import org.scijava.util.POM;

/**
 * Abstract superclass of {@link App} implementations.
 * 
 * @author Curtis Rueden
 */
public abstract class AbstractApp extends AbstractRichPlugin implements App {

	@Parameter(required = false)
	private LogService log;

	/** Maven POM with metadata about the application. */
	private POM pom;

	/** JAR manifest with metadata about the application. */
	private Manifest manifest;

	@Override
	public String getTitle() {
		return getInfo().getName();
	}

	@Override
	public String getVersion() {
		// NB: We do not use VersionUtils.getVersion(c, groupId, artifactId)
		// because that method does not cache the parsed Manifest and/or POM.
		// We might have them already parsed here, and if not, we want to
		// parse then cache locally, rather than discarding them afterwards.

		// try the manifest first, since it might know its build number
		final Manifest m = getManifest();
		if (m != null) {
			final String v = m.getVersion();
			if (v != null) return v;
		}
		// try the POM
		final POM p = getPOM();
		if (p != null) {
			final String v = p.getVersion();
			if (v != null) return v;
		}
		return "Unknown";
	}

	@Override
	public POM getPOM() {
		if (pom == null) {
			// load the POM lazily
			pom = POM.getPOM(getClass(), getGroupId(), getArtifactId());
		}
		return pom;
	}

	@Override
	public Manifest getManifest() {
		if (manifest == null) {
			// load the manifest lazily
			manifest = Manifest.getManifest(getClass());
		}
		return manifest;
	}

	@Override
	public String getInfo(boolean mem) {
		final String appTitle = getTitle();
		final String appVersion = getVersion();
		final String javaVersion = System.getProperty("java.version");
		final String osArch = System.getProperty("os.arch");
		final long maxMem = Runtime.getRuntime().maxMemory();
		final long totalMem = Runtime.getRuntime().totalMemory();
		final long freeMem = Runtime.getRuntime().freeMemory();
		final long usedMem = totalMem - freeMem;
		final long usedMB = usedMem / 1048576;
		final long maxMB = maxMem / 1048576;
		final StringBuilder sb = new StringBuilder();
		sb.append(appTitle + " " + appVersion);
		sb.append("; Java " + javaVersion + " [" + osArch + "]");
		if (mem) sb.append("; " + usedMB + "MB of " + maxMB + "MB");
		return sb.toString();
	}

	@Override
	public String getSystemProperty() {
		return getInfo().getName().toLowerCase() + ".dir";
	}

	@Override
	public File getBaseDirectory() {
		return AppUtils.getBaseDirectory(getSystemProperty(), getClass(), null);
	}

	@Override
	public void about() {
		if (log != null) log.info(getInfo(false));
	}

	@Override
	public void prefs() {
		// NB: Do nothing.
	}

	@Override
	public void quit() {
		getContext().dispose();
	}

}
