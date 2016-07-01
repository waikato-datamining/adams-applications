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
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.env.Environment;
import adams.flow.transformer.movieimagesampler.TimestampMovieSampler;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract images given a list of timestamps
 *
 * @author sjb90
 * @version $Revision$
 */
public class ImageExtractor {

  private static final int BATCH_SIZE = 300;
  private BaseTimeMsec[] m_Timestamps;
  private TimestampMovieSampler m_Sampler;
  private PlaceholderFile m_CurrentFile;


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
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ImageExtractor extractor = new ImageExtractor();
    extractor.setFile(new PlaceholderFile(args[0]));
    SimpleTrailReader trailReader = new SimpleTrailReader();
    trailReader.setInput(new PlaceholderFile(args[1]));
    String outputPath = args[2];
    Trail t = trailReader.read().get(0);
    List<Step> steps = t.toList();
    List<BaseTimeMsec> ts = new ArrayList<>(steps.size());
    BaseTimeMsec[] tsa = new BaseTimeMsec[BATCH_SIZE];
    DefaultSimpleReportWriter reportWriter = new DefaultSimpleReportWriter();
    for (Step s : steps) {
      System.out.println(s);
      ts.add(new BaseTimeMsec(s.getTimestamp()));
    }
    int index = 0;
    // Because we might have too many images
    while (!ts.isEmpty()) {
      List<BaseTimeMsec> timeStampSubList = new ArrayList<>();
      while(!ts.isEmpty() && timeStampSubList.size() < BATCH_SIZE) {
	timeStampSubList.add(ts.get(0));
	ts.remove(0);
      }
      extractor.setimestamps(timeStampSubList.toArray(tsa));
      BufferedImageContainer[] images = extractor.extract();
      try {

	for (int k = 0 ; k < images.length; k++) {
	  //System.out.println("Image " + images[i].getReport().getStringValue("Timestamp"));
	  ImageIO.write(images[k].getImage(), "png", new File(outputPath + extractor.getFile().getName() +
	    images[k].getReport().getStringValue("Timestamp") + "-" + index + ".png"));
	  reportWriter.setOutput(new PlaceholderFile(outputPath + extractor.getFile().getName() +
	    images[k].getReport().getStringValue("Timestamp") + "-" + index + ".report"));
	  reportWriter.write(images[k].getReport());
	  index++;
	}
      } catch (Exception e) {
	System.out.println(e.toString());
      }
    }
  }
}
