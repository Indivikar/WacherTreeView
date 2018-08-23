package app.TreeViewWatchService;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.TreeItem;

public class WindowsExplorerComparator implements Comparator<PathItem> {

    private static final Pattern splitPattern = Pattern.compile("\\d+|\\.|\\s");

    @Override
    public int compare(PathItem pathItem1, PathItem pathItem2) {
    	
    	String str1 = pathItem1.toString();
    	String str2 = pathItem2.toString();
    	
    	File file1 = pathItem1.getPath().toFile();
    	File file2 = pathItem2.getPath().toFile();
    	
        Iterator<String> i1 = splitStringPreserveDelimiter(str1).iterator();
        Iterator<String> i2 = splitStringPreserveDelimiter(str2).iterator();
        while (true) {
            //Til here all is equal.
            if (!i1.hasNext() && !i2.hasNext()) {
                return 0;
            }
            //first has no more parts -> comes first
            if (!i1.hasNext() && i2.hasNext()) {
                return -1;
            }
            //first has more parts than i2 -> comes after
            if (i1.hasNext() && !i2.hasNext()) {
                return 1;
            }

            String data1 = i1.next();
            String data2 = i2.next();
            int result;
            try {
                //If both datas are numbers, then compare numbers
                result = Long.compare(Long.valueOf(data1), Long.valueOf(data2));
                //If numbers are equal than longer comes first
                if (result == 0) {
                    result = -Integer.compare(data1.length(), data2.length());
                }
            } catch (NumberFormatException ex) {
                //compare text case insensitive
                result = data1.compareToIgnoreCase(data2);
            }

            if (file1.isDirectory() && !file2.isDirectory()) {
                // Directory before non-directory
            	result = -1;
              } else if (!file1.isDirectory() && file2.isDirectory()) {
                // Non-directory after directory
            	  result = 1;
              } 
            
            if (result != 0) {
                return result;
            }
        }
    }

    private List<String> splitStringPreserveDelimiter(String str) {
        Matcher matcher = splitPattern.matcher(str);
        List<String> list = new ArrayList<String>();
        int pos = 0;
        while (matcher.find()) {
            list.add(str.substring(pos, matcher.start()));
            list.add(matcher.group());
            pos = matcher.end();
        }
        list.add(str.substring(pos));
        return list;
    }
}
