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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Extract images given a list of timestamps
 *
 * @author sjb90
 * @version $Revision$
 */
public class ImageExtractor2 {

  private static final int BATCH_SIZE = 300;
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

  public ImageExtractor2() {
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
    ImageExtractor2 extractor = new ImageExtractor2();

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
      extractor.setimestamp(new BaseTimeMsec(s.getTimestamp()));
      BufferedImageContainer[] images = extractor.extract();
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
	    report.getStringValue("Timestamp") + "-" + index + ".png"));
	  reportWriter.setOutput(new PlaceholderFile(outputPath + extractor.getFile().getName() +
            report.getStringValue("Timestamp") + "-" + index + ".report"));
	  reportWriter.write(report);
	  index++;
	}
      } catch (Exception e) {
	System.out.println(e.toString());
      }

    }
    extractor.cleanup();
  }

  private void cleanup() {
    m_Sampler.cleanUp();
  }
}
