package dev.tauri.jsg.api;

import org.slf4j.Logger;
import org.slf4j.Marker;

@SuppressWarnings("all")
public class LoggerWrapper implements Logger {
    public final Logger wrappedLogger;
    public final String prefix;

    public LoggerWrapper(String prefix, Logger wrappedLogger) {
        this.wrappedLogger = wrappedLogger;
        this.prefix = prefix;
    }


    @Override
    public String getName() {
        return wrappedLogger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return wrappedLogger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        wrappedLogger.trace(prefix + msg);
    }

    @Override
    public void trace(String format, Object arg) {
        wrappedLogger.trace(prefix + format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        wrappedLogger.trace(prefix + format, arg1, arg2);

    }

    @Override
    public void trace(String format, Object... arguments) {
        wrappedLogger.trace(prefix + format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        wrappedLogger.trace(prefix + msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return wrappedLogger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        wrappedLogger.trace(marker, prefix + msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        wrappedLogger.trace(marker, prefix + format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.trace(marker, prefix + format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        wrappedLogger.trace(marker, prefix + format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        wrappedLogger.trace(marker, prefix + msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return wrappedLogger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        wrappedLogger.debug(prefix + msg);
    }

    @Override
    public void debug(String format, Object arg) {
        wrappedLogger.debug(prefix + format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        wrappedLogger.debug(prefix + format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        wrappedLogger.debug(prefix + format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        wrappedLogger.debug(prefix + msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return wrappedLogger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        wrappedLogger.debug(marker, prefix + msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        wrappedLogger.debug(marker, prefix + format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.debug(marker, prefix + format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        wrappedLogger.debug(marker, prefix + format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        wrappedLogger.debug(marker, prefix + msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return wrappedLogger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        wrappedLogger.info(prefix + msg);
    }

    @Override
    public void info(String format, Object arg) {
        wrappedLogger.info(prefix + format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        wrappedLogger.info(prefix + format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        wrappedLogger.info(prefix + format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        wrappedLogger.info(prefix + msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return wrappedLogger.isTraceEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        wrappedLogger.info(marker, prefix + msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        wrappedLogger.info(marker, prefix + format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.info(marker, prefix + format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        wrappedLogger.info(marker, prefix + format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        wrappedLogger.info(marker, prefix + msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return wrappedLogger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        wrappedLogger.warn(prefix + msg);
    }

    @Override
    public void warn(String format, Object arg) {
        wrappedLogger.warn(prefix + format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        wrappedLogger.warn(prefix + format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        wrappedLogger.warn(prefix + format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        wrappedLogger.warn(prefix + msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return wrappedLogger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        wrappedLogger.warn(marker, prefix + msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        wrappedLogger.warn(marker, prefix + format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.warn(marker, prefix + format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        wrappedLogger.warn(marker, prefix + format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        wrappedLogger.warn(marker, prefix + msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return wrappedLogger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        wrappedLogger.error(prefix + msg);
    }

    @Override
    public void error(String format, Object arg) {
        wrappedLogger.error(prefix + format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        wrappedLogger.error(prefix + format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        wrappedLogger.error(prefix + format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        wrappedLogger.error(prefix + msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return wrappedLogger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        wrappedLogger.error(prefix + marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        wrappedLogger.error(prefix + marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.error(prefix + marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        wrappedLogger.error(prefix + marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        wrappedLogger.error(prefix + marker, msg, t);
    }
}
