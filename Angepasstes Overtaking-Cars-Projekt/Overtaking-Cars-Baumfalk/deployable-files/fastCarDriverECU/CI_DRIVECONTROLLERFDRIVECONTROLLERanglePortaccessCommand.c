#include <SimpleHardwareController_Connector.h>
#include "CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h"
void CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand(int8_T* angle){
	// Start of user code API
	*angle = SimpleHardwareController_DriveController_GetAngle();

	// End of user code
}
