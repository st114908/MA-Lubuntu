#include "CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h"
#include <SimpleHardwareController_Connector.h>
void CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand(int32_T* distance){
	// Start of user code API
	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);
	// End of user code
}
