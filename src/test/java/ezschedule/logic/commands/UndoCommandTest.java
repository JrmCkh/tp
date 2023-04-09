package ezschedule.logic.commands;

import static ezschedule.logic.commands.CommandTestUtil.assertCommandSuccess;
import static ezschedule.testutil.Assert.assertThrows;
import static ezschedule.testutil.TypicalEvents.getTypicalScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ezschedule.commons.core.index.Index;
import ezschedule.logic.commands.EditCommand.EditEventDescriptor;
import ezschedule.logic.commands.exceptions.CommandException;
import ezschedule.model.Model;
import ezschedule.model.ModelManager;
import ezschedule.model.UserPrefs;
import ezschedule.model.event.Date;
import ezschedule.model.event.Event;
import ezschedule.model.event.RecurFactor;
import ezschedule.testutil.EventBuilder;

public class UndoCommandTest {
    private List<String> recurFactorList = new ArrayList<>(Arrays.asList("day", "week", "month"));
    private Model model = new ModelManager(getTypicalScheduler(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalScheduler(), new UserPrefs());
    @Test
    public void execute_undoAddCommand_successful() throws CommandException {
        Event validEvent = new EventBuilder().build();
        Command prevCommand = new AddCommand(validEvent);
        model.addEvent(validEvent);
        model.addRecentEvent(validEvent);
        model.addRecentCommand(prevCommand);
        assertCommandSuccess(new UndoCommand(), model, String.format(UndoCommand.MESSAGE_UNDONE_SUCCESS,
                prevCommand.commandWord()), expectedModel);
    }
    @Test
    public void execute_undoEditCommand_successful() {
        List<Event> lastShownList = model.getFilteredEventList();
        Index targetIndex = Index.initIndex(new Random().nextInt(lastShownList.size()));
        Command editCommand = new EditCommand(targetIndex, new EditEventDescriptor());
        Event eventToEdit = lastShownList.get(targetIndex.getZeroBased());
        Event editedEvent = new EventBuilder(EventBuilder.EDIT_EVENT_NAME, EventBuilder.EDIT_EVENT_DATE,
                EventBuilder.EDIT_EVENT_START_TIME, EventBuilder.EDIT_EVENT_END_TIME).build();
        model.addRecentCommand(editCommand);
        model.addRecentEvent(eventToEdit);
        model.addRecentEvent(editedEvent);
        model.setEvent(eventToEdit, editedEvent);
        assertCommandSuccess(new UndoCommand(), model, String.format(UndoCommand.MESSAGE_UNDONE_SUCCESS,
                editCommand.commandWord()), expectedModel);
    }
    @Test
    public void execute_undoDeleteCommand_successful() throws CommandException {
        List<Event> lastShownList = model.getFilteredEventList();
        Index targetIndex = Index.initIndex(new Random().nextInt(lastShownList.size()));
        List<Index> targetIndexes = new ArrayList<>();
        targetIndexes.add(targetIndex);
        Event eventToDelete = lastShownList.get(targetIndex.getZeroBased());
        Command deleteCommand = new DeleteCommand(targetIndexes);
        model.deleteEvent(eventToDelete);
        model.addRecentEvent(eventToDelete);
        model.addRecentCommand(deleteCommand);
        assertCommandSuccess(new UndoCommand(), model, String.format(UndoCommand.MESSAGE_UNDONE_SUCCESS,
                        deleteCommand.commandWord()),
                expectedModel);
    }
    @Test
    public void execute_undoRecurCommand_successful() throws CommandException {
        List<Event> lastShownList = model.getFilteredEventList();
        Index targetIndex = Index.initIndex(new Random().nextInt(lastShownList.size()));
        Date endDate = new Date("2023-05-30");
        String stringRecurFactor = recurFactorList.get(new Random().nextInt(recurFactorList.size()));
        RecurFactor recurFactor = new RecurFactor(stringRecurFactor);
        Command recurCommand = new RecurCommand(targetIndex, endDate, recurFactor);
        Event eventToRecur = lastShownList.get(targetIndex.getZeroBased());
        model.addRecentCommand(recurCommand);
        switch(stringRecurFactor) {
        case "day": ((RecurCommand) recurCommand).addEventPerDay(model, eventToRecur);
            break;
        case "week": ((RecurCommand) recurCommand).addEventPerWeek(model, eventToRecur);
            break;
        case "month": ((RecurCommand) recurCommand).addEventPerMonth(model, eventToRecur);
            break;
        default:
            break;
        }
        assertCommandSuccess(new UndoCommand(), model, String.format(UndoCommand.MESSAGE_UNDONE_SUCCESS,
                        recurCommand.commandWord()),
                expectedModel);
    }
    @Test
    public void execute_multipleUndoCommandConsecutively_failure() throws CommandException {
        Event validEvent = new EventBuilder().build();
        Command prevCommand = new AddCommand(validEvent);
        model.addEvent(validEvent);
        model.addRecentEvent(validEvent);
        model.addRecentCommand(prevCommand);
        Command undoCommand = new UndoCommand();
        CommandResult commandResult = undoCommand.execute(model);
        assertThrows(CommandException.class, UndoCommand.MESSAGE_UNDO_ERROR, () -> undoCommand.execute(model));
    }
    @Test
    public void execute_undoCommandAtStart_failure() {
        assertThrows(CommandException.class, UndoCommand.MESSAGE_UNDO_ERROR, () -> new UndoCommand().execute(model));
    }
    @Test
    public void execute_undoCommandAfterInvalidCommands_successful() {
        Event validEvent = new EventBuilder().build();
        Command prevCommand = new AddCommand(validEvent);
        model.addEvent(validEvent);
        model.addRecentCommand(prevCommand);
        model.addRecentEvent(validEvent);
        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_EVENT, () -> prevCommand.execute(model));
        Command undoCommand = new UndoCommand();
        assertCommandSuccess(undoCommand, model, String.format(UndoCommand.MESSAGE_UNDONE_SUCCESS,
                prevCommand.commandWord()), expectedModel);
    }
}