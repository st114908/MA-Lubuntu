package mumlacgppa.tests;

import mumlacgppa.pipeline.parts.storage.VariableHandler;

class InternalTestVariableHandlerClearStorageAccess extends VariableHandler {
	void clearVariableHandlerStorage(){
		clearStorage();
	}
}
