package keikkapaikat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Keikkapaikat-tietorakenne. Toteutettu taulukolla.
 * @author Juho Karppinen
 * @version 1.0 14.3.2016
 */
public class Keikkapaikat {
	
	private final String tiedosto = "keikkapaikat/paikat.dat";
	private int max_lkm = 5;
	private int lkm = 0;
	private Keikkapaikka[] alkiot = new Keikkapaikka[max_lkm];
	
	/**
	 * Antaa keikkapaikkojen lukumäärän.
	 * @return keikkapaikkojen lukumäärä
	 */
	public int getLkm() {
		return lkm;
	}
	
	
	/**
	 * Antaa i:nnen keikkapaikan.
	 * @param i Indeksi
	 * @return i:s keikkapaikka
	 */
	public Keikkapaikka anna(int i) throws IndexOutOfBoundsException {
		if (i < 0 || i >= lkm)
			throw new IndexOutOfBoundsException("Laiton indeksi: " + i);
		return alkiot[i];		
	}
	
	
	/**
	 * Antaa alkioiden indeksin, josta annetun id:n omistava keikkapaikka löytyy.
	 * @param paikkaId Sen keikkapaikan paikkaId, jonka indeksi halutaan.
	 * @return alkiot-taulukon indeksi, josta keikkapaikka löytyy. -1 jos ei löydy.
	 */
	public int annaIndeksi(int paikkaId) {
		for (int i = 0; i < lkm; i++)
			if (paikkaId == alkiot[i].getPaikkaId())
				return i;
		return -1;		
	}
	
	
	/**
	 * Lisää keikkapaikan keikkapaikkoihin
	 * @param paikka Lisättävä keikkapaikka
	 */
	public void lisaa(Keikkapaikka paikka) {
		if (lkm >= max_lkm)     // Tässä voisi toki heittää poikkeuksen,
			this.kasvata();     // mutta kaiketi siistimpää on lisätä taulukon
		alkiot[lkm++] = paikka; // kokoa ihan muina metodeina...
		jarjesta();
	}
	

	/**
	 * Poistaa i:nnen keikkapaikan.
	 * @param i Indeksi, jota vastaava paikka poistetaan.
	 */
	public void poista(int i) {
//		alkiot[i] = null;
		for (;i < lkm; i++)
			alkiot[i] = alkiot[i+1];
		lkm--;
	}
	
	
	/**
	 * Etsii keikkapaikkojen joukosta ne keikkapaikat, joiden nimestä
	 * löytyy annettu jono.
	 * @param jono Etsittävä jono.
	 * @return Lista löydettyjen keikkapaikkojen indekseistä.
	 */
	public ArrayList<Integer> etsi(String jono) {
		jono = jono.toLowerCase();
		ArrayList<Integer> indeksit = new ArrayList<>();
		for (int i = 0; i < lkm; i++) {
			String ehdokas = alkiot[i].getNimi().toLowerCase();
			if (ehdokas.matches("(.*)"+jono+"(.*)"))
				indeksit.add(i);
		}
		return indeksit;
	}
	
	
	/**
	 * Asettaa viimeksi lisätyn keikkapaikan aakkosjärjestyksessä
	 * oikealle paikalleen. Algoritmina kuplalajittelu.
	 */
	public void jarjesta() {
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
		Keikkapaikka temp;
		for (int indeksi = lkm - 1; indeksi > 0; indeksi--) {
			if (collator.compare(alkiot[indeksi].getNimi(), alkiot[indeksi-1].getNimi()) < 0) {
				temp = alkiot[indeksi];
				alkiot[indeksi] = alkiot[indeksi-1];
				alkiot[indeksi-1] = temp;
			} 
		}
	}

	
	/**
	 * Etsii keikkapaikkojen joukosta ne keikkapaikat, joilla on
	 * annettu kuntaId.
	 * @param kuntaId KuntaId, jonka perusteella keikkapaikkoja etsitään
	 * @return Lista löydettyjen keikkapaikkojen indekseistä.
	 */
	public ArrayList<Integer> etsiKuntaId(Integer kuntaId) {
		ArrayList<Integer> indeksit = new ArrayList<>();
		for (int i = 0; i < lkm; i++)
			if (alkiot[i].getKuntaId() == kuntaId)
				indeksit.add(i);
		return indeksit;
	}
	

	/**
	 * Kasvattaa alkiot-taulukon kokoa viidellä.
	 */
	private void kasvata() {
		max_lkm += 5;
		Keikkapaikka[] t = new Keikkapaikka[max_lkm];
		for (int i = 0; i < lkm; i++)
			t[i] = alkiot[i];
		alkiot = t;
	}
	
	
	/**
	 * Lataa keikkapaikkojen tiedot tiedostosta.
	 * @throws TietokantaException Jos lataus epäonnistuu
	 */
	public void lataa() throws TietokantaException {
//		try (Scanner fi = new Scanner(new FileInputStream(tiedosto))) {
		try (Scanner fi = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(tiedosto), "UTF-8")))) {
			while (fi.hasNext()) {
				String rivi = fi.nextLine();
				if (rivi.startsWith(";")) continue;
				lisaa(new Keikkapaikka(rivi));
			}	
		} catch (FileNotFoundException e) {
			throw new TietokantaException("Tiedostoa " + tiedosto + " ei löydy!");			
		} catch (UnsupportedEncodingException e1) {
			throw new TietokantaException("Tiedoston merkistökoodaus on virheellinen!");
		}
	}
	
	
	/**
	 * Tallentaa tietokannan keikkapaikat tiedostoon.
	 * @throws TietokantaException Jos tallennus epäonnistuu
	 */
	public void tallenna() throws TietokantaException {
//		try (PrintStream fi = new PrintStream(new FileOutputStream(tiedosto, false))) {
		try (BufferedWriter fo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tiedosto, false), "UTF-8"))) {
			fo.write("; Jos haluat sorkkia tätä tiedostoa käsin, ota ensin varmuuskopio.\n");
			fo.write("; Erityisesti id-kenttien kopelointi voi aiheuttaa ongelmia!\n");
			fo.write(String.format(Keikkapaikka.getFormatter() + "%n", 
					"; paikka_id", "Nimi", "Osoite", "kunta_id",
					"Nettisivut", "Roudaus", "Lavan koko", "Voimavirtapistoke",
					"Talon valot", "Talon PA", "Talon taukomusiikki", "Savukoneen käyttö", 
					"Muita huomioita", "Ruokailu", "Majoitus"));
			for (Keikkapaikka kp: alkiot)
				if (kp != null) fo.write(kp + "\n");
		} catch (IOException e) {
			throw new TietokantaException("Virhe kirjoittaessa tiedostoa.");
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
		Keikkapaikat paikat = new Keikkapaikat();
		
		// Lisätään keikkapaikkoja testidatasta.
		try {
			paikat.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
		
		// Kokeillaan keikkapaikkojen saantia ja tulostusta.
		for (int i = 0; i < paikat.getLkm(); i++)
			System.out.println(paikat.anna(i));
		System.out.println();
		
		// Kokeillaan etsimistä.		
//		String[] etsittavat = { "i", "k", "p", "r", "y" };
//		for (String etsittava: etsittavat) {
//			ArrayList<Integer> t = paikat.etsi(etsittava);
//			System.out.printf("Hakutermillä '%s' löytyi:%n", etsittava);
//			for (Integer n: t)
//				paikat.anna(n).tulosta(System.out);
//		}
		
//		// Kokeillaan tallentamista.
//		try {
//			paikat.tallenna();
//		} catch (TietokantaException e) {
//			System.err.println(e.getMessage());
//		}
	}
}
