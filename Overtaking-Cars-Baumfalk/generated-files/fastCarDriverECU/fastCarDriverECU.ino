
#include "Debug.h"

#include "I2cCustomLib.hpp"


#include "MCC_driveControllerComponent.h"
#include "MCC_courseControlComponent.h"
#include "MCC_distanceSensorComponent.h"

// Start of user code DEVICEINITINCLUDES
/* TODO: if devices or libraries are used which need an initialization, include the headers here */
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
	/* TODO: if devices are used which need an initialization, call the functionse here */
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

	DriveControllerComponent_processStep(atomic_c1);
	DistanceSensorComponent_processStep(atomic_c2);
	CourseControlComponent_processStep(atomic_c3);
	DistanceSensorComponent_processStep(atomic_c4);
}	


