
#include "robotCarDriveControllerOpRep.h"
/** Start of user code User includes **/ 
#include <SimpleHardwareController_Connector.h>


/**End of user code**/

void RobotCarDriveController_robotCarDriveControllerChangeLaneLeft(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerChangeLaneLeft **/ 
//@TODO: add your implementation here
SimpleHardwareController_LineFollower_SetLineToFollow(0);
/**End of user code**/

}



void RobotCarDriveController_robotCarDriveControllerChangeLaneRight(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerChangeLaneRight **/ 
//@TODO: add your implementation here
SimpleHardwareController_LineFollower_SetLineToFollow(1);
/**End of user code**/

}



void RobotCarDriveController_robotCarDriveControllerFollowLine(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerFollowLine **/ 
//@TODO: add your implementation here
SimpleHardwareController_DriveController_SetSpeed(velocity);
/**End of user code**/

}




