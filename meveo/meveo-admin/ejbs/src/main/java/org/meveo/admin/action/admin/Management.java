/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.action.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.log.Log;

/**
 * Class to manage other applications : Medina, Vertina, Oudaya, Bayad
 * 
 * @author Gediminas Ubartas
 */
@Name("management")
@Scope(ScopeType.CONVERSATION)
public class Management implements Serializable {

    private static final long serialVersionUID = 1L;

    @Logger
    protected Log log;

    // TODO Use ParamBean
    private static String CONNECTION_URL = ResourceBundle.instance().getString("connectionUrl");

    private static int CONNECTION_PORT = Integer.parseInt(ResourceBundle.instance().getString("connectionPort"));

    /**
     * Application name for daemon to know what application info to send back.
     */
    private static String application;

    /**
     * Output stream for network communication.
     */
    private transient ObjectOutputStream oos = null;

    /**
     * Input stream for network communication.
     */
    private transient ObjectInputStream ois = null;

    /**
     * Client socket. (A socket is an endpoint for communication between two
     * machines. )
     */
    private transient Socket socket = null;

    /**
     * List of application logs.
     */
    public List<String> logs = new ArrayList<String>();

    /**
     * Is connection established flag.
     */
    public boolean connectionEstablished;

    /**
     * Connects to socket server.
     */
    public void connect() {
        connectionEstablished = false;
        // open a socket connection
        try {
            socket = new Socket(CONNECTION_URL, CONNECTION_PORT);
            connectionEstablished = true;
            // open I/O streams for objects
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            log.error("Unknown Host Exception", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    /**
     * Checks if connection is available.
     */
    public boolean checkConnection() {
        connect();
        try {
            if (connectionEstablished) oos.writeObject("check");
        } catch (IOException e) {
            log.error("IOException", e);
        } finally {
            close();
        }
        return connectionEstablished;

    }

    /**
     * Closes connection.
     * 
     * @param jobName
     *            name of Job
     * @param jobRepositoryId
     *            Job id in repository
     * @param executionDate
     *            Date of job execution
     * @param jobId
     *            Job entity id
     */
    public void close() {
        try {
            if (oos != null) oos.close();
            if (ois != null) {
                ois.close();
            }
        } catch (IOException e) {
            log.error("IOException", e);

        }

    }

    /**
     * Checks application status.
     * 
     * @param jobName
     *            Name of Job.
     * @param jobRepositoryId
     *            Job id in repository.
     * @param executionDate
     *            Date of job execution.
     * @param jobId
     *            Job entity id.
     */
    public boolean isEnabled() {
        try {
            connect();
            oos.writeObject("status");
            oos.writeObject(application);
            String text = (String) ois.readObject();
            close();
            if (text.equals("true")) {
                return true;
            } else
                return false;
        } catch (IOException e) {
            log.error("IOException", e);

            return false;
        } catch (ClassNotFoundException e1) {
            log.error("ClassNotFoundException", e1);
            return false;
        }
    }

    /**
     * Converts Logging Events list to string list.
     * 
     * @param tempLogList
     *            Logging Events List.
     */
    public void convertLogs(List<LoggingEvent> tempLogList) {

        for (Object o : tempLogList) {
            if (o instanceof LoggingEvent) {
                final LoggingEvent logEvent = (LoggingEvent) o;
                logs.add("[" + logEvent.getLevel() + "] " + logEvent.getRenderedMessage());
            }
        }
    }

    /**
     * Sends application kill signal to socket server.
     */
    public void kill() {
        try {
            connect();
            oos.writeObject("kill");
            oos.writeObject(application);
            close();
        } catch (IOException e) {
            log.error("IOException", e);

        }

    }

    /**
     * Sends restart signal to socket server
     * 
     */
    public void restart() {
        kill();
        init();
    }

    /**
     * Sends "Turn On" signal to socket server.
     */
    public void init() {
        try {
            connect();
            oos.writeObject("init");
            oos.writeObject(application);
            String text = (String) ois.readObject();
            System.out.println(text);
            close();

        } catch (Exception e) {
            log.error("IOException", e);
        }
    }

    /**
     * Get logs from application (what it does at the time).
     */
    @SuppressWarnings("unchecked")
    public List<String> getLogs() {
        connect();
        try {
            oos.writeObject("log");
            logs.clear();
            List<LoggingEvent> tempLogList = (List<LoggingEvent>) ois.readObject();
            convertLogs(tempLogList);
            close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public String getApplication() {
        return application;
    }

    @SuppressWarnings("static-access")
    public void setApplication(String application) {
        this.application = application;
    }

}
