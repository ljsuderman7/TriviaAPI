package ca.lsuderman.triviaapi;

public class Utilities {
    //TODO: Add more conversions

    // replaces HTML characters to readable values. ex &quot; -> "
    public static String replaceHTMLCharacters(String conversion){
        conversion = conversion.replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&#039;", "\'");

        return conversion;
    }

    // replaces readable characters to HTML characters to save in DB (it doesn't like things like single quotes..)
    public static String convertBackToHTMLCharacters(String conversion){
        conversion = conversion.replace("\"", "&quot;")
                .replace("&", "&amp;")
                .replace("\'", "&#039;");

        return conversion;
    }
}
