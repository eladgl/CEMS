package Client;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import entities.GradedTest;

public class WordDocument {
	/**
	 * Generates a Word document based on the provided GradedTest object and saves it to the specified path.
	 *
	 * @param path     The path where the Word document will be saved.
	 * @param wordFile The GradedTest object containing the data for the Word document.
	 * @param testName The name of the test for the document headline.
	 */
	public static void GeneratedWord(String path, GradedTest wordFile, String testName) {
		try {

			XWPFDocument document = new XWPFDocument();
			FileOutputStream out = new FileOutputStream(new File(path));
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			
			// Headline
			XWPFRun run = paragraph.createRun();
			run.setBold(true);
			run.setItalic(true);
			run.setColor("7D7D7D"); // Grey
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.setText(testName);
			run.setFontSize(24);
			run.addBreak();

			paragraph = document.createParagraph();
			run = paragraph.createRun();
			run.setBold(true);
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
			run.setFontSize(18);
			run.setText("Username: "+wordFile.getName());
			run.addBreak();
			run.setText("Final Grade: \n" + wordFile.getGrade());
			
			// main body
			for (int i = 0; i < wordFile.getQuestions().size(); i++) {

				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);

				run = paragraph.createRun();
				run.setBold(true);
				run.setUnderline(UnderlinePatterns.SINGLE);
				run.setText((i+1)+". "+wordFile.getQuestions().get(i));
				run.setFontSize(18);
	
				run = paragraph.createRun();
				run.setFontSize(10);
				run.addBreak();
				run.addBreak();
				
				//options
				for(int j=0; j<wordFile.getOptions().get(i).size(); j++) {
					run = paragraph.createRun();
					run.setBold(true);
					run.setFontSize(15);
					if (wordFile.getCorrectAnswers()[i].equals(Integer.toString(j+1)))
						run.setColor("1ba813"); // green
					else if (wordFile.getStudentAnswers()[i].equals(Integer.toString(j+1)))
						run.setColor("ff0000"); // red
					else
						run.setColor("000000"); // black
					run.setText("   • "+wordFile.getOptions().get(i).get(j));
					run.addBreak();
				}
				
				run = paragraph.createRun();
				run.setFontSize(8);
				run.addBreak();
				
				//comment
				run = paragraph.createRun();
				run.setBold(true);
				run.setFontSize(15);
				run.setColor("2F5496"); //dark blue
				run.addTab();
				run.setText(wordFile.getQuestionsComments()[i]);
				run.addBreak();
			}
			try {
			    // ...
			    document.write(out);
			    document.close(); // Close the document
			    out.close();
			    System.out.println("created!");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}