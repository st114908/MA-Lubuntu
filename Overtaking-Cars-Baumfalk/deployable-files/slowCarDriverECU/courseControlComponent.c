		// Standard library
		
		#include "courseControlComponent_Interface.h"
		

		
	
		void CourseControlComponent_initialize(CourseControlComponent* component) {
		
			CourseControlCourseControlComponentStateChart_initialize(
					component->stateChart);
			/*Initialize all DirectedTypedPorts*/
			Clock_reset(component->frontDistancePortClock);
			Clock_reset(component->rearDistancePortClock);
			Clock_reset(component->velocityPortClock);
			Clock_reset(component->anglePortClock);
		}
		

		CourseControlComponent CourseControlComponent_create(void) {
		
			CourseControlComponent component;
		
			component.stateChart = NULL;
		
			return component;
		}
		

		void CourseControlComponent_destroy(CourseControlComponent* component) {
			if (component != NULL) {
				CourseControlCourseControlComponentStateChart_destroy(
						component->stateChart);
		
				//temporarly deactivated
				//	Port_destroy(component->courseControlPort);
		
				//suicide
				free(component);
			}
		}
		
		
		void CourseControlComponent_processStep(CourseControlComponent* component) {
				
		
					component->stateChart->CourseControlCourseControlComponent_isExecutable = true;
					component->stateChart->CourseControlCourseControlCourseControlPort_isExecutable = true;
					component->stateChart->CourseControlCourseControl_mainDriving_isExecutable = true;
				CourseControlCourseControlComponentStateChart_processStep(component->stateChart);
				/*Send Messages for Hybrid Out Ports*/	
				if (Clock_getTime(component->velocityPortClock) >= 30 * 1.0) //interval of hybrid port
				{
					setterOf_velocity(&component->velocityPort, &component->stateChart->velocity);
					Clock_reset(component->velocityPortClock);
				}
				if (Clock_getTime(component->anglePortClock) >= 30 * 1.0) //interval of hybrid port
				{
					setterOf_angle(&component->anglePort, &component->stateChart->angle);
					Clock_reset(component->anglePortClock);
				}
				/*Receive Messages for Hybrid In Ports*/
				if (Clock_getTime(component->frontDistancePortClock) >= 30 * 1.0) //interval of hybrid port
				{
					bool_T changed = false;
					changed = getterOf_frontDistance(&component->frontDistancePort, &component->stateChart->frontDistance);
					Clock_reset(component->frontDistancePortClock);
				}
				if (Clock_getTime(component->rearDistancePortClock) >= 30 * 1.0) //interval of hybrid port
				{
					bool_T changed = false;
					changed = getterOf_rearDistance(&component->rearDistancePort, &component->stateChart->rearDistance);
					Clock_reset(component->rearDistancePortClock);
				}
			
		}

		
		CourseControlCourseControlComponentStateChart* CourseControlComponent_getStateMachine(CourseControlComponent* component) {
					return component->stateChart;
				} 
			Port* CourseControlComponent_getcourseControl(CourseControlComponent* component) {
			return &(component->courseControlPort);
		}
		
			void setterOf_velocity(Port* velocityPort, int16_T* velocity) {
				MCC_CourseControlComponent_velocity_send_value(velocityPort, velocity);		
		}
			void setterOf_angle(Port* anglePort, int8_T* angle) {
				MCC_CourseControlComponent_angle_send_value(anglePort, angle);		
		}
			
			bool_T getterOf_frontDistance(Port* frontDistancePort, int32_T* frontDistance) {
			if(MCC_CourseControlComponent_frontDistance_exists_value(frontDistancePort)){
				MCC_CourseControlComponent_frontDistance_recv_value(frontDistancePort, frontDistance);
			return true;
			}
			else return false;
		}
			
			bool_T getterOf_rearDistance(Port* rearDistancePort, int32_T* rearDistance) {
			if(MCC_CourseControlComponent_rearDistance_exists_value(rearDistancePort)){
				MCC_CourseControlComponent_rearDistance_recv_value(rearDistancePort, rearDistance);
			return true;
			}
			else return false;
		}

		

