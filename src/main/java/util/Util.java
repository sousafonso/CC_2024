package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import message.Message;

public class Util{
    public static byte[] serialize(Message msg){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(msg);
            os.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public static Message deserialize(byte[] data){
        Message msg = null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        
        try{
            ObjectInputStream is = new ObjectInputStream(in);
            msg = (Message) is.readObject();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Classe não encontrada");
            e.printStackTrace();
        }

        return msg;
    }
}