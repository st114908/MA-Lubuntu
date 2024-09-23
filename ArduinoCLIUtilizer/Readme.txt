This Eclipse plugin project is supposed to enable to work directly with Arduino libraries and code files without having to switch to the ArduinoIDE and be it can be used from other plug-ins.
It requires an installed or at least downloaded ArduinoLCI (it utilizes it, hence the name "ArduinoCLIUtilizer") and that the config file "arduinoCLIUtilizerConfig.yaml" in the folder "automatisationConfig" in the root of the eclipse project the clicked file is in.
Furthermore it requires the Eclipse plugin project ProjectFolderPathStoragePlugIn for its internal workings.


In the configuration file exist two settings that can be changed:

arduinoCLIPathSetInPathEnvironment:
This one sets if the path to the ArduinoCLI file (see arduinoCLIDirectory) has been set in the path environment or if the internal working have to perform a temporary addition of it.
By default it is set to "false" (without " characters) for "ArduinoCLI not set in path environment".
Set it to "true" (without " characters) if the ArduinoCLI is set in the path environment.

arduinoCLIDirectory:
This one sets where the ArduinoCLI file is located. Required if arduinoCLIPathSetInPathEnvironment is set to "false" (without " characters).
By default set to /home/muml/ArduinoCLI.


Usage:
If you right click on a .zip, .ino or .hex, then the context menu will have the additional entry "ArduinoCLIUtilizer" (without " characters), which opens a sub menu. The entries of this sub menu depend on the clicked file type:

.zip files:
- "Generate ArduinoCLIUtilizer config file"
- "Install zipped arduino library"


.ino files: 
- "List connected Arduino boards"
- "Generate ArduinoCLIUtilizer config file"
- "Compile and upload Arduino project"
- "Compile Arduino project"
- "Verify Arduino project"

.hex files:
- "List connected Arduino boards"
- "Generate ArduinoCLIUtilizer config file"
- "Upload.hex file"

All the above listed entry labels have been chosen to fit/briefly describe the respective entry's function/purpose and if something is missing or fails then a window with the notification pops up and additional info is placed under [Folder of the clicked file]/SavedResponses.