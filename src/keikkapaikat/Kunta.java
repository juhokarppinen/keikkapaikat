package keikkapaikat;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Luokka yhden kunnan tietojen tallennusta ja käsittelyä varten.
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class Kunta {

	private static int seuraava = 1;				// Pitää kirjaa juoksevasta id-numerosta.
	private static final int EROTTIMIEN_MAARA = 2; 	// Kenttien määrä - 1

	private int kuntaId;
	private String kunta;
	private String maakunta;
	
	
	/** Palauttaa kunnan id:n. */
	public int getKuntaId() { return kuntaId; }
	
	/** Palauttaa kunnan nimen. */
	public String getKunta() { return kunta; }
	
	/** Palauttaa maakunnan, missä kunta sijaitsee. */
	public String getMaakunta() { return maakunta; }
	
	/**
	 * Alustaa tyhjän kunnan uudella id-numerolla.
	 * Käytännössä tarpeeton, sillä tiedot tullaan lukemaan
	 * tiedostosta.
	 */
	public Kunta() {
		kuntaId = seuraava++;
	}
	
	
	/**
	 * Alustaa kunnan tiedot annetun merkkijonon perusteella
	 * @param jono Parsittava jono
	 */
	public Kunta(String jono) throws TietokantaException {
		try {
			this.parse(jono);
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Lukee tolppamerkein erotellun merkkijonon sisällön olion tiedoiksi.
	 * @param tietue Tietue, josta tiedot luetaan.
	 */
	public void parse(String tietue) throws TietokantaException {
		if (tietue.length() - tietue.replace("|", "").length() != EROTTIMIEN_MAARA )
			throw new TietokantaException("Väärä kenttien määrä!");
		
		String[] t = tietue.split("[|]");
		
		try {
			kuntaId = Integer.parseInt(t[0].trim());
		} catch (NumberFormatException e) {
			throw new TietokantaException("Virheellinen id: '" + t[0].trim() + "'!");
		}
		kunta = t[1].trim();
		maakunta = t[2].trim();		
	}
	
	
	
	/**
	 * Tulostaa kunnan tiedot tolppamerkein erotettuna.
	 * @param os Tietovirta, johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		PrintStream out = new PrintStream(os);
		out.printf(" %3d | %-25s | %-25s%n", kuntaId, kunta, maakunta);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	java.util.Random rand = new java.util.Random();
	
	
	/**
	 * Lisää kunnalle tietoja testausta varten.
	 */
	void lisaaTietoja() {
		String testiK = Integer.toString(rand.nextInt(1000));
		String testiMK = Integer.toString(rand.nextInt(1000));
		
		try {
			this.parse(String.format("%d | %s | %s", kuntaId, "Äänekoski_" + testiK, "Keski-Suomi_" + testiMK));	
		} 
		catch (TietokantaException e) {
			seuraava--;
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Testipääluokka
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Kunta tyhja = new Kunta();
		tyhja.tulosta(System.out);
		
		Kunta kunta = new Kunta();
		kunta.lisaaTietoja();
		kunta.tulosta(System.out);	
		
		Kunta kunta2 = new Kunta();
		kunta2.lisaaTietoja();
		kunta2.tulosta(System.out);
	}
}
