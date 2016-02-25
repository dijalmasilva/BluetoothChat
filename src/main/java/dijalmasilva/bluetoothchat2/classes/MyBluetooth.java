/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijalmasilva.bluetoothchat2.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

/**
 *
 * @author Dijalma Silva <dijalmacz@gmail.com>
 */
public class MyBluetooth {

    private LocalDevice localDevice;
    private DiscoveryAgent agent;
    private DiscoveryListener listener;
    private Object inquiry;
    private Object inquiryServices;
    private List<RemoteDevice> remoteDevices;
    private List<String> servicesFound;
    private RemoteDevice deviceSelect;
    //private StreamConnection connection;

    public MyBluetooth() {
        //  connection = null;
    }

    public void initDeviceLocal() {
        try {
            inquiry = new Object();
            inquiryServices = new Object();
            localDevice = LocalDevice.getLocalDevice();
            System.out.println("Dispositivo " + localDevice.getFriendlyName() + " iniciado.");
            agent = localDevice.getDiscoveryAgent();
        } catch (BluetoothStateException ex) {
            //ex.printStackTrace();
            System.out.println(" Verifique se o bluetooth esta ligado e volte a executar");
        }

    }

    public void searchDevices() {

        try {
            System.out.println("Procurando dispositivos...");
            remoteDevices = new ArrayList<>();
            listener = new DiscoveryClass(remoteDevices, inquiry);
            synchronized (inquiry) {
                agent.startInquiry(DiscoveryAgent.GIAC, listener);
                inquiry.wait();
                if (remoteDevices.size() == 0) {
                    System.out.println(" Nenhum dispositivo encontrado!");
                } else {
                    System.out.println(remoteDevices.size() + " dispositivo(s) encontrado(s).");
                }
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }
    }

    public void searchServices(String name) {
        //
        findSelect(name);
        UUID[] uuidSet = {new UUID(0x1105)};
        int[] attrSet = {0x0100};
        System.out.println("Procurando servicos...");
        synchronized (inquiryServices) {
            servicesFound = new ArrayList<>();
            DiscoveryListener l = new DiscoveryClass(inquiry, servicesFound);
            try {
                int serviceFound = agent.searchServices(attrSet, uuidSet, deviceSelect, l);
                System.out.println("Servico(numero): " + serviceFound);
            } catch (BluetoothStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tryConnection() {
        try {
            //connection = (StreamConnection) Connector.open(servicesFound.get(0));
            System.out.println("Imprimindo servicesFound(0): " + servicesFound.get(0));
            //HeaderSet hs = new 

            javax.microedition.io.Connection con = Connector.open(servicesFound.get(0).split(";")[0], Connector.WRITE);

            ClientSession client = (ClientSession) con;
            HeaderSet hs = client.createHeaderSet();
            HeaderSet hsConnectReply = client.connect(null);

            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                System.out.println(hsConnectReply.getResponseCode());
                System.out.println("Failed to connect");
                return;
            }else{
                System.out.println(hsConnectReply.getResponseCode());
            }
            //BLZ

            System.out.println("ID de conexao de cliente: " + client.getConnectionID());
            File f = new File("/home/dijalma/Imagens/singleton_php.jpg");
            hs.setHeader(HeaderSet.NAME, "Singleton");
            hs.setHeader(HeaderSet.TYPE, "image/jpeg");
            hs.setHeader(HeaderSet.LENGTH, new Long(f.length()));

            //
            Operation putOperation = client.put(hs); 
           OutputStream out = putOperation.openOutputStream();
            InputStream is = new FileInputStream(f);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            System.out.println("Enviando arquivo...");
            out.write(buffer);
            out.flush();
            out.close();
//            //connection = (StreamConnection) Connector.open(servicesFound.get(0));
//            System.out.println("Imprimindo servicesFound(0): " + servicesFound.get(0));
//            Connection con = new Connection();
//            con.connect(null);
//            
//            ClientSession client = (ClientSession) con;
//            HeaderSet hs = client.createHeaderSet();
//            System.out.println("ID de conexao de cliente: " + client.getConnectionID());
//            client.connect(hs);
//            File f = new File("/home/dijalma/Imagens/singleton_php.jpg");
//            hs.setHeader(HeaderSet.NAME, "Singleton");
//            hs.setHeader(HeaderSet.TYPE, "image/jpeg");
//            hs.setHeader(HeaderSet.LENGTH, f.length());
//
//            Operation putOperation = client.put(hs);
//            OutputStream out = putOperation.openOutputStream();
//            InputStream is = new FileInputStream(f);
//            byte[] buffer = new byte[is.available()];
//            is.read(buffer);
//            is.close();
//            System.out.println("Enviando arquivo...");
//            out.write(buffer);
//            out.flush();
//            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void findSelect(String name) {

        for (RemoteDevice r : remoteDevices) {
            try {
                if (name.equals(r.getFriendlyName(true))) {
                    deviceSelect = r;
                    break;
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    public void sendObject(Object obj) {

    }

    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(LocalDevice localDevice) {
        this.localDevice = localDevice;
    }

    public List<RemoteDevice> getRemoteDevices() {
        return remoteDevices;
    }

    public void setRemoteDevices(List<RemoteDevice> remoteDevices) {
        this.remoteDevices = remoteDevices;
    }

    public RemoteDevice getDeviceSelect() {
        return deviceSelect;
    }

    public void setDeviceSelect(RemoteDevice deviceSelect) {
        this.deviceSelect = deviceSelect;
    }

}
