package org.globsframework.serialisation.field.writer;

import org.globsframework.core.metamodel.fields.GlobUnionField;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.core.model.Glob;
import org.globsframework.serialisation.BinWriter;
import org.globsframework.serialisation.field.FieldWriter;
import org.globsframework.serialisation.stream.CodedOutputStream;

public class GlobUnionFieldWriter implements FieldWriter {
    private final BinWriter binWriter;
    private final int fieldNumber;
    private final GlobUnionField field;

    public GlobUnionFieldWriter(BinWriter binWriter, int fieldNumber, GlobUnionField field) {
        this.binWriter = binWriter;
        this.fieldNumber = fieldNumber;
        this.field = field;
    }

    public void write(CodedOutputStream codedOutputStream, FieldValuesAccessor data) {
        if (!data.isSet(field)) {
            return;
        }
        Glob glob = data.get(field);
        if (glob == null) {
            codedOutputStream.writeNull(fieldNumber);
        } else {
            codedOutputStream.writeGlobUnion(fieldNumber);
            binWriter.write(glob);
        }
    }

    public int getFieldNumber() {
        return fieldNumber;
    }
}
