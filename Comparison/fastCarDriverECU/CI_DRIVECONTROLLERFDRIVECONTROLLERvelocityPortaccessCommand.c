#include "CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h"
#include <SimpleHardwareController_Connector.h>
void CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand(int16_T* velocity){
	// Start of user code API
	*velocity = SimpleHardwareController_DriveController_GetSpeed();
	// End of user code
}
