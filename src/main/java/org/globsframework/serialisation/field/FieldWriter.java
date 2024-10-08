package org.globsframework.serialisation.field;

import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.serialisation.stream.CodedOutputStream;

public interface FieldWriter {
    void write(CodedOutputStream codedOutputStream, FieldValuesAccessor data);

    int getFieldNumber();
}
