/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.comcast.visual;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.comcast.exceptions.FTPConectionRefusedException;
import org.comcast.logic.FileFinder;
import org.comcast.logic.ServerConfig;
import org.comcast.logic.Validator;
import org.comcast.router.Message;
import org.comcast.router.RouterRetrieve;
import org.comcast.strategy.FileListing;
import org.comcast.strategy.FileTypeListing;
import org.comcast.strategy.ListingStrategy;
import org.comcast.strategy.NameListing;
import org.comcast.strategy.SizeListing;
import org.comcast.structures.SimpleList;
import org.comcast.tableModels.LocalFileTableModel;
import org.comcast.tableModels.RemoteFileTableModel;
import org.comcast.wizards.DownloadWizard;
import org.comcast.wizards.UploadWizard;
import org.comcast.xml.Loader;
import org.comcast.xml.LoaderProvider;
import org.xml.sax.SAXException;

/**
 *
 * @author Quality of Service
 */
public class Main extends javax.swing.JFrame {

    private ServerConfig config;
    private Loader loader;
    private Map settings;
    private Map download;

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        centrarPantalla();
        setImageIconFrame();
        initElements();
        initObjects();
//        connection();
    }

    private void centrarPantalla() {
        Dimension tamFrame = this.getSize();//para obtener las dimensiones del frame
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();//para obtener el tamaño de la pantalla
        setLocation((tamPantalla.width - tamFrame.width) / 2, (tamPantalla.height - tamFrame.height) / 2);//para posicionar
    }

    private void setImageIconFrame() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../images/pendrive_32x32.png")));
    }

    private void initElements() {
        this.radioMenor.setSelected(true);
        this.radioExacto.setSelected(true);

        String o = "Cantidad de Archivos: ";
        String p = "Tamaño total: ";
        lblTotales.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
        lblSeleccionTotal.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));

        lblTotalesRemotos.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
        lblSeleccionTotalRemoto.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
    }

    private void initObjects() {
        try {
            loader = LoaderProvider.getInstance();
            config = loader.getServerConfiguration();
            settings = new HashMap();
            download = new HashMap();
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException | URISyntaxException ex) {
            JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void connection() {
        String j = "No se pudo establecer la conexion con el servidor FTP.";
        String c = "Mas informacion del error: ";
        String k = "Error en la conexion.";

        try {
            RouterRetrieve r = new RouterRetrieve(config);
            r.testConnection();

        } catch (SocketException ex) {
            JOptionPane.showMessageDialog(this, j + "\n" + c + ex.toString(), k, JOptionPane.ERROR_MESSAGE);

            Settings s = new Settings();
            s.setVisible(true);

        } catch (IOException | FTPConectionRefusedException ex) {
            JOptionPane.showMessageDialog(this, j + "\n" + c + ex.toString(), k, JOptionPane.ERROR_MESSAGE);

            Settings s = new Settings();
            s.setVisible(true);
        }
    }

    private void initLocalTable(String pathName) {
        String o = "Cantidad de Archivos: ";
        String p = "Tamaño total: ";

        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            FileListing list = new FileListing();
            sortTable(list);
            Message[] localMessage = list.getLocalMessage(pathName);
            long count = 0L;

            for (Message aux : localMessage) {
                count += aux.getLocalFile().length();
            }

            lblTotales.setText(o + localMessage.length + "      " + p + FileUtils.byteCountToDisplaySize(count));
            lblSeleccionTotal.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));

            this.tableLocal.setModel(new LocalFileTableModel(localMessage));
            this.tableLocal.getTableHeader().setReorderingAllowed(false);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception ex) {
            this.tableLocal.setModel(new DefaultTableModel());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            lblTotales.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
            lblSeleccionTotal.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
        }
    }

    private void initRemoteTable(String pathName) {
        String o = "Cantidad de Archivos: ";
        String p = "Tamaño total: ";

        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            FileListing list = new FileListing();
            sortTable(list);
            Message[] localMessage = list.getRemoteMessages(pathName);

            long count = 0L;

            for (Message aux : localMessage) {
                count += aux.getLocalFile().length();
            }

            lblTotalesRemotos.setText(o + localMessage.length + "      " + p + FileUtils.byteCountToDisplaySize(count));
            lblSeleccionTotalRemoto.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));

            this.tableRemote.setModel(new RemoteFileTableModel(localMessage));
            this.tableRemote.getTableHeader().setReorderingAllowed(false);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception ex) {
            this.tableRemote.setModel(new DefaultTableModel());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            lblTotalesRemotos.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
            lblSeleccionTotalRemoto.setText(o + 0 + "      " + p + FileUtils.byteCountToDisplaySize(0));
        }
    }

    private void sortTable(FileListing list) {
        int selection = this.boxSort.getSelectedIndex();

        switch (selection) {
            case 0:
                if (radioMenor.isSelected()) {
                    list.setListingStrategy(new NameListing(config, ListingStrategy.ASC));
                } else {
                    if (radioMayor.isSelected()) {
                        list.setListingStrategy(new NameListing(config, ListingStrategy.DESC));
                    }
                }
                break;

            case 1:
                if (radioMenor.isSelected()) {
                    list.setListingStrategy(new FileTypeListing(config, ListingStrategy.ASC));
                } else {
                    if (radioMayor.isSelected()) {
                        list.setListingStrategy(new FileTypeListing(config, ListingStrategy.DESC));
                    }
                }
                break;

            case 2:
                if (radioMenor.isSelected()) {
                    list.setListingStrategy(new SizeListing(config, ListingStrategy.ASC));
                } else {
                    if (radioMayor.isSelected()) {
                        list.setListingStrategy(new SizeListing(config, ListingStrategy.DESC));
                    }
                }
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        tabbed = new javax.swing.JTabbedPane();
        panelListadoLocal = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableLocal = new javax.swing.JTable();
        lblTotales = new javax.swing.JLabel();
        lblSeleccionTotal = new javax.swing.JLabel();
        panelSeleccionArchivoLocal = new javax.swing.JPanel();
        lblFileSelected = new javax.swing.JLabel();
        btnFileSelection = new javax.swing.JButton();
        btnSeleccionLocal = new javax.swing.JButton();
        panelListadoRemoto = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        panelSeleccionArchivoRemoto = new javax.swing.JPanel();
        lblFileSelectedRemote = new javax.swing.JLabel();
        btnFileSelectionRemote = new javax.swing.JButton();
        btnSeleccionRemota = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRemote = new javax.swing.JTable();
        lblSeleccionTotalRemoto = new javax.swing.JLabel();
        lblTotalesRemotos = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnUpload = new javax.swing.JButton();
        btnDownload = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        btnRestart = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelOrdenarPor = new javax.swing.JPanel();
        boxSort = new javax.swing.JComboBox();
        radioMayor = new javax.swing.JRadioButton();
        radioMenor = new javax.swing.JRadioButton();
        panelBuscarPor = new javax.swing.JPanel();
        boxBusqueda = new javax.swing.JComboBox();
        radioExacto = new javax.swing.JRadioButton();
        radioAprox = new javax.swing.JRadioButton();
        radioExtension = new javax.swing.JRadioButton();
        txtBusqueda = new javax.swing.JTextField();
        btnBuscarArchivos = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuAyuda = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SECURE FTP");
        setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        tabbed.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N

        tableLocal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableLocal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableLocal.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableLocal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableLocalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableLocal);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(lblTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116)
                .addComponent(lblSeleccionTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeleccionTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelSeleccionArchivoLocal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Directorio Local Activo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 10), new java.awt.Color(255, 51, 51))); // NOI18N

        lblFileSelected.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblFileSelected.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFileSelected.setToolTipText("");

        btnFileSelection.setText("...");
        btnFileSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileSelectionActionPerformed(evt);
            }
        });

        btnSeleccionLocal.setText("Guardar Seleccion");
        btnSeleccionLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionLocalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSeleccionArchivoLocalLayout = new javax.swing.GroupLayout(panelSeleccionArchivoLocal);
        panelSeleccionArchivoLocal.setLayout(panelSeleccionArchivoLocalLayout);
        panelSeleccionArchivoLocalLayout.setHorizontalGroup(
            panelSeleccionArchivoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeleccionArchivoLocalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFileSelection)
                .addGap(26, 26, 26)
                .addComponent(btnSeleccionLocal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSeleccionArchivoLocalLayout.setVerticalGroup(
            panelSeleccionArchivoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeleccionArchivoLocalLayout.createSequentialGroup()
                .addGroup(panelSeleccionArchivoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSeleccionArchivoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFileSelection)
                        .addComponent(btnSeleccionLocal)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelListadoLocalLayout = new javax.swing.GroupLayout(panelListadoLocal);
        panelListadoLocal.setLayout(panelListadoLocalLayout);
        panelListadoLocalLayout.setHorizontalGroup(
            panelListadoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelListadoLocalLayout.createSequentialGroup()
                .addGroup(panelListadoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSeleccionArchivoLocal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelListadoLocalLayout.setVerticalGroup(
            panelListadoLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListadoLocalLayout.createSequentialGroup()
                .addComponent(panelSeleccionArchivoLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbed.addTab("Listados de Archivos Locales", panelListadoLocal);

        panelSeleccionArchivoRemoto.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Directorio Remoto  Activo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 10), new java.awt.Color(255, 51, 51))); // NOI18N

        lblFileSelectedRemote.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblFileSelectedRemote.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFileSelectedRemote.setToolTipText("");

        btnFileSelectionRemote.setText("...");
        btnFileSelectionRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileSelectionRemoteActionPerformed(evt);
            }
        });

        btnSeleccionRemota.setText("Guardar Seleccion");
        btnSeleccionRemota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionRemotaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSeleccionArchivoRemotoLayout = new javax.swing.GroupLayout(panelSeleccionArchivoRemoto);
        panelSeleccionArchivoRemoto.setLayout(panelSeleccionArchivoRemotoLayout);
        panelSeleccionArchivoRemotoLayout.setHorizontalGroup(
            panelSeleccionArchivoRemotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeleccionArchivoRemotoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFileSelectedRemote, javax.swing.GroupLayout.PREFERRED_SIZE, 747, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFileSelectionRemote)
                .addGap(18, 18, 18)
                .addComponent(btnSeleccionRemota)
                .addContainerGap())
        );
        panelSeleccionArchivoRemotoLayout.setVerticalGroup(
            panelSeleccionArchivoRemotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblFileSelectedRemote, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelSeleccionArchivoRemotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnFileSelectionRemote)
                .addComponent(btnSeleccionRemota))
        );

        tableRemote.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableRemote.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRemoteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableRemote);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSeleccionArchivoRemoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(lblTotalesRemotos, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116)
                .addComponent(lblSeleccionTotalRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(panelSeleccionArchivoRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalesRemotos, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeleccionTotalRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelListadoRemotoLayout = new javax.swing.GroupLayout(panelListadoRemoto);
        panelListadoRemoto.setLayout(panelListadoRemotoLayout);
        panelListadoRemotoLayout.setHorizontalGroup(
            panelListadoRemotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelListadoRemotoLayout.setVerticalGroup(
            panelListadoRemotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbed.addTab("Listado Archivos Remotos", panelListadoRemoto);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnUpload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/comcast/images/Apple Red_48.png"))); // NOI18N
        btnUpload.setToolTipText("Sube archivos al servidor FTP");
        btnUpload.setFocusable(false);
        btnUpload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnUpload);

        btnDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/comcast/images/Apple Blue_48.png"))); // NOI18N
        btnDownload.setToolTipText("Descarga archivos desde un Servidor FTP");
        btnDownload.setFocusable(false);
        btnDownload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDownload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDownload);

        btnSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/comcast/images/Configuration 1-48.png"))); // NOI18N
        btnSettings.setToolTipText("Configuraciones");
        btnSettings.setFocusable(false);
        btnSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSettings);

        btnRestart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/comcast/images/Ball-logoff-48.png"))); // NOI18N
        btnRestart.setToolTipText("Resetear la aplicacion");
        btnRestart.setFocusable(false);
        btnRestart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRestart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRestart);

        jSplitPane1.setDividerLocation(350);

        panelOrdenarPor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ordenar por...", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 11), new java.awt.Color(255, 51, 51))); // NOI18N

        boxSort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nombre", "Tipo de Archivo", "Tamaño" }));
        boxSort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxSortItemStateChanged(evt);
            }
        });

        buttonGroup1.add(radioMayor);
        radioMayor.setText("Mayor a Menor");
        radioMayor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioMayorActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioMenor);
        radioMenor.setText("Menor a Mayor");
        radioMenor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioMenorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOrdenarPorLayout = new javax.swing.GroupLayout(panelOrdenarPor);
        panelOrdenarPor.setLayout(panelOrdenarPorLayout);
        panelOrdenarPorLayout.setHorizontalGroup(
            panelOrdenarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenarPorLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(boxSort, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelOrdenarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioMayor)
                    .addComponent(radioMenor))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelOrdenarPorLayout.setVerticalGroup(
            panelOrdenarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenarPorLayout.createSequentialGroup()
                .addComponent(radioMenor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioMayor)
                .addGap(0, 15, Short.MAX_VALUE))
            .addGroup(panelOrdenarPorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(panelOrdenarPor);

        panelBuscarPor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buscar por...", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 11), new java.awt.Color(255, 51, 51))); // NOI18N

        boxBusqueda.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Archivos Locales", "Archivos Remotos" }));

        buttonGroup2.add(radioExacto);
        radioExacto.setText("Nombre Exacto");

        buttonGroup2.add(radioAprox);
        radioAprox.setText("Nombre Aprox");

        buttonGroup2.add(radioExtension);
        radioExtension.setText("Extension");

        btnBuscarArchivos.setText("Buscar");
        btnBuscarArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarArchivosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBuscarPorLayout = new javax.swing.GroupLayout(panelBuscarPor);
        panelBuscarPor.setLayout(panelBuscarPorLayout);
        panelBuscarPorLayout.setHorizontalGroup(
            panelBuscarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarPorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBuscarPorLayout.createSequentialGroup()
                        .addComponent(radioExacto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioAprox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioExtension)
                        .addGap(18, 18, 18)
                        .addComponent(boxBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarArchivos, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        panelBuscarPorLayout.setVerticalGroup(
            panelBuscarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarPorLayout.createSequentialGroup()
                .addGroup(panelBuscarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioExacto)
                    .addComponent(radioAprox)
                    .addComponent(radioExtension))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarPorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarArchivos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(panelBuscarPor);

        menuAyuda.setText("Ayuda");
        jMenuBar1.add(menuAyuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbed, javax.swing.GroupLayout.PREFERRED_SIZE, 1017, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbed, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private TrayIcon trayIcon;
    private SystemTray tray;

    private void visibilidad() {
        this.setVisible(true);
    }

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        if (SystemTray.isSupported()) {
            try {

                this.setVisible(false);

                tray = SystemTray.getSystemTray();

                Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("../images/pendrive_32x32.png"));

                MouseListener mouseListener = new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //System.out.println("Tray Icon - Mouse clicked!");
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        //System.out.println("Tray Icon - Mouse entered!");
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        //System.out.println("Tray Icon - Mouse exited!");
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        //System.out.println("Tray Icon - Mouse pressed!");
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        //System.out.println("Tray Icon - Mouse released!");
                    }
                };
                ActionListener exitListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String a = "Seguro que desea cerrar la ventana";
                        String b = "Secure FTP";
                        int option = JOptionPane.showConfirmDialog(null, a, b, JOptionPane.YES_NO_OPTION);

                        if (option == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                };

                ActionListener mostrarListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        visibilidad();

                        tray.remove(trayIcon);
                    }
                };

                ActionListener packListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        compress();
                        visibilidad();

                        tray.remove(trayIcon);
                    }
                };

                ActionListener unpackListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        decompress();
                        visibilidad();

                        tray.remove(trayIcon);
                    }
                };

                PopupMenu popup = new PopupMenu();
                MenuItem ventanaPrinc = new MenuItem("Volver a ventana principal");
                MenuItem comprimir = new MenuItem("Subir Archivos");
                MenuItem descomprimir = new MenuItem("Descargar Archivos");
                MenuItem sa = new MenuItem("Salir del programa");

                ventanaPrinc.addActionListener(mostrarListener);
                comprimir.addActionListener(packListener);
                descomprimir.addActionListener(unpackListener);
                sa.addActionListener(exitListener);

                popup.add(ventanaPrinc);
                popup.add(comprimir);
                popup.add(descomprimir);
                popup.addSeparator();
                popup.add(sa);

                trayIcon = new TrayIcon(image, "Secure FTP", popup);

                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String c = "Secure FTP";
                        String d = "Click izquierdo para visualizar mas opciones.";
                        trayIcon.displayMessage(c, d, TrayIcon.MessageType.INFO);
                        visibilidad();
                        tray.remove(trayIcon);
                    }
                };
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(actionListener);
                trayIcon.addMouseListener(mouseListener);

                tray.add(trayIcon);

            } catch (AWTException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            String e = "System tray is currently not supported.";
            String f = "System Tray Error";
            JOptionPane.showMessageDialog(null, e, f, JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowIconified

    private void prepareDestination() {
        this.settings.put("destination", lblFileSelectedRemote.getText());
    }

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        prepareDestination();

        if (!this.settings.containsKey("selectedFiles")) {
            String g = "No hay Archivos Seleccionados.";
            String h = "Por favor seleccione los archivos y guarde la seleccion";
            String i = "Sin seleccion";
            JOptionPane.showMessageDialog(this, g + "\n" + h, i, JOptionPane.WARNING_MESSAGE);
            return;
        }

        UploadWizard up = new UploadWizard();
        up.main(settings);
    }//GEN-LAST:event_btnUploadActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowDeactivated

    private void btnFileSelectionRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileSelectionRemoteActionPerformed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            RemoteTree r = new RemoteTree(this.lblFileSelectedRemote, this.tableRemote, this.boxSort, this.radioMenor, this.radioMayor);
            r.setLabelTotal(lblTotalesRemotos, lblSeleccionTotalRemoto);
            r.setVisible(true);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnFileSelectionRemoteActionPerformed

    private void btnSeleccionLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionLocalActionPerformed
        int[] selectedRows = this.tableLocal.getSelectedRows();
        Message[] toTransefer = new Message[selectedRows.length];

        int i = 0;
        if (selectedRows.length != 0) {
            for (int aux : selectedRows) {
                Message valueAt = (Message) this.tableLocal.getModel().getValueAt(aux, 4);
                toTransefer[i] = valueAt;
                i++;
            }
            settings.remove("selectedFiles");
            settings.put("selectedFiles", toTransefer);
        } else {
            String j = "No ha seleccionado ningun archivo";
            String k = "Sin archivos";
            int op = JOptionPane.showConfirmDialog(this, j, k, JOptionPane.WARNING_MESSAGE);

            if (op == JOptionPane.OK_OPTION && this.tableLocal.getModel().getRowCount() == 0) {
                btnFileSelectionActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_btnSeleccionLocalActionPerformed

    private void btnFileSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileSelectionActionPerformed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            String p = lblFileSelected.getText();
            JFileChooser jfc = new JFileChooser();
            if (p != null) {
                File sel = new File(p);
                jfc = new JFileChooser(sel);
            }

            jfc.setApproveButtonText("Seleccionar");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setMultiSelectionEnabled(false);
            int response = jfc.showOpenDialog(this);

            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFiles = jfc.getSelectedFile();
                this.lblFileSelected.setText(selectedFiles.getAbsolutePath());
                initLocalTable(selectedFiles.getAbsolutePath());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnFileSelectionActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        DownloadWizard dw = new DownloadWizard();
        dw.main(download);
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnSeleccionRemotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionRemotaActionPerformed
        int[] selectedRows = this.tableRemote.getSelectedRows();
        Message[] toTransefer = new Message[selectedRows.length];

        int i = 0;
        if (selectedRows.length != 0) {
            for (int aux : selectedRows) {
                Message valueAt = (Message) this.tableRemote.getModel().getValueAt(aux, 4);
                toTransefer[i] = valueAt;
                i++;
            }
            download.remove("selectedFiles");
            download.put("selectedFiles", toTransefer);
        } else {
            int op = JOptionPane.showConfirmDialog(this, "No ha seleccionado ningun archivo", "Sin archivos", JOptionPane.WARNING_MESSAGE);

            if (op == JOptionPane.OK_OPTION && this.tableRemote.getModel().getRowCount() == 0) {
                btnFileSelectionRemoteActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_btnSeleccionRemotaActionPerformed

    private void boxSortItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxSortItemStateChanged
        initLocalTable(this.lblFileSelected.getText());
        if ("".equals(lblFileSelectedRemote.getText())) {
            this.lblFileSelectedRemote.setText("/");
        }
        initRemoteTable(this.lblFileSelectedRemote.getText());
    }//GEN-LAST:event_boxSortItemStateChanged

    private void radioMenorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioMenorActionPerformed
        initLocalTable(this.lblFileSelected.getText());
        if ("".equals(lblFileSelectedRemote.getText())) {
            this.lblFileSelectedRemote.setText("/");
        }
        initRemoteTable(this.lblFileSelectedRemote.getText());
    }//GEN-LAST:event_radioMenorActionPerformed

    private void radioMayorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioMayorActionPerformed
        initLocalTable(this.lblFileSelected.getText());
        if ("".equals(lblFileSelectedRemote.getText())) {
            this.lblFileSelectedRemote.setText("/");
        }
        initRemoteTable(this.lblFileSelectedRemote.getText());
    }//GEN-LAST:event_radioMayorActionPerformed

    private void searchTable(String search) throws Exception {
        int selection = this.boxBusqueda.getSelectedIndex();
        FileFinder finder = new FileFinder(config);

        switch (selection) {
            case 0:
                if (radioExacto.isSelected()) {
                    Message localExactName = finder.getLocalExactName(this.lblFileSelected.getText(), search);
                    if (localExactName != null) {
                        Message[] only = new Message[1];
                        only[0] = localExactName;

                        this.tableLocal.setModel(new LocalFileTableModel(only));
                    } else {
                        JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (radioAprox.isSelected()) {
                        SimpleList<Message> localNameAprox = finder.getLocalNameAprox(this.lblFileSelected.getText(), search);

                        if (!localNameAprox.isEmpty()) {
                            Message[] only = localNameAprox.toArray(Message.class);
                            this.tableLocal.setModel(new LocalFileTableModel(only));
                        } else {
                            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        if (radioExtension.isSelected()) {
                            SimpleList<Message> localNameAprox = finder.getLocalExtAprox(this.lblFileSelected.getText(), search);

                            if (!localNameAprox.isEmpty()) {
                                Message[] only = localNameAprox.toArray(Message.class);
                                this.tableLocal.setModel(new LocalFileTableModel(only));
                            } else {
                                JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
                break;

            case 1:
                if (radioExacto.isSelected()) {
                    Message remoteExactName = finder.getRemoteExactName(this.lblFileSelectedRemote.getText(), search);
                    if (remoteExactName != null) {
                        Message[] only = new Message[1];
                        only[0] = remoteExactName;

                        this.tableRemote.setModel(new RemoteFileTableModel(only));
                    } else {
                        JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (radioAprox.isSelected()) {
                        SimpleList<Message> remoteNameAprox = finder.getRemoteNameAprox(this.lblFileSelectedRemote.getText(), search);

                        if (!remoteNameAprox.isEmpty()) {
                            Message[] only = remoteNameAprox.toArray(Message.class);
                            this.tableRemote.setModel(new RemoteFileTableModel(only));
                        } else {
                            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        if (radioExtension.isSelected()) {
                            SimpleList<Message> remoteNameAprox = finder.getRemoteExtAprox(this.lblFileSelectedRemote.getText(), search);

                            if (!remoteNameAprox.isEmpty()) {
                                Message[] only = remoteNameAprox.toArray(Message.class);
                                this.tableRemote.setModel(new RemoteFileTableModel(only));
                            } else {
                                JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo", "Archivo no encontrado", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
                break;
        }
    }

    private void btnBuscarArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarArchivosActionPerformed
        String search = this.txtBusqueda.getText();

        if (Validator.isTextEmpty(search) && search.startsWith(" ")) {
            JOptionPane.showMessageDialog(this, "Campo de texto vacio", "Campo vacio", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                searchTable(search);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnBuscarArchivosActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Settings set = new Settings();
            set.setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void tableLocalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableLocalMouseClicked
        int[] selectedRows = this.tableLocal.getSelectedRows();

        String o = "Cantidad de Archivos Seleccionados: ";
        String p = "Tamaño seleccion: ";
        long count = 0L;

        int i = 0;
        if (selectedRows.length != 0) {
            for (int aux : selectedRows) {
                Message valueAt = (Message) this.tableLocal.getModel().getValueAt(aux, 4);
                count += valueAt.getLocalFile().length();
                i++;
            }
        }

        lblSeleccionTotal.setText(o + selectedRows.length + "      " + p + FileUtils.byteCountToDisplaySize(count));
    }//GEN-LAST:event_tableLocalMouseClicked

    private void tableRemoteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRemoteMouseClicked
        int[] selectedRows = this.tableRemote.getSelectedRows();

        String o = "Cantidad de Archivos Seleccionados: ";
        String p = "Tamaño seleccion: ";
        long count = 0L;

        int i = 0;
        if (selectedRows.length != 0) {
            for (int aux : selectedRows) {
                Message valueAt = (Message) this.tableRemote.getModel().getValueAt(aux, 4);
                count += valueAt.getFtpFile().getSize();
                i++;
            }
        }

        lblSeleccionTotalRemoto.setText(o + selectedRows.length + "      " + p + FileUtils.byteCountToDisplaySize(count));
    }//GEN-LAST:event_tableRemoteMouseClicked

    private void btnRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestartActionPerformed
        try {
            /**/
            Runnable b = new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        File[] exist = {new File("C:\\Temp\\"), new File("C:\\Key\\"), new File("C:\\ServerDownloads\\")};

                        for (File aux : exist) {
                            if (!aux.exists()) {
                                aux.mkdir();
                            }
                        }

//                        new Main().setVisible(true);
                        Main m = new Main();
//                        m.connection();
                        m.setVisible(true);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        JOptionPane.showConfirmDialog(null, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            };

            restartApplication(b);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRestartActionPerformed
    /**
     * Sun property pointing the main class and its arguments. Might not be
     * defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";

    /**
     * Restart the current Java application
     *
     * @param runBeforeRestart some custom code to be run before restarting
     * @throws IOException
     */
    private void restartApplication(Runnable runBeforeRestart) throws IOException {
        try {
            // java binary
            String java = System.getProperty("java.home") + "/bin/java";
            // vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
                // address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

            // program main and program arguments
            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
            // program main is a jar
            if (mainCommand[0].endsWith(".jar")) {
                // if it's a jar, add -jar mainJar
                cmd.append("-jar " + new File(mainCommand[0]).getPath());
            } else {
                // else it's a .class, add the classpath and mainClass
                cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
            }
            // finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            // execute some custom code before restarting
            if (runBeforeRestart != null) {
                runBeforeRestart.run();
            }
            // exit
            System.exit(0);
        } catch (Exception e) {
            // something went wrong
            throw new IOException("Error while trying to restart the application", e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    File[] exist = {new File("C:\\Temp\\"), new File("C:\\Key\\"), new File("C:\\ServerDownloads\\")};

                    for (File aux : exist) {
                        if (!aux.exists()) {
                            aux.mkdir();
                        }
                    }

//                    new Main().setVisible(true);
                    Main m = new Main();
                    m.connection();
                    m.setVisible(true);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox boxBusqueda;
    private javax.swing.JComboBox boxSort;
    private javax.swing.JButton btnBuscarArchivos;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnFileSelection;
    private javax.swing.JButton btnFileSelectionRemote;
    private javax.swing.JButton btnRestart;
    private javax.swing.JButton btnSeleccionLocal;
    private javax.swing.JButton btnSeleccionRemota;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnUpload;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblFileSelected;
    private javax.swing.JLabel lblFileSelectedRemote;
    private javax.swing.JLabel lblSeleccionTotal;
    private javax.swing.JLabel lblSeleccionTotalRemoto;
    private javax.swing.JLabel lblTotales;
    private javax.swing.JLabel lblTotalesRemotos;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JPanel panelBuscarPor;
    private javax.swing.JPanel panelListadoLocal;
    private javax.swing.JPanel panelListadoRemoto;
    private javax.swing.JPanel panelOrdenarPor;
    private javax.swing.JPanel panelSeleccionArchivoLocal;
    private javax.swing.JPanel panelSeleccionArchivoRemoto;
    private javax.swing.JRadioButton radioAprox;
    private javax.swing.JRadioButton radioExacto;
    private javax.swing.JRadioButton radioExtension;
    private javax.swing.JRadioButton radioMayor;
    private javax.swing.JRadioButton radioMenor;
    private javax.swing.JTabbedPane tabbed;
    private javax.swing.JTable tableLocal;
    private javax.swing.JTable tableRemote;
    private javax.swing.JTextField txtBusqueda;
    // End of variables declaration//GEN-END:variables
}
