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
 * ReportToArff.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.honours;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.ArffSpreadSheetReader;
import adams.data.io.output.ArffSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import org.openimaj.math.geometry.shape.Rectangle;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Takes an arff file and calculates some extra features
 *
 * @author sjb90
 * @version $Revision$
 */
public class CalculateFeatures {

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ArffSpreadSheetReader reader = new ArffSpreadSheetReader();
    SpreadSheet ss = reader.read(new PlaceholderFile(args[0]));
    SpreadSheet result = addFeatures(ss);

    ArffSpreadSheetWriter writer = new ArffSpreadSheetWriter();
    writer.write(result, new PlaceholderFile(args[1]));
  }

  private static SpreadSheet addFeatures(SpreadSheet ss) {
    SpreadSheet result = new DefaultSpreadSheet();
    // get the header row of the input spreadsheet
    Row inputHeader = ss.getHeaderRow();
    //get the header row of the output spreadsheet
    Row outputHeader = result.getHeaderRow();
    String socialKey = "";
    Dictionary<String, String> keys = new Hashtable<>();
    for(String key : inputHeader.cellKeys()) {
     // System.out.println(inputHeader.getCell(key).getContent());
      keys.put(inputHeader.getCell(key).getContent(), key);
      if(inputHeader.getCell(key).getContent().equals("dataset-1-Social")) {
	System.out.println(inputHeader.getCell(key).getContent());
	socialKey = key;
      }
      outputHeader.addCell(key).setContentAsString(inputHeader.getCell(key).getContent());
    }
    outputHeader.addCell("dist").setContentAsString("Distance");
    outputHeader.addCell("boundH").setContentAsString("Bound Height");
    outputHeader.addCell("boundW").setContentAsString("Bound Width");
    outputHeader.addCell("boundD").setContentAsString("Bound Diagonal");
    outputHeader.removeCell(socialKey);
    outputHeader.addCell(socialKey).setContentAsString("dataset-1-Social");
    for(Row row : ss.rows()) {
      if(row == inputHeader)
	continue;
      Row resultRow = result.addRow();
      for(String key : row.cellKeys()) {
	resultRow.addCell(key).setContent(row.getCell(key).getContent());
      }
      // Calculate distance. For this we need to make use of the number of objects and their coordinates
      resultRow.addCell("dist").setContent(getDistance(row, keys));
      // Calculate the size of the bounding rectangle around both objects. Store width, height and diagonal of this box
      Rectangle bound 	= getSuperBound(row, keys);
      Double	diag	= Math.hypot(bound.width, bound.height);
      resultRow.addCell("boundW").setContent(bound.width);
      resultRow.addCell("boundH").setContent(bound.height);
      resultRow.addCell("boundD").setContent(diag);
    }
    return result;
  }

//  private static SpreadSheet toSpreadSheet(List<Report> reports) {
//    // Create a spreadsheet
//    SpreadSheet result = new DefaultSpreadSheet();
//    // Get the header row
//    Row headerRow = result.getHeaderRow();
//    // Loop through all reports and make sure all possible fields are added to the spreadsheet
//    reports.stream().forEach(report1 -> {
//      report1.getFields().
//        stream().
//        forEach(abstractField -> headerRow.addCell(abstractField.toString()).setContentAsString(abstractField.toString()));
//    });
//    headerRow.addCell("dist").setContentAsString("Distance");
//    headerRow.addCell("boundH").setContentAsString("Bound Height");
//    headerRow.addCell("boundW").setContentAsString("Bound Width");
//    headerRow.addCell("boundD").setContentAsString("Bound Diagonal");
//    headerRow.removeCell("Social");
//    headerRow.addCell("Social").setContentAsString("Social");
//
//    for(Report report : reports) {
//      if(report.getDoubleValue("Object.count") > 2 || report.getDoubleValue("Object.count") < 1)
//	continue;
//      Row row = result.addRow();
//      List<AbstractField> fields = report.getFields();
//      for (AbstractField f : fields) {
//	Cell cell;
//	switch (f.getDataType()) {
//	  case NUMERIC:
//	    cell = row.addCell(f.toString());
//	    if(cell == null) {
//	      System.out.println(f.toString());
//	      System.out.println("cell is null");
//	      System.out.println(report.getStringValue("Timestamp"));
//	      continue;
//	    }
//	    cell.setContent(report.getDoubleValue(f));
//	    break;
//	  case BOOLEAN:
//	    cell = row.addCell(f.toString());
//	    if(cell == null) {
//	      System.out.println("cell is null");
//	      System.out.println(report.getStringValue("Timestamp"));
//	      continue;
//	    }
//            System.out.println(report.getBooleanValue(f));
//	    cell.setContent(report.getBooleanValue(f));
//	    break;
//	  default:
//	    cell = row.addCell(f.toString());
//	    if(cell == null) {
//	      System.out.println(f.toString());
//	      System.out.println("cell is null");
//	      System.out.println(report.getStringValue("Timestamp"));
//	      continue;
//	    }
//	    cell.setContent(report.getStringValue(f));
//	}
//      }
//      // Calculate distance. For this we need to make use of the number of objects and their coordinates
//      row.addCell("dist").setContent(getDistance(report, keys));
//      // Calculate the size of the bounding rectangle around both objects. Store width, height and diagonal of this box
//      Rectangle bound 	= getSuperBound(report, keys);
//      Double	diag	= Math.hypot(bound.width, bound.height);
//      row.addCell("boundW").setContent(bound.width);
//      row.addCell("boundH").setContent(bound.height);
//      row.addCell("boundD").setContent(diag);
//    }
//    return result;
//  }

  private static double getDistance(Row row, Dictionary<String, String> keys) {
    double result = 0.0;
    if(row.getCell(keys.get("dataset-2-BIMoment")).getContent().equals("?")) {
      result = 0;
    }
    else {
      // Find middle of the rectangles
      double o1X = Double.parseDouble(row.getCell(keys.get("dataset-1-X")).getContent());
      double o1Y = Double.parseDouble(row.getCell(keys.get("dataset-1-Y")).getContent());
      double o1H = Double.parseDouble(row.getCell(keys.get("dataset-1-Height")).getContent());
      double o1W = Double.parseDouble(row.getCell(keys.get("dataset-1-Width")).getContent());

      double o2X = Double.parseDouble(row.getCell(keys.get("dataset-2-X")).getContent());
      double o2Y = Double.parseDouble(row.getCell(keys.get("dataset-2-Y")).getContent());
      double o2H = Double.parseDouble(row.getCell(keys.get("dataset-2-Height")).getContent());
      double o2W = Double.parseDouble(row.getCell(keys.get("dataset-2-Width")).getContent());
      double o1MX = o1X + (o1W/2);
      double o1MY = o1Y + (o1H/2);

      double o2MX = o2X + (o2W/2);
      double o2MY = o2Y + (o2H/2);
      result = Math.sqrt(Math.pow(o2MX - o1MX, 2) + Math.pow(o2MY - o1MY,2));
    }
    return result;
  }

  private static Rectangle getSuperBound(Row row, Dictionary<String, String> keys) {
    Rectangle result;
    Rectangle obj1 = new Rectangle(Float.parseFloat(row.getCell(keys.get("dataset-1-X")).getContent()),
      Float.parseFloat(row.getCell(keys.get("dataset-1-Y")).getContent()),
      Float.parseFloat(row.getCell(keys.get("dataset-1-Width")).getContent()),
      Float.parseFloat(row.getCell(keys.get("dataset-1-Height")).getContent()));
    if(row.getCell(keys.get("dataset-2-BIMoment")).getContent().equals("?")) {
      result = obj1;
    }
    else {
      Rectangle obj2 = new Rectangle(Float.parseFloat(row.getCell(keys.get("dataset-2-X")).getContent()),
	Float.parseFloat(row.getCell(keys.get("dataset-2-Y")).getContent()),
	Float.parseFloat(row.getCell(keys.get("dataset-2-Width")).getContent()),
	Float.parseFloat(row.getCell(keys.get("dataset-2-Height")).getContent()));
      result = obj1.union(obj2);
    }
    return result;
  }
}
