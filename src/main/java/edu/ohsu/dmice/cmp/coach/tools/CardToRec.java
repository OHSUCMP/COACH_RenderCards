package edu.ohsu.dmice.cmp.coach.tools;

/* SOME NOTES
- edu.ohsu.dmice.cmp.coach.tools.CardToRec.java accepts recommendation number as input (e.g. 12, 20a, etc., make sure it matches the number written in
CDS cards excel file) and outputs html file corresponding to the recommendation
- can comment ou call to writeLinksHtml() function if those links won't be displayed to patient (these are not the
counseling links)
- program throws exception if recommendation text is not written in excel file
- make sure column letters/numbers are updated to match excel file, and file path is updated
- "update-goal" type not written (since excel cells for type is N/A)
 */

import java.io.*;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CardToRec {
    private static String html = ""; // html for rec
    private static boolean twoRows = false; // if rec in excel has two rows for suggestions

    // UPDATE THESE VALUES IF COLUMN LETTERS CHANGE
    private static final int colF = 5; // rationale column # (change if column for rec changes)
    private static final int colK = 10; // indicator column #
    private static final int colE = 4; // summary column #
    private static final int colV = 21; // links column #
    private static final int colU = 20; // link label column #
    private static final int colR = 17; // suggestions v3 actions column #
    private static final int colP = 15; // suggestions v3 type column #
    private static final int colO = 14; // suggestions v3 label column #

    public static void main(String[] args) throws Exception {
        String targetRec = args[0]; // recommendation number
        // String targetRec = "23c";

        // [CHANGE PATH NAME FOR OWN EXCEL FILE]
        File cards = new File("/Users/elliechang/Desktop/HBP_CDS_Cards_v5.xlsx");

        // finds workbook instance for the xlsx file
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(cards));
        XSSFSheet sheet = wb.getSheetAt(0); // return first sheet from the xlsx file

        // finds row number for rec
        int rowTarget = findRowNum(sheet, targetRec);
        Row rowFound = sheet.getRow(rowTarget);
        Row rowNext = sheet.getRow(rowTarget+1);

        // writes html string
        writeRecHtml(rowFound, rowNext);

        // creates new html file and writes to it
        String newFile = targetRec+".html";
        FileWriter fw = new FileWriter(newFile);
        fw.write(html);
        fw.close();

        // prints out rec and html
        Cell cellRecFound = rowFound.getCell(colF);
        String rec = cellRecFound.getStringCellValue();
        System.out.println(rec+"\n");
        System.out.println(html);
    }

    // finds the row in the excel corresponding to the inputted rec number
    public static int findRowNum(XSSFSheet sheet, String targetRec) throws Exception {
        int rows = 60; // No. of rows (change to match number of recs)

        // Ensures that we get the data properly even if it doesn't start from first few rows
        for(int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            Cell recCell = row.getCell(0);
            Cell nextCell = sheet.getRow(i+1).getCell(0);
            switch (recCell.getCellType()) {
                case STRING:
                    String recStr = recCell.getStringCellValue();
                    if (recStr.equals(targetRec)) {
                        if ((nextCell == null || nextCell.getCellType() == CellType.BLANK) && i+1 != rows) {
                            twoRows = true;
                        }
                        return i;
                    }
                    break;
                case NUMERIC:
                    if (isNumeric(targetRec)) {
                        double targetRecNum = Double.parseDouble(targetRec);
                        double recNum = recCell.getNumericCellValue();
                        if (Double.compare(recNum, targetRecNum) == 0) {
                            if ((nextCell == null || nextCell.getCellType() == CellType.BLANK) && i+1 != rows) {
                                twoRows = true;
                            }
                            return i;
                        }
                        break;
                    }
            }
        }
        throw new Exception("Recommendation number does not exist. Please " +
                "double check the Excel file for correct number.");
    }

    // returns true if rec number is numeric; false if rec is a string
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    // writes the html for the recommendation
    public static void writeRecHtml(Row rowFound, Row rowNext) {
        Cell cellRecFound = rowFound.getCell(colF);
        String rationale = cellRecFound.getStringCellValue();

        html += "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" +
                "    <link rel=\"stylesheet\" href=\"recommendations.css\">\n" + "</head>";

        String indicator = rowFound.getCell(colK).getStringCellValue();
        String summary = rowFound.getCell(colE).getStringCellValue();
        String links = rowFound.getCell(colV).getStringCellValue();
        String linkLabels = rowFound.getCell(colU).getStringCellValue();
        html += "<div class='card " + indicator + "'>\n";
        html += "<table style='width:100%'><tr><td>\n";
        html += "<div class='circle'><span>XX</span></div>\n";
        html += "</td><td>\n";
        html += "<div class='content'>\n";
        html += "<span class='summary heading'>" + summary + "</span>\n";
        if (!rationale.isBlank()) {
            html += "<span class='rationale'>" + rationale + "</span>\n";
        }
        if (!links.isBlank()) {
            html += "<div class='links'>";
            writeLinksHtml(links, linkLabels);
        }
        writeActionsHtml(rowFound, rowNext);
    }

    // writes links from columns U and V (not updated in excel yet)
    public static void writeLinksHtml(String links, String linkLabels) {
        // links and labels in excel need to be separated by new line
        String linkLabel;
        String[] linksArr = links.split("\n");
        String[] linkLabelsArr = linkLabels.split("\n");
        for (int i = 0; i < linksArr.length; i++) {
            try {
                linkLabel = linkLabelsArr[i];
            } catch(ArrayIndexOutOfBoundsException ai) {
                linkLabel = "missing link label";
            }
            html += "<a class='link' href='" + linksArr[i] + "'>" + linkLabel + "</a>\n";
        }
        html += "</div>\n";
    }

    // writes html for "goal" type
    private static void writeActionsHtml(Row rowFound, Row rowNext) {
        // assumes any type can be first or second row
        // no "update-goal" type case because excel cards don't have this filled in yet
        String actionOne = rowFound.getCell(colR).getStringCellValue();
        String typeOne = rowFound.getCell(colO).getStringCellValue();
        String labelOne = rowFound.getCell(colP).getStringCellValue();
        actionsHelper(actionOne, typeOne, labelOne);
        if (twoRows) {
            String actionTwo = rowNext.getCell(colR).getStringCellValue();
            String typeTwo = rowNext.getCell(colO).getStringCellValue();
            String labelTwo = rowNext.getCell(colP).getStringCellValue();
            actionsHelper(actionTwo, typeTwo, labelTwo);
        }
    }

    // helper function to help write goals
    private static void actionsHelper(String action, String type, String label) {
        if (!action.isBlank() && !type.isBlank() && !label.isBlank()) {
            switch (type) {
                case "counseling-link":
                    writeCounselingHtml(action, label);
                    break;
                case "goal":
                    writeGoalsHtml(action, label);
                    break;
                case "suggestion-link":
                    writeSuggestionLinksHtml(action, label);
                    break;
                default:
                    System.out.println("goals/suggestions need to be updated");
                    break;
            }
        }
    }

    // writes html for "counseling-link" type
    public static void writeCounselingHtml(String action, String label) {
        if (action.contains("label") && action.contains("url")) {
            String[] found = findUrlLabel(action);
            String actionLabel = found[0];
            String actionUrl = found[1];
            html += "<br><span class='label heading'>" + label + "</span>";
            html += "<ul class='actions'>";
            html += "<li class='action'><a href='" + actionUrl + "'>" + actionLabel + "</a></li>";
            html += "</ul>";
        }
        html += "</div>\n";
    }

    // writes the html for "suggestion-link" type
    private static void writeSuggestionLinksHtml(String action, String label) {
        html += "</td><td>\n";
        if (action.contains("label") && action.contains("url")) {
            String[] found = findUrlLabel(action);
            String actionLabel = found[0];
            String actionUrl = found[1];
            html += "<div style='background-color: #dde9f8 ; padding: 10px'>\n";
            html += "<span class='label heading'>" + label + "</span>";
            html += "<table><tr>";
            html += "<td><div class='action'><a href='" + actionUrl + "'>" + actionLabel + "</a></td>\n";
            html += "</tr>";

            html += "</table>";
            html += "</div></div>\n";
        }
    }

    private static void writeGoalsHtml(String action, String label) {
        html += "</td><td>\n";
        if (!label.equals("BPGoal")) {
            html += "<div style='background-color: #dde9f8 ; padding: 10px'>\n";
            html += "<span class='label heading'>" + label + " (freetext) </span>";
            html += "<table><tr>";
            html += "<td>";

            // freeform input (not for BPgoal)
            html += "<div class='action'>";
            html += "<input type='text' class='freetextResponse' placeholder='Describe your goal here' />";
            finishGoalHtml();
        }
        if (action.contains("label")){
            // predefined multiple-choice goal, these are radio buttons
            Random rand = new Random();
            int x = rand.nextInt(5);
            html += "<br><div style='background-color: #dde9f8 ; padding: 10px'>\n";
            html += "<span class='label heading'>" + label + " (choice) </span> <br>";
            html += "<table><tr>";
            html += "<td>";
            html += "<div class='action'>";

            String[] labels = action.split("\"");
            for (int i = 0; i < labels.length; i++) {
                if (labels[i].equals("label")) {
                    String actionLabel = labels[i+2];
                    html += "<input name='action" + x + "' type='radio' id='action" + x + "_" + i + "' value='" + actionLabel + "' />";
                    html += "<label for='action" + x + "_" + i + "'>" + actionLabel + "</label><br>\n";
                }
            }
            finishGoalHtml();
        }
    }

    // helper method to finish writing "commit to goal" button and set goal date
    private static void finishGoalHtml() {
        html += "</div>\n";
        html += "</td>";
        html += "<td><div class='commitToGoalButton'><span>Commit to Goal</span></div></td>\n";
        html += "</tr>";

        Random rand = new Random();
        int x = rand.nextInt(5);
        html += "<td><label for='goalTargetDate" + x + "'>When do you want to achieve this goal?</label></td>";
        html += "<td><input id='goalTargetDate" + x + "' type='text' class='goalTargetDate' placeholder='--Select Date--' readOnly/></td>";
        html += "</tr>";

        html += "</table>";
        html += "</div>\n";
    }

    // finds url and url label from excel cell and returns string array found with label in 0th
    // index and url in 1st index
    private static String[] findUrlLabel(String action) {
        String[] found = new String[2];
        String[] counselInfo = action.split("\"");
        for (int i = 0; i < counselInfo.length; i++) {
            if (counselInfo[i].equals("label")) found[0] = counselInfo[i + 2];
            if (counselInfo[i].equals("url")) found[1] = counselInfo[i + 2];
        }
        return found;
    }
}
