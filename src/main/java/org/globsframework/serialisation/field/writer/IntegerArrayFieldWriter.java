package org.globsframework.serialisation.field.writer;

import org.globsframework.metamodel.fields.IntegerArrayField;
import org.globsframework.metamodel.fields.IntegerField;
import org.globsframework.model.Glob;
import org.globsframework.serialisation.field.FieldWriter;
import org.globsframework.serialisation.stream.CodedOutputStream;

public class IntegerArrayFieldWriter implements FieldWriter {
    private final int fieldNumber;
    private final IntegerArrayField field;

    public IntegerArrayFieldWriter(Integer fieldNumber, IntegerArrayField field) {
        this.fieldNumber = fieldNumber;
        this.field = field;
    }

    public void write(CodedOutputStream codedOutputStream, Glob data) {
        if (data.isSet(field)) {
            int[] value = data.get(field);
            if (value == null) {
                codedOutputStream.writeNull(fieldNumber);
            } else {
                codedOutputStream.writeIntArray(fieldNumber, value);
            }
        }
    }

    public int getFieldNumber() {
        return fieldNumber;
    }
}
