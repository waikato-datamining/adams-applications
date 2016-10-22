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
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.boofcv.transformer.AbstractBoofCVTransformer;
import adams.data.image.AbstractImageContainer;
import adams.flow.control.StorageName;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class BinaryMask extends AbstractBoofCVTransformer {

  /** the image in internal storage. */
  protected StorageName m_Mask;

  public boolean isHigh() {
    return m_High;
  }

  public void setHigh(boolean m_High) {
    this.m_High = m_High;
  }

  /** use high or low value for mask **/
  protected boolean m_High;
  /**
   * Getter method for the mask
   * @return the mask name
   */
  public StorageName getMask() {
    return m_Mask;
  }

  /**
   * Set the mask name
   * @param m_Mask
   */
  public void setMask(StorageName m_Mask) {
    this.m_Mask = m_Mask;
  }

  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("mask", "mask", new StorageName());
    m_OptionManager.add("high", "high", false);
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
    BoofCVImageContainer 	container = new BoofCVImageContainer();
    BufferedImage image;

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
    container.setImage(maskImg);

    // check that images are the same size
    if(img.getHeight() != maskImg.getHeight() || img.getWidth() != maskImg.getWidth())
      throw new IllegalStateException("Mask and image must be the same size");

    if(!BoofCVHelper.isBinary(container))
      throw new IllegalStateException("Mask must be binary");
    byte[] data = maskImg.getData();


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

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Masks an image using a provided binary mask";
  }
}
