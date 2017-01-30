/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * BinaryMask.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImageContainer;
import adams.flow.control.StorageName;
import boofcv.struct.image.ImageUInt8;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Masks an image using a provided binary mask from storage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-mask &lt;adams.flow.control.StorageName&gt; (property: mask)
 * &nbsp;&nbsp;&nbsp;The name of the mask in internal storage.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-high &lt;boolean&gt; (property: high)
 * &nbsp;&nbsp;&nbsp;If enabled, uses 255 as mask color instead of 0.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class BinaryMask
  extends AbstractBoofCVTransformer {

  private static final long serialVersionUID = -4518424819623795118L;

  /** use high or low value for mask **/
  protected boolean m_High;

  /** the image in internal storage. */
  protected StorageName m_Mask;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Masks an image using a provided binary mask from storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("mask", "mask", new StorageName());
    m_OptionManager.add("high", "high", false);
  }

  /**
   * Sets whether to use 0 (low) or 255 (high) as mask color.
   *
   * @param value	true if to use high mask color
   */
  public void setHigh(boolean value) {
    m_High = value;
    reset();
  }

  /**
   * Returns whether to use low/high mask color.
   *
   * @return		true if to use high mask color
   */
  public boolean isHigh() {
    return m_High;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String highTipText() {
    return "If enabled, uses 255 as mask color instead of 0.";
  }

  /**
   * Set the mask name in storage.
   *
   * @param value	the name
   */
  public void setMask(StorageName value) {
    m_Mask = value;
    reset();
  }

  /**
   * Getter method for the mask storage name.
   *
   * @return 		the mask name
   */
  public StorageName getMask() {
    return m_Mask;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maskTipText() {
    return "The name of the mask in internal storage.";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    ImageUInt8			maskImg;
    Object			obj;
    BoofCVImageContainer 	container;
    BufferedImage 		image;

    if (getFlowContext() == null)
      throw new IllegalStateException("No flow context set!");

    obj = getFlowContext().getStorageHandler().getStorage().get(m_Mask);
    if (obj == null)
      throw new IllegalStateException("Mask not available from storage: " + m_Mask);

    if (obj instanceof BufferedImage)
      maskImg = (ImageUInt8)BoofCVHelper.toBoofCVImage((BufferedImage) obj, BoofCVImageType.UNSIGNED_INT_8);
    else if (obj instanceof AbstractImageContainer)
      maskImg = (ImageUInt8)BoofCVHelper.toBoofCVImage((AbstractImageContainer) obj,BoofCVImageType.UNSIGNED_INT_8);
    else
      throw new IllegalStateException(
	"Mask must be either a " + BufferedImage.class.getName()
	  + " or derived from " + AbstractImageContainer.class.getName()
	  + ", found: " + obj.getClass().getName());

    container = new BoofCVImageContainer();
    container.setImage(maskImg);

    // check that images are the same size
    if(img.getHeight() != maskImg.getHeight() || img.getWidth() != maskImg.getWidth())
      throw new IllegalStateException("Mask and image must be the same size");

    if(!BoofCVHelper.isBinary(container))
      throw new IllegalStateException("Mask must be binary");

    image = img.toBufferedImage();
    int maskColour = 0;
    if(m_High)
      maskColour = 255;

    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
	if(maskImg.get(x,y) == maskColour) {
	  image.setRGB(x,y,0x00FFFFFF);
	}
      }
    }

    BoofCVImageContainer[] result = new BoofCVImageContainer[1];
    result[0] = new BoofCVImageContainer();
    result[0].setImage(BoofCVHelper.toBoofCVImage(image));
    return result;
  }
}
