package keikkapaikat;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Luokka, joka vastaa muiden luokkien välisestä yhteistyöstä sekä välittää
 * tietoja käyttöliittymältä ja käyttöliittymälle.
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class Tietokanta {
	private final Keikkapaikat keikkapaikat = new Keikkapaikat();
	private final Kunnat kunnat = new Kunnat();
	private final Yhteystiedot yhteystiedot = new Yhteystiedot();

	
	/**
	 * Palauttaa keikkapaikkojen lukumäärän.
	 * @return Keikkapaikkojen lukumäärä
	 */
	public int getKeikkapaikkoja() {
		return keikkapaikat.getLkm();
	}

	
	/**
	 * Palauttaa kuntien lukumäärän.
	 * @return Kuntien lukumäärä
	 */
	public int getKuntia() {
		return kunnat.getLkm();
	}

	
	/**
	 * Palauttaa yhteystietojen lukumäärän.
	 * @return Yhteystietojen lukumäärä
	 */
	public int getYhteystietoja() {
		return yhteystiedot.getLkm();
	}
	
	
	/**
	 * Antaa i:nnen keikkapaikan
	 * @param i Keikkapaikan indeksi
	 * @return Keikkapaikka
	 */
	public Keikkapaikka annaKeikkapaikka(int i) {
		return keikkapaikat.anna(i);
	}
	
	
	/**
	 * Antaa halutun paikkaId:n omistavan keikkapaikan indeksin keikkapaikkojen
	 * alkioissa.
	 * @param paikkaId Haettavan paikan id
	 * @return Haetun paikan indeksi tai -1, jos ei löydy
	 */
	public int annaIndeksi(int paikkaId) {
		return keikkapaikat.annaIndeksi(paikkaId);
	}
	
	
	/**
	 * Antaa i:nnen kunnan
	 * @param i kunnan indeksi
	 * @return kunta
	 */
	public Kunta annaKunta(int i) {
		return kunnat.anna(i);
	}
	
	
	/**
	 * Antaa i:nnen yhteystiedon
	 * @param i Yhteystiedon indeksi
	 * @return Yhteystieto
	 */
	public Yhteystieto annaYhteystieto(int i) {
		return yhteystiedot.anna(i);
	}
	
	
	/**
	 * Antaa listan niiden yhteystietojen indekseistä, joihin
	 * liittyy halutun keikkapaikan id.
	 * @param paikkaId Keikkapaikan id
	 * @return Lista yhteystietojen indekseistä.
	 */
	public ArrayList<Integer> annaPaikanYhteystiedot(int paikkaId) {
		return yhteystiedot.annaPaikanYhteystiedot(paikkaId);
	}

	
	/**
	 * Lisää keikkapaikan.
	 * @param paikka Lisättävä keikkapaikka
	 */
	public void lisaaKeikkapaikka(Keikkapaikka paikka) {
		keikkapaikat.lisaa(paikka);
	}
	
	
	/**
	 * Lisää kunnan.
	 * @param paikka Lisättävä kunta
	 */
	public void lisaaKunta(Kunta kunta) {
		kunnat.lisaa(kunta);
	}
	
	
	/**
	 * Lisää yhteystiedon.
	 * @param yhteystieto Lisättävä yhteystieto
	 */
	public void lisaaYhteystieto(Yhteystieto yhteystieto) {
		yhteystiedot.lisaa(yhteystieto);
	}
	
	
	/**
	 * Lisää tyhjän yhteystiedon, joka liitetään tiettyyn keikkapaikkaan.
	 * @param paikkaId Sen keikkapaikan Id, johon yhteystieto liitetään
	 */
	public void lisaaYhteystieto(int paikkaId) {
		yhteystiedot.lisaa(paikkaId);
	}
	

	/**
	 * Poistaa i:nnen keikkapaikan.
	 * @param i Indeksi, jota vastaava paikka poistetaan
	 */
	public void poistaKeikkapaikka(int i) {
		keikkapaikat.poista(i);
	}
	
	
	/**
	 * Poistaa i:nnen yhteystiedon.
	 * @param i Indeksi, jota vastaava yhteystieto poistetaan
	 */
	public void poistaYhteystieto(int i) {
		yhteystiedot.poista(i);
	}
	
	
	/**
	 * Poistaa kaikki ne yhteystiedot, joilla on annettu paikkaId.
	 * @param paikkaId Sen paikan id, jonka yhteystiedot poistetaan.
	 */
	public void poistaKaikkiYhteystiedot(int paikkaId) {
		yhteystiedot.poistaKaikki(paikkaId);
	}

	
	/**
	 * Antaa listan niiden keikkapaikkojen id-numeroista, joiden nimestä TAI
	 * kunnan nimestä TAI maakunnan nimestä löytyy annettu merkkijono. Isot ja 
	 * pienet kirjaimet samastetaan.
	 * @param jono Etsittävä merkkijono
	 * @return Lista keikkapaikkojen id-numeroista
	 */
	public ArrayList<Integer> etsi(String jono) {
		jono = jono.toLowerCase();

		ArrayList<Integer> indeksit = new ArrayList<>();
		indeksit.addAll(keikkapaikat.etsi(jono));
		
		// Käydään läpi kaikki tietokannan keikkapaikat ja verrataan niiden
		// kuntaId:tä haetun kuntaId-listan alkioihin. Lisätään vain ne paikat,
		// joita ei vielä ole indeksit-listassa.
		ArrayList<Integer> kuntaIdt = kunnat.etsi(jono);
		for (int indeksi = 0; indeksi < getKeikkapaikkoja(); indeksi++)
			if (!(indeksit.contains(indeksi)) && kuntaIdt.contains(annaKeikkapaikka(indeksi).getKuntaId()))
					indeksit.add(indeksi);
		
		indeksit.sort(null);
		return indeksit;
	}
	

	/**
	 * Tulostaa kaikki i:nnen keikkapaikan tiedot sekä siihen liittyvät
	 * relaatioiden kautta tulevat tiedot. Käyttää saantimetodeja.
	 * @param i keikkapaikan indeksi
	 * @param out tulostusvirta
	 */
	public void tulostaTiedot(int i, OutputStream os) {
		PrintStream out = new PrintStream(os);
		
		Keikkapaikka kp = annaKeikkapaikka(i);
		int paikkaId = kp.getPaikkaId();
		int kuntaId = kp.getKuntaId();
		
		ArrayList<String> puh = new ArrayList<>();
		ArrayList<String> sp = new ArrayList<>();
		for (int j = 0; j < this.getYhteystietoja(); j++) {
			if ( annaYhteystieto(j).getPaikkaId() == paikkaId ) {
				puh.add(annaYhteystieto(j).getPuhelin());
				sp.add(annaYhteystieto(j).getSahkoposti());
			}
		}
		
		out.print("ID           : " + paikkaId + "\n" + 
		          "NIMI         : " + kp.getNimi() + "\n" +
		          "OSOITE       : " + kp.getLahiosoite() + "\n" +
		          "KUNTA        : " + annaKunta(kuntaId - 1).getKunta() + "\n" + 
		          "MAAKUNTA     : " + annaKunta(kuntaId - 1).getMaakunta() + "\n" +
				  "NETTISIVUT   : " + kp.getNettisivut() + "\n");
		out.println("> > > > > > >");
		for (int j = 0; j < puh.size(); j++) {
			out.printf("PUHELIN %d    : %s%n", j+1, puh.get(j));
			out.printf("SÄHKÖPOSTI %d : %s%n", j+1, sp.get(j));
		}
		out.println("> > > > > > >");
		out.print("ROUDAUS      : " + kp.getRoudaus() + "\n" + 
		          "LAVAN KOKO   : " + kp.getLavanKoko() + "\n" +
		          "VOIMAVIRTA   : " + kp.getVoimavirtapistoke() + "\n" +
		          "TALON VALOT  : " + kp.getTalonValot() + "\n" + 
		          "TALON PA     : " + kp.getTalonPA() + "\n" +
		          "TAUKOMUSA    : " + kp.getTalonTaukomusiikki() + "\n" + 
		          "SAVUKONE     : " + kp.getSavukoneenKaytto() + "\n" +
		          "MUITA HUOM.  : " + kp.getMuitaHuomioita() + "\n" +
		          "RUOKAILU     : " + kp.getRuokailu() + "\n" + 
		          "MAJOITUS     : " + kp.getMajoitus() + "\n");
		out.println();
	}
	
	
	/**
	 * Tulostaa i:nnen keikkapaikan tiedot ja siihen liittyvien
	 * yhteystietojen ja kunnan tiedot käyttäen niiden omia
	 * tulostusmetodejaan.
	 * @param i Tulostettavan keikkapaikan indeksi
	 * @param os Tietovirta, johon tulostetaan
	 */
	public void tulostaTiedot2(int i, OutputStream os) {
		PrintStream out = new PrintStream(os);
		
		Keikkapaikka tulostettavaKeikkapaikka = annaKeikkapaikka(i);
		Kunta tulostettavaKunta = annaKunta(tulostettavaKeikkapaikka.getKuntaId());
		ArrayList<Yhteystieto> tulostettavatYhteystiedot = new ArrayList<>();
		
		for (int j = 0; j < getYhteystietoja(); j++)
			if (annaYhteystieto(j).getPaikkaId() == tulostettavaKeikkapaikka.getPaikkaId())
				tulostettavatYhteystiedot.add(annaYhteystieto(j));		
		
		out.print("Keikkapaikka: "); tulostettavaKeikkapaikka.tulosta(out);
		out.print("Kunta:        "); tulostettavaKunta.tulosta(out);
		for (Yhteystieto y: tulostettavatYhteystiedot) {
			out.print("Yhteystieto:  "); y.tulosta(out);
		}	
	}
	
	
	/**
	 * Lataa yhteystiedot tiedostosta.
	 */
	public void lataaYhteystiedot() {
		try {
			yhteystiedot.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Lataa keikkapaikat tiedostosta.
	 */
	public void lataaKeikkapaikat() {
		try {
			keikkapaikat.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Lataa kunnat tiedostosta.
	 */
	public void lataaKunnat() {
		try {
			kunnat.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}


	/** 
	 * Etsii kuntien joukosta ensimmäisen kunnan id:n, jonka nimi alkaa annetulla jonolla.
	 * Jos kuntaa ei löydy, palauttaa 0.
	 * 
	 * @param jono Etsittävä jono
	 * @return Ensimmäisen kunnan id, jonka nimi alkaa etsittävällä jonolla tai 0, jos ei löydy.
	 */
	public int etsiKuntaId(String jono) {
		ArrayList<Integer> indeksit = kunnat.etsiKunta(jono);
		if (indeksit.size() == 0) return 0;
		return indeksit.get(0);
	}


	/**
	 * Tarkistaa, löytyykö annetulle paikkaId:lle kuuluvia yhteystietoja.
	 * @param paikkaId Etsittävän paikan id
	 * @return True jos löydyy, false jos ei.
	 */
	public boolean onkoYhteystietoja(int paikkaId) {
		return annaPaikanYhteystiedot(paikkaId).size() > 0;
	}


	/**
	 * Tallentaa tietokannan tiedot.
	 */
	public void tallenna() throws TietokantaException {
		try {
			yhteystiedot.tallenna();
		} catch (TietokantaException e) {
			throw new TietokantaException("Virhe tallennettaessa yhteystietoja: " + e.getMessage());
		}
		try {
		keikkapaikat.tallenna();
		} catch (TietokantaException e) {
			throw new TietokantaException("Virhe tallennettaessa keikkapaikkoja: " + e.getMessage());
		}
	}

	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Testipääohjelma
	 * @param args Ei käytössä
	 */
	public static void main(String[] args) {
		Tietokanta tietokanta = new Tietokanta();
		
		tietokanta.lataaYhteystiedot();
		tietokanta.lataaKunnat();
		tietokanta.lataaKeikkapaikat();
		
		// Lisätään tyhjä yhteystieto keikkapaikalle, jonka id on 1.
		// tietokanta.lisaaYhteystieto(1);
		
		// Tulostetaan keikkapaikkojen tiedot.		
		for (int i = 0; i < tietokanta.getKeikkapaikkoja(); i++)
			tietokanta.tulostaTiedot2(i, System.out);
	}

	
	/**
	 * Järjestää keikkapaikat nimen perusteella järjestykseen.
	 */
	public void jarjesta() {
		keikkapaikat.jarjesta();		
	}

	
	/**
	 * Tarkistaa, montako yhteystietoa kuuluu siihen
	 * keikkapaikkaan, jolla on annettu id.
	 * @param paikkaId Paikka, jonka yhteystietojen lukumäärä lasketaan
 	 * @return Yhteystietojen lukumäärä
	 */
	public int getYhteystietoja(int paikkaId) {
		int yhteystietoja = 0;
		for (int i = 0; i < getYhteystietoja(); i++)
			if (annaYhteystieto(i).getPaikkaId() == paikkaId)
				yhteystietoja++;
		return yhteystietoja;
	}
}
