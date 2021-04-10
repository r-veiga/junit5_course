package org.aguzman.junit5app.ejemplos.models;

import org.aguzman.junit5app.ejemplos.exceptions.DineroInsuficienteException;
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

    @Test
    void testDineroInsuficienteException() {
        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        // WHEN - THEN
        Exception exception = assertThrows(
                DineroInsuficienteException.class,
                () -> {
                    cuenta.debito(new BigDecimal(1500));
                }
        );
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuentaOrigen = new Cuenta("John Doe", new BigDecimal("300"));
        Cuenta cuentaDestino = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));

        Banco banco = new Banco();
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("150"));
        assertEquals("1150.12345", cuentaDestino.getSaldo().toPlainString());
        assertEquals("150", cuentaOrigen.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuentaOrigen = new Cuenta("John Doe", new BigDecimal("300"));
        Cuenta cuentaDestino = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.addCuenta(cuentaOrigen);
        banco.addCuenta(cuentaDestino);
        assertAll(
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estado", cuentaOrigen.getBanco().getNombre()),
                () -> assertEquals("Andrés Guzmán", banco.getCuentas().stream()
                        .filter(cuenta -> cuenta.getPersona().equals("Andrés Guzmán"))
                        .findFirst()
                        .get()
                        .getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(cuenta -> cuenta.getPersona().equals("Andrés Guzmán"))),
                () -> assertTrue(banco.getCuentas().stream()
                        .filter(cuenta -> cuenta.getPersona().equals("Andrés Guzmán"))
                        .findFirst()
                        .isPresent())
        );
    }
}