package no.nav.oko.betsys.agresso.integrasjonskomponent;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class SftpConnection {


    private static final Logger log = LoggerFactory.getLogger(SftpConnection.class);
    private final JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;


    public SftpConnection(String username, String host, Integer port, String password) throws JSchException {
        jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no"); //TODO: Find a way to set known hosts? or discover safe hosts? fasit?
        session.connect();
        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
    }

    public boolean checkForNewFile(){
//
//        try {
//            fixConection();
//        } catch (JSchException e) {
//            log.error("Failed to fix connection", e);
//        }
//        try {
//            Vector vector = sftpChannel.ls(EnvironmentConfig.NFSFILEPATH);
//            System.out.println(vector);
//        } catch (SftpException e) {
//            log.error("Failed to fix connection", e);
//        }
        return true;
    }

    private void fixConection() throws JSchException {
        if(!session.isConnected()){
            session.connect();
        }
        if(!sftpChannel.isConnected()){
            sftpChannel.connect();
        }

    }
}
