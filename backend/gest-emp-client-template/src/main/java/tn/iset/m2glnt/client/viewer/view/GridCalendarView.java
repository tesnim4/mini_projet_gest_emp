package tn.iset.m2glnt.client.viewer.view;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GridCalendarView extends GridPane implements CalendarView{
    private final List<Label> firstRowLabels = new ArrayList<>();
    private final List<Label> firstColumnLabels = new ArrayList<>();
    private final Map<Integer,SlotView> slotViewMap = new HashMap<>();
    private Node buttonBox;
    private Scene scene;

    @Override
    public void removeSlot(int slotId) {
        SlotView eventView = slotViewMap.remove(slotId);
        getChildren().remove(eventView);
    }

    @Override
    public Scene constructScene() {
        if(scene == null) {
            scene = new Scene(this);
        }
        return scene;
    }

    @Override
    public void clearViewSlots() {
        getChildren().removeAll(slotViewMap.values());
        slotViewMap.clear();
    }

    @Override
    public void addSlotView(SlotView slotView, int rowIndex, int columnIndex,
                            int rowSpan, int colSpan) {
        slotViewMap.put(slotView.getSlotId(), slotView);
        add(slotView, rowIndex, columnIndex, rowSpan, colSpan);
    }

    @Override
    public void addLabelInFirstRow(Label label, int columnIndex) {
        add(label, columnIndex, 0);
        firstRowLabels.add(label);
    }

    @Override
    public void addLabelInFirstColumn(Label label, int rowIndex) {
        add(label, 0, rowIndex);
        firstColumnLabels.add(label);
    }

    @Override
    public void clearLabelsInFirstRow() {
        getChildren().removeAll(firstRowLabels);
        firstRowLabels.clear();
    }

    @Override
    public void clearLabelsInFirstColumn() {
        getChildren().removeAll(firstColumnLabels);
        firstColumnLabels.clear();
    }

    @Override
    public void addButtonBoxInTopLeftCell(HBox buttonBox) {
        add(buttonBox,0,0);
        this.buttonBox = buttonBox;
    }

    @Override
    public void clearButtonBoxInFirstCell() {
        if(buttonBox != null) {
            getChildren().remove(buttonBox);
            buttonBox = null;
        }
    }
}
