#include "CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h"
#include <SimpleHardwareController_Connector.h>
void CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand(int32_T* distance){
	// Start of user code API
	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);
	// End of user code
}
