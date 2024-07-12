package cz.foresttech.forestredis.shared.adapter;

public interface ILoggerAdapter {

    void info(String message);
    void warning(String message);
    void error(String message);

}
