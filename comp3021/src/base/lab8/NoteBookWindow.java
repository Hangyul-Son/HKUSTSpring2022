package lab8;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {

	/**
	 * TextArea containing the note
	 */
	TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	ListView<String> titleslistView = new ListView<String>();
	/**
	 * 
	 * Combobox for selecting the folder
	 * 
	 */
	ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current search string
	 */
	String currentSearch = "";

	/**
	 * current note selected by the user
	 */
	String currentNote = "";
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		loadNoteBook();
		this.stage = stage;
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addNoteVBox());
		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}
	private VBox addNoteVBox() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes
		vbox.getChildren().add(addNoteButtonsHBox());
		vbox.getChildren().add(textAreaNote);
		return vbox;
	}
	private HBox addNoteButtonsHBox() {
		HBox hBox = new HBox();
		hBox.setSpacing(10); // Gap between nodes
		ImageView saveView = new ImageView(new Image(new File("save.png").toURI().toString()));
		saveView.setFitHeight(18);
		saveView.setFitWidth(18);
		saveView.setPreserveRatio(true);

		Button buttonSaveNote = new Button();
		buttonSaveNote.setText("Save Note");
		buttonSaveNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if(currentFolder.isEmpty() || currentNote.isEmpty()){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Warning");
					alert.setContentText("Please select a folder and a note");
					alert.showAndWait();
				}
				else {
					for(Folder folder: noteBook.getFolders()) {
						if(folder.getName().equals(currentFolder)){
							for(Note note: folder.getNotes()){
								if(note.getTitle().equals(currentNote)){
									((TextNote) note).content = textAreaNote.getText();
									break;
								}
							}
						}
					}
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Saved Note");
					alert.setContentText(currentNote + " content saved!");
					alert.showAndWait();
				}
			}
		});

		ImageView saveView1 = new ImageView(new Image(new File("save.png").toURI().toString()));
		saveView1.setFitHeight(18);
		saveView1.setFitWidth(18);
		saveView1.setPreserveRatio(true);

		Button buttonDeleteNote = new Button();
		buttonDeleteNote.setText("Delete Note");
		buttonDeleteNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if(currentFolder.isEmpty() || currentNote.isEmpty()){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Warning");
					alert.setContentText("Please select a folder and a note");
					alert.showAndWait();
				}
				else {
					for(Folder folder: noteBook.getFolders()) {
						if(folder.getName().equals(currentFolder)){
							for(Note note: folder.getNotes()){
								if(note.getTitle().equals(currentNote)){
									folder.removeNotes(currentNote);
									Alert alert = new Alert(Alert.AlertType.INFORMATION);
									alert.setTitle("Succeed!");
									alert.setContentText("Your note has been successfully removed!");
									alert.showAndWait();

									//UpdateListView
									ArrayList<String> list = new ArrayList<String>();
									ArrayList<Note> notes = folder.getNotes();
									for(Note note1: notes){
										list.add(note1.getTitle());
									}
									updateListView(list);
									break;
								}
							}
						}
					}
				}
			}
		});

		hBox.getChildren().addAll(saveView, buttonSaveNote, saveView1, buttonDeleteNote);
		return hBox;
	}
	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load from File");
		buttonLoad.setPrefSize(100, 20);
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please choose a file that contains the Notebook object");
				FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Serialzed object file (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extensionFilter);

				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					loadNoteBook(file);
					titleslistView = new ListView<>();
					foldersComboBox = new ComboBox<>();
					textAreaNote = new TextArea("");
					currentNote = "";
					currentFolder = "";
					currentSearch = "";
					BorderPane border = new BorderPane();
					// add top, left and center
					border.setTop(addHBox());
					border.setLeft(addVBox());
					border.setCenter(addNoteVBox());
					Scene scene = new Scene(border);
					stage.setScene(scene);
					stage.setTitle("NoteBook COMP 3021");
					stage.show();

					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Successfully loaded");
					alert.setContentText("You have successfully loaded " + file.getName());
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});

				}
				else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Failed to Load");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}
		});

		Button buttonSave = new Button("Save to File");
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please choose a file to contain the Notebook object");
				FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Serialzed object file (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extensionFilter);

				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					noteBook.save(file.toString());
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Successfully saved");
					alert.setContentText("You file has been saved to file note.ser");
					alert.showAndWait().ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}
		});
		buttonSave.setPrefSize(100, 20);

		Label label = new Label("Search : ");
		TextField textField = new TextField();

		Button buttonSearch = new Button("Search");
		buttonSearch.setPrefSize(100, 20);
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				currentSearch = textField.getText();
				ArrayList<Folder> folders = noteBook.getFolders();
				for (Folder folder:folders) {
					if(folder.getName().equals(currentFolder)) {
						ArrayList<Note> notes = folder.searchNotes(currentSearch);
						ArrayList<String> list = new ArrayList<>();
						for(Note note: notes){
							list.add(note.getTitle());
						}
						updateListView(list);
						break;
					}
				}
			}
		});

		Button buttonClearSearch = new Button("Clear Search");
		buttonClearSearch.setPrefSize(130, 20);
		buttonClearSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				// this contains the name of the folder selected
				ArrayList<String> list = new ArrayList<String>();

				ArrayList<Folder> folders = noteBook.getFolders();
				for(Folder folder: folders){
					if(folder.getName().equals(currentFolder)){
						ArrayList<Note> notes = folder.getNotes();
						for(Note note: notes){
							list.add(note.getTitle());
						}
						break;
					}
				}
				updateListView(list);
			}
		});

		hbox.getChildren().addAll(buttonLoad, buttonSave, label, textField, buttonSearch, buttonClearSearch);
		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes

		// TODO: This line is a fake folder list. We should display the folders in noteBook variable! Replace this with your implementation
		ArrayList<Folder> folders = noteBook.getFolders();
		ArrayList<String> names = new ArrayList<>();
		for(Folder folder: folders) {
			names.add(folder.getName());
		}
		foldersComboBox.getItems().addAll(names);

		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				currentFolder = t1.toString();
				// this contains the name of the folder selected
				// TODO update listview
				ArrayList<String> list = new ArrayList<String>();

				// TODO populate the list object with all the TextNote titles of the currentFolder
				ArrayList<Folder> folders = noteBook.getFolders();
				for(Folder folder: folders){
					if(folder.getName().equals(currentFolder)){
						ArrayList<Note> notes = folder.getNotes();
						for(Note note: notes){
							if(note instanceof TextNote){
								list.add(note.getTitle());
							}
						}
						break;
					}
				}
				updateListView(list);
			}
		});

		foldersComboBox.setValue("-----");

		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				String content = "";
				// This is the selected title
				// TODO load the content of the selected note in textAreNote
				ArrayList<Folder> folders = noteBook.getFolders();
				for(Folder folder: folders){
					if(folder.getName().equals(currentFolder)){
						ArrayList<Note> notes = folder.getNotes();
						for(Note note: notes){
							if(note.getTitle().equals(title)){
								content = ((TextNote) note).content;
							}
						}
					}
				}
				textAreaNote.setText(content);
				currentNote = title;
			}
		});
		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(addChooseFolderHbox());
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		Button buttonAddNote = new Button();
		buttonAddNote.setText("Add a Note");
		buttonAddNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if(currentFolder.isEmpty()){
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Warning");
					alert.setContentText("Please choose folder first!");
					alert.showAndWait();
				}
				else {
					TextInputDialog dialog = new TextInputDialog("Add a Note");
					dialog.setTitle("Input");
					dialog.setHeaderText("Add a new note to current folder");
					dialog.setContentText("Please enter the name your note: ");
					Optional<String> result = dialog.showAndWait();
					if(result.isPresent()){
						if(result.get().isEmpty() || titleslistView.getItems().contains(result.get())){
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setTitle("Failed");
							alert.setContentText("Please enter a valid note name");
							alert.showAndWait();
						}
						else {
							noteBook.createTextNote(currentFolder, result.get());
							Alert alert = new Alert(Alert.AlertType.INFORMATION);
							alert.setTitle("Successful!");
							alert.setContentText("Insert note " + result.get() + " to folder " + currentFolder +" successfully!");
							alert.showAndWait();

							ArrayList<String> list = new ArrayList<String>();
							ArrayList<Folder> folders = noteBook.getFolders();
							for(Folder folder: folders){
								if(folder.getName().equals(currentFolder)){
									ArrayList<Note> notes = folder.getNotes();
									for(Note note: notes){
										list.add(note.getTitle());
									}
									break;
								}
							}
							updateListView(list);
						}
					}
				}
			}
		});
		vbox.getChildren().add(buttonAddNote);

		return vbox;
	}

	private HBox addChooseFolderHbox() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes
		Button buttonAddFolder = new Button();
		buttonAddFolder.setText("Add a Folder");
		buttonAddFolder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				TextInputDialog dialog = new TextInputDialog("Add a Folder");
				dialog.setTitle("Input");
				dialog.setHeaderText("Add a new folder for your notebook:");
				dialog.setContentText("Please enter the name you want to create:");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					// TODO
					if(result.get().isEmpty()) {
						Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please input a valid folder name");
						alert.showAndWait();
					}
					else if(noteBook.getFolders().stream().anyMatch(name -> name.getName().equals(result.get()))) {
						Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("You already have a folder named " + result.get());
						alert.showAndWait();
					}
					else {
						noteBook.addFolder(result.get());
						foldersComboBox.getItems().add(result.get());
						foldersComboBox.getSelectionModel().select(result.get());
						currentFolder = result.get();
					}
				}
			}
		});
		hbox.getChildren().addAll(foldersComboBox, buttonAddFolder);
		return hbox;
	}
	private void updateListView(ArrayList<String> list) {
		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
		currentNote = "";
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */

	private GridPane addGridPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 0);

		return grid;
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called “the most shocking play in NFL history” and the Washington Redskins dubbed the “Throwback Special”: the November 1985 play in which the Redskins’ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Award–winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything—until it wasn’t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant—a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether’s Daddy Was a Number Runner and Dorothy Allison’s Bastard Out of Carolina, Jacqueline Woodson’s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood—the promise and peril of growing up—and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;
	}
	private void loadNoteBook(File file){
		NoteBook nb = new NoteBook(file.getPath());
		noteBook = nb;
	}
}
