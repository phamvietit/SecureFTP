/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.comcast.strategy;

import java.io.File;
import org.apache.commons.net.ftp.FTPFile;
import org.comcast.router.Message;
import org.comcast.structures.SimpleList;

/**
 *
 * @author Quality of Service
 */
public interface ListingStrategy {

    public static final int ASC = 1;
    public static final int DESC = 2;

    public Message[] listLocalMessages(String pathName);

    public Message[] listRemoteFiles(String pathName);

    public String[] getRemoteDirectories(String pathName);
}