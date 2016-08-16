package org.eclipse.jetty.nosql.kvs.session.xstream;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.jetty.nosql.kvs.session.ISerializationTranscoder;
import org.eclipse.jetty.nosql.kvs.session.TranscoderException;

import com.thoughtworks.xstream.XStream;

public class XStreamTranscoder implements ISerializationTranscoder {
    private XStream xstream = null;
    private ClassLoader classLoader = null;

    public XStreamTranscoder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public XStreamTranscoder(ClassLoader cl) {
        xstream = new XStream();
        // FIXME: using SessionManager's `getContext().getClassLoader()` causes `com.thoughtworks.xstream.mapper.CannotResolveClassException`
        //        on deserializing `org.eclipse.jetty.nosql.kvs.session.xstream.XStreamSession`
        //  xstream.setClassLoader(cl);
        classLoader = cl;
    }

    public byte[] encode(Object obj) throws TranscoderException {
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
        
        // unsafe cast, but we really should not be sending anything that isn't serializable
        return SerializationUtils.serialize((Serializable) obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] raw, Class<T> klass) throws TranscoderException {
        return SerializationUtils.deserialize(raw);
        
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
