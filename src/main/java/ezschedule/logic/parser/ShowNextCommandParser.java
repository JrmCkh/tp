package ezschedule.logic.parser;

import ezschedule.commons.core.Messages;
import ezschedule.logic.commands.ShowNextCommand;
import ezschedule.logic.parser.exceptions.ParseException;
import ezschedule.model.event.UpcomingEventPredicate;

/**
 * Parses input arguments and creates a new ShowNextCommandParser object
 */
public class ShowNextCommandParser implements Parser<ShowNextCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ShowNextCommand
     * and returns an ShowNextCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public ShowNextCommand parse(String userInput) throws ParseException {
        // No argument provided, just show the next one
        if (userInput.isBlank()) {
            return new ShowNextCommand(new UpcomingEventPredicate(1));
        }

        try {
            int count = Integer.parseInt(userInput.trim());
            if (count > 0) {
                return new ShowNextCommand(new UpcomingEventPredicate(count));
            }
        } catch (NumberFormatException e) {
            // Empty here; another exception is throw outside.
        }

        throw new ParseException(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ShowNextCommand.MESSAGE_USAGE));
    }
}