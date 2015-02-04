/*
* ReflectionMessage.java
* OpenCOMJ is a flexible component model for reconfigurable reflection developed at Lancaster University.
* Copyright (C) 2005 Paul Grace
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package OpenCOM.ReflectionMessages;
import java.io.*;

/**
 * General reflection message format
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 */

public class ReflectionMessage implements java.io.Serializable{
    static final long serialVersionUID = -6868911804849179003L;
    byte Command = 98;
    
    public byte[] getAsByteStream(){
        byte[] result = null;

        try{
            // - serialize these into a byte stream
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream ow = new ObjectOutputStream(bout);
            ow.writeObject(this);
            ow.flush();
            result = bout.toByteArray();
            ow.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return result;
    }
    
    public byte[] GetPacket(){
        byte[] content = getAsByteStream();
        
        byte[] packet = new byte[content.length+1];
        packet[0]=Command;
        int k=0;
        for(int i=1;i<content.length+1;i++){
            packet[i]=content[k++];
        }
        return packet;
    }
}