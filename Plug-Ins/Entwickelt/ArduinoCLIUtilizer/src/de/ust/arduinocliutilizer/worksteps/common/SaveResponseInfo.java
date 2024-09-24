package de.ust.arduinocliutilizer.worksteps.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author muml
 * Saves the data to the command and its Response data in a text file with a given name in an extra folder named SavedResponses.
 */
public interface SaveResponseInfo{
	public final String savedResponsesFolderName = "SavedResponses";
	
	/**
	 * Saves the data of the command and its Response data in a text file with a given name in an extra folder named SavedResponses.
	 * @param targetFileDirectoryPath The path of the target file, such as an .ino file with arduino source code to compile.
	 * @param name How the data file shall be named.  
	 * @param feedback The response data to save.
	 * @return The complete Path to the generated file.
	 * @throws IOException
	 */
	public default Path saveShellResponseInfo(Path targetFileDirectoryPath, String name, CallAndResponses feedback) throws IOException{
		Path savedResponsesFolderPath = targetFileDirectoryPath.resolve(savedResponsesFolderName);
		if (!Files.exists(savedResponsesFolderPath) && !Files.isDirectory(savedResponsesFolderPath)){
		    Files.createDirectories(savedResponsesFolderPath);
		}
		
		Path completeFilePath = savedResponsesFolderPath.resolve(name);
		BufferedWriter writer = new BufferedWriter(new FileWriter(completeFilePath.toString(), false));
	    writer.write("Command:\n" + feedback.getUsedCommand() + "\n\n");
	    writer.append("Results:\n");
	    writer.append("Exit code: " + feedback.getExitCode() + "\n\n\n");
	    writer.append("Normal response stream:\n" + feedback.getNormalFeedback() + "\n\n\n");
	    writer.append("Error response stream:\n" + feedback.getErrorFeedback() + "\n");
	    writer.close();
	    
	    return completeFilePath;
	}
}
