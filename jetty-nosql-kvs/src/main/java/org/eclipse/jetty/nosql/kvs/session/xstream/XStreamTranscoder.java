package org.eclipse.jetty.nosql.kvs.session.xstream;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jetty.nosql.kvs.session.ISerializationTranscoder;
import org.eclipse.jetty.nosql.kvs.session.TranscoderException;

import com.thoughtworks.xstream.XStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XStreamTranscoder implements ISerializationTranscoder {
    private final static boolean SERIALIZABILITY_CHECK = true;
    private XStream xstream = null;
    //private ClassLoader classLoader = null;

    public XStreamTranscoder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public XStreamTranscoder(ClassLoader cl) {
        xstream = new XStream();
        // FIXME: using SessionManager's `getContext().getClassLoader()` causes `com.thoughtworks.xstream.mapper.CannotResolveClassException`
        //        on deserializing `org.eclipse.jetty.nosql.kvs.session.xstream.XStreamSession`
        //  xstream.setClassLoader(cl);
        //classLoader = cl;
    }

    public byte[] encode(Object obj) throws TranscoderException {
        if (SERIALIZABILITY_CHECK && obj instanceof Serializable) {
            // unsafe cast, but we really should not be sending anything that isn't serializable
            return SerializationUtils.serialize((Serializable) obj);
        } else {
            byte[] raw = null;
            try {
                raw = xstream.toXML(obj).getBytes("UTF-8");
            } catch (Exception error) {
                throw (new TranscoderException(error));
            }
            
            return raw;
        }
        /*
        final long t1 = System.currentTimeMillis();
        byte[] raw = null;
        try {
            raw = xstream.toXML(obj).getBytes("UTF-8");
        } catch (Exception error) {
            throw (new TranscoderException(error));
        }
        final long t2 = System.currentTimeMillis();        
        byte[] serializedBytes = SerializationUtils.serialize((Serializable) obj);
        final long t3 = System.currentTimeMillis();
        
        final String msg = String.format("XStream time = %dms (%d bytes), SerializationUtils time = %dms (%d bytes)", t2 - t1, raw.length, t3 - t2, serializedBytes.length);
        
        System.out.println(msg);
        
        return serializedBytes;
        */
        
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] raw, Class<T> klass) throws TranscoderException {
        // we could check the interfaces, but requires reflection...do a simple xstream xml test...
        T obj = null;
        
        try {
            if (raw[0] == '<') {
                obj = (T) xstream.fromXML(new String(raw, "UTF-8"));
            } else {
                obj = SerializationUtils.deserialize(raw);
            }
        } catch (Exception e) {
            //throw (new TranscoderException(e));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            
            log.error(sw.toString());
        }
        
        return obj;
        
        /*
        T obj = null;

        try {
            obj = (T) xstream.fromXML(new String(raw, "UTF-8"));
        } catch (Exception error) {
            throw (new TranscoderException(error));
        }
        return obj;
        */
    }
}
