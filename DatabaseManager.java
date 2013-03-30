import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * Database manager for keeping track of sequence memory
 * and free blocks.  Allows for several interface methods
 * using the Handle class to determine which bytes are
 * sequences.
 */
public class DatabaseManager {

	/**
	 * File pointer to our byte array on disk.  Used to
	 * store and access sequences based on give Handles,
	 * with given offsets and lengths.
	 */
	RandomAccessFile file;
	
	/**
	 * Linked List for keeping track of all free memory
	 * blocks.  Each block is represented by a Handle,
	 * with a given offset and length.
	 */
	LinkedList<Handle> free;
	
	/**
	 * Basic constructor for the DatabaseManager class.
	 * Will initialize all member fields appropriately.
	 */
	public DatabaseManager() {
		// TODO implement constructor
	}
	
	/**
	 * Method to insert a given sequence into the first
	 * free memory block.  If there are no free memory
	 * blocks of sufficient size, will create a new one
	 * and add it to the end of the file.
	 * 
	 * @param sequence - the sequence to insert
	 * @param length - the length of the given sequence
	 * @return - the Handle for the given sequence
	 */
	public Handle insert(String sequence, int length) {
		
		// Calculate number of bytes needed to store this sequence
		int bytesNeeded = (int) Math.ceil(length / 4);
		
		// Check if we can use a free block
		for (Handle freeBlock: free) {
			int offset = freeBlock.getOffset();
			if (freeBlock.getLength() >= bytesNeeded) {
				
				// Attempt to write to our data file
				try {
					file.seek(offset);
					file.write(buildByteArray(sequence, bytesNeeded));
				} catch (IOException e) {
					System.err.println("Problem writing to file. See stack trace for details.");
					e.printStackTrace();
					return null;
				}
				
				// Fix the list of free bytes according to how much we used
				if (freeBlock.getLength() == bytesNeeded) {
					free.remove(freeBlock);
				} else {
					free.set(free.indexOf(freeBlock), new Handle(offset + bytesNeeded, freeBlock.getLength() - bytesNeeded));
				}
				
				return new Handle(offset, bytesNeeded);
			}
		}
		
		int oldLength = -1;
		try {
			oldLength = (int) file.length();
		} catch (IOException e) {
			System.err.println("Problem writing to file. See stack trace for details.");
			e.printStackTrace();
		}
		
		// Check if the last free block is at the end of file
		Handle last = free.getLast();
		if (last.getOffset() + last.getLength() == oldLength) {
			
			// Attempt to write to our data file
			int offset = last.getOffset();
			try {
				file.seek(offset);
				file.write(buildByteArray(sequence, bytesNeeded));
			} catch (IOException e) {
				System.err.println("Problem writing to file. See stack trace for details.");
				e.printStackTrace();
				return null;
			}
			
			// Remove the last block we used up
			free.remove(last);
			
			return new Handle(offset, bytesNeeded);
		}
		
		// No valid free space so append to end of file
		try {
			file.setLength(oldLength + bytesNeeded);
			file.seek(oldLength);
			file.write(buildByteArray(sequence, bytesNeeded));
		} catch (IOException e) {
			System.err.println("Problem writing to file. See stack trace for details.");
			e.printStackTrace();
		}
		
		return new Handle(oldLength, bytesNeeded);
	}
	
	/**
	 * Builds a byte array of the given sequence represented in binary.
	 * 
	 * @param sequence - the String sequence to build from
	 * @param bytesNeeded - the number of bytes needed to represent the sequence
	 * @return - a byte array that should be written to the file
	 */
	private byte[] buildByteArray(String sequence, int bytesNeeded) {
		
		// Append A's to the sequence to make it even
		for (int mod = sequence.length() % 4; mod > 0; mod--) {
			sequence += "A";
		}
		
		// Set each byte to the appropriate string of 4 characters
		byte[] array = new byte[bytesNeeded];
		for (int i = 0; i < bytesNeeded; i++) {
			array[i] = stringtobyte(sequence.substring(i, i + 4));
		}
		
		return array;
	}
	
	/**
	 * Converts a string of four ACGT characters into a byte
	 * for storage in data memory.
	 * 
	 * @param sequence - a sequence of four ACGT characters
	 * @return - an appropriate byte for the four characters
	 */
	private byte stringtobyte(String sequence) {
		sequence = sequence.toUpperCase();
		byte output = 0;
	
		// Shift left twice and add 2 new bits
		for (char c : sequence.toCharArray()) {
			output <<= 2;
			output |= getCharValue(c);
		}
		
		return output;
	}
	
	/**
	 * Method to determine the correct binary value for a given
	 * ACGT character.
	 * 
	 * @param c - the character to convert to binary
	 * @return - the binary value of the given character
	 */
	private byte getCharValue(char c) {
		c = Character.toUpperCase(c);
		
		if (c == 'A')
			return 0;
		if (c == 'C')
			return 1;
		if (c == 'G')
			return 2;
		if (c == 'T')
			return 3;
		
		System.err.println(c + " is not a valid character for this sequence.");
		return -1;
	}
	
	/**
	 * Method to remove a sequence from the database.
	 * Creates a new free memory block in the place of
	 * the removed sequence.
	 * 
	 * @param handle - the given Handle for the sequence
	 */
	public void remove(Handle handle) {
		// TODO implement remove
	}
	
	/**
	 * Method to retrieve a DNA sequence using a given
	 * handle.  Will give the bytes in memory regardless
	 * of whether or not they have meaning (i.e. has no
	 * error checking).
	 * 
	 * @param handle - the given Handle for the sequence
	 * @return - the sequence in the memory location
	 */
	public String getEntry(Handle handle) {
		// TODO implement getEntry
		
		return "NYI: getEntry(Handle handle)";
	}
	
	/**
	 * Method to produce a string representation of all
	 * free memory blocks.
	 * 
	 * @return - all elements of the linked list free
	 */
	public String toString() {
		// TODO implement toString
		
		return "NYI: toString()";
	}
}
