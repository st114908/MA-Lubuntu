package de.ust.mumlacgppa.pipeline.parts.storage;

import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;

public interface GenerateVariableDefStructure extends Keywords{
	public static String generateVariableDefStructure(String name, String value, String type){
		String currentVariableDef = type + " " + name + " " + value;
		return currentVariableDef;
	}
}
