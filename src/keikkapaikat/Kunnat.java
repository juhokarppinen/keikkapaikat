package keikkapaikat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Kunnat-tietorakenne. Toteutettu ArrayListillä
 * @author Juho Karppinen
 * @version 1.0 8.3.2016
 */
public class Kunnat {
			
	private final String tiedosto = "keikkapaikat/kunnat.dat";
	private ArrayList<Kunta> alkiot = new ArrayList<>();

	
	/**
	 * Antaa i:nnen kunnan.
	 * @param i Indeksi
	 * @return i:s kunta
	 */
	public Kunta anna(int i) throws IndexOutOfBoundsException {
		if ( i < 0 || i  >= alkiot.size() )
			throw new IndexOutOfBoundsException("Laiton indeksi: " + i);
		return alkiot.get(i);
	}
	
	
	/**
	 * Antaa i:nnen kunnan maakunnan
	 * @param i kunnan indeksi
	 * @return i:nnen kunnan maakunta
	 * @throws IndexOutOfBoundsException
	 */
	public String annaMaakunta(int i) throws IndexOutOfBoundsException {
		if ( i < 0 || i >= alkiot.size() )
			throw new IndexOutOfBoundsException("Laiton indeksi: " + i);
		return alkiot.get(i).getMaakunta();
	}
	
	
	/**
	 * Lisää kunnan alkioihin
	 * @param kunta Lisättävä kunta
	 */
	public void lisaa(Kunta kunta) {
		alkiot.add(kunta);
	}

	
	/**
	 * Etsii kuntien joukosta ne kunnat, joiden nimestä tai maakunnan nimestä
	 * löytyy annettu jono. Pienet ja isot kirjaimet samastetaan.
	 * @param jono Etsittävä jono
	 * @return Lista löydettyjen kuntien indekseistä
	 */
	public ArrayList<Integer> etsi(String jono) {
		jono = jono.toLowerCase();
		ArrayList<Integer> indeksit = new ArrayList<>(); 
		for (int i = 0; i < alkiot.size(); i++) {
			String ehdokasKunta = alkiot.get(i).getKunta().toLowerCase();
			String ehdokasMaakunta = alkiot.get(i).getMaakunta().toLowerCase();
			if ( ehdokasKunta.matches("(.*)"+jono+"(.*)") || ehdokasMaakunta.matches("(.*)"+jono+"(.*)") )
				indeksit.add(i);
		}
		return indeksit;
	}
	
	
	/**
	 * Etsii kuntien joukosta ne kunnat, joiden nimi alkaa annetulla jonolla
	 * @param jono Etsittävä jono
	 * @return Lista löydettyjen kuntien indekseistä.
	 */
	public ArrayList<Integer> etsiKunta(String jono) {
		jono = jono.toLowerCase();
		ArrayList<Integer> indeksit = new ArrayList<>(); 
		for (int i = 0; i < alkiot.size(); i++)
			if ( alkiot.get(i).getKunta().toLowerCase().startsWith(jono) )
				indeksit.add(i);
		return indeksit;	
	}
	
	
	/**
	 * Lataa kuntien tiedot tiedostosta.
	 * @throws TietokantaException Jos lataus epäonnistuu
	 */
	public void lataa() throws TietokantaException {
//		try (Scanner fi = new Scanner(new FileInputStream(tiedosto))) {
		try (Scanner fi = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(tiedosto), "UTF-8")))) {
			while (fi.hasNext()) {
				String rivi = fi.nextLine();
				if (rivi.startsWith(";")) continue;
				lisaa(new Kunta(rivi));
			}	
		} catch (FileNotFoundException e) {
			throw new TietokantaException("Tiedostoa " + tiedosto + " ei löydy!");			
		} catch (UnsupportedEncodingException e1) {
			throw new TietokantaException("Tiedoston merkistökoodaus on virheellinen!");
		}
	}
	
	
	/**
	 * Palauttaa kuntien lukumäärän
	 * @return
	 */
	public int getLkm() {
		return alkiot.size();
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// TÄSTÄ ALASPÄIN TESTAUKSEEN TARVITTAVIA METODEJA JA ATTRIBUUTTEJA //
	//////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Pääohjelma testausta varten
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Kunnat kunnat = new Kunnat();
		
		try {
			kunnat.lataa();
		} catch (TietokantaException e) {
			System.err.println(e.getMessage());
		}
		
		// Testataan kuntien saantia ja tulostusta
		for (int i = 0; i < kunnat.getLkm(); i++)
			kunnat.anna(i).tulosta(System.out);
		System.out.println();
		
		// Testataan maakuntien saantia ja tulostusta
		for (int i = 0; i < kunnat.getLkm(); i++)
			System.out.println(kunnat.annaMaakunta(i));
		System.out.println();
		
		// Testataan etsimistä
		String[] jonot = { "", "a", "e", "i", "k", "o", "p", "u", "y", "ä" };
		for (String jono : jonot) {
			System.out.println("Hakutermillä " + jono + " löytyi seuraavat:");
			for (int i : kunnat.etsi(jono)) {
				kunnat.anna(i).tulosta(System.out);
			}
			System.out.println();
		}			
	}
}
