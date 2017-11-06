package at.fhv.team3.presentation.home;

        import at.fhv.team3.rmi.interfaces.RMIMediaSearch;
        import at.fhv.team3.domain.dto.BookDTO;
        import at.fhv.team3.domain.dto.DTO;
        import at.fhv.team3.domain.dto.DvdDTO;
        import at.fhv.team3.domain.dto.MagazineDTO;
        import at.fhv.team3.presentation.bibinfo.BibInfoView;
        import at.fhv.team3.presentation.costumermanagement.CostumerManagementView;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.stage.Modality;
        import javafx.stage.Stage;
        import javafx.stage.WindowEvent;

        import java.net.URL;
        import java.rmi.registry.LocateRegistry;
        import java.rmi.registry.Registry;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Optional;
        import java.util.ResourceBundle;

public class HomePresenter implements Initializable {

    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private TableView<BookDTO> bookTable;

    @FXML
    private TableColumn<BookDTO, String> bookTitle;

    @FXML
    private TableColumn<BookDTO, String> bookAuthor;

    @FXML
    private TableColumn<BookDTO, String> bookIsbn;

    @FXML
    private TableView<DvdDTO> dvdTable;

    @FXML
    private TableColumn<DvdDTO, String> dvdTitle;

    @FXML
    private TableColumn<DvdDTO, String> dvdRegisseur;

    @FXML
    private TableView<MagazineDTO> magazineTable;

    @FXML
    private TableColumn<MagazineDTO, String> magazineTitle;

    @FXML
    private TableColumn<MagazineDTO, String> magazineEdition;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button BibInfoButton;

    @FXML
    private Button CostumerManagementButton;



    @FXML
    private void handleButtonActionBibInfo(ActionEvent event) {
        BibInfoView bi = new BibInfoView();
        Scene scene = new Scene(bi.getView());
        Stage stage = (Stage) BibInfoButton.getScene().getWindow();
        stage.setHeight(BibInfoButton.getScene().getWindow().getHeight());
        stage.setWidth(BibInfoButton.getScene().getWindow().getWidth());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleButtonActionCostumerManagement(ActionEvent event) {
        CostumerManagementView cm = new CostumerManagementView();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(cm.getView()));
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Ihre Eingaben gehen verloren", ButtonType.CANCEL, ButtonType.OK);
                alert.setTitle("Attention");
                alert.setHeaderText("Wollen Sie wirklich abbrechen?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    stage.close();
                } else {
                    event.consume();
                }
            }
        });
        stage.show();

    }

    @FXML
    public void search(ActionEvent event) {

        bookTitle.setCellValueFactory(new PropertyValueFactory<>("_title"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("_author"));
        bookIsbn.setCellValueFactory(new PropertyValueFactory<>("_isbn"));

        dvdTitle.setCellValueFactory(new PropertyValueFactory<>("_title"));
        dvdRegisseur.setCellValueFactory(new PropertyValueFactory<>("_regisseur"));

        magazineTitle.setCellValueFactory(new PropertyValueFactory<>("_title"));
        magazineEdition.setCellValueFactory(new PropertyValueFactory<>("_edition"));

        if(!searchField.getText().isEmpty()) {
            try {
                Registry registry = LocateRegistry.getRegistry(1099);
                RMIMediaSearch searchMedia = (RMIMediaSearch) registry.lookup("Search");

                    ArrayList<ArrayList<DTO>> allMedias = searchMedia.search(searchField.getText());

                    ArrayList<DTO> bookArrayList= allMedias.get(0);
                    ArrayList<DTO> dvdArrayList = allMedias.get(1);
                    ArrayList<DTO> magazineArrayList = allMedias.get(2);

                    ObservableList<BookDTO> books = FXCollections.observableArrayList();

                    // buch hashmap iterieren und daten holen
                    for (int i = 0; i < bookArrayList.size(); i++) {
                        HashMap<String, String> bookResult = bookArrayList.get(i).getAllData();
                        books.add(new BookDTO(Integer.parseInt(bookResult.get("id")), bookResult.get("title"), bookResult.get("publisher"), bookResult.get("author"),
                                bookResult.get("isbn"), bookResult.get("edition"), bookResult.get("pictureURL"), bookResult.get("shelfPos")));
                    }

                    ObservableList<DvdDTO> dvds = FXCollections.observableArrayList();
                    for (int i = 0; i < dvdArrayList.size(); i++) {
                        HashMap<String, String> dvdResult = dvdArrayList.get(i).getAllData();
                            dvds.add(new DvdDTO(Integer.parseInt(dvdResult.get("id")), dvdResult.get("title"), dvdResult.get("regisseur"),
                                dvdResult.get("pictureURL"), dvdResult.get("shelfPos")));
                    }

                    ObservableList<MagazineDTO> magazines = FXCollections.observableArrayList();
                    for (int i = 0; i < magazineArrayList.size(); i++) {
                        HashMap<String, String> magazineResult = magazineArrayList.get(i).getAllData();
                        magazines.add(new MagazineDTO(Integer.parseInt(magazineResult.get("id")), magazineResult.get("title"), magazineResult.get("edition"),
                                magazineResult.get("publisher"), magazineResult.get("pictureURL"), magazineResult.get("shelfPos")));
                    }

                    bookTable.setItems(books);

                    dvdTable.setItems(dvds);

                    magazineTable.setItems(magazines);

                    bookTable.getColumns().addAll(bookTitle, bookAuthor, bookIsbn);

                    dvdTable.getColumns().addAll(dvdTitle, dvdRegisseur);

                    magazineTable.getColumns().addAll(magazineTitle, magazineEdition);


            } catch (Exception e) {
                System.out.println("HelloClient exception: " + e.getMessage());
                e.printStackTrace();
            }

        }else{
            bookTable.setPlaceholder(new Label("Eine leere Suche ergibt kein Ergebnis! Bitte geben sie einen Suchbegriff ein!"));
            dvdTable.setPlaceholder(new Label("Eine leere Suche ergibt kein Ergebnis! Bitte geben sie einen Suchbegriff ein!"));
            magazineTable.setPlaceholder(new Label("Eine leere Suche ergibt kein Ergebnis! Bitte geben sie einen Suchbegriff ein!"));
        }
    }
}

