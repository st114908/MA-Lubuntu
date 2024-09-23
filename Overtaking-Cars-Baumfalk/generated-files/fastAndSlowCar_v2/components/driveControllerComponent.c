		// Standard library
		
		#include "../components/driveControllerComponent_Interface.h"
		

		
	
		void DriveControllerComponent_initialize(DriveControllerComponent* component) {
		
			/*Initialize all DirectedTypedPorts*/
			Clock_reset(component->velocityPortClock);
			Clock_reset(component->anglePortClock);
		}
		

		DriveControllerComponent DriveControllerComponent_create(void) {
		
			DriveControllerComponent component;
		
			return component;
		}
		

		void DriveControllerComponent_destroy(DriveControllerComponent* component) {
			if (component != NULL) {
		
				//suicide
				free(component);
			}
		}
		
		
		void DriveControllerComponent_processStep(DriveControllerComponent* component) {
				
		
			
				/*Receive Messages for Continuous In Ports*/
				{
				/*FIXME Currently a Continuous Port has no update interval. We set a fixed interval of 1ms.  An interval has to be defined in the model in the future.*/ 
				int localTimeInterval = 1;
				if (Clock_getTime(component->velocityPortClock) >= localTimeInterval * 1) //interval of continuous port
				{	
					bool_T changed = false;
					changed =  getterOf_velocity(&component->velocityPort, &component->velocity);
					Clock_reset(component->velocityPortClock);
					if(changed){
						component->velocityPortAccessFunction(&component->velocity);
					}
				}
				}
				{
				/*FIXME Currently a Continuous Port has no update interval. We set a fixed interval of 1ms.  An interval has to be defined in the model in the future.*/ 
				int localTimeInterval = 1;
				if (Clock_getTime(component->anglePortClock) >= localTimeInterval * 1) //interval of continuous port
				{	
					bool_T changed = false;
					changed =  getterOf_angle(&component->anglePort, &component->angle);
					Clock_reset(component->anglePortClock);
					if(changed){
						component->anglePortAccessFunction(&component->angle);
					}
				}
				}
			
		}

		
		bool_T getterOf_velocity(Port* velocityPort, int16_T* velocity) {
			if(MCC_DriveControllerComponent_velocity_exists_value(velocityPort)){
				MCC_DriveControllerComponent_velocity_recv_value(velocityPort, velocity);
			return true;
			}
			else return false;
		}
			
			bool_T getterOf_angle(Port* anglePort, int8_T* angle) {
			if(MCC_DriveControllerComponent_angle_exists_value(anglePort)){
				MCC_DriveControllerComponent_angle_recv_value(anglePort, angle);
			return true;
			}
			else return false;
		}

		

