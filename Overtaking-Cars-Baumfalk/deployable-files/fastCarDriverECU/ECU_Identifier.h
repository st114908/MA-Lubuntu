#ifndef ECU_IDENTIFIER_H
#define ECU_IDENTIFIER_H
	
// code for ECU Config fastCarDriverECU_config
/**
*
*@brief Identifier for Messages used on fastCarDriverECU_config
*@details Identifier to Identy Local Messages
*/
//Identifier for Messages used on this ECU
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESEXECUTEDOVERTAKINGOVERTAKINGPERMISSIONMESSAGESMESSAGE 1 /**< ECU Identifier: For the Message-Type: executedOvertaking */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESDENYPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 2 /**< ECU Identifier: For the Message-Type: denyPermission */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESREQUESTPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 3 /**< ECU Identifier: For the Message-Type: requestPermission */
#define MESSAGE_OVERTAKINGPERMISSIONMESSAGESGRANTPERMISSIONOVERTAKINGPERMISSIONMESSAGESMESSAGE 4 /**< ECU Identifier: For the Message-Type: grantPermission */

//Identifier for ComponentInstances
/**
*
*@brief Identifier to distinguish Component Instance on ECU fastCarDriverECU_config
*@details Used by a component container to identify component instances of the same component type
*/
#define CI_REARDISTANCESENSORFDISTANCESENSOR 1 /**< Identifier for Component Instance rearDistanceSensor.F */
#define CI_COURSECONTROLFCOURSECONTROL 2 /**< Identifier for Component Instance courseControl.F */
#define CI_FRONTDISTANCESENSORFDISTANCESENSOR 3 /**< Identifier for Component Instance frontDistanceSensor.F */
#define CI_DRIVECONTROLLERFDRIVECONTROLLER 4 /**< Identifier for Component Instance driveController.F */
			
#endif /* ECU_IDENTIFIER_H */
