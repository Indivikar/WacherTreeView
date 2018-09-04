package app.interfaces;

public interface ISuffix {
	
	public static String getSuffix(String dateiName){
		
		String suffix = null;
		if ( dateiName.lastIndexOf( '.' ) > 0 ) // das > ist pure Absicht, damit versteckte Dateien nicht als Dateiendung interpretiert werden!
		{
		  suffix = dateiName.substring(dateiName.lastIndexOf('.'));
//		  System.out.println("DateiEndung: " + suffix);
		}
		else
		{
		  suffix = "";
		}

		return suffix;
	}
}
