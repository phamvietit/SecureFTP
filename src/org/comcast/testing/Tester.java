/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.comcast.testing;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.comcast.builder.Mail;
import org.comcast.builder.MailBuilder;
import org.comcast.exceptions.InformationRequiredException;
import org.comcast.logic.Client;
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

/**
 *
 * @author Quality of Service
 */
public class Tester {

    public static String empty;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SchedulerException, IOException, InformationRequiredException {
        // TODO code application logic here
        ServerConfig config = new ServerConfig("QoS-PC");
        config.setUserLogin("adminroot");
        config.setPassLogin("adminroot");

        /*Message archivo = new Message(new Client(),
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
         pila.insert(archivo3);
         pila.insert(archivo);
         pila.insert(archivo2);
         pila.insert(archivo1);
         pila.insert(archivo4);

         Properties props = new Properties();
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
         builder.buildMailUserPassword("www.640intelPRO.net");

         Mail m = builder.getMail();

         InputScheduler s = new InputScheduler(config, pila, m);
         //OutputScheduler s = new OutputScheduler(config, pila, m);

         s.startJob();

         System.out.println("Pausa... pulse una tecla para finalizar la aplicación");
         System.in.read();

         s.stopJob();*/

        RouterRetrieve list = new RouterRetrieve(config);
        /*FTPFile[] files = list.getFiles("/");

         for (FTPFile ff : files) {
         if (ff.getType() == FTPFile.FILE_TYPE) {
         System.out.println("File Name: " + ff.getName() + "  Type: " + ff.getType()
         + ";;;; " + FileUtils.byteCountToDisplaySize(ff.getSize()));
         } else {
         System.out.println("Directory Name: " + ff.getName());
         }
         }*/
        SimpleList<Message> files = list.getAllFiles("/");
        Message aux = null;
        LocalIterator<Message> iter = files.getIterador();

        while (iter.hasMoreElements()) {
            aux = iter.returnElement();
            FTPFile ff = aux.getEncapsulation();

            if (ff.getType() == FTPFile.FILE_TYPE) {
                System.out.println("File Name: " + aux.getRemotePath() + "  Type: " + ff.getType()
                        + ";;;; " + FileUtils.byteCountToDisplaySize(ff.getSize()));
                System.out.println("\n");
            } else {
                System.out.println("Directory Name: " + aux.getRemotePath());
            }
        }


        /*String password = "www.640intelPRO@fox.com";
        
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
