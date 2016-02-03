package ca.cmpt213.as1;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Program to read a list of files from an input text file and randomly 
 * assign those files to different file-collections while limiting the size (in bytes) 
 * of each collection.
 * 
 * Usage: java ca.cmpt213.as1.FileCollector <# collections> <bytes per collection> <input list file>
 * 
 * Assume that the file name in the arguments contains no spaces.
 * @author Brian Fraser
 *
 */
public class FileCollector {
	private static final int REQUIRED_NUMBER_ARGUMENTS = 3;
	private static final int ARGUMENT_INDEX_NUM_COLLECTIONS = 0;
	private static final int ARGUMENT_INDEX_COLLECTION_SIZE = 1;
	private static final int ARGUMENT_INDEX_SOURCE_FILE = 2;
	private final static int KIBIBYTES = 1024;
	private final static int MEBIBYTES = KIBIBYTES * KIBIBYTES;
	private final static int FAIL_EXIT = 1;

	public static void main(String[] args) {
		if (incorrectNumberArguments(args)) {
			displayHelp();
			return;
		} 
		
		// Extract arguments
		int numberCollections = (int)convertPositiveLongOrExit(args[ARGUMENT_INDEX_NUM_COLLECTIONS]);
		long eachCollectionSize = convertPositiveLongOrExit(args[ARGUMENT_INDEX_COLLECTION_SIZE]);
		String sourceFilePath = extractExistingFilenameOrExit(args[ARGUMENT_INDEX_SOURCE_FILE]);
		displayArguments(numberCollections, eachCollectionSize, sourceFilePath);

		// Create FileList from the input file.
		FileList inputFileList = makeFileListFromFileOrExit(sourceFilePath);
		
		// Heuristic: Add the largest files first, so sort by size.
		inputFileList.sortBySize();
		
		// Process each element, assigning it to a list.
		FileListManager collection = new FileListManager(numberCollections, eachCollectionSize);
		FileList extras = new FileList(); 
		assignFilesToManager(inputFileList, collection, extras);
		
		// Output:
		collection.sortEachFileListBySize();
		displayCollections(collection, extras);
	}

	private static FileList makeFileListFromFileOrExit(String sourceFilePath) {
		try {
			return  FileList.makeFromTextFile(sourceFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	private static boolean incorrectNumberArguments(String[] args) {
		return args.length != REQUIRED_NUMBER_ARGUMENTS;
	}

	private static void displayHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("   java ca.cmpt213.as1.FileCollector <# collections> <bytes per collection> <input list file>");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("   java ca.cmpt213.as1.FileCollector 3 1024 daFiles.txt");
		System.out.println("   java ca.cmpt213.as1.FileCollector 10 1048576 c:\\files\\input.txt");
		System.out.println();
	}
	
	private static long convertPositiveLongOrExit(String source) {
		try {
			long value = Long.parseLong(source);
			if (value < 0) {
				exitForLongParameterProblem();
			}
			return value;
		} catch (NumberFormatException exception) {
			exitForLongParameterProblem();
		}
		return 0;
	}
	private static void exitForLongParameterProblem() {
		System.out.println("ERROR: Number of collections, and size of collections must non-negative integers.");
		System.exit(FAIL_EXIT);
	}

	private static String extractExistingFilenameOrExit(String sourceFilePath) {
		File sourceFile = new File(sourceFilePath);
		boolean isNotFound =!sourceFile.exists(); 
		boolean isNotFile = !sourceFile.isFile();
		if (isNotFound || isNotFile) {
			System.out.println("Error: Source file not found or not a valid file (" + sourceFilePath + ").");
			System.exit(FAIL_EXIT);
		}
		return sourceFilePath;
	}

	private static void displayArguments(int numberCollections,	long eachCollectionSize, String fileList) {
		System.out.println("Now building collection:");
		System.out.println("**************************");
		System.out.println("# Collections:       " + numberCollections);
		System.out.println("Size per Collection: " + eachCollectionSize);
		System.out.println("Source file list:    " + fileList);
		System.out.println();
		
	}

	private static void assignFilesToManager(FileList inputFileList,
			FileListManager collection, FileList extras) {
		// Suggestion: Change to an iterator.
		for (int fileIndex = 0; fileIndex < inputFileList.getNumberFiles(); fileIndex++) {
			File nextFile = inputFileList.getFileByIndex(fileIndex);
			try {
				collection.randomlyAssignFile(nextFile);
			} catch (NoRoomInListsException exception) {
				extras.addFileOrFolder(nextFile);
			}
		}
	}

	private static void displayCollections(FileListManager collection, FileList extras) {
		// Suggested change: Use an iterator.
		for (int i = 0; i < collection.getNumberFileLists(); i++) {
			FileList list = collection.getFileList(i);
			displayFileList("Collection " + (i + 1), list);
		}
		displayFileList("Extra Files", extras);
	}

	private static void displayFileList(String listDescription, final FileList list) {
		long sizeInBytes = list.getSizeOfAllFiles();
		double sizeInMiB = (double) sizeInBytes / MEBIBYTES;
		
		System.out.printf("%s: %,.2f MiB (%,d bytes)%n",
				listDescription,
				sizeInMiB,
				sizeInBytes);
		System.out.println("**************************************************");
		list.outputToWriter(new PrintWriter(System.out));
		System.out.println();
	}

}
