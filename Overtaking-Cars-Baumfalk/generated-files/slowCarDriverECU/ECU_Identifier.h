#ifndef ECU_IDENTIFIER_H
#define ECU_IDENTIFIER_H
	
// code for ECU Config slowCarDriverECU_config
/**
*
*@brief Identifier for Messages used on slowCarDriverECU_config
*@details Identifier to Identy Local Messages
*/
//Identifier for Messages used on this ECU
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESDENYPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 1 /**< ECU Identifier: For the Message-Type: denyPermission */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESEXECUTEDOVERTAKINGOVERTAKINGPERMISSIONMESSAGESMESSAGE 2 /**< ECU Identifier: For the Message-Type: executedOvertaking */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESGRANTPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 3 /**< ECU Identifier: For the Message-Type: grantPermission */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESREQUESTPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 4 /**< ECU Identifier: For the Message-Type: requestPermission */

//Identifier for ComponentInstances
/**
*
*@brief Identifier to distinguish Component Instance on ECU slowCarDriverECU_config
*@details Used by a component container to identify component instances of the same component type
*/
#define CI_COURSECONTROLSCOURSECONTROL 1 /**< Identifier for Component Instance courseControl.S */
#define CI_FRONTDISTANCESENSORSDISTANCESENSOR 2 /**< Identifier for Component Instance frontDistanceSensor.S */
#define CI_DRIVECONTROLLERSDRIVECONTROLLER 3 /**< Identifier for Component Instance driveController.S */
#define CI_REARDISTANCESENSORSDISTANCESENSOR 4 /**< Identifier for Component Instance rearDistanceSensor.S */
			
#endif /* ECU_IDENTIFIER_H */
