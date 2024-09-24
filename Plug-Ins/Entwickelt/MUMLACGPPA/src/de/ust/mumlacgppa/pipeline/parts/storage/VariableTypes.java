package de.ust.mumlacgppa.pipeline.parts.storage;

public interface VariableTypes {
	public final String AnyType = "Any";
	
	public final String NumberType = "Number";
	public final String StringType = "String";
	public final String BooleanType = "Boolean";
	
	//public final String PathType = "Path";
	public final String FolderPathType = "FolderPath";
	public final String FilePathType = "FilePath";
	
	public final String BoardSerialNumberType = "BoardSerialNumber";
	public final String BoardIdentifierFQBNType = "BoardIdentifierFQBN";
	public final String ConnectionPortType = "ConnectionPort";
	
	public final String WLANNameType = "WLANName";
	public final String WLANPasswordType = "WLANPassword";
	public final String ServerIPAddressType = "ServerIPAddress";
	public final String ServerPortType = "ServerPort";
	
	
	
	public static boolean checkIfAllowedType(String typeToCheck){
		switch(typeToCheck){
			case NumberType:
				return true;
			case StringType:
				return true;
			case BooleanType:
				return true;

			//case PathType:
			//	return true;
			case FolderPathType:
				return true;
			case FilePathType:
				return true;
				
			case BoardSerialNumberType:
				return true;
			case BoardIdentifierFQBNType:
				return true;
			case ConnectionPortType:
				return true;

			case WLANNameType:
				return true;
			case WLANPasswordType:
				return true;
			case ServerIPAddressType:
				return true;
			case ServerPortType:
				return true;
			
			default:
				return false;
		}
	}
}
