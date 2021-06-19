import java.util.Random;

public class Main {
    public static void main(String[] args) {
        GordonNewell gordonNewell = new GordonNewell();
        gordonNewell.upisUtxtGN();
        Buzen buzen = new Buzen();
        buzen.upisUtxtBuzen();
        Simulacija simulacija = new Simulacija();
        simulacija.upisUtxtSim(60);
        simulacija.upisUtxtUsrednjeno(60);
        gordonNewell.upisUtxtGNexcel();
        buzen.upisUtxtBuzenexcel();
        simulacija.upisUtxtSimexcel(60);
        simulacija.upisUtxtUsrednjenoexcel(60);
    }
}
