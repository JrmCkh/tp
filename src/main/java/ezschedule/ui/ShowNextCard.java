package ezschedule.ui;

import ezschedule.model.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * A UI component that displays information of a {@code Event}.
 */
public class ShowNextCard extends UiPart<Region> {

    private static final String FXML = "ShowNextCard.fxml";

    public final Event event;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label completed;
    @FXML
    private Label id;
    @FXML
    private Label date;
    @FXML
    private Label startTime;
    @FXML
    private Label endTime;

    /**
     * Creates a {@code EventCode} with the given {@code Event} and index to display.
     */
    public ShowNextCard(Event event, int displayedIndex) {
        super(FXML);
        this.event = event;

        id.setText(displayedIndex + ". ");
        name.setText(event.getName().fullName);
        completed.setText(event.getCompletedStatus());
        date.setText("Date: " + event.getDate().toString());
        startTime.setText("Start Time: " + event.getStartTime().toString());
        endTime.setText("End Time: " + event.getEndTime().toString());
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ShowNextCard)) {
            return false;
        }

        // state check
        ShowNextCard card = (ShowNextCard) other;
        return id.getText().equals(card.id.getText())
                && event.equals(card.event);
    }
}