	/**
 * @file 
 * @author generated by Fraunhofer IEM
 * @brief Specification of Component of Type: Coordinator
 * @details This files contains a description of the Coordinator in form of the CoordinatorComponent
 * 			and all methods which can be executed on an Instance of this Component
 */
		#ifndef COORDINATORCOMPONENT_Interface_H_
		#define COORDINATORCOMPONENT_Interface_H_

#ifdef __cplusplus
  extern "C" {
#endif		// Library
	#include "standardTypes.h"
	#include "customTypes.h"
	#include "port.h"
		#include "messages_types.h"
	#include "clock.h"
	#include "Debug.h"


		

	//include Operation Repositories




		//#include "coordinatorCoordinatorComponentStateChart.h"
			/*****
 			 *
 			 *  Forward Delcaration of Container Functions
 			 *
 			 */		
					void MCC_CoordinatorComponent_communicator_send_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(Port* p, OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message* msg);
					void MCC_CoordinatorComponent_communicator_send_OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message(Port* p, OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_communicator_recv_OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message(Port* p, OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_communicator_exists_OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message(Port* p);
					bool_T MCC_CoordinatorComponent_communicator_recv_OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message(Port* p, OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_communicator_exists_OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message(Port* p);
					void MCC_CoordinatorComponent_overtakingInitiator_send_OvertakingCoordinationMessagesRequestOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesRequestOvertaking_OvertakingCoordinationMessages_Message* msg);
					void MCC_CoordinatorComponent_overtakingInitiator_send_OvertakingCoordinationMessagesFinishedOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesFinishedOvertaking_OvertakingCoordinationMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_overtakingInitiator_recv_OvertakingCoordinationMessagesAcceptOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesAcceptOvertaking_OvertakingCoordinationMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_overtakingInitiator_exists_OvertakingCoordinationMessagesAcceptOvertaking_OvertakingCoordinationMessages_Message(Port* p);
					void MCC_CoordinatorComponent_overtakingAffiliate_send_OvertakingCoordinationMessagesAcceptOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesAcceptOvertaking_OvertakingCoordinationMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_overtakingAffiliate_recv_OvertakingCoordinationMessagesRequestOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesRequestOvertaking_OvertakingCoordinationMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_overtakingAffiliate_exists_OvertakingCoordinationMessagesRequestOvertaking_OvertakingCoordinationMessages_Message(Port* p);
					bool_T MCC_CoordinatorComponent_overtakingAffiliate_recv_OvertakingCoordinationMessagesFinishedOvertaking_OvertakingCoordinationMessages_Message(Port* p, OvertakingCoordinationMessagesFinishedOvertaking_OvertakingCoordinationMessages_Message* msg);
					bool_T MCC_CoordinatorComponent_overtakingAffiliate_exists_OvertakingCoordinationMessagesFinishedOvertaking_OvertakingCoordinationMessages_Message(Port* p);
			
			
			
	
			/*****
 			 *
 			 *  Component Functions
 			 *
 			 */
			/**
			 * @file 
			 * @author generated by Fraunhofer IEM 
			 * @brief Specification of Component of Type: Coordinator
			 * @details This files contains a description of the Coordinator in form of the CoordinatorComponent
			 * 			and all methods which can be executed on an Instance of this Component
			 */
				
						/**
						 * @brief Forward Declaration of the struct CoordinatorCoordinatorComponentStateChart which describes the behavior of CoordinatorComponent
						 * 
						 */
						 typedef struct CoordinatorCoordinatorComponentStateChart CoordinatorCoordinatorComponentStateChart;
						/**
						 * @brief Forward Declaration of the struct CoordinatorComponent
						 */
							typedef struct CoordinatorComponent CoordinatorComponent;
						
						/**
						*  
						* @brief Description for a ComponentInstance of Type: Coordinator
						* @details This struct describes a specific Component Instances which is typed over the Component: Coordinator
						*/
						struct CoordinatorComponent {
						
								uint8_T ID;	
						
						
									CoordinatorCoordinatorComponentStateChart* stateChart;	/**< The CoordinatorCoordinatorComponentStateChart of the Component Coordinator */
							
										Port communicatorPort; /**< A  Component's Port: communicator */
										Port overtakingInitiatorPort; /**< A  Component's Port: overtakingInitiator */
										Port overtakingAffiliatePort; /**< A  Component's Port: overtakingAffiliate */
								
						
								
								
						
								CoordinatorComponent * next;/**< A Pointer to the next component part, if this component ist part of a multipart in the CIC */
								
							};
					
						/**
						* @brief This Methodes intializes the Component: Coordinator
						* @details All struct members of the struct CoordinatorComponent are initialized
						* 
						* @param component The CoordinatorComponent to be initialized
						*/	
							void CoordinatorComponent_initialize(CoordinatorComponent* component);
				
						/**
						* @brief Creates a Instance of the Component: Coordinator
						* @details Allocates Memory for the struct CoordinatorComponent
						* @return A Pointer to the new created CoordinatorComponent
						*/
							CoordinatorComponent CoordinatorComponent_create();
				
						/**
						 * @brief Destroys a Component: Coordinator
						 * @details Frees the Memory for the struct CoordinatorComponent
						 * 
						* @param component The specific CoordinatorComponent to be destroyed
						 */
							void CoordinatorComponent_destroy(CoordinatorComponent* component);
						
						/**
						* @brief Executes the next Step of the behavior an Instance of the Component: Coordinator
						* @details The behavior of the Component:  Coordinator is executed.
						*			
						* @param component The CoordinatorComponent whose behavior shall be checked
						*/
							void CoordinatorComponent_processStep(CoordinatorComponent* component);
				
			
			
			
			
						/*Getter and Setter for Sending Values of Hybrid/Continuous ports*/
								CoordinatorCoordinatorComponentStateChart* CoordinatorComponent_getStateMachine(CoordinatorComponent* component);
						
						/**
								 * @brief Get the Port: communicator of an Instance of the Component: Coordinator
								 * @details The Pointer CoordinatorComponent::communicatorPort is returned
								 * 
								 * @param component The specific Instance CoordinatorComponent  of the Component: Coordinator whose Port shall be returned
								 * @return A Pointer to the Port communicator of the struct CoordinatorComponent
								 */
								Port* CoordinatorComponent_getcommunicator(CoordinatorComponent* component);
						/**
								 * @brief Get the Port: overtakingInitiator of an Instance of the Component: Coordinator
								 * @details The Pointer CoordinatorComponent::overtakingInitiatorPort is returned
								 * 
								 * @param component The specific Instance CoordinatorComponent  of the Component: Coordinator whose Port shall be returned
								 * @return A Pointer to the Port overtakingInitiator of the struct CoordinatorComponent
								 */
								Port* CoordinatorComponent_getovertakingInitiator(CoordinatorComponent* component);
						/**
								 * @brief Get the Port: overtakingAffiliate of an Instance of the Component: Coordinator
								 * @details The Pointer CoordinatorComponent::overtakingAffiliatePort is returned
								 * 
								 * @param component The specific Instance CoordinatorComponent  of the Component: Coordinator whose Port shall be returned
								 * @return A Pointer to the Port overtakingAffiliate of the struct CoordinatorComponent
								 */
								Port* CoordinatorComponent_getovertakingAffiliate(CoordinatorComponent* component);
					
						
		

	
	
			/*****
 			 *
 			 *  RealtimeStatechart Functions
 			 *
 			 */
			/**
			 * @file 
			 * @author generated by Fraunhofer IEM
			 * @brief Specification of Realtime-StateChart: CoordinatorCoordinatorComponent
			 * @details This files contains the description of the Realtime-StateChart: CoordinatorCoordinatorComponent and its behavior which is executed
			 */
			
			//ENUM
				/** 
				 * @brief This enum represents the States of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 */
					typedef enum {
						COORDINATORCOORDINATORCOMPONENT_INACTIVE,
						STATE_COORDINATORCOORDINATOR_MAIN /**< Represents the State: STATE_COORDINATORCOORDINATOR_MAIN of the Realtime-StateChart: CoordinatorCoordinatorComponent */
						,
								STATE_COORDINATORCOMMUNICATORIDLE /**< Represents the State: STATE_COORDINATORCOMMUNICATORIDLE of the Realtime-StateChart: CoordinatorCommunicatorCommunicatorPort */
						,		STATE_COORDINATORCOMMUNICATORWAITFORCOORDINATION /**< Represents the State: STATE_COORDINATORCOMMUNICATORWAITFORCOORDINATION of the Realtime-StateChart: CoordinatorCommunicatorCommunicatorPort */
						,		STATE_COORDINATORCOMMUNICATORWAITFOROVERTAKING /**< Represents the State: STATE_COORDINATORCOMMUNICATORWAITFOROVERTAKING of the Realtime-StateChart: CoordinatorCommunicatorCommunicatorPort */
						,		STATE_COORDINATOROVERTAKINGINITIATORIDLE /**< Represents the State: STATE_COORDINATOROVERTAKINGINITIATORIDLE of the Realtime-StateChart: CoordinatorOvertakingInitiatorOvertakingInitiatorPort */
						,		STATE_COORDINATOROVERTAKINGINITIATORINITIATING /**< Represents the State: STATE_COORDINATOROVERTAKINGINITIATORINITIATING of the Realtime-StateChart: CoordinatorOvertakingInitiatorOvertakingInitiatorPort */
						,		STATE_COORDINATOROVERTAKINGINITIATORPASSING /**< Represents the State: STATE_COORDINATOROVERTAKINGINITIATORPASSING of the Realtime-StateChart: CoordinatorOvertakingInitiatorOvertakingInitiatorPort */
						,		STATE_COORDINATOROVERTAKINGAFFILIATEIDLE /**< Represents the State: STATE_COORDINATOROVERTAKINGAFFILIATEIDLE of the Realtime-StateChart: CoordinatorOvertakingAffiliateOvertakingAffiliatePort */
						,		STATE_COORDINATOROVERTAKINGAFFILIATECOORDINATING /**< Represents the State: STATE_COORDINATOROVERTAKINGAFFILIATECOORDINATING of the Realtime-StateChart: CoordinatorOvertakingAffiliateOvertakingAffiliatePort */
						,		STATE_COORDINATOROVERTAKINGAFFILIATEGETTINGPASSED /**< Represents the State: STATE_COORDINATOROVERTAKINGAFFILIATEGETTINGPASSED of the Realtime-StateChart: CoordinatorOvertakingAffiliateOvertakingAffiliatePort */	
					}CoordinatorCoordinatorComponentState;
				
				
					/**
					 * 
					 * @brief Description of the Realtime-StateChart: CoordinatorCoordinatorComponent
					 * @details This struct represents the  Realtime-StateChart: CoordinatorCoordinatorComponent and its States
					 */
				struct CoordinatorCoordinatorComponentStateChart {	
						CoordinatorComponent * parentComponent;/**< A pointer to the parent ComponentInstance of Type: CoordinatorComponent , which has this Realtime-StateChart as its behavior */			
				
				
									CoordinatorCoordinatorComponentState currentStateOfCoordinatorCoordinatorComponent;/**< The current State of the Realtime-StateChart: CoordinatorCoordinatorComponent */
				
							CoordinatorCoordinatorComponentState currentStateOfCoordinatorCommunicatorCommunicatorPort;/**< Represents the state of region: CoordinatorCommunicatorCommunicatorPort */
							CoordinatorCoordinatorComponentState currentStateOfCoordinatorOvertakingInitiatorOvertakingInitiatorPort;/**< Represents the state of region: CoordinatorOvertakingInitiatorOvertakingInitiatorPort */
							CoordinatorCoordinatorComponentState currentStateOfCoordinatorOvertakingAffiliateOvertakingAffiliatePort;/**< Represents the state of region: CoordinatorOvertakingAffiliateOvertakingAffiliatePort */
				
						bool_t CoordinatorCoordinatorComponent_isExecutable;/**< Execution Verifier of RTSC: CoordinatorCoordinatorComponent. This variable is used to ensure that a RTSC is executed only once per execution cycle */
							bool_t CoordinatorCommunicatorCommunicatorPort_isExecutable;/**< Execution Verifier of RTSC: CoordinatorCommunicatorCommunicatorPort. This variable is used to ensure that a RTSC is executed only once per execution cycle */
							bool_t CoordinatorOvertakingInitiatorOvertakingInitiatorPort_isExecutable;/**< Execution Verifier of RTSC: CoordinatorOvertakingInitiatorOvertakingInitiatorPort. This variable is used to ensure that a RTSC is executed only once per execution cycle */
							bool_t CoordinatorOvertakingAffiliateOvertakingAffiliatePort_isExecutable;/**< Execution Verifier of RTSC: CoordinatorOvertakingAffiliateOvertakingAffiliatePort. This variable is used to ensure that a RTSC is executed only once per execution cycle */
					
							Clock coordinatorCoordinationTimeCoordinatorCoordinatorComponentClock;/**< The Clock: CoordinatorCoordinationTimeCoordinatorCoordinatorComponent */
				
				
					
				
							int32_T coordinationTimeout; /**< The Realtime-StateChart Variable: coordinationTimeout of Type: int32; */
							bool_T coordinatorIsBusy; /**< The Realtime-StateChart Variable: coordinatorIsBusy of Type: boolean; */
				
				
				
						
				
					} ;
			
			//METHOD STUBS
				/**
				 * @brief Creates an Instance of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details Allocates Memory for the struct CoordinatorCoordinatorComponentStateChart
				 * 
				 * @param parentComponent An Instance of the Component: CoordinatorComponent which behavior is described via this Realtime-StateChart
				 * @return A Pointer to the new created CoordinatorCoordinatorComponentStateChart
				 */
					CoordinatorCoordinatorComponentStateChart* CoordinatorCoordinatorComponentStateChart_create(CoordinatorComponent* parentComponent);
				/**
				 * @brief Initializes an Instance of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details All struct members of the struct CoordinatorCoordinatorComponentStateChart are initialized,
				 *			and all Regions of the Realtime-StateChart are initialized, too.
				 * 
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart to be initialized
				 */
					void CoordinatorCoordinatorComponentStateChart_initialize(CoordinatorCoordinatorComponentStateChart* rtsc);
				/**
				 * @brief Initializes the Region: CoordinatorCommunicatorCommunicatorPort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details The Member CoordinatorCoordinatorComponentStateChart::currentStateOfCoordinatorCommunicatorCommunicatorPort is initialized
				 * 
				 * @param stateChart The specific CoordinatorCoordinatorComponentStateChart whose Region shall be initialized
				 */
				
					void initializeCoordinatorCommunicatorCommunicatorPortRegion(CoordinatorCoordinatorComponentStateChart* stateChart);
				/**
				 * @brief Initializes the Region: CoordinatorOvertakingInitiatorOvertakingInitiatorPort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details The Member CoordinatorCoordinatorComponentStateChart::currentStateOfCoordinatorOvertakingInitiatorOvertakingInitiatorPort is initialized
				 * 
				 * @param stateChart The specific CoordinatorCoordinatorComponentStateChart whose Region shall be initialized
				 */
				
					void initializeCoordinatorOvertakingInitiatorOvertakingInitiatorPortRegion(CoordinatorCoordinatorComponentStateChart* stateChart);
				/**
				 * @brief Initializes the Region: CoordinatorOvertakingAffiliateOvertakingAffiliatePort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details The Member CoordinatorCoordinatorComponentStateChart::currentStateOfCoordinatorOvertakingAffiliateOvertakingAffiliatePort is initialized
				 * 
				 * @param stateChart The specific CoordinatorCoordinatorComponentStateChart whose Region shall be initialized
				 */
				
					void initializeCoordinatorOvertakingAffiliateOvertakingAffiliatePortRegion(CoordinatorCoordinatorComponentStateChart* stateChart);
				/**
				 * @brief Destroys the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details Frees the Memory for the struct CoordinatorCoordinatorComponentStateChart
				 * 
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart to be destroyed
				 */	
					void CoordinatorCoordinatorComponentStateChart_destroy(CoordinatorCoordinatorComponentStateChart* rtsc);		
				/**
				 * @brief Executes the next Step of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * 
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart to be executed
				 */	
					void CoordinatorCoordinatorComponentStateChart_processStep(CoordinatorCoordinatorComponentStateChart* rtsc);
			
				/**
				 * @brief Leaves the Region: communicatorPort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details This method is called, whenever a the Region: communicatorPort is left.
				 * 			The correct State is set and all Exit-Events are executed.
				 *
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart whose Region: communicatorPort shall be exit
				 */
					void CoordinatorCommunicatorCommunicatorPortStateChart_exit(CoordinatorCoordinatorComponentStateChart* rtsc);
				/**
				 * @brief Leaves the Region: overtakingInitiatorPort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details This method is called, whenever a the Region: overtakingInitiatorPort is left.
				 * 			The correct State is set and all Exit-Events are executed.
				 *
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart whose Region: overtakingInitiatorPort shall be exit
				 */
					void CoordinatorOvertakingInitiatorOvertakingInitiatorPortStateChart_exit(CoordinatorCoordinatorComponentStateChart* rtsc);
				/**
				 * @brief Leaves the Region: overtakingAffiliatePort of the Realtime-StateChart: CoordinatorCoordinatorComponent
				 * @details This method is called, whenever a the Region: overtakingAffiliatePort is left.
				 * 			The correct State is set and all Exit-Events are executed.
				 *
				 * @param rtsc The specific CoordinatorCoordinatorComponentStateChart whose Region: overtakingAffiliatePort shall be exit
				 */
					void CoordinatorOvertakingAffiliateOvertakingAffiliatePortStateChart_exit(CoordinatorCoordinatorComponentStateChart* rtsc);
						
				/**
				 * @brief Returns wether the Realtime-StateChart: CoordinatorCoordinatorComponent is in a specific State
				 * 
				 * @param rtsc The specific Realtime-StateChart: CoordinatorCoordinatorComponentStateChart
				 * @param state One of the States of the Enum: CoordinatorCoordinatorComponentState
				 * 
				 * @return True, If the Realtime-StateChart is in the specific State, otherwise False
				 */	
					bool_t CoordinatorCoordinatorComponentStateChart_isInState(CoordinatorCoordinatorComponentStateChart* rtsc, CoordinatorCoordinatorComponentState state);





#ifdef __cplusplus
  }
#endif
		#endif /* COORDINATORCOMPONENT_Interface_H_ */
