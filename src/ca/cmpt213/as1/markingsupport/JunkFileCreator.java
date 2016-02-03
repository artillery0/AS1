package ca.cmpt213.as1.markingsupport;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

/**
 * Create a file of specific name and size that contains garbage ('*'s).
 * 
 * For generating files automatically, the "file" input argument is used an the 
 * starting direcotry (automatically stripping off the file name).
 * @author Brian Fraser
 *
 */
public class JunkFileCreator {

	private static final int VALUE_FOR_FILE = 42;

	public static void main(String[] args) throws IOException {
		// Help
		if (args.length < 2) {
			System.out.println("Usage:   java ca.cmpt213.as1.JunkFileCreator <name> <size [bytes]>");
			System.out.println("Example: java ca.cmpt213.as1.JunkFileCreator junk1.xyz 1024");
			return;
		}

		// Process Arguments
		File outFile = new File(args[0]);
		long numBytes = Long.parseLong(args[1]);

		// Generate file (pass file, which may become just the path).
//		generateFile(outFile, numBytes);
//		generate80to10Files(outFile);
//		generate500to100ByteFiles(outFile);
		makeRandomFiles(outFile);
	}

	private static void generate80to10Files(File outFile) throws IOException {
		File targetDir = outFile.getParentFile();
		for (int i = 80; i >= 10; i -= 10) {
			String fileName = "\\Small file (" + i + " bytes).txt";
			File target = new File(targetDir.getAbsolutePath() + fileName);
			generateFile (target, i);
		}
	}

	private static void generate500to100ByteFiles(File outFile) throws IOException {
		final int FILE_SIZE = 100;
		File targetDir = outFile.getParentFile();
		for (int i = 1; i <= 500; i++) {
			String fileName = "\\Many Files # " + i + ".txt";
			File target = new File(targetDir.getAbsolutePath() + fileName);
			generateFile (target, FILE_SIZE);
		}
	}
	
	private static void makeRandomFiles(File outFile) throws IOException {
		final long MEBIBYTE = 1024*1024;
		final long MAX_FILE_SIZE = MEBIBYTE * 5;
		final long MIN_FILE_SIZE = (long)(0.5 * MEBIBYTE);
		final long FILE_SIZE_RANGE = MAX_FILE_SIZE - MIN_FILE_SIZE;
		final int NUM_FILES = 50;
		
		Random rand = new Random();
		
		File targetDir = outFile.getParentFile();
		for (int i = 1; i <= NUM_FILES; i++) {
			String fileName = "\\Rand Song Size # " + i + ".mp3";
			long size = Math.abs(rand.nextLong()) % FILE_SIZE_RANGE + MIN_FILE_SIZE;
			
			File target = new File(targetDir.getAbsolutePath() + fileName);
			generateFile (target, size);
		}
	}
	
	private static void generateFile(File outFile, long numBytes)
			throws IOException {

		final int BLOCK_SIZE = 1024 * 1024;
		
		System.out.println("Writing " + numBytes 
				+ " bytes to " + outFile.getAbsolutePath());

		PrintStream stream = new PrintStream(outFile);
		
		byte[] data = new byte[BLOCK_SIZE];
		for (int i = 0; i < data.length; i++) {
			data[i] = VALUE_FOR_FILE;
		}
		
		long numBytesWritten = 0;
		while (numBytesWritten < numBytes) {
			if (numBytesWritten + BLOCK_SIZE < numBytes) {
				stream.write(data);
				numBytesWritten += BLOCK_SIZE;
			} else {
				stream.write(VALUE_FOR_FILE);
				numBytesWritten++;
			}
		}
		stream.close();
	}
}