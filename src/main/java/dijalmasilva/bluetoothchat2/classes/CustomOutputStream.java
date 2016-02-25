/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dijalmasilva.bluetoothchat2.classes;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 * 
 * @author Dijalma Silva <dijalmacz@gmail.com>
 */
public class CustomOutputStream extends OutputStream{

    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }
    
    
    @Override
    public void write(int b) throws IOException {
    
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
