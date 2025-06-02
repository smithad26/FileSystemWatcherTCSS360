package View;

import Model.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableBuilder {
    public static TableView<Event> createResultTable() {
        TableView<Event> table = new TableView<>();
        table.setEditable(false); // users can't edit any cells
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // fills the available width
        table.setMaxWidth(Double.MAX_VALUE); // lets it stretch fully

        //Define all 5 columns
//        TableColumn<Event, Integer> idCol = new TableColumn<>("ID");
//        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
//        //idCol.setPrefWidth(50);
//        idCol.setResizable(false);

        TableColumn<Event, String> nameCol = new TableColumn<>("Filename");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        //nameCol.setPrefWidth(150);
        nameCol.setResizable(false);

        TableColumn<Event, String> typeCol = new TableColumn<>("Event Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        //typeCol.setPrefWidth(80);
        typeCol.setResizable(false);

        TableColumn<Event, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        //timeCol.setPrefWidth(100);
        timeCol.setResizable(false);

        TableColumn<Event, String> extCol = new TableColumn<>("Extension");
        extCol.setCellValueFactory(new PropertyValueFactory<>("extension"));
        //timeCol.setPrefWidth(100);
        extCol.setResizable(false);

        TableColumn<Event, String> dirCol = new TableColumn<>("Directory");
        dirCol.setCellValueFactory(new PropertyValueFactory<>("directory"));
        //dirCol.setPrefWidth(240);
        dirCol.setResizable(false);

        // Add all columns to the table
        table.getColumns().addAll(nameCol, typeCol, timeCol, extCol, dirCol);

        // don't let users drag column headers around
        table.getColumns().forEach(col -> col.setReorderable(false));


        //table.setPrefHeight(300);
        return table;
    }
}
