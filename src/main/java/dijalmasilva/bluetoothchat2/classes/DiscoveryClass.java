/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijalmasilva.bluetoothchat2.classes;

import java.io.IOException;
import java.util.List;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 *
 * @author Dijalma Silva <dijalmacz@gmail.com>
 */
public class DiscoveryClass implements DiscoveryListener {

    private List<RemoteDevice> remoteDevices;
    private Object inquiry;
    private Object inquiryServices;
    private List<String> serviceFound;

    public DiscoveryClass(List<RemoteDevice> remoteDevices, Object inquiry) {
        this.remoteDevices = remoteDevices;
        this.inquiry = inquiry;
    }

    public DiscoveryClass(Object inquiryServices, List<String> serviceFound) {
        this.inquiryServices = inquiryServices;
        this.serviceFound = serviceFound;
    }

    @Override
    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
        remoteDevices.add(rd);
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {
        for (int k = 0; k < srs.length; k++) {
            String url = srs[k].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                continue;
            }
            serviceFound.add(url);
            DataElement serviceName = srs[k].getAttributeValue(0x0100);
            if (serviceName != null) {
                System.out.println("Servico " + serviceName.getValue() + " encontrado " + url);
            } else {
                System.out.println("Servico encontrado: " + url);
            }
        }
        
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {
        System.out.println("Busca pelo servico completa!");
        synchronized(inquiryServices){
            inquiryServices.notifyAll();
        }
    }

    @Override
    public void inquiryCompleted(int i) {
        System.out.println("Busca pelos dispositivos realizada com sucesso!");
        synchronized (inquiry) {
            inquiry.notifyAll();
        }
    }

}
