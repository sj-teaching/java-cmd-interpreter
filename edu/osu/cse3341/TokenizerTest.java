package edu.osu.cse3341;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private Tokenizer t;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        t = new TokenizerImpl();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void currentToken() {
    }

    @org.junit.jupiter.api.Test
    void nextToken() {
    }

    @org.junit.jupiter.api.Test
    void hasNext() {
    }

    @org.junit.jupiter.api.Test
    void tokenize() {
    }


    @org.junit.jupiter.api.Test
    void codeReservedWords() {
        assertEquals(1, t.code("program"));
        assertEquals(2, t.code("begin"));
        assertEquals(3, t.code("end"));
        assertEquals(4, t.code("int"));
        assertEquals(5, t.code("if"));
        assertEquals(6, t.code("then"));
        assertEquals(7, t.code("else"));
        assertEquals(8, t.code("while"));
        assertEquals(9, t.code("loop"));
        assertEquals(10, t.code("read"));
        assertEquals(11, t.code("write"));
        assertEquals(12, t.code("and"));
        assertEquals(13, t.code("or"));
    }

    @org.junit.jupiter.api.Test
    void codeNumbers() {
        assertEquals(31, t.code("12"));
        assertEquals(31, t.code("98"));
        assertEquals(31, t.code("12345678"));
    }

    @org.junit.jupiter.api.Test
    void codeIdentifiers() {
        assertEquals(32, t.code("X"));
        assertEquals(32, t.code("ABC"));
        assertEquals(32, t.code("A123"));
    }

    @org.junit.jupiter.api.Test
    void codeSymbols() {
        assertEquals(14, t.code(";"));
        assertEquals(15, t.code(","));
        assertEquals(16, t.code("="));
        assertEquals(17, t.code("!"));
        assertEquals(18, t.code("["));
        assertEquals(19, t.code("]"));
        assertEquals(20, t.code("("));
        assertEquals(21, t.code(")"));
        assertEquals(22, t.code("+"));
        assertEquals(23, t.code("-"));
        assertEquals(24, t.code("*"));
        assertEquals(25, t.code("!="));
        assertEquals(26, t.code("=="));
        assertEquals(27, t.code(">="));
        assertEquals(28, t.code("<="));
        assertEquals(29, t.code(">"));
        assertEquals(30, t.code("<"));
    }
}