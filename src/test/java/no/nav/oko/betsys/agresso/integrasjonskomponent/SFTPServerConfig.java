//package no.nav.oko.betsys.agresso.integrasjonskomponent;
//
//import org.apache.sshd.common.NamedFactory;
//import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
//import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
//import org.apache.sshd.server.Command;
//import org.apache.sshd.server.SshServer;
//import org.apache.sshd.server.auth.UserAuth;
//import org.apache.sshd.server.auth.UserAuthNone;
//import org.apache.sshd.server.auth.UserAuthNoneFactory;
//import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
//import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
//import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
//import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
//import org.apache.sshd.server.scp.ScpCommandFactory;
//import org.apache.sshd.server.session.ServerSession;
//import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
//
//import java.net.URISyntaxException;
//import java.nio.file.Paths;
//import java.security.PublicKey;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//public class SFTPServerConfig {
//
//    public SshServer configure(String host, int port, String path) {
////        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
////        userAuthFactories.add(new UserAuthPublicKeyFactory());
//        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
//        userAuthFactories.add(new UserAuthNoneFactory());
//        List<NamedFactory<Command>> sftpCommandFactory = new ArrayList<>();
//        sftpCommandFactory.add(new SftpSubsystemFactory());
//
//        SshServer sshd = SshServer.setUpDefaultServer();
//        sshd.setPort(port);
//        sshd.setHost(host);
//        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
//        sshd.setCommandFactory(new ScpCommandFactory());
//        sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
//            @Override
//            public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
//                return false;
//            }
//        });
//        sshd.setUserAuthFactories(userAuthFactories);
//        sshd.setSubsystemFactories(sftpCommandFactory);
//        ClassLoader classLoader = SFTPServerConfig.class.getClassLoader();
//        try {
//            sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(classLoader.getResource(path).toURI())));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
////        sshd.setPasswordAuthenticator((username, password, session) -> {
////            if ((username.equals("admin")) && (password.equals("admin"))) {
////                ClassLoader classLoader = SFTPServerConfig.class.getClassLoader();
////                try {
////
////                    sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(classLoader.getResource(path).toURI())));
////                } catch (URISyntaxException e) {
////                    e.printStackTrace();
////                }
////                return true;
////            }
////            return false;
////        });
//
//        return sshd;
//    }
//
//    public synchronized void waitMethod() {
//
//        while (true) {
//            System.out.println("always running program ==> " + Calendar.getInstance().getTime());
//            try {
//                wait(2000);
//            } catch (InterruptedException e) {
//
//                e.printStackTrace();
//            }
//        }
//    }
//}