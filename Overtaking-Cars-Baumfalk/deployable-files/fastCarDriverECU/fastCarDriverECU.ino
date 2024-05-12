
#include "Debug.h"
#include <SimpleHardwareController.hpp>
#include <SimpleHardwareController_Connector.h>
#include "Config.hpp"

#include "I2cCustomLib.hpp"


#include "MCC_driveControllerComponent.h"
#include "MCC_courseControlComponent.h"
#include "MCC_distanceSensorComponent.h"

// Start of user code DEVICEINITINCLUDES
SimpleHardwareController fastCarDriverController;
// End of user code


//variable for component Instances
DriveControllerComponent* atomic_c1;
DistanceSensorComponent* atomic_c2;
CourseControlComponent* atomic_c3;
DistanceSensorComponent* atomic_c4;

void setup(){
	#ifdef DEBUG
	Serial.begin(9600);
	Serial.println("Initialization starting...");
	#endif
	// Start of user code DEVICEINIT
	initSofdcarHalConnectorFor(&fastCarDriverController);
	fastCarDriverController.initializeCar(config, lineConfig);
	// End of user code
	atomic_c1= MCC_create_DriveControllerComponent(CI_DRIVECONTROLLERFDRIVECONTROLLER);
	atomic_c2= MCC_create_DistanceSensorComponent(CI_REARDISTANCESENSORFDISTANCESENSOR);
	atomic_c3= MCC_create_CourseControlComponent(CI_COURSECONTROLFCOURSECONTROL);
	atomic_c4= MCC_create_DistanceSensorComponent(CI_FRONTDISTANCESENSORFDISTANCESENSOR);
	
	i2cCommunication_setup(9);


	#ifdef DEBUG
	Serial.println("Initialization done...start execution.");
	#endif
}

void loop(){
	fastCarDriverController.loop();

	DriveControllerComponent_processStep(atomic_c1);
	DistanceSensorComponent_processStep(atomic_c2);
	CourseControlComponent_processStep(atomic_c3);
	DistanceSensorComponent_processStep(atomic_c4);
}	



