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

// Portions of this class were derived from the loci.common.DataTools class of
// the Bio-Formats library, licensed according to Simplified BSD, as follows:
//
// Copyright (C) 2005 - 2015 Open Microscopy Environment:
//   - Board of Regents of the University of Wisconsin-Madison
//   - Glencoe Software, Inc.
//   - University of Dundee
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package org.scijava.util;

import java.io.File;
import java.text.DecimalFormatSymbols;

/**
 * Useful methods for working with {@link String}s.
 *
 * @author Curtis Rueden
 * @author Chris Allan
 * @author Melissa Linkert
 */
public final class StringUtils {

	private StringUtils() {
		// NB: prevent instantiation of utility class.
	}

	/** Normalizes the decimal separator for the user's locale. */
	public static String sanitizeDouble(String value) {
		value = value.replaceAll("[^0-9,\\.]", "");
		final char separator = new DecimalFormatSymbols().getDecimalSeparator();
		final char usedSeparator = separator == '.' ? ',' : '.';
		value = value.replace(usedSeparator, separator);
		try {
			Double.parseDouble(value);
		}
		catch (final Exception e) {
			value = value.replace(separator, usedSeparator);
		}
		return value;
	}

	/** Removes null bytes from a string. */
	public static String stripNulls(final String toStrip) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < toStrip.length(); i++) {
			if (toStrip.charAt(i) != 0) {
				s.append(toStrip.charAt(i));
			}
		}
		return s.toString().trim();
	}

	/** Checks if two filenames have the same prefix. */
	public static boolean samePrefix(final String s1, final String s2) {
		if (s1 == null || s2 == null) return false;
		final int n1 = s1.indexOf(".");
		final int n2 = s2.indexOf(".");
		if ((n1 == -1) || (n2 == -1)) return false;

		final int slash1 = s1.lastIndexOf(File.pathSeparator);
		final int slash2 = s2.lastIndexOf(File.pathSeparator);

		final String sub1 = s1.substring(slash1 + 1, n1);
		final String sub2 = s2.substring(slash2 + 1, n2);
		return sub1.equals(sub2) || sub1.startsWith(sub2) || sub2.startsWith(sub1);
	}

	/** Removes unprintable characters from the given string. */
	public static String sanitize(final String s) {
		if (s == null) return null;
		StringBuffer buf = new StringBuffer(s);
		for (int i = 0; i < buf.length(); i++) {
			final char c = buf.charAt(i);
			if (c != '\t' && c != '\n' && (c < ' ' || c > '~')) {
				buf = buf.deleteCharAt(i--);
			}
		}
		return buf.toString();
	}

}
