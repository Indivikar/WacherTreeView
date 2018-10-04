package app.interfaces;

public interface ISuffix {
	
	public static String getSuffix(String fileName){
		
		String suffix = null;
		if ( fileName.lastIndexOf( '.' ) > 0 ) // Do not consider files without a dot
		{
		  suffix = fileName.substring(fileName.lastIndexOf('.'));
		}
		else
		{
		  suffix = "";
		}

		return suffix;
	}
	
	public default String suffixRemove(String fileName){

		String withoutSuffix = null;
		if ( fileName.lastIndexOf( '.' ) > 0 ) // Do not consider files without a dot
		{
			withoutSuffix = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		else
		{
			withoutSuffix = "";
		}

		return withoutSuffix;
	}
	
}
