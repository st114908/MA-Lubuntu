#include <SimpleHardwareController_Connector.h>
#include "CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.h"
void CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand(int16_T* velocity){
	// Start of user code API
	*velocity = SimpleHardwareController_DriveController_GetSpeed();

	// End of user code
}
