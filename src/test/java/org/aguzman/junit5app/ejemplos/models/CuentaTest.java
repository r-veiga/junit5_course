package org.aguzman.junit5app.ejemplos.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    
    @Test
    void testNombreCuenta() {
        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés", new BigDecimal("1000.1234"));
        cuenta.setPersona("Andrés");
        String esperado = "Andrés";
        // WHEN
        String real = cuenta.getPersona();
        // THEN
        assertEquals(esperado, real);
    }

    @Test
    void testSaldoCuenta() {
        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés", new BigDecimal("1000.1234"));
        // THEN
        assertEquals(1000.1234, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testDosCuentasIguales() {
        // GIVEN
        Cuenta cuenta1 = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        // THEN
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    void testDebitoCuenta() {
        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        // WHEN
        cuenta.debito(new BigDecimal(100));
        // THEN
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCrebitoCuenta() {
        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        // WHEN
        cuenta.credito(new BigDecimal(100));
        // THEN
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }
}