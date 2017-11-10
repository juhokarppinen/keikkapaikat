package fxKeikkapaikat;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import keikkapaikat.Keikkapaikka;
import keikkapaikat.Yhteystieto;
import keikkapaikat.Tietokanta;
import keikkapaikat.TietokantaException;


/**
 * Ohjainluokka, joka käsittelee käyttöliittymästä tulevat tapahtumat. 
 * 
 * TODO: Keikkapaikkojen järjestäminen toimii tällä hetkellä vain
 * kun ne ladataan tiedostosta. Toteutettava myös "live-järjestäminen",
 * kun uusia paikkoja lisätään tai paikkoja nimetään uudelleen.
 * 
 * @author Juho Karppinen
 * @version 8.4.2016
 */
public class KeikkapaikatGUIController implements Initializable {

	@FXML private ListChooser chooserKeikkapaikat;
	@FXML private ScrollPane  panelKeikkapaikka;
	@FXML private MenuItem    menuItemTallenna;
    @FXML private MenuItem    menuItemLopeta;
    @FXML private MenuItem    menuItemLisaaPaikka;
    @FXML private MenuItem    menuItemLisaaPuhelin;
    @FXML private MenuItem    menuItemLisaaSahkoposti;
    @FXML private MenuItem    menuItemPoistaPaikka;
    @FXML private MenuItem    menuItemOhje;
    @FXML private MenuItem    menuItemTietoja;
    @FXML private Button      buttonTyhjenna;
    @FXML private Button      buttonLisaaPaikka;
    @FXML private Button      buttonPoistaPaikka;
    @FXML private Button      buttonAvaaKartta;
    @FXML private Button      buttonAvaaSivu;
    @FXML private TextField	  textEtsi;
    @FXML private TextField   textNimi;
    @FXML private TextField   textOsoite;
    @FXML private TextField   textKunta;
    @FXML private TextField   textMaakunta;
    @FXML private TextField   textNettisivut;
    @FXML private TextField   textPuhelin1;
    @FXML private TextField   textSahkoposti1;
    @FXML private TextField   textPuhelin2;
    @FXML private TextField   textSahkoposti2;
    @FXML private TextField   textRoudaus;
    @FXML private TextField   textLavanKoko;
    @FXML private TextField   textVoimavirtapistoke;
    @FXML private TextField   textTalonValot;
    @FXML private TextField   textTalonPA;
    @FXML private TextField   textTalonTaukomusiikki;
    @FXML private TextField   textSavukoneenKaytto;
    @FXML private TextField   textMuitaHuomioita;
    @FXML private CheckBox    checkboxRuokailu;
    @FXML private CheckBox    checkboxMajoitus;
    
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
    	alusta();
    }
    
    @FXML void handleAvaaKartta() {
    	try {
			avaaSivu("https://www.google.fi/maps/place/" +
			          kasitteleJono(textOsoite.getText()) + "," + kasitteleJono(textKunta.getText()));
		} catch (TietokantaException e) {
			Dialogs.showMessageDialog(e.getMessage());
		}
    }

    @FXML void handleAvaaSivu() {
    	try {
			avaaSivu(textNettisivut.getText());
		} catch (TietokantaException e) {
			Dialogs.showMessageDialog(e.getMessage());
		}
    }

    @FXML void handleLisaaPaikka() {
    	lisaaPaikka();
    }
    
    @FXML void handleEtsi() {
    	etsi();
    }

    @FXML void handleTallenna() {
    	tallenna();
    }

	@FXML void handleLopeta() {
    	boolean lopetus = Dialogs.showQuestionDialog("Lopeta?", "Haluatko varmasti lopettaa?", "Kyllä", "Ei");
    	if (lopetus)
    		Platform.exit();
    }

    @FXML void handleOhje() {
    	avaaOhje();
    }

    @FXML void handlePoistaPaikka() {
    	if (keikkapaikkaKohdalla != null)
    		if (Dialogs.showQuestionDialog("Poistetaanko keikkapaikka?", 
    									   "Haluatko varmasti poistaa keikkapaikan?\nToimintoa ei voi perua.", 
    									   "Kyllä", 
    									   "Ei"))
    			poistaPaikka();
    }

    @FXML void handleTietoja() {
    	TietojaGUIController.naytaTietoja(null);
    }

    @FXML void handleTyhjenna() {
    	tyhjennaHakukentta();
    }    
    
    @FXML void handleMuutos() {
    	paivitaTiedot();
    }
    
    
    ///////////////////////////////////
    // Ei FXML-asioita tämän jälkeen //
    ///////////////////////////////////

    
    private Tietokanta tietokanta;
    private Keikkapaikka keikkapaikkaKohdalla = null;
    private ListView<Keikkapaikka> listKeikkapaikat = new ListView<Keikkapaikka>();
    private ObservableList<Keikkapaikka> listdataKeikkapaikat = FXCollections.observableArrayList();
      
    
    /**
     * Luokka, joka hoitaa keikkapaikkojen näyttämisen listassa.
     */
    public static class CellKeikkapaikka extends ListCell<Keikkapaikka> {
    	@Override protected void updateItem(Keikkapaikka item, boolean empty) {
    		super.updateItem(item, empty); // Päivitetään valittu rivi näkymään
    		setText(empty ? "" : item.getNimi());
    	}
    }
    
    
    /**
     * Alustetaan käyttöliittymän keikkapaikkalistaus, tyhjennetään 
     * tekstikentät sekä valintalaatikot ja alustetaan dynaamiset
     * käyttöliittymäkomponentit.
     */
    private void alusta() {
    	tyhjennaTekstikentat();

    	BorderPane parent = (BorderPane)chooserKeikkapaikat.getParent();
    	parent.setCenter(listKeikkapaikat);
    	listKeikkapaikat.setCellFactory( p -> new CellKeikkapaikka());
    	listKeikkapaikat.getSelectionModel().selectedItemProperty().addListener((observable, oldvalue, newvalue) -> { 
    		naytaKeikkapaikka(); });
    }
    
    
    /**
     * Lataa kunnat tiedostosta.
     */
    void lataaKunnat() {
    	tietokanta.lataaKunnat();
    }
    
    
    /**
     * Lataa keikkapaikat tiedostosta, hakee ne keikkapaikkalistaukseen ja asettaa
     * ensimmäisen keikkapaikan valituksi.
     */
    void lataaKeikkapaikat() {
    	tietokanta.lataaKeikkapaikat();
    	hae(0); 	
    }
    
    
    /**
     * Lataa yhteystiedot tiedostosta.
     */
    void lataaYhteystiedot() {
    	tietokanta.lataaYhteystiedot();
    }
    
    
    /**
     * Tallentaa kaikki tämänhetkiset tiedot tiedostoihin.
     */
    private void tallenna() {
    	String hakukentta = textEtsi.getText(); // Kierretään tallennusbugi siten, että tyhjennetään hakukenttä
    	tyhjennaHakukentta();                   // tallentamisen ajaksi, ja palautetaan haku tallennuksen
    	paivitaTiedot();                        // jälkeen.
    	try {
    		tietokanta.tallenna();
    	} catch (TietokantaException e) {
    		System.err.println(e.getMessage());
    	}
    	textEtsi.setText(hakukentta);
    	etsi();
	}
    
    
//    /**
//     * Järjestää keikkapaikat aakkosjärjestykseen.
//     */
//    private void jarjesta() {
//    	keikkapaikkaKohdalla = listKeikkapaikat.getSelectionModel().getSelectedItem();
//    	if ( keikkapaikkaKohdalla == null ) return;
//    	tietokanta.jarjesta();
//    	hae(tietokanta.annaIndeksi(keikkapaikkaKohdalla.getPaikkaId()));
//    }
    
    
    /**
     * Päivittää keikkapaikkaan liittyvät tiedot käyttöliittymän tekstikenttien perusteella.
     */
    private void paivitaTiedot() {
    	keikkapaikkaKohdalla = listKeikkapaikat.getSelectionModel().getSelectedItem();
    	if ( keikkapaikkaKohdalla == null ) return;
    	
    	keikkapaikkaKohdalla.setLahiosoite(textOsoite.getText());
    	keikkapaikkaKohdalla.setNettisivut(textNettisivut.getText());
    	keikkapaikkaKohdalla.setRoudaus(textRoudaus.getText());
    	keikkapaikkaKohdalla.setLavanKoko(textLavanKoko.getText());
    	keikkapaikkaKohdalla.setVoimavirtapistoke(textVoimavirtapistoke.getText());
    	keikkapaikkaKohdalla.setTalonValot(textTalonValot.getText());
    	keikkapaikkaKohdalla.setTalonPA(textTalonPA.getText());
    	keikkapaikkaKohdalla.setTalonTaukomusiikki(textTalonTaukomusiikki.getText());
    	keikkapaikkaKohdalla.setSavukoneenKaytto(textSavukoneenKaytto.getText());
    	keikkapaikkaKohdalla.setMuitaHuomioita(textMuitaHuomioita.getText());
    	keikkapaikkaKohdalla.setRuokailu(checkboxRuokailu.isSelected());
    	keikkapaikkaKohdalla.setMajoitus(checkboxMajoitus.isSelected());
    	
    	// Jos nimeä muutetaan, näytetään muutos myös keikkapaikkalistauksessa.
    	if (textNimi.getText() != keikkapaikkaKohdalla.getNimi()) {
    		keikkapaikkaKohdalla.setNimi(textNimi.getText());
    		listdataKeikkapaikat.set(tietokanta.annaIndeksi(keikkapaikkaKohdalla.getPaikkaId()), keikkapaikkaKohdalla);
//    		jarjesta();
    	}
    	
    	// Jos kunta muutetaan, muutetaan näkyvää maakuntaakin.
    	int kuntaId = tietokanta.etsiKuntaId(textKunta.getText());
    	if (kuntaId != keikkapaikkaKohdalla.getKuntaId()) {
    		keikkapaikkaKohdalla.setKuntaId(kuntaId);
    		naytaKeikkapaikka();
    	}
    	
    	// Jos käyttäjä antaa olemattoman kunnan, näytetään tilanne kunta- ja maakuntakentissä.
    	if (keikkapaikkaKohdalla.getKuntaId() == 0) {
    		textKunta.setText("Kuntaa ei löydy!");
    		textMaakunta.setText("Maakuntaa ei löydy!");
    	}
    	
    	try {
    		Yhteystieto yt1 = tietokanta.annaYhteystieto(tietokanta.annaPaikanYhteystiedot(keikkapaikkaKohdalla.getPaikkaId()).get(0));
    		yt1.setPuhelin(textPuhelin1.getText());
    		yt1.setSahkoposti(textSahkoposti1.getText());
    	} catch (IndexOutOfBoundsException e) {
    		System.err.println(e.getMessage());
    	}

    	try {
    		Yhteystieto yt2 = tietokanta.annaYhteystieto(tietokanta.annaPaikanYhteystiedot(keikkapaikkaKohdalla.getPaikkaId()).get(1));
    		yt2.setPuhelin(textPuhelin2.getText());
    		yt2.setSahkoposti(textSahkoposti2.getText());
    	} catch (IndexOutOfBoundsException e) {
    		System.err.println(e.getMessage());
    	} 	
    }
    
    
    /**
     * Lisää tyhjän keikkapaikan yhteystietoineen ja asettaa sen valituksi.
     */
    private void lisaaPaikka() {
    	Keikkapaikka uusi = new Keikkapaikka();
    	tietokanta.lisaaKeikkapaikka(uusi);
    	tietokanta.lisaaYhteystieto(uusi.getPaikkaId());
    	tietokanta.lisaaYhteystieto(uusi.getPaikkaId());
    	tyhjennaHakukentta(tietokanta.annaIndeksi(uusi.getPaikkaId()));
    }
    
    
    /**
     * Suodattaa keikkapaikkalistaukseen ne keikkapaikat, joiden nimestä
     * löydyy etsi-kentän merkkijono, tai joiden kunta tai maakunta alkaa
     * etsi-kentän merkkijonolla.
     */
    private void etsi() {
    	hae(0, textEtsi.getText().toLowerCase());
    }
    
    
    /**
     * Poistaa valitun keikkapaikan ja siihen liittyvät yhteystiedot.
     */
    private void poistaPaikka() {
    	keikkapaikkaKohdalla = listKeikkapaikat.getSelectionModel().getSelectedItem();
    	if (keikkapaikkaKohdalla == null) return;
    	int paikkaId = keikkapaikkaKohdalla.getPaikkaId();
    	tietokanta.poistaKeikkapaikka(tietokanta.annaIndeksi(paikkaId));
    	tietokanta.poistaKaikkiYhteystiedot(paikkaId);
    	listdataKeikkapaikat.remove(keikkapaikkaKohdalla);
    	hae(0);
    }
    
    
    /**
     * Tyhjentää kaikki käyttöliittymän tekstikentät ja checkboxit.
     */
    private void tyhjennaTekstikentat() {
    	textNimi.setText("");
    	textOsoite.setText("");
    	textKunta.setText("");
    	textMaakunta.setText("");
    	textNettisivut.setText("");
    	textRoudaus.setText("");
    	textLavanKoko.setText("");
    	textVoimavirtapistoke.setText("");
    	textTalonValot.setText("");
    	textTalonPA.setText("");
    	textTalonTaukomusiikki.setText("");
    	textSavukoneenKaytto.setText("");
    	checkboxRuokailu.setSelected(false);
    	checkboxMajoitus.setSelected(false);
    	
    	textPuhelin1.setText("");
    	textSahkoposti1.setText("");
    	textPuhelin2.setText("");
    	textSahkoposti2.setText("");
    }
    
    
    /**
     * Näyttää valitun keikkapaikan tiedot ikkunassa.
     */
    private void naytaKeikkapaikka() {
    	keikkapaikkaKohdalla = listKeikkapaikat.getSelectionModel().getSelectedItem();
    	if ( keikkapaikkaKohdalla == null ) {
    		tyhjennaTekstikentat();
    		return;
    	}

    	textNimi.setText(keikkapaikkaKohdalla.getNimi());
    	textOsoite.setText(keikkapaikkaKohdalla.getLahiosoite());
    	textKunta.setText(tietokanta.annaKunta(keikkapaikkaKohdalla.getKuntaId()).getKunta());
    	textMaakunta.setText(tietokanta.annaKunta(keikkapaikkaKohdalla.getKuntaId()).getMaakunta());
    	textNettisivut.setText(keikkapaikkaKohdalla.getNettisivut());
    	textRoudaus.setText(keikkapaikkaKohdalla.getRoudaus());
    	textLavanKoko.setText(keikkapaikkaKohdalla.getLavanKoko());
    	textVoimavirtapistoke.setText(keikkapaikkaKohdalla.getVoimavirtapistoke());
    	textTalonValot.setText(keikkapaikkaKohdalla.getTalonValot());
    	textTalonPA.setText(keikkapaikkaKohdalla.getTalonPA());
    	textTalonTaukomusiikki.setText(keikkapaikkaKohdalla.getTalonTaukomusiikki());
    	textSavukoneenKaytto.setText(keikkapaikkaKohdalla.getSavukoneenKaytto());
    	textMuitaHuomioita.setText(keikkapaikkaKohdalla.getMuitaHuomioita());
    	
    	checkboxRuokailu.setSelected(keikkapaikkaKohdalla.getRuokailu());
    	checkboxMajoitus.setSelected(keikkapaikkaKohdalla.getMajoitus());
    	
    	if (keikkapaikkaKohdalla.getKuntaId() == 0) {
    		textKunta.setText("Kuntaa ei löydy!");
    		textMaakunta.setText("Maakuntaa ei löydy!");
    	}
    	
    	ArrayList<Integer> indeksit = tietokanta.annaPaikanYhteystiedot(keikkapaikkaKohdalla.getPaikkaId());
    	try {
    		textPuhelin1.setText(tietokanta.annaYhteystieto(indeksit.get(0)).getPuhelin());
    		textSahkoposti1.setText(tietokanta.annaYhteystieto(indeksit.get(0)).getSahkoposti());
    	} catch (IndexOutOfBoundsException e) {
    		tietokanta.lisaaYhteystieto(keikkapaikkaKohdalla.getPaikkaId());
    		textPuhelin1.setText("");
    		textSahkoposti1.setText("");
    	}
    	
    	try {
    		textPuhelin2.setText(tietokanta.annaYhteystieto(indeksit.get(1)).getPuhelin());
    		textSahkoposti2.setText(tietokanta.annaYhteystieto(indeksit.get(1)).getSahkoposti());
    	} catch (IndexOutOfBoundsException e) {
    		tietokanta.lisaaYhteystieto(keikkapaikkaKohdalla.getPaikkaId());
    		textPuhelin2.setText("");
    		textSahkoposti2.setText("");
    	}
    }
    
    
    /**
     * Ottaa tietokannan käyttöön.
     * @param tietokanta Käyttöön otettava tietokanta.
     */
	public void setTietokanta(Tietokanta tietokanta) {
		this.tietokanta = tietokanta;		
	}
    
    
    /**
     * Hakee listaan niiden keikkapaikkojen tiedot, joiden nimestä,
     * kunnan nimestä tai maakunnan nimestä löytyy annettu merkkijono, 
     * ja aktivoi valitun paikan. Isot ja pienet kirjaimet samastetaan.
     * @param knro Keikkapaikan numero, joka aktivoidaan haun jälkeen.
     * @param jono Jono, jota etsitään.
     */
    protected void hae(int knro, String jono) {
    	listdataKeikkapaikat.clear();
    	listKeikkapaikat.setItems(listdataKeikkapaikat);
    	int index = 0;
    	for (Integer i: tietokanta.etsi(jono.toLowerCase())) {
    		if (i == knro) index = i;
   			listdataKeikkapaikat.add(tietokanta.annaKeikkapaikka(i));
    	}
    	listKeikkapaikat.getSelectionModel().select(index);
    }
    
    
    /**
     * Hakee kaikkien keikkapaikkojen nimet listaan.
     * @param knro Aktivoitavan keikkapaikan numero
     */
    protected void hae(int knro) {
    	hae(knro, "");
    }
    
    
    /**
     * Tyhjentää hakukentän ja asettaa listan i:nnen keikkapaikan valituksi.
     */
    private void tyhjennaHakukentta(int i) {
    	textEtsi.setText("");
    	hae(i);
    }
    
    
    /**
     * Tyhjentää hakukentän ja pitää viimeksi valitun keikkapaikan valittuna.
     * Jos valinta poistuu hakutermin johdosta, asettaa listan ensimmäisen
     * keikkapaikan valituksi.
     */
    private void tyhjennaHakukentta() {
    	keikkapaikkaKohdalla = listKeikkapaikat.getSelectionModel().getSelectedItem();
    	int knro = (keikkapaikkaKohdalla == null) ? 0 : tietokanta.annaIndeksi(keikkapaikkaKohdalla.getPaikkaId()); 
    	tyhjennaHakukentta(knro);
    }
  

    /**
     * Palauttaa annetun jonon siten, että isot kirjaimet on muutettu
     * pieniksi ja suomalaiset skandit korvattu pisteettömillä vastineillaan.
     * @param jono Käsiteltävä jono
     * @return Puunattu jono
     */
    private String kasitteleJono(String jono) {
    	return jono.toLowerCase().replace(' ','+').replace('ä','a').replace('ö','o').replace('å','a');
    }
    
    
    /**
     * Avaa annetun osoitteen oletusselaimessa.
     * @param jono Osoite, joka avataan.
     */
    private void avaaSivu(String jono) throws TietokantaException {
        try {
            Desktop.getDesktop().browse(new URL(jono).toURI());
        } catch (IOException e) {
            throw new TietokantaException("Osoitteen\n" + jono + "\navaaminen ei onnistunut.");
        } catch (URISyntaxException e) {
        	throw new TietokantaException("Osoitteen\n" + jono + "\navaaminen ei onnistunut.");
		}
    }
    
    
    /**
     * Avaa ohjelman ohjeet oletusselaimessa.
     */
    private void avaaOhje() {
    	try {
    		Desktop.getDesktop().open(new File("ohje.html"));
    	} catch (IOException e) {
    		Dialogs.showMessageDialog("Ohjetiedosto \"ohje.html\" ei aukea.");
    	}
    }
    
}
