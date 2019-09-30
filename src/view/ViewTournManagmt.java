package view;

import controller.ViewModel;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.*;

public class ViewTournManagmt extends VBox {

    private final Stage stage;
    private final ViewModel vm;
    private static final int TEXTSIZE = 400, SPACING = 10;
    private final ListView<Player> subsList = new ListView<>();
    private final TableView<Match> matchesList = new TableView<>();
    private final ListView<Tournament> tournamentsList = new ListView<>();
    private final HBox displayZone = new HBox();
    private final GridPane left = new GridPane();
    private final GridPane right = new GridPane();
    private final ComboBox<Player> cbPlayersList = new ComboBox<>();
    private final ComboBox<Player> cbOpponentsList = new ComboBox<>();
    private final ComboBox<String> cbResultsList = new ComboBox<>();
    private final Button btnValidate = new Button();
    private final Button btnClear = new Button();
    private final Button btnPlay = new Button();
    private final Button btnCredits = new Button();
    private final GridPane gpButtons = new GridPane();//gere les boutons

    public ViewTournManagmt(Stage primaryStage, ViewModel ctrl) throws FileNotFoundException {
        this.vm = ctrl;
        this.stage = primaryStage;
        initData();
        configBindings();
        tournamentsList.focusedProperty();
        Scene scene = new Scene(displayZone, 1145, 500);
        //stage.setResizable(false);
        //stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Gestion de  Tournois");
        stage.setScene(scene);
    }

    private void addResultsToCB() {
        cbResultsList.getItems().addAll(
                "EX_AEQUO",
                "VAINQUEUR_J1",
                "VAINQUEUR_J2"
        );
    }

    private void configBindings() {
        configBindingsView();
        configBindingAttributes();

    }

    private void configBindingsView() {
        subsList.itemsProperty().bindBidirectional(vm.SubscribListProperty());
        matchesList.itemsProperty().bindBidirectional(vm.matchListProperty());
        tournamentsList.itemsProperty().bind(vm.tournamantProperty());
        tournamentsList.getSelectionModel().selectFirst();
        cbPlayersList.itemsProperty().bindBidirectional(vm.SubscribListProperty());
        cbOpponentsList.itemsProperty().bindBidirectional(vm.opponentsListProperty());
        cbPlayersList.valueProperty().bindBidirectional(vm.getClearPlayerOne());
        cbOpponentsList.valueProperty().bindBidirectional(vm.getClearPlayerTwo());
        cbResultsList.valueProperty().bindBidirectional(vm.getClearResult());
        btnValidate.disableProperty().bindBidirectional(vm.getBtnValidate());
        btnPlay.disableProperty().bindBidirectional(vm.getBtnPlayClicked());
    }

    private void configBindingAttributes() {
        vm.indexTournamentProperty().bind(tournamentsList.getSelectionModel().selectedIndexProperty());
        vm.actualProperty().bind(cbPlayersList.getSelectionModel().selectedItemProperty());
        vm.combobox1Property().bind(cbPlayersList.getSelectionModel().selectedItemProperty());
        vm.combobox2Property().bind(cbOpponentsList.getSelectionModel().selectedItemProperty());
        vm.combobox3Property().bind(cbResultsList.getSelectionModel().selectedItemProperty());
        vm.indexMatchProperty().bind(matchesList.getSelectionModel().selectedIndexProperty());
        vm.matchSelectedProperty().bind(matchesList.getSelectionModel().selectedItemProperty());
    }

    private void tableViewColumnConfig() {
        TableColumn<Match, String> player1 = new TableColumn<>("Joueur 1");
        player1.setMinWidth(133);
        player1.setCellValueFactory(new PropertyValueFactory<>("player1"));

        TableColumn<Match, String> player2 = new TableColumn<>("Joueur 2");
        player2.setMinWidth(133);
        player2.setCellValueFactory(new PropertyValueFactory<>("player2"));

        TableColumn<Match, RESULTS> results = new TableColumn<>("Resultats");
        results.setMinWidth(133);
        results.setCellValueFactory(new PropertyValueFactory<>("results"));

        addToTableView(player1, player2, results);
    }

    private void addToTableView(TableColumn<Match, String> p1, TableColumn<Match, String> p2, TableColumn<Match, RESULTS> res) {
        matchesList.getColumns().add(p1);
        matchesList.getColumns().add(p2);
        matchesList.getColumns().add(res);
    }

    private void configFocusListener() throws FileNotFoundException {
        tournamentsList.getSelectionModel().selectedIndexProperty()
                .addListener((Observable o) -> {
                    vm.setTournament();
                });
        matchesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    vm.launchPopUp(mouseEvent, matchesList);
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    // ajoute un listener sur les combobox.
    public void addListernerComboBox() {
        cbPlayersList.getSelectionModel().selectedIndexProperty()
                .addListener((Observable o) -> {
                    vm.oppValidList();
                });
        cbResultsList.getSelectionModel().selectedIndexProperty()
                .addListener((Observable o) -> {
                    vm.btnValidateDisable();

                });
        btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    vm.launchGame(getSelectedItem(cbPlayersList), getSelectedItem(cbOpponentsList));
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        });
        btnValidate.setOnAction((ActionEvent event) -> {
            vm.createMatch(cbResultsList.getSelectionModel().getSelectedItem());
        });
        btnClear.setOnAction((ActionEvent event) -> {
            vm.ClearComboBox();
        });
        btnCredits.setOnAction((ActionEvent event) -> {
            vm.popupCredits();
        });
    }

    private Player getSelectedItem(ComboBox<Player> cb) {
        return cb.getSelectionModel().getSelectedItem();
    }

    private void initData() throws FileNotFoundException {
        configDisplay();
        configBottomZone();
        decor();
        tableViewColumnConfig();
        configFocusListener();
        addListernerComboBox();
        addResultsToCB();
    }

    private void decor() {
        tournamentsList.setMinHeight(50);
        subsList.getSelectionModel().select(-1);
        tournamentsList.setPrefWidth(TEXTSIZE);
        matchesList.setPrefWidth(TEXTSIZE);
    }

    private void configDisplay() {
        displayZone.setPadding(new Insets(SPACING));
        left.setPadding(new Insets(10, 5, 10, 10));
        right.setPadding(new Insets(10, 10, 10, 5));
        right.setHgap(10);
        left.add(new Label("les tournois"), 0, 0);
        left.add(new Label("les inscrits"), 0, 2);
        right.add(new Label("les matchs  (Double clic pour supprimer un tournois)"), 0, 0);
        left.add(tournamentsList, 0, 1);
        left.add(subsList, 0, 3);
        right.add(matchesList, 0, 1);
        right.add(gpButtons, 0, 2);
        displayZone.getChildren().addAll(left, right);
    }

    private void configBottomZone() {
        gpButtons.setVgap(4);
        gpButtons.setHgap(30);
        gpButtons.setPadding(new Insets(20, 0, 0, 20));
        gpButtons.add(new Label("Joueur 1: "), 0, 0);
        gpButtons.add(cbPlayersList, 0, 1);
        gpButtons.add(new Label("Joueur 2: "), 1, 0);
        gpButtons.add(cbOpponentsList, 1, 1);
        gpButtons.add(new Label("Resultat "), 2, 0);
        gpButtons.add(cbResultsList, 2, 1);
        btnValidate.setText("valider");
        btnClear.setText("annuler");
        btnPlay.setText("jouer");
        btnCredits.setText("crédits");
        gpButtons.add(btnPlay, 5, 1);
        gpButtons.add(btnValidate, 6, 1);
        gpButtons.add(btnClear, 7, 1);
        gpButtons.add(btnCredits, 7, 0);
    }
}
