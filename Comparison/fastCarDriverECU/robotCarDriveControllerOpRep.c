
#include "robotCarDriveControllerOpRep.h"
#include <SimpleHardwareController_Connector.h>
/** Start of user code User includes **/ 


/**End of user code**/

void RobotCarDriveController_robotCarDriveControllerChangeLaneLeft(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerChangeLaneLeft **/ 
SimpleHardwareController_LineFollower_SetLineToFollow(0);
/**End of user code**/

}



void RobotCarDriveController_robotCarDriveControllerChangeLaneRight(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerChangeLaneRight **/ 
SimpleHardwareController_LineFollower_SetLineToFollow(0);
/**End of user code**/

}



void RobotCarDriveController_robotCarDriveControllerFollowLine(int16_T velocity){

/** Start of user code RobotCarDriveController_robotCarDriveControllerFollowLine **/ 
SimpleHardwareController_DriveController_SetSpeed(velocity);
/**End of user code**/

}




