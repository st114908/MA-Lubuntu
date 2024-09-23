#include <SimpleHardwareController_Connector.h>
#include "CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h"
void CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand(int32_T* distance){
	// Start of user code API
	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);

	// End of user code
}
