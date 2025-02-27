/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.schema;

import java.util.regex.*;

public final class Server implements MultiPartName {

    public static final Pattern SERVER_PATTERN = Pattern.
            compile("\\[([^\\]]+?)(?:\\\\([^\\]]+))?\\]");

    private String serverName;

    private String instanceName;

    private String simpleName;

    public Server(String serverAndInstanceName) {
        if (serverAndInstanceName != null) {
            final Matcher matcher = SERVER_PATTERN.matcher(serverAndInstanceName);
            //如果不匹配pattern，则直接赋值给simpleName，serverName和instanceName为null
            if (!matcher.find()) {
                simpleName = serverAndInstanceName;
            } else {
                //如果匹配pattern，则分别赋值给serverName和InstanceName，simpleName为null
                setServerName(matcher.group(1));
                setInstanceName(matcher.group(2));
            }
        }
    }

    public Server(String serverName, String instanceName) {
        setServerName(serverName);
        setInstanceName(instanceName);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String getFullyQualifiedName() {

        if (serverName != null && !serverName.isEmpty()
                && instanceName != null && !instanceName.isEmpty()) {
            return String.format("[%s\\%s]", serverName, instanceName);

        } else if (serverName != null && !serverName.isEmpty()) {
            return String.format("[%s]", serverName);

        } else if (simpleName != null && !simpleName.isEmpty()) {
            return simpleName;

        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }

    public Server withServerName(String serverName) {
        this.setServerName(serverName);
        return this;
    }

    public Server withInstanceName(String instanceName) {
        this.setInstanceName(instanceName);
        return this;
    }

    public static void main(String[] args) {
        Server server = new Server("a");
        server.withServerName("server");
        server.withInstanceName("instance");
        //[server\instance]
        System.out.println(server);
    }

}
