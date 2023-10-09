package hu.bagoyMuvek.Satellite.ubx;

import java.util.Arrays;

public class UBXMessage {
    protected int messageClass;
    protected int messageId;
    protected int length;
    protected int[] payload;
    protected int checksumA;
    protected int checksumB;
    protected boolean VALID = true;


    public UBXMessage(int messageClass, int messageId, int length, int[] payload, int checksumA, int checksumB) {
        this.messageClass = messageClass;
        this.messageId = messageId;
        this.length = length;
        this.payload = payload;
        this.checksumA = checksumA;
        this.checksumB = checksumB;
    }

    @Override
    public String toString() {
        return "UBXMessage{" +
                "class=Ox" + String.format("%02X", messageClass) +
                ", id=Ox" + String.format("%02X", messageId) +
                ", length=" + length +
                ", payload=" + Arrays.toString(payload) +
                ", checksumA=" + checksumA +
                ", checksumB=" + checksumB +
                '}';
    }
}
