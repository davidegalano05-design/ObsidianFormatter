import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObsidianFormatterTest {

    // Metodo di utilità per invocare il metodo privato formatLine via Reflection
    private String invokeFormatLine(String input) throws Exception {
        Method method = ObsidianFormatter.class.getDeclaredMethod("formatLine", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, input);
    }

    @Test
    public void testFormatTitle() throws Exception {
        String input = "Analisi Matematica 2";
        String expected = "# Analisi Matematica 2";
        assertEquals(expected, invokeFormatLine(input));
    }

    @Test
    public void testFormatSubtitle() throws Exception {
        String input = "Studio dei limiti e integrali definiti";
        String expected = "### Studio dei limiti e integrali definiti";
        assertEquals(expected, invokeFormatLine(input));
    }

    @Test
    public void testFormatMathEquationWithText() throws Exception {
        String input = "Data la funzione f(x) = sin(x)/x";
        String expected = "$$ \\text{Data la funzione} \\ f(x) = \\sin\\frac{x}{x} $$";
        assertEquals(expected, invokeFormatLine(input));
    }

    @Test
    public void testFormatFractionWithExponent() throws Exception {
        String input = "La sua derivata prima è f'(x) = 2x/(x+1)^2";
        String expected = "$$ \\text{La sua derivata prima è} \\ f'(x) = \\frac{2x}{(x+1)^2} $$";
        assertEquals(expected, invokeFormatLine(input));
    }

    @Test
    public void testFormatIntegral() throws Exception {
        String input = "L'integrale di 1/x dx = ln|x| + C";
        String expected = "$$ \\int \\frac{1}{x} dx = \\ln|x| + C $$";
        assertEquals(expected, invokeFormatLine(input));
    }

    @Test
    public void testFormatLimit() throws Exception {
        String input1 = "Calcolo del limite lim x->0 f(x) = 1";
        String expected1 = "$$ \\text{Calcolo del limite} \\ \\lim_{x \\to 0 f(x) = 1} $$";

        String input2 = "Il limite lim x->+inf 1/x = 0";
        String expected2 = "$$ \\text{Il limite} \\ \\lim_{x \\to +\\infty \\frac{1}{x} = 0} $$";

        assertEquals(expected1, invokeFormatLine(input1));
        assertEquals(expected2, invokeFormatLine(input2));
    }
}
