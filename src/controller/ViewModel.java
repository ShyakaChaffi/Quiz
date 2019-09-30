package controller;

import java.io.FileNotFoundException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Match;
import model.Player;
import model.Question;
import model.Tournament;
import model.TournamentFacade;
import model.RESULTS;
import view.PopUpDelete;
import view.ViewInitGame;

/**
 *
 * @author Spy
 */
public class ViewModel {

    TournamentFacade facade;
    private ObservableList<Player> subscribeList;
    private ObservableList<Match> matchList;
    private ObservableList<Player> oppList = FXCollections.observableArrayList();
    private IntegerProperty indexTournament = new SimpleIntegerProperty();
    private final ObjectProperty<Player> actualPlayer = new SimpleObjectProperty<Player>();
    private final ObjectProperty<Player> cb1 = new SimpleObjectProperty<>();
    private final ObjectProperty<Player> cb2 = new SimpleObjectProperty<>();
    private StringProperty cb3 = new SimpleStringProperty();
    private IntegerProperty indexMatch = new SimpleIntegerProperty();
    private ObjectProperty<Match> matchSelected = new SimpleObjectProperty<>();
    private ObservableList<Question> selectedQuestionList = FXCollections.observableArrayList();
    private ObjectProperty<Question> selectedQuestion = new SimpleObjectProperty<>();
    private BooleanProperty btnValidate = new SimpleBooleanProperty();
    private StringProperty questionName = new SimpleStringProperty();
    private IntegerProperty questionPoint = new SimpleIntegerProperty();
    private StringProperty res1 = new SimpleStringProperty();
    private StringProperty res2 = new SimpleStringProperty();
    private StringProperty res3 = new SimpleStringProperty();
    private StringProperty res4 = new SimpleStringProperty();
    private ObjectProperty<Question> currentQuestion = new SimpleObjectProperty<>();
    private IntegerProperty cptFillQuestions = new SimpleIntegerProperty();
    private IntegerProperty indexQuestion = new SimpleIntegerProperty();
    private BooleanProperty gameOver = new SimpleBooleanProperty();
    private final BooleanProperty bl = new SimpleBooleanProperty(true);
    private final BooleanProperty deselectedRadioButon = new SimpleBooleanProperty(true);
    private ObjectProperty<Player> clearPlayerOne = new SimpleObjectProperty<>();
    private ObjectProperty<Player> clearPlayerTwo = new SimpleObjectProperty<>();
    private ObjectProperty<String> clearResult = new SimpleObjectProperty<>();
    private ObjectProperty<FocusModel<Tournament>> selectTournament = new SimpleObjectProperty<>();
    private BooleanProperty btnPlayClicked = new SimpleBooleanProperty();

    public ViewModel(TournamentFacade facade) {
        this.facade = facade;
        bl.setValue(Boolean.TRUE);
        initList();
    }

    public void initList() {
        subscribeList = FXCollections.observableArrayList(subscribesListProperty());
        matchList = FXCollections.observableArrayList(matchsProperty());
        setFirstIndex();
        btnValidate.set(true);
        btnPlayClicked.set(true);
    }
    
    public void setFirstIndex(){
    indexTournament.set(0);
    }

    public void fillList() {
        subscribeList.clear();
        matchList.clear();
        for (Player p : subscribesListProperty()) {
            subscribeList.add(p);
        }
        for (Match m : matchsProperty()) {
            matchList.add(m);
        }

    }

    public void setAttributQuetion(Question q) {
        if (null != q) {
            this.questionName.set(q.getName().get());
            this.questionPoint.set(q.getPoints());
            setReponse(q);
            this.currentQuestion.set(q);
        }
    }

    public void setReponse(Question q) {
        res1.set(q.getResponses().get(0));
        res2.set(q.getResponses().get(1));
        res3.set(q.getResponses().get(2));
        res4.set(q.getResponses().get(3));
    }

    public void clearOppList() {
        this.oppList().clear();
    }

    public void launchPopUp(MouseEvent mouseEvent, TableView<Match> matchesList) throws FileNotFoundException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2 && matchesList.getSelectionModel().getSelectedItem() != null) {
                new PopUpDelete(matchSelected.get(), this);
                clearOppList();
                oppList();
            }
        }
        fillList();
        ClearComboBox();
    }

    public void launchGame(Player p1, Player p2) throws Exception {
        if (!cb1.get().getFirstName().equals("") && !cb2.get().getFirstName().equals("")) {
            bl.setValue(true);
            VMInitGame vm1 = new VMInitGame(this);
            new ViewInitGame(vm1, p1, p2);
        }
    }

    public void createMatch(String score) {
        if (isComboBoxesNotEmpty(score)) {
            Match m = new Match(new Player(cb1.getValue().toString()),
                      new Player(cb2.getValue().toString()),
                      results(score));
            if (!matchsProperty().contains(m)) {
                facade.getTournament().addMatch(m);
            }
        }
        fillList();
        ClearComboBox();
        btnValidate.set(true);
    }
    
    private boolean isComboBoxesNotEmpty(String score){
        return !score.equals("") 
                && !cb1.get().getFirstName().equals("") 
                && !cb2.get().getFirstName().equals("");
    }

    public void ClearComboBox() {
        clearPlayerOne.set(null);
        clearPlayerTwo.set(null);
        clearResult.set("");
        btnValidate.set(true);
    }

    public void removeMatch() {
        if (this.indexMatch.get() == 0) {
            this.getTournament().getMatchList().remove(this.matchSelected.get());
        } else {
            this.getTournament().getMatchList().remove(this.matchSelected.get());
        }
    }

    private RESULTS results(String res) {
        if (res.equals(RESULTS.VAINQUEUR_J1.name())) {
            return RESULTS.VAINQUEUR_J1;
        }
        if (res.equals(RESULTS.VAINQUEUR_J2.name())) {
            return RESULTS.VAINQUEUR_J2;
        }
        if (res.equals(RESULTS.EX_AEQUO.name())) {
            return RESULTS.EX_AEQUO;
        }
        return null;
    }

    public ObservableList<Match> addMatchPlayed() {
        ObservableList<Match> matchPlayed = FXCollections.observableArrayList();
        for (Match m : matchsProperty()) {
            if (m.getPlayer1().getFirstName().equals(actualPlayer.get().toString())
                    || m.getPlayer2().getFirstName().equals(actualPlayer.get().toString())) {
                matchPlayed.add(m);
            }
        }
        return matchPlayed;
    }

    private boolean isTheOpponent(String p) {
        return !p.equals(actualPlayer.getValue().toString());
    }

    private ObservableList<Player> addOpponentInvalidList() {
        ObservableList<Player> playerInvalid = FXCollections.observableArrayList();
        for (Match m : addMatchPlayed()) {
            if (isTheOpponent(m.getPlayer1().getFirstName())) {
                playerInvalid.add(m.getPlayer1());
            }
            if (isTheOpponent(m.getPlayer2().getFirstName())) {
                playerInvalid.add(m.getPlayer2());
            }
        }
        return playerInvalid;
    }

    public void oppValidList() {
        if (actualProperty().get() != null) {
            ObservableList<Player> list2 = addOpponentInvalidList();
            oppList.clear();
            for (Player s : subscribesListProperty()) {
                if (!list2.contains(s) && !s.getFirstName().equals(actualPlayer.getValue().toString())) {
                    this.oppList.add(s);
                }
            }
        }
        btnValidateDisable();
    }
    
    public void btnValidateDisable() {
        if (actualProperty().get() != null && cb2.get() != null && cb3.get() != null) {
            btnValidate.set(false);
        }
        btnPlayClicked.set(false);
    }
    
    public void emptyselectedList() {
        selectedQuestionList.clear();
    }
    
    public void setTournament() {
        facade.indexTournamentProperty().set(indexTournament.get());
        fillList();
        ClearComboBox();
    }
    
    public void popupCredits() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.CLOSE);
        Image image = new Image("img/rodolphe&Lindsay.png");
        
        alert.setGraphic(new ImageView(image));
        alert.showAndWait();

        if (alert.getResult() == ButtonType.CLOSE) {
            alert.close();
        }
    }

    public SimpleListProperty<Question> quetionsProperty() {
        return new SimpleListProperty<>(facade.getQuestions());
    }

    public StringProperty getRes1() {
        return res1;
    }

    public StringProperty getRes2() {
        return res2;
    }

    public StringProperty getRes3() {
        return res3;
    }

    public StringProperty getRes4() {
        return res4;
    }

    public ObjectProperty<Question> getSelectedQuestion() {
        return selectedQuestion;
    }

    public StringProperty questionNameProperty() {
        return questionName;
    }

    public IntegerProperty questionPointProperty() {
        return questionPoint;
    }

    public ObservableList<Question> selectedQuestionList() {
        return selectedQuestionList;
    }

    public ObservableList<Player> oppList() {
        return this.oppList;
    }

    public SimpleListProperty<Player> subscribesListProperty() {
        return new SimpleListProperty<>(facade.getTournamentSubsList());
    }

    public ObjectProperty<Player> combobox1Property() {
        return cb1;
    }

    public ObjectProperty<Player> combobox2Property() {
        return cb2;
    }

    public StringProperty combobox3Property() {
        return cb3;
    }

    public SimpleIntegerProperty indexMatchProperty() {
        return new SimpleIntegerProperty(indexMatch.get());
    }

    public ObjectProperty<Match> matchSelectedProperty() {
        return matchSelected;
    }

    public SimpleListProperty<Player> opponentsListProperty() {
        return new SimpleListProperty<>(this.oppList);
    }

    public SimpleListProperty<Match> matchsProperty() {
        return new SimpleListProperty<>(facade.getMatchList());
    }

    public SimpleListProperty<Tournament> tournamantProperty() {
        return new SimpleListProperty<>(facade.getTournamentList());
    }

    public TournamentFacade getFacade() {
        return facade;
    }

    public ObjectProperty<Player> actualProperty() {
        return actualPlayer;
    }

    public ObservableList<Match> getAllMatch() {
        return facade.getTournament().getMatchList();
    }

    public IntegerProperty indexTournamentProperty() {
        return this.indexTournament;
    }

    public Tournament getTournament() {
        return facade.getTournament();
    }

    public SimpleListProperty<Player> SubscribListProperty() {
        return new SimpleListProperty<>(subscribeList);

    }

    public SimpleListProperty<Match> matchListProperty() {
        return new SimpleListProperty<>(matchList);
    }

    public ObservableList<Player> getSubscribeList() {
        return subscribeList;
    }

    public ObservableList<Match> getMatchList() {
        return matchList;
    }

    public ObservableList<Player> getOppList() {
        return oppList;
    }

    public IntegerProperty getIndexTournament() {
        return indexTournament;
    }

    public ObjectProperty<Player> getActualPlayer() {
        return actualPlayer;
    }

    public ObjectProperty<Player> getCb1() {
        return cb1;
    }

    public ObjectProperty<Player> getCb2() {
        return cb2;
    }

    public StringProperty getCb3() {
        return cb3;
    }

    public IntegerProperty getIndexMatch() {
        return indexMatch;
    }

    public ObjectProperty<Match> getMatchSelected() {
        return matchSelected;
    }

    public ObservableList<Question> getSelectedQuestionList() {
        return selectedQuestionList;
    }

    public BooleanProperty getBtnValidate() {
        return btnValidate;
    }

    public StringProperty getQuestionName() {
        return questionName;
    }

    public IntegerProperty getQuestionPoint() {
        return questionPoint;
    }

    public ObjectProperty<Question> getCurrentQuestion() {
        return currentQuestion;
    }

    public IntegerProperty getCptFillQuestions() {
        return cptFillQuestions;
    }

    public IntegerProperty getIndexQuestion() {
        return indexQuestion;
    }

    public BooleanProperty getGameOver() {
        return gameOver;
    }

    public BooleanProperty getBl() {
        return bl;
    }

    public BooleanProperty getDeselectedRadioButon() {
        return deselectedRadioButon;
    }

    public ObjectProperty<Player> getClearPlayerOne() {
        return clearPlayerOne;
    }

    public ObjectProperty<Player> getClearPlayerTwo() {
        return clearPlayerTwo;
    }

    public ObjectProperty<String> getClearResult() {
        return clearResult;
    }

    public ObjectProperty<FocusModel<Tournament>> getSelectTournament() {
        return selectTournament;
    }

    public BooleanProperty getBtnPlayClicked() {
        return btnPlayClicked;
    }

    void setBtnPlayClicked(Boolean b){
        btnPlayClicked.set(b);
    }
    
    
}
