
#include "robotCarPowerTrainOpRep.h"
#include <SimpleHardwareController_Connector.h>
/** Start of user code User includes **/ 


/**End of user code**/

void RobotCarPowerTrain_robotCarPowerTrainChangeLaneLeft(int32_T velocity){

/** Start of user code RobotCarPowerTrain_robotCarPowerTrainChangeLaneLeft **/ 
//fastCarDriverController.getLineFollower()->setLineToFollow(0);
SimpleHardwareController_LineFollower_SetLineToFollow(0);
/**End of user code**/

}



void RobotCarPowerTrain_robotCarPowerTrainChangeLaneRight(int32_T velocity){

/** Start of user code RobotCarPowerTrain_robotCarPowerTrainChangeLaneRight **/ 
//fastCarDriverController.getLineFollower()->setLineToFollow(1);
SimpleHardwareController_LineFollower_SetLineToFollow(1);
/**End of user code**/

}



void RobotCarPowerTrain_robotCarPowerTrainFollowLine(int32_T velocity){

/** Start of user code RobotCarPowerTrain_robotCarPowerTrainFollowLine **/ 
//fastCarDriverController.getDriveController()->setSpeed(velocity);
SimpleHardwareController_DriveController_SetSpeed(velocity);
/**End of user code**/

}
