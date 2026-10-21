package insurance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ContextPurityTest {
    @ParameterizedTest
    @ValueSource(strings = {"payout/PayoutCalculator.java", "risk/RiskAssessor.java"})
    void contextHasNoBranchingOnStrategyType(String file) throws IOException {
        String code = Files.readString(Paths.get("src/main/java/insurance", file));
        assertFalse(code.contains("switch"), file + " contains switch");
        assertFalse(code.contains("instanceof"), file + " contains instanceof");
        assertFalse(Pattern.compile("\\bif\\s*\\(").matcher(code).find(), file + " contains if");
    }
}
