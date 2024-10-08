package org.globsframework.serialisation.stream;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeResolver;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.serialization.SerializedInput;
import org.globsframework.core.utils.serialization.SerializedInputOutputFactory;
import org.globsframework.serialisation.WireConstants;
import org.globsframework.serialisation.glob.type.GlobTypeFieldReaders;
import org.globsframework.serialisation.glob.type.manager.GlobTypeFieldReadersManager;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.globsframework.serialisation.WireConstants.Type.END_GLOB;

public class CodedInputStream {
    private static final String UTC = "UTC";
    private GlobTypeFieldReadersManager globTypeFieldReadersManager;
    private SerializedInput serializedInput;

    public CodedInputStream(GlobTypeFieldReadersManager globTypeFieldReadersManager, SerializedInput serializedInput) {
        this.globTypeFieldReadersManager = globTypeFieldReadersManager;
        this.serializedInput = serializedInput;
    }

    public static CodedInputStream newInstance(GlobTypeFieldReadersManager globTypeFieldReadersManager, InputStream inputStream) {
        return new CodedInputStream(globTypeFieldReadersManager, SerializedInputOutputFactory.init(inputStream));
    }

    public static CodedInputStream newInstance(GlobTypeFieldReadersManager globTypeFieldReadersManager, byte[] data) {
        return new CodedInputStream(globTypeFieldReadersManager, SerializedInputOutputFactory.init(data));
    }

    public int readTag() {
        return readInt();
    }

    public void skipField(int tag) {
        int type = WireConstants.getTagWireType(tag);
        switch (type) {
            case WireConstants.Type.NULL:
                break;
            case WireConstants.Type.BOOLEAN:
                readBoolean();
                break;
            case WireConstants.Type.BOOLEAN_ARRAY:
                readBooleanArray();
                break;
            case WireConstants.Type.INT:
                readInt();
                break;
            case WireConstants.Type.INT_ARRAY:
                readIntArray();
                break;
            case WireConstants.Type.LONG:
                readLong();
                break;
            case WireConstants.Type.LONG_ARRAY:
                readLongArray();
                break;
            case WireConstants.Type.DOUBLE:
                readDouble();
                break;
            case WireConstants.Type.DOUBLE_ARRAY:
                readDoubleArray();
                break;
            case WireConstants.Type.BIG_DECIMAL:
                readBigDecimal();
                break;
            case WireConstants.Type.BIG_DECIMAL_ARRAY:
                readBigDecimalArray();
                break;
            case WireConstants.Type.STRING:
            case WireConstants.Type.START_GLOB:
                readUtf8String();
                break;
            case WireConstants.Type.STRING_ARRAY:
                readStringArray();
                break;
            case WireConstants.Type.DATE:
                readLocalDate();
                break;
            case WireConstants.Type.DATE_TIME:
                readZonedDateTime();
                break;
            case WireConstants.Type.BYTES:
                readBytes();
                break;
            case WireConstants.Type.GLOB:
            case WireConstants.Type.GLOB_UNION:
                skipGlobField();
                break;
            case WireConstants.Type.GLOB_ARRAY:
            case WireConstants.Type.GLOB_UNION_ARRAY:
                int size = readInt();
                for (int index = 0; index < size; index++) {
                    skipGlobField();
                }
                break;
            default:
                throw new RuntimeException("type " + type + " not managed yet.");
        }
    }

    private void skipGlobField() {
        while (true) {
            int subTag = readTag();
            if (WireConstants.getTagWireType(subTag) == WireConstants.Type.END_GLOB) {
                break;
            }
            skipField(subTag);
        }
    }

    public boolean readBoolean() {
        return serializedInput.readBoolean();
    }

    public boolean[] readBooleanArray() {
        return serializedInput.readBooleanArray();
    }

    public int readInt() {
        return serializedInput.readNotNullInt();
    }

    public int readByte() {
        return serializedInput.readByte();
    }

    public int[] readIntArray() {
        return serializedInput.readIntArray();
    }

    public long readLong() {
        return serializedInput.readNotNullLong();
    }

    public long[] readLongArray() {
        return serializedInput.readLongArray();
    }

    public double readDouble() {
        return serializedInput.readNotNullDouble();
    }

    public double[] readDoubleArray() {
        return serializedInput.readDoubleArray();
    }

    public BigDecimal readBigDecimal() {
        BigDecimal[] bigDecimals = readBigDecimalArray();

        if (bigDecimals == null || bigDecimals.length == 0) {
            throw new RuntimeException("cannot read BigDecimal");
        }

        return bigDecimals[0];
    }

    public BigDecimal[] readBigDecimalArray() {
        return serializedInput.readBigDecimalArray();
    }

    public String readUtf8String() {
        return serializedInput.readUtf8String();
    }

    public String[] readStringArray() {
        return serializedInput.readStringArray();
    }

    public LocalDate readLocalDate() {
        int all = readInt();
        int year = (all >>> 9);
        int month = (all >>> 5) & 0xF;
        int dayOfMonth = ((int) (all & 0x1F));

        return LocalDate.of(year, month, dayOfMonth);
    }

    public ZonedDateTime readZonedDateTime() {
        int val1 = readInt();
        int year = (val1 >>> 9);
        int month = (val1 >>> 5) & 0xF;
        int dayOfMonth = ((int) (val1 & 0x1F));
        int val2 = readInt();
        int hour = (val2 >>> 12);
        int minute = (val2 >>> 6) & 0x3F;
        int second = ((int) (val2 & 0x3F));
        int nanoOfSecond = readInt();
        ZoneId zoneId = ZoneId.of(readUtf8String());

        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zoneId);
    }

    public byte[] readBytes() {
        return serializedInput.readBytes();
    }

    public Optional<Glob> readGlob(GlobTypeResolver typeResolver) {
        int tag = readTag();
        if (WireConstants.getTagWireType(tag) != WireConstants.Type.START_GLOB) {
            throw new RuntimeException("Expecting Glob but got " + tag + " : " + WireConstants.getTagWireType(tag));
        }

        String typeName = readUtf8String();
        Optional<GlobType> globTypeOpt = typeResolver.find(typeName);

        if (globTypeOpt.isPresent()) {
            return readGlob(globTypeOpt.get(), globTypeFieldReadersManager.getOrCreate(globTypeOpt.get()));
        } else {
            while (true) {
                int fieldTag = readTag();
                int tagWireType = WireConstants.getTagWireType(fieldTag);

                if (tagWireType == END_GLOB) {
                    return Optional.empty();
                }
                skipField(tag);
            }
        }
    }

    private Optional<Glob> readGlob(GlobType globType, GlobTypeFieldReaders globTypeFieldReaders) {
        MutableGlob data = globType.instantiate();
        while (true) {
            int fieldTag = readTag();
            int fieldNumber = WireConstants.getTagFieldNumber(fieldTag);
            int tagWireType = WireConstants.getTagWireType(fieldTag);

            if (tagWireType == END_GLOB) {
                return Optional.of(data);
            }

            globTypeFieldReaders.get(fieldNumber)
                    .read(data, fieldTag, tagWireType, this);
        }
    }
}
