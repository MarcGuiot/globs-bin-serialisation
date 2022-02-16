package org.globsframework.serialisation.field.writer;

import org.globsframework.model.Glob;
import org.globsframework.serialisation.field.FieldWriter;
import org.globsframework.serialisation.stream.CodedOutputStream;

public class NullFieldWriter implements FieldWriter {
    public static final NullFieldWriter INSTANCE = new NullFieldWriter();

    public void write(CodedOutputStream codedOutputStream, Glob data) {
    }

    public int getFieldNumber() {
        return 0;
    }
}
