package org.aguzman.junit5app.ejemplos.models;

import org.aguzman.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto) {
        this.saldo = this.saldo.subtract(monto); // porque BigDecimal es inmutable
        if (this.saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto); // porque BigDecimal es inmutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuenta)) return false;
        Cuenta cuenta = (Cuenta) o;
        return getPersona().equals(cuenta.getPersona()) && getSaldo().equals(cuenta.getSaldo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPersona(), getSaldo());
    }
}
