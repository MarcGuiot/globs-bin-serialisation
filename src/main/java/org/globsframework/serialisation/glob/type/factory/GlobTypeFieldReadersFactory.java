package org.globsframework.serialisation.glob.type.factory;

import org.globsframework.metamodel.GlobType;
import org.globsframework.serialisation.glob.type.GlobTypeFieldReaders;

public interface GlobTypeFieldReadersFactory {

    GlobTypeFieldReaders create(GlobType globType);

}
