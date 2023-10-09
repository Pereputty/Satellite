package hu.bagoyMuvek.Satellite.ubx;

public class SatInfo {
    int gnssIdentifier;
    int satelliteIdentifier;
    int cno;
    int elev;
    int azim;
    int prRes;
    long bitMask;

    public SatInfo(int gnssIdentifier, int satelliteIdentifier, int cno, int elev, int azim, int prRes, long bitMask) {
        this.gnssIdentifier = gnssIdentifier;
        this.satelliteIdentifier = satelliteIdentifier;
        this.cno = cno;
        this.elev = elev;
        this.azim = azim;
        this.prRes = prRes;
        this.bitMask = bitMask;
    }

    private static String gnssIdentifierToString(int gnssId) {
        switch (gnssId) {
            case 0:
                return "GPS";
            case 1:
                return "SBAS";
            case 2:
                return "Galileo";
            case 3:
                return "BeiDou";
            case 5:
                return "QZSS";
            case 6:
                return "GLONAS";
            default:
                return "Oh sit, this is a spy satellite";
        }
    }

    public int getGnssIdentifier() {
        return gnssIdentifier;
    }

    public int getSatelliteIdentifier() {
        return satelliteIdentifier;
    }

    public int getCno() {
        return cno;
    }

    public int getElev() {
        return elev;
    }

    public int getAzim() {
        return azim;
    }

    @Override
    public String toString() {
        return "\nSatInfo{" +
                "gnss=" + gnssIdentifierToString(gnssIdentifier) +
                ", satId=" + satelliteIdentifier +
                ", cno=" + cno +
                ", elev=" + elev +
                ", azim=" + azim +
                ", prRes=" + prRes +
                //", bitMask=" + bitMask + // useless shit
                "}";
    }
}
