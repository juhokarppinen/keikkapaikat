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
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Yhteystiedot-tietorakenne. Toteutettu ArrayListillä
 * @author Juho Karppinen
 * @version 1.0 8.3.2016
 */
public class Yhteystiedot {
	
	private final String tiedosto = "keikkapaikat/yhteystiedot.dat";
	private ArrayList<Yhteystieto> alkiot = new ArrayList<>();	
	
	/**
	 * Antaa yhteystietojen lukumäärän.
	 * @return Yhteystietojen lukumäärä
	 */
	public int getLkm() {
		return alkiot.size();
	}
	
	
	/**
	 * Antaa i:nnen yhteystiedon.
	 * @param i Indeksi
	 * @return yhteystieto
	 */
	public Yhteystieto anna(int i) throws IndexOutOfBoundsException {
		if ( i < 0 || i  >= alkiot.size() )
			throw new IndexOutOfBoundsException("Laiton indeksi: " + i);
		return alkiot.get(i);
	}
	
	
	/**
	 * Antaa listan niiden yhteystietojen indekseistä, joihin
	 * liittyy halutun keikkapaikan id.
	 * @param paikkaId Keikkapaikan id
	 * @return Lista yhteystietojen indekseistä.
	 */
	public ArrayList<Integer> annaPaikanYhteystiedot(int paikkaId) {
		ArrayList<Integer> indeksit = new ArrayList<>();
		for (int i = 0; i < getLkm(); i++)
			if (alkiot.get(i).getPaikkaId() == paikkaId)
				indeksit.add(i);
		return indeksit;
	}
	
	
	/**
	 * Lisää yhteystiedon alkioihin.
	 * @param kunta Lisättävä yhteystieto
	 */
	public void lisaa(Yhteystieto yhteystieto) {
		alkiot.add(yhteystieto);
	}
	
	
	/**
	 * Lisää alkioihin tyhjän yhteystiedon, jolla on
	 * halutun keikkapaikan Id. 
	 * @param paikkaId Sen keikkapaikan Id, johon yhteystieto liitetään.
	 */
	public void lisaa(int paikkaId) {
		alkiot.add(new Yhteystieto(paikkaId));
	}
	
	
	/**
	 * Poistaa i:nnen yhteystiedon.
	 * @param i Poistettavan yhteystiedon indeksi.
	 */
	public void poista(int i) {
		alkiot.remove(i);
	}
	
	
	/**
	 * Poistaa kaikki yhteystiedot, joilla on annettu paikkaId.
	 * @param paikkaId 
	 */
	public void poistaKaikki(int paikkaId) {
		int i = 0;
		while (i < getLkm()) {
			if (alkiot.get(i).getPaikkaId() == paikkaId)
				alkiot.remove(i--);
			i++;
		}
	}

	
	/**
	 * Lataa yhtyestiedot tiedostosta.
	 * @throws TietokantaException Jos lataus epäonnistuu
	 */
	public void lataa() throws TietokantaException {
//		try (Scanner fi = new Scanner(new FileInputStream(tiedosto))) {
		try (Scanner fi = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(tiedosto), "UTF-8")))) {
			while (fi.hasNext()) {
				String rivi = fi.nextLine();
				if (rivi.startsWith(";")) continue;
				lisaa(new Yhteystieto(rivi));
			}	
		} catch (FileNotFoundException e) {
			throw new TietokantaException("Tiedostoa " + tiedosto + " ei löydy!");			
		} catch (UnsupportedEncodingException e1) {
			throw new TietokantaException("Tiedoston merkistökoodaus on virheellinen!");
		}
	}


	/**
	 * Tallentaa tietokannan yhteystiedot tiedostoon.
	 * @throws TietokantaException Jos tallennus epäonnistuu
	 */
	public void tallenna() throws TietokantaException {
//		try (PrintStream fo = new PrintStream(new FileOutputStream(tiedosto, false))) {
		try (BufferedWriter fo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tiedosto, false), "UTF-8"))) {
			fo.write("; Jos haluat sorkkia tätä tiedostoa käsin, ota ensin varmuuskopio.\n");
			fo.write("; Erityisesti id-kenttien kopelointi voi aiheuttaa ongelmia!\n");
			fo.write("; yhteystieto_id | paikka_id | Puhelin       | Sähköposti\n");
			for (Yhteystieto y: alkiot)
				fo.write(y.toString() + "\n");
		} catch (IOException e) {
			throw new TietokantaException("Virhe tiedostoa kirjoittaessa.");
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Testipääluokka
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Yhteystiedot yhteystiedot = new Yhteystiedot();

		// Kokeillaan tiedostosta lataamista.
		try {
			yhteystiedot.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}		
			
		// Luodaan uusi yhteystieto.
		Yhteystieto uusi = new Yhteystieto();
		uusi.lisaaTietoja();
		yhteystiedot.lisaa(uusi);
		
		// Kokeillaan yhteystietojen saantia ja tulostamista.
		for (int i = 0; i < yhteystiedot.getLkm(); i++) {
			yhteystiedot.anna(i).tulosta(System.out);
		}

		// Kokeillaan yhteystietojen tallentamista.
		try {
			yhteystiedot.tallenna();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
	}
}
