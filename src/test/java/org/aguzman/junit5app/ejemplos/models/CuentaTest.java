package org.aguzman.junit5app.ejemplos.models;

import org.aguzman.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    private Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        System.out.println("...iniciando el método");
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("¡¡¡ Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("!!! Finalizando el test");
    }

    @Test
    @Disabled // Test deshabilitado para que pase la suite de tests
    @DisplayName("** nombre de la cuenta corriente **")
    void testNombreCuenta() {

        fail("Fuerzo fallo en el test para así demostrar la anotación @Disabled");

        // GIVEN
        Cuenta cuenta = new Cuenta("Andrés", new BigDecimal("1000.1234"));
        cuenta.setPersona("Andrés");
        String esperado = "Andrés";
        // WHEN
        String real = cuenta.getPersona();
        // THEN
        assertNotNull(real, () -> ">>> La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> ">>> El nombre de cuenta no es el que se esperaba");
    }

    @Test
    @DisplayName("** saldo de la cuenta corriente **")
    void testSaldoCuenta() {
        // GIVEN
        // THEN
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> ">>> El saldo debe ser mayor de cero.");
    }

    @Test
    @DisplayName("** igualdad de cuentas **")
    void testDosCuentasIguales() {
        // GIVEN
        Cuenta cuenta1 = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andrés Guzmán", new BigDecimal("1000.12345"));
        // THEN
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    @DisplayName("** débito en cuenta **")
    void testDebitoCuenta() {
        // GIVEN
        // WHEN
        cuenta.debito(new BigDecimal(100));
        // THEN
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("** crédito en cuenta **")
    void testCreditoCuenta() {
        // GIVEN
        // WHEN
        cuenta.credito(new BigDecimal(100));
        // THEN
        assertNotNull(cuenta.getSaldo(), ">>> El saldo de la cuenta no puede ser null");
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("** EXCEPCIÓN cuando no dinero suficiente **")
    void testDineroInsuficienteException() {
        // GIVEN
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
                () -> assertEquals(2, banco.getCuentas().size(), () -> ">>> El banco no tiene las cuentas esperadas"),
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

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() { }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() { }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() { }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJdk8() { }

    @Test
    @EnabledOnJre(JRE.JAVA_11)
    void soloJdk11() { }

    @Test
    @DisabledOnJre(JRE.JAVA_15)
    void nuncaJdk15() { }

    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k,v) -> System.out.println("clave = " + k + " ; valor = " + v));
    }

    @Test
    void imprimirEnvironmentVariables() {
        Map<String, String> envVars = System.getenv();
        envVars.forEach((k,v) -> System.out.println("clave = " + k + " ; valor = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "java.vm.vendor", matches = "Ubuntu")
    void testOnSystemVariable() { }

    @Test
    @DisabledIfEnvironmentVariable(named = "os.arch", matches = "*.32.*")
    void nuncaArquitectura32bits() { }

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "roberto")
    void testUserName() { }

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void entornoDev() { }

    @Test
    @DisabledIfEnvironmentVariable(named = "USERNAME", matches = "roberto")
    void testUsarname() { }

    @Test
    @DisabledIfEnvironmentVariable(named = "SHELL", matches = "/bin/bash")
    void nuncaShellBash() { }

    @Test
    @EnabledIfEnvironmentVariable(named = "VERO_ENV", matches = "dev")
    void siempreEnvironmentDev() { }

    @Test
    @DisabledIfEnvironmentVariable(named = "VERO_ENV", matches = "prod")
    void nuncaEnvironmentProd() { }

}