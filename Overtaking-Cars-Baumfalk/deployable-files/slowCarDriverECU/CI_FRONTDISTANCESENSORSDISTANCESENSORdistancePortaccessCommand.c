#include <SimpleHardwareController_Connector.h>
#include "CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h"
void CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand(int32_T* distance){
	// Start of user code API
	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);

	// End of user code
}
