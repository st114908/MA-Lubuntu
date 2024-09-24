This Eclipse plugin project provides the execution of pipeline based process automatisation for MUML projects that are used for generating Arduino code provided by the MUMLACGPPA Eclipse plugin project.
It is intended to be used together with the MechatronicUML Tool Suite with the code generation plug-ins and David Stürners modifications (see https://github.com/SQA-Robo-Lab/MUML_1_0-win32-x86_64), but it also relies on the Eclpse plugin projects ArduinoCLIUtilizer and MUMACGPPA. Furthermore it requires the Eclipse plugin project ProjectFolderPathStoragePlugIn for its internal workings.

Due to a lack of time the selection of the MUML ressources couldn't be understood and implemented, so the export wizard framework has been reused for export wizard "PipeLineExecutionAsExport",which starts the pipeline and performs the respective automatic selection of the used ressources for ContainerTransformation and ComponentCodeGeneration. Please note that these selections are automatically performed by the respective export settings window and only appear if the pipeline sequence contains a step that requires it.

As usage scenario the Overtaking-Cars project of the branch ma_baumfalk has been selected and thus it is a bit specialized for this one.
It can detect if the configuration files for the ArduinoCLIUtilizer (arduinoCLIUtilizerConfig.yaml) and the MUMACGPPA (pipelineSettings.yaml) Eclipse plugin projects are exist or not. If not then it will notify the user on how to let the respective config file be generated.
In case of the configuration files for the ArduinoCLIUtilizer (arduinoCLIUtilizerConfig.yaml) it checks first if the ArduinoCLIUtilizer is required. 

Due to limitations from Eclipse the pipeline execution can't create windows, so "PipeLineExecutionAsExport" takes over for the functionality of the steps DialogMessage, SelectableTextWindow and OnlyContinueIfFulfilledElseAbort and prints out their messages on the console window of the Eclipse instance that is used to run the MUML plug-ins and so on as application.

Usage:
Right click on a .muml file, select "Export" (without " characters), open "MechatronicUML" (without " characters) and select one of the following export operations:

- Execute pipeline sequence
- Execute postprocessing sequence
- Execute container transformation
- Execute container code generation
- Execute component code generation

The other entries are not from this Eclipse plugin project.

All the above listed entry labels have been chosen to fit/briefly describe the respective entry's function/purpose and if something is missing or fails then a window with the notification pops up and additional info is placed under [Folder of the clicked file]/SavedResponses.