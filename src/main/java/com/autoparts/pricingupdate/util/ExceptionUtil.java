package com.autoparts.pricingupdate.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.autoparts.pricingupdate.errors.MicroserviceException;
import com.autoparts.pricingupdate.model.MicroserviceResponse;
import org.springframework.http.HttpStatus;

import clover.org.apache.commons.lang.ArrayUtils;
import clover.org.apache.commons.lang.ClassUtils;
import clover.org.apache.commons.lang.NullArgumentException;
import clover.org.apache.commons.lang.StringUtils;
import clover.org.apache.commons.lang.SystemUtils;
import clover.org.apache.commons.lang.exception.Nestable;


public class ExceptionUtil {
	
	static final String WRAPPED_MARKER = " [wrapped] ";
    private static final Object CAUSE_METHOD_NAMES_LOCK = new Object();
    private static String[] CAUSE_METHOD_NAMES = new String[]{"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
    private static final Method THROWABLE_CAUSE_METHOD;
    private static final Method THROWABLE_INITCAUSE_METHOD;


    public static void addCauseMethodName(String methodName) {
        if(StringUtils.isNotEmpty(methodName) && !isCauseMethodName(methodName)) {
            ArrayList list = getCauseMethodNameList();
            if(list.add(methodName)) {
                Object var2 = CAUSE_METHOD_NAMES_LOCK;
                synchronized(CAUSE_METHOD_NAMES_LOCK) {
                    CAUSE_METHOD_NAMES = toArray(list);
                }
            }
        }

    }

    public static void removeCauseMethodName(String methodName) {
        if(StringUtils.isNotEmpty(methodName)) {
            ArrayList list = getCauseMethodNameList();
            if(list.remove(methodName)) {
                Object var2 = CAUSE_METHOD_NAMES_LOCK;
                synchronized(CAUSE_METHOD_NAMES_LOCK) {
                    CAUSE_METHOD_NAMES = toArray(list);
                }
            }
        }

    }

    public static boolean setCause(Throwable target, Throwable cause) {
        if(target == null) {
            throw new NullArgumentException("target");
        } else {
            Object[] causeArgs = new Object[]{cause};
            boolean modifiedTarget = false;
            if(THROWABLE_INITCAUSE_METHOD != null) {
                try {
                    THROWABLE_INITCAUSE_METHOD.invoke(target, causeArgs);
                    modifiedTarget = true;
                } catch (IllegalAccessException var8) {
                    ;
                } catch (InvocationTargetException var9) {
                    ;
                }
            }

            try {
                Method ignored = target.getClass().getMethod("setCause", new Class[]{java.lang.Throwable.class});

                ignored.invoke(target, causeArgs);
                modifiedTarget = true;
            } catch (NoSuchMethodException var5) {
                ;
            } catch (IllegalAccessException var6) {
                ;
            } catch (InvocationTargetException var7) {
                ;
            }

            return modifiedTarget;
        }
    }

    private static String[] toArray(List list) {
        return (String[])((String[])list.toArray(new String[list.size()]));
    }

    private static ArrayList getCauseMethodNameList() {
        Object var0 = CAUSE_METHOD_NAMES_LOCK;
        synchronized(CAUSE_METHOD_NAMES_LOCK) {
            return new ArrayList(Arrays.asList(CAUSE_METHOD_NAMES));
        }
    }

    public static boolean isCauseMethodName(String methodName) {
        Object var1 = CAUSE_METHOD_NAMES_LOCK;
        synchronized(CAUSE_METHOD_NAMES_LOCK) {
            return ArrayUtils.indexOf(CAUSE_METHOD_NAMES, methodName) >= 0;
        }
    }

    public static Throwable getCause(Throwable throwable) {
        Object var1 = CAUSE_METHOD_NAMES_LOCK;
        synchronized(CAUSE_METHOD_NAMES_LOCK) {
            return getCause(throwable, CAUSE_METHOD_NAMES);
        }
    }

    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        if(throwable == null) {
            return null;
        } else {
            Throwable cause = getCauseUsingWellKnownTypes(throwable);
            if(cause == null) {
                if(methodNames == null) {
                    Object i = CAUSE_METHOD_NAMES_LOCK;
                    synchronized(CAUSE_METHOD_NAMES_LOCK) {
                        methodNames = CAUSE_METHOD_NAMES;
                    }
                }

                for(int var6 = 0; var6 < methodNames.length; ++var6) {
                    String methodName = methodNames[var6];
                    if(methodName != null) {
                        cause = getCauseUsingMethodName(throwable, methodName);
                        if(cause != null) {
                            break;
                        }
                    }
                }

                if(cause == null) {
                    cause = getCauseUsingFieldName(throwable, "detail");
                }
            }

            return cause;
        }
    }

    public static Throwable getRootCause(Throwable throwable) {
        List list = getThrowableList(throwable);
        return list.size() < 2?null:(Throwable)list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
        return (Throwable)(throwable instanceof Nestable?((Nestable)throwable).getCause():(throwable instanceof SQLException?((SQLException)throwable).getNextException():(throwable instanceof InvocationTargetException?((InvocationTargetException)throwable).getTargetException():null)));
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;

        try {
            method = throwable.getClass().getMethod(methodName, (Class[])null);
        } catch (NoSuchMethodException var7) {
            ;
        } catch (SecurityException var8) {
            ;
        }

        if(method != null && (java.lang.Throwable.class).isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke(throwable, ArrayUtils.EMPTY_OBJECT_ARRAY);
            } catch (IllegalAccessException var4) {
                ;
            } catch (IllegalArgumentException var5) {
                ;
            } catch (InvocationTargetException var6) {
                ;
            }
        }

        return null;
    }

    private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName) {
        Field field = null;

        try {
            field = throwable.getClass().getField(fieldName);
        } catch (NoSuchFieldException var6) {
            ;
        } catch (SecurityException var7) {
            ;
        }

        if(field != null && (java.lang.Throwable.class).isAssignableFrom(field.getType())) {
            try {
                return (Throwable)field.get(throwable);
            } catch (IllegalAccessException var4) {
                ;
            } catch (IllegalArgumentException var5) {
                ;
            }
        }

        return null;
    }

    public static boolean isThrowableNested() {
        return THROWABLE_CAUSE_METHOD != null;
    }

    public static boolean isNestedThrowable(Throwable throwable) {
        if(throwable == null) {
            return false;
        } else if(throwable instanceof Nestable) {
            return true;
        } else if(throwable instanceof SQLException) {
            return true;
        } else if(throwable instanceof InvocationTargetException) {
            return true;
        } else if(isThrowableNested()) {
            return true;
        } else {
            Class cls = throwable.getClass();
            Object ignored = CAUSE_METHOD_NAMES_LOCK;
            synchronized(CAUSE_METHOD_NAMES_LOCK) {
                int i = 0;
                int isize = CAUSE_METHOD_NAMES.length;

                while(true) {
                    if(i >= isize) {
                        break;
                    }

                    try {
                        Method ignored1 = cls.getMethod(CAUSE_METHOD_NAMES[i], (Class[])null);
                        if(ignored1 != null && (java.lang.Throwable.class).isAssignableFrom(ignored1.getReturnType())) {
                            boolean var10000 = true;
                            return var10000;
                        }
                    } catch (NoSuchMethodException var9) {
                        ;
                    } catch (SecurityException var10) {
                        ;
                    }

                    ++i;
                }
            }

            try {
                Field var12 = cls.getField("detail");
                if(var12 != null) {
                    return true;
                }
            } catch (NoSuchFieldException var7) {
                ;
            } catch (SecurityException var8) {
                ;
            }

            return false;
        }
    }

    public static int getThrowableCount(Throwable throwable) {
        return getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List list = getThrowableList(throwable);
        return (Throwable[])((Throwable[])list.toArray(new Throwable[list.size()]));
    }

    public static List getThrowableList(Throwable throwable) {
        ArrayList list;
        for(list = new ArrayList(); throwable != null && !list.contains(throwable); throwable = getCause(throwable)) {
            list.add(throwable);
        }

        return list;
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz) {
        return indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz, int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class type) {
        return indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class type, int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }

    private static int indexOf(Throwable throwable, Class type, int fromIndex, boolean subclass) {
        if(throwable != null && type != null) {
            if(fromIndex < 0) {
                fromIndex = 0;
            }

            Throwable[] throwables = getThrowables(throwable);
            if(fromIndex >= throwables.length) {
                return -1;
            } else {
                int i;
                if(subclass) {
                    for(i = fromIndex; i < throwables.length; ++i) {
                        if(type.isAssignableFrom(throwables[i].getClass())) {
                            return i;
                        }
                    }
                } else {
                    for(i = fromIndex; i < throwables.length; ++i) {
                        if(type.equals(throwables[i].getClass())) {
                            return i;
                        }
                    }
                }

                return -1;
            }
        } else {
            return -1;
        }
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if(throwable != null) {
            if(stream == null) {
                throw new IllegalArgumentException("The PrintStream must not be null");
            } else {
                String[] trace = getRootCauseStackTrace(throwable);

                for(int i = 0; i < trace.length; ++i) {
                    stream.println(trace[i]);
                }

                stream.flush();
            }
        }
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if(throwable != null) {
            if(writer == null) {
                throw new IllegalArgumentException("The PrintWriter must not be null");
            } else {
                String[] trace = getRootCauseStackTrace(throwable);

                for(int i = 0; i < trace.length; ++i) {
                    writer.println(trace[i]);
                }

                writer.flush();
            }
        }
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if(throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            Throwable[] throwables = getThrowables(throwable);
            int count = throwables.length;
            ArrayList frames = new ArrayList();
            List nextTrace = getStackFrameList(throwables[count - 1]);
            int i = count;

            while(true) {
                --i;
                if(i < 0) {
                    return (String[])((String[])frames.toArray(new String[0]));
                }

                List trace = nextTrace;
                if(i != 0) {
                    nextTrace = getStackFrameList(throwables[i - 1]);
                    removeCommonFrames(trace, nextTrace);
                }

                if(i == count - 1) {
                    frames.add(throwables[i].toString());
                } else {
                    frames.add(" [wrapped] " + throwables[i].toString());
                }

                for(int j = 0; j < trace.size(); ++j) {
                    frames.add(trace.get(j));
                }
            }
        }
    }

    public static void removeCommonFrames(List causeFrames, List wrapperFrames) {
        if(causeFrames != null && wrapperFrames != null) {
            int causeFrameIndex = causeFrames.size() - 1;

            for(int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --wrapperFrameIndex) {
                String causeFrame = (String)causeFrames.get(causeFrameIndex);
                String wrapperFrame = (String)wrapperFrames.get(wrapperFrameIndex);
                if(causeFrame.equals(wrapperFrame)) {
                    causeFrames.remove(causeFrameIndex);
                }

                --causeFrameIndex;
            }

        } else {
            throw new IllegalArgumentException("The List must not be null");
        }
    }

    public static String getFullStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        Throwable[] ts = getThrowables(throwable);

        for(int i = 0; i < ts.length; ++i) {
            ts[i].printStackTrace(pw);
            if(isNestedThrowable(ts[i])) {
                break;
            }
        }

        return sw.getBuffer().toString();
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String[] getStackFrames(Throwable throwable) {
        return throwable == null?ArrayUtils.EMPTY_STRING_ARRAY:getStackFrames(getStackTrace(throwable));
    }

    static String[] getStackFrames(String stackTrace) {
        String linebreak = SystemUtils.LINE_SEPARATOR;
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        ArrayList list = new ArrayList();

        while(frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }

        return toArray(list);
    }

    static List getStackFrameList(Throwable t) {
        String stackTrace = getStackTrace(t);
        String linebreak = SystemUtils.LINE_SEPARATOR;
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        ArrayList list = new ArrayList();
        boolean traceStarted = false;

        while(frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if(at != -1 && token.substring(0, at).trim().length() == 0) {
                traceStarted = true;
                list.add(token);
            } else if(traceStarted) {
                break;
            }
        }

        return list;
    }

    public static String getMessage(Throwable th) {
        if(th == null) {
            return "";
        } else {
            String clsName = ClassUtils.getShortClassName(th, (String)null);
            String msg = th.getMessage();
            return clsName + ": " + StringUtils.defaultString(msg);
        }
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = getRootCause(th);
        root = root == null?th:root;
        return getMessage(root);
    }

    static {
        Method causeMethod;
        try {
            causeMethod = (java.lang.Throwable.class).getMethod("getCause", (Class[])null);
        } catch (Exception var3) {
            causeMethod = null;
        }

        THROWABLE_CAUSE_METHOD = causeMethod;

        try {
            causeMethod = (java.lang.Throwable.class).getMethod("initCause", new Class[]{java.lang.Throwable.class});
        } catch (Exception var2) {
            causeMethod = null;
        }

        THROWABLE_INITCAUSE_METHOD = causeMethod;
    }

    public static void handleException(Exception exception, MicroserviceResponse msResponse) throws MicroserviceException {
        handleException(exception, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, msResponse);
    }

    public static void handleException(Exception exception, String errorMessage, MicroserviceResponse msResponse) throws MicroserviceException {
        handleException(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, msResponse);
    }

    public static void handleException(Exception exception, String errorMessage, HttpStatus httpStatus, MicroserviceResponse msResponse) throws MicroserviceException {
        msResponse.setCode(httpStatus.value());
        msResponse.setHttpStatus(httpStatus);
        msResponse.setMessage(exception.getMessage());
        throw new MicroserviceException(httpStatus, String.valueOf(httpStatus.value()), errorMessage).setMicroserviceResponse(msResponse);
    }

}
