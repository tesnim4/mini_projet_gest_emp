package tn.iset.m2glnt.client.viewer.presenter.dialog;

import javafx.scene.control.*;
import tn.iset.m2glnt.client.model.Slot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SimpleSlotFormFactory implements SlotFormFactory {
    private final SlotFormBuilder slotFormBuilder = new SlotFormBuilder();
    private final List<LocalTime> possibleTimes;
    private final List<Duration> possibleDurations;

    public SimpleSlotFormFactory(List<LocalTime> possibleTimes, List<Duration> possibleDurations) {
        this.possibleTimes = possibleTimes;
        this.possibleDurations = possibleDurations;
    }

    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(Slot slotInfo) {
        int indexOfSlotTime = possibleTimes.indexOf(slotInfo.startDateTime().toLocalTime());
        if (indexOfSlotTime == -1) {
            throw new IllegalArgumentException("Slot is outside range");
        }
        int indexOfSlotDuration = possibleDurations.indexOf(slotInfo.duration());

        return slotFormBuilder.reset()
                .buildTitle("Édition de créneau")
                .buildHeader("Édition d'un créneau du calendrier")
                .buildTextFieldWithDefaultText("Description :", slotInfo.description())
                .buildDatePicker("Date :", slotInfo.startDateTime().toLocalDate())
                .buildTimePicker("Heure :", possibleTimes, indexOfSlotTime)
                .buildDurationPicker("Durée :", possibleDurations, indexOfSlotDuration)
                .buildCancelButton()
                .buildDeleteButton("Supprimer créneau")
                .buildConfirmButton("Modifier créneau")
                .getDialog();
    }


    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(LocalDateTime defaultLocalDateTime,
                                                            int defaultPossibleDurationIndex) {
        int defaultTimeIndex = possibleTimes.indexOf(defaultLocalDateTime.toLocalTime());
        if(defaultTimeIndex == -1) {
            defaultTimeIndex = 0;
        }
        return slotFormBuilder.reset()
                .buildTitle("Création de créneau")
                .buildHeader("Création d'un créneau du calendrier")
                .buildTextFieldWithPrompt("Description :", "description du créneau")
                .buildDatePicker("Date :", defaultLocalDateTime.toLocalDate())
                .buildTimePicker("Heure :", possibleTimes, defaultTimeIndex)
                .buildDurationPicker("Durée :", possibleDurations, defaultPossibleDurationIndex)
                .buildCancelButton()
                .buildConfirmButton("Créer créneau")
                .getDialog();
    }
}
