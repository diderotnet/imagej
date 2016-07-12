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

package org.scijava.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.scijava.module.Module;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.RichPlugin;
import org.scijava.plugin.SingletonPlugin;

/**
 * The base interface for scripting language adapters.
 * <p>
 * Every SciJava scripting language implements this interface, which is based on
 * <a href="https://jcp.org/aboutJava/communityprocess/final/jsr223/">JSR
 * 223</a>, Scripting for the Java Platform, included in Java 6 and later in the
 * {@link javax.script} package. This {@link ScriptLanguage} interface extends
 * {@link ScriptEngineFactory}, meaning it can act as a JSR 223 Java scripting
 * language, while also providing additional functionality necessary for full
 * support within applications such as ImageJ. In particular, this interface
 * adds API for code generation of scripts to replicate SciJava {@link Module}
 * executions (i.e., for "script recording" of SciJava commands).
 * </p>
 * <p>
 * Script languages discoverable at runtime must implement this interface and be
 * annotated with @{@link Plugin} with attribute {@link Plugin#type()} =
 * {@link ScriptLanguage}.class. While it possible to create a scripting
 * language adapter merely by implementing this interface, it is encouraged to
 * instead extend {@link AbstractScriptLanguage}, for convenience.
 * </p>
 * 
 * @author Johannes Schindelin
 */
public interface ScriptLanguage extends ScriptEngineFactory, RichPlugin,
	SingletonPlugin
{

	/** True iff this language requires a compilation step. */
	boolean isCompiledLanguage();

	/**
	 * Performs any necessary conversion of an encoded object retrieved from the
	 * language's script engine.
	 * 
	 * @see ScriptEngine#get(String)
	 */
	Object decode(Object object);

}
