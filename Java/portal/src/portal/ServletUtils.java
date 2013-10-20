package portal;


import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	public static int parseInt(HttpServletRequest request, String paramName, int defaultValue) {
		String str = request.getParameter(paramName);
		if (str != null) {
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {}
		}
		return defaultValue;
	}
	
	public static long parseLong(HttpServletRequest request, String paramName, long defaultValue) {
		String str = request.getParameter(paramName);
		if (str != null) {
			try {
				return Long.parseLong(str);
			} catch (NumberFormatException e) {}
		}
		return defaultValue;
	}
	
	public static String parseString(HttpServletRequest request, String paramName, String defaultValue) {
		String str = request.getParameter(paramName);
		return (str==null||str.isEmpty())?defaultValue:str;
	}
}
