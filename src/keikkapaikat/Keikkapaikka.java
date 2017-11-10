package keikkapaikat;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Luokka yhden keikkapaikan tietojen tallennusta ja käsittelyä varten.
 * Pitää kirjaa keikkapaikkojen merkkijonoattribuuttien suurimmista pituuksista
 * tiedostoon tallentamista varten.
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class Keikkapaikka {
	
	private static int seuraava = 1;				                 // Pitää kirjaa juoksevasta id-numerosta.
	private static final int KENTTIEN_MAARA = 15;
	private static final int EROTTIMIEN_MAARA = KENTTIEN_MAARA - 1;
	private static int[] pisimmat = new int[KENTTIEN_MAARA - 4];     // Pitää kirjaa pisimmistä tekstimuotoisista
																     // tiedoista siistiä tulostusta varten.
	private static final int OLETUSLEVEYS = 19;	                     // Alkuarvo tiedoston tekstikenttien leveydeksi. 
	
	private int     paikkaId;
	private String  nimi;
	private String  lahiosoite;
	private int     kuntaId;
	private String  nettisivut;
	private String  roudaus;
	private String  lavanKoko;
	private String  voimavirtapistoke;
	private String  talonValot;
	private String  talonPA;
	private String  talonTaukomusiikki;
	private String  savukoneenKaytto;
	private String  muitaHuomioita;
	private boolean ruokailu;
	private boolean majoitus;	
	
	public int      getPaikkaId()			{ return paikkaId; }
	public String   getNimi()				{ return nimi; }
	public String   getLahiosoite()			{ return lahiosoite; }
	public int      getKuntaId()			{ return kuntaId; }
	public String   getNettisivut()			{ return nettisivut; }
	public String   getRoudaus()			{ return roudaus; }
	public String   getLavanKoko()			{ return lavanKoko; }
	public String   getVoimavirtapistoke() 	{ return voimavirtapistoke; }
	public String   getTalonValot()			{ return talonValot; }
	public String   getTalonPA()			{ return talonPA; }
	public String   getTalonTaukomusiikki()	{ return talonTaukomusiikki; }
	public String   getSavukoneenKaytto()	{ return savukoneenKaytto; }
	public String   getMuitaHuomioita()		{ return muitaHuomioita; }
	public boolean  getRuokailu()			{ return ruokailu; }
	public boolean  getMajoitus()			{ return majoitus; }
	
	
	public void setNimi(String n)				{ nimi = n; paivitaPituus(nimi, 0); }
	public void setLahiosoite(String l)			{ lahiosoite = l; paivitaPituus(lahiosoite, 1); }
	public void setKuntaId(int id)				{ kuntaId = id; }
	public void setNettisivut(String n)			{ nettisivut = n; paivitaPituus(nettisivut, 2);}
	public void setRoudaus(String r)			{ roudaus = r; paivitaPituus(roudaus, 3);}
	public void setLavanKoko(String l) 			{ lavanKoko = l; paivitaPituus(lavanKoko, 4);}
	public void setVoimavirtapistoke(String v)	{ voimavirtapistoke = v; paivitaPituus(voimavirtapistoke, 5);}
	public void setTalonValot(String t)			{ talonValot = t; paivitaPituus(talonValot, 6);}
	public void setTalonPA(String t)			{ talonPA = t; paivitaPituus(talonPA, 7);}
	public void setTalonTaukomusiikki(String t)	{ talonTaukomusiikki = t; paivitaPituus(talonTaukomusiikki, 8);}
	public void setSavukoneenKaytto(String s)	{ savukoneenKaytto = s; paivitaPituus(savukoneenKaytto, 9);}
	public void setMuitaHuomioita(String m)		{ muitaHuomioita = m; paivitaPituus(muitaHuomioita, 10); }
	
	public void setRuokailu(boolean r)			{ ruokailu = r; }
	public void setMajoitus(boolean m)			{ majoitus = m; }
	
	
	/**
	 * Alustaa tyhjän keikkapaikan, kasvattaa juoksevaa id-numeora yhdellä ja 
	 * asettaa pisimpien tekstikenttien alkuarvot.
	 */
	public Keikkapaikka() {
		paikkaId = seuraava++;
		nimi = "Uusi";
		lahiosoite = "";
		kuntaId = 0; // Ei viittaa mihinkään kuntaan.
		nettisivut = "";
		roudaus = "";
		lavanKoko = "";
		voimavirtapistoke = "";
		talonValot = "";
		talonPA = "";
		talonTaukomusiikki = "";
		savukoneenKaytto = "";
		muitaHuomioita = "";
		ruokailu = false;
		majoitus = false;
		
		for (int i = 0; i < pisimmat.length; i++)
			pisimmat[i] = OLETUSLEVEYS;
	}
	
	
	/**
	 * Alustaa keikkapaikan tiedot annetun merkkijonon perusteella
	 * @param jono Parsittava jono
	 */
	public Keikkapaikka(String jono) throws TietokantaException {
		try {
			this.parse(jono);
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Käy läpi kaikkien merkkijonoattribuuttien pituudet ja vertaa niitä
	 * pisimmat-taulukon alkioihin. Jos jokin merkkijono on pitempi kuin
	 * sitä vastaava taulukon arvo, taulukon arvoksi muutetaan tämän
	 * jonon pituus.
	 */
	private void paivitaPituudet() {
		paivitaPituus(getNimi(), 0);
		paivitaPituus(getLahiosoite(), 1);
		paivitaPituus(getNettisivut(), 2);
		paivitaPituus(getRoudaus(), 3);
		paivitaPituus(getLavanKoko(), 4);
		paivitaPituus(getVoimavirtapistoke(), 5);
		paivitaPituus(getTalonValot(), 6);
		paivitaPituus(getTalonPA(), 7);
		paivitaPituus(getTalonTaukomusiikki(), 8);
		paivitaPituus(getSavukoneenKaytto(), 9);
		paivitaPituus(getMuitaHuomioita(), 10);
	}
	
	
	/**
	 * Tarkistaa, onko annetun merkkijonon pituus suurempi kuin pisimmat-taulukon
	 * i:s alkio, ja muuttaa taulukon i:nnen arvon jonon pituudeksi tarvittaessa.
	 * @param jono Tutkittava jono
	 * @param i pisimmat-taulukon indeksi
	 */
	private void paivitaPituus(String jono, int i) {
		 if (pisimmat[i] < jono.length()) pisimmat[i] = jono.length();
		 if (pisimmat[i] < OLETUSLEVEYS) pisimmat[i] = OLETUSLEVEYS;
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
		paikkaId = Integer.parseInt(t[0].trim());
		} catch (NumberFormatException e) {
			throw new TietokantaException("Virheellinen paikkaId: " + t[0]);
		}
		
		try {
		kuntaId = Integer.parseInt(t[3].trim());
		} catch (NumberFormatException e) {
			throw new TietokantaException("Virheellinen kuntaId: " + t[3]);
		}
		
		nimi = t[1].trim();                
		lahiosoite = t[2].trim();          
		nettisivut = t[4].trim();          
		roudaus = t[5].trim();             
		lavanKoko = t[6].trim();           
		voimavirtapistoke = t[7].trim();  
		talonValot = t[8].trim();         
		talonPA = t[9].trim();             
		talonTaukomusiikki = t[10].trim(); 
		savukoneenKaytto = t[11].trim();   
		muitaHuomioita = t[12].trim();     
		ruokailu = Boolean.parseBoolean(t[13].trim());
		majoitus = Boolean.parseBoolean(t[14].trim());

		paivitaPituudet();
		
		// Jos luettu paikkaId on suurempi kuin juokseva id, kasvatetaan juoksevaa id:tä.
		if ( seuraava <= paikkaId )
			seuraava = paikkaId + 1;
	}
	
	
	/**
	 * Tulostaa keikkapaikan tiedot tolppamerkein erotettuna.
	 * @param out Tietovirta, johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		PrintStream out = new PrintStream(os);
		out.println(toString());
	}
	
	
	/**
	 * Palauttaa keikkapaikan tietojen tulostuksessa tarvittavan tekstimuotoilijan.
	 * @return Tekstimuotoilija
	 */
	public static String getFormatter() {
//		paivitaPituudet();
		StringBuilder tulostusmuotoilija = new StringBuilder();
		tulostusmuotoilija.append("%11s | %-" + pisimmat[0] + "s | %-" + pisimmat[1] + "s | %8s ");
		for (int i = 2; i < 11; i++)
			tulostusmuotoilija.append("| %-" + pisimmat[i] + "s ");
		tulostusmuotoilija.append("| %-8s | %-8s");
		return tulostusmuotoilija.toString();
	}

	
	/**
	 * Palauttaa keikkapaikan tiedot tolppamerkein erotettuna merkkijonona.
	 */
	public String toString() {
		String tulostusmuotoilija = getFormatter();
		return String.format(tulostusmuotoilija.toString(),
				   ""+paikkaId, nimi, lahiosoite, ""+kuntaId, nettisivut, 
				   roudaus, lavanKoko, voimavirtapistoke, talonValot, 
				   talonPA, talonTaukomusiikki, savukoneenKaytto, 
				   muitaHuomioita, ruokailu, majoitus);
	}

	
	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	java.util.Random rand = new java.util.Random();
	
	
	/**
	 * Lisää keikkapaikalle satunnaisia tietoja.
	 * @param kuntaIdOnYksi Jos true, asettaa kuntaId:n arvoksi 1.
	 */
	public void lisaaTietoja(boolean kuntaIdOnYksi) {
	    int testKuntaId = kuntaIdOnYksi ? 1 : rand.nextInt(100);
		String tietue = paikkaId + "| Kartano Kievari | Saarijärventie " + rand.nextInt(100) + " | " +
                testKuntaId + " | http://www.kartanokievari.fi | Ramppi rakennuksen etuosan keskellä | "+
                "Suuri! | Lavan vasen reuna | Vain etuvalot | Melko kehno | " +
                "Kyllä | Sallittu | | true | false";
		try {
			this.parse(tietue);
		} 
		catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
		
		
	/**
	 * Testipääohjelma
	 * @param args Ei käytössä
	 */
	public static void main(String[] args) {
		Keikkapaikka tyhja = new Keikkapaikka();
		Keikkapaikka paikka = new Keikkapaikka();
		paikka.lisaaTietoja(false);
		Keikkapaikka paikka2 = new Keikkapaikka();
		paikka2.lisaaTietoja(false);
				
		tyhja.tulosta(System.out);
		paikka.tulosta(System.out);
		paikka2.tulosta(System.out);
		

	}
}
