

		#include "courseControlComponent_Interface.h"
		



		void initializeCourseControlCourseControlCourseControlPortRegion(
				CourseControlCourseControlComponentStateChart* stateChart) {
		
			stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
					STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING;
		
			stateChart->CourseControlCourseControlCourseControlPort_isExecutable = true;
		
		}
		void initializeCourseControlCourseControl_mainDrivingRegion(
				CourseControlCourseControlComponentStateChart* stateChart) {
		
			Clock_reset(
					stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANEDoClock);
			Clock_reset(
					stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLEDoClock);
			Clock_reset(
					stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKINGDoClock);
		
			stateChart->currentStateOfCourseControlCourseControl_mainDriving =
					STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE;
		
			stateChart->CourseControlCourseControl_mainDriving_isExecutable = true;
		
		}
		
		void CourseControlCourseControlComponentStateChart_initialize(
				CourseControlCourseControlComponentStateChart* stateChart) {
			//initialize clocks
		
			//initialize variables of the root statechart
			stateChart->distanceLimit = stateChart->distanceLimit = 40;
			;
		
			stateChart->desiredVelocity = stateChart->desiredVelocity = 55;
			;
		
			stateChart->slowVelocity = stateChart->slowVelocity = 0;
			;
		
			stateChart->laneDistance = stateChart->laneDistance = 70;
			;
		
			//initialize port variables of the root statechart
		
			//initialize init state
			stateChart->currentStateOfCourseControlCourseControlComponent =
					STATE_COURSECONTROLCOURSECONTROL_MAIN;
		
			initializeCourseControlCourseControlCourseControlPortRegion(stateChart);
			initializeCourseControlCourseControl_mainDrivingRegion(stateChart);
		}
		
		

		CourseControlCourseControlComponentStateChart* CourseControlCourseControlComponentStateChart_create(
				CourseControlComponent* parentComponent) {
			CourseControlCourseControlComponentStateChart* stateChart =
					(CourseControlCourseControlComponentStateChart*) malloc(
							sizeof(CourseControlCourseControlComponentStateChart));
			if (stateChart != NULL) {
				stateChart->parentComponent = parentComponent;
				if (stateChart->parentComponent != NULL) {
					CourseControlCourseControlComponentStateChart_initialize(
							stateChart);
				} else {
		
					CourseControlCourseControlComponentStateChart_destroy(stateChart);
					stateChart = NULL;
				}
			} else {
		
			}
			return stateChart;
		}
		

			void CourseControlCourseControlComponentStateChart_destroy(CourseControlCourseControlComponentStateChart* stateChart) {
				if(stateChart != NULL) {
		
		
					free(stateChart);
				}
			}

		//implementations for RTSC internal operations
		
		
		
		void CourseControlCourseControlCourseControlPortStateChart_processStep(
				CourseControlCourseControlComponentStateChart* stateChart) {
			switch (stateChart->currentStateOfCourseControlCourseControlCourseControlPort) {
			case STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING:
				if (
		
				stateChart->frontDistance < stateChart->distanceLimit
		
				//
		
						) {
		
					// execute exit actions
					// nothing to do
		
					// Transition Effects (incl. clock resets)
		
					stateChart->velocity = stateChart->slowVelocity;
					;
		
					//create new Parameter struct for OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message
					OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesRequestPermission;
		
					//send Message
					MCC_CourseControlComponent_courseControl_send_OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message(
							&(stateChart->parentComponent->courseControlPort),
							&msg_OvertakingPermissionMessagesRequestPermission);
		#ifdef DEBUG
					printDebugInformation("CourseControl sent message of type OvertakingPermissionMessagesRequestPermission");
		#endif		
		
					//release all created received events
					//release all created sent events
					// change the state
					stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
							STATE_COURSECONTROLCOURSECONTROLWAITFORPERMISSION;
		#ifdef DEBUG
					printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLWAITFORPERMISSION" );
		#endif		
		
					// execute entry actions
					// nothing to do
		
					//
				} else {
		
				}
				break;
			case STATE_COURSECONTROLCOURSECONTROLWAITFORPERMISSION:
				if ((stateChart->CourseControlCourseControl_mainDriving_isExecutable)
						&& (stateChart->currentStateOfCourseControlCourseControl_mainDriving
								== STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE)) {
					if (1
		
							&& MCC_CourseControlComponent_courseControl_exists_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(
									CourseControlComponent_getcourseControl(
											stateChart->parentComponent))
		
											) {
						OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesGrantPermission;
						MCC_CourseControlComponent_courseControl_recv_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(
								CourseControlComponent_getcourseControl(
										stateChart->parentComponent),
								&msg_OvertakingPermissionMessagesGrantPermission);
		
		#ifdef DEBUG
						printDebugInformation("CourseControl received message of typeOvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message");
		#endif
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						stateChart->velocity = stateChart->desiredVelocity;
						;
		
						// nothing to do			
		
						//release all created received events
						//	free(mwMsg);
		
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
								STATE_COURSECONTROLCOURSECONTROLOVERTAKING;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLOVERTAKING" );
		#endif		
		
						// execute entry actions
						// nothing to do
		
						//
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						RobotCarDriveController_robotCarDriveControllerChangeLaneLeft(
								stateChart->desiredVelocity);
						;
		
						// nothing to do			
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControl_mainDriving =
								STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControl_mainDriving switched state to STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE" );
		#endif		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLEDoClock);
		
						// execute entry actions
						// nothing to do
		
						//
						stateChart->CourseControlCourseControl_mainDriving_isExecutable =
								false;
						stateChart->CourseControlCourseControlCourseControlPort_isExecutable =
								false;
						//break;
					}
				} else if (0) {
					//dummy
				} else if (MCC_CourseControlComponent_courseControl_exists_OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message(
						CourseControlComponent_getcourseControl(
								stateChart->parentComponent))
		
								) {
					OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesDenyPermission;
					MCC_CourseControlComponent_courseControl_recv_OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message(
							CourseControlComponent_getcourseControl(
									stateChart->parentComponent),
							&msg_OvertakingPermissionMessagesDenyPermission);
		
		#ifdef DEBUG
					printDebugInformation("CourseControl received message of typeOvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message");
		#endif
		
					// execute exit actions
					// nothing to do
		
					// Transition Effects (incl. clock resets)
					// nothing to do
		
					// nothing to do			
		
					//release all created received events
					//	free(mwMsg);
		
					//release all created sent events
					// change the state
					stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
							STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING;
		#ifdef DEBUG
					printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING" );
		#endif		
		
					// execute entry actions
					// nothing to do
		
					//
				} else {
		
				}
				break;
			case STATE_COURSECONTROLCOURSECONTROLOVERTAKING:
				if ((stateChart->CourseControlCourseControl_mainDriving_isExecutable)
						&& (stateChart->currentStateOfCourseControlCourseControl_mainDriving
								== STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKING)) {
					if (
		
					stateChart->rearDistance > 2 * stateChart->distanceLimit
		
					//
		
							&& 1
		
							) {
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						RobotCarDriveController_robotCarDriveControllerChangeLaneRight(
								stateChart->desiredVelocity);
						;
		
						// nothing to do			
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControl_mainDriving =
								STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControl_mainDriving switched state to STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE" );
		#endif		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANEDoClock);
		
						// execute entry actions
						// nothing to do
		
						//
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
						// nothing to do
		
						//create new Parameter struct for OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message
						OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesExecutedOvertaking;
		
						//send Message
						MCC_CourseControlComponent_courseControl_send_OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message(
								&(stateChart->parentComponent->courseControlPort),
								&msg_OvertakingPermissionMessagesExecutedOvertaking);
		#ifdef DEBUG
						printDebugInformation("CourseControl sent message of type OvertakingPermissionMessagesExecutedOvertaking");
		#endif		
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
								STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING" );
		#endif		
		
						// execute entry actions
						// nothing to do
		
						//
						stateChart->CourseControlCourseControl_mainDriving_isExecutable =
								false;
						stateChart->CourseControlCourseControlCourseControlPort_isExecutable =
								false;
						//break;
					}
				} else if (0) {
					//dummy
				} else {
		
				}
				break;
			default:
				break;
			}
			stateChart->CourseControlCourseControlCourseControlPort_isExecutable =
					false;
		}
		
		void CourseControlCourseControl_mainDrivingStateChart_processStep(
				CourseControlCourseControlComponentStateChart* stateChart) {
			switch (stateChart->currentStateOfCourseControlCourseControl_mainDriving) {
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE:
				if ((stateChart->CourseControlCourseControlCourseControlPort_isExecutable)
						&& (stateChart->currentStateOfCourseControlCourseControlCourseControlPort
								== STATE_COURSECONTROLCOURSECONTROLWAITFORPERMISSION)) {
					if (MCC_CourseControlComponent_courseControl_exists_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(
							CourseControlComponent_getcourseControl(
									stateChart->parentComponent))
		
					&& 1
		
					) {
						OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesGrantPermission;
						MCC_CourseControlComponent_courseControl_recv_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(
								CourseControlComponent_getcourseControl(
										stateChart->parentComponent),
								&msg_OvertakingPermissionMessagesGrantPermission);
		
		#ifdef DEBUG
						printDebugInformation("CourseControl received message of typeOvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message");
		#endif
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						stateChart->velocity = stateChart->desiredVelocity;
						;
		
						// nothing to do			
		
						//release all created received events
						//	free(mwMsg);
		
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
								STATE_COURSECONTROLCOURSECONTROLOVERTAKING;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLOVERTAKING" );
		#endif		
		
						// execute entry actions
						// nothing to do
		
						//
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						RobotCarDriveController_robotCarDriveControllerChangeLaneLeft(
								stateChart->desiredVelocity);
						;
		
						// nothing to do			
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControl_mainDriving =
								STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControl_mainDriving switched state to STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE" );
		#endif		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLEDoClock);
		
						// execute entry actions
						// nothing to do
		
						//
						stateChart->CourseControlCourseControlCourseControlPort_isExecutable =
								false;
						stateChart->CourseControlCourseControl_mainDriving_isExecutable =
								false;
						//break;
					}
				} else if (0) {
					//dummy
				} else {
		
					//execute doEvent
					if (Clock_getTime(
							stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANEDoClock)
							>= 1 * 1.0) {
						//execute action followMainLane
						RobotCarDriveController_robotCarDriveControllerFollowLine(
								stateChart->desiredVelocity);
		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANEDoClock);
					}
		
				}
				break;
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE:
				if (
		
				stateChart->rearDistance < stateChart->laneDistance
		
				//
		
						) {
		
					// execute exit actions
					// nothing to do
		
					// Transition Effects (incl. clock resets)
					// nothing to do
		
					// nothing to do			
		
					//release all created received events
					//release all created sent events
					// change the state
					stateChart->currentStateOfCourseControlCourseControl_mainDriving =
							STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKING;
		#ifdef DEBUG
					printDebugInformation("currentStateOfCourseControlCourseControl_mainDriving switched state to STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKING" );
		#endif		
					Clock_reset(
							stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKINGDoClock);
		
					// execute entry actions
					// nothing to do
		
					//
				} else {
		
					//execute doEvent
					if (Clock_getTime(
							stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLEDoClock)
							>= 1 * 1.0) {
						//execute action followFastLane
						RobotCarDriveController_robotCarDriveControllerFollowLine(
								stateChart->desiredVelocity);
		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLEDoClock);
					}
		
				}
				break;
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKING:
				if ((stateChart->CourseControlCourseControlCourseControlPort_isExecutable)
						&& (stateChart->currentStateOfCourseControlCourseControlCourseControlPort
								== STATE_COURSECONTROLCOURSECONTROLOVERTAKING)) {
					if (1
		
					&&
		
					stateChart->rearDistance > 2 * stateChart->distanceLimit
		
					//
		
							) {
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
		
						RobotCarDriveController_robotCarDriveControllerChangeLaneRight(
								stateChart->desiredVelocity);
						;
		
						// nothing to do			
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControl_mainDriving =
								STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControl_mainDriving switched state to STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE" );
		#endif		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANEDoClock);
		
						// execute entry actions
						// nothing to do
		
						//
		
						// execute exit actions
						// nothing to do
		
						// Transition Effects (incl. clock resets)
						// nothing to do
		
						//create new Parameter struct for OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message
						OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message msg_OvertakingPermissionMessagesExecutedOvertaking;
		
						//send Message
						MCC_CourseControlComponent_courseControl_send_OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message(
								&(stateChart->parentComponent->courseControlPort),
								&msg_OvertakingPermissionMessagesExecutedOvertaking);
		#ifdef DEBUG
						printDebugInformation("CourseControl sent message of type OvertakingPermissionMessagesExecutedOvertaking");
		#endif		
		
						//release all created received events
						//release all created sent events
						// change the state
						stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
								STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING;
		#ifdef DEBUG
						printDebugInformation("currentStateOfCourseControlCourseControlCourseControlPort switched state to STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING" );
		#endif		
		
						// execute entry actions
						// nothing to do
		
						//
						stateChart->CourseControlCourseControlCourseControlPort_isExecutable =
								false;
						stateChart->CourseControlCourseControl_mainDriving_isExecutable =
								false;
						//break;
					}
				} else if (0) {
					//dummy
				} else {
		
					//execute doEvent
					if (Clock_getTime(
							stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKINGDoClock)
							>= 1 * 1.0) {
						//execute action followFastLane
						RobotCarDriveController_robotCarDriveControllerFollowLine(
								stateChart->desiredVelocity);
		
						Clock_reset(
								stateChart->sTATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKINGDoClock);
					}
		
				}
				break;
			default:
				break;
			}
			stateChart->CourseControlCourseControl_mainDriving_isExecutable = false;
		}
		
		void CourseControlCourseControlComponentStateChart_processStep(
				CourseControlCourseControlComponentStateChart* stateChart) {
			switch (stateChart->currentStateOfCourseControlCourseControlComponent) {
			case STATE_COURSECONTROLCOURSECONTROL_MAIN:
		
				if (stateChart->CourseControlCourseControl_mainDriving_isExecutable)
					CourseControlCourseControl_mainDrivingStateChart_processStep(
							stateChart);
				if (stateChart->CourseControlCourseControlCourseControlPort_isExecutable)
					CourseControlCourseControlCourseControlPortStateChart_processStep(
							stateChart);
		
				break;
			default:
				break;
			}
			stateChart->CourseControlCourseControlComponent_isExecutable = false;
		}
		
		
		void CourseControlCourseControl_mainDrivingStateChart_exit(
				CourseControlCourseControlComponentStateChart* stateChart) {
			switch (stateChart->currentStateOfCourseControlCourseControl_mainDriving) {
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGFOLLOWMAINLANE:
				// nothing to do
		
				break;
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGPASSSLOWERVEHICLE:
				// nothing to do
		
				break;
			case STATE_COURSECONTROLCOURSECONTROL_MAINDRIVINGCOMPLETEOVERTAKING:
				// nothing to do
		
				break;
			default:
				break;
			}
			stateChart->currentStateOfCourseControlCourseControl_mainDriving =
					COURSECONTROLCOURSECONTROLCOMPONENT_INACTIVE;
		}
		void CourseControlCourseControlCourseControlPortStateChart_exit(
				CourseControlCourseControlComponentStateChart* stateChart) {
			switch (stateChart->currentStateOfCourseControlCourseControlCourseControlPort) {
			case STATE_COURSECONTROLCOURSECONTROLAUTONOMOUSDRIVING:
				// nothing to do
		
				break;
			case STATE_COURSECONTROLCOURSECONTROLWAITFORPERMISSION:
				// nothing to do
		
				break;
			case STATE_COURSECONTROLCOURSECONTROLOVERTAKING:
				// nothing to do
		
				break;
			default:
				break;
			}
			stateChart->currentStateOfCourseControlCourseControlCourseControlPort =
					COURSECONTROLCOURSECONTROLCOMPONENT_INACTIVE;
		}
				
			
		bool_t CourseControlCourseControlComponentStateChart_isInState(
				CourseControlCourseControlComponentStateChart* stateChart,
				CourseControlCourseControlComponentState state) {
			return (stateChart->currentStateOfCourseControlCourseControl_mainDriving
					== state
					|| stateChart->currentStateOfCourseControlCourseControlCourseControlPort
							== state);
		
		}
		

	

