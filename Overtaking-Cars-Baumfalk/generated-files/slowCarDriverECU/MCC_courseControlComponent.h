#ifndef MCC_MCC_COURSECONTROLCOMPONENT_H_
#define MCC_MCC_COURSECONTROLCOMPONENT_H_

// Library
#include "ContainerTypes.h"
#include "LocalBufferManager.h"


//Identifier of this ECU
#include "ECU_Identifier.h"

//I2C Specific includes
#include "SerialCustomLib.hpp"
//include the component_interface header
#include "courseControlComponent_Interface.h"
//include api mapping headers

		
/**
 * @file 
 * @author generated
 * @brief Specification of Component Container for Component of Type: Atomic_Component_CourseControl
 * @details This file contains an implementation of the container interfaces of a components
 */

/**
 * @brief Forward Declaration of the method MCC_create_CourseControlComponent
 * @details The method for initializing and creating a component instance of type: Atomic_Component_CourseControl
 */
CourseControlComponent* MCC_create_CourseControlComponent(uint8_T id);

#endif /* MCC_MCC_COURSECONTROLCOMPONENT_H_ */
