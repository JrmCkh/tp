package ezschedule.logic.commands;

import static java.util.Objects.requireNonNull;

import ezschedule.logic.commands.exceptions.CommandException;
import ezschedule.logic.parser.CliSyntax;
import ezschedule.model.Model;
import ezschedule.model.event.Event;

/**
 * Adds a event to the scheduler.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an event to the scheduler."
        + "\nParameters: "
        + CliSyntax.PREFIX_NAME + "NAME "
        + CliSyntax.PREFIX_DATE + "DATE "
        + CliSyntax.PREFIX_START + "START TIME "
        + CliSyntax.PREFIX_END + "END TIME "
        + "\nExample: " + COMMAND_WORD + " "
        + CliSyntax.PREFIX_NAME + "Tennis "
        + CliSyntax.PREFIX_DATE + "2023-12-20 "
        + CliSyntax.PREFIX_START + "18:00 "
        + CliSyntax.PREFIX_END + "20:00";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in the scheduler";

    private final Event toAdd;

    /**
     * Creates an AddEventCommand to add the specified {@code Event}
     */
    public AddCommand(Event event) {
        requireNonNull(event);
        toAdd = event;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasEvent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        }

        model.addEvent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof AddCommand // instanceof handles nulls
            && toAdd.equals(((AddCommand) other).toAdd));
    }
}
