package org.aguzman.junit5app.ejemplos.models;

import org.aguzman.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

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

    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() { }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() { }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() { }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() { }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJdk11() { }

        @Test
        @DisabledOnJre(JRE.JAVA_15)
        void nuncaJdk15() { }
    }

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

    @Nested
    class SystemPropertyTest {
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
    }

    @Nested
    class EnvironementVariableTest {
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

    @Nested
    class AssumeIsDevToTestSaldo {
        @Test
        @DisplayName("** [DEV] saldo de la cuenta corriente **")
        void testSaldoCuentaDev() {

            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumeTrue(esDev); // se ejecuta lo que viene a continuación sólo si se cumple el assume...()

            // GIVEN
            // THEN
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> ">>> El saldo debe ser mayor de cero.");
        }

        @Test
        @DisplayName("** [DEV] (2) saldo cuenta corriente **")
        void testSaldoCuentaDev2() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumingThat(esDev, () -> {
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> ">>> El saldo debe ser mayor de cero.");
            });
        }
    }

    @DisplayName("** 5 VECES débito **")
    @RepeatedTest(value=5, name="{displayName} Rep nº {currentRepetition} de {totalRepetitions}")
    void repeatedTestDebitoCuenta(RepetitionInfo iteracion) {
        // GIVEN
        System.out.println("----------- Esta iteración es la número " + iteracion.getCurrentRepetition());
        // WHEN
        cuenta.debito(new BigDecimal(100));
        // THEN
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Nested
    class PruebasParametrizadasTest {

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        // @ValueSource(strings = {"100", "200", "300", "400", "500", "700", "1000"})
        // @ValueSource(ints = {100, 200, 300, 400, 500, 700, 1000})
        @ValueSource(doubles = {100.00, 200.00, 300.00, 400.00, 500.00, 700.00, 1000.00})
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConArray(/* String, int */ double monto) {
            // GIVEN
            // WHEN
            cuenta.debito(new BigDecimal(monto));
            // THEN
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1, 100", "2, 200", "3, 300", "4, 400", "5, 500", "6, 700", "7, 1000.1234"})
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConCSV(String index, String monto) {
            // GIVEN
            // WHEN
            cuenta.debito(new BigDecimal(monto));
            // THEN
            System.out.println(index + " -> " + monto);
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200, 100, Joh, Andrés",
                    "250, 200, pepe, Pepe",
                    "300, 300, maria, Maria",
                    "400, 400, Pepa, Pepa",
                    "400, 500, Cata, Cata",
                    "750, 700, Carlos, Carlos",
                    "1000.12345, 1000.1234, Ben, Ben"})
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConCSV2(String saldo, String monto, String esperado, String actual) {
            // GIVEN
            // WHEN
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            // THEN
            System.out.println("Al saldo " + saldo + " se le debitará -> " + monto);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources="/data.csv")
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConCSVFile(String monto) {
            // GIVEN
            // WHEN
            cuenta.debito(new BigDecimal(monto));
            // THEN
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources="/data2.csv")
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConCSVFile2(String saldo, String monto, String esperado, String actual) {
            // GIVEN
            // WHEN
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            // THEN
            System.out.println("Al saldo " + saldo + " se le debitará -> " + monto);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        @DisplayName("** PARAMETRIZADO débito en cuenta **")
        void debitoTestParametrizadoConMetodo(String monto) {
            // GIVEN
            // WHEN
            cuenta.debito(new BigDecimal(monto));
            // THEN
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }
    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "400", "500", "700", "1000.1234");
    }
}