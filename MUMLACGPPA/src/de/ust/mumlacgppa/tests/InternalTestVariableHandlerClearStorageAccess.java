package de.ust.mumlacgppa.tests;

import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

class InternalTestVariableHandlerClearStorageAccess extends VariableHandler {
	void clearVariableHandlerStorage(){
		clearStorage();
	}
}
