//
// ReorderAxes.java
//

/*
ImageJ software for multidimensional image processing and analysis.

Copyright (c) 2010, ImageJDev.org.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the names of the ImageJDev.org developers nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package imagej.core.plugins.restructure;

import imagej.ImageJ;
import imagej.data.Dataset;
import imagej.data.display.ImageDisplayService;
import imagej.ext.module.DefaultModuleItem;
import imagej.ext.plugin.DynamicPlugin;
import imagej.ext.plugin.Menu;
import imagej.ext.plugin.Plugin;
import imagej.util.Log;

import java.util.ArrayList;
import java.util.Map;

import net.imglib2.RandomAccess;
import net.imglib2.img.Axes;
import net.imglib2.img.Axis;
import net.imglib2.img.ImgPlus;
import net.imglib2.ops.RegionIndexIterator;
import net.imglib2.type.numeric.RealType;

// TODO
// - can reorder X & Y out of 1st two positions. This could be useful in future
//     but might need to block right now.

/**
 * Changes the internal ImgPlus of a Dataset so that its data values stay the
 * same but the order of the axes is changed.
 * 
 * @author Barry DeZonia
 */
@Plugin(menu = { @Menu(label = "Image", mnemonic = 'i'),
	@Menu(label = "Stacks", mnemonic = 's'), @Menu(label = "Reorder Axes...") })
public class ReorderAxes extends DynamicPlugin {

	// -- instance variables --
	
	private Dataset dataset;
	private String[] axisNames;
	private int[] permutationAxisIndices;
	private Axis[] desiredAxisOrder;

	// -- public interface --
	
	public ReorderAxes() {
		dataset = ImageJ.get(ImageDisplayService.class).getActiveDataset();
		if (dataset == null) return;
		
		final Axis[] axes = dataset.getAxes();

		final ArrayList<String> choices = new ArrayList<String>();
		for (int i = 0; i < axes.length; i++) {
			choices.add(axes[i].getLabel());
		}
		for (int i = 0; i < axes.length; i++) {
			final DefaultModuleItem<String> name =
				new DefaultModuleItem<String>(this, name(i), String.class);
			name.setChoices(choices);
			addInput(name);
		}
	}

	/** Runs the plugin and reorders axes as specified by user. */
	@Override
	public void run() {
		if (dataset == null) return;
		getAxisNamesInOrder();
		setupDesiredAxisOrder();
		if (inputBad()) return;
		setupPermutationVars();
		final ImgPlus<? extends RealType<?>> newImgPlus = getReorganizedData();
		// reportDims(dataset.getImgPlus());
		// reportDims(newImgPlus);
		final int count = dataset.getCompositeChannelCount();
		dataset.setImgPlus(newImgPlus);
		dataset.setCompositeChannelCount(count);
	}

	// -- private helpers --

	/*
	private void reportDims(ImgPlus<?> imgPlus) {
		System.out.println("Dimension report");
		long[] dims = new long[imgPlus.numDimensions()];
		imgPlus.dimensions(dims);
		Axis[] axes = new Axis[dims.length];
		imgPlus.axes(axes);
		for (int i = 0; i < dims.length; i++) {
			System.out.println(dims[i]+" "+axes[i]);
		}
		System.out.println();
	}
	*/

	private String name(final int i) {
		return "Axis #" + i;
	}

	private void getAxisNamesInOrder() {
		final Map<String, Object> inputs = getInputs();
		axisNames = new String[dataset.getImgPlus().numDimensions()];
		for (int i = 0; i < axisNames.length; i++)
			axisNames[i] = (String) inputs.get(name(i));
	}

	/**
	 * Fills the internal variable "desiredAxisOrder" with the order of axes that
	 * the user specified in the dialog. all axes are present rather than just
	 * those present in the input Dataset.
	 */
	private void setupDesiredAxisOrder() {
		desiredAxisOrder = new Axis[axisNames.length];
		for (int i = 0; i < axisNames.length; i++)
			desiredAxisOrder[i] = Axes.get(axisNames[i]);
	}

	/**
	 * Returns true if user input is invalid. Basically this is a test that the
	 * user did not repeat any axis when specifying the axis ordering.
	 */
	private boolean inputBad() {
		for (int i = 0; i < desiredAxisOrder.length; i++)
			for (int j = i + 1; j < desiredAxisOrder.length; j++)
				if (desiredAxisOrder[i] == desiredAxisOrder[j]) {
					Log.error("at least one axis preference is repeated:"
						+ " axis preferences must be mututally exclusive");
					return true;
				}
		return false;
	}

	/**
	 * Takes a given set of axes (usually a subset of all possible axes) and
	 * returns a permuted set of axes that reflect the user specified axis order
	 */
	private Axis[] getPermutedAxes(final Axis[] currAxes) {
		final Axis[] permuted = new Axis[currAxes.length];
		int index = 0;
		for (int i = 0; i < desiredAxisOrder.length; i++)
			for (int j = 0; j < currAxes.length; j++) {
				if (currAxes[j] == desiredAxisOrder[i]) {
					permuted[index++] = currAxes[j];
					break;
				}
			}
		return permuted;
	}

	/**
	 * Sets up the working variable "permutationAxisIndices" which is used to
	 * actually permute positions.
	 */
	private void setupPermutationVars() {
		final Axis[] currAxes = dataset.getAxes();
		final Axis[] permutedAxes = getPermutedAxes(currAxes);
		permutationAxisIndices = new int[currAxes.length];
		for (int i = 0; i < currAxes.length; i++) {
			final Axis axis = currAxes[i];
			final int newIndex = getNewAxisIndex(permutedAxes, axis);
			permutationAxisIndices[i] = newIndex;
		}
	}

	/**
	 * Returns an ImgPlus that has same data values as the input Dataset but which
	 * has them stored in a different axis order
	 */
	private ImgPlus<? extends RealType<?>> getReorganizedData() {
		final RandomAccess<? extends RealType<?>> inputAccessor =
			dataset.getImgPlus().randomAccess();
		final long[] inputOrigin = new long[dataset.getImgPlus().numDimensions()];
		final long[] inputOffsets = new long[inputOrigin.length];
		dataset.getImgPlus().dimensions(inputOffsets);
		for (int i = 0; i < inputOffsets.length; i++)
			inputOffsets[i]--;
		final RegionIndexIterator iter =
			new RegionIndexIterator(inputOrigin, new long[inputOrigin.length],
				inputOffsets);
		final long[] origDims = dataset.getDims();
		final Axis[] origAxes = dataset.getAxes();
		final long[] newDims = getNewDims(origDims);
		final Axis[] newAxes = getNewAxes(origAxes);
		final ImgPlus<? extends RealType<?>> newImgPlus =
			RestructureUtils.createNewImgPlus(dataset, newDims, newAxes);
		newImgPlus.setCompositeChannelCount(dataset.getCompositeChannelCount());
		final RandomAccess<? extends RealType<?>> outputAccessor =
			newImgPlus.randomAccess();
		final long[] permutedPos = new long[inputOrigin.length];
		long[] currPos;
		while (iter.hasNext()) {
			iter.fwd();
			currPos = iter.getPosition();
			inputAccessor.setPosition(currPos);
			final double value = inputAccessor.get().getRealDouble();
			permute(currPos, permutedPos);
			outputAccessor.setPosition(permutedPos);
			outputAccessor.get().setReal(value);
		}
		return newImgPlus;
	}

	/**
	 * Returns the axis index of an Axis given a permuted set of axes.
	 */
	private int
		getNewAxisIndex(final Axis[] permutedAxes, final Axis originalAxis)
	{
		for (int i = 0; i < permutedAxes.length; i++) {
			if (permutedAxes[i] == originalAxis) return i;
		}
		throw new IllegalArgumentException("axis not found!");
	}

	/**
	 * Taking the original dims this method returns the new dimensions of the
	 * permuted space.
	 */
	private long[] getNewDims(final long[] origDims) {
		final long[] newDims = new long[origDims.length];
		permute(origDims, newDims);
		return newDims;
	}

	/**
	 * Taking the original axes order this method returns the new axes in the
	 * order of the permuted space.
	 */
	private Axis[] getNewAxes(final Axis[] origAxes) {
		final Axis[] newAxes = new Axis[origAxes.length];
		permute(origAxes, newAxes);
		return newAxes;
	}

	/**
	 * Permutes from a position in the original space into a position in the
	 * permuted space
	 */
	private void permute(final long[] origPos, final long[] permutedPos) {
		for (int i = 0; i < origPos.length; i++)
			permutedPos[permutationAxisIndices[i]] = origPos[i];
	}

	/**
	 * Permutes from an axis order in the original space into an axis order in the
	 * permuted space
	 */
	private void permute(final Axis[] origAxes, final Axis[] permutedAxes) {
		for (int i = 0; i < origAxes.length; i++)
			permutedAxes[permutationAxisIndices[i]] = origAxes[i];
	}

}
