// Container HeaderFile
#include "MCC_courseControlComponent.h"

/**
*  
* @brief The options a port declares
* @details These options are used in the struct courseControlComponent_Builder
*/ 
struct port_option {
	union {
		struct{
		uint16_T pubID;
		uint16_T subID;
		} local_option;
		struct {
			uint8_T ownAddress;
			uint8_T otherAddress;
		} i2c_option;
		struct {
			char* publishingTopic;
			char* subscriptionTopic;
		} mqtt_option;
	};
};

/**
*  
* @brief The builder struct for the Component Type: CourseControl
* @details This is a realization of the builder pattern, the struct represents a Concrete Builder
*/ 
typedef struct courseControlComponent_Builder {
		uint8_T ID;
		//create Builder Functions for each Port
			PortStatus FRONTDISTANCE; /**< The status of port frontDistance */
			PortHandle* (*createFRONTDISTANCEHandle)(struct courseControlComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port frontDistance */
			struct port_option FRONTDISTANCE_op; /**< The port_option for the PortHandle of port frontDistance */
			PortStatus REARDISTANCE; /**< The status of port rearDistance */
			PortHandle* (*createREARDISTANCEHandle)(struct courseControlComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port rearDistance */
			struct port_option REARDISTANCE_op; /**< The port_option for the PortHandle of port rearDistance */
			PortStatus COURSECONTROL; /**< The status of port courseControl */
			PortHandle* (*createCOURSECONTROLHandle)(struct courseControlComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port courseControl */
			struct port_option COURSECONTROL_op; /**< The port_option for the PortHandle of port courseControl */
			PortStatus VELOCITY; /**< The status of port velocity */
			PortHandle* (*createVELOCITYHandle)(struct courseControlComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port velocity */
			struct port_option VELOCITY_op; /**< The port_option for the PortHandle of port velocity */
			PortStatus ANGLE; /**< The status of port angle */
			PortHandle* (*createANGLEHandle)(struct courseControlComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port angle */
			struct port_option ANGLE_op; /**< The port_option for the PortHandle of port angle */
}courseControlComponent_Builder;

/**
*
*@brief A initializer for the struct courseControlComponent_Builder
*@details Shall be used when creating a struct courseControlComponent_Builder manually
*/
static const courseControlComponent_Builder INIT_BUILDER = { 
	0, 
	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
,	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
,	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
,	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
,	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
};

/**
*
*@brief The pool of component instance of Component Type CourseControl
*@details The container manages the resource instances in this pool, and this pool allocates the memory for component instances statically
*/
	static CourseControlComponent instancePool [1];
	static int pool_length = 0;
	static int pool_index = 0;

/**
*
*@brief method to test if message exists for DirectedTypedPort frontDistance 
*@details looksup if the  buffer contains a messages
*
*/
bool_t MCC_CourseControlComponent_frontDistance_exists_value(Port* port){
			LocalHandle* localHandle;
			int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_LOCAL:
			localHandle = (LocalHandle*) port->handle->concreteHandle;
			MessageBuffer* buf = NULL;
			//dont handle a pointer over the the buffer, because msg is already a pointer
			for (i = 0; i < localHandle->numOfSubs; i++) {
				if (localHandle->localSubscribers[i].msgID == 0) {
					buf = localHandle->localSubscribers[i].buffer;
					break;
				}
			}
			return MessageBuffer_doesMessageExists(buf);
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The receive method for DirectedTypedPort frontDistance 
*
*/	
bool_t MCC_CourseControlComponent_frontDistance_recv_value(Port* port, int32_T* msg){
			LocalHandle* localHandle;
			int i = 0;
	switch(port->handle->type) {
			case PORT_HANDLE_TYPE_LOCAL:
				localHandle = (LocalHandle*) port->handle->concreteHandle;
				//dont handle a pointer over the the buffer, because msg is already a pointer
				publishMessage(localHandle->pubID, 0, msg);
				break;
	default:
		break;
	}
	return false;
}	
			
/**
*
*@brief method to test if message exists for DirectedTypedPort rearDistance 
*@details looksup if the  buffer contains a messages
*
*/
bool_t MCC_CourseControlComponent_rearDistance_exists_value(Port* port){
			LocalHandle* localHandle;
			int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_LOCAL:
			localHandle = (LocalHandle*) port->handle->concreteHandle;
			MessageBuffer* buf = NULL;
			//dont handle a pointer over the the buffer, because msg is already a pointer
			for (i = 0; i < localHandle->numOfSubs; i++) {
				if (localHandle->localSubscribers[i].msgID == 0) {
					buf = localHandle->localSubscribers[i].buffer;
					break;
				}
			}
			return MessageBuffer_doesMessageExists(buf);
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The receive method for DirectedTypedPort rearDistance 
*
*/	
bool_t MCC_CourseControlComponent_rearDistance_recv_value(Port* port, int32_T* msg){
			LocalHandle* localHandle;
			int i = 0;
	switch(port->handle->type) {
			case PORT_HANDLE_TYPE_LOCAL:
				localHandle = (LocalHandle*) port->handle->concreteHandle;
				//dont handle a pointer over the the buffer, because msg is already a pointer
				publishMessage(localHandle->pubID, 0, msg);
				break;
	default:
		break;
	}
	return false;
}	
			
/**
*
*@brief The Check method for DiscretePort courseControl  and message OvertakingPermissionMessagesGrantPermission 
*@details Checks if  buffer contains a message of type OvertakingPermissionMessagesGrantPermission
*
*/	
bool_t MCC_CourseControlComponent_courseControl_exists_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(Port* port){
	I2cHandle* i2cHandle;
	int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			MessageBuffer* buffer = NULL;
			for (i = 0; i < i2cHandle->numOfReceivers; i++) {
				if (strcmp(i2cHandle->receivers[i].messageTypeName, "OvertakingPermissionMessagesGrantPermission") == 0){
					buffer = i2cHandle->receivers[i].buffer;
					break;
				}
			}
			return MessageBuffer_doesMessageExists(buffer);
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The receive method for DiscretePort courseControl and message OvertakingPermissionMessagesGrantPermission
*@details Receives  a message of type OvertakingPermissionMessagesGrantPermission
*
*/
bool_t MCC_CourseControlComponent_courseControl_recv_OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message(Port* port, OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message* msg){
	I2cHandle* i2cHandle;
	int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			MessageBuffer* buffer = NULL;
			for (i = 0; i < i2cHandle->numOfReceivers; i++) {
				if (strcmp(i2cHandle->receivers[i].messageTypeName, "OvertakingPermissionMessagesGrantPermission") == 0){
					buffer = i2cHandle->receivers[i].buffer;
					break;
				}
			}
			return MessageBuffer_dequeue(buffer, msg); 
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The Check method for DiscretePort courseControl  and message OvertakingPermissionMessagesDenyPermission 
*@details Checks if  buffer contains a message of type OvertakingPermissionMessagesDenyPermission
*
*/	
bool_t MCC_CourseControlComponent_courseControl_exists_OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message(Port* port){
	I2cHandle* i2cHandle;
	int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			MessageBuffer* buffer = NULL;
			for (i = 0; i < i2cHandle->numOfReceivers; i++) {
				if (strcmp(i2cHandle->receivers[i].messageTypeName, "OvertakingPermissionMessagesDenyPermission") == 0){
					buffer = i2cHandle->receivers[i].buffer;
					break;
				}
			}
			return MessageBuffer_doesMessageExists(buffer);
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The receive method for DiscretePort courseControl and message OvertakingPermissionMessagesDenyPermission
*@details Receives  a message of type OvertakingPermissionMessagesDenyPermission
*
*/
bool_t MCC_CourseControlComponent_courseControl_recv_OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message(Port* port, OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message* msg){
	I2cHandle* i2cHandle;
	int i = 0;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			MessageBuffer* buffer = NULL;
			for (i = 0; i < i2cHandle->numOfReceivers; i++) {
				if (strcmp(i2cHandle->receivers[i].messageTypeName, "OvertakingPermissionMessagesDenyPermission") == 0){
					buffer = i2cHandle->receivers[i].buffer;
					break;
				}
			}
			return MessageBuffer_dequeue(buffer, msg); 
			break;
	default:
		break;	
	}
	return false;
}
/**
*
*@brief The send method for DiscretePort courseControl and message OvertakingPermissionMessagesRequestPermission
*@details Send a message of type OvertakingPermissionMessagesRequestPermission  
*
*/	
void MCC_CourseControlComponent_courseControl_send_OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message(Port* port, OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message* msg){
	I2cHandle* i2cHandle;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			sendI2cMessage(i2cHandle->otherI2cAddress, "OvertakingPermissionMessagesRequestPermission", (byte *) msg, sizeof(OvertakingPermissionMessagesRequestPermission_OvertakingPermissionMessages_Message)); 
			break;
	default:
		break;	
	}
}
/**
*
*@brief The send method for DiscretePort courseControl and message OvertakingPermissionMessagesExecutedOvertaking
*@details Send a message of type OvertakingPermissionMessagesExecutedOvertaking  
*
*/	
void MCC_CourseControlComponent_courseControl_send_OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message(Port* port, OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message* msg){
	I2cHandle* i2cHandle;
	switch(port->handle->type) {
		case PORT_HANDLE_TYPE_I2C:
			i2cHandle = (I2cHandle*) port->handle->concreteHandle;
			sendI2cMessage(i2cHandle->otherI2cAddress, "OvertakingPermissionMessagesExecutedOvertaking", (byte *) msg, sizeof(OvertakingPermissionMessagesExecutedOvertaking_OvertakingPermissionMessages_Message)); 
			break;
	default:
		break;	
	}
}
/**
*
*@brief The send method for DirectedTypedPort velocity 
*
*/	
void MCC_CourseControlComponent_velocity_send_value(Port* port, int16_T* msg){
			LocalHandle* localHandle;
	switch(port->handle->type) {
			case PORT_HANDLE_TYPE_LOCAL:
				localHandle = (LocalHandle*) port->handle->concreteHandle;
				//dont handle a pointer over the the buffer, because msg is already a pointer
				publishMessage(localHandle->pubID, 0, msg);
				break;
	default:
		break;	
	}
}
			
/**
*
*@brief The send method for DirectedTypedPort angle 
*
*/	
void MCC_CourseControlComponent_angle_send_value(Port* port, int8_T* msg){
			LocalHandle* localHandle;
	switch(port->handle->type) {
			case PORT_HANDLE_TYPE_LOCAL:
				localHandle = (LocalHandle*) port->handle->concreteHandle;
				//dont handle a pointer over the the buffer, because msg is already a pointer
				publishMessage(localHandle->pubID, 0, msg);
				break;
	default:
		break;	
	}
}
			
 

/**
*
*@brief The builder for component instance of Component Type CourseControl
*@details This method creates and initializes a component instance properly by using the struct courseControlComponent_Builder
*/
	static CourseControlComponent* MCC_CourseControlComponent_Builder(courseControlComponent_Builder* b){
		instancePool[pool_index].ID = b->ID;
		instancePool[pool_index].stateChart = CourseControlCourseControlComponentStateChart_create(
			&instancePool[pool_index]);
		//call init after RTSC was created
		CourseControlComponent_initialize(&instancePool[pool_index]);
		//For each port initialize it
		if(b->FRONTDISTANCE != PORT_DEACTIVATED) {
			instancePool[pool_index].frontDistancePort.status = b->FRONTDISTANCE;
			instancePool[pool_index].frontDistancePort.handle = (PortHandle*) malloc(sizeof(PortHandle));
 			instancePool[pool_index].frontDistancePort.handle->port = &(instancePool[pool_index].frontDistancePort);
			b->createFRONTDISTANCEHandle(b, (instancePool[pool_index].frontDistancePort.handle));
			//instancePool[pool_index].frontDistancePort.handle->port = &(instancePool[pool_index].frontDistancePort);
		}
		if(b->REARDISTANCE != PORT_DEACTIVATED) {
			instancePool[pool_index].rearDistancePort.status = b->REARDISTANCE;
			instancePool[pool_index].rearDistancePort.handle = (PortHandle*) malloc(sizeof(PortHandle));
 			instancePool[pool_index].rearDistancePort.handle->port = &(instancePool[pool_index].rearDistancePort);
			b->createREARDISTANCEHandle(b, (instancePool[pool_index].rearDistancePort.handle));
			//instancePool[pool_index].rearDistancePort.handle->port = &(instancePool[pool_index].rearDistancePort);
		}
		if(b->COURSECONTROL != PORT_DEACTIVATED) {
			instancePool[pool_index].courseControlPort.status = b->COURSECONTROL;
			instancePool[pool_index].courseControlPort.handle = (PortHandle*) malloc(sizeof(PortHandle));
 			instancePool[pool_index].courseControlPort.handle->port = &(instancePool[pool_index].courseControlPort);
			b->createCOURSECONTROLHandle(b, (instancePool[pool_index].courseControlPort.handle));
			//instancePool[pool_index].courseControlPort.handle->port = &(instancePool[pool_index].courseControlPort);
		}
		if(b->VELOCITY != PORT_DEACTIVATED) {
			instancePool[pool_index].velocityPort.status = b->VELOCITY;
			instancePool[pool_index].velocityPort.handle = (PortHandle*) malloc(sizeof(PortHandle));
 			instancePool[pool_index].velocityPort.handle->port = &(instancePool[pool_index].velocityPort);
			b->createVELOCITYHandle(b, (instancePool[pool_index].velocityPort.handle));
			//instancePool[pool_index].velocityPort.handle->port = &(instancePool[pool_index].velocityPort);
		}
		if(b->ANGLE != PORT_DEACTIVATED) {
			instancePool[pool_index].anglePort.status = b->ANGLE;
			instancePool[pool_index].anglePort.handle = (PortHandle*) malloc(sizeof(PortHandle));
 			instancePool[pool_index].anglePort.handle->port = &(instancePool[pool_index].anglePort);
			b->createANGLEHandle(b, (instancePool[pool_index].anglePort.handle));
			//instancePool[pool_index].anglePort.handle->port = &(instancePool[pool_index].anglePort);
		}
	
		return &instancePool[pool_index++];
	}

static PortHandle* create_FRONTDISTANCELocalHandle(courseControlComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+1*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->FRONTDISTANCE_op.local_option.pubID;
	hndl->subID = b->FRONTDISTANCE_op.local_option.subID;
	//create space for Subscriber
	hndl->numOfSubs = 1;
	subscribeToMessage(&(hndl->localSubscribers[0] ),hndl->subID, 0, 1, sizeof(int32_T), true);
	return ptr;
}
static PortHandle* create_REARDISTANCELocalHandle(courseControlComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+1*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->REARDISTANCE_op.local_option.pubID;
	hndl->subID = b->REARDISTANCE_op.local_option.subID;
	//create space for Subscriber
	hndl->numOfSubs = 1;
	subscribeToMessage(&(hndl->localSubscribers[0] ),hndl->subID, 0, 1, sizeof(int32_T), true);
	return ptr;
}
/**
 * @brief The Builder for an I2cHandle for port courseControl
 *
 * @details creates a struct I2cHandle which encapsulates the logic for communication 
 */
static PortHandle* create_COURSECONTROLI2cHandle(courseControlComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_I2C;
	//create the handle for the discrete port COURSECONTROL with all its message types
	I2cHandle* handle = (I2cHandle*) malloc(sizeof(I2cHandle)+2*sizeof(I2cReceiver));
	handle->numOfReceivers = 2;
	//register a receiver for every message type of the port
	initAndRegisterI2cReceiver(&(handle->receivers[0]), 
								"OvertakingPermissionMessagesGrantPermission",
								5,
								sizeof(OvertakingPermissionMessagesGrantPermission_OvertakingPermissionMessages_Message),
								 false );
	initAndRegisterI2cReceiver(&(handle->receivers[1]), 
								"OvertakingPermissionMessagesDenyPermission",
								5,
								sizeof(OvertakingPermissionMessagesDenyPermission_OvertakingPermissionMessages_Message),
								 false );
	handle->ownI2cAddress = b->COURSECONTROL_op.i2c_option.ownAddress;
	handle->otherI2cAddress = b->COURSECONTROL_op.i2c_option.otherAddress;
	ptr->concreteHandle = handle;
	
	return ptr;
}
static PortHandle* create_VELOCITYLocalHandle(courseControlComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+0*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->VELOCITY_op.local_option.pubID;
	hndl->subID = b->VELOCITY_op.local_option.subID;
	return ptr;
}
static PortHandle* create_ANGLELocalHandle(courseControlComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+0*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->ANGLE_op.local_option.pubID;
	hndl->subID = b->ANGLE_op.local_option.subID;
	return ptr;
}


/**
 * @brief Create a component instance with the given id.
 * 
 * @details Creates a component instance using the builder and the configuration options, and also configures the port instances.
 * 
 * @param ID the identifier of the component instance
 */
CourseControlComponent* MCC_create_CourseControlComponent(uint8_T ID){
	struct courseControlComponent_Builder b = INIT_BUILDER;
	switch(ID){
		case CI_COURSECONTROLFCOURSECONTROL:
			b.ID = ID;
			b.REARDISTANCE = PORT_ACTIVE;
			b.createREARDISTANCEHandle = &create_REARDISTANCELocalHandle;
			b.REARDISTANCE_op.local_option.pubID = 30090;
			b.REARDISTANCE_op.local_option.subID = 715;
			b.VELOCITY = PORT_ACTIVE;
			b.createVELOCITYHandle = &create_VELOCITYLocalHandle;
			b.VELOCITY_op.local_option.pubID = -31865;
			b.VELOCITY_op.local_option.subID = 31295;
			b.ANGLE = PORT_ACTIVE;
			b.createANGLEHandle = &create_ANGLELocalHandle;
			b.ANGLE_op.local_option.pubID = -21702;
			b.ANGLE_op.local_option.subID = 31687;
			b.FRONTDISTANCE = PORT_ACTIVE;
			b.createFRONTDISTANCEHandle = &create_FRONTDISTANCELocalHandle;
			b.FRONTDISTANCE_op.local_option.pubID = -24519;
			b.FRONTDISTANCE_op.local_option.subID = -26761;
			b.COURSECONTROL = PORT_ACTIVE;
			b.createCOURSECONTROLHandle = &create_COURSECONTROLI2cHandle;
			b.COURSECONTROL_op.i2c_option.ownAddress = 9;
			b.COURSECONTROL_op.i2c_option.otherAddress = 1;
		break;
	default:
		break;
	}
	return MCC_CourseControlComponent_Builder(&b);
}
	
