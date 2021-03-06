package mariadb.migration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
//import java.sql.ResultSet;

public class Util {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
    public static DecimalFormat numberFormat = new DecimalFormat("###,###,###,###,###");
    public static DecimalFormat digitsFormat = new DecimalFormat("00");
    public static DecimalFormat percentFormat = new DecimalFormat("###.00");
    public static ExodusPropertyReader exodusPrope = new ExodusPropertyReader("Exodus.properties");
    
    public static String getPropertyValue(String propName) {
		String PropertyValue="";
		String StringTerminator="";
    	try {
			StringTerminator = exodusPrope.getValue("StringTerminator");
			String strTerm[] = StringTerminator.split(",");
			//Remove the String Terminator, This will let one retreive leading/trailing spaces if needed in the Strings
			PropertyValue = exodusPrope.getValue(propName);

			if (strTerm.length >= 1) {
				PropertyValue = PropertyValue.replace(strTerm[0], "");
				if (strTerm.length == 2) {
					PropertyValue = PropertyValue.replace(strTerm[1], "");
				}
			}

			if (PropertyValue == null) {
				PropertyValue="";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			PropertyValue="";
		}
    	return PropertyValue;
    }
    
    public static long ExecuteScript(DBConHandler TargetCon, String SQLScript) {
    	long ReturnStatus = 0;
		Statement StatementObj;

		try {
			StatementObj = TargetCon.getDBConnection().createStatement();
	
			StatementObj.executeUpdate(SQLScript);
			
			StatementObj.close();
		} catch (SQLException e) {
			System.out.println("*** Failed to Execute: " + SQLScript);
			e.printStackTrace();
			ReturnStatus = -1;
		}
    	return ReturnStatus;
    }

	//Execute all the queries in the List one by one
	public static long ExecuteScript(DBConHandler TargetCon, List<String> SQLScript) {
    	long ReturnStatus = 0;
		Statement StatementObj;

		try {
			StatementObj = TargetCon.getDBConnection().createStatement();

			//Change this code to execute each script as individual instead of a batch
			for (String SQL : SQLScript) {
				try {
					StatementObj.execute(SQL);
				} catch (SQLException ex) {
					System.out.println("*** Failed to Execute: " + SQL);
					ex.printStackTrace();
				}
			}
			TargetCon.getDBConnection().commit();
			StatementObj.close();
		} catch (Exception e) {
			System.out.println("*** Failed to Execute: " + SQLScript);
			e.printStackTrace();
			ReturnStatus = -1;
		}
    	return ReturnStatus;
	}

	public static List<String> GetExtraStatements(String Key) {
		List<String> Scripts = new ArrayList<String>();
		String Statement;
		int Counter=0;

		Statement = Util.getPropertyValue(Key + "." + (++Counter));
		while(!Statement.isEmpty()) {
			Scripts.add(Statement);
			Statement = Util.getPropertyValue(Key + "." + (++Counter));
		}
		return Scripts;
	}

	public static String TimeToString(long lSeconds) {
		String SecondsRemaining="";
		long Hours, Minutes, Seconds;
		
		Hours = lSeconds / 60 / 60;
		Minutes = (lSeconds / 60) - (Hours * 60);
		Seconds = lSeconds - (Minutes * 60);

		SecondsRemaining += digitsFormat.format(Hours) + ":" + digitsFormat.format(Minutes) + ":" + digitsFormat.format(Seconds);

		return SecondsRemaining;
	}

	public static String lPad(String SourceStr, int FixedLen, String PadChar) {
    	int StrLength=SourceStr.length();
    	String TmpStr="";
    	if (StrLength < FixedLen) {
    		FixedLen -= StrLength;
	    	for (int x = 0; x < FixedLen; x++) {
	    		TmpStr += PadChar;
	    	}
    	}
    	return TmpStr + SourceStr;
    }
	
	public static String rPad(String SourceStr, int FixedLen, String PadChar) {
    	int StrLength=SourceStr.length();
    	String TmpStr="";
    	if (StrLength < FixedLen) {
    		FixedLen -= StrLength;
	    	for (int x = 0; x < FixedLen; x++) {
	    		TmpStr += PadChar;
	    	}
    	}
    	return SourceStr + TmpStr;
	}
	
	//This is specifically created to remove "" quotes from the function/procedure names
	public static String ReplaceString(String SourceStr, char SearchChar, char ReplaceWithChar, String TargetChar) {
		StringBuilder TmpStr = new StringBuilder(SourceStr);
		int TargetCharPosition=SourceStr.indexOf(TargetChar) + TargetChar.length() + 1;

		for (int CharCount=0; CharCount <= TargetCharPosition; CharCount++) {
			if (SourceStr.charAt(CharCount) == SearchChar) {
				TmpStr.setCharAt(CharCount, ReplaceWithChar);
			}
		}
		return TmpStr.toString();
	}

	public static void BoxedText(String sMessage, String BoxChar, int BoxSize) {
		int strLen;
		strLen = sMessage.length();
		
		if (BoxSize <= strLen) {
			BoxSize = strLen + 2;			
		}

		System.out.println("\n" + Util.rPad(BoxChar, BoxSize, BoxChar));
		System.out.println(Util.rPad(BoxChar + " " + sMessage, BoxSize - 2, " ") + " " + BoxChar);
		System.out.println(Util.rPad(BoxChar, BoxSize, BoxChar));
	}

	public static void BoxedText(String sPreBox, String sMessage, String sPostBox, String BoxChar, int BoxSize) {
		int strLen;
		strLen = sMessage.length();
		
		if (BoxSize <= strLen) {
			BoxSize = strLen + 2;			
		}

		System.out.println(sPreBox);
		System.out.println(Util.rPad(BoxChar, BoxSize, BoxChar));
		System.out.println(Util.rPad(BoxChar + " " + sMessage, BoxSize - 2, " ") + " " + BoxChar);
		System.out.println(Util.rPad(BoxChar, BoxSize, BoxChar));
		System.out.print(sPostBox);
	}
}
