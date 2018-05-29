package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SFTPServerConfig {

    public void start(int port, String path) {
        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(new UserAuthPasswordFactory());

        List<NamedFactory<Command>> sftpCommandFactory = new ArrayList<NamedFactory<Command>>();
        sftpCommandFactory.add(new SftpSubsystemFactory());

        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setUserAuthFactories(userAuthFactories);
        sshd.setSubsystemFactories(sftpCommandFactory);
        sshd.setPasswordAuthenticator((username, password, session) -> {
            if ((username.equals("admin")) && (password.equals("admin"))) {
                ClassLoader classLoader = SFTPServerConfig.class.getClassLoader();
                try {
                    sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(classLoader.getResource(path).toURI())));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });

        try {
            sshd.start();
//            SFTPServerConfig object = new SFTPServerConfig();
//            object.waitMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void waitMethod() {

        while (true) {
            System.out.println("always running program ==> " + Calendar.getInstance().getTime());
            try {
                wait(2000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }
}