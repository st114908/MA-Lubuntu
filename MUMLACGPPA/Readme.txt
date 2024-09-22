This Eclipse plugin project is supposed to provide a pipeline based process automatisation for MUML projects that are used for generating Arduino code. Hence the name MUMLACGPPA for "MUML Arduino Code Generation and PostProcessing Automatisation".
It is intended to be used together with the MechatronicUML Tool Suite with the code generation plugins and David Stürners modifications (see https://github.com/SQA-Robo-Lab/MUML_1_0-win32-x86_64), but it also accesses the Eclpse plugin project ArduinoCLIUtilizer. Furthermore it requires the Eclipse plugin project ProjectFolderPathStoragePlugIn for its internal workings.

As usage scenario the Overtaking-Cars project of the branch ma_baumfalk has been selected and thus its example pipeline is specialized for this one, but it can be reconfigured.
The various pipeline steps have been designed to be easy to understand and use. The work steps from https://github.com/SQA-Robo-Lab/MUML-CodeGen-Wiki/blob/main/user-documentation/main.md and https://github.com/SQA-Robo-Lab/Overtaking-Cars/tree/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test are automatisized through some of the pipeline steps. OnlyContinueIfFulfilledElseAbort can be used to abort the pipeline execution and TerminalCommand can be used to start other Scripts and programs.

The generatable file LocalSofdcarHalConfig.hpp serves to store pin configurations locally without relying on downloading it, which can help in case of local versions.

Due to a lack of time the selection of the MUML ressources couldn't be understood and implemented, so the export wizard framework has been reused for export wizard "PipeLineExecutionAsExport",which starts the pipeline and performs the respective automatic selection of the used ressources for ContainerTransformation and ComponentCodeGeneration. Please note that these selections are automatically performed by the respective export settings window and only appear if the pipeline sequence contains a step that requires it.

Due to limitations from Eclipse the pipeline execution can't create windows, so "PipeLineExecutionAsExport" takes over for the functionality of the steps DialogMessage, SelectableTextWindow and OnlyContinueIfFulfilledElseAbort and prints out their messages on the console window of the Eclipse instance that is used to run the MUML plugins and so on as application.

Paths get read like this:
If they don't start with a "/" (without ") then they get interpreted as a path relative to the project root folder.
If they start with a "/" (without ") then they get interpreted as an absolute path.

Please keep in mind that the integrated validation only checks the variables/the data flow of the ins and outs.
Due to time constraints it doesn't check if the inputs are correct.

Usage:
If you right click on a .muml file, then the context menu will have the additional entry "MUMLACGPPA" (without " characters), which opens a sub menu.
The entries of this sub menu are like this:

- "Run pipeline" (Can't do anything functional as explained before and reminds of the Export wizard "Execute pipeline")
- "Generate default/example pipeline settings"
- "Generate all pipeline step example settings"
- "Generate local settings for Sofdcar-Hal"
- "Generate ArduinoCLIUtilizer config file"
- "List connected Arduino boards"

All the above listed entry labels have been chosen to fit/briefly describe the respective entry's function/purpose and if something is missing or fails then a window with the notification pops up and additional info is placed under [Folder of the clicked file]/SavedResponses.