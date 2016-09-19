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
 * Blah.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.honours;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.SimpleTrailWriter;
import adams.data.trail.Step;
import adams.data.trail.StepComparator;
import adams.data.trail.Trail;
import adams.env.Environment;

import java.util.Collections;
import java.util.List;

/**
 * Takes two trail files and combines them into one
 *
 * @author steven
 * @version $Revision$
 */
public class TrailCombiner {
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    // read in trail files
    Trail trackingTrail;
    Trail annotationTrail;
    Trail result = new Trail();
    SimpleTrailReader tReader = new SimpleTrailReader();
    tReader.setInput(new PlaceholderFile(args[0]));
    trackingTrail = tReader.read().get(0);
    tReader.setInput(new PlaceholderFile(args[1]));
    annotationTrail = tReader.read().get(0);
    // We need to find the closest matching timestamp
    List<Step> annotationList = annotationTrail.toList();
    StepComparator comp = new StepComparator();
    for(Step s : trackingTrail) {
      // Find the position this should fit in the annotationList
      int index = Collections.binarySearch( annotationList, s, comp);
      if(index < 0)
        index = -index;
      System.out.println(index);
      // next compare to the index and the index + 1
      // Check that index +1 is not out of bound
      int next = index + 1;
      if(next < annotationList.size()) {
	// If it is not out of bounds then find out which is the closest timestamp
	Step closest = getClosest(s, annotationList.get(index), annotationList.get(index + 1));
	// then add the metadata to the tracking step
	s.getMetaData().putAll(closest.getMetaData());
	// and add it to the result trail
	result.add(s);
      }
    }
    //result.addAll(annotationTrail);
    SimpleTrailWriter tWriter = new SimpleTrailWriter();
    // Write out the combined trails
    tWriter.setOutput(new PlaceholderFile(args[2]));
    tWriter.write(result);
  }

  private static Step getClosest(Step trackingStep, Step currentAnnotation, Step nextAnnotation ) {
    Step closest = null;
    long trackingTime	= trackingStep.getTimestamp().getTime();
    long nextTime 	= nextAnnotation.getTimestamp().getTime();
    long currentTime	= currentAnnotation.getTimestamp().getTime();
    if(Math.abs(currentTime - trackingTime) < Math.abs(nextTime - trackingTime))
      closest = currentAnnotation;
    else
      closest = nextAnnotation;
    return closest;
  }
}
