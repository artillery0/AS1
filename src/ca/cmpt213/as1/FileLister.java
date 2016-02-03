package ca.cmpt213.as1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Creates a list of files in a specified folder, using specified extension
 * types.
 * Usage:    java ca.cmpt213.as1.FileLister <source folder> <target file> <filters>
 * 
 * Assume that the file name in the arguments contains no spaces.
 * @author Brian Fraser
 * 
 */
public class FileLister {
	private static final int MIN_COMMANDLINE_ARGUMENTS = 2;
	private static final int ARGUMENT_OFFSET_SOURCE = 0;
	private static final int ARGUMENT_OFFSET_TARGET = 1;
	private static final int ARGUMENT_OFFSET_FILEFILTERS = 2;
	private static final int KIBIBYTES = 1024;
	private static final int MEBIBYTES = KIBIBYTES * KIBIBYTES;

	private String sourceFileName;
	private String targetFileName;
	private ArrayList<String> fileExtensions;
	private FileList filesAccepted;

	// Static functions for execution:
	public static void main(String[] args) {
		try {
			runProgram(args);
		} catch (InvalidParameterException exception) {
			displayHelp();
		}
	}
	private static void runProgram(String[] args) {
		FileLister lister = new FileLister(args);
		
		lister.displayArguments();
		lister.displayAcceptedFileStatistics();
		lister.displayFileList();
		
		lister.writeFileListToOutputFile();
	}
	private static void displayHelp() {
		String className = FileLister.class.getName();
		System.out.println();
		System.out.println("Usage:");
		System.out.println("   java " + FileLister.class.getSimpleName() + " <source folder> <target file> <filters>");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("   java " + className + " C:\\Music\\ C:\\ouput.txt .mp3");
		System.out.println("   java " + className + " C:\\ C:\\test\\list.txt .mp3 .jpg");
		System.out.println();
	}
	
	
	public FileLister(String[] args) {
		if (hasTooFewCommandlineArguments(args)) {
			throw new InvalidParameterException("Too few arguments.");
		}
		sourceFileName = args[ARGUMENT_OFFSET_SOURCE];
		targetFileName = args[ARGUMENT_OFFSET_TARGET];
		fileExtensions = extractExtensions(args);
		
		filesAccepted = new FileList(sourceFileName, fileExtensions);
	}

	private boolean hasTooFewCommandlineArguments(String[] args) {
		return args.length < MIN_COMMANDLINE_ARGUMENTS;
	}

	private ArrayList<String> extractExtensions(String[] args) {
		ArrayList<String> extensions = new ArrayList<String>();
		for (int i = ARGUMENT_OFFSET_FILEFILTERS; i < args.length; i++) {
			fileExtensions.add(args[i]);
		}		
		return extensions;
	}
	
	private void displayArguments() {
		System.out.println("Statistics on Files Found:");
		System.out.println("**************************");
		System.out.println("Source Path:   " + sourceFileName);
		System.out.println("Target Path:   " + targetFileName);
		System.out.print  ("Extensions:    ");
		for (String extension : fileExtensions) {
			System.out.print(extension + " ");
		}
		System.out.println();
	}

	private void displayAcceptedFileStatistics() {
		long sizeInBytes = filesAccepted.getSizeOfAllFiles();
		double sizeInMiB = (double)sizeInBytes / MEBIBYTES;

		System.out.println("Files Found:   " + filesAccepted.getNumberFiles());
		System.out.printf("Total size:    %,.2f MiB (%,d bytes)%n", 
				sizeInMiB, sizeInBytes);
	}

	private void displayFileList() {
		System.out.println("");
		System.out.println("Files:");
		System.out.println("*****************");
		filesAccepted.outputToWriter(new PrintWriter(System.out));
		System.out.println();
	}

	private void writeFileListToOutputFile() {
		try {
			System.out.println("Writing file list to output file: "	+ targetFileName);
			File file = new File(targetFileName);
			PrintWriter writer = new PrintWriter(file);
			filesAccepted.outputToWriter(writer);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Unable to write to output file "	+ targetFileName);
			e.printStackTrace();
		}
	}
}
