package com.xin.utils.jdbc;

import com.xin.utils.AssertUtil;
import com.xin.utils.log.LogFactory;
import org.apache.log4j.Logger;
import org.apache.zookeeper.common.IOUtils;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

public class XinDataSource implements DataSource {

    private Properties properties = new Properties();
    private Logger dataSourceLog = LogFactory.getLogger("XinDataSource");
    private PrintWriter printWriter;
    File file;

    private String configPath;

    private int loginTimeout;

    public XinDataSource(String configPath) {
        AssertUtil.checkNotEmpty(configPath, "configPath must not be empty and null.");
        this.configPath = configPath;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(getConfigPath()));
            getProperties().load(in);
        } catch (Exception e) {
            dataSourceLog.error("DataSource getProperties unowned error.", e);
        } finally {
            IOUtils.closeStream(in);
        }
        init();

    }

    public XinDataSource(Properties properties) {
        this.properties = properties;
    }

    private void init() {
        InputStream in = null;
        try {
            String driverClassName = getProperties().getProperty(Constants.JDBC_DRIVER_CLASS_NAME_KEY);
            Class.forName(driverClassName);
            dataSourceLog.debug("properties config info:" + getProperties());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dataSourceLog.error("DataSource getProperties unowned error.", e);
        } finally {
            IOUtils.closeStream(in);
        }
    }

    public String getConfigPath() {
        return this.configPath;
    }

    private Properties getProperties(){
        return this.properties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getProperties().getProperty(Constants.JDBC_URL_KEY), getProperties().getProperty(Constants.JDBC_USER_NAME_KEY), getProperties().getProperty(Constants.JDBC_PASSWORD_KEY));
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(getProperties().getProperty(Constants.JDBC_URL_KEY), username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(System.out);
        }

        return this.printWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.printWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.loginTimeout;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
