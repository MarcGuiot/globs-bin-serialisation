package org.globsframework.serialisation.stream;

import org.globsframework.serialisation.WireConstants;
import org.globsframework.utils.serialization.SerializedInput;
import org.globsframework.utils.serialization.SerializedInputOutputFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CodedInputStream {
    private static final String UTC = "UTC";

    private SerializedInput serializedInput;

    public CodedInputStream(SerializedInput serializedInput) {
        this.serializedInput = serializedInput;
    }

    public static CodedInputStream newInstance(InputStream inputStream) {
        return new CodedInputStream(SerializedInputOutputFactory.init(inputStream));
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
        return serializedInput.readBigDecimaleArray();
    }

    public String readUtf8String() {
        return serializedInput.readUtf8String();
    }

    public String[] readStringArray() {
        return serializedInput.readStringArray();
    }

    public LocalDate readLocalDate() {
        int year = readInt();
        int month = readInt();
        int dayOfMonth = readInt();

        return LocalDate.of(year, month, dayOfMonth);
    }

    public ZonedDateTime readZonedDateTime() {
        int year = readInt();
        int month = readInt();
        int dayOfMonth = readInt();
        int hour = readInt();
        int minute = readInt();
        int second = readInt();
        int nanoOfSecond = readInt();
        ZoneId zoneId = ZoneId.of(readUtf8String());

        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zoneId);
    }

    public byte[] readBytes() {
        return serializedInput.readBytes();
    }
}
