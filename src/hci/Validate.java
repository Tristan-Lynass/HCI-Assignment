package hci;

public class Validate
{
    public static boolean validateDimension(String input)
    {
        boolean valid;
        if (input.matches("[0-9]+"))
        {
            try
            {
                int inputNum = Integer.parseInt(input);

                if (inputNum >= 0)
                {
                    valid = true;
                }
                else valid = false;

            }
            catch (NumberFormatException ex) { valid = false; }
        }
        else valid = false;

        return valid;
    }
}
