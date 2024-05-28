
#include "Debug.h"
#include <SimpleHardwareController.hpp>
#include <SimpleHardwareController_Connector.h>
#include "Config.hpp"

#include "SerialCustomLib.hpp"


#include "MCC_courseControlComponent.h"
#include "MCC_distanceSensorComponent.h"
#include "MCC_driveControllerComponent.h"

// Start of user code DEVICEINITINCLUDES
SimpleHardwareController slowCarDriverController;
// End of user code


//variable for component Instances
DriveControllerComponent* atomic_c1;
CourseControlComponent* atomic_c2;
DistanceSensorComponent* atomic_c3;
DistanceSensorComponent* atomic_c4;

void setup(){
	#ifdef DEBUG
	Serial.begin(9600);
	Serial.println("Initialization starting...");
	#endif
	// Start of user code DEVICEINIT
	initSofdcarHalConnectorFor(&slowCarDriverController);
	slowCarDriverController.initializeCar(config, lineConfig);
	// End of user code
	atomic_c1= MCC_create_DriveControllerComponent(CI_DRIVECONTROLLERSDRIVECONTROLLER);
	atomic_c2= MCC_create_CourseControlComponent(CI_COURSECONTROLSCOURSECONTROL);
	atomic_c3= MCC_create_DistanceSensorComponent(CI_FRONTDISTANCESENSORSDISTANCESENSOR);
	atomic_c4= MCC_create_DistanceSensorComponent(CI_REARDISTANCESENSORSDISTANCESENSOR);
	
	i2cCommunication_setup(10);


	#ifdef DEBUG
	Serial.println("Initialization done...start execution.");
	#endif
}

void loop(){
	slowCarDriverController.loop();

	DriveControllerComponent_processStep(atomic_c1);
	CourseControlComponent_processStep(atomic_c2);
	DistanceSensorComponent_processStep(atomic_c3);
	DistanceSensorComponent_processStep(atomic_c4);
}	



