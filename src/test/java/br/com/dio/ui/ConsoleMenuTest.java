package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardEntity;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    @Test
    void mainMenu_exitOption_returnsToCaller() throws Exception {
        var output = captureOutput(() -> new MainMenu(new Scanner("4\n")).execute());

        assertTrue(output.contains("Até logo"));
    }

    @Test
    void boardMenu_closeOption_returnsToCaller() throws Exception {
        var output = captureOutput(() -> new BoardMenu(new Scanner("11\n"), new BoardEntity()).execute());

        assertTrue(output.contains("Fechando board"));
    }

    private String captureOutput(final CheckedRunnable action) throws Exception {
        var originalOutput = System.out;
        var capturedOutput = new ByteArrayOutputStream();
        try (var printStream = new PrintStream(capturedOutput)) {
            System.setOut(printStream);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedOutput.toString();
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}