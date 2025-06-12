package View;

import Model.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableBuilder {
    public static TableView<Event> createResultTable() {
        TableView<Event> table = new TableView<>();
        table.setEditable(false); // users can't edit any cells
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // fills the available width
        table.setMaxWidth(Double.MAX_VALUE); // lets it stretch fully

        TableColumn<Event, String> nameCol = new TableColumn<>("Filename");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        nameCol.setCellFactory(col -> new TableCell<Event, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });
        nameCol.setPrefWidth(100);
        nameCol.setResizable(false);

        TableColumn<Event, String> typeCol = new TableColumn<>("Event Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        typeCol.setCellFactory(col -> new TableCell<Event, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });
        typeCol.setPrefWidth(100);
        typeCol.setResizable(false);

        TableColumn<Event, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setCellFactory(col -> new TableCell<Event, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });
        timeCol.setPrefWidth(100);
        timeCol.setResizable(false);

        TableColumn<Event, String> extCol = new TableColumn<>("Extension");
        extCol.setCellValueFactory(new PropertyValueFactory<>("extension"));
        extCol.setCellFactory(col -> new TableCell<Event, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });
        timeCol.setPrefWidth(100);
        extCol.setResizable(false);

        TableColumn<Event, String> dirCol = new TableColumn<>("Directory");
        dirCol.setCellValueFactory(new PropertyValueFactory<>("directory"));
        dirCol.setCellFactory(col -> new TableCell<Event, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });
        dirCol.setPrefWidth(100);
        dirCol.setResizable(false);

        // Add all columns to the table
        table.getColumns().addAll(nameCol, typeCol, timeCol, extCol, dirCol);

        // don't let users drag column headers around
        table.getColumns().forEach(col -> col.setReorderable(false));


        //table.setPrefHeight(300);
        return table;
    }
}
