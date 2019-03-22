package app.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IStringBreaker {

	// setzt ein Zeilenumbruch, nach einer bestimmten Stringlänge
	public default String splitString(String msg, int lineSize) {
        String newString = "";

        Pattern p = Pattern.compile("\\b.{1," + (lineSize-1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);
        
        while(m.find()) {
//                System.out.println(m.group().trim());   // Debug
                newString += m.group().trim() + "\n";
        }
        return removeLastLineBreaks(newString);
    }
	
	public default String removeLastLineBreaks(String str) {
		  final char LF = '\n';
		  final char CR = '\r';
		  
	      if (isEmpty(str)) {
	          return str;
	      }

	      if (str.length() == 1) {
	          char ch = str.charAt(0);
	          if (ch == CR || ch == LF) {
	              return "";
	          }
	          return str;
	      }

	      int lastIdx = str.length() - 1;
	      char last = str.charAt(lastIdx);

	      if (last == LF) {
	          if (str.charAt(lastIdx - 1) == CR) {
	              lastIdx--;
	          }
	      } else if (last != CR) {
	          lastIdx++;
	      }
	      return str.substring(0, lastIdx);
	  }
	
	public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	}
	
}
