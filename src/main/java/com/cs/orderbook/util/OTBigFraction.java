package com.cs.orderbook.util;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * BigFraction's toString includes a '/'
 * which breaks JSON response. This Class is extended
 * to override toString() method
 * to prevent breaking JSON response due to '/'
 */
public class OTBigFraction extends BigFraction {

    private static final long serialVersionUID = 1L;

    public OTBigFraction(BigInteger num) {
        super(num);
    }

    public OTBigFraction(double value)  {
        super(value);
    }

    public OTBigFraction(int num) {
        super(num);
    }

    public OTBigFraction(long num) {
        super(num);
    }

    public OTBigFraction(BigInteger num, BigInteger den) {
        super(num, den);
    }

    public OTBigFraction(double value, int maxDenominator) {
        super(value, maxDenominator);
    }

    public OTBigFraction(int num, int den) {
        super(num, den);
    }

    public OTBigFraction(long num, long den) {
        super(num, den);
    }

    public OTBigFraction(double value, double epsilon, int maxIterations)  {
        super(value, epsilon, maxIterations);
    }

    @Override
    public String toString() {
        String str = null;
        if (BigInteger.ONE.equals(getDenominator())) {
            str = getNumerator().toString();
        } else if (BigInteger.ZERO.equals(getNumerator())) {
            str = "0";
        } else {
            str = "\"" + getNumerator() + " by " + getDenominator() + "\"";
        }
        return str;
    }

}
