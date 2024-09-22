#include <SimpleHardwareController_Connector.h>
#include "CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.h"
void CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand(int8_T* angle){
	// Start of user code API
	*angle = SimpleHardwareController_DriveController_GetAngle();

	// End of user code
}
