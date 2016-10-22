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

import adams.core.Utils;
import adams.core.base.BaseTimeMsec;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingObject;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.env.Environment;
import adams.flow.transformer.movieimagesampler.SingleTimestampMovieSampler;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Extract images given a list of timestamps
 *
 * @author sjb90
 * @version $Revision$
 */
public class ImageExtractor3
  extends LoggingObject {
  private BaseTimeMsec m_Timestamp;
  private SingleTimestampMovieSampler m_Sampler;
  private PlaceholderFile m_CurrentFile;


  public BaseTimeMsec getTimestamp() {
    return m_Timestamp;
  }

  public void setimestamp(BaseTimeMsec timestamp) {
    m_Timestamp = timestamp;
  }

  public void setFile(PlaceholderFile file) {
    m_CurrentFile = file;
  }

  public PlaceholderFile getFile() {
    return m_CurrentFile;
  }

  public ImageExtractor3() {
    m_Sampler = new SingleTimestampMovieSampler();
  }

  public BufferedImageContainer[] extract() {
    m_Sampler.setTimeStamp(m_Timestamp);
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
    boolean dupe = false;
    SimpleTrailReader trailReader = new SimpleTrailReader();
    trailReader.setInput(new PlaceholderFile(args[1]));
    String outputPath = args[2];
    Trail t = trailReader.read().get(0);
    List<Step> steps = t.toList();
    DefaultSimpleReportWriter reportWriter = new DefaultSimpleReportWriter();
    int stepNum = 0;
    ImageExtractor3 extractor = null;
    for (Step s : steps) {
      //Utils.wait(extractor,1000,50);
      stepNum++;
      BufferedImageContainer[] images;
      if (extractor != null)
        extractor.cleanup();
      extractor = new ImageExtractor3();
      extractor.setFile(new PlaceholderFile(args[0]));
      extractor.setimestamp(new BaseTimeMsec(s.getTimestamp()));
      images = extractor.extract();
      while(images.length == 0) {
        dupe = true;
        Utils.wait(extractor,1000,50);
        System.out.println("STEP No Dupe" + stepNum + " : " + s);
        images = extractor.extract();
      }
      System.out.println("# images: " + images.length);
      try {
        int index = 0;
	for (int k = 0 ; k < 1;/*images.length;*/ k++) {
          Report report = images[0].getReport();

          // Add the annotation information to the report
          HashMap<String, Object> metaData = s.getMetaData();
          for(String metaKey : metaData.keySet()) {
            Field field;
            if (Utils.isBoolean("" + metaData.get(metaKey))) {
              field = new Field(metaKey, DataType.BOOLEAN);
              report.addField(field);
              report.setValue(field, "" + metaData.get(metaKey));
            }
            else if (Utils.isDouble("" + metaData.get(metaKey))) {
              field = new Field(metaKey, DataType.NUMERIC);
              report.addField(field);
              report.setValue(field, "" + metaData.get(metaKey));
            }
            else {
              field = new Field(metaKey, DataType.STRING);
              report.addField(field);
              report.setValue(field, "" + metaData.get(metaKey));
            }
          }
          ImageIO.write(images[0].getImage(), "png", new File(outputPath + extractor.getFile().getName() +
	    report.getStringValue("Timestamp") + "-" + index + "-" + stepNum +  ".png"));
	  reportWriter.setOutput(new PlaceholderFile(outputPath + extractor.getFile().getName() +
          report.getStringValue("Timestamp") + "-" + index + "-" + stepNum + ".report"));
          reportWriter.write(report);
	  index++;
          dupe = false;
	}
      } catch (Exception e) {
	System.out.println("EXCEPTION: " + e.toString() + " " + images.length);
      }
    }
    if (extractor != null)
    extractor.cleanup();
  }

  private void cleanup() {
    m_Sampler.cleanUp();
  }
}
