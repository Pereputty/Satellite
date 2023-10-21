package hu.bagoyMuvek.Satellite.ubx;

import java.util.ArrayList;
import java.util.List;

public class UBX_NAV_SAT extends UBXMessage {

    private final List<SatInfo> satInfos = new ArrayList<>();
    private long timeStamp;
    private int version;
    private int numOfSatellites;

    public UBX_NAV_SAT(int messageClass, int messageId, int length, int[] payload, int checksumA, int checksumB) {
        super(messageClass, messageId, length, payload, checksumA, checksumB);
        processPayload();
    }

    public List<SatInfo> getSatInfos() {
        return satInfos;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNumOfSatellites() {
        return numOfSatellites;
    }

    private void processPayload() {
        int offset = 0;
        int ts1 = payload[offset++];
        int ts2 = payload[offset++];
        int ts3 = payload[offset++];
        int ts4 = payload[offset++];
        timeStamp = 16777216L * ts4 + 65536L * ts3 + 256L * ts2 + ts1;
        version = payload[offset++];
        numOfSatellites = payload[offset++];
        offset++;  // reserved unused byte
        offset++;  // reserved unused byte
        if (length > offset) {
            for (int i = 0; i < numOfSatellites; i++) {
                satInfos.add(readSatelliteInfo(offset));
                offset += 12;
            }
        }
        if (offset != length) {
            VALID = false;
        }
    }

    private SatInfo readSatelliteInfo(int offset) {
        int gnssId = payload[offset++];
        int satId = payload[offset++];
        int cno = payload[offset++];
        int elev = payload[offset++];
        int azim1 = payload[offset++];
        int azim2 = payload[offset++];
        int azim = 256 * azim2 + azim1;
        int res1 = payload[offset++];
        int res2 = payload[offset++];
        int res = 256 * res2 + res1;
        int flags1 = payload[offset++];
        int flags2 = payload[offset++];
        int flags3 = payload[offset++];
        int flags4 = payload[offset++];
        long flags = 16777216L * flags4 + 65536L * flags3 + 256L * flags2 + flags1;
        return new SatInfo(gnssId, satId, cno, elev, azim, res, flags);
    }

    @Override
    public String toString() {
        return "UBX NAV SAT Message{" +
                ", length=" + length +
                ", GPS time of week=" + timeStamp +
                ", version=" + version +
                ", num of satellites=" + numOfSatellites +
                ", satInfos=" + satInfos +
                '}';
    }

    @Override
    public String toShortString() {
        return "UBX NAV SAT Message{" +
                ", num of satellites=" + numOfSatellites +
                '}';
    }
}
