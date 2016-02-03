package ca.cmpt213.as1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Manage a collection of FileLists supporting randomly assigning files to 
 * FileLists while enforcing a maximum size.
 * @author Brian Fraser
 *
 */
public class FileListManager {
	private long maxBytesPerList = 0;
	private FileList[] fileLists;

	public FileListManager(int numberFileLists, long maxBytesPerList) {
		this.maxBytesPerList = maxBytesPerList;
		
		fileLists = new FileList[numberFileLists];
		for (int i = 0; i < numberFileLists; i++) {
			fileLists[i] = new FileList();
		}
	}

	public void randomlyAssignFile(File newFile) {
		ArrayList<Integer> listsToTry = generatePermutationOf1ToN(fileLists.length);
		for (Integer i : listsToTry) {
			FileList currentList = fileLists[i];
			if (isRoomForFileInList(newFile, currentList)) {
				currentList.addFileOrFolder(newFile);
				return;
			}
		}
		
		throw new NoRoomInListsException();	
	}

	private ArrayList<Integer> generatePermutationOf1ToN(int n) {
		ArrayList<Integer> permutation = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			permutation.add(i);
		}
		
		Collections.shuffle(permutation);
		
		return permutation;
	}

	private boolean isRoomForFileInList(File nextFile, FileList fileList) {
		return getRoomLeftInList(fileList) >= nextFile.length();
	}
	private long getRoomLeftInList(FileList fileList) {
		return maxBytesPerList - fileList.getSizeOfAllFiles();
	}

	public int getNumberFileLists() {
		return fileLists.length;
	}

	public void sortEachFileListBySize() {
		for (FileList list : fileLists) {
			list.sortBySize();
		}
	}

	public FileList getFileList(int index) {
		return fileLists[index];
	}

}
