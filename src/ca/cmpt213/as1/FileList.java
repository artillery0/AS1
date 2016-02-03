package ca.cmpt213.as1;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


/**
 * Manages a set of files with operations including adding 
 * files/folders, reading/writing to a stream,
 * and getting the sum of all file sizes in the list.
 * @author Brian Fraser
 *
 */
public class FileList {
	private List<File> files = new ArrayList<File>();
	private Iterable<String> extensions;
	
	public FileList() {
	}
	public FileList(String sourceFileOrFolderName, Iterable<String> fileExtensions) {
		extensions = fileExtensions;
		
		addFileOrFolder(new File(sourceFileOrFolderName));
	}
	
	// Add files/folders
	public void addFileOrFolder(File file) {
		if (!file.exists()) {
			return;
		}
		
		if (file.isDirectory()) {
			addFolder(file);
		} else if (file.isFile()) {
			addFile(file);
		} else {
			throw new RuntimeException("Unknown file or folder type.");
		}
	}
	private void addFile(File file) {
		files.add(file);
	}
	private void addFolder(File directory) {
		FileFilter extensionFilter = createExtensionFilter();		
		File[] files = directory.listFiles(extensionFilter);
		for (File file: files) {
			addFileOrFolder(file);
		}
	}
	private FileFilter createExtensionFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return matchAllFiles() 
						|| pathname.isDirectory() 
						|| hasAcceptedExtension(pathname);
			}

			private boolean hasAcceptedExtension(File pathname) {
				String lowerName = pathname.getName().toLowerCase();
				for (String extension: extensions) {
					extension = extension.toLowerCase();
					if (lowerName.endsWith(extension)){
						return true;
					}
				}
				return false;
			}
		};
	}
	
	// Output Support
	public void outputToWriter(PrintWriter out) {
		for (File file : files) {
			out.println(file.getAbsolutePath());
		}
		out.flush();
	}
	
	// Helper Methods
	public int getNumberFiles() {
		return files.size();
	}
	public File getFileByIndex(int index) {
		return files.get(index);
	}
	public long getSizeOfAllFiles() {
		long sum = 0;
		for (File file : files) {
			sum += file.length();
		}
		return sum;
	}
	private boolean matchAllFiles() {
		return !extensions.iterator().hasNext();
	}
		
	public void sortBySize() {
		Comparator<File> comparator = createSizeComparator();
		java.util.Collections.sort(files, comparator);
	}
	private Comparator<File> createSizeComparator() {
		return new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				if (file1 == null || file2 == null) {
					return 0;
				}

				long sizeDiff = file1.length() - file2.length();
				return Long.signum(sizeDiff);
			}
		};
	}

	public static FileList makeFromTextFile(String inputFileName) throws IOException {
		FileReader fileReader = new FileReader(inputFileName);
		Scanner scanner = new Scanner(fileReader);
		
		try {
			return makeFromScanner(scanner);
		} finally {
			scanner.close();
		} 
	}
	private static FileList makeFromScanner(Scanner scanner) {
		FileList fileList = new FileList();
		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			File file = new File(nextLine);
			if (file.exists()) {
				fileList.addFile(file);
			} else {
				System.out.println("Warning: File does not exist (" + file.getAbsolutePath() + ").");
			}
		}
		return fileList;
	}
}
