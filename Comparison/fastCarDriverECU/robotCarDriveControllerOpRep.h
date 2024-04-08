#ifndef ROBOTCARDRIVECONTROLLER_H_
#define ROBOTCARDRIVECONTROLLER_H_
#ifdef __cplusplus
  extern "C" {
#endif

	#include "standardTypes.h"
	#include "customTypes.h"


			
		/**
		 * @brief Implementation stub of operation changeLaneLeft
 		 * @details This function shall contain the implementation of the operation changeLaneLeft.
		 * @param velocity 
		 */
		void RobotCarDriveController_robotCarDriveControllerChangeLaneLeft(int16_T velocity);
		/**
		 * @brief Implementation stub of operation changeLaneRight
 		 * @details This function shall contain the implementation of the operation changeLaneRight.
		 * @param velocity 
		 */
		void RobotCarDriveController_robotCarDriveControllerChangeLaneRight(int16_T velocity);
		/**
		 * @brief Implementation stub of operation followLine
 		 * @details This function shall contain the implementation of the operation followLine.
		 * @param velocity 
		 */
		void RobotCarDriveController_robotCarDriveControllerFollowLine(int16_T velocity);


#ifdef __cplusplus
  }
#endif
#endif /*ROBOTCARDRIVECONTROLLER_H_ */
