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
 * ImageExtractor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.honours;

import adams.core.base.BaseTimeMsec;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.env.Environment;
import adams.flow.transformer.movieimagesampler.TimestampMovieSampler;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Extract images given a list of timestamps
 *
 * @author sjb90
 * @version $Revision$
 */
public class ImageExtractor {

  private BaseTimeMsec[] 	m_Timestamps;
  private TimestampMovieSampler m_Sampler;
  private PlaceholderFile	m_CurrentFile;


  public BaseTimeMsec[] getTimestamps() {
    return m_Timestamps;
  }

  public void setimestamps(BaseTimeMsec[] timestamps) {
    m_Timestamps = timestamps;
  }

  public void setFile(PlaceholderFile file) {
    m_CurrentFile = file;
  }

  public PlaceholderFile getFile() {
    return m_CurrentFile;
  }

  public ImageExtractor() {
    m_Sampler = new TimestampMovieSampler();
  }

  public BufferedImageContainer[] extract() {
    m_Sampler.setTimeStamps(m_Timestamps);
    return m_Sampler.sample(m_CurrentFile);
  }

  /**
   * Main method for running as standalone
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ImageExtractor extractor = new ImageExtractor();
    extractor.setFile(new PlaceholderFile(args[0]));
    BaseTimeMsec[] ts = new BaseTimeMsec[6];
    for (int i = 0; i < ts.length / 2; i++) {
      for (int j = 0; j < 2; j++) {
	String time = "00:0" + i + ":" + j * 3 + "0.000";
	ts[i*2+j] = new BaseTimeMsec(time);
	System.out.println(ts[i*2+j]);
	System.out.println(ts[i*2+j].dateValue());
	System.out.println(ts[i*2+j].dateValue().getTime());
      }
    }
    extractor.setimestamps(ts);

    BufferedImageContainer[] images = extractor.extract();
    try {
      DefaultSimpleReportWriter reportWriter = new DefaultSimpleReportWriter();
      for (int i = 0; i < images.length; i++) {
        System.out.println("Image " + images[i].getReport().getStringValue("Timestamp"));
        ImageIO.write(images[i].getImage(), "png", new File("/home/sjb90/Pictures/testImage" +
          images[i].getReport().getStringValue("Timestamp") + "-" + i + ".png"));
	reportWriter.setOutput(new PlaceholderFile("/home/sjb90/Pictures/testImage" +
          images[i].getReport().getStringValue("Timestamp") + "-" + i+ ".report"));
	reportWriter.write(images[i].getReport());
      }
    }
    catch (Exception e) {

    }

  }
}
