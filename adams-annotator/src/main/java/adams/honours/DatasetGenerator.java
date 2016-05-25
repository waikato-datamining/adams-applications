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
 * DatasetGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.honours;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.ArffSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.env.Environment;
import adams.flow.transformer.TrailFileReader;

import java.nio.file.InvalidPathException;

/**
 * Writes data into an ARFF file for use with Weka
 *
 * @author sjb90
 * @version $Revision$
 */
public class DatasetGenerator {

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

    // Read in a trail file into a trail object
    SimpleTrailReader tReader = new SimpleTrailReader();
    tReader.setInput(new PlaceholderFile(args[0]));
    Trail trail = tReader.read().get(0);
    if(trail == null) {
      throw new InvalidPathException(args[0], "Not a valid trail file.");
    }

    // For each step in the trail we need to

    if(true)
    	return;

    SpreadSheet sheet = new DefaultSpreadSheet();
    Row row;

    // header
    row = sheet.getHeaderRow();
    row.addCell("file").setContentAsString("File");
    row.addCell("x").setContentAsString("X");
    row.addCell("y").setContentAsString("Y");
    row.addCell("class").setContentAsString("Class");

    // data
    for (int i = 0; i < 100; i++){
      row = sheet.addRow();
      row.addCell("file").setContentAsString("blah" + i + ".jpg");
      row.addCell("x").setContent(-i);
      row.addCell("y").setContent(i);
      row.addCell("class").setContentAsString(i % 3 == 0 ? "yes" : "no");
    }

    System.out.println(sheet);
    ArffSpreadSheetWriter writer = new ArffSpreadSheetWriter();
    writer.write(sheet, "/tmp/out.arff");
  }
}
