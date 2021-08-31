package edu.ohsu.dmice.cmp.coach.tools;

public class CardToRec {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: java -jar CardToRec.jar <XLSX file> <recommendation #>");
            System.out.println("       <XLSX file> : HBP CDS Cards v5 or greater Excel spreadsheet");
            System.out.println("       <recommendation #> : a value from Column A (Recommendation #) in the spreadsheet");
            System.exit(0);
        }

        String filename = args[0]; // excel file
        String targetRec = args[1]; // recommendation number

        new RecommendationGenerator(filename, targetRec).exec();
    }
}