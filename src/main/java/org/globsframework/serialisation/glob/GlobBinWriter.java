package org.globsframework.serialisation.glob;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.Glob;
import org.globsframework.serialisation.BinWriter;
import org.globsframework.serialisation.field.FieldWriter;
import org.globsframework.serialisation.glob.type.GlobTypeFieldWriters;
import org.globsframework.serialisation.glob.type.factory.DefaultGlobTypeFieldWritersFactory;
import org.globsframework.serialisation.glob.type.factory.GlobTypeFieldWritersFactory;
import org.globsframework.serialisation.glob.type.manager.GlobTypeFieldWritersManager;
import org.globsframework.serialisation.stream.CodedOutputStream;

import java.io.OutputStream;
import java.util.Collection;

public class GlobBinWriter implements BinWriter {
    private final CodedOutputStream codedOutputStream;
    private final GlobTypeFieldWritersManager globTypeFieldWritersManager;
    private final GlobTypeFieldWritersFactory globTypeFieldWritersFactory;

    public GlobBinWriter(OutputStream outputStream, GlobTypeFieldWritersManager globTypeFieldWritersManager) {
        this.codedOutputStream = CodedOutputStream.newInstance(outputStream);
        this.globTypeFieldWritersManager = globTypeFieldWritersManager;
        this.globTypeFieldWritersFactory = new DefaultGlobTypeFieldWritersFactory(this);
    }

    public void write(Glob glob) {
        GlobType globType = glob.getType();

        GlobTypeFieldWriters globTypeFieldWriters = globTypeFieldWritersManager.getOrCreate(
                globType, globTypeFieldWritersFactory);

        codedOutputStream.writeStartGlob(globType.getName());
        for (FieldWriter fieldWriter : globTypeFieldWriters.getFieldWriters()) {
            fieldWriter.write(codedOutputStream, glob);
        }
        codedOutputStream.writeEndGlob();
    }

    public void write(Collection<Glob> globs) {
        codedOutputStream.writeInt(globs.size());
        globs.forEach(this::write);
    }

}
