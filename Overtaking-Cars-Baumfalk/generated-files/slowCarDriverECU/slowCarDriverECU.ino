
#include "Debug.h"

#include "I2cCustomLib.hpp"


#include "MCC_courseControlComponent.h"
#include "MCC_distanceSensorComponent.h"
#include "MCC_driveControllerComponent.h"

// Start of user code DEVICEINITINCLUDES
/* TODO: if devices or libraries are used which need an initialization, include the headers here */
// End of user code


//variable for component Instances
DistanceSensorComponent* atomic_c1;
DistanceSensorComponent* atomic_c2;
CourseControlComponent* atomic_c3;
DriveControllerComponent* atomic_c4;

void setup(){
	#ifdef DEBUG
	Serial.begin(9600);
	Serial.println("Initialization starting...");
	#endif
	// Start of user code DEVICEINIT
	/* TODO: if devices are used which need an initialization, call the functionse here */
	// End of user code
	atomic_c1= MCC_create_DistanceSensorComponent(CI_FRONTDISTANCESENSORSDISTANCESENSOR);
	atomic_c2= MCC_create_DistanceSensorComponent(CI_REARDISTANCESENSORSDISTANCESENSOR);
	atomic_c3= MCC_create_CourseControlComponent(CI_COURSECONTROLSCOURSECONTROL);
	atomic_c4= MCC_create_DriveControllerComponent(CI_DRIVECONTROLLERSDRIVECONTROLLER);
	
	i2cCommunication_setup(10);


	#ifdef DEBUG
	Serial.println("Initialization done...start execution.");
	#endif
}

void loop(){

	DistanceSensorComponent_processStep(atomic_c1);
	DistanceSensorComponent_processStep(atomic_c2);
	CourseControlComponent_processStep(atomic_c3);
	DriveControllerComponent_processStep(atomic_c4);
}	



