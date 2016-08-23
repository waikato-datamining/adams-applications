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
import adams.data.trail.Trail;
import adams.env.Environment;

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

    SpreadSheet sheetFrom = trail.toSpreadSheet();
    SpreadSheet sheetTo   = new DefaultSpreadSheet();
    Row fromHeaderRow = sheetFrom.getHeaderRow();
    Row toHeaderRow = sheetTo.getHeaderRow();

    for(String key : fromHeaderRow.cellKeys()) {
      toHeaderRow.addCell(key).setContentAsString(fromHeaderRow.getCell(key).getContent());
    }
    toHeaderRow.addCell("dist").setContentAsString("Distance");
    for(Row r : sheetFrom.rows()) {
      Row newRow = sheetTo.addRow();
      for(String key : r.cellKeys()) {
        newRow.addCell(key).setContent(r.getCell(key).getContent());
      }
    }



    for (int i = 1; i < sheetFrom.getRowCount(); i++) {
      // get the current row and the previous row
      Row currentRow = sheetFrom.getRow(i);
      Row previousRow = sheetFrom.getRow(i-1);
      double x1 = Double.parseDouble(previousRow.getCell("X").getContent());
      double y1 = Double.parseDouble(previousRow.getCell("Y").getContent());
      double x2 = Double.parseDouble(currentRow.getCell("X").getContent());
      double y2 = Double.parseDouble(currentRow.getCell("Y").getContent());
      double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
      Row destinationRow = sheetTo.getRow(i);
      destinationRow.getCell("dist").setContent(distance);
    }



    ArffSpreadSheetWriter writer = new ArffSpreadSheetWriter();
    writer.write(sheetTo, args[1]);
  }
}
