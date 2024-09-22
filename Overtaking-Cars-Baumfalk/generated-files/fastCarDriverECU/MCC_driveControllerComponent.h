#ifndef MCC_MCC_DRIVECONTROLLERCOMPONENT_H_
#define MCC_MCC_DRIVECONTROLLERCOMPONENT_H_

// Library
#include "ContainerTypes.h"
#include "LocalBufferManager.h"


//Identifier of this ECU
#include "ECU_Identifier.h"

//include the component_interface header
#include "driveControllerComponent_Interface.h"
//include api mapping headers
#include "CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h"
#include "CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h"

		
/**
 * @file 
 * @author generated
 * @brief Specification of Component Container for Component of Type: Atomic_Component_DriveController
 * @details This file contains an implementation of the container interfaces of a components
 */

/**
 * @brief Forward Declaration of the method MCC_create_DriveControllerComponent
 * @details The method for initializing and creating a component instance of type: Atomic_Component_DriveController
 */
DriveControllerComponent* MCC_create_DriveControllerComponent(uint8_T id);

#endif /* MCC_MCC_DRIVECONTROLLERCOMPONENT_H_ */
