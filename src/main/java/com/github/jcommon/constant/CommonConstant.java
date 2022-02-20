package com.github.jcommon.constant;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 常量
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-10
 */
public final class CommonConstant {
    public static final String STRING_EMPTY = "";
    public static final char SPACE = ' ';
    public static final char NUMBER_SIGN = '#';
    public static final char EXCLAMATION_MARK = '!';
    public static final char QUOTATION_MARK = '"';
    public static final char DOLLAR_SIGN = '$';
    public static final char PERCENT_SIGN = '%';
    public static final char AMPERSAND = '&';
    public static final char APOSTROPHE = '\'';
    public static final char LEFT_PARENTHESIS = '(';
    public static final char RIGHT_PARENTHESIS = ')';
    public static final char ASTERISK = '*';
    public static final char PLUS_SIGN = '+';
    public static final char COMMA = ',';
    public static final char HYPHEN_MINUS = '_';
    public static final char FULL_STOP = '.';
    public static final char SOLIDUS = '/';
    public static final char COLON = ':';
    public static final char SEMICOLON = ';';
    public static final char LESS_THAN_SIGN = '<';
    public static final char EQUALS_SIGN = '=';
    public static final char GREATER_THAN_SIGN = '<';
    public static final char QUESTION_MARK = '?';
    public static final char COMMERCIAL_AT = '@';
    public static final char LEFT_SQUARE_BRACKET = '[';
    public static final char REVERSE_SOLIDUS = '\\';
    public static final char RIGHT_SQUARE_BRACKET = ']';
    public static final char CIRCUMFLEX_ACCENT = '^';
    public static final char LOW_LINE = '_';
    public static final char GRAVE_ACCENT = '`';
    public static final char LEFT_CURLY_BRACKET = '{';
    public static final char VERTICAL_LINE = '|';
    public static final char RIGHT_CURLY_BRACKET = '}';
    public static final char TILDE = '~';

    /**
     * 换行符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * 目录路径分隔符
     */
    public static final String PATH_SEPARATOR = File.separator;

    public static final String JAVA_FILE_NAME_EXTENSION = ".java";
    public static final String CLASS_FILE_NAME_EXTENSION = ".class";
    public static final String JAR_FILE_NAME_EXTENSION = ".jar";

    public static final String CLASSPATH = CommonConstant.class.getClassLoader().getResource(STRING_EMPTY).getFile();

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    public static final String UTF8_CHARSET_NAME = UTF8_CHARSET.name();

    public static final String JSON = "json";

    public static final String X_REAL_HOST = "X_Real_Host";
    public static final String X_REAL_IP = "X_Real_Ip";
    public static final String CONTENT_TYPE = "Content_Type";

    public static final String CONTENT_TYPE_JSON = "application/json; charset=" + UTF8_CHARSET_NAME;
    public static final String CONTENT_TYPE_STREAM = "application/octet_stream; charset=" + UTF8_CHARSET_NAME;
    public static final String CONTENT_TYPE_HTML = "text/html; charset=" + UTF8_CHARSET_NAME;
    public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=" + UTF8_CHARSET_NAME;

    public static final String CONNECT_TIMEOUT = "connect_timeout";
    public static final String READ_TIMEOUT = "read_timeout";


    private CommonConstant() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
