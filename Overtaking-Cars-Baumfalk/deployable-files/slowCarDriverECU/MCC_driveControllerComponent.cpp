// Container HeaderFile
#include "MCC_driveControllerComponent.h"

/**
*  
* @brief The options a port declares
* @details These options are used in the struct driveControllerComponent_Builder
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
* @brief The builder struct for the Component Type: DriveController
* @details This is a realization of the builder pattern, the struct represents a Concrete Builder
*/ 
typedef struct driveControllerComponent_Builder {
		uint8_T ID;
		//create Builder Functions for each Port
			PortStatus VELOCITY; /**< The status of port velocity */
			PortHandle* (*createVELOCITYHandle)(struct driveControllerComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port velocity */
			struct port_option VELOCITY_op; /**< The port_option for the PortHandle of port velocity */
			PortStatus ANGLE; /**< The status of port angle */
			PortHandle* (*createANGLEHandle)(struct driveControllerComponent_Builder*, PortHandle*); /**< The builder method to create a PortHandle for port angle */
			struct port_option ANGLE_op; /**< The port_option for the PortHandle of port angle */
		void (*velocityPortAccessFunction) (int16_T*); /**access function pointer for continuous port */
		/*FIXME add initialize and destroy function pointer*/
		void (*anglePortAccessFunction) (int8_T*); /**access function pointer for continuous port */
		/*FIXME add initialize and destroy function pointer*/
}driveControllerComponent_Builder;

/**
*
*@brief A initializer for the struct driveControllerComponent_Builder
*@details Shall be used when creating a struct driveControllerComponent_Builder manually
*/
static const driveControllerComponent_Builder INIT_BUILDER = { 
	0, 
	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
,	PORT_DEACTIVATED, 
	NULL, 
	{0,0}
};

/**
*
*@brief The pool of component instance of Component Type DriveController
*@details The container manages the resource instances in this pool, and this pool allocates the memory for component instances statically
*/
	static DriveControllerComponent instancePool [1];
	static int pool_length = 0;
	static int pool_index = 0;

/**
*
*@brief method to test if message exists for DirectedTypedPort velocity 
*@details looksup if the  buffer contains a messages
*
*/
bool_t MCC_DriveControllerComponent_velocity_exists_value(Port* port){
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
*@brief The receive method for DirectedTypedPort velocity 
*
*/	
bool_t MCC_DriveControllerComponent_velocity_recv_value(Port* port, int16_T* msg){
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
*@brief method to test if message exists for DirectedTypedPort angle 
*@details looksup if the  buffer contains a messages
*
*/
bool_t MCC_DriveControllerComponent_angle_exists_value(Port* port){
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
*@brief The receive method for DirectedTypedPort angle 
*
*/	
bool_t MCC_DriveControllerComponent_angle_recv_value(Port* port, int8_T* msg){
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
*@brief The builder for component instance of Component Type DriveController
*@details This method creates and initializes a component instance properly by using the struct driveControllerComponent_Builder
*/
	static DriveControllerComponent* MCC_DriveControllerComponent_Builder(driveControllerComponent_Builder* b){
		instancePool[pool_index].ID = b->ID;
		instancePool[pool_index].velocityPortAccessFunction = b->velocityPortAccessFunction;
		instancePool[pool_index].anglePortAccessFunction = b->anglePortAccessFunction;
		//For each port initialize it
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

static PortHandle* create_VELOCITYLocalHandle(driveControllerComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+1*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->VELOCITY_op.local_option.pubID;
	hndl->subID = b->VELOCITY_op.local_option.subID;
	//create space for Subscriber
	hndl->numOfSubs = 1;
	subscribeToMessage(&(hndl->localSubscribers[0] ),hndl->subID, 0, 1, sizeof(int16_T), true);
	return ptr;
}
static PortHandle* create_ANGLELocalHandle(driveControllerComponent_Builder* b, PortHandle *ptr){
	ptr->type = PORT_HANDLE_TYPE_LOCAL;
	LocalHandle* hndl = (LocalHandle*) malloc(sizeof(LocalHandle)+1*sizeof(LocalSubscriber));
	ptr->concreteHandle = hndl;
	hndl->pubID = b->ANGLE_op.local_option.pubID;
	hndl->subID = b->ANGLE_op.local_option.subID;
	//create space for Subscriber
	hndl->numOfSubs = 1;
	subscribeToMessage(&(hndl->localSubscribers[0] ),hndl->subID, 0, 1, sizeof(int8_T), true);
	return ptr;
}


/**
 * @brief Create a component instance with the given id.
 * 
 * @details Creates a component instance using the builder and the configuration options, and also configures the port instances.
 * 
 * @param ID the identifier of the component instance
 */
DriveControllerComponent* MCC_create_DriveControllerComponent(uint8_T ID){
	struct driveControllerComponent_Builder b = INIT_BUILDER;
	switch(ID){
		case CI_DRIVECONTROLLERSDRIVECONTROLLER:
			b.ID = ID;
			b.velocityPortAccessFunction=&CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand;
			b.anglePortAccessFunction=&CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand;
			b.VELOCITY = PORT_ACTIVE;
			b.createVELOCITYHandle = &create_VELOCITYLocalHandle;
			b.VELOCITY_op.local_option.pubID = -31489;
			b.VELOCITY_op.local_option.subID = 9589;
			b.ANGLE = PORT_ACTIVE;
			b.createANGLEHandle = &create_ANGLELocalHandle;
			b.ANGLE_op.local_option.pubID = 18413;
			b.ANGLE_op.local_option.subID = -48;
		break;
	default:
		break;
	}
	return MCC_DriveControllerComponent_Builder(&b);
}
	
