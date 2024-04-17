package arduinocliutilizer.worksteps.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public interface SaveResponseInfoLocation{
	
	public static String saveShellResponseInfo(String targetDirectory, String name, String command, ResponseFeedback feedback) throws IOException{
		File directoryCheck = new File(targetDirectory + "/SavedResponses");
		if (!directoryCheck.exists()){
		    directoryCheck.mkdirs();
		}
		
		String completeFileLocation = targetDirectory + "/SavedResponses/" + name;
		BufferedWriter writer = new BufferedWriter(new FileWriter(completeFileLocation, false));
	    writer.write("Command:\n" + command + "\n\n");
	    writer.append("Result:\n");
	    writer.append("Exit code: " + feedback.exitCode + "\n\n\n");
	    writer.append("Normal response stream:\n" + feedback.normalFeedback + "\n\n\n");
	    writer.append("Error response stream:\n" + feedback.errorFeedback + "\n");
	    writer.close();
	    
	    return completeFileLocation;
	}
}
