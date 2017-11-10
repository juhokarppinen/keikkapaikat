package keikkapaikat;

/**
 * Poikkeusluokka tietorakenteesta aiheutuville poikkeuksille.
 * @author Juho Karppinen
 * @version 25.2.2016
 */
public class TietokantaException extends Exception {
	private static final long serialVersionUID = 1L;
	

	/**
	 * Poikkeuksen muodostaja, jolle tuodaan poikkeuksessa käytettävä viesti.
	 * @param viesti Poikkeuksen viesti
	 */
	public TietokantaException(String viesti) {
		super(viesti);
	}
}
