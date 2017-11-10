package keikkapaikat;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Luokka yhden yhteystiedon tallennusta ja käsittelyä varten.
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class Yhteystieto {
	
	private static int seuraava = 1;        		// Pitää kirjaa juoksevasta id-numerosta.
	private static final int EROTTIMIEN_MAARA = 3; 	// Kenttien määrä - 1.
	
	private int    yhteystietoId;
	private int    paikkaId;
	private String puhelin = "";
	private String sahkoposti = "";
	
	/** Antaa yhteystiedon id:n */
	public int getYhteystietoId() { return yhteystietoId; }
	
	/** Antaa sen keikkapaikan id:n, johon tämä yhteystieto liittyy. */
	public int getPaikkaId()      { return paikkaId; }
	
	/** Antaa tähän yhteystietoon kuuluvan puhelinnumeron. */
	public String getPuhelin()    { return puhelin; }
	
	/** Antaa tähän yhteystietoon kuuluvan sähköpostiosoitteen. */
	public String getSahkoposti() { return sahkoposti; }
	
	/** Asettaa yhteystiedon id-numeron. */
	public void setYhteystietoId(int id) 	{ yhteystietoId = id; }
	
	/** Asettaa yhteystiedolle sen kunnan id:n, johon yhteystieto liittyy. */
	public void setPaikkaId(int id)			{ paikkaId = id; }
	
	/** Asettaa yhteystiedon puhelinnumeron. */
	public void setPuhelin(String p)		{ puhelin = p; }
	
	/** Asettaa yhteystiedon sähköpostiosoitteen. */
	public void setSahkoposti(String s)		{ sahkoposti = s; }
	
	
	/**
	 * Alustaa tyhjän yhteystiedon uudella id-numerolla.
	 */
	public Yhteystieto() {
		yhteystietoId = seuraava++;
	}
	
	
	/**
	 * Alustaa tyhjän yhteystiedon uudella id-numerolla ja annetulla paikkaId:llä
	 * @param paikkaId Sen keikkapaikan Id, johon yhteystieto liitetään
	 */
	public Yhteystieto(int paikkaId) {
		yhteystietoId = seuraava++;
		this.paikkaId = paikkaId;
	}
	
	
	/**
	 * Alustaa yhteystiedon tiedot annetun merkkijonon perusteella.
	 * @param jono Parsittava jono
	 * @throws 
	 */
	public Yhteystieto(String jono) throws TietokantaException {
		this.parse(jono);
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
			yhteystietoId = Integer.parseInt(t[0].trim());
		} catch (NumberFormatException e) {
			throw new TietokantaException("Virheellinen yhteystietoId: '" + t[0].trim() + "'!");
		}
		
		try {
			paikkaId = Integer.parseInt(t[1].trim());
		} catch (NumberFormatException e) {
			throw new TietokantaException("Virheellinen paikkaId: '" + t[1].trim() + "'!");
		}
		
		puhelin = t[2].trim();
		sahkoposti = t[3].trim();
		
		if (yhteystietoId >= seuraava) seuraava = yhteystietoId + 1;
	}
	
	
	/**
	 * Tulostaa yhteystiedon tiedot tolppamerkein erotettuna.
	 * @param os Tietovirta, johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		PrintStream out = new PrintStream(os);
		out.printf("%4d | %4d | %-15s | %-40s%n", yhteystietoId, paikkaId, puhelin, sahkoposti);
	}
	
	
	/**
	 * Palauttaa yhteystiedon tiedot tolppamerkein erotettuna merkkijonona.
	 */
	public String toString() {
		return String.format("%16d | %9d | %-13s | %s", yhteystietoId, paikkaId, puhelin, sahkoposti);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	
	java.util.Random rand = new java.util.Random();
	
	
	/**
	 * Lisää yhteystiedolle tietoja testausta varten
	 */
	public void lisaaTietoja() {
		int testiPId = (paikkaId == 0) ? rand.nextInt(1000) : paikkaId;
		int testiPuh = rand.nextInt(1111111) + 8888888;
		int testiSP  = rand.nextInt(88) + 11;
		
		try {
			this.parse(String.format("%d | %d | %d | jamppa%s@hotmail.com", yhteystietoId, testiPId, testiPuh, testiSP));	
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
		Yhteystieto tyhja = new Yhteystieto();
		tyhja.tulosta(System.out);
		
		Yhteystieto paikkaEsim = new Yhteystieto(256);
		paikkaEsim.tulosta(System.out);
		
		Yhteystieto yhteystieto = new Yhteystieto();
		yhteystieto.lisaaTietoja();
		yhteystieto.tulosta(System.out);
		
		Yhteystieto yhteystieto2 = new Yhteystieto();
		yhteystieto2.lisaaTietoja();
		yhteystieto2.tulosta(System.out);
		
		System.out.println(yhteystieto);
	}
}
