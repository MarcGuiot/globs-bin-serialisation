package org.globsframework.serialisation.field.writer;

import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.serialisation.field.FieldWriter;
import org.globsframework.serialisation.stream.CodedOutputStream;

public class IntegerFieldWriter implements FieldWriter {
    private final int fieldNumber;
    private final IntegerField field;

    public IntegerFieldWriter(Integer fieldNumber, IntegerField field) {
        this.fieldNumber = fieldNumber;
        this.field = field;
    }

    public void write(CodedOutputStream codedOutputStream, FieldValuesAccessor data) {
        if (data.isSet(field)) {
            Integer value = data.get(field);
            if (value == null) {
                codedOutputStream.writeNull(fieldNumber);
            } else {
                codedOutputStream.writeInt32(fieldNumber, value);
            }
        }
    }

    public int getFieldNumber() {
        return fieldNumber;
    }
}
