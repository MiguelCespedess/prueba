package cl.iplacex.automatizacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraTest {

    private Calculadora calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new Calculadora();
    }

    @Test
    void sumaDosNumeros() {
        assertEquals(5.0, calculadora.sumar(2, 3));
    }

    @Test
    void restaDosNumeros() {
        assertEquals(1.0, calculadora.restar(4, 3));
    }

    @Test
    void multiplicaDosNumeros() {
        assertEquals(12.0, calculadora.multiplicar(4, 3));
    }

    @Test
    void divideDosNumeros() {
        assertEquals(2.0, calculadora.dividir(6, 3));
    }

    @Test
    void divisionPorCeroLanzaExcepcion() {
        assertThrows(ArithmeticException.class, () -> calculadora.dividir(1, 0));
    }
}
