package eu.wdaqua.lodrank.source;

import org.apache.commons.codec.net.URLCodec;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jose M. Gimenez-Garcia on 2017-05-22.
 */
public class CleanInputStream extends FilterInputStream {

    private InputStreamReader inputStreamReader;
    private URLCodec urlCodec = new URLCodec();

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    protected CleanInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        return encodeCharacter(super.read());
    }

    private int encodeCharacter(int character) {
        int returnCharacter = character;
        switch (character) {
            case ' ': returnCharacter = (int) '+';
            break;
        }
        return returnCharacter;
    }
}
