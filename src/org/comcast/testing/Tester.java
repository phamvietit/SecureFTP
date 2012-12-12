/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.comcast.testing;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.comcast.builder.Mail;
import org.comcast.builder.MailBuilder;
import org.comcast.exceptions.InformationRequiredException;
import org.comcast.builder.Client;
import org.comcast.logic.ServerConfig;
import org.comcast.logic.Validator;
import org.comcast.router.Message;
import org.comcast.router.RouterRetrieve;
import org.comcast.schedulers.InputScheduler;
import org.comcast.schedulers.OutputScheduler;
import org.comcast.structures.BinaryHeap;
import org.comcast.structures.LocalIterator;
import org.comcast.structures.SimpleList;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import javax.crypto.NoSuchPaddingException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.comcast.crypto.Crypto;
import org.comcast.crypto.CryptoData;
import org.comcast.crypto.CryptoProvider;
import org.comcast.logic.DateScheduler;
import org.comcast.xml.Loader;
import org.comcast.xml.LoaderProvider;
import org.comcast.xml.XMLConfiguration;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.xml.sax.ContentHandler;

/**
 *
 * @author Quality of Service
 */
public class Tester {

    public static String empty;

    private static void s() throws IOException {

        FileInputStream is = null;
        try {
            File f = new File("D:\\Proyectos en NetBeans 6\\TRABAJO PRACTICO 2\\configinit.xml");
            is = new FileInputStream(f);

            ContentHandler contenthandler = new BodyContentHandler(10 * 1024 * 1024);
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
            Parser parser = new AutoDetectParser();
            ParseContext co = new ParseContext();
            // OOXMLParser parser = new OOXMLParser();
            parser.parse(is, contenthandler, metadata, co);
            System.out.println("Mime: " + metadata.get(Metadata.CONTENT_TYPE));
            System.out.println("Last Modified: " + metadata.get(Metadata.LAST_MODIFIED));
            System.out.println("Title: " + metadata.get(Metadata.TITLE));
            System.out.println("Author: " + metadata.get(Metadata.AUTHOR));
            System.out.println("content: " + contenthandler.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static String getIPAddress() {
        try {
            InetAddress localhost = InetAddress.getByName("192.168.1.1");
            return localhost.getHostAddress();
        } catch (UnknownHostException ex) {
            return "localhost";
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SchedulerException, IOException,
            InformationRequiredException, NoSuchAlgorithmException, ClassNotFoundException,
            NoSuchPaddingException, NoSuchPaddingException, InvalidKeyException, GeneralSecurityException, Exception {

        /*XMLConfiguration xml = new XMLConfiguration();
         xml.createConection(XMLConfiguration.CLIENT_CONFIG_SCHEMA, XMLConfiguration.CLIENT_CONFIG);
         //ServerConfig config = xml.getServerConfig();
         Client config = xml.getClientConfig();
         xml.closeConection(XMLConfiguration.CLIENT_CONFIG);
        
         System.out.println(config.toString());
        
         config.setClientName("Damian");
         config.setClientLastName("Bruera");
         config.setOrganization("Dynamic Software");
         config.setDownloadPath("C:\\ServerDownloads");
         config.setSetupRun(false);
         xml.createConection(XMLConfiguration.CLIENT_CONFIG_SCHEMA, XMLConfiguration.CLIENT_CONFIG);
         xml.setClientConfig(config);
         xml.closeConection(XMLConfiguration.CLIENT_CONFIG);
        
         System.out.println(config.toString());*/

        /*XMLConfiguration xml = new XMLConfiguration();
         xml.createConection(XMLConfiguration.MAIL_CONTENT_SCHEMA, XMLConfiguration.MAIL_CONTENT);
         Properties prop = xml.getMailContent();
         Properties p = new Properties();
         p.setProperty("comcast.from", prop.getProperty("comcast.from"));
         p.setProperty("comcast.recipient", prop.getProperty("comcast.recipient"));
         p.setProperty("comcast.subject", "Resultados");
         p.setProperty("comcast.protocol", prop.getProperty("comcast.protocol"));
         p.setProperty("comcast.text", "Tareas Realizadas");
         p.setProperty("comcast.user", prop.getProperty("comcast.user"));
         p.setProperty("comcast.password", "null");
         xml.setMailContent(p);
         xml.closeConection(XMLConfiguration.MAIL_CONTENT);*/

        //System.out.println(prop.toString());

        /*xml.createConection(XMLConfiguration.MAIL_PROPERTIES_SCHEMA, XMLConfiguration.MAIL_PROPERTIES);
         xml.setMailProperties("brueradamian@gmail.com");
         xml.closeConection(XMLConfiguration.MAIL_PROPERTIES);*/

        // TODO code application logic here
        /*ServerConfig config = new ServerConfig("QoS-PC");
         config.setUserLogin("adminroot");
         config.setPassLogin("adminroot");*/

        //s();

//        System.out.println(getIPAddress());
        Loader l = LoaderProvider.getInstance();
        ServerConfig config = l.getServerConfiguration();
        Client c = l.getClientConfiguration();
        Mail m = l.getMail();
//        DateScheduler date = new DateScheduler(1, 58, 0, 10, DateScheduler.DECEMBER, 2012);
        System.out.println(config.toString());
        System.out.println(m.toString());
        System.out.println(c.toString());

        Message archivo = new Message(new Client(),
                Message.HIGH_PRIORITY, "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\RSAPrivate.key", "\\RSAPrivate.key", new FTPFile());

        Message archivo1 = new Message(new Client(),
                Message.HIGH_PRIORITY, "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\RSAPublic.key", "\\RSAPublic.key", new FTPFile());

        Message archivo3 = new Message(new Client(),
                Message.NORMAL_PRIORITY, "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\retrieve.txt", "\\retrieve.txt", new FTPFile());

        Message archivo4 = new Message(new Client(),
                Message.NORMAL_PRIORITY, "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\dynamic.txt", "\\dynamic.txt", new FTPFile());

        Message archivo2 = new Message(new Client(),
                Message.LOW_PRIORITY, "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\UPLOADER.txt", "\\UPLOADER.txt", new FTPFile());

        BinaryHeap<Message> pila = new BinaryHeap<>();
//        BinaryHeap<Message> pila2 = new BinaryHeap<>();
        pila.insert(archivo3);
        pila.insert(archivo);
        pila.insert(archivo2);
        pila.insert(archivo1);
        pila.insert(archivo4);

        /*Properties props = new Properties();
         props.setProperty("mail.smtp.host", "smtp.gmail.com");
         props.setProperty("mail.smtp.starttls.enable", "true");
         props.setProperty("mail.smtp.port", "587");
         props.setProperty("mail.smtp.user", "brueradamian@gmail.com");
         props.setProperty("mail.smtp.auth", "true");

         MailBuilder builder = new MailBuilder();
         builder.buildProperties(props);
         builder.buildFrom("bruera@noreply.com");
         builder.buildRecipient("brueradamian@gmail.com");
         builder.buildSubject("Prueba completa");
         builder.buildSendProtocol("smtp");
         builder.buildMailText("FTPClient running");
         builder.buildMailUserName("brueradamian@gmail.com");
         builder.buildMailUserPassword("null");

         Mail m = builder.getMail();*/

//        SchedulerFactory sf = new StdSchedulerFactory();
//
////        InputScheduler s = new InputScheduler(config, pila, m);
////        s.setScheduler(sf.getScheduler());
////        s.setDateScheduler(date);
//        OutputScheduler se = new OutputScheduler(config, pila, m);
//        se.setScheduler(sf.getScheduler());
//        se.setDateScheduler(date);

        //s.startJob();
        //se.startJob();
//        s.start();
//        se.start();
//        
        /*if(se.isAlive()){
         System.out.println("IS ALIVE!!!!!!!!");
         se.interrupt();
         }*/

        /*System.out.println("Pausa... pulse una tecla para finalizar la aplicación");
         System.in.read();*/

        //s.stopJob();
        //se.stopJob();

        Crypto crypto = CryptoProvider.getInstance();

        String partida = "D:\\Proyectos en NetBeans 9\\neuromancerV1\\FTPServer2\\dynamic.txt";

        String aux = partida.substring(partida.lastIndexOf("\\") + 1, partida.indexOf("."));
        String particion = partida.substring(0, partida.lastIndexOf("\\") + 1);
        String ex = partida.substring(partida.lastIndexOf(".") + 1, partida.length());

        String pn = aux + ".public";
        String pv = aux + ".private";
        String cryp = aux + ".crypto";

        String publicName = "C:\\Key\\" + pn;
        String privateName = "C:\\Key\\" + pv;
        String cyptoFile = particion + cryp;

        System.out.println("Public: " + publicName);
        System.out.println("Private: " + privateName);
        //System.out.println("Particion: " + particion);
        System.out.println("Original: " + partida);
        System.out.println("CryptoFile: " + cyptoFile);
        System.out.println("Extension: " + ex);

        CryptoData nuevo = new CryptoData("turco", "D:\\turco.txt", "D:\\tsla.crypto", "C:\\tsla.public", "C:\\tsla.private", "txt");
//        l.appendCryptoData(nuevo);
        l.removeCryptoData(nuevo.getFileName(), nuevo.getOriginalExtension());
        CryptoData cd = l.getCryptoData(nuevo.getFileName() + "." + nuevo.getOriginalExtension());
        
        if(cd != null){
            System.out.println(cd.toString());
        }else{
            System.out.println("No existe");
        }
        
        //crypto.keyGenerateRSA(publicName, privateName);

        //crypto.encryptRSA(partida, cyptoFile, publicName);

        //crypto.decryptRSA(cyptoFile, partida, privateName);

//        RouterRetrieve list = new RouterRetrieve(config);
//        FTPFile[] files = list.getFiles("/");
//
//        for (FTPFile ff : files) {
//            if (ff.getType() == FTPFile.FILE_TYPE) {
//                System.out.println("File Name: " + ff.getName() + "  Type: " + ff.getType()
//                        + ";;;; " + FileUtils.byteCountToDisplaySize(ff.getSize()));
//            } else {
//                System.out.println("Directory Name: " + ff.getName());
//            }
//        }
//        SimpleList<Message> files = list.getAllFiles("/");
//        // SimpleList<Message> files = list.getSimpleListCurrent("/Testing/");
//        Message aux2 = null;
//        LocalIterator<Message> iter = files.getIterador();
//        int i = 0;
//        long l1 = 0L;
//
//        while (iter.hasMoreElements()) {
//            aux2 = iter.returnElement();
//            FTPFile ff = aux2.getEncapsulation();
//
//            if (ff.getType() == FTPFile.FILE_TYPE) {
//                System.out.println("File Name: " + ff.getName() + "  Type: " + aux2.getFileType()
//                        + "   Size: " + FileUtils.byteCountToDisplaySize(ff.getSize()));
//                i++;
//                l1 += ff.getSize();
//            } else {
//                System.out.println("Directory Name: " + aux2.getRemotePath());
//            }
//        }
//        DecimalFormat df = new DecimalFormat("0.000");
//        System.out.println("Files: " + i + " Total Size: " + df.format(((l1 / 1024.0) / 1024)));

        /*Message[] array = files.toArray(Message.class);

         for (Message m : array) {
         aux = m;
         FTPFile ff = aux.getEncapsulation();

         if (ff.getType() == FTPFile.FILE_TYPE) {
         System.out.println("File Name: " + aux.getRemotePath() + "  Type: " + ff.getType()
         + ";;;; " + FileUtils.byteCountToDisplaySize(ff.getSize()));
         } else {
         System.out.println("Directory Name: " + aux.getRemotePath());
         }
         }

         System.out.println("\n\n\n\n");
         String[] dir = list.getDirNames("/Testing/");

         for (String name : dir) {
         System.out.println("Directorio: " + name);
         }*/

        /*String password = "null";
        
         if(Validator.isPassword(password)){
         System.out.println("Password validado y correcto");
         }else{
         System.out.println("Password no valido");
         }
        
         String mail = "brueradamian@gmail.com";
        
         if(Validator.isMail(mail)){
         System.out.println("Nombre mail Correcto");
         }else{
         System.out.println("Nombre de mail Incorrecto");
         }
        
         String subject = "TRabajo PRactico TSb 2012 3K4.";
       
         if(Validator.isMailSubject(subject)){
         System.out.println("Es texto");
         }*/


        /*if(Validator.isTextEmpty("WTF (Walter Trae Fideos)")){
         System.out.println("Es texto + " + empty.toString());
         }*/
    }
}
